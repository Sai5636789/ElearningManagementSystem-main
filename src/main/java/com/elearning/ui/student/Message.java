package com.elearning.ui.student;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.elearning.dao.DBConnection;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class Message extends JFrame implements ActionListener{
    JTextArea messageTextArea;
    JButton sendBtn;
    JLabel receiverNameLabel, writeMsgLbl;
    JScrollPane scroll;
    JPanel mainPanel;
    int to_ID;
    public Message(int from_ID, String toName, int to_ID){
        super("Message");
        setLayout(new BorderLayout());
        this.to_ID = to_ID;
        receiverNameLabel = new JLabel(toName);
        receiverNameLabel.setHorizontalAlignment(JLabel.CENTER);
        receiverNameLabel.setFont(new Font(Font.SERIF,Font.BOLD, 23));
        add(receiverNameLabel, BorderLayout.NORTH);
        
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        
        writeMsgLbl = new JLabel("Write Message");
        writeMsgLbl.setHorizontalAlignment(JLabel.LEFT);
        writeMsgLbl.setFont(new Font(Font.DIALOG,Font.PLAIN, 12));
        mainPanel.add(writeMsgLbl, BorderLayout.NORTH);
        
        messageTextArea = new JTextArea();
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);
        messageTextArea.setFont(new Font(Font.SERIF,Font.PLAIN, 18));
        messageTextArea.setToolTipText( "Write Message Here." );
        scroll = new JScrollPane(messageTextArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        mainPanel.add(scroll, BorderLayout.CENTER);
        
        sendBtn = new JButton("Send");
        sendBtn.setPreferredSize(new Dimension(0, 50));
        sendBtn.addActionListener(this);
        add(sendBtn, BorderLayout.SOUTH);
        
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400,320);
        setLocation(515,300);
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == sendBtn){
            String messageStr = messageTextArea.getText().trim();
            if (messageStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Message cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int senderId = com.elearning.config.SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);

            try {
                com.elearning.dao.MessageDao messageDao = new com.elearning.dao.MessageDao();
                boolean success = messageDao.sendMessage(senderId, to_ID, messageStr);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Message Sent");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to send message", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(Exception exception){
                JOptionPane.showMessageDialog(this, "Error sending message: " + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                exception.printStackTrace();
            }
        }
    }
}