
package com.bank.storage;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.service.AccountManager;
import com.bank.service.BankService;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class FileManager {

    private static final String DATA_FOLDER = "data";
    private static final String ACCOUNT_FILE = DATA_FOLDER + "/accounts.csv";
    private static final String TRANSACTION_FILE = DATA_FOLDER + "/transactions.csv";

    private static final String ENCRYPTED_PREFIX = "ENC:";
    private static final int NONCE_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public FileManager() {
        try {
            Files.createDirectories(Paths.get(DATA_FOLDER));
        } catch (IOException e) {
            throw new RuntimeException("Could not create data folder", e);
        }
    }

    // Get AES encryption key from Windows environment variable
    private SecretKeySpec getEncryptionKey() {

        String keyText = System.getenv("BANK_ENCRYPTION_KEY");

        if (keyText == null || keyText.trim().isEmpty()) {
            throw new IllegalStateException(
                "Encryption key not found! Please set BANK_ENCRYPTION_KEY in Windows."
            );
        }

        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyText);

            if (keyBytes.length != 32) {
                throw new IllegalStateException(
                    "Invalid encryption key. It must contain 32 bytes."
                );
            }

            return new SecretKeySpec(keyBytes, "AES");

        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                "Invalid encryption key format.",
                e
            );
        }
    }

    // Encrypt sensitive data using AES-256-GCM
    private String encrypt(String value) {

        if (value == null) {
            return "NULL";
        }

        if (value.isEmpty()) {
            return "";
        }

        try {
            byte[] nonce = new byte[NONCE_LENGTH];
            SECURE_RANDOM.nextBytes(nonce);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(
                Cipher.ENCRYPT_MODE,
                getEncryptionKey(),
                new GCMParameterSpec(TAG_LENGTH, nonce)
            );

            byte[] encryptedBytes = cipher.doFinal(
                value.getBytes(StandardCharsets.UTF_8)
            );

            // Combine nonce and encrypted data
            byte[] combined = new byte[nonce.length + encryptedBytes.length];

            System.arraycopy(nonce, 0, combined, 0, nonce.length);

            System.arraycopy(
                encryptedBytes, 0,
                combined, nonce.length,
                encryptedBytes.length
            );

            return ENCRYPTED_PREFIX +
                Base64.getEncoder().encodeToString(combined);

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(
                "Could not encrypt sensitive data.",
                e
            );
        }
    }

    // Decrypt encrypted data
    private String decrypt(String value) {

        if (value == null || value.isEmpty()) {
            return value;
        }

        if (value.equals("NULL")) {
            return null;
        }

        // Support old records that were not encrypted
        if (!value.startsWith(ENCRYPTED_PREFIX)) {
            return value;
        }

        try {
            String encryptedText =
                value.substring(ENCRYPTED_PREFIX.length());

            byte[] combined =
                Base64.getDecoder().decode(encryptedText);

            if (combined.length <= NONCE_LENGTH) {
                throw new IllegalArgumentException(
                    "Invalid encrypted data."
                );
            }

            byte[] nonce = Arrays.copyOfRange(
                combined, 0, NONCE_LENGTH
            );

            byte[] encryptedBytes = Arrays.copyOfRange(
                combined, NONCE_LENGTH, combined.length
            );

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(
                Cipher.DECRYPT_MODE,
                getEncryptionKey(),
                new GCMParameterSpec(TAG_LENGTH, nonce)
            );

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(
                decryptedBytes,
                StandardCharsets.UTF_8
            );

        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new RuntimeException(
                "Could not decrypt data. Check the encryption key.",
                e
            );
        }
    }

    // Save accounts with encrypted account numbers
    public void saveAccounts(AccountManager manager) {

        try (PrintWriter writer = new PrintWriter(
            Files.newBufferedWriter(
                Paths.get(ACCOUNT_FILE),
                StandardCharsets.UTF_8
            )
        )) {

            for (Account account : manager.getAllAccounts()) {

                writer.println(
                    encrypt(account.getAccountNumber()) + "," +
                    account.getCustomerName() + "," +
                    account.getAccountType() + "," +
                    account.getBalance()
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(
                "Could not save accounts.",
                e
            );
        }
    }

    // Load accounts and decrypt account numbers
    public void loadAccounts(AccountManager manager) {

        File file = new File(ACCOUNT_FILE);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(
            file.toPath(),
            StandardCharsets.UTF_8
        )) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data.length == 4) {

                    String accountNumber = decrypt(data[0]);

                    Account account = new Account(
                        accountNumber,
                        data[1],
                        data[2],
                        Double.parseDouble(data[3])
                    );

                    manager.loadAccount(account);
                }
            }

        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(
                "Could not load accounts.",
                e
            );
        }
    }

    // Save transactions with encrypted account references
    public void saveTransactions(BankService bankService) {

        try (PrintWriter writer = new PrintWriter(
            Files.newBufferedWriter(
                Paths.get(TRANSACTION_FILE),
                StandardCharsets.UTF_8
            )
        )) {

            for (Transaction t : bankService.getTransactions()) {

                writer.println(
                    t.getTransactionId() + "," +
                    t.getType() + "," +
                    encrypt(t.getFromAccount()) + "," +
                    encrypt(t.getToAccount()) + "," +
                    t.getAmount() + "," +
                    t.getDate()
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(
                "Could not save transactions.",
                e
            );
        }
    }

    // Load transactions and decrypt account references
    public void loadTransactions(BankService bankService) {

        File file = new File(TRANSACTION_FILE);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(
            file.toPath(),
            StandardCharsets.UTF_8
        )) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data.length == 6) {

                    Transaction transaction = new Transaction(
                        data[0],
                        data[1],
                        decrypt(data[2]),
                        decrypt(data[3]),
                        Double.parseDouble(data[4]),
                        data[5]
                    );

                    bankService.loadTransaction(transaction);
                }
            }

        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(
                "Could not load transactions.",
                e
            );
        }
    }
}
