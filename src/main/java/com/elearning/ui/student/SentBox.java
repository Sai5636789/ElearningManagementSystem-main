package com.elearning.ui.student;

import com.elearning.dao.MessageDao;
import com.elearning.config.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SentBox extends JFrame implements ActionListener, ListSelectionListener {
    private static final Logger log = LoggerFactory.getLogger(SentBox.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final MessageDao messageDao = new MessageDao();

    private JList<String> messagesList;
    private DefaultListModel<String> listModel;
    private JLabel title, messageLbl;
    private JPanel mainPanel;
    private JTextArea messageTxt;
    private JButton messageBtn, deleteBtn;
    private JScrollPane scroll1, scroll2;

    private List<com.elearning.model.Message> messages = new ArrayList<>();
    private int currentToUserID, currentMessageID;
    private String currentToUserName;

    public SentBox() {
        super("SentBox");
        setLayout(new BorderLayout());

        title = new JLabel("SentBox");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font(Font.SERIF, Font.BOLD, 23));
        title.setBackground(Color.LIGHT_GRAY);
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        add(title, BorderLayout.NORTH);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        add(mainPanel, BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        messagesList = new JList<>(listModel);
        messagesList.setFixedCellHeight(40);
        messagesList.setFixedCellWidth(150);
        messagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messagesList.addListSelectionListener(this);
        scroll1 = new JScrollPane(messagesList);
        scroll1.setBounds(50, 50, 500, 220);
        mainPanel.add(scroll1);

        loadMessages();

        messageLbl = new JLabel("Message");
        messageLbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        messageLbl.setBounds(30, 270, 70, 50);
        mainPanel.add(messageLbl);

        messageTxt = new JTextArea();
        messageTxt.setLineWrap(true);
        messageTxt.setWrapStyleWord(true);
        messageTxt.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        messageTxt.setEditable(false);
        scroll2 = new JScrollPane(messageTxt);
        scroll2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll2.setBounds(30, 310, 550, 210);
        mainPanel.add(scroll2);

        messageBtn = new JButton("Text");
        messageBtn.addActionListener(this);
        messageBtn.setEnabled(false);
        messageBtn.setBounds(455, 530, 80, 30);
        mainPanel.add(messageBtn);

        deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(this);
        deleteBtn.setEnabled(false);
        deleteBtn.setBounds(70, 530, 80, 30);
        mainPanel.add(deleteBtn);

        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 640);
        setLocation(385, 100);
        setVisible(true);
    }

    private void loadMessages() {
        listModel.clear();
        messageTxt.setText("");
        messageBtn.setEnabled(false);
        deleteBtn.setEnabled(false);

        try {
            int studentId = SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);
            messages = messageDao.getSentBox(studentId);
            for (com.elearning.model.Message m : messages) {
                String receiver = m.getReceiverName() != null ? m.getReceiverName() : "User #" + m.getReceiverId();
                String timeStr = m.getTimestamp() != null ? m.getTimestamp().format(FORMATTER) : "";
                String entry = String.format("<html><b>To: %s</b> &nbsp;&nbsp;&nbsp; <i>%s</i></html>", receiver, timeStr);
                listModel.addElement(entry);
            }
        } catch (Exception e) {
            log.error("Failed to load sentbox", e);
            JOptionPane.showMessageDialog(this, "Error loading sentbox: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == messageBtn) {
            int currentStudentId = SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);
            new Message(currentStudentId, currentToUserName, currentToUserID);
        } else if (e.getSource() == deleteBtn) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this sent message?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int studentId = SessionContext.getUserIdOrDefault(StudentLogin.currentStudentID);
                    boolean ok = messageDao.deleteFromSentBox(currentMessageID, studentId);
                    if (ok) {
                        JOptionPane.showMessageDialog(this, "Message Deleted!");
                        loadMessages();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete message", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception exception) {
                    log.error("Failed to delete sent message", exception);
                    JOptionPane.showMessageDialog(this, "Error: " + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int index = messagesList.getSelectedIndex();
            if (index >= 0 && index < messages.size()) {
                com.elearning.model.Message m = messages.get(index);
                messageTxt.setText(m.getContent());
                currentToUserID = m.getReceiverId();
                currentToUserName = m.getReceiverName() != null ? m.getReceiverName() : "User #" + m.getReceiverId();
                currentMessageID = m.getId();
                messageBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
            }
        }
    }

    public static void main(String[] args) {
        new SentBox();
    }
}