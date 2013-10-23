package UdpChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class UdpChatClient {
    private static final int PORT = 6789; /* PORT to connect to */
    private static final String HOST = "localhost"; /* HOST to connect to */

    private static BufferedReader stdIn;
    private static String nick;

    // odczytanie nicku oraz proba autentyfikacji na serwerze
    private static String getNick(BufferedReader in, PrintWriter out) throws IOException {
        System.out.print("Enter your nick: ");
        String msg = stdIn.readLine();
        out.println("NICK " + msg);
        String serverResponse = in.readLine();
        if ("SERVER: OK".equals(serverResponse)) return msg;
        System.out.println(serverResponse);
        return getNick(in, out);
    }

    public static void main(String[] args) throws IOException {

        Socket server = null;

        try {
            server = new Socket(HOST, PORT);
        } catch (UnknownHostException e) {
            System.err.println(e);
            System.exit(1);
        }

        stdIn = new BufferedReader(new InputStreamReader(System.in));

        // output stream z serwera
        PrintWriter out = new PrintWriter(server.getOutputStream(), true);
        // input stream z serwera
        BufferedReader in = new BufferedReader(new InputStreamReader(
                server.getInputStream()));

        // proba uwierzytelnienia nicka (parametry in i out
        // sa odpowiedzialne za wyslanie pytania do serwera,
        // oraz czekanie na odpowiedz
        nick = getNick(in, out);

        // Watek odpoweidzialny za odczytywanie wiadomosci z serwera
        ServerConn sc = new ServerConn(server);
        Thread t = new Thread(sc);
        t.start();

        String msg;
        // petla, w ktorej odczytywane sa wiadomosci z stdIn, oraz wysylane do serwera
        while ((msg = stdIn.readLine()) != null) {
            out.println(msg);
        }
    }
}

class ServerConn implements Runnable {
    private BufferedReader in = null;

    public ServerConn(Socket server) throws IOException {
        //input stream z serwera
        in = new BufferedReader(new InputStreamReader(
                server.getInputStream()));
    }

    public void run() {
        String msg;
        try {
            // petla, ktora odczytuje wiadomosci z serwera i wypisuje je
            while ((msg = in.readLine()) != null) {
                System.out.println(msg);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}