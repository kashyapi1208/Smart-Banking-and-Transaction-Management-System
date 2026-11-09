package com.bank.service;

import com.bank.model.Account;
import com.bank.model.CurrentAccount;
import com.bank.model.SavingsAccount;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountManager {

    private HashMap<String, Account> accounts = new HashMap<>();
    private int accountCounter = 1001;

    public Account createAccount(String name, String type, double balance) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }

        name = name.trim();

        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select account type");
        }

        if (!Double.isFinite(balance) || balance < 0) {
            throw new IllegalArgumentException(
                "Opening balance must be a valid non-negative number"
            );
        }

        if (!type.equalsIgnoreCase("Savings") &&
            !type.equalsIgnoreCase("Current")) {
            throw new IllegalArgumentException("Invalid account type");
        }

        String accountNumber = String.valueOf(accountCounter++);

        Account account;

        if (type.equalsIgnoreCase("Savings")) {
            account = new SavingsAccount(accountNumber, name, balance);
        } else {
            account = new CurrentAccount(accountNumber, name, balance);
        }

        accounts.put(accountNumber, account);

        return account;
    }

    public Account getAccount(String accountNumber) {

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return null;
        }

        return accounts.get(accountNumber.trim());
    }

    public ArrayList<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public void loadAccount(Account account) {

        if (account == null) {
            throw new IllegalArgumentException("Cannot load an empty account");
        }

        String accountNumber = account.getAccountNumber();

        if (accountNumber == null || !accountNumber.matches("\\d+")) {
            throw new IllegalArgumentException(
                "Invalid account number in saved data"
            );
        }

        if (account.getCustomerName() == null ||
            account.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Invalid customer name in saved data"
            );
        }

        if (!Double.isFinite(account.getBalance()) ||
            account.getBalance() < 0) {
            throw new IllegalArgumentException(
                "Invalid balance in saved data"
            );
        }

        accounts.put(accountNumber, account);

        int number = Integer.parseInt(accountNumber);

        if (number >= accountCounter) {
            accountCounter = number + 1;
        }
    }
}