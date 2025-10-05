package com.serine.library.model;

import java.time.LocalDate;

public class BorrowRecord {
    private Book book;
    private LocalDate borrowDate;
    private LocalDate dueDate;

    public BorrowRecord(Book book, int borrowDays) {
        this(book, LocalDate.now(), borrowDays);
    }

    public BorrowRecord(Book book, LocalDate borrowDate, int borrowDays) {
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(borrowDays);
    }

    public Book getBook() { return book; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        return String.format("BorrowRecord{book=%s, borrowDate=%s, dueDate=%s, overdue=%s}",
                book.getTitle(), borrowDate, dueDate, isOverdue());
    }
}
