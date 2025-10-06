package com.serine.library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class Member {
    private static int counter = 1;
    private int id;
    private String name;
    private MembershipType type;
    private int borrowLimit;
    private final List<BorrowRecord> borrowedBooks = new ArrayList<>();
        
    // Default constructor: REGULAR
    public Member(String name) {
        this.id = counter++;
        this.name = name;
        this.type = MembershipType.REGULAR;
        this.borrowLimit = 3;
    }

    // Constructor with explicit type
    public Member(String name, MembershipType type) {
        this.id = counter++;
        this.name = name;
        this.type = type;
        // reasonable defaults: PREMIUM larger than REGULAR
        this.borrowLimit = (type == MembershipType.PREMIUM) ? 10 : 5;
    }

    public int getId() { return id; }    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public MembershipType getType() { return type; }
    public int getBorrowLimit() { return borrowLimit; }
    public void setBorrowLimit(int borrowLimit) { this.borrowLimit = borrowLimit; }

    public List<BorrowRecord> getBorrowedBooks() { 
        return borrowedBooks; 
    }

    public void borrowBook(Book b, int borrowDays) {
        borrowedBooks.add(new BorrowRecord(b, borrowDays));
    }

    public void returnBook(Book b) {
        borrowedBooks.removeIf(record -> record.getBook().equals(b));
    }

    public boolean hasOverdueBooks() {
        return borrowedBooks.stream().anyMatch(BorrowRecord::isOverdue);
    }

    public String exportBorrowingHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("Borrowing History for ").append(name).append(" (ID: ").append(id).append(")\n");
        for (BorrowRecord record : borrowedBooks) {
            sb.append("Book: ").append(record.getBook().getTitle())
            .append(" | Borrowed on: ").append(record.getBorrowDate())
            .append(" | Due: ").append(record.getDueDate())
            .append(record.isOverdue() ? " (OVERDUE)" : "")
            .append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("Member{id=%d, name='%s', borrowed=%d}", id, name, borrowedBooks.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return id == member.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}