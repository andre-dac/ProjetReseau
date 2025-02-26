import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPScanner {

    /**
     * Scanne les ports UDP compris entre startPort et endPort.
     * Pour chaque port, tente d'ouvrir un DatagramSocket.
     * Si l'ouverture réussit, le port est considéré comme disponible.
     * En cas d'exception, le port est considéré comme occupé.
     *
     * @param startPort port de début de la plage
     * @param endPort   port de fin de la plage
     */
    public static void scanUDPPorts(int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port++) {
            try {
                // Essai d'ouverture d'un DatagramSocket sur le port
                DatagramSocket ds = new DatagramSocket(port);
                ds.close(); // Fermeture immédiate pour libérer le port
                System.out.println("Port " + port + " disponible");
            } catch (SocketException e) {
                // Exception levée : le port est déjà occupé
                System.out.println("Port " + port + " occupé");
            }
        }
    }
}
