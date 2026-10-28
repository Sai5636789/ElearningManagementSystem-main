package com.elearning.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class LoadingScreen extends JFrame {
    JPanel mainPanel;
    JProgressBar loadingBar;
    JLabel backgroundPic, upperPic, textLbl, loadingLbl, percentageLbl;

    public LoadingScreen() {
        super("E-Learning System");
        setSize(1280, 720);
        setLocation(35, 30);
        setLayout(new BorderLayout());

        ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("icons/systemIcon.png"));
        setIconImage(icon.getImage());

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        add(mainPanel, BorderLayout.CENTER);

        upperPic = new JLabel();
        upperPic.setIcon(new ImageIcon(ClassLoader.getSystemResource("icons/loadingIconImg.png")));
        upperPic.setBounds(460, 70, 360, 343);
        mainPanel.add(upperPic);

        loadingLbl = new JLabel("Loading...");
        loadingLbl.setHorizontalAlignment(JLabel.LEFT);
        loadingLbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        loadingLbl.setForeground(Color.WHITE);
        loadingLbl.setBounds(5, 630, 250, 30);

        percentageLbl = new JLabel("0 %");
        percentageLbl.setHorizontalAlignment(JLabel.RIGHT);
        percentageLbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        percentageLbl.setForeground(Color.WHITE);
        percentageLbl.setBounds(1160, 630, 100, 30);

        mainPanel.add(loadingLbl);
        mainPanel.add(percentageLbl);

        textLbl = new JLabel("Welcome To E-Learning System");
        textLbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 55));
        textLbl.setForeground(Color.WHITE);
        textLbl.setBounds(245, 420, 850, 80);
        mainPanel.add(textLbl);

        backgroundPic = new JLabel();
        backgroundPic.setIcon(new ImageIcon(ClassLoader.getSystemResource("icons/background.png")));
        backgroundPic.setBounds(0, 0, 1280, 720);
        mainPanel.add(backgroundPic);

        loadingBar = new JProgressBar();
        loadingBar.setPreferredSize(new Dimension(0, 25));
        loadingBar.setValue(0);
        loadingBar.setStringPainted(true);
        loadingBar.setForeground(Color.GREEN);
        add(loadingBar, BorderLayout.SOUTH);

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        startLoading();
    }

    public void startLoading() {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(15);
                    publish(i);
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                int i = chunks.get(chunks.size() - 1);
                loadingBar.setValue(i);
                percentageLbl.setText(i + " %");

                if (i == 10) {
                    loadingLbl.setText("Turning On Modules...");
                } else if (i == 25) {
                    loadingLbl.setText("Loading Modules...");
                } else if (i == 50) {
                    loadingLbl.setText("Connecting to Database...");
                } else if (i == 75) {
                    loadingLbl.setText("Connection Successful!");
                } else if (i == 90) {
                    loadingLbl.setText("Launching Application...");
                }
            }

            @Override
            protected void done() {
                new Main();
                dispose();
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoadingScreen::new);
    }
}
