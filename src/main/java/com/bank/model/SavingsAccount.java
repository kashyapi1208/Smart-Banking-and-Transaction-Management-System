
package com.bank.model;

public class SavingsAccount extends Account {

    public SavingsAccount(String accountNumber, String customerName,
                          double balance) {

        super(accountNumber, customerName, "Savings", balance);
    }
}
