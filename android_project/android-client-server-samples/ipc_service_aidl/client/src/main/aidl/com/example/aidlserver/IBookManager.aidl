package com.example.aidlserver;

import com.example.aidlserver.Book;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    Book getBook(int id);
}
