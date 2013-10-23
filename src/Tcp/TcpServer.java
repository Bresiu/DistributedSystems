package Tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    public static void main(String argv[]) throws Exception {
        final int socketNumber = 6789;
        String clientMessage;
        String serverResponse;

        ServerSocket welcomeSocket = new ServerSocket(socketNumber);

        while(true)
        {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            clientMessage = inFromClient.readLine();
            System.out.println("Received: " + clientMessage);
            serverResponse = clientMessage.toUpperCase() + '\n';
            outToClient.writeBytes(serverResponse);
        }
    }
}
