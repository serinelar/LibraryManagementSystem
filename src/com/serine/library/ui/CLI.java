package com.serine.library.ui;

import com.serine.library.model.Book;
import com.serine.library.model.Member;
import com.serine.library.service.LibraryService;
import com.serine.library.repository.BookRepository;
import com.serine.library.repository.MemberRepository;

import java.util.List;
import java.util.Scanner;

public class CLI {
    private final LibraryService service;
    private final Scanner scanner = new Scanner(System.in);
    
    public CLI(LibraryService service) { this.service = service; }
    
    public void start() {
        printHeader();
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": addBook(); break;
                case "2": listBooks(); break;
                case "3": searchBooks(); break;
                case "4": registerMember(); break;
                case "5": borrowBook(); break;
                case "6": returnBook(); break;
                case "0": running = false; break;
                default: System.out.println("Invalid option. Try again.");
            }
        }
        System.out.println("Goodbye!");
    }
    
    private void printHeader() {
        System.out.println("=== Library Management (CLI) ===");
    }
    
    private void printMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("1) Add book");
        System.out.println("2) List all books");
        System.out.println("3) Search books by title/author");
        System.out.println("4) Register member");
        System.out.println("5) Borrow book");
        System.out.println("6) Return book");
        System.out.println("0) Exit");
        System.out.print("> ");
    }
    
    private void addBook() {
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Copies: ");
        int copies = Integer.parseInt(scanner.nextLine().trim());
        
        Book b = service.addBook(title, author, copies);
        System.out.println("Added: " + b);
    }

    private void listBooks() {
        List<Book> books = service.listAllBooks();
        if (books.isEmpty()) System.out.println("No books available.");
        for (Book b : books) System.out.println(b);
    }

    private void searchBooks() {
        System.out.print("Search query: ");
        String q = scanner.nextLine().trim();
        List<Book> res = service.searchBooks(q);
        if (res.isEmpty()) System.out.println("No results for '" + q + "'.");
        for (Book b : res) System.out.println(b);
    }
    private void registerMember() {
        System.out.print("Member name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Borrow limit (enter for default 3): ");
        String limitInput = scanner.nextLine().trim();
        int limit = limitInput.isEmpty() ? 3 : Integer.parseInt(limitInput);
        Member m = service.registerMember(name, limit);
        System.out.println("Registered: " + m);
    }

    private void borrowBook() {
        System.out.print("Member id: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Book id: ");
        int bookId = Integer.parseInt(scanner.nextLine().trim());
        boolean ok = service.borrowBook(memberId, bookId);
        System.out.println(ok ? "Borrow successful." : "Cannot borrow (check member/book/limits).\n");
    }
    
    private void returnBook() {
        System.out.print("Member id: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Book id: ");
        int bookId = Integer.parseInt(scanner.nextLine().trim());
        boolean ok = service.returnBook(memberId, bookId);
        System.out.println(ok ? "Return successful." : "Cannot return (check member/book).\n");
    }
}