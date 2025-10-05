package com.serine.library.service;

import com.serine.library.model.*;
import com.serine.library.repository.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

public class LibraryServiceTest {
    @Test
    void testAddBook() {
        var service = new LibraryService(new InMemoryBookRepository(), new InMemoryMemberRepository());
        Book b = service.addBook("Test Driven Development", "Kent Beck", 1);
        assertEquals("Test Driven Development", b.getTitle());
    }

    @Test
    void testRegisterMember() {
        var service = new LibraryService(new InMemoryBookRepository(), new InMemoryMemberRepository());
        Member m = service.registerMember("Alice", 5);
        assertEquals("Alice", m.getName());
        assertEquals(5, m.getBorrowLimit());
    }

    @Test
    void testBorrowBookSuccess() {
        var service = new LibraryService(new InMemoryBookRepository(), new InMemoryMemberRepository());
        Book b = service.addBook("Clean Code", "Robert C. Martin", 2);
        Member m = service.registerMember("Bob", 3);

        boolean borrowed = service.borrowBook(m.getId(), b.getId());
        assertTrue(borrowed, "Borrow should succeed");
        assertEquals(1, b.getAvailableCopies(), "Copies should decrease after borrow");
    }

    @Test
    void testBorrowBookFailWhenNoCopies() {
        var service = new LibraryService(new InMemoryBookRepository(), new InMemoryMemberRepository());
        Book b = service.addBook("Algorithms", "Cormen", 1);
        Member m1 = service.registerMember("Alice", 3);
        Member m2 = service.registerMember("Bob", 3);

        // Alice borrows first
        assertTrue(service.borrowBook(m1.getId(), b.getId()));

        // Bob tries but should fail
        boolean borrowed = service.borrowBook(m2.getId(), b.getId());
        assertFalse(borrowed, "Borrow should fail when no copies left");
    }

    @Test
    void testReturnBook() {
        var service = new LibraryService(new InMemoryBookRepository(), new InMemoryMemberRepository());
        Book b = service.addBook("Refactoring", "Martin Fowler", 1);
        Member m = service.registerMember("Charlie", 2);

        service.borrowBook(m.getId(), b.getId());
        boolean returned = service.returnBook(m.getId(), b.getId());

        assertTrue(returned, "Return should succeed");
        assertEquals(1, b.getAvailableCopies(), "Copies should restore after return");
    }

    @Test
    void testSearchBooks() {
        var service = new LibraryService(new InMemoryBookRepository(), new InMemoryMemberRepository());
        service.addBook("Effective Java", "Joshua Bloch", 2);
        service.addBook("Java Concurrency in Practice", "Goetz", 1);

        var results = service.searchBooks("Java");
        assertEquals(2, results.size(), "Should find 2 books containing 'Java'");
    }

    @Test
    void testBorrowLimit() {
        var service = new LibraryService(new InMemoryBookRepository(), new InMemoryMemberRepository());
        Book b1 = service.addBook("Book1", "A", 1);
        Book b2 = service.addBook("Book2", "B", 1);
        Book b3 = service.addBook("Book3", "C", 1);
            Member m = service.registerMember("John", 2);

    assertTrue(service.borrowBook(m.getId(), b1.getId()));
    assertTrue(service.borrowBook(m.getId(), b2.getId()));

    // This should fail because limit is 2
    assertFalse(service.borrowBook(m.getId(), b3.getId())); 
}
    @Test
    void testBookReservation() {
        var service = new LibraryService(new InMemoryBookRepository(), new InMemoryMemberRepository());
        Book book = service.addBook("Clean Code", "Robert C. Martin", 1);
        Member m1 = service.registerMember("Alice", 3);
        Member m2 = service.registerMember("Bob", 3);

        // Alice borrows first
        assertTrue(service.borrowBook(m1.getId(), book.getId()));

        // Bob reserves since no copies left
        service.reserveBook(book.getId(), m2.getId());
        assertEquals(1, book.getReservationQueue().size());
        assertTrue(book.getReservationQueue().contains(m2));

        // Alice returns, Bob gets notified
        assertTrue(service.returnBook(m1.getId(), book.getId()));
        assertEquals(1, book.getAvailableCopies());
    }

    @Test
    void testOverdueBookDetection() {
        LibraryService service = new LibraryService(new InMemoryBookRepository(), new InMemoryMemberRepository());
        Member m = service.registerMember("Alice", 3);
        Book b = service.addBook("1984", "Orwell", 1);

        // Simulate overdue borrow
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        BorrowRecord overdue = new BorrowRecord(b, tenDaysAgo, 5); // due 5 days ago
        m.getBorrowedBooks().add(overdue);
        
        assertTrue(m.hasOverdueBooks(), "Member should have overdue books");
    }

    @Test
    public void testSearchBooksByTitle() {
        BookRepository bookRepo = new InMemoryBookRepository();
        MemberRepository memberRepo = new InMemoryMemberRepository();
        LibraryService library = new LibraryService(bookRepo, memberRepo);
    
        Book book1 = new Book("Effective Java", "Joshua Bloch", 2);
        Book book2 = new Book("Clean Code", "Robert Martin", 1);
        bookRepo.save(book1);
        bookRepo.save(book2);

        List<Book> results = library.searchBooksByTitle("effective");
        assertEquals(1, results.size());
        assertEquals("Effective Java", results.get(0).getTitle());
    }
    
    @Test
    public void testSearchBooksByAuthor() {
        BookRepository bookRepo = new InMemoryBookRepository();
        MemberRepository memberRepo = new InMemoryMemberRepository();
        LibraryService library = new LibraryService(bookRepo, memberRepo);

        Book book1 = new Book("Effective Java", "Joshua Bloch", 2);
        Book book2 = new Book("Clean Code", "Robert Martin", 1);
        bookRepo.save(book1);
        bookRepo.save(book2);
        
        List<Book> results = library.searchBooksByAuthor("martin");
        assertEquals(1, results.size());
        assertEquals("Robert Martin", results.get(0).getAuthor());
    }

    @Test
    public void testSearchBooksByAvailability() {
        BookRepository bookRepo = new InMemoryBookRepository();
        MemberRepository memberRepo = new InMemoryMemberRepository();
        LibraryService library = new LibraryService(bookRepo, memberRepo);

        Book book1 = new Book("Effective Java", "Joshua Bloch", 1);
        Book book2 = new Book("Clean Code", "Robert Martin", 1);
        bookRepo.save(book1);
        bookRepo.save(book2);

        // Borrow one book
        Member member = new Member("Alice");
        memberRepo.save(member);
        library.borrowBook(member.getId(), book1.getId());


        // check filters
        List<Book> available = library.searchBooksByAvailability(true);
        List<Book> unavailable = library.searchBooksByAvailability(false);

        assertEquals(1, available.size());
        assertEquals("Clean Code", available.get(0).getTitle());

        assertEquals(1, unavailable.size());
        assertEquals("Effective Java", unavailable.get(0).getTitle());
    }

}