package com.serine.library.model;

import java.util.HashSet;
import java.util.Set;

public class Member {
    private int id;
    private String name;
    private int borrowLimit = 3; // default; you can change for tiers
    private Set<Integer> borrowedBookIds = new HashSet<>();
    
    public Member() {}
    
    public Member(String name) {
        this.name = name;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getBorrowLimit() { return borrowLimit; }
    public void setBorrowLimit(int borrowLimit) { this.borrowLimit = borrowLimit; }


    public Set<Integer> getBorrowedBookIds() { return borrowedBookIds; }


    // helper methods
    public boolean canBorrowMore() { return borrowedBookIds.size() < borrowLimit; }
    public void borrowBook(int bookId) { borrowedBookIds.add(bookId); }
    public void returnBook(int bookId) { borrowedBookIds.remove(bookId); }


    @Override
    public String toString() {
        return String.format("Member{id=%d, name='%s', borrowed=%d}", id, name, borrowedBookIds.size());
    }
}