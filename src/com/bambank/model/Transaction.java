package com.bambank.model;

public class Transaction {
    private int fromID;
    private int toID;
    private int value;
    private String description;
    private Integer transactionID;

    public int getFromID() {
        return fromID;
    }

    public void setFromID(int fromID) {
        this.fromID = fromID;
    }

    public int getToID() {
        return toID;
    }

    public void setToID(int toID) {
        this.toID = toID;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Integer getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Integer transactionID) {
        this.transactionID = transactionID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Transaction generateReverse() {
        Transaction reverse = new Transaction();
        reverse.setFromID(toID);
        reverse.setToID(fromID);
        reverse.setValue(value);
        reverse.setDescription("Reversal of " + description);
        return reverse;
    }
}
