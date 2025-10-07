package com.serine.library.ui;

import com.serine.library.model.Book;
import com.serine.library.model.Member;
import com.serine.library.service.LibraryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RecommendationPanel extends JPanel {
    private final LibraryService service;
    private final JComboBox<String> memberCombo;
    private final DefaultTableModel tableModel;

    public RecommendationPanel(LibraryService service) {
        this.service = service;
        setBackground(new Color(245, 248, 255));
        UIManager.put("Button.background", new Color(66, 135, 245));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Panel.background", new Color(245, 248, 255));
        UIManager.put("Label.foreground", new Color(30, 30, 30));
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        memberCombo = new JComboBox<>();
        JButton recommendBtn = new JButton("Get Recommendations");
        JButton refreshBtn = new JButton("Refresh Members");

        top.add(new JLabel("Member:"));
        top.add(memberCombo);
        top.add(recommendBtn);
        top.add(refreshBtn);
        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Genre"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        recommendBtn.addActionListener(e -> {
            String selected = (String) memberCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a member first.");
                return;
            }
            int memberId = Integer.parseInt(selected.split(" - ")[0]);
            List<Book> recs = service.recommendBooks(memberId);
            populateTable(recs);
        });

        refreshBtn.addActionListener(e -> refreshMembers());
        refreshMembers();
    }

    private void refreshMembers() {
        memberCombo.removeAllItems();
        for (Member m : service.listAllMembers()) {
            memberCombo.addItem(m.getId() + " - " + m.getName());
        }
    }

    private void populateTable(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book b : books) {
            tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getGenre()});
        }
    }
}
