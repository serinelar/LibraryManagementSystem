package com.serine.library.service;

import com.serine.library.model.*;
import com.serine.library.repository.BookRepository;
import com.serine.library.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LibraryService {
    private final BookRepository bookRepo;
    private final MemberRepository memberRepo;
    
    public LibraryService(BookRepository bookRepo, MemberRepository memberRepo) {
        this.bookRepo = bookRepo;
        this.memberRepo = memberRepo;
    }
    
    // Add a new book and return the saved entity (with id)
    public Book addBook(String title, String author, int copies) {
        Book book = new Book(title, author, copies);
        return bookRepo.save(book);
    }
    
    // Register a new member
    public Member registerMember(String name, int borrowLimit) {
        Member m = new Member(name);
        m.setBorrowLimit(borrowLimit);
        return memberRepo.save(m);
    }
    
    
    public List<Book> listAllBooks() { return bookRepo.findAll(); }
    
    public List<Book> searchBooks(String query) { return bookRepo.findByTitleOrAuthor(query); }
    
    // Borrow logic: check availability and member's limit
    public boolean borrowBook(int memberId, int bookId) {
        Optional<Member> mOpt = memberRepo.findById(memberId);
        Optional<Book> bOpt = bookRepo.findById(bookId);
        
        if (mOpt.isEmpty() || bOpt.isEmpty()) return false;

        Member m = mOpt.get();
        Book b = bOpt.get();

        // Check borrow limit
        if (m.getBorrowedBooks().size() >= m.getBorrowLimit()) {
            System.out.println("Member has reached borrowing limit.");
            return false;
        }

        // Check availability
        if (b.getAvailableCopies() <= 0) {
            System.out.println("No copies available. Consider reserving.");
            return false;
        }
        
        // Borrow (14-day loan by default)
        b.setAvailableCopies(b.getAvailableCopies() - 1);
        m.borrowBook(b, 14);
        return true;
}

public void reserveBook(int bookId, int memberId) {
    Optional<Book> bOpt = bookRepo.findById(bookId);
    Optional<Member> mOpt = memberRepo.findById(memberId);

    if (bOpt.isEmpty() || mOpt.isEmpty()) {
        System.out.println("Book or Member not found.");
        return;
    }

    Book b = bOpt.get();
    Member m = mOpt.get();

    if (b.getAvailableCopies() > 0) {
        System.out.println("Book is available, no need to reserve. Just borrow it.");
    } else {
        b.reserveBook(m);
        System.out.println(m.getName() + " reserved " + b.getTitle());
    }
}

public boolean returnBook(int memberId, int bookId) {
    Optional<Book> bOpt = bookRepo.findById(bookId);
    Optional<Member> mOpt = memberRepo.findById(memberId);

    if (bOpt.isEmpty() || mOpt.isEmpty()) {
        System.out.println("Book or Member not found.");
        return false;
    }

    Book b = bOpt.get();
    Member m = mOpt.get();

    // find borrow record
    boolean wasOverdue = m.getBorrowedBooks().stream()
            .filter(record -> record.getBook().equals(b))
            .anyMatch(BorrowRecord::isOverdue);

    m.returnBook(b);
    b.setAvailableCopies(b.getAvailableCopies() + 1);

    if (wasOverdue) {
        System.out.println("⚠️ Book was returned overdue by " + m.getName());
    }

    // Notify next in line
    Member next = b.popNextReservation();
    if (next != null) {
        System.out.println("Book returned. Notifying " + next.getName() + " that " + b.getTitle() + " is available.");
        // Optionally auto-borrow for them
        // next.borrowBook(b, 14); b.setAvailable(false);
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

}
