package com.serine.library.ui;

import com.serine.library.model.Book;
import com.serine.library.service.LibraryService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BookPanel extends JPanel {
    private final LibraryService service;
    private final JTextArea queueArea;
    private final JList<Book> bookList;
    private final DefaultListModel<Book> bookListModel;
    private final JTextField titleField, authorField, genreField, copiesField, searchField;

    public BookPanel(LibraryService service) {
        this.service = service;
        setBackground(new Color(242, 247, 255));
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ==== Top Section: Form ====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 240), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("Title:");
        JLabel authorLabel = new JLabel("Author:");
        JLabel genreLabel = new JLabel("Genre:");
        JLabel copiesLabel = new JLabel("Copies:");
        titleField = new JTextField(15);
        authorField = new JTextField(15);
        genreField = new JTextField(15);
        copiesField = new JTextField(5);

        JButton addBtn = styledButton("Add Book", new Color(66, 135, 245));
        JButton delBtn = styledButton("Delete Book", new Color(231, 76, 60));
        JButton refreshBtn = styledButton("Refresh", new Color(52, 152, 219));
        JButton searchBtn = styledButton("Search", new Color(52, 152, 219));

        // --- Row 0 ---
        gbc.gridy = 0;
        gbc.gridx = 0; formPanel.add(titleLabel, gbc);
        gbc.gridx = 1; formPanel.add(titleField, gbc);
        gbc.gridx = 2; formPanel.add(authorLabel, gbc);
        gbc.gridx = 3; formPanel.add(authorField, gbc);

        // --- Row 1 ---
        gbc.gridy = 1;
        gbc.gridx = 0; formPanel.add(genreLabel, gbc);
        gbc.gridx = 1; formPanel.add(genreField, gbc);
        gbc.gridx = 2; formPanel.add(copiesLabel, gbc);
        gbc.gridx = 3; formPanel.add(copiesField, gbc);

        // --- Row 2 (Buttons) ---
        gbc.gridy = 2;
        gbc.gridx = 0; formPanel.add(addBtn, gbc);
        gbc.gridx = 1; formPanel.add(delBtn, gbc);
        gbc.gridx = 2; formPanel.add(refreshBtn, gbc);

        // --- Row 3 (Search) ---
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        searchField = new JTextField(20);
        formPanel.add(searchField, gbc);
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        formPanel.add(searchBtn, gbc);

        add(formPanel, BorderLayout.NORTH);

        // ==== Center: Book List ====
        bookListModel = new DefaultListModel<>();
        bookList = new JList<>(bookListModel);
        bookList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bookList.setBackground(Color.WHITE);
        bookList.setBorder(BorderFactory.createTitledBorder("Book List"));
        bookList.setCellRenderer(new HighlightRenderer());
        bookList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showReservationQueue();
        });

        JScrollPane bookScroll = new JScrollPane(bookList);
        bookScroll.setBorder(BorderFactory.createLineBorder(new Color(210, 220, 240)));
        add(bookScroll, BorderLayout.CENTER);

        // ==== Bottom: Reservation Queue ====
        queueArea = new JTextArea(5, 40);
        queueArea.setEditable(false);
        queueArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        queueArea.setBorder(BorderFactory.createTitledBorder("Reservation Queue"));
        queueArea.setBackground(Color.WHITE);
        add(new JScrollPane(queueArea), BorderLayout.SOUTH);

        // ==== Button Actions ====
        addBtn.addActionListener(e -> addBook());
        delBtn.addActionListener(e -> deleteBook());
        searchBtn.addActionListener(e -> searchBooks());
        refreshBtn.addActionListener(e -> refreshBooks());

        refreshBooks();
    }

    private JButton styledButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setBackground(baseColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
        });
        return btn;
    }

    // --- Add book ---
    private void addBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String genre = genreField.getText().trim();
        int copies = 1;
        try { copies = Integer.parseInt(copiesField.getText().trim()); } catch (NumberFormatException ignored) {}

        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and author required.");
            return;
        }

        service.addBook(title, author, copies);
        JOptionPane.showMessageDialog(this, "Book added successfully.");
        titleField.setText("");
        authorField.setText("");
        genreField.setText("");
        copiesField.setText("");
        refreshBooks();
    }

    // --- Delete book ---
    private void deleteBook() {
        Book selected = bookList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a book to delete.");
            return;
        }
        service.deleteBook(selected.getId());
        refreshBooks();
    }

    // --- Search books ---
    private void searchBooks() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) { refreshBooks(); return; }

        List<Book> results = service.listAllBooks().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(query)
                        || b.getAuthor().toLowerCase().contains(query)
                        || (b.getGenre() != null && b.getGenre().toLowerCase().contains(query)))
                .toList();

        bookListModel.clear();
        for (Book b : results) bookListModel.addElement(b);
        ((HighlightRenderer) bookList.getCellRenderer()).setHighlight(query);
    }

    private void refreshBooks() {
        bookListModel.clear();
        for (Book b : service.listAllBooks()) bookListModel.addElement(b);
        ((HighlightRenderer) bookList.getCellRenderer()).setHighlight(null);
        queueArea.setText("");
    }

    private void showReservationQueue() {
        Book book = bookList.getSelectedValue();
        if (book == null) return;
        var queue = book.getReservationQueue();
        if (queue.isEmpty()) queueArea.setText("No current reservations for this book.");
        else {
            StringBuilder sb = new StringBuilder("Current reservation queue:\n\n");
            int pos = 1;
            for (var m : queue) sb.append(pos++).append(". ").append(m.getName()).append("\n");
            queueArea.setText(sb.toString());
        }
    }

    // --- Custom Renderer with highlight background ---
    private static class HighlightRenderer extends DefaultListCellRenderer {
        private String highlight;
        public void setHighlight(String q) { this.highlight = q == null ? null : q.toLowerCase(); }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Book b) {
                String text = b.getId() + " - " + b.getTitle()
                        + " (" + b.getAuthor() + ") | " + b.getGenre()
                        + " - avail: " + b.getAvailableCopies();

                if (highlight != null && !highlight.isEmpty())
                    text = highlightMatch(text, highlight);

                label.setFont(new Font("SansSerif", Font.PLAIN, 13));
                label.setText("<html>" + text + "</html>");
            }
            return label;
        }

        private String highlightMatch(String text, String query) {
            String lower = text.toLowerCase();
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < text.length()) {
                int idx = lower.indexOf(query, i);
                if (idx == -1) {
                    sb.append(text.substring(i));
                    break;
                }
                sb.append(text, i, idx)
                        .append("<span style='background-color: #fff7a0;'>")
                        .append(text, idx, idx + query.length())
                        .append("</span>");
                i = idx + query.length();
            }
            return sb.toString();
        }
    }
}
