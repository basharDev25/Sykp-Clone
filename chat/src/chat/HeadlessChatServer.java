package chat;

import chat_video.VoiceServer;
import chat_video.video_server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeadlessChatServer {

    private static final int CHAT_PORT = 1201;
    private static final int VIDEO_PORT = 1202;
    private static final int VOICE_PORT = 1203;
    private static final int MAX_CLIENTS = 2;

    private static ServerSocket chatServerSocket;
    private static ServerSocket videoServerSocket;
    private static ServerSocket voiceServerSocket;

    private static List<ClientHandler> connectedClients = new ArrayList<>();
    private static List<VideoClientHandler> videoClients = new ArrayList<>();
    private static List<VoiceClientHandler> voiceClients = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Headless Chat Server Starting...");
        try {
            // Start Chat Server
            chatServerSocket = new ServerSocket(CHAT_PORT);
            System.out.println("Chat Server listening on port " + CHAT_PORT);

            // Start Video Server
            videoServerSocket = new ServerSocket(VIDEO_PORT);
            System.out.println("Video Server listening on port " + VIDEO_PORT);

            // Start Voice Server
            voiceServerSocket = new ServerSocket(VOICE_PORT);
            System.out.println("Voice Server listening on port " + VOICE_PORT);

            // Thread to accept chat connections
            new Thread(() -> {
                try {
                    while (true) {
                        Socket clientSocket = chatServerSocket.accept();
                        if (connectedClients.size() >= MAX_CLIENTS) {
                            System.out.println("Connection refused (Chat): Max clients reached. " + clientSocket.getInetAddress().getHostAddress());
                            clientSocket.close();
                        } else {
                            System.out.println("New chat client connected: " + clientSocket.getInetAddress().getHostAddress());
                            ClientHandler clientHandler = new ClientHandler(clientSocket);
                            connectedClients.add(clientHandler);
                            clientHandler.start();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Chat Server error: " + e.getMessage());
                    Logger.getLogger(HeadlessChatServer.class.getName()).log(Level.SEVERE, null, e);
                }
            }).start();

            // Thread to accept video connections
            new Thread(() -> {
                try {
                    while (true) {
                        Socket videoSocket = videoServerSocket.accept();
                        if (videoClients.size() >= MAX_CLIENTS) {
                            System.out.println("Connection refused (Video): Max clients reached. " + videoSocket.getInetAddress().getHostAddress());
                            videoSocket.close();
                        } else {
                            System.out.println("New video client connected: " + videoSocket.getInetAddress().getHostAddress());
                            VideoClientHandler videoHandler = new VideoClientHandler(videoSocket);
                            videoClients.add(videoHandler);
                            videoHandler.start();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Video Server error: " + e.getMessage());
                    Logger.getLogger(HeadlessChatServer.class.getName()).log(Level.SEVERE, null, e);
                }
            }).start();

            // Thread to accept voice connections
            new Thread(() -> {
                try {
                    while (true) {
                        Socket voiceSocket = voiceServerSocket.accept();
                        if (voiceClients.size() >= MAX_CLIENTS) {
                            System.out.println("Connection refused (Voice): Max clients reached. " + voiceSocket.getInetAddress().getHostAddress());
                            voiceSocket.close();
                        } else {
                            System.out.println("New voice client connected: " + voiceSocket.getInetAddress().getHostAddress());
                            VoiceClientHandler voiceHandler = new VoiceClientHandler(voiceSocket);
                            voiceClients.add(voiceHandler);
                            voiceHandler.start();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Voice Server error: " + e.getMessage());
                    Logger.getLogger(HeadlessChatServer.class.getName()).log(Level.SEVERE, null, e);
                }
            }).start();

        } catch (IOException e) {
            System.err.println("Server initialization error: " + e.getMessage());
            Logger.getLogger(HeadlessChatServer.class.getName()).log(Level.SEVERE, null, e);
        } // No finally block here, as server threads run indefinitely
    }

    private static void closeServerSockets() {
        try {
            if (chatServerSocket != null && !chatServerSocket.isClosed()) {
                chatServerSocket.close();
            }
            if (videoServerSocket != null && !videoServerSocket.isClosed()) {
                videoServerSocket.close();
            }
            if (voiceServerSocket != null && !voiceServerSocket.isClosed()) {
                voiceServerSocket.close();
            }
        } catch (IOException e) {
            Logger.getLogger(HeadlessChatServer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    // Method to broadcast messages to all connected clients
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : connectedClients) {
            if (client != sender) { // Don't send message back to sender
                client.sendMessage(message);
            }
        }
    }

    // Method to broadcast files to all connected clients
    public static void broadcastFile(String fileName, long fileSize, byte[] fileData, ClientHandler sender) {
        for (ClientHandler client : connectedClients) {
            if (client != sender) {
                client.sendFile(fileName, fileSize, fileData);
            }
        }
    }

    // Method to forward video frames to the other connected video client
    public static void forwardVideoFrame(byte[] frameData, VideoClientHandler sender) {
        for (VideoClientHandler client : videoClients) {
            if (client != sender) {
                client.sendFrame(frameData);
            }
        }
    }

    // Method to forward voice data to the other connected voice client
    public static void forwardVoiceData(byte[] voiceData, VoiceClientHandler sender) {
        for (VoiceClientHandler client : voiceClients) {
            if (client != sender) {
                client.sendData(voiceData);
            }
        }
    }

    // Inner class to handle each chat client connection
    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private DataInputStream din;
        private DataOutputStream dout;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                din = new DataInputStream(clientSocket.getInputStream());
                dout = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                System.err.println("Error setting up streams for chat client: " + e.getMessage());
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        public void sendMessage(String message) {
            try {
                dout.writeUTF(message);
                dout.flush();
            } catch (IOException e) {
                System.err.println("Error sending message to chat client: " + e.getMessage());
                closeClientConnection();
            }
        }

        public void sendFile(String fileName, long fileSize, byte[] fileData) {
            try {
                dout.writeUTF("sending_file");
                dout.writeUTF(fileName);
                dout.writeLong(fileSize);
                dout.write(fileData);
                dout.flush();
            } catch (IOException e) {
                System.err.println("Error sending file to chat client: " + e.getMessage());
                closeClientConnection();
            }
        }

        @Override
        public void run() {
            String msgin = "";
            try {
                while (!clientSocket.isClosed()) {
                    msgin = din.readUTF();

                    if (msgin.equals("sending_file")) {
                        String fileName = din.readUTF();
                        long fileSize = din.readLong();
                        byte[] fileData = new byte[(int) fileSize];
                        din.readFully(fileData);

                        System.out.println("Received file from " + clientSocket.getInetAddress().getHostAddress() + ": " + fileName + " (" + fileSize + " bytes)");
                        File receivedDir = new File("received_files");
                        if (!receivedDir.exists()) {
                            receivedDir.mkdirs();
                        }
                        File file = new File(receivedDir, "received_" + fileName);
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            fos.write(fileData);
                        }
                        System.out.println("File saved on server: " + file.getAbsolutePath());

                        broadcastFile(fileName, fileSize, fileData, this);

                    } else {
                        System.out.println("Received from " + clientSocket.getInetAddress().getHostAddress() + ": " + msgin);
                        broadcastMessage(msgin, this);
                    }
                }
            } catch (IOException e) {
                System.err.println("Chat client disconnected: " + clientSocket.getInetAddress().getHostAddress() + " - " + e.getMessage());
            } finally {
                closeClientConnection();
            }
        }

        private void closeClientConnection() {
            try {
                if (din != null) din.close();
                if (dout != null) dout.close();
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, e);
            }
            connectedClients.remove(this);
            System.out.println("Chat client disconnected: " + clientSocket.getInetAddress().getHostAddress() + ". Remaining chat clients: " + connectedClients.size());
        }
    }

    // Inner class to handle each video client connection
    static class VideoClientHandler extends Thread {
        private Socket videoSocket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        public VideoClientHandler(Socket socket) {
            this.videoSocket = socket;
            try {
                oos = new ObjectOutputStream(videoSocket.getOutputStream());
                ois = new ObjectInputStream(videoSocket.getInputStream());
            } catch (IOException e) {
                System.err.println("Error setting up streams for video client: " + e.getMessage());
                Logger.getLogger(VideoClientHandler.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        public void sendFrame(byte[] frameData) {
            try {
                oos.writeObject(frameData);
                oos.flush();
            } catch (IOException e) {
                System.err.println("Error sending video frame to client: " + e.getMessage());
                closeClientConnection();
            }
        }

        @Override
        public void run() {
            try {
                while (!videoSocket.isClosed()) {
                    byte[] frameData = (byte[]) ois.readObject();
                    // Forward the frame to the other video client
                    forwardVideoFrame(frameData, this);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Video client disconnected: " + videoSocket.getInetAddress().getHostAddress() + " - " + e.getMessage());
            } finally {
                closeClientConnection();
            }
        }

        private void closeClientConnection() {
            try {
                if (ois != null) ois.close();
                if (oos != null) oos.close();
                if (videoSocket != null && !videoSocket.isClosed()) {
                    videoSocket.close();
                }
            } catch (IOException e) {
                Logger.getLogger(VideoClientHandler.class.getName()).log(Level.SEVERE, null, e);
            }
            videoClients.remove(this);
            System.out.println("Video client disconnected: " + videoSocket.getInetAddress().getHostAddress() + ". Remaining video clients: " + videoClients.size());
        }
    }

    // Inner class to handle each voice client connection
    static class VoiceClientHandler extends Thread {
        private Socket voiceSocket;
        private DataInputStream dis;
        private DataOutputStream dos;

        public VoiceClientHandler(Socket socket) {
            this.voiceSocket = socket;
            try {
                dis = new DataInputStream(voiceSocket.getInputStream());
                dos = new DataOutputStream(voiceSocket.getOutputStream());
            } catch (IOException e) {
                System.err.println("Error setting up streams for voice client: " + e.getMessage());
                Logger.getLogger(VoiceClientHandler.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        public void sendData(byte[] voiceData) {
            try {
                dos.write(voiceData);
                dos.flush();
            } catch (IOException e) {
                System.err.println("Error sending voice data to client: " + e.getMessage());
                closeClientConnection();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[4096]; // Adjust buffer size as needed
            int bytesRead;
            try {
                while (!voiceSocket.isClosed()) {
                    bytesRead = dis.read(buffer);
                    if (bytesRead != -1) {
                        byte[] voiceData = new byte[bytesRead];
                        System.arraycopy(buffer, 0, voiceData, 0, bytesRead);
                        // Forward the voice data to the other voice client
                        forwardVoiceData(voiceData, this);
                    }
                }
            } catch (IOException e) {
                System.err.println("Voice client disconnected: " + voiceSocket.getInetAddress().getHostAddress() + " - " + e.getMessage());
            } finally {
                closeClientConnection();
            }
        }

        private void closeClientConnection() {
            try {
                if (dis != null) dis.close();
                if (dos != null) dos.close();
                if (voiceSocket != null && !voiceSocket.isClosed()) {
                    voiceSocket.close();
                }
            } catch (IOException e) {
                Logger.getLogger(VoiceClientHandler.class.getName()).log(Level.SEVERE, null, e);
            }
            voiceClients.remove(this);
            System.out.println("Voice client disconnected: " + voiceSocket.getInetAddress().getHostAddress() + ". Remaining voice clients: " + voiceClients.size());
        }
    }
}


