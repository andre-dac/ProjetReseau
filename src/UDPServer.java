import java.net.*;

public class UDPServer {
    public static void main(String[] args) {
        final int SERVER_PORT = 3020; // Port d'écoute du serveur
        byte[] buffer = new byte[1024];

        try {
            DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
            System.out.println("Serveur UDP en attente sur le port " + SERVER_PORT);

            while (true) {
                // Réception du message client
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(receivedPacket);

                // Extraction et affichage des informations
                String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                InetAddress clientAddress = receivedPacket.getAddress();
                int clientPort = receivedPacket.getPort();
                System.out.println("Nouveau client : " + clientAddress + ":" + clientPort);
                System.out.println("Message reçu : " + message);

                // Réponse au client
                String response = "Serveur RX302 ready";
                byte[] responseData = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
                serverSocket.send(responsePacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
