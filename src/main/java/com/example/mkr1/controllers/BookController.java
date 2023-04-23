package com.example.mkr1.controllers;

import com.example.mkr1.domain.*;
import com.example.mkr1.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookRepo bookRepo;
    private final CategoryRepo categoryRepo;

    @Autowired
    public BookController(BookRepo bookRepo, CategoryRepo categoryRepo) {
        this.bookRepo = bookRepo;
        this.categoryRepo = categoryRepo;
    }

    @GetMapping("/")
    public String showBook(@RequestParam(name = "category_id", required = false) Long categoryId, Map<String, Object> model) {
        List<Book> books;
        List<Category> categories = categoryRepo.findAll();

        if (categoryId == null) {
            books = bookRepo.findAll();
        } else {
            Optional<Category> category = categoryRepo.findById(categoryId);
            if (category.isEmpty()) {
                throw new IllegalArgumentException("Invalid category id: " + categoryId);
            }
            books = category.get().getBooks();
        }

        model.put("books", books);
        model.put("categories", categories);
        model.put("category_id", categoryId);

        return "books";
    }

    @PostMapping("/add")
    public String addBook(@RequestParam String title,
                          @RequestParam String author,
                          @RequestParam(required = false) Double price,
                          @RequestParam Long category_id) {

        if (title.trim().isEmpty() || author.trim().isEmpty()) {
            return "redirect:/books/";
        }

        Category category = categoryRepo.findById(category_id).orElseThrow(() -> new IllegalArgumentException("Invalid category id: " + category_id));

        Book book = new Book(title.trim(), author.trim(), price);
        book.getCategories().add(category);
        bookRepo.save(book);

        return "redirect:/books/";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id) {
        bookRepo.deleteById(id);

        return "redirect:/books/";
    }

    @GetMapping("/edit/{id}")
    public String editBook(@PathVariable("id") Long id, Map<String, Object> model) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book id: " + id));

        List<Category> categories = categoryRepo.findAll();

        model.put("book", book);
        model.put("categories", categories);

        return "book_edit";
    }

    @PostMapping("/save")
    public String saveBook(@RequestParam Long id,
                           @RequestParam String title,
                           @RequestParam String author,
                           @RequestParam(required = false) Double price,
                           @RequestParam Long category_id) {
        Book book = bookRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid book id:" + id));

        Category category = categoryRepo.findById(category_id).orElseThrow(() -> new IllegalArgumentException("Invalid category id:" + category_id));

        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setPrice(price);
        book.setCategory(category);

        bookRepo.save(book);

        return "redirect:/books/";
    }
}