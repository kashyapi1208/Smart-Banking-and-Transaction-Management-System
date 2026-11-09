
package com.bank.model;

public class CurrentAccount extends Account {

    public CurrentAccount(String accountNumber, String customerName,
                          double balance) {

        super(accountNumber, customerName, "Current", balance);
    }
}

