package com.serine.library.ui;

import com.serine.library.model.Book;
import com.serine.library.model.Member;
import com.serine.library.service.LibraryService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BorrowPanel extends JPanel {
    private final LibraryService service;
    private final JComboBox<String> memberCombo;
    private final JComboBox<String> bookCombo;

    public BorrowPanel(LibraryService service) {
        setBackground(new Color(245, 248, 255));
        UIManager.put("Button.background", new Color(66, 135, 245));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Panel.background", new Color(245, 248, 255));
        UIManager.put("Label.foreground", new Color(30, 30, 30));

        this.service = service;
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new GridLayout(4, 2, 8, 8));
        memberCombo = new JComboBox<>();
        bookCombo = new JComboBox<>();
        JButton borrowBtn = new JButton("Borrow Selected");
        JButton returnBtn = new JButton("Return Selected");
        JButton reserveBtn = new JButton("Reserve Selected");        
        JButton refreshBtn = new JButton("Refresh lists");

        top.add(new JLabel("Member:"));
        top.add(memberCombo);
        top.add(new JLabel("Book:"));
        top.add(bookCombo);
        top.add(borrowBtn);
        top.add(returnBtn);
        top.add(new JLabel("")); // placeholder for alignment
        top.add(reserveBtn);

        add(top, BorderLayout.NORTH);
        add(refreshBtn, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refreshLists());

        borrowBtn.addActionListener(e -> {
            String mem = (String) memberCombo.getSelectedItem();
            String bk = (String) bookCombo.getSelectedItem();
            if (mem == null || bk == null) {
                JOptionPane.showMessageDialog(this, "Choose member and book.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int memberId = Integer.parseInt(mem.split(" - ")[0]);
            int bookId = Integer.parseInt(bk.split(" - ")[0]);

            boolean ok = service.borrowBook(memberId, bookId);
            JOptionPane.showMessageDialog(this, ok ? "Borrow successful." : "Borrow failed (limit/availability).");
            refreshLists();
        });

        returnBtn.addActionListener(e -> {
            String mem = (String) memberCombo.getSelectedItem();
            String bk = (String) bookCombo.getSelectedItem();
            if (mem == null || bk == null) {
                JOptionPane.showMessageDialog(this, "Choose member and book.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int memberId = Integer.parseInt(mem.split(" - ")[0]);
            int bookId = Integer.parseInt(bk.split(" - ")[0]);

            boolean ok = service.returnBook(memberId, bookId);
            JOptionPane.showMessageDialog(this, ok ? "Return successful." : "Return failed.");
            refreshLists();
        });

        reserveBtn.addActionListener(e -> {
            String mem = (String) memberCombo.getSelectedItem();
            String bk = (String) bookCombo.getSelectedItem();
            if (mem == null || bk == null) {
                JOptionPane.showMessageDialog(this, "Choose member and book.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int memberId = Integer.parseInt(mem.split(" - ")[0]);
            int bookId = Integer.parseInt(bk.split(" - ")[0]);

            String message = service.reserveBook(bookId, memberId);
            JOptionPane.showMessageDialog(this, message, "Reservation Result", JOptionPane.INFORMATION_MESSAGE);
            refreshLists();
        });
    }
  
    private void refreshLists() {
        memberCombo.removeAllItems();
        bookCombo.removeAllItems();

        List<Member> members = service.listAllMembers();
        for (Member m : members) memberCombo.addItem(m.getId() + " - " + m.getName());

        List<Book> books = service.listAllBooks();
        for (Book b : books) bookCombo.addItem(b.getId() + " - " + b.getTitle() + " (avail: " + b.getAvailableCopies() + ")");
    }
}
