package org.example.model;

import java.time.LocalDateTime;

public class transaction {

    private int mobile_number;
    private String transaction_type;
    private double amount;
    private String details;
    private LocalDateTime transaction_date;
    private LocalDateTime transaction_time;
    private String transaction_status;


    public transaction(String type, double amount, String details) {

        this.transaction_type = type;
        this.amount = amount;
        this.details = details;
        this.transaction_date = LocalDateTime.now();
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getTransaction_date() {
        return transaction_date;
    }
}