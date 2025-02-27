import java.io.*;
import java.net.*;
import java.util.Scanner;

public class UDPClient {
    private static final int PORT = 9876;
    private static final String SERVER_IP = "127.0.0.1";

    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            Scanner scanner = new Scanner(System.in);
            System.out.print("Entrez votre pseudo : ");
            String pseudo = scanner.nextLine();

            sendMessage(clientSocket, serverAddress, PORT, "LOGIN:" + pseudo);

            Thread receiveThread = new Thread(() -> {
                while (true) {
                    try {
                        String response = receiveMessage(clientSocket);
                        System.out.println(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();

            while (true) {
                String message = scanner.nextLine();
                if (message.startsWith("@")) {
                    // Message privé au format : @pseudo message
                    int spaceIndex = message.indexOf(" ");
                    if (spaceIndex > 1) {
                        String targetPseudo = message.substring(1, spaceIndex);
                        String privateMessage = message.substring(spaceIndex + 1);
                        sendMessage(clientSocket, serverAddress, PORT, "PRIVATE:" + targetPseudo + ":" + privateMessage);
                    } else {
                        System.out.println("Format incorrect. Utilisez : @pseudo message");
                    }
                } else {
                    sendMessage(clientSocket, serverAddress, PORT, "BROADCAST:" + message);
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

    private static String receiveMessage(DatagramSocket socket) throws IOException {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }
}