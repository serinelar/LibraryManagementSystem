package com.serine.library.service;

import com.serine.library.model.Book;
import com.serine.library.model.Member;
import com.serine.library.repository.BookRepository;
import com.serine.library.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

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

public boolean returnBook(int memberId, int bookId) {
    Optional<Member> mOpt = memberRepo.findById(memberId);
    Optional<Book> bOpt = bookRepo.findById(bookId);

    if (mOpt.isEmpty() || bOpt.isEmpty()) return false;

    Member m = mOpt.get();
    Book b = bOpt.get();

    boolean borrowed = m.getBorrowedBooks().stream()
        .anyMatch(record -> record.getBook().equals(b));

    if (!borrowed) {
        System.out.println("This member didn't borrow this book.");
        return false;
    }

    b.setAvailableCopies(b.getAvailableCopies() + 1);
    m.returnBook(b);
    return true;
}
}
