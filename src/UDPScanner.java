import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * UDPScanner permet de scanner une plage de ports UDP pour en vérifier la disponibilité.
 *
 * <p>
 * La méthode {@code scanUDPPorts} tente d'ouvrir un {@link DatagramSocket} sur chaque port
 * de la plage spécifiée. Si l'ouverture réussit, le port est considéré comme disponible.
 * En cas d'exception {@link SocketException}, le port est considéré comme occupé.
 * Les résultats du scan sont affichés sur la sortie standard.
 * </p>
 *
 * @version 1.0
 */
public class UDPScanner {

    /**
     * Scanne les ports UDP compris entre {@code startPort} et {@code endPort}.
     *
     * <p>
     * Pour chaque port dans la plage, la méthode tente d'ouvrir un {@link DatagramSocket}.
     * Si l'ouverture réussit, le socket est immédiatement fermé et le port est indiqué comme disponible.
     * Si une {@link SocketException} est levée, le port est considéré comme occupé.
     * </p>
     *
     * @param startPort Le port de début de la plage à scanner.
     * @param endPort   Le port de fin de la plage à scanner.
     */
    public static void scanUDPPorts(int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port++) {
            try {
                // Tentative d'ouverture d'un DatagramSocket sur le port.
                DatagramSocket ds = new DatagramSocket(port);
                ds.close(); // Fermeture immédiate pour libérer le port.
                System.out.println("Port " + port + " disponible");
            } catch (SocketException e) {
                // Exception levée : le port est considéré comme occupé.
                System.out.println("Port " + port + " occupé");
            }
        }
    }
}
