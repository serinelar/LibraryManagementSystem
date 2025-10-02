package com.serine.library.repository;

import com.serine.library.model.Book;
import java.util.*;

public class InMemoryBookRepository implements BookRepository {
    private final Map<Integer, Book> store = new HashMap<>();
    private int nextId = 1;


    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            book.setId(nextId++);
        }
        store.put(book.getId(), book);
        return book;
    }


    @Override
    public Optional<Book> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }


    @Override
    public List<Book> findAll() { return new ArrayList<>(store.values()); }


    @Override
    public List<Book> findByTitleOrAuthor(String query) {
        String q = query.toLowerCase();
        List<Book> result = new ArrayList<>();
        for (Book b : store.values()) {
            if (b.getTitle().toLowerCase().contains(q) || b.getAuthor().toLowerCase().contains(q)) {
                result.add(b);
            }
        }
        return result;
    }


    @Override
    public void delete(int id) { store.remove(id); }
}
