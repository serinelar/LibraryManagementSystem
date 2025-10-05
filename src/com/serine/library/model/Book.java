package com.serine.library.model;

import java.util.LinkedList;
import java.util.Queue;

public class Book {
    private static int counter = 1;
    private int id;
    private String title;
    private String author;
    private int availableCopies; // track copies
    private Queue<Member> reservationQueue = new LinkedList<>();
        
    public Book(String title, String author, int copies) {
        this.id = counter++;
        this.title = title;
        this.author = author;
        this.availableCopies = copies;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }


    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int copies) { this.availableCopies = copies; }

    // Reservations
    public Queue<Member> getReservationQueue() {
        return reservationQueue;
    }

    public void reserveBook(Member m) {
        if (!reservationQueue.contains(m)) {
            reservationQueue.add(m);
        }
    }

    public Member popNextReservation() {
        return reservationQueue.poll();
    }

    @Override
    public String toString() {
        return String.format("Book{id=%d, title='%s', author='%s', available=%d/%d}",
        id, title, author, availableCopies, reservationQueue.size());
        }
    }