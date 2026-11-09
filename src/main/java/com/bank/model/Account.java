package com.bank.model;

public class Account {

    private String accountNumber;
    private String customerName;
    private String accountType;
    private double balance;

    public Account(String accountNumber, String customerName,
                   String accountType, double balance) {

        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}