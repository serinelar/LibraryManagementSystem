package com.serine.library.repository;

import com.serine.library.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book); // create or update
    Optional<Book> findById(int id);
    List<Book> findAll();
    List<Book> findByTitleOrAuthor(String query);
    void delete(int id);
}