package ru.yourname.library.model;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

public class Loan {
    private long id;
    private long bookId;
    private long userId;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private boolean returned;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public Loan() {
        this.id = 0;
        this.bookId = 0;
        this.userId = 0;
        this.loanDate = LocalDate.now();
        this.dueDate = LocalDate.now();
        this.returned = true;
    }

    public Loan(long id, long bookId, long userId, LocalDate loanDate, LocalDate dueDate, boolean returned) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returned = returned;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", returned=" + returned +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return id == loan.id && bookId == loan.bookId && userId == loan.userId && returned == loan.returned && Objects.equals(loanDate, loan.loanDate) && Objects.equals(dueDate, loan.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookId, userId, loanDate, dueDate, returned);
    }
}
