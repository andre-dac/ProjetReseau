import java.net.*;

public class UDPServer {
    public static void main(String[] args) {
        final int SERVER_PORT = 3020; // Port d'écoute du serveur
        byte[] buffer = new byte[1024]; // Buffer pour recevoir les données

        try {
            DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
            System.out.println("Serveur UDP démarré sur le port " + SERVER_PORT);

            while (true) {
                // Réception du message du client
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(receivedPacket);

                // Extraire les informations du client
                InetAddress clientAddress = receivedPacket.getAddress();
                int clientPort = receivedPacket.getPort();
                String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                System.out.println("Nouveau client : " + clientAddress + ":" + clientPort);
                System.out.println("Message reçu : " + message);

                // Vérification du message attendu
                if (message.equals("hello serveur RX302")) {
                    String response = "Serveur RX302 ready";
                    byte[] responseData = response.getBytes();

                    // Envoi de la réponse au client
                    DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
                    serverSocket.send(responsePacket);
                    System.out.println("Réponse envoyée : " + response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
