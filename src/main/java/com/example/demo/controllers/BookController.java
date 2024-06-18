package com.example.demo.controllers;

import com.example.demo.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.example.demogenerated.api.BooksApi;
import com.example.demogenerated.model.Book;
import org.springframework.http.ResponseEntity;

@RestController
public class BookController implements BooksApi {

    @Autowired
    private BookService bookService;

    @Override
    public ResponseEntity<Book> getBookById(String bookId) {
        Book book = bookService.findBookById(bookId);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
