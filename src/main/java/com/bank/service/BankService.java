package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankService {

    private AccountManager accountManager;

    private ArrayList<Transaction> transactions = new ArrayList<>();

    private int transactionCounter = 101;

    public BankService(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    // Check that an account number is entered and the account exists.
    private Account findAccount(String accountNumber) {

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Please enter an account number"
            );
        }

        Account account = accountManager.getAccount(accountNumber.trim());

        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        return account;
    }

    // Check that the amount is a valid positive number.
    private void validateAmount(double amount) {

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                "Amount must be a valid number greater than zero"
            );
        }
    }

    // Generate transaction ID
    private String generateTransactionId() {
        return "T" + transactionCounter++;
    }

    // Get current date and time
    private String getDate() {
        return LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        );
    }

    // Deposit money
    public void deposit(String accountNumber, double amount) {

        Account account = findAccount(accountNumber);
        validateAmount(amount);

        double newBalance = account.getBalance() + amount;

        if (!Double.isFinite(newBalance)) {
            throw new IllegalArgumentException(
                "Amount is too large to deposit"
            );
        }

        account.setBalance(newBalance);

        transactions.add(new Transaction(
            generateTransactionId(),
            "DEPOSIT",
            accountNumber.trim(),
            "-",
            amount,
            getDate()
        ));
    }

    // Withdraw money
    public void withdraw(String accountNumber, double amount) {

        Account account = findAccount(accountNumber);
        validateAmount(amount);

        if (amount > account.getBalance()) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - amount);

        transactions.add(new Transaction(
            generateTransactionId(),
            "WITHDRAW",
            accountNumber.trim(),
            "-",
            amount,
            getDate()
        ));
    }

    // Transfer money
    public void transfer(String fromAccount, String toAccount, double amount) {

        Account sender = findAccount(fromAccount);
        Account receiver = findAccount(toAccount);

        String senderNumber = fromAccount.trim();
        String receiverNumber = toAccount.trim();

        if (senderNumber.equals(receiverNumber)) {
            throw new IllegalArgumentException(
                "Cannot transfer to the same account"
            );
        }

        validateAmount(amount);

        if (amount > sender.getBalance()) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        double receiverNewBalance = receiver.getBalance() + amount;

        if (!Double.isFinite(receiverNewBalance)) {
            throw new IllegalArgumentException(
                "Transfer amount is too large for the receiver's balance"
            );
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiverNewBalance);

        transactions.add(new Transaction(
            generateTransactionId(),
            "TRANSFER",
            senderNumber,
            receiverNumber,
            amount,
            getDate()
        ));
    }

    // Get transaction history
    public ArrayList<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    // Restore a saved transaction
    public void loadTransaction(Transaction transaction) {

        if (transaction == null ||
            transaction.getTransactionId() == null ||
            !transaction.getTransactionId().matches("T\\d+")) {
            throw new IllegalArgumentException(
                "Invalid transaction in saved data"
            );
        }

        transactions.add(transaction);

        int number = Integer.parseInt(
            transaction.getTransactionId().substring(1)
        );

        if (number >= transactionCounter) {
            transactionCounter = number + 1;
        }
    }
}