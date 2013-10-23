package UdpChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class UdpChatServer {
    private static final int PORT = 6789;

    public static void main(String[] args) throws IOException {

        // Nasluchiwanie na porcie
        ServerSocket server = null;
        try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
            System.err.println(e);
            System.exit(1);
        }

        // Akceptacja klienta
        Socket client = null;
        while (true) {
            try {
                client = server.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.err.println(e);
                System.exit(1);
            }
            // Watek odpowiedzialny za komunikacje z klientem
            Thread t = new Thread(new ClientConn(client));
            t.start();
        }
    }
}

class ChatServerProtocol {
    private String nick;
    private ClientConn conn;

    // Tu bedziemy trzymali nicki klientow podlaczonych do serwera
    private static Hashtable<String, ClientConn> nicks =
            new Hashtable<String, ClientConn>();

    private static final String msg_OK = "OK";
    private static final String msg_NICK_IN_USE = "NICK IN USE";
    private static final String msg_SPECIFY_NICK = "SPECIFY NICK";
    private static final String msg_INVALID = "INVALID COMMAND";
    private static final String msg_SEND_FAILED = "FAILED TO SEND";

    // Dodaje nick do hashtable, zwraca false, jezeli nick juz sie w niej znajduje
    private static boolean add_nick(String nick, ClientConn c) {
        if (nicks.containsKey(nick)) {
            return false;
        } else {
            nicks.put(nick, c);
            return true;
        }
    }

    public ChatServerProtocol(ClientConn c) {
        nick = null;
        conn = c;
    }

    private void log(String msg) {
        System.err.println(msg);
    }

    public boolean isAuthenticated() {
        return !(nick == null);
    }

    // Autentykacja
    // msg_OK - jest autentyfikacja
    // msg_NICK_IN_USE - podany nick jest w użyciu
    // msg_SPECIFY_NICK - wiadomosc nie zaczyna sie od NICK
    private String authenticate(String msg) {
        if (msg.startsWith("NICK")) {
            String tryNick = msg.substring(5);
            if (add_nick(tryNick, this.conn)) {
                log("Nick " + tryNick + " joined.");
                this.nick = tryNick;
                return msg_OK;
            } else {
                return msg_NICK_IN_USE;
            }
        } else {
            return msg_SPECIFY_NICK;
        }
    }

    // Wyslij wiadomosc do odbiorcy
    private boolean sendMsg(String recipient, String msg) {
        if (nicks.containsKey(recipient)) {
            ClientConn c = nicks.get(recipient);
            c.sendMsg(nick + ": " + msg);
            return true;
        } else {
            return false;
        }
    }

    // Przetwarzanie wiadomosci od klienta
    public String process(String msg) {
        if (!isAuthenticated())
            return authenticate(msg);

        String[] msg_parts = msg.split(" ", 3);
        String msg_type = msg_parts[0];

        if (msg_type.equals("MSG")) {
            if (msg_parts.length < 3) return msg_INVALID;
            if (sendMsg(msg_parts[1], msg_parts[2])) return msg_OK;
            else return msg_SEND_FAILED;
        } else {
            return msg_INVALID;
        }
    }
}

class ClientConn implements Runnable {
    private Socket client;
    private BufferedReader in = null;
    private PrintWriter out = null;

    ClientConn(Socket client) {
        this.client = client;
        try {
            // input stream do klienta
            in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            // output stream do klienta
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println(e);
            return;
        }
    }

    public void run() {
        String msg, response;
        ChatServerProtocol protocol = new ChatServerProtocol(this);
        try {
            // petla czytajaca linnie od klienta i przekazujaca odpowiedz do klienta
            while ((msg = in.readLine()) != null) {
                response = protocol.process(msg);
                out.println("SERVER: " + response);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void sendMsg(String msg) {
        out.println(msg);
    }
}