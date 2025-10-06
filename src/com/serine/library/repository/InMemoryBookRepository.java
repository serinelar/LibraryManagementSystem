package com.serine.library.repository;

import com.serine.library.model.Book;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryBookRepository implements BookRepository {
    private final Map<Integer, Book> store = new ConcurrentHashMap<>();

    @Override
    public Book save(Book book) {
        store.put(book.getId(), book);
        return book;
    }


    @Override
    public Optional<Book> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }


    @Override
    public List<Book> findAll() { 
        return new ArrayList<>(store.values()); }


    @Override
    public List<Book> findByTitleOrAuthor(String query) {
        String q = query.toLowerCase();
        return store.values().stream()
                .filter(b -> (b.getTitle() != null && b.getTitle().toLowerCase().contains(q)) ||
                             (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(q)))
                .collect(Collectors.toList());
    }


    @Override
    public void delete(int id) { store.remove(id); }
}
