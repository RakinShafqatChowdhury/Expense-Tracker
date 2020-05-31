package com.example.expensetracker.Model;

public class ExpenseData {
    private String id;
    private String product;
    private int amount;
    private String note;
    private int quantity;
    private String date;




    public ExpenseData() {
    }

    public ExpenseData(String id, String product, int amount, String note, int quantity,String date) {
        this.id = id;
        this.product = product;
        this.amount = amount;
        this.note = note;
        this.quantity = quantity;
        this.date = date;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ExpenseData{" +
                "id='" + id + '\'' +
                ", product='" + product + '\'' +
                ", amount=" + amount +
                ", note='" + note + '\'' +
                ", quantity=" + quantity +
                ", date='" + date + '\'' +
                '}';
    }
}
