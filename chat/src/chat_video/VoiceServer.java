package chat_video;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.WindowConstants;


public class VoiceServer extends javax.swing.JFrame {

    private static TargetDataLine microphone;
    private static SourceDataLine speakers;
    private static Socket voiceSocket;
    private static InputStream inputStreamFromServer;
    private static OutputStream outputStreamToServer;

    public VoiceServer() {
        initComponents();
        setLocation(820,320);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnConnect = new javax.swing.JButton();
        btnConnect1 = new javax.swing.JButton();
        LBLstatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btnConnect.setText("END");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        btnConnect1.setText("Connect");
        btnConnect1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnect1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LBLstatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConnect1, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))
                .addGap(72, 72, 72)
                .addComponent(btnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConnect1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(LBLstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        endCall();
        dispose();
    }//GEN-LAST:event_btnConnectActionPerformed

    public void endCall(){
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
        if (speakers != null) {
            speakers.stop();
            speakers.close();
        }
        try {
            if (voiceSocket != null && !voiceSocket.isClosed()) {
                voiceSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void startVoiceCall(String serverIp) {
        try {
            connect(serverIp);
        } catch (IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    private void btnConnect1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnect1ActionPerformed
        // This button is likely not needed if called from chat_client
        // If used, it needs to get serverIp from somewhere
        // try {
        //     connect("localhost"); // Placeholder
        // } catch (IOException | LineUnavailableException ex) {
        //     ex.printStackTrace();
        // }
    }//GEN-LAST:event_btnConnect1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        endCall();
        dispose();
    }//GEN-LAST:event_formWindowClosing

    public static void connect(String serverIp) throws IOException, LineUnavailableException {
        voiceSocket = new Socket(serverIp, 1203);
        VoiceServer.LBLstatus.setText("Connected to Voice Server!");

        outputStreamToServer = voiceSocket.getOutputStream();
        inputStreamFromServer = voiceSocket.getInputStream();

        AudioFormat format = new AudioFormat(16000, 8, 2, true, true);

        microphone = AudioSystem.getTargetDataLine(format);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();

        speakers = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
        speakers.open(format);
        speakers.start();

        // Thread to send local audio
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytesRead;
            try {
                while (microphone.isOpen() && !voiceSocket.isClosed()) {
                    bytesRead = microphone.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        outputStreamToServer.write(buffer, 0, bytesRead);
                        outputStreamToServer.flush();
                    }
                }
            } catch (IOException e) {
                System.err.println("Error sending voice data: " + e.getMessage());
            }
        }).start();

        // Thread to receive remote audio
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytesRead;
            try {
                while (speakers.isOpen() && !voiceSocket.isClosed()) {
                    bytesRead = inputStreamFromServer.read(buffer);
                    if (bytesRead != -1) {
                        speakers.write(buffer, 0, bytesRead);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error receiving voice data: " + e.getMessage());
            }
        }).start();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VoiceServer().setVisible(true);
            }
        });
        // This main method will not be used directly when called from chat_client
        // It's kept for standalone testing if needed, but will require a serverIp argument
        // try {
        //     connect("localhost"); // Placeholder
        // } catch (IOException | LineUnavailableException ex) {
        //     ex.printStackTrace();
        // }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel LBLstatus;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnConnect1;
    // End of variables declaration//GEN-END:variables
}


