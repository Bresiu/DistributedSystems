package UdpChat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

class MessageSender implements Runnable {
    public final static int PORT = 6789;
    private DatagramSocket socket;
    private String hostname;
    private String nick;

    MessageSender(DatagramSocket socket, String hostname, String nick) {
        this.socket = socket;
        this.hostname = hostname;
        this.nick = nick;
    }

    private void sendMessage(String m) throws Exception {
        String message = nick + "|" + m;
        byte buf[] = message.getBytes();
        InetAddress address = InetAddress.getByName(hostname);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
        socket.send(packet);
    }

    public void run() {
        boolean connected = false;
        do {
            try {
                sendMessage("User Connected");
                connected = true;
            } catch (Exception e) {
                System.out.println("Something wrong");
            }
        } while (!connected);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                while (!in.ready()) {
                    Thread.sleep(100);
                }
                sendMessage(in.readLine());
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}

class MessageReceiver implements Runnable {
    DatagramSocket sock;
    byte buf[];

    MessageReceiver(DatagramSocket s) {
        sock = s;
        buf = new byte[1024];
    }

    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                sock.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}

public class UdpChatClient {
    static Scanner inputScanner = new Scanner(System.in);
    private static String nick;

    public static void main(String args[]) throws Exception {
        String host = "localhost";
        DatagramSocket socket = new DatagramSocket();

        System.out.print("Enter your nick: ");
        nick = inputScanner.nextLine();
        System.out.println("Your nick: " + nick);

        // Wywołanie konstruktorów
        MessageReceiver messageReceiver = new MessageReceiver(socket);
        MessageSender messageSender = new MessageSender(socket, host, nick);
        // Tworzenie wątków
        Thread messageReceiverThread = new Thread(messageReceiver);
        Thread messageSenderThread = new Thread(messageSender);
        // Wystartowanie wątków
        messageReceiverThread.start();
        messageSenderThread.start();
    }
}