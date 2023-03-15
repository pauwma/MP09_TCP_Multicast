import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientTcpAdivina_Obj extends Thread {
    /* CLient TCP que ha endevinar un número pensat per SrvTcpAdivina_Obj.java */

    private String Nom;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private Scanner scin;
    private boolean continueConnected;
    private Tauler t;
    private Jugada j;

    private ClientTcpAdivina_Obj(String hostname, int port) {
        try {
            socket = new Socket(InetAddress.getByName(hostname), port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (UnknownHostException ex) {
            System.out.println("Error de connexió. No existeix el host: " + ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        continueConnected = true;
        scin = new Scanner(System.in);
        j = new Jugada();
    }

    public void run() {
        String msg = null;
        while(continueConnected){
            //Llegir info del servidor (estat del tauler)
            t = getRequest();

            //Crear codi de resposta a missatge
            switch (t.resultat) {
                case 3:	msg = "Benvingut al joc " + Nom + " - " + t.getNumPlayers(); break;
                case 2:	msg = "Més gran"; break;
                case 1: msg = "Més petit"; break;
                case 0:
                    System.out.println("Correcte");
                    System.out.println(t);
                    continueConnected = false;
                    continue;
            }
            System.out.println(msg);
            System.out.println(t);

            if(t.resultat != 0) {
                System.out.println("Entra un número: ");
                j.num = scin.nextInt();
                j.Nom = Nom;
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(out);
                    oos.writeObject(j);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        close(socket);

    }
    private Tauler getRequest() {
        try {
            ObjectInputStream ois = new ObjectInputStream(in);
            t = (Tauler) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }


    private void close(Socket socket){
        //si falla el tancament no podem fer gaire cosa, només enregistrar
        //el problema
        try {
            //tancament de tots els recursos
            if(socket!=null && !socket.isClosed()){
                if(!socket.isInputShutdown()){
                    socket.shutdownInput();
                }
                if(!socket.isOutputShutdown()){
                    socket.shutdownOutput();
                }
                socket.close();
            }
        } catch (IOException ex) {
            //enregistrem l'error amb un objecte Logger
            Logger.getLogger(ClientTcpAdivina_Obj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        String jugador, ipSrv;

        //Demanem la ip del servidor i nom del jugador
        System.out.println("Ip del servidor?");
        Scanner sip = new Scanner(System.in);
        ipSrv = sip.next();
        System.out.println("Nom jugador:");
        jugador = sip.next();

        ClientTcpAdivina_Obj clientTcp = new ClientTcpAdivina_Obj(ipSrv,5558);
        clientTcp.Nom = jugador;
        clientTcp.start();
    }
}