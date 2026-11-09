package com.bank.model;

public class Transaction {

    private String transactionId;
    private String type;
    private String fromAccount;
    private String toAccount;
    private double amount;
    private String date;

    public Transaction(String transactionId, String type,
                       String fromAccount, String toAccount,
                       double amount, String date) {

        this.transactionId = transactionId;
        this.type = type;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.date = date;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}
