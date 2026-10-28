package com.elearning.ui.admin;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.elearning.dao.DBConnection;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class AdminManageAccount extends JFrame implements ActionListener{
    JPanel panel;
    JLabel title;
    JButton b1, b2, b3, b4, b5;
    FileInputStream fis = null;
    File f = null;
    public AdminManageAccount(){
        super("Manage Admin Account");
        
        panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;

        title = new JLabel("<html><h1><strong>Manage Account</strong></h1></html>");
        panel.add(title, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttons = new JPanel(new GridBagLayout());
        b1 = new JButton("Change Password");
        b1.addActionListener((ActionListener) this);
        b2 = new JButton("Change Name");
        b2.addActionListener((ActionListener) this);
        b3 = new JButton("Change Email");
        b3.addActionListener((ActionListener) this);
        b4 = new JButton("Change Profile");
        b4.addActionListener((ActionListener) this);
        b5 = new JButton("Exit"); 
        b5.addActionListener((ActionListener) this);
        buttons.add(b1, gbc);
        buttons.add(b2, gbc);
        buttons.add(b3, gbc);
        buttons.add(b4, gbc);
        buttons.add(b5, gbc);

        gbc.weighty = 1;
        panel.add(buttons, gbc);
        
        add(panel);
        
        setSize(420,320);
        setLocation(460,280);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == b1){
            new AdminChangePassword();
        }
        else if(ae.getSource() == b2){
            new AdminChangeName();
        }
        else if(ae.getSource() == b3){
            new AdminChangeEmail();
        }
        else if(ae.getSource() == b4){
            JFileChooser fchoser = new JFileChooser();
            int result = fchoser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION && fchoser.getSelectedFile() != null) {
                File image = fchoser.getSelectedFile();
                int adminId = com.elearning.config.SessionContext.getUserIdOrDefault(AdminLogin.currentAdminID);
                try (FileInputStream fis = new FileInputStream(image)) {
                    com.elearning.dao.UserDao userDao = new com.elearning.dao.UserDao();
                    boolean success = userDao.updateProfilePicture(adminId, com.elearning.model.Role.ADMIN, fis, (int) image.length());
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Profile updated Successfully!");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update profile", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(this, "Error: " + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if(ae.getSource() == b5){
            setVisible(false);
        }
    }
    public static void main(String[] args) {
        new AdminManageAccount();
    }
}
