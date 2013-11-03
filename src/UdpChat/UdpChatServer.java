package UdpChat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class UdpChatServer extends Thread {
    private static final int PORT = 6789;
    private final static int BUFFER = 1024;

    // Nasluchiwanie na porcie
    private DatagramSocket socket = null;
    // Lista z adresami klientow
    private ArrayList<InetAddress> clientAddresses;
    // List z portami klientow
    private ArrayList<Integer> clientPorts;
    // Lista z id klientow
    private HashSet<String> existingClients;

    public UdpChatServer() throws IOException {
        try {
            socket = new DatagramSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
            System.err.println(e);
            System.exit(1);
        }
        // Inicjalizacja list
        clientAddresses = new ArrayList<InetAddress>();
        clientPorts = new ArrayList<Integer>();
        existingClients = new HashSet<String>();
    }

    public void run() {
        byte[] buf = new byte[BUFFER];
        while (true) {
            try {
                // wypelnienie zerami
                Arrays.fill(buf, (byte) 0);
                // tworzymy nowy datagram
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
                // przechwycamy do niego bajty z socketu
                socket.receive(datagramPacket);

                // Tworzymy String z tablicy bajtow
                String content = new String(buf);

                // Wyciagamy InetAddress z datagtamu
                InetAddress clientAddress = datagramPacket.getAddress();
                // Wyciagamy port klienta z datagramu
                int clientPort = datagramPacket.getPort();

                // Tworzymy id klienta
                // String id = clientAddress.toString() + "," + clientPort;
                String id = content.substring(0, content.indexOf("|"));
                String message = content.substring(content.indexOf("|")+1);

                // Jezeli id nie istnieje w naszej liscie klientow...
                if (!existingClients.contains(id)) {
                    // dodajemy id, port i adres klienta do list
                    existingClients.add(id);
                    clientPorts.add(clientPort);
                    clientAddresses.add(clientAddress);
                }

                System.out.println(id + " : " + message);

                byte[] data = (id + " : " + message).getBytes();
                // Wysyłamy do wszystkich uzytkownikow wiadomosc
                for (int i = 0; i < clientAddresses.size(); i++) {
                    InetAddress cl = clientAddresses.get(i);
                    int cp = clientPorts.get(i);
                    datagramPacket = new DatagramPacket(data, data.length, cl, cp);
                    socket.send(datagramPacket);
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        UdpChatServer s = new UdpChatServer();
        s.start();
    }
}