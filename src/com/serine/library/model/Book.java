package com.serine.library.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;
    
    public Book() {}
    
    public Book(String title, String author, int copies) {
        this.title = title;
        this.author = author;
        this.totalCopies = copies;
        this.availableCopies = copies;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }


    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }


    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    @Override
    public String toString() {
        return String.format("Book{id=%d, title='%s', author='%s', available=%d/%d}",
        id, title, author, availableCopies, totalCopies);
        }
    }