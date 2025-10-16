package ru.yourname.library.model;

import java.util.Objects;

public class Book {
    private long id;
    private String title;
    private String author;
    private int year;
    private Genre genre;
    private boolean available;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Book() {
        this.id = 0;
        this.title = "";
        this.author = "";
        this.year = 0;
        this.genre = Genre.OTHER;
        this.available = false;
    } // overload conception

    public Book(long id, String title, String author, int year, Genre genre, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.available = available;
    } // overload conception

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", genre=" + genre +
                ", available=" + available +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id && year == book.year && available == book.available && Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(genre, book.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, year, genre, available);
    }
}
