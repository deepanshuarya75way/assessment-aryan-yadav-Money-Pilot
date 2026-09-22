package com.example.Money_tracker.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "expenses")
public class Expense {
    @Id
    private String id;
    private Double amount;
    private String category;
    private String description;
    private LocalDate date;
    private String userEmail;
    private String title;
    private String reason;
    private String status;

    

     public FlaggedItem(String expenseId, String description, BigDecimal amount, String reason) {
        this.expenseId = expenseId;
        this.description = description;
        this.amount = amount;
        this.reason = reason;
        this.status = "PENDING";
    }

    public String getreason(){
        return reason;
    }
    public void setReason(String reason){
        this.reason=reason;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}