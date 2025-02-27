import java.io.*;
import java.net.*;
import java.util.*;

public class UDPServer {
    private static final int PORT = 9876;
    private static final String SERVER_IP = "127.0.0.1";

    private static Map<String, String> clients = new HashMap<>(); // Associe IP:PORT -> Pseudo

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
                    sendMessage(serverSocket, clientAddress, clientPort, "Connection au serveur réussite ! " + pseudo + " !");
                } else if (message.startsWith("PRIVATE:")) {
                    // Gérer les messages privés
                    int firstColon = message.indexOf(":", 8);
                    if (firstColon != -1) {
                        String targetPseudo = message.substring(8, firstColon);
                        String privateMessage = message.substring(firstColon + 1);
                        sendPrivateMessage(serverSocket, clientKey, targetPseudo, privateMessage);
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
        // Si le destinataire n'existe pas
        String[] senderParts = senderKey.split(":");
        InetAddress senderAddress = InetAddress.getByName(senderParts[0]);
        int senderPort = Integer.parseInt(senderParts[1]);
        sendMessage(socket, senderAddress, senderPort, "Utilisateur " + targetPseudo + " introuvable.");
    }

    private static void sendMessage(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
        byte[] sendBuffer = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
        socket.send(sendPacket);
    }

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
