import java.net.*;

public class UDPClient {
    public static void main(String[] args) {
        final int SERVER_PORT = 3020; // Port du serveur
        final String SERVER_ADDRESS = "127.0.0.1"; // Adresse du serveur (localhost)
        String message = "hello serveur";

        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);

            // Envoi du message au serveur
            byte[] data = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);
            System.out.println("Message envoyé au serveur : " + message);

            // Réception de la réponse du serveur
            byte[] buffer = new byte[1024];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            clientSocket.receive(receivedPacket);

            // Affichage de la réponse
            String response = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
            System.out.println("Réponse du serveur : " + response + " : " +
                    receivedPacket.getAddress() + ":" + receivedPacket.getPort());

            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
