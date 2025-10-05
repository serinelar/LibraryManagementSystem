package com.serine.library.service;

import com.serine.library.model.Book;
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
}