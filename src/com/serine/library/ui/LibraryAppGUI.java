package com.serine.library.ui;

import com.serine.library.service.LibraryService;
import com.serine.library.model.MembershipType;

import javax.swing.*;
import java.awt.*;

public class LibraryAppGUI extends JFrame {
    private final LibraryService service;

    public LibraryAppGUI(LibraryService service) {
        super("Library Management System");
        this.service = service;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 700);
        setLocationRelativeTo(null);

        preloadDemoData();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", new BookPanel(service));
        tabs.addTab("Members", new MemberPanel(service));
        tabs.addTab("Borrow / Return", new BorrowPanel(service));
        tabs.addTab("Recommendations", new RecommendationPanel(service));

        add(tabs, BorderLayout.CENTER);
    }

    private void preloadDemoData() {
        // Prefilled books
        service.addBook("The Great Gatsby", "F. Scott Fitzgerald", "Classic");
        service.addBook("1984", "George Orwell", "Dystopian");
        service.addBook("To Kill a Mockingbird", "Harper Lee", "Drama");
        service.addBook("Clean Code", "Robert C. Martin", "Programming");
        service.addBook("Harry Potter", "J.K. Rowling", "Fantasy");

        // Prefilled members
        service.registerMember("Alice", MembershipType.REGULAR);
        service.registerMember("Bob", MembershipType.PREMIUM);
        service.registerMember("Charlie", MembershipType.REGULAR);
    }

    public static void main(String[] args) {
        LibraryService service = new LibraryService();
        SwingUtilities.invokeLater(() -> {
            var gui = new LibraryAppGUI(service);
            gui.setVisible(true);
        });
    }
}
