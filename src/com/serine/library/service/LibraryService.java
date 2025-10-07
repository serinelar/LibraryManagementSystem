package com.serine.library.service;

import com.serine.library.model.*;
import com.serine.library.repository.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

public class LibraryService {
    private final BookRepository bookRepo;
    private final MemberRepository memberRepo;
    
    public LibraryService(BookRepository bookRepo, MemberRepository memberRepo) {
        this.bookRepo = bookRepo;
        this.memberRepo = memberRepo;
    }

    // Convenience constructor (in-memory default)
    public LibraryService() {
        this(new InMemoryBookRepository(), new InMemoryMemberRepository());
    }
    
    // Add a new book and return the saved entity (with id)
    public Book addBook(String title, String author, int copies) {
        Book book = new Book(title, author, copies);
        return bookRepo.save(book);
    }

    public Book addBook(String title, String author, String genre) {
        Book book = new Book(title, author, genre);
        return bookRepo.save(book);
    }
    
    // Register a new member
    public Member registerMember(String name, MembershipType type) {
        Member m = new Member(name, type);
        return memberRepo.save(m);
    }
    
    // Delete a book by ID
    public void deleteBook(int id) {
        bookRepo.delete(id);
    }
    // Delete a member by ID
    public void deleteMember(int id) {
        memberRepo.delete(id);
    }
  
    public List<Book> listAllBooks() { return bookRepo.findAll(); }
    public List<Member> listAllMembers() { return memberRepo.findAll(); }
    
    public List<Book> searchBooks(String query) { return bookRepo.findByTitleOrAuthor(query); }
    
    // Borrow logic: check availability and member's limit
    public boolean borrowBook(int memberId, int bookId) {
        Optional<Member> mOpt = memberRepo.findById(memberId);
        Optional<Book> bOpt = bookRepo.findById(bookId);
        
        if (mOpt.isEmpty() || bOpt.isEmpty()) return false;

        Member m = mOpt.get();
        Book b = bOpt.get();

        // Already borrowed?
        if (m.getBorrowedBooks().stream().anyMatch(r -> r.getBook().getId() == bookId))
            return false;

        // Limit & availability
        if (m.getBorrowedBooks().size() >= m.getBorrowLimit()) return false;
        if (!b.isAvailable()) return false;
        
        // Borrow (14-day loan by default)
        b.setAvailableCopies(b.getAvailableCopies() - 1);
        m.borrowBook(b, 14);
        bookRepo.save(b);
        memberRepo.save(m);
        return true;
}

public String reserveBook(int bookId, int memberId) {
    Optional<Book> bOpt = bookRepo.findById(bookId);
    Optional<Member> mOpt = memberRepo.findById(memberId);

    if (bOpt.isEmpty() || mOpt.isEmpty()) {
        return "Book or Member not found.";
    }

    Book b = bOpt.get();
    Member m = mOpt.get();

    if (b.getAvailableCopies() > 0) {
        return "Book is available, no need to reserve. Just borrow it.";
    }

    // Prevent duplicate reservation by the same member
    var queue = b.getReservationQueue();
        if (queue.stream().anyMatch(r -> r.getId() == m.getId()))
            return "You have already reserved this book.";

    // Reserve the book
    b.reserveBook(m);
    bookRepo.save(b);
    int position = b.getReservationQueue().size();
    return "Reservation successful. Your position in the queue is #" + position + ".";
}


public boolean returnBook(int memberId, int bookId) {
    Optional<Book> bOpt = bookRepo.findById(bookId);
    Optional<Member> mOpt = memberRepo.findById(memberId);

    if (bOpt.isEmpty() || mOpt.isEmpty()) return false;

    Book b = bOpt.get();
    Member m = mOpt.get();

    // Check if this member actually borrowed this book
    Optional<BorrowRecord> recordOpt = m.getBorrowedBooks().stream()
            .filter(record -> record.getBook().getId() == bookId)
            .findFirst();

    if (recordOpt.isEmpty()) {
        // Member never borrowed that specific book -> cannot return
        return false;
    }

    boolean wasOverdue = recordOpt.map(BorrowRecord::isOverdue).orElse(false);

    // Remove borrow record, increment availability and persist
    m.returnBook(b);
    memberRepo.save(m);

    // Handle reservation queue
    if (!b.getReservationQueue().isEmpty()) {
        Member next = b.popNextReservation();

    // Auto-borrow logic for next in queue
    if (next.getBorrowedBooks().size() < next.getBorrowLimit()) {
        next.borrowBook(b, 14); // 14-day auto loan
        memberRepo.save(next);
        JOptionPane.showMessageDialog(null,
                "Book returned: " + b.getTitle() + "\n" +
                "Automatically borrowed by next reserver: " + next.getName(),
          "Auto Borrow Notification", JOptionPane.INFORMATION_MESSAGE);
    } else {
        // If the next reserver reached limit → skip borrowing but keep reservation
        JOptionPane.showMessageDialog(null,
                next.getName() + " reached their borrow limit.\n"
                + "Book availability held until they free a slot.",
          "Reservation Pending", JOptionPane.WARNING_MESSAGE);
        }
    } else {
        b.setAvailableCopies(b.getAvailableCopies() + 1);
    }

    bookRepo.save(b);

    if (wasOverdue) {
        System.out.println("Book was returned overdue by " + m.getName());
    }

    return true;    
    }
    
    public List<Book> searchBooksByTitle(String title) {
        return bookRepo.findAll().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByAuthor(String author) {
        return bookRepo.findAll().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Book> searchBooksByAvailability(boolean available) {
        return bookRepo.findAll().stream()
                .filter(book -> book.isAvailable() == available)
                .collect(Collectors.toList());
    }

    public String exportMemberHistory(int memberId) {
        return memberRepo.findById(memberId)
               .map(Member::exportBorrowingHistory)
               .orElse("Member not found.");
    }

    // Recommendation System
    public List<Book> recommendBooks(int memberId) {
        Optional<Member> mOpt = memberRepo.findById(memberId);
        if (mOpt.isEmpty()) return Collections.emptyList();

        Member member = mOpt.get();

        // Genres the member already borrowed
        Set<String> borrowedGenres = member.getBorrowedBooks().stream()
                .map(r -> r.getBook().getGenre())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());


        // Books the member already borrowed (to exclude them)
        Set<Integer> borrowedBookIds = member.getBorrowedBooks().stream()
                .map(r -> r.getBook().getId())
                .collect(Collectors.toSet());

        // Candidate books: available and not already borrowed
        List<Book> allBooks = bookRepo.findAll();
        return allBooks.stream()
                .filter(b -> b.getAvailableCopies() > 0)
                .filter(b -> !borrowedBookIds.contains(b.getId()))
                .sorted((a, b) -> {
                    boolean aFav = borrowedGenres.contains(a.getGenre());
                    boolean bFav = borrowedGenres.contains(b.getGenre());
                    // prefer same-genre
                    return Boolean.compare(bFav, aFav);
                })
                .limit(10)
                .collect(Collectors.toList());
        }
}
