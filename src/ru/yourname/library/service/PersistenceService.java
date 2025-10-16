package ru.yourname.library.service;

import ru.yourname.library.model.Book;
import ru.yourname.library.model.Genre;
import ru.yourname.library.model.Loan;
import ru.yourname.library.model.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class PersistenceService {
    public static void saveToFile(String path) throws Exception {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8));
            /*
            BOOKS SECTION
             */
            writer.write("#Books", 0, 6);
            writer.newLine();
            for (Book book : LibraryService.getBooks()) {
                writer.write(book.getId() + ";");
                writer.write(book.getTitle() + ";");
                writer.write(book.getAuthor() + ";");
                writer.write(book.getGenre() + ";");
                writer.write(book.getYear() + ";");
                writer.write(Boolean.toString(book.isAvailable()));
                writer.newLine();
            }
            /*
            USERS SECTION
             */
            writer.write("#Users");
            writer.newLine();
            for (User user : LibraryService.getUsers()) {
                writer.write(user.getId() + ";");
                writer.write(user.getName() + ";");
                writer.write(user.getEmail() + ";");
                writer.newLine();
            }
            /*
            LOAN SECTION
             */
            writer.write("#Loans");
            writer.newLine();
            for (Loan loan : LibraryService.getLoans()) {
                writer.write(loan.getId() + ";");
                writer.write(loan.getBookId() + ";");
                writer.write(loan.getUserId() + ";");
                writer.write(loan.getLoanDate() + ";");
                writer.write(loan.getDueDate() + ";");
                writer.write(loan.isReturned() + ";");
                writer.newLine();
            }
            writer.write("#End");
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public static void loadFromFile(String path) throws Exception {
        List<String> content;
        LibraryService.getBooks().clear();

        try {
            content = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);

            // BOOKS SECTION
            content.removeFirst();
            while (!content.isEmpty()) {
                if (content.getFirst().equals("#Users")) {
                    content.remove(content.getFirst());
                    break;
                }

                String[] record = content.getFirst().split(";");
                content.remove(content.getFirst());

                long id = Long.parseLong(record[0]);
                String title = record[1];
                String author = record[2];
                Genre genre = switch (record[3]) {
                    case "FICTION" -> Genre.FICTION;
                    case "SCIENCE" -> Genre.SCIENCE;
                    case "HISTORY" -> Genre.HISTORY;
                    case "PROGRAMMING" -> Genre.PROGRAMMING;
                    case null, default -> Genre.OTHER;
                };
                int year = Integer.valueOf(record[4]);
                boolean available = Boolean.valueOf(record[5]);

                LibraryService.getBooks().add(new Book(id, title, author, year, genre, available));
            }

            // USERS SECTION
            while (!content.isEmpty()) {
                if (content.getFirst().equals("#Loans")) {
                    content.remove(content.getFirst());
                    break;
                }

                String[] record = content.getFirst().split(";");
                content.remove(content.getFirst());

                long id = Long.valueOf(record[0]);
                String name = record[1];
                String email = record[2];

                LibraryService.getUsers().add(new User(id, name, email));
            }

            // LOANS SECTION
            while (!content.isEmpty()) {
                if (content.getFirst().equals("#End")) {
                    content.remove(content.getFirst());
                    break;
                }

                String[] record = content.getFirst().split(";");
                content.remove(content.getFirst());

                long id = Long.valueOf(record[0]);
                long bookId = Long.valueOf(record[1]);
                long userId = Long.valueOf(record[2]);
                String[] loanDateParams = record[3].split("-");
                int year = Integer.parseInt(loanDateParams[0]);
                int month = Integer.parseInt(loanDateParams[1]);
                int day = Integer.parseInt(loanDateParams[2]);
                LocalDate loanDate = LocalDate.of(year, month, day);
                String[] dueDateParams = record[4].split("-");
                year = Integer.parseInt(dueDateParams[0]);
                month = Integer.parseInt(dueDateParams[1]);
                day = Integer.parseInt(dueDateParams[2]);
                LocalDate dueDate = LocalDate.of(year, month, day);
                boolean returned = Boolean.parseBoolean(record[5]);

                LibraryService.getLoans().add(new Loan(id, bookId, userId, loanDate, dueDate, returned));
            }
        } catch (java.io.FileNotFoundException e) { // NOT FOUND
            throw new java.io.FileNotFoundException();
        } catch (java.lang.ArrayIndexOutOfBoundsException e) { // DAMAGED FILE TO LOAD
            throw new java.lang.ArrayIndexOutOfBoundsException();
        } catch (Exception e) { // ANY OTHER NOT EXPECTED ERROR
            throw new Exception();
        }
    }
}
