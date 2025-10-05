package com.serine.library.service;

import com.serine.library.model.Book;
import com.serine.library.model.Member;
import com.serine.library.repository.InMemoryBookRepository;
import com.serine.library.repository.InMemoryMemberRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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

}