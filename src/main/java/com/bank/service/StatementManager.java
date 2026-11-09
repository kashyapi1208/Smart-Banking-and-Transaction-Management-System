
package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class StatementManager {

    public void generateStatement(
            Account account,
            BankService bankService) {

        if (account == null) {
            throw new IllegalArgumentException("Account not found");
        }

        String fileName =
            "Statement_" + account.getAccountNumber() + ".txt";

        try (PrintWriter writer =
                 new PrintWriter(new FileWriter(fileName))) {

            writer.println("==================================");
            writer.println("       SMART BANKING SYSTEM");
            writer.println("          ACCOUNT STATEMENT");
            writer.println("==================================");

            writer.println("Account Number: " +
                account.getAccountNumber());

            writer.println("Customer Name: " +
                account.getCustomerName());

            writer.println("Account Type: " +
                account.getAccountType());

            writer.println("Current Balance: Rs. " +
                account.getBalance());

            writer.println("----------------------------------");
            writer.println("         TRANSACTION HISTORY");
            writer.println("----------------------------------");

            for (Transaction t : bankService.getTransactions()) {

                if (t.getFromAccount().equals(
                        account.getAccountNumber()) ||
                    t.getToAccount().equals(
                        account.getAccountNumber())) {

                    writer.println("Transaction ID: " +
                        t.getTransactionId());

                    writer.println("Type: " + t.getType());

                    writer.println("From: " +
                        t.getFromAccount());

                    writer.println("To: " +
                        t.getToAccount());

                    writer.println("Amount: Rs. " +
                        t.getAmount());

                    writer.println("Date: " + t.getDate());

                    writer.println("----------------------------------");
                }
            }

            writer.println("       END OF STATEMENT");

        } catch (IOException e) {
            throw new RuntimeException(
                "Could not generate statement", e);
        }
    }
}

