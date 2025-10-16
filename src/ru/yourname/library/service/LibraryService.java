package ru.yourname.library.service;

import ru.yourname.library.model.Book;
import ru.yourname.library.model.Genre;
import ru.yourname.library.model.Loan;
import ru.yourname.library.model.User;

import java.time.LocalDate;
import java.util.*;

public class LibraryService {
    private static List<Book> books = new ArrayList<Book>();
    private static List<User> users = new ArrayList<User>();
    private static List<Loan> loans = new ArrayList<Loan>();

    public static List<Book> getBooks() {
        return books;
    }
    public static void setBooks(List<Book> books) {
        LibraryService.books = books;
    }

    public static List<User> getUsers() {
        return users;
    }
    public static void setUsers(List<User> users) {
        LibraryService.users = users;
    }

    public static List<Loan> getLoans() {
        return loans;
    }
    public static void setLoans(List<Loan> loans) {
        LibraryService.loans = loans;
    }


    public static Book addBook(String title, String author, int year, Genre genre) {
        boolean found;
        Book retBook;
        // Находим минимальный "пропавший" номер
        for (long i = 1; ; i++) {
            found = false;
            for (Book book : books) {
                if (book.getId() == i) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                retBook = new Book(i, title, author, year, genre, true);
                books.add(retBook);
                break;
            }
        }
        return retBook;
    }


    public static boolean removeBook(long bookId) {
        for (Book book : books) {
            if (book.getId() == bookId) {
                books.remove(book);
                return true;
            }
        }
        return false;
    }


    public static User addUser(String name, String email) {
        boolean found;
        User retUser;
        // Находим минимальный "пропавший" номер
        for (long i = 1; ; i++) {
            found = false;
            for (User user : users) {
                if (user.getId() == i) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                retUser = new User(i, name, email);
                users.add(retUser);
                break;
            }
        }
        return retUser;
    }


    public static Loan loanBook(long bookId, long userId, int days) throws LibraryOperationException {
        Book detectedBook = new Book();
        User detectedUser = new User();

        boolean found = false;
        for (Book book : books) {
            if (book.getId() == bookId) {
                found = true;
                book.setAvailable(false);
                detectedBook = book;
                break;
            }
        }

        if (!found) {
            throw new LibraryOperationException(bookId, LibraryOperationException.ObjectType.BOOK);
        }

        found = false;
        for (User user : users) {
            if (user.getId() == bookId) {
                found = true;
                detectedUser = user;
                break;
            }
        }

        if (!found) {
            throw new LibraryOperationException(bookId, LibraryOperationException.ObjectType.USER);
        }

        Loan retLoan;
        // Находим минимальный "пропавший" номер
        for (long i = 1; ; i++) {
            found = false;
            for (User user : users) {
                if (user.getId() == i) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                retLoan = new Loan(i, bookId, userId, LocalDate.now(), LocalDate.now().plusDays(days), false);
                loans.add(retLoan);
                break;
            }
        }

        detectedBook.setAvailable(false);
        return retLoan;
    }


    public static boolean returnBook(long loanId) {
        boolean found = false;
        for (Loan loan : loans) {
            if (loan.getId() == loanId) {
                loan.setReturned(true);
                for (Book book : books) {
                    if (loan.getBookId() == book.getId()) {
                        book.setAvailable(true);
                        break;
                    }
                }
                found = true;
                break;
            }
        }
        return found;
    }


    public static List<Book> searchBooksByTitle(String q) {
        List<Book> searchQueue = new ArrayList<Book>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(q.toLowerCase())) {
                searchQueue.add(book);
            }
        }
        return searchQueue;
    }


    public static Map<Genre, Integer> countByGenre() {
        Map<Genre, Integer> informationBuffer = new Hashtable<Genre, Integer>();
        int counts = 0;
        for (Book book : books) {
            if(book.getGenre() == Genre.FICTION) {
                counts++;
            }
        }
        informationBuffer.put(Genre.FICTION, counts);

        counts = 0;
        for (Book book : books) {
            if(book.getGenre() == Genre.HISTORY) {
                counts++;
            }
        }
        informationBuffer.put(Genre.HISTORY, counts);

        counts = 0;
        for (Book book : books) {
            if(book.getGenre() == Genre.SCIENCE) {
                counts++;
            }
        }
        informationBuffer.put(Genre.SCIENCE, counts);

        counts = 0;
        for (Book book : books) {
            if(book.getGenre() == Genre.PROGRAMMING) {
                counts++;
            }
        }
        informationBuffer.put(Genre.PROGRAMMING, counts);

        counts = 0;
        for (Book book : books) {
            if(book.getGenre() == Genre.OTHER) {
                counts++;
            }
        }
        informationBuffer.put(Genre.OTHER, counts);

        return informationBuffer;
    }


    public static List<Loan> getOverdueLoans() {
        List<Loan> overDueLoans = new ArrayList<Loan>();
        for (Loan loan : loans) {
            if (loan.getDueDate().isBefore(LocalDate.now())) {
                overDueLoans.add(loan);
            }
        }
        return overDueLoans;
    }
}
