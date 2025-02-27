import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    private static final int SERVER_PORT = 9876;

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT)) {
            System.out.println("Serveur en attente sur le port " + SERVER_PORT);

            while (true) {
                // Réception du premier message d'un client
                byte[] buffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(receivePacket);

                // Informations du client
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                System.out.println("Connexion d'un client : " + clientAddress + ":" + clientPort);

                // Démarre un thread pour gérer ce client
                new Thread(new ClientHandler(clientAddress, clientPort)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Gestion de chaque client dans un thread séparé
    private static class ClientHandler implements Runnable {
        private final InetAddress clientAddress;
        private final int clientPort;

        public ClientHandler(InetAddress clientAddress, int clientPort) {
            this.clientAddress = clientAddress;
            this.clientPort = clientPort;
        }

        @Override
        public void run() {
            try (DatagramSocket clientSocket = new DatagramSocket()) {
                System.out.println("Nouveau thread pour " + clientAddress + ":" + clientPort);

                while (true) {
                    // Réception du message du client
                    byte[] buffer = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    clientSocket.receive(receivePacket);

                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Message reçu de " + clientAddress + ":" + clientPort + " : " + message);

                    // Réponse au client
                    String response = "Reçu : " + message;
                    byte[] sendData = response.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                    clientSocket.send(sendPacket);
                }
            } catch (IOException e) {
                System.out.println("Déconnexion de " + clientAddress + ":" + clientPort);
            }
        }
    }
}
