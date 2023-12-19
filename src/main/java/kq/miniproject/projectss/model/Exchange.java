package kq.miniproject.projectss.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;

public class Exchange implements Serializable {

    @FutureOrPresent(message = "Date of exchange must be after today")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @Digits(integer = 4, fraction = 2, message = "Must be a number below 10000")
    @Min(value = 0, message = "Must be a positive number")
    private Double budget;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Exchange() {
    }

    public Exchange(Date date, Double budget) {
        this.date = date;
        this.budget = budget;
    }
}
