package chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class chat_server {

    static ServerSocket sss;
    static Socket ss;
    static DataInputStream din;
    static DataOutputStream dout;

    public static void main(String args[]) throws IOException {

        System.out.println("Chat Server (GUI version) is now deprecated. Please use HeadlessChatServer for server functionality.");
        System.out.println("This file is kept for compatibility but its GUI features are not functional as a server.");

        sss = new ServerSocket(1201); //server starts at 1201 port number
        ss = sss.accept(); // server will accept the connection
        din = new DataInputStream(ss.getInputStream());
        dout = new DataOutputStream(ss.getOutputStream());

        String msgin = "";

        while (!msgin.equals("exit")) {
            try {
                msgin = din.readUTF();
                System.out.println("Received message: " + msgin);
                // In a real scenario, this message would be forwarded to another client
                // For now, it just prints to console.
            } catch (IOException ex) {
                Logger.getLogger(chat_server.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
        if (ss != null && !ss.isClosed()) {
            ss.close();
        }
        if (sss != null && !sss.isClosed()) {
            sss.close();
        }
    }
}


