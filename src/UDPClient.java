import java.io.*;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");

            System.out.println("Client UDP démarré.");

            // Envoi du message initial "hello serveur RX302"
            String initialMessage = "hello serveur RX302";
            sendMessage(clientSocket, serverAddress, 9876, initialMessage);

            // Réception de la réponse du serveur
            String initialResponse = receiveMessage(clientSocket);
            System.out.println("Réponse du serveur : " + initialResponse);

            // Communication continue : envoi et réception des messages du clavier
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Votre message : ");
                String message = scanner.nextLine();

                sendMessage(clientSocket, serverAddress, 9876, message);

                String response = receiveMessage(clientSocket);
                System.out.println("Réponse du serveur : " + response);
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

    private static String receiveMessage(DatagramSocket socket) throws IOException {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);

        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }
}