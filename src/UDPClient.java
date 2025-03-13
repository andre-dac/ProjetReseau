import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * UDPClient est une classe qui permet de se connecter à un serveur UDP,
 * d'envoyer des messages (login, broadcast et privés) et de recevoir des réponses du serveur.
 *
 * <p>
 * Le client se connecte à un serveur en utilisant une adresse IP et un port prédéfinis.
 * Il demande à l'utilisateur d'entrer son pseudo, envoie une demande de connexion,
 * puis lit en continu les messages à envoyer ou reçus.
 * Les messages privés doivent être préfixés par '@' suivi du pseudo du destinataire.
 * </p>
 *
 * @version 1.0
 */
public class UDPClient {
    private static final int PORT = 9876;
    private static final String SERVER_IP = "192.168.1.59";

    /**
     * Point d'entrée de l'application client.
     *
     * <p>
     * Initialise la connexion UDP avec le serveur, demande à l'utilisateur son pseudo,
     * envoie une requête de connexion et démarre un thread dédié à la réception des messages.
     * Ensuite, il lit en continu les messages de l'utilisateur pour envoyer soit un message privé,
     * soit un message en broadcast.
     * </p>
     *
     * @param args Les arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            Scanner scanner = new Scanner(System.in);
            System.out.print("Entrez votre pseudo : ");
            String pseudo = scanner.nextLine();

            sendMessage(clientSocket, serverAddress, PORT, "LOGIN:" + pseudo);

            // Démarrage d'un thread pour la réception asynchrone des messages du serveur.
            Thread receiveThread = new Thread(() -> {
                while (true) {
                    try {
                        String response = receiveMessage(clientSocket);
                        System.out.println(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();

            // Boucle principale pour l'envoi de messages.
            while (true) {
                String message = scanner.nextLine();
                if (message.startsWith("@")) {
                    // Message privé au format : @pseudo message
                    int spaceIndex = message.indexOf(" ");
                    if (spaceIndex > 1) {
                        String targetPseudo = message.substring(1, spaceIndex);
                        String privateMessage = message.substring(spaceIndex + 1);
                        sendMessage(clientSocket, serverAddress, PORT, "PRIVATE:" + targetPseudo + ":" + privateMessage);
                    } else {
                        System.out.println("Format incorrect. Utilisez : @pseudo message");
                    }
                } else {
                    sendMessage(clientSocket, serverAddress, PORT, "BROADCAST:" + message);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie un message via le socket UDP à une adresse et un port spécifiés.
     *
     * @param socket  Le DatagramSocket utilisé pour envoyer le message.
     * @param address L'adresse IP du destinataire.
     * @param port    Le port du destinataire.
     * @param message La chaîne de caractères à envoyer.
     * @throws IOException En cas d'erreur d'entrée/sortie lors de l'envoi du message.
     */
    private static void sendMessage(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
        byte[] sendBuffer = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
        socket.send(sendPacket);
    }

    /**
     * Reçoit un message via le socket UDP.
     *
     * @param socket Le DatagramSocket utilisé pour recevoir le message.
     * @return La chaîne de caractères contenant le message reçu.
     * @throws IOException En cas d'erreur d'entrée/sortie lors de la réception du message.
     */
    private static String receiveMessage(DatagramSocket socket) throws IOException {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }
}
