package chat_video;

import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;

public class video_server extends javax.swing.JFrame {
    
    private static javax.swing.JLabel img_server_remote;
    private static javax.swing.JLabel img_server_local;
    
    private static Webcam webcam;
    private static ObjectOutputStream outputStreamToServer;
    private static Socket videoSocket;
    private static ObjectInputStream inputStreamFromServer;

    public static void startVideoServer(String serverIp) throws IOException, ClassNotFoundException{
            
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new video_server().setVisible(true);
            }
        });
        
        videoSocket = new Socket(serverIp, 1202); // Connect to HeadlessChatServer's video port
        System.out.println("Video Server (Client Mode): Connected to HeadlessChatServer for video.");
        
        outputStreamToServer = new ObjectOutputStream(videoSocket.getOutputStream());
        inputStreamFromServer = new ObjectInputStream(videoSocket.getInputStream());

        // Thread to receive and display remote video from server
        Thread receiveRemoteVideoThread = new Thread(new Runnable(){
            @Override
            public void run() {
                ImageIcon ic;
                while(true){
                    try {
                        byte[] frameData = (byte[]) inputStreamFromServer.readObject();
                        ic = new ImageIcon(frameData);
                        Image image=ic.getImage();
                        Image newImg=image.getScaledInstance(img_server_remote.getWidth(), img_server_remote.getHeight(), java.awt.Image.SCALE_SMOOTH);
                        ic=new ImageIcon(newImg);
                        img_server_remote.setIcon(ic);
                    } catch (IOException ex) {
                        Logger.getLogger(video_server.class.getName()).log(Level.SEVERE, null, ex);
                        break; // Exit loop on error
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(video_server.class.getName()).log(Level.SEVERE, null, ex);
                        break; // Exit loop on error
                    }
                }
            }
        });
        receiveRemoteVideoThread.start();

        // Thread to capture and send local video to server
        Thread sendLocalVideoThread = new Thread(new Runnable(){
            @Override
            public void run() {
                webcam = Webcam.getDefault();
                if (webcam == null) {
                    JOptionPane.showMessageDialog(null, "No webcam found for server.", "Webcam Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                webcam.open();
                ImageIcon ic;
                BufferedImage br;
                while(true){
                    try {
                        br = webcam.getImage();
                        ic = new ImageIcon(br);
                        
                        // Convert BufferedImage to byte array
                        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                        javax.imageio.ImageIO.write(br, "PNG", baos);
                        byte[] imageBytes = baos.toByteArray();
                        
                        outputStreamToServer.writeObject(imageBytes);
                        outputStreamToServer.flush();

                        Image image=ic.getImage();
                        Image newImg=image.getScaledInstance(img_server_local.getWidth(), img_server_local.getHeight(), java.awt.Image.SCALE_SMOOTH);
                        ic=new ImageIcon(newImg);
                        img_server_local.setIcon(ic);
                    } catch (IOException ex) {
                        Logger.getLogger(video_server.class.getName()).log(Level.SEVERE, null, ex);
                        break; // Exit loop on error
                    }
                }
            }
        });
        sendLocalVideoThread.start();
    }

    public void endVideoCall(){
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
        try {
            if (videoSocket != null && !videoSocket.isClosed()) {
                videoSocket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(video_server.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setVisible(false);
        this.dispose();
    }
        
    public video_server() {
        initComponents();
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        img_server_remote = new javax.swing.JLabel();
        img_server_local = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(153, 0, 0));
        setResizable(false);
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(img_server_remote, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(img_server_local, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(img_server_remote, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
            .addComponent(img_server_local, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setBounds(900, 300, 550, 323);
    }//GEN-LAST:event_formWindowOpened

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
      
    }//GEN-LAST:event_formWindowStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        dispose();
    }//GEN-LAST:event_formWindowClosing

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        // This main method will not be used directly when called from chat_server
        // It's kept for standalone testing if needed, but will require a serverIp argument
        // startVideoServer("localhost"); 
    }
}


