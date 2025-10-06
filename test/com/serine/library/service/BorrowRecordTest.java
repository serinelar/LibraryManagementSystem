package com.serine.library.service;

import com.serine.library.model.*;
import com.serine.library.repository.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BorrowRecordTest {
    @Test
    void testBorrowAndReturnBook() {
        // Setup
        var service = new LibraryService(new InMemoryBookRepository(), new InMemoryMemberRepository());

        Book b = service.addBook("Clean Code", "Robert C. Martin", 1);
        Member m = service.registerMember("Alice", MembershipType.REGULAR);

        // Borrow book
        boolean borrowed = service.borrowBook(m.getId(), b.getId());
        assertTrue(borrowed);
        assertEquals(0, b.getAvailableCopies());
        assertEquals(1, m.getBorrowedBooks().size());

        // Check overdue is false right after borrowing
        var record = m.getBorrowedBooks().get(0);
        assertFalse(record.isOverdue());

        // Return book
        boolean returned = service.returnBook(m.getId(), b.getId());
        assertTrue(returned);
        assertEquals(1, b.getAvailableCopies());
        assertEquals(0, m.getBorrowedBooks().size());
    }
}
