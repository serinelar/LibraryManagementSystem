package com.serine.library.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Objects;

public class Book {
    private static int counter = 1;
    private int id;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies; // track copies
    private Queue<Member> reservationQueue = new LinkedList<>();
        
    public Book(String title, String author, int copies) {
        this.id = counter++;
        this.title = title;
        this.author = author;
        this.totalCopies = copies;
        this.availableCopies = copies;
    }

    // Constructor with genre (default 1 copy)
    public Book(String title, String author, String genre) {
        this.id = counter++;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = 1;
        this.availableCopies = 1;
    }

    public int getId() { return id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getTotalCopies() { return totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int copies) { this.availableCopies = copies; }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void reserveBook(Member m) {
        if (!reservationQueue.contains(m)) {
            reservationQueue.add(m);
        }
    }

    public Member popNextReservation() {
        return reservationQueue.poll();
    }

    public Queue<Member> getReservationQueue() { return reservationQueue; }

    @Override
    public String toString() {
        return String.format("Book{id=%d, title='%s', author='%s', available=%d/%d}",
        id, title, author, genre, availableCopies, totalCopies);    
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    }