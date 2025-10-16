package ru.yourname.library;

import ru.yourname.library.model.Book;
import ru.yourname.library.model.Genre;
import ru.yourname.library.model.Loan;
import ru.yourname.library.model.User;
import ru.yourname.library.service.LibraryOperationException;
import ru.yourname.library.service.LibraryService;
import ru.yourname.library.service.PersistenceService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    private static boolean running = true;
    private static boolean hasUnsavedChanges = false;

    static void main() {
        // Устанавливаем кодировку для вывода в консоль
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));

        // Загрузка данных при старте
        loadData();

        // Главное меню
        showMainMenu();

        // Сохранение при выходе
        saveDataOnExit();
    }

    // === ОСНОВНЫЕ МЕТОДЫ МЕНЮ ===

    public static void showMainMenu() {
        while (running) {
            clearConsole();
            printMainHeader();
            printMainMenu();

            String input = scanner.nextLine().trim();

            handleMainMenuInput(input);
        }
    }

    private static void printMainHeader() {
        System.out.println("====================================================");
        System.out.println("               БИБЛИОТЕЧНАЯ СИСТЕМА");
        System.out.println("           Управление книгами и займами");
        System.out.println("====================================================");

        if (hasUnsavedChanges) {
            System.out.println("        *** Есть несохраненные изменения! ***");
        }
        System.out.println();
    }

    private static void printMainMenu() {
        System.out.println("----------------------------------------------------");
        System.out.println("                   ГЛАВНОЕ МЕНЮ");
        System.out.println("----------------------------------------------------");
        System.out.println("  1. Добавить книгу");
        System.out.println("  2. Список всех книг");
        System.out.println("  3. Поиск книг");
        System.out.println("  4. Добавить пользователя");
        System.out.println("  5. Выдать книгу");
        System.out.println("  6. Вернуть книгу");
        System.out.println("  7. Сохранить в файл");
        System.out.println("  8. Загрузить из файла");
        System.out.println("  9. Отчёты");
        System.out.println();
        System.out.println("  0. Выход");
        System.out.println("----------------------------------------------------");
        System.out.print("Выберите пункт меню: ");
    }

    private static void handleMainMenuInput(String input) {
        switch (input) {
            case "1":
                addBook();
                break;
            case "2":
                listBooks();
                break;
            case "3":
                searchBooks();
                break;
            case "4":
                addUser();
                break;
            case "5":
                loanBook();
                break;
            case "6":
                returnBook();
                break;
            case "7":
                saveToFile();
                break;
            case "8":
                loadFromFile();
                break;
            case "9":
                showReportsMenu();
                break;
            case "0":
                handleExit();
                break;
            default:
                showError("Неверный пункт меню!");
                waitForEnter();
        }
    }

    // === ОПЕРАЦИИ С КНИГАМИ ===

    private static void addBook() {
        printOperationHeader("ДОБАВЛЕНИЕ НОВОЙ КНИГИ");

        String title = readStringNotEmpty("Введите название книги: ");

        String author = readStringNotEmpty("Введите автора: ");

        int year = readInteger("Введите год выпуска книги: ");

        Genre genre = readGenre();

        LibraryService.addBook(title, author, year, genre);

        showSuccess("Книга '" + title + "' успешно добавлена!");
        hasUnsavedChanges = true;
        waitForEnter();
    }

    private static void listBooks() {
        printOperationHeader("СПИСОК ВСЕХ КНИГ");

        if (LibraryService.getBooks().isEmpty()) {
            System.out.println("В библиотеке нет книг");
        } else {
            System.out.println("┌──────┬────────────────────────────────────┬──────────────────────┬──────┬─────────────────┬─────────────┐");
            System.out.println("│  ID  │ Название                           │ Автор                │ Год  │ Жанр            │ Доступность │");
            System.out.println("├──────┼────────────────────────────────────┼──────────────────────┼──────┼─────────────────┼─────────────┤");

            for (Book book : LibraryService.getBooks()) {
                String availability = book.isAvailable() ? "Доступна" : "Выдана";
                String genreName = book.getGenre() != null ? book.getGenre().name() : "Не указан";

                // Обрезаем длинные названия и авторов
                String title = book.getTitle();
                if (title.length() > 30) {
                    title = title.substring(0, 27) + "...";
                }

                String author = book.getAuthor();
                if (author.length() > 18) {
                    author = author.substring(0, 15) + "...";
                }

                System.out.printf("│ %4d │ %-30s     │ %-20s │ %4d │ %-15s │ %-10s  │%n",
                        book.getId(),
                        title,
                        author,
                        book.getYear(),
                        genreName,
                        availability);
            }

            // Нижняя граница
            System.out.println("└──────┴────────────────────────────────────┴──────────────────────┴──────┴─────────────────┴─────────────┘");
            System.out.println("Всего книг: " + LibraryService.getBooks().size());
        }

        System.out.println("----------------------------------------------------");
        waitForEnter();
    }

    private static void searchBooks() {
        printOperationHeader("ПОИСК КНИГ");

        // Вывод справки
        printSearchHelp();

        System.out.println("Введите критерии поиска (используйте '*' чтобы пропустить поле):");
        System.out.println();

        String title = readStringWithStar("Название книги: ");
        if (title == null) return;

        String author = readStringWithStar("Автор: ");
        if (author == null) return;

        String yearStr = readStringWithStar("Год издания: ");
        if (yearStr == null) return;

        String genreStr = readStringWithStar("Жанр (FICTION, HISTORY, SCIENCE, PROGRAMMING, OTHER): ");
        if (genreStr == null) return;

        String availableStr = readStringWithStar("Доступность (да/нет): ");
        if (availableStr == null) return;

        // Выполняем поиск
        List<Book> results = performSearch(title, author, yearStr, genreStr, availableStr);

        // Выводим результаты
        displaySearchResults(results, title, author, yearStr, genreStr, availableStr);

        waitForEnter();
    }

    // Метод для чтения строки с поддержкой *
    private static String readStringWithStar(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("help")) {
                printSearchHelp();
                continue;
            }

            if (input.equalsIgnoreCase("back")) {
                return null;
            }

            return input;
        }
    }

    // Метод поиска
    private static List<Book> performSearch(String title, String author, String yearStr,
                                            String genreStr, String availableStr) {
        List<Book> allBooks = LibraryService.getBooks();
        List<Book> results = new ArrayList<>();

        for (Book book : allBooks) {
            boolean matches = title.equals("*") || book.getTitle().toLowerCase().contains(title.toLowerCase());

            // Проверка названия

            // Проверка автора
            if (matches && !author.equals("*") && !book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                matches = false;
            }

            // Проверка года
            if (matches && !yearStr.equals("*")) {
                try {
                    int searchYear = Integer.parseInt(yearStr);
                    if (book.getYear() != searchYear) {
                        matches = false;
                    }
                } catch (NumberFormatException e) {
                    // Если год не число, но не "*" - не совпадает
                    matches = false;
                }
            }

            // Проверка жанра
            if (matches && !genreStr.equals("*")) {
                try {
                    Genre searchGenre = Genre.valueOf(genreStr.toUpperCase());
                    if (book.getGenre() != searchGenre) {
                        matches = false;
                    }
                } catch (IllegalArgumentException e) {
                    // Если жанр не распознан, но не "*" - не совпадает
                    matches = false;
                }
            }

            // Проверка доступности
            if (matches && !availableStr.equals("*")) {
                boolean searchAvailable = availableStr.equalsIgnoreCase("да") ||
                        availableStr.equalsIgnoreCase("yes") ||
                        availableStr.equalsIgnoreCase("д") ||
                        availableStr.equalsIgnoreCase("y");
                if (book.isAvailable() != searchAvailable) {
                    matches = false;
                }
            }

            if (matches) {
                results.add(book);
            }
        }

        return results;
    }

    // Метод для вывода результатов поиска
    private static void displaySearchResults(List<Book> results, String title, String author,
                                             String yearStr, String genreStr, String availableStr) {
        System.out.println();
        System.out.println("====================================================");
        System.out.println("                  РЕЗУЛЬТАТЫ ПОИСКА");
        System.out.println("====================================================");

        // Показываем использованные критерии
        System.out.println("Критерии поиска:");
        System.out.printf("  Название: %s%n", title.equals("*") ? "любое" : "'" + title + "'");
        System.out.printf("  Автор: %s%n", author.equals("*") ? "любой" : "'" + author + "'");
        System.out.printf("  Год: %s%n", yearStr.equals("*") ? "любой" : yearStr);
        System.out.printf("  Жанр: %s%n", genreStr.equals("*") ? "любой" : genreStr);
        System.out.printf("  Доступность: %s%n", availableStr.equals("*") ? "любая" : availableStr);
        System.out.println();

        if (results.isEmpty()) {
            System.out.println("Книги по заданным критериям не найдены.");
        } else {
            System.out.println("Найдено книг: " + results.size());
            System.out.println();

            // Используем наш красивый вывод таблицы
            System.out.println("┌──────┬────────────────────────────────────┬──────────────────────┬──────┬─────────────────┬─────────────┐");
            System.out.println("│  ID  │ Название                           │ Автор                │ Год  │ Жанр            │ Доступность │");
            System.out.println("├──────┼────────────────────────────────────┼──────────────────────┼──────┼─────────────────┼─────────────┤");

            for (Book book : results) {
                String availability = book.isAvailable() ? "Доступна" : "Выдана";
                String genreName = book.getGenre() != null ? book.getGenre().name() : "Не указан";

                // Обрезаем длинные названия и авторов
                String bookTitle = book.getTitle();
                if (bookTitle.length() > 30) {
                    bookTitle = bookTitle.substring(0, 27) + "...";
                }

                String bookAuthor = book.getAuthor();
                if (bookAuthor.length() > 18) {
                    bookAuthor = bookAuthor.substring(0, 15) + "...";
                }

                System.out.printf("│ %4d │ %-30s │ %-20s │ %4d │ %-15s │ %-10s │%n",
                        book.getId(),
                        bookTitle,
                        bookAuthor,
                        book.getYear(),
                        genreName,
                        availability);
            }

            System.out.println("└──────┴────────────────────────────────────┴──────────────────────┴──────┴─────────────────┴─────────────┘");
        }
    }

    // Метод для вывода справки
    private static void printSearchHelp() {
        System.out.println("┌────────────────────────────────────────────────────────┐");
        System.out.println("│                      СПРАВКА ПО ПОИСКУ                 │");
        System.out.println("├────────────────────────────────────────────────────────┤");
        System.out.println("│ Как использовать поиск:                               │");
        System.out.println("│                                                       │");
        System.out.println("│ • Введите '*' чтобы пропустить поле                   │");
        System.out.println("│ • Поиск по названию и автору нечувствителен к регистру│");
        System.out.println("│ • Для жанра используйте английские названия:          │");
        System.out.println("│   FICTION, HISTORY, SCIENCE, PROGRAMMING, OTHER       │");
        System.out.println("│ • Для доступности используйте: да/нет                 │");
        System.out.println("│                                                       │");
        System.out.println("│ Примеры:                                              │");
        System.out.println("│   Название: война*     - найдет 'Война и мир'         │");
        System.out.println("│   Автор: *             - любой автор                  │");
        System.out.println("│   Год: 2020            - только книги 2020 года       │");
        System.out.println("│   Жанр: FICTION        - только художественная литература│");
        System.out.println("│   Доступность: да      - только доступные книги       │");
        System.out.println("│                                                       │");
        System.out.println("│ Команды:                                              │");
        System.out.println("│   help - показать эту справку                         │");
        System.out.println("│   back - вернуться в меню                             │");
        System.out.println("└────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    // === ОПЕРАЦИИ С ПОЛЬЗОВАТЕЛЯМИ ===

    private static void addUser() {
        printOperationHeader("ДОБАВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ");

        String name = readStringNotEmpty("Введите имя пользователя: ");

        String email = readStringNotEmpty("Введите email пользователя: ");

        LibraryService.addUser(name, email);

        showSuccess("Пользователь '" + name + "' успешно добавлен!");
        hasUnsavedChanges = true;
        waitForEnter();
    }

    // === ОПЕРАЦИИ С ЗАЙМАМИ ===

    private static void loanBook() {
        printOperationHeader("ВЫДАЧА КНИГИ");

        Long bookId = readLong("Введите ID книги: ");

        Long userId = readLong("Введите ID пользователя: ");

        Integer days = readInteger("Введите количество дней займа: ");

        // ЗДЕСЬ ТВОЯ РЕАЛИЗАЦИЯ
        try {
            LibraryService.loanBook(bookId, userId, days);
        } catch (LibraryOperationException e) {
            waitForEnter();
            return;
        }

        showSuccess("Книга успешно выдана пользователю!");
        hasUnsavedChanges = true;
        waitForEnter();
    }

    private static void returnBook() {
        printOperationHeader("ВОЗВРАТ КНИГИ");

        Long loanId = readLong("Введите ID займа: ");

        // ЗДЕСЬ ТВОЯ РЕАЛИЗАЦИЯ
        if (!LibraryService.returnBook(loanId)) {
            showError("Ошибка поиска записи #" + loanId);
            return;
        }

        showSuccess("Книга успешно возвращена!");
        hasUnsavedChanges = true;
        waitForEnter();
    }

    // === ОТЧЕТЫ ===

    private static void showReportsMenu() {
        while (true) {
            clearConsole();
            printOperationHeader("ОТЧЁТЫ");

            System.out.println("----------------------------------------------------");
            System.out.println("               ДОСТУПНЫЕ ОТЧЁТЫ");
            System.out.println("----------------------------------------------------");
            System.out.println("  1. Книги по жанрам");
            System.out.println("  2. Просроченные книги");
            System.out.println();
            System.out.println("  0. Назад в главное меню");
            System.out.println("----------------------------------------------------");
            System.out.print("Выберите отчёт: ");

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return;
            }

            switch (input) {
                case "1":
                    showBooksByGenreReport();
                    break;
                case "2":
                    showOverdueBooksReport();
                    break;
                default:
                    showError("Неверный выбор отчёта!");
                    waitForEnter();
            }
        }
    }

    private static void showBooksByGenreReport() {
        printOperationHeader("КНИГИ ПО ЖАНРАМ");

        Map<Genre, Integer> genreStats = LibraryService.countByGenre();

        if (genreStats.isEmpty()) {
            System.out.println("В библиотеке нет книг для анализа по жанрам");
        } else {
            System.out.println("┌─────────────────┬────────────┬──────────────┬────────────────┐");
            System.out.println("│ Жанр            │ Количество │ Доступно     │ Выдано         │");
            System.out.println("├─────────────────┼────────────┼──────────────┼────────────────┤");

            int totalBooks = 0;
            int totalAvailable = 0;
            int totalLoaned = 0;

            for (Map.Entry<Genre, Integer> entry : genreStats.entrySet()) {
                Genre genre = entry.getKey();
                int count = entry.getValue();

                // Подсчитываем доступные и выданные книги для этого жанра
                int availableCount = 0;
                int loanedCount = 0;

                for (Book book : LibraryService.getBooks()) {
                    if (book.getGenre() == genre) {
                        if (book.isAvailable()) {
                            availableCount++;
                        } else {
                            loanedCount++;
                        }
                    }
                }

                String genreName = getRussianGenreName(genre);

                System.out.printf("│ %-15s │ %-10d │ %-12d │ %-14d │%n",
                        genreName,
                        count,
                        availableCount,
                        loanedCount);

                totalBooks += count;
                totalAvailable += availableCount;
                totalLoaned += loanedCount;
            }

            System.out.println("├─────────────────┼────────────┼──────────────┼────────────────┤");
            System.out.printf("│ %-15s │ %-10d │ %-12d │ %-14d │%n",
                    "ВСЕГО",
                    totalBooks,
                    totalAvailable,
                    totalLoaned);
            System.out.println("└─────────────────┴────────────┴──────────────┴────────────────┘");

            // Дополнительная статистика
            if (totalBooks > 0) {
                double availablePercent = (double) totalAvailable / totalBooks * 100;
                double loanedPercent = (double) totalLoaned / totalBooks * 100;
                System.out.printf("\nСтатистика: Доступно %.1f%%, Выдано %.1f%%%n",
                        availablePercent, loanedPercent);
            }
        }

        System.out.println("----------------------------------------------------");
        waitForEnter();
    }

    // Вспомогательный метод для русских названий жанров
    private static String getRussianGenreName(Genre genre) {
        return switch (genre) {
            case FICTION -> "Художественная";
            case HISTORY -> "История";
            case SCIENCE -> "Наука";
            case PROGRAMMING -> "Программирование";
            case OTHER -> "Другое";
        };
    }

    private static void showOverdueBooksReport() {
        printOperationHeader("ПРОСРОЧЕННЫЕ КНИГИ");

        List<Loan> overdueLoans = LibraryService.getOverdueLoans();

        if (overdueLoans.isEmpty()) {
            System.out.println("В библиотеке нет просроченных займов");
        } else {
            System.out.println("┌────────┬──────────┬──────────┬────────────┬────────────┬────────────┬──────────────┐");
            System.out.println("│ ID     │ ID книги │ ID читат.│ Дата выдачи│ Срок сдачи │ Просрочено │ Статус       │");
            System.out.println("├────────┼──────────┼──────────┼────────────┼────────────┼────────────┼──────────────┤");

            for (Loan loan : overdueLoans) {
                // Находим информацию о книге и пользователе
                String bookTitle = "Не найдена";
                String userName = "Не найден";

                for (Book book : LibraryService.getBooks()) {
                    if (book.getId() == loan.getBookId()) {
                        bookTitle = book.getTitle();
                        if (bookTitle.length() > 25) {
                            bookTitle = bookTitle.substring(0, 22) + "...";
                        }
                        break;
                    }
                }

                for (User user : LibraryService.getUsers()) {
                    if (user.getId() == loan.getUserId()) {
                        userName = user.getName();
                        if (userName.length() > 20) {
                            userName = userName.substring(0, 17) + "...";
                        }
                        break;
                    }
                }

                // Вычисляем дни просрочки
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), java.time.LocalDate.now());

                System.out.printf("│ %-6d │ %-8d │ %-8d │ %-10s │ %-10s │ %-10d │ %-12s │%n",
                        loan.getId(),
                        loan.getBookId(),
                        loan.getUserId(),
                        loan.getLoanDate(),
                        loan.getDueDate(),
                        daysOverdue,
                        loan.isReturned() ? "Возвращена" : "Просрочена");
            }

            System.out.println("└────────┴──────────┴──────────┴────────────┴────────────┴────────────┴──────────────┘");
            System.out.println("Всего просроченных займов: " + overdueLoans.size());
        }

        System.out.println("----------------------------------------------------");
        waitForEnter();
    }

    // === РАБОТА С ФАЙЛАМИ ===

    private static void saveToFile() {
        printOperationHeader("СОХРАНЕНИЕ ДАННЫХ");

        try {
            // ЗДЕСЬ ТВОЯ РЕАЛИЗАЦИЯ СОХРАНЕНИЯ
            PersistenceService.saveToFile("library_data.txt");

            showSuccess("Данные успешно сохранены!");
            hasUnsavedChanges = false;
        } catch (Exception e) {
            showError("Ошибка при сохранении: " + e.getMessage());
        }

        waitForEnter();
    }

    private static void loadFromFile() {
        printOperationHeader("ЗАГРУЗКА ДАННЫХ");

        if (hasUnsavedChanges) {
            if (!confirmAction("Есть несохраненные изменения. Продолжить?")) {
                return;
            }
        }

        try {
            // ЗДЕСЬ ТВОЯ РЕАЛИЗАЦИЯ ЗАГРУЗКИ
            PersistenceService.loadFromFile("library_data.txt");

            showSuccess("Данные успешно загружены!");
            hasUnsavedChanges = false;
        } catch (Exception e) {
            showError("Ошибка при загрузке: " + e.getMessage());
        }

        waitForEnter();
    }

    private static void loadData() {
        try {
            // Автозагрузка при старте
             PersistenceService.loadFromFile("library_data.txt");
        } catch (Exception e) {
            System.out.println("Не удалось загрузить данные: " + e.getMessage());
        }
    }

    private static void saveDataOnExit() {
        if (hasUnsavedChanges) {
            System.out.println("\n*** Есть несохраненные изменения! ***");
            if (confirmAction("Сохранить данные перед выходом?")) {
                saveToFile();
            }
        }
        System.out.println("\nДо свидания! Программа завершена.");
    }

    // === УТИЛИТЫ ДЛЯ РАБОТЫ С ВВОДОМ ===

    private static String readStringNotEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                showError("Поле не может быть пустым!");
                continue;
            }

            return input;
        }
    }

    private static Long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                showError("Поле не может быть пустым!");
                continue;
            }

            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                showError("Ошибка: Введите корректное число!");
            }
        }
    }

    private static Integer readInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                showError("Поле не может быть пустым!");
                continue;
            }

            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException e) {
                showError("Ошибка: Введите корректное число!");
            }
        }
    }

    private static Genre readGenre() {
        while (true) {

            System.out.print("Доступные жанры:\n1. Фикшн\n2. История\n3. Наука\n4. Программирование\n5. Другое\nВыберите жанр: ");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    return Genre.FICTION;
                case "2":
                    return Genre.HISTORY;
                case "3":
                    return Genre.SCIENCE;
                case "4":
                    return Genre.PROGRAMMING;
                case "5":
                    return Genre.OTHER;
                default:
                    showError("Неверный ввод пункта меню!");
            }
        }
    }

    private static void waitForEnter() {
        System.out.print("\nНажмите Enter для продолжения...");
        scanner.nextLine();
    }

    private static boolean confirmAction(String message) {
        System.out.print("\n" + message + " (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes") || input.equals("д") || input.equals("да");
    }

    // === УТИЛИТЫ ДЛЯ ОФОРМЛЕНИЯ ===

    private static void printOperationHeader(String title) {
        clearConsole();
        System.out.println("====================================================");
        System.out.println("                  " + title);
        System.out.println("====================================================");
        System.out.println();
    }

    private static void showSuccess(String message) {
        System.out.println("\n[УСПЕХ] " + message);
    }

    private static void showError(String message) {
        System.out.println("\n[ОШИБКА] " + message);
    }

    private static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Если очистка не удалась, просто выводим пустые строки
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    private static void handleExit() {
        System.out.println("\n--- Подтверждение выхода ---");
        if (confirmAction("Вы уверены, что хотите выйти?")) {
            running = false;
        }
    }
}