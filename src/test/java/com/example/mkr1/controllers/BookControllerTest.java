package com.example.mkr1.controllers;

import com.example.mkr1.domain.*;
import com.example.mkr1.repos.*;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ui.ModelMap;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookControllerTest {

    @Mock
    private BookRepo bookRepo;

    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testShowBookWhenCategoryIdIsNull() {
        List<Book> books = List.of(new Book("Title", "Author", 10.0));
        List<Category> categories = Collections.singletonList(new Category("Category"));
        when(bookRepo.findAll()).thenReturn(books);
        when(categoryRepo.findAll()).thenReturn(categories);
        ModelMap model = new ModelMap();

        String result = bookController.showBook(null, model);

        assertEquals("books", result);
        assertEquals(books, model.get("books"));
        assertEquals(categories, model.get("categories"));
        assertNull(model.get("category_id"));
        verify(bookRepo).findAll();
        verify(categoryRepo).findAll();
    }

    @Test
    public void testShowBookWhenCategoryIdIsNotNullAndCategoryDoesNotExist() {
        when(categoryRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookController.showBook(1L, new ModelMap()));

        verify(categoryRepo).findById(1L);
    }

    @Test
    public void testAddBookWithValidInput() {
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(new Category("Category")));
        Book book = new Book("Title", "Author", 10.0);
        book.setId(1L);
        when(bookRepo.save(any(Book.class))).thenReturn(book);

        String result = bookController.addBook("Title", "Author", 10.0, 1L);

        assertEquals("redirect:/books/", result);
        verify(categoryRepo).findById(1L);
        verify(bookRepo).save(any(Book.class));
    }

    @Test
    public void testAddBookWithInvalidInput() {
        String emptyTitle = "";
        String emptyAuthor = "";
        Double price = 10.0;
        Long categoryId = 1L;

        assertEquals("redirect:/books/", bookController.addBook(emptyTitle, emptyAuthor, price, categoryId));

        String title = "Book Title";
        String author = "Book Author";
        final Long finalCategoryId = 999L;

        assertThrows(IllegalArgumentException.class, () -> bookController.addBook(title, author, price, finalCategoryId));
    }

    @Test
    public void testDeleteBookWithValidInput(){
        Book book = new Book();
        book.setId(1L);
        when(bookRepo.findById(1L)).thenReturn(Optional.of(book));

        bookController.deleteBook(1L);

        verify(bookRepo, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteBookWithInvalidInput() {
        Long invalidId = 999L;
        doThrow(EmptyResultDataAccessException.class).when(bookRepo).deleteById(invalidId);

        assertThrows(EmptyResultDataAccessException.class, () -> bookController.deleteBook(invalidId));

        verify(bookRepo, times(1)).deleteById(invalidId);
    }


}