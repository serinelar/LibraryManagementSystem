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
        Optional<Member> memberOpt = memberRepo.findById(memberId);
        Optional<Book> bookOpt = bookRepo.findById(bookId);
        
        if (memberOpt.isEmpty() || bookOpt.isEmpty()) return false;
        
        Member member = memberOpt.get();
        Book book = bookOpt.get();
        
        if (!member.canBorrowMore()) return false;
        if (book.getAvailableCopies() <= 0) return false;
        
        // perform borrow
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        member.borrowBook(bookId);
        
        bookRepo.save(book);
        memberRepo.save(member);
        return true;
    }
    
    public boolean returnBook(int memberId, int bookId) {
        Optional<Member> memberOpt = memberRepo.findById(memberId);
        Optional<Book> bookOpt = bookRepo.findById(bookId);
        
        if (memberOpt.isEmpty() || bookOpt.isEmpty()) return false;
        
        Member member = memberOpt.get();
        Book book = bookOpt.get();
        
        if (!member.getBorrowedBookIds().contains(bookId)) return false;
        
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        member.returnBook(bookId);
        
        bookRepo.save(book);
        memberRepo.save(member);
        
        return true;
    }
}
