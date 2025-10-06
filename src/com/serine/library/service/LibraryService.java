package com.serine.library.service;

import com.serine.library.model.*;
import com.serine.library.repository.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

        // Check borrow limit
        if (m.getBorrowedBooks().size() >= m.getBorrowLimit()) return false;
        if (!b.isAvailable()) return false;
        
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

    if (bOpt.isEmpty() || mOpt.isEmpty()) return false;

    Book b = bOpt.get();
    Member m = mOpt.get();

    // find borrow record
    boolean wasOverdue = m.getBorrowedBooks().stream()
            .filter(record -> record.getBook().equals(b))
            .anyMatch(BorrowRecord::isOverdue);

    m.returnBook(b);
    b.setAvailableCopies(b.getAvailableCopies() + 1);

    if (wasOverdue) {
        System.out.println("Book was returned overdue by " + m.getName());
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
        Set<Book> borrowedBooks = member.getBorrowedBooks().stream()
                .map(BorrowRecord::getBook)
                .collect(Collectors.toSet());

        // 1. Collect genres borrowed by this member
        Set<String> preferredGenres = borrowedBooks.stream()
                .map(Book::getGenre)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 2. Candidate books: available + not borrowed yet
        List<Book> candidateBooks = bookRepo.findAll().stream()
                .filter(Book::isAvailable)
                .filter(b -> !borrowedBooks.contains(b))
                .collect(Collectors.toList());

        // 3. Hybrid scoring
        Map<Book, Double> scores = new HashMap<>();
        for (Book book : candidateBooks) {
            double score = 0.0;
            if (preferredGenres.contains(book.getGenre())) score += 1.0;

            long popularity = memberRepo.findAll().stream()
                    .filter(m -> m.getBorrowedBooks().stream()
                            .anyMatch(r -> r.getBook().equals(book)))
                    .count();
            score += popularity * 0.5;

            scores.put(book, score);
        }

        return scores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
