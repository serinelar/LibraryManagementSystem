package com.serine.library.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Member {
    private static int counter = 1;
    private int id;
    private String name;
    private int borrowLimit = 3; // default; you can change for tiers
    private List<BorrowRecord> borrowedBooks = new ArrayList<>();
        
    public Member(String name) {
        this.id = counter++;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<BorrowRecord> getBorrowedBooks() { 
        return borrowedBooks; 
    }

    public int getBorrowLimit() { return borrowLimit; }
    public void setBorrowLimit(int borrowLimit) { this.borrowLimit = borrowLimit; }

    public void borrowBook(Book b, int borrowDays) {
        borrowedBooks.add(new BorrowRecord(b, borrowDays));
    }

    public void returnBook(Book b) {
        borrowedBooks.removeIf(record -> record.getBook().equals(b));
    }

    public boolean hasOverdueBooks() {
        return borrowedBooks.stream().anyMatch(BorrowRecord::isOverdue);
    }

    @Override
    public String toString() {
        return String.format("Member{id=%d, name='%s', borrowed=%d}", id, name, borrowedBooks.size());
    }
}