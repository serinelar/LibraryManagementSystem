package com.serine.library.ui;

import com.serine.library.model.Member;
import com.serine.library.model.MembershipType;
import com.serine.library.service.LibraryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MemberPanel extends JPanel {
    private final LibraryService service;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;

    public MemberPanel(LibraryService service) {
        this.service = service;

        setBackground(new Color(245, 248, 255));
        UIManager.put("Button.background", new Color(66, 135, 245));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Panel.background", new Color(245, 248, 255));
        UIManager.put("Label.foreground", new Color(30, 30, 30));
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JTextField nameField = new JTextField(14);
        JComboBox<MembershipType> typeCombo = new JComboBox<>(MembershipType.values());
        JButton addBtn = new JButton("Register Member");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton exportBtn = new JButton("Export History");

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Type:"));
        form.add(typeCombo);
        form.add(addBtn);
        form.add(deleteBtn);
        form.add(exportBtn);
        add(form, BorderLayout.NORTH);


        String[] cols = {"ID", "Name", "Type", "BorrowLimit", "BorrowedCount"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");

        bottom.add(new JLabel("Search by name:"));
        bottom.add(searchField);
        bottom.add(searchBtn);
        bottom.add(refreshBtn);
        add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name required.");
                return;
            }
            MembershipType type = (MembershipType) typeCombo.getSelectedItem();
            service.registerMember(name, type);
            nameField.setText("");
            refreshTable();
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a member to delete.");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            service.deleteMember(id);
            refreshTable();
        });

        exportBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a member to export history.");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            String history = service.exportMemberHistory(id);
            JTextArea ta = new JTextArea(history);
            ta.setEditable(false);
            ta.setRows(20);
            ta.setColumns(60);
            JScrollPane sp = new JScrollPane(ta);
            JOptionPane.showMessageDialog(this, sp, "Borrowing History for member " + id, JOptionPane.INFORMATION_MESSAGE);
        });

        searchBtn.addActionListener(e -> {
            String q = searchField.getText().trim();
            if (q.isEmpty()) refreshTable();
            else populateTable(service.listAllMembers().stream()
                    .filter(m -> m.getName().toLowerCase().contains(q.toLowerCase()))
                    .toList());
        });

        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            refreshTable();
        });

        refreshTable();
    }

    private void refreshTable() {
        populateTable(service.listAllMembers());
    }

    private void populateTable(List<Member> members) {
        tableModel.setRowCount(0);
        for (Member m : members) {
            tableModel.addRow(new Object[]{m.getId(), m.getName(), m.getType(), m.getBorrowLimit(), m.getBorrowedBooks().size()});
        }
    }
}
