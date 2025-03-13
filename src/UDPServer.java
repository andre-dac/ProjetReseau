import java.io.*;
import java.net.*;
import java.util.*;

/**
 * UDPServer implémente un serveur de communication UDP.
 *
 * <p>
 * Le serveur écoute sur une adresse IP et un port prédéfinis et gère différents types de messages :
 * <ul>
 *   <li><b>LOGIN:</b> Enregistrement d'un nouveau client avec son pseudo.</li>
 *   <li><b>PRIVATE:</b> Envoi de messages privés d'un client vers un autre.</li>
 *   <li><b>BROADCAST:</b> Diffusion d'un message à tous les clients connectés.</li>
 *   <li><b>LIST:</b> Renvoi de la liste des utilisateurs connectés au client demandeur.</li>
 * </ul>
 * Les clients sont identifiés par une clé sous la forme "IP:PORT" et stockés dans une collection.
 * </p>
 *
 */
public class UDPServer {
    private static final int PORT = 9876;
    private static final String SERVER_IP = "192.168.1.59";

    /**
     * Collection associant une clé (IP:PORT) au pseudo du client.
     */
    private static Map<String, String> clients = new HashMap<>();

    /**
     * Point d'entrée de l'application serveur UDP.
     *
     * <p>
     * Le serveur démarre un {@link DatagramSocket} lié à l'adresse IP et au port spécifiés.
     * Il attend ensuite les messages des clients dans une boucle infinie et traite chaque message selon son type :
     * <ul>
     *   <li>Si le message commence par "LOGIN:", le client est enregistré et un message de confirmation est envoyé.</li>
     *   <li>Si le message commence par "PRIVATE:", le message est traité comme un message privé.</li>
     *   <li>Si le message commence par "BROADCAST:", le message est diffusé à tous les clients (sauf l'expéditeur).</li>
     *   <li>Si le message est exactement "LIST", la liste de tous les utilisateurs connectés est renvoyée au client demandeur.</li>
     *   <li>Sinon, un message d'erreur est renvoyé au client.</li>
     * </ul>
     * </p>
     *
     * @param args Les arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT, InetAddress.getByName(SERVER_IP));
            System.out.println("Serveur UDP démarré en local sur le port " + PORT + "...");

            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String clientKey = clientAddress.getHostAddress() + ":" + clientPort;

                if (message.startsWith("LOGIN:")) {
                    String pseudo = message.substring(6).trim();
                    clients.put(clientKey, pseudo);
                    System.out.println("Nouveau client : " + pseudo + " (" + clientKey + ")");
                    sendMessage(serverSocket, clientAddress, clientPort, "Connection au serveur réussie ! " + pseudo + " !");
                } else if (message.startsWith("PRIVATE:")) {
                    // Traitement d'un message privé.
                    int firstColon = message.indexOf(":", 8);
                    if (firstColon != -1) {
                        String targetPseudo = message.substring(8, firstColon);
                        String privateMessage = message.substring(firstColon + 1);
                        sendPrivateMessage(serverSocket, clientKey, targetPseudo, privateMessage);
                    }
                } else if (message.equals("LIST")) {
                    if (clients.containsKey(clientKey)) {
                        // Construction de la liste des utilisateurs connectés.
                        StringBuilder userList = new StringBuilder("Utilisateurs connectés : ");
                        for (String user : clients.values()) {
                            userList.append(user).append(", ");
                        }
                        // Suppression de la dernière virgule et espace.
                        if (userList.length() > 0) {
                            userList.setLength(userList.length() - 2);
                        }
                        sendMessage(serverSocket, clientAddress, clientPort, userList.toString());
                    } else {
                        sendMessage(serverSocket, clientAddress, clientPort, "Erreur : Vous devez vous connecter en envoyant LOGIN:pseudo.");
                    }
                } else if (message.startsWith("BROADCAST:")) {
                    if (clients.containsKey(clientKey)) {
                        String pseudo = clients.get(clientKey);
                        String broadcastMessage = message.substring(10);
                        System.out.println(pseudo + " : " + broadcastMessage);
                        broadcastMessage(serverSocket, pseudo + " : " + broadcastMessage, clientKey);
                    }
                } else {
                    sendMessage(serverSocket, clientAddress, clientPort, "Erreur : Connectez-vous avec LOGIN:pseudo");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie un message privé d'un client vers un autre.
     *
     * <p>
     * La méthode recherche dans la collection des clients le destinataire correspondant au pseudo fourni.
     * Si le destinataire est trouvé, le message privé est envoyé ; sinon, un message d'erreur est renvoyé à l'expéditeur.
     * </p>
     *
     * @param socket      Le DatagramSocket utilisé pour envoyer le message.
     * @param senderKey   La clé du client expéditeur (format "IP:PORT").
     * @param targetPseudo Le pseudo du destinataire.
     * @param message     Le contenu du message privé.
     * @throws IOException En cas d'erreur d'entrée/sortie lors de l'envoi du message.
     */
    private static void sendPrivateMessage(DatagramSocket socket, String senderKey, String targetPseudo, String message) throws IOException {
        String senderPseudo = clients.get(senderKey);
        for (Map.Entry<String, String> entry : clients.entrySet()) {
            if (entry.getValue().equals(targetPseudo)) {
                String[] parts = entry.getKey().split(":");
                InetAddress address = InetAddress.getByName(parts[0]);
                int port = Integer.parseInt(parts[1]);
                System.out.println("@" + senderPseudo + " -> @" + targetPseudo + " : " + message);
                sendMessage(socket, address, port, "[Privé de " + senderPseudo + "] : " + message);
                return;
            }
        }
        // Si le destinataire n'existe pas, envoi d'un message d'erreur à l'expéditeur.
        String[] senderParts = senderKey.split(":");
        InetAddress senderAddress = InetAddress.getByName(senderParts[0]);
        int senderPort = Integer.parseInt(senderParts[1]);
        sendMessage(socket, senderAddress, senderPort, "Utilisateur " + targetPseudo + " introuvable.");
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
     * Diffuse un message à tous les clients connectés, à l'exception du client expéditeur.
     *
     * <p>
     * Pour chaque client enregistré dont la clé est différente de celle de l'expéditeur,
     * le message est envoyé via UDP.
     * </p>
     *
     * @param socket    Le DatagramSocket utilisé pour envoyer le message.
     * @param message   La chaîne de caractères à diffuser.
     * @param senderKey La clé du client expéditeur (format "IP:PORT") qui ne recevra pas le message.
     * @throws IOException En cas d'erreur d'entrée/sortie lors de l'envoi du message.
     */
    private static void broadcastMessage(DatagramSocket socket, String message, String senderKey) throws IOException {
        for (Map.Entry<String, String> entry : clients.entrySet()) {
            String clientKey = entry.getKey();
            if (!clientKey.equals(senderKey)) {
                String[] parts = clientKey.split(":");
                InetAddress address = InetAddress.getByName(parts[0]);
                int port = Integer.parseInt(parts[1]);
                sendMessage(socket, address, port, message);
            }
        }
    }
}
