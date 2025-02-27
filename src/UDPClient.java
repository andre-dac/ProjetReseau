import java.net.*;

public class UDPClient {
    public static void main(String[] args) {
        final int SERVER_PORT = 3020; // Port du serveur
        final String SERVER_ADDRESS = "127.0.0.1"; // Adresse du serveur (localhost)

        try {
            DatagramSocket clientSocket = new DatagramSocket();

            // Préparer le message
            String message = "hello serveur RX302";
            byte[] data = message.getBytes();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);

            // Envoi du message au serveur
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);
            System.out.println("Message envoyé : " + message);

            // Réception de la réponse du serveur
            byte[] buffer = new byte[1024];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            clientSocket.receive(receivedPacket);

            // Afficher la réponse
            String response = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
            System.out.println("Réponse reçue : " + response + " : " + receivedPacket.getAddress() + ":" + receivedPacket.getPort());

            clientSocket.close(); // Fermer le socket
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
