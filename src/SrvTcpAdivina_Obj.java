import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SrvTcpAdivina_Obj {
    /* Servidor TCP que genera un número perquè ClientTcpAdivina_Obj.java jugui a encertar-lo
     * i on la comunicació dels diferents jugadors la gestionaran els Threads : ThreadServidorAdivina_Obj.java
     * */

    private int port;
    private SecretNum ns;
    private Tauler t;
    private InetAddress multicastAddress;
    private int multicastPort;
    private MulticastSocket multicastSocket;

    private SrvTcpAdivina_Obj(int port, InetAddress multicastAddress, int multicastPort) {
        this.port = port;
        ns = new SecretNum(100);
        t = new Tauler();
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        try {
            multicastSocket = new MulticastSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while(true) { //esperar connexió del client i llançar thread
                clientSocket = serverSocket.accept();
                //Llançar Thread per establir la comunicació
                //sumem 1 al numero de jugadors
                t.addNUmPlayers();
                ThreadSevidorAdivina_Obj FilServidor = new ThreadSevidorAdivina_Obj(clientSocket, ns, t, multicastSocket, multicastAddress, multicastPort);
                Thread client = new Thread(FilServidor);
                client.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(SrvTcpAdivina_Obj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        InetAddress multicastAddress = InetAddress.getByName("224.0.0.1");
        int multicastPort = 5559;
        SrvTcpAdivina_Obj srv = new SrvTcpAdivina_Obj(5558, multicastAddress, multicastPort);
        srv.listen();
    }
}


