import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    private static final int SERVER_PORT = 9876;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            System.out.println("Client connecté au serveur sur le port " + SERVER_PORT);

            // Démarre un thread pour recevoir les messages du serveur
            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        byte[] buffer = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                        socket.receive(receivePacket);

                        String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.println("Réponse du serveur : " + response);
                    }
                } catch (IOException e) {
                    System.out.println("Erreur lors de la réception.");
                }
            });
            receiveThread.start();

            // Thread principal : envoie des messages
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Votre message : ");
                String message = scanner.nextLine();

                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
