import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadSevidorAdivina_Obj implements Runnable {
    /* Thread que gestiona la comunicació de SrvTcPAdivina_Obj.java i un cllient ClientTcpAdivina_Obj.java */

    private Socket clientSocket = null;
    private InputStream in = null;
    private OutputStream out = null;
    private SecretNum ns;
    private Tauler tauler;
    private boolean acabat;
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private int multicastPort;

    public ThreadSevidorAdivina_Obj(Socket clientSocket, SecretNum ns, Tauler t, MulticastSocket multicastSocket, InetAddress multicastAddress, int multicastPort) throws IOException {
        this.clientSocket = clientSocket;
        this.ns = ns;
        tauler = t;
        //Al inici de la comunicació el resultat ha de ser diferent de 0(encertat)
        tauler.resultat = 3;
        acabat = false;
        //Enllacem els canals de comunicació
        in = clientSocket.getInputStream();
        out = clientSocket.getOutputStream();
        this.multicastSocket = multicastSocket;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        System.out.println("canals i/o creats amb un nou jugador");
    }

    @Override
    public void run() {
        Jugada j = null;
        try {
            while (!acabat) {

                //Enviem tauler al jugador
                ObjectOutputStream oos = new ObjectOutputStream(out);
                oos.writeObject(tauler);
                oos.flush();

                //Enviamos el estado del juego a través del socket multicast
                byte[] buffer = Util.serialize(tauler);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, multicastAddress, multicastPort);
                multicastSocket.send(packet);

                //Llegim la jugada
                ObjectInputStream ois = new ObjectInputStream(in);
                try {
                    j = (Jugada) ois.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println("jugada: " + j.Nom + "->" + j.num);
                if (!tauler.map_jugadors.containsKey(j.Nom)) tauler.map_jugadors.put(j.Nom, 1);
                else {
                    //Si el judador ja esxiteix, actualitzem la quatitat de tirades
                    int tirades = tauler.map_jugadors.get(j.Nom) + 1;
                    tauler.map_jugadors.put(j.Nom, tirades);
                }

                //comprobar la jugada i actualitzar tauler amb el resultat de la jugada
                tauler.resultat = ns.comprova(j.num);
                if (tauler.resultat == 0) {
                    acabat = true;
                    System.out.println(j.Nom + " l'ha encertat");
                    tauler.acabats++;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        //Enviem últim estat del tauler abans de acabar amb la comunicació i acabem
        try {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(tauler);
            oos.flush();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}