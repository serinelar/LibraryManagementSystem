package com.serine.library;

import com.serine.library.repository.*;
import com.serine.library.service.LibraryService;
import com.serine.library.ui.CLI;

public class Main {
    public static void main(String[] args) {
        var bookRepo = new InMemoryBookRepository();
        var memberRepo = new InMemoryMemberRepository();
        var service = new LibraryService(bookRepo, memberRepo);
        
        var cli = new CLI(service);
        
        // Optional: seed some data for quick testing
        service.addBook("Clean Code", "Robert C. Martin", 2);
        service.addBook("Introduction to Algorithms", "Cormen et al.", 1);
        service.registerMember("Alice", 3);
        
        cli.start();
    }
}
