package chat;

import chat_video.VoiceClient;
import chat_video.video_client;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.sound.sampled.LineUnavailableException;

public class chat_client extends javax.swing.JFrame {

    static Socket clientSocket;
    static DataInputStream din;
    static DataOutputStream dout;
    video_client vc = new video_client();
    chat_video.VoiceClient voc = new VoiceClient();
    // 13.62.30.17
    // localhost
    private static String serverIpAddress = "13.62.30.17"; // Default to localhost, will be updated

    public chat_client() {
        initComponents();
        video_chat.setVisible(false);
        msg_text.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        msg_text = new javax.swing.JTextField();
        msg_send = new javax.swing.JButton();
        video_chat = new javax.swing.JButton();
        btnVoiceChat = new javax.swing.JButton();
        video_chatMax = new javax.swing.JButton();
        btnEndCall = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtPaneChat = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("client");
        setBackground(new java.awt.Color(102, 204, 255));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        msg_text.setBackground(new java.awt.Color(204, 204, 255));
        getContentPane().add(msg_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 285, 354, 40));

        msg_send.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sent_48px.png"))); // NOI18N
        msg_send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                msg_sendActionPerformed(evt);
            }
        });
        getContentPane().add(msg_send, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 285, 48, 40));

        video_chat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_video_call_40px.png"))); // NOI18N
        video_chat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                video_chatActionPerformed(evt);
            }
        });
        getContentPane().add(video_chat, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 28, 48, 40));
        video_chat.getAccessibleContext().setAccessibleName("video chat");

        btnVoiceChat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_call_40px.png"))); // NOI18N
        btnVoiceChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVoiceChatActionPerformed(evt);
            }
        });
        getContentPane().add(btnVoiceChat, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 123, -1, 44));

        video_chatMax.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_video_call_40px.png"))); // NOI18N
        video_chatMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                video_chatMaxActionPerformed(evt);
            }
        });
        getContentPane().add(video_chatMax, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 173, 48, 40));

        btnEndCall.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons8_call_30px_1.png"))); // NOI18N
        btnEndCall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndCallActionPerformed(evt);
            }
        });
        getContentPane().add(btnEndCall, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 80, 48, -1));

        txtPaneChat.setEditable(false);
        txtPaneChat.setBackground(new java.awt.Color(73, 73, 168));
        jScrollPane2.setViewportView(txtPaneChat);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 28, 352, 239));

        jButton1.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 12)); // NOI18N
        jButton1.setText("File");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 231, 50, 30));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ggggg.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-1, -1, 440, 370));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    void appendMessage(String message, boolean isSender) {
        StyledDocument doc = txtPaneChat.getStyledDocument();

        // تحديد خصائص التنسيق
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontSize(attr, 14);
        StyleConstants.setForeground(attr, Color.WHITE);
        StyleConstants.setBackground(attr, isSender ? new Color(0, 132, 255) : new Color(96, 96, 96));
        StyleConstants.setAlignment(attr, isSender ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);
        StyleConstants.setLeftIndent(attr, 5);
        StyleConstants.setRightIndent(attr, 5);
        StyleConstants.setSpaceAbove(attr, 5);
        StyleConstants.setSpaceBelow(attr, 5);
        StyleConstants.setBold(attr, true);

        try {
            // إدراج الرسالة مع التنسيق
            doc.insertString(doc.getLength(), message + "\n", attr);
            doc.setParagraphAttributes(doc.getLength(), 1, attr, false);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    private void msg_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_msg_sendActionPerformed
        try {
            String msgout;
            msgout = msg_text.getText().trim();
            dout.writeUTF(msgout);
            appendMessage("Me: " + msgout, true);
        } catch (Exception ex) {
            Logger.getLogger(chat_client.class.getName()).log(Level.SEVERE, null, ex);
        }
        msg_text.setText("");
    }//GEN-LAST:event_msg_sendActionPerformed

    private void video_chatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_video_chatActionPerformed
        try {
            vc.startVideoClient(serverIpAddress);
        } catch (IOException ex) {
            Logger.getLogger(chat_client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_video_chatActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.setBounds(300, 300, 450, 400);
    }//GEN-LAST:event_formWindowOpened

    private void btnVoiceChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVoiceChatActionPerformed
        voc.setVisible(true);
        try {
            voc.startVoiceCall(serverIpAddress);
        } catch (IOException | LineUnavailableException ex) {
            Logger.getLogger(chat_client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnVoiceChatActionPerformed

    private void video_chatMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_video_chatMaxActionPerformed
        video_chatMax.setVisible(false);
        try {
            vc.startVideoClient(serverIpAddress);
            voc.startVoiceCall(serverIpAddress);
        } catch (IOException | LineUnavailableException ex) {
            Logger.getLogger(chat_client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_video_chatMaxActionPerformed

    private void btnEndCallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndCallActionPerformed
        voc.endCall();
        vc.endVideoCall();
    }//GEN-LAST:event_btnEndCallActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File fileToSend = fileChooser.getSelectedFile();
            try {
                dout.writeUTF("sending_file"); // إشعار الطرف الآخر
                dout.writeUTF(fileToSend.getName());
                dout.writeLong(fileToSend.length());

                FileInputStream fis = new FileInputStream(fileToSend);
                byte[] buffer = new byte[4096];
                int count;
                while ((count = fis.read(buffer)) > 0) {
                    dout.write(buffer, 0, count);
                }
                fis.close();

                appendMessage("Me: [File Sent] " + fileToSend.getName(), true);

            } catch (Exception e) {
                appendMessage("Me: [Error Sending File]", true);
                Logger.getLogger(chat_client.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {
        // Check if server IP is provided as a command-line argument
        if (args.length > 0) {
            serverIpAddress = args[0];
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new chat_client().setVisible(true);
            }
        });

        try {
            clientSocket = new Socket(serverIpAddress, 1201); 
            din = new DataInputStream(clientSocket.getInputStream());
            dout = new DataOutputStream(clientSocket.getOutputStream());
            String msgin = "";
            while (!clientSocket.isClosed()) {
                msgin = din.readUTF();

                if (msgin.equals("sending_file")) {
                    String fileName = din.readUTF();
                    long fileSize = din.readLong();

                    File file = new File("received_" + fileName); 
                    FileOutputStream fos = new FileOutputStream(file);

                    byte[] buffer = new byte[4096];
                    int count;
                    long totalRead = 0;

                    while (totalRead < fileSize && (count = din.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead))) > 0) {
                        fos.write(buffer, 0, count);
                        totalRead += count;
                    }

                    fos.close();
                    ((chat_client) javax.swing.JFrame.getFrames()[0]).appendMessage("Client: [File Received] " + file.getName(), false);

                } else {
                    ((chat_client) javax.swing.JFrame.getFrames()[0]).appendMessage("Client: " + msgin, false);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(chat_client.class.getName()).log(Level.SEVERE, null, ex);
            // Handle server disconnection or connection failure
            if (javax.swing.JFrame.getFrames().length > 0 && javax.swing.JFrame.getFrames()[0] instanceof chat_client) {
                ((chat_client) javax.swing.JFrame.getFrames()[0]).appendMessage("Disconnected from server.", false);
            }
        }
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnEndCall;
    private javax.swing.JButton btnVoiceChat;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton msg_send;
    private javax.swing.JTextField msg_text;
    private javax.swing.JTextPane txtPaneChat;
    private javax.swing.JButton video_chat;
    private javax.swing.JButton video_chatMax;
    // End of variables declaration                        
}


