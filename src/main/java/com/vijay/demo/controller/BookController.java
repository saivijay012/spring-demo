package com.vijay.demo.controller;

import com.vijay.demo.dto.AuthRequest;
import com.vijay.demo.entity.Book;
import com.vijay.demo.repo.BookRepo;
import com.vijay.demo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class BookController {

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/books")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookRepo.findAll();
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/books/{id}")
    @PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
    public ResponseEntity<Object> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepo.findById(id);
        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found for the given id");
    }

    @GetMapping("/findBooks/author")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<List<Book>> findBooksByAuthor(@RequestParam("name") String author) {
        List<Book> books = bookRepo.findAll();
        if (books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Book> matchedBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().equals(author)) {
                matchedBooks.add(book);
            }
        }
        if (matchedBooks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(matchedBooks);
    }


    @PostMapping("/addBook")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        if (book.getAuthor() != null && book.getTitle() != null) {
            bookRepo.save(book);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/updateBook/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Book> updateBookById(@PathVariable Long id, @RequestBody Book book) {
        Optional<Book> existingBook = bookRepo.findById(id);
        if (existingBook.isPresent()) {
            Book updatedBook = existingBook.get();
            updatedBook.setAuthor(book.getAuthor());
            updatedBook.setTitle(book.getTitle());
            bookRepo.save(updatedBook);
            return ResponseEntity.ok(updatedBook);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/deleteBook/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Book> deleteBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepo.findById(id);
        if (book.isPresent()) {
            bookRepo.delete(book.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/loginToken")
    public ResponseEntity<Object> generateToken(@RequestBody AuthRequest authRequest) {
        try {
            // Authenticate the user using AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            // If authentication is successful, set the authentication in the context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Generate JWT token for the authenticated user
            String token = jwtService.generateToken(authRequest.getUsername());
            // Return the token in the response with a 200 OK status
            return ResponseEntity.ok().body("Bearer " + token);
        } catch (BadCredentialsException e) {
            // If authentication fails, return a 401 Unauthorized response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username and Password is incorrect");
        }
    }
}
