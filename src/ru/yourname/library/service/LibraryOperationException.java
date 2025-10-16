package ru.yourname.library.service;

public class LibraryOperationException extends java.lang.Exception {
    public enum ObjectType {
        USER, BOOK, LOAN;
    }

    public LibraryOperationException(Long id, ObjectType Object) {
            super(GenerateMessege(id, Object));
    }

    private static String GenerateMessege(Long id, ObjectType Object) {
        if (Object.equals(ObjectType.BOOK)) return "ERROR: Book with ID = " + id;
        else if (Object.equals(ObjectType.LOAN)) return "ERROR: Loan with ID = " + id;
        else if (Object.equals(ObjectType.USER)) return "ERROR: User with ID = " + id;
        else return "ERROR: Book with ID = " + id;
    }
}
