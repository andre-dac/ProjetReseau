import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class UDPServer {
    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(9876);
            System.out.println("Serveur UDP démarré...");

            // Stockage des clients déjà connectés
            Set<String> connectedClients = new HashSet<>();

            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                String clientKey = clientAddress.getHostAddress() + ":" + clientPort;

                // Si c'est un nouveau client, on l'affiche
                if (!connectedClients.contains(clientKey) && message.equals("hello serveur RX302")) {
                    System.out.println("Nouveau client : " + clientKey);
                    connectedClients.add(clientKey);

                    // Envoyer la réponse initiale
                    String response = "Serveur RX302 ready";
                    sendMessage(serverSocket, clientAddress, clientPort, response);
                } else {
                    System.out.println("Message reçu de " + clientKey + " : " + message);

                    // Répondre avec un écho du message
                    String response = "Reçu : " + message;
                    sendMessage(serverSocket, clientAddress, clientPort, response);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(DatagramSocket socket, InetAddress address, int port, String message) throws IOException {
        byte[] sendBuffer = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);
        socket.send(sendPacket);
    }
}
