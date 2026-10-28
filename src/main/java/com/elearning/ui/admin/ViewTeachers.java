package com.elearning.ui.admin;

import com.elearning.dao.UserDao;
import com.elearning.model.Role;
import com.elearning.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class ViewTeachers extends JFrame implements ActionListener {
    private static final Logger log = LoggerFactory.getLogger(ViewTeachers.class);
    private final UserDao userDao = new UserDao();

    private final String[] columnNames = {"ID", "First Name", "Last Name", "Email", "Username", "Gender", "Picture"};
    private JTable table;
    private JButton deleteButton;
    private DefaultTableModel model;
    private JLabel title;

    public ViewTeachers() {
        super("View Teachers");
        setLayout(new BorderLayout());

        title = new JLabel("Teachers", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(25.0f));
        title.setBackground(Color.LIGHT_GRAY);
        title.setForeground(Color.BLACK);
        title.setOpaque(true);
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 6) {
                    return ImageIcon.class;
                }
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.LIGHT_GRAY);
        JScrollPane pane = new JScrollPane(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setRowHeight(96);
        add(pane, BorderLayout.CENTER);

        deleteButton = new JButton("Delete");
        deleteButton.setHorizontalAlignment(JButton.CENTER);
        deleteButton.addActionListener(this);
        deleteButton.setToolTipText("Click to Delete Selected Record.");
        deleteButton.setPreferredSize(new Dimension(20, 50));
        add(deleteButton, BorderLayout.SOUTH);

        loadTeachers();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 500);
        setLocation(250, 200);
        setVisible(true);
    }

    private void loadTeachers() {
        model.setRowCount(0);
        try {
            List<User> teachers = userDao.findAllByRole(Role.TEACHER);
            for (User t : teachers) {
                ImageIcon icon = getImageIcon(t.getPicture());
                model.addRow(new Object[]{
                        t.getId(),
                        t.getFirstName(),
                        t.getLastName(),
                        t.getEmail(),
                        t.getUsername(),
                        t.getGender(),
                        icon
                });
            }
        } catch (Exception e) {
            log.error("Failed to load teachers", e);
            JOptionPane.showMessageDialog(this, "Error loading teachers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImageIcon getImageIcon(byte[] byteImage) {
        if (byteImage == null || byteImage.length == 0) {
            return null;
        }
        try (InputStream is = new ByteArrayInputStream(byteImage)) {
            BufferedImage bufferedImage = ImageIO.read(is);
            if (bufferedImage != null) {
                Image scaled = bufferedImage.getScaledInstance(96, 96, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (Exception ex) {
            log.warn("Failed to decode teacher picture", ex);
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == deleteButton) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this teacher?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean ok = userDao.deleteAccount(id, Role.TEACHER);
                        if (ok) {
                            JOptionPane.showMessageDialog(this, "Teacher deleted successfully");
                            loadTeachers();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete teacher", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception exception) {
                        log.error("Failed to delete teacher", exception);
                        JOptionPane.showMessageDialog(this, "Error: " + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a teacher row first", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new ViewTeachers();
    }
}