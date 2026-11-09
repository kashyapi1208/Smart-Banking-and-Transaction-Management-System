
package com.bank;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.service.AccountManager;
import com.bank.service.BankService;
import com.bank.service.StatementManager;
import com.bank.storage.FileManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {

    private AccountManager accountManager;
    private BankService bankService;
    private FileManager fileManager;
    private StatementManager statementManager;

    private TableView<Account> accountTable;
    private ObservableList<Account> accountData;
    private FilteredList<Account> filteredAccounts;
    private TextField searchField;

    private TableView<Transaction> transactionTable;
    private ObservableList<Transaction> transactionData;

    private Label totalAccountsValue;
    private Label totalBalanceValue;
    private Label totalTransactionsValue;

    private final String navy = "#12345A";
    private final String teal = "#087F8C";
    private final String tealBright = "#009C96";
    private final String blue = "#2876C7";
    private final String background = "#EAF2F5";
    private final String border = "#D4E1E9";

    @Override
    public void start(Stage stage) {

        accountManager = new AccountManager();
        bankService = new BankService(accountManager);
        fileManager = new FileManager();
        statementManager = new StatementManager();

        fileManager.loadAccounts(accountManager);
        fileManager.loadTransactions(bankService);

        showLoginScreen(stage);
    }

    // =====================================================
    // ACCOUNT NUMBER MASKING
    // =====================================================

    // Display only the last four digits of longer account numbers.
    // If the account number has four or fewer digits, hide all digits.

    private String maskAccountNumber(String accountNumber) {

        if (accountNumber == null || accountNumber.isEmpty()) {
            return "";
        }

        int length = accountNumber.length();

        if (length <= 4) {
            return "X".repeat(length);
        }

        String lastFourDigits =
            accountNumber.substring(length - 4);

        return "X".repeat(length - 4) + lastFourDigits;
    }

    // =====================================================
    // DIAGONAL BACKGROUND PATTERN
    // =====================================================

    private Canvas createDiagonalPattern(StackPane parent) {

        Canvas canvas = new Canvas();

        canvas.widthProperty().bind(parent.widthProperty());
        canvas.heightProperty().bind(parent.heightProperty());

        canvas.widthProperty().addListener(
            (obs, oldValue, newValue) -> drawDiagonalLines(canvas)
        );

        canvas.heightProperty().addListener(
            (obs, oldValue, newValue) -> drawDiagonalLines(canvas)
        );

        Platform.runLater(() -> drawDiagonalLines(canvas));

        canvas.setMouseTransparent(true);

        return canvas;
    }

    private void drawDiagonalLines(Canvas canvas) {

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (width <= 0 || height <= 0) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, width, height);
        gc.setStroke(Color.web("#D5E0E7", 0.72));
        gc.setLineWidth(1);

        for (double x = -height; x < width; x += 14) {
            gc.strokeLine(x, 0, x + height, height);
        }
    }

    // =====================================================
    // HEADER CIRCULAR PATTERN
    // =====================================================

    private Canvas createHeaderPattern(StackPane parent) {

        Canvas canvas = new Canvas();

        canvas.widthProperty().bind(parent.widthProperty());
        canvas.heightProperty().bind(parent.heightProperty());

        canvas.widthProperty().addListener(
            (obs, oldValue, newValue) -> drawHeaderCircles(canvas)
        );

        canvas.heightProperty().addListener(
            (obs, oldValue, newValue) -> drawHeaderCircles(canvas)
        );

        Platform.runLater(() -> drawHeaderCircles(canvas));

        canvas.setMouseTransparent(true);

        return canvas;
    }

    private void drawHeaderCircles(Canvas canvas) {

        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (width <= 0 || height <= 0) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, width, height);
        gc.setStroke(Color.web("#FFFFFF", 0.10));
        gc.setLineWidth(1.1);

        for (int i = 0; i < 5; i++) {

            double size = 100 + (i * 55);

            gc.strokeOval(
                120 - size / 2,
                height / 2 - size / 2,
                size,
                size
            );
        }

        for (int i = 0; i < 6; i++) {

            double size = 100 + (i * 55);

            gc.strokeOval(
                width - 125 - size / 2,
                height / 2 - size / 2,
                size,
                size
            );
        }
    }

    // =====================================================
    // LOGIN SCREEN
    // =====================================================

    private void showLoginScreen(Stage stage) {

        Label brand = new Label("SMART BANKING");

        brand.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        Label brandSubtitle = new Label(
            "Smart Banking and Transaction Management System"
        );

        brandSubtitle.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #DCEAF0;"
        );

        VBox brandContent = new VBox(12, brand, brandSubtitle);
        brandContent.setAlignment(Pos.CENTER);
        brandContent.setPadding(new Insets(35));

        StackPane brandHeader = new StackPane();
        brandHeader.setMinHeight(145);

        brandHeader.setStyle(
            "-fx-background-color: linear-gradient(to right, " +
            navy + ", " + teal + ");" +
            "-fx-background-radius: 16 16 0 0;"
        );

        Canvas headerPattern = createHeaderPattern(brandHeader);
        brandHeader.getChildren().addAll(headerPattern, brandContent);

        Label heading = new Label("Welcome Back");

        heading.setStyle(
            "-fx-font-size: 25px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + navy + ";"
        );

        Label subtitle = new Label("Admin / Bank Staff Login");

        subtitle.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #718096;"
        );

        TextField staffIdField = new TextField();
        staffIdField.setPromptText("Enter Staff ID");
        styleInput(staffIdField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        styleInput(passwordField);

        Label messageLabel = new Label();

        messageLabel.setStyle(
            "-fx-text-fill: #D32F2F;" +
            "-fx-font-size: 12px;"
        );

        Button loginButton = new Button("LOGIN");
        styleButton(loginButton);
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setDefaultButton(true);

        loginButton.setOnAction(e -> {

            String staffId = staffIdField.getText().trim();
            String password = passwordField.getText();

            if (staffId.isEmpty() || password.isEmpty()) {

                messageLabel.setText(
                    "Please enter both Staff ID and password."
                );

            } else if (staffId.equals("admin") &&
                       password.equals("Bank@123")) {

                showDashboard(stage);

            } else {

                messageLabel.setText(
                    "Invalid Staff ID or password."
                );

                passwordField.clear();
            }
        });

        VBox loginForm = new VBox(
            16,
            heading,
            subtitle,
            staffIdField,
            passwordField,
            loginButton,
            messageLabel
        );

        loginForm.setAlignment(Pos.CENTER);
        loginForm.setPadding(new Insets(35));

        VBox loginCard = new VBox(0, brandHeader, loginForm);
        loginCard.setMaxWidth(430);

        loginCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + border + ";" +
            "-fx-border-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, #AABBC7, 20, 0.15, 0, 6);"
        );

        StackPane root = new StackPane();
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + background + ";");

        Canvas pattern = createDiagonalPattern(root);
        root.getChildren().addAll(pattern, loginCard);

        Scene scene = new Scene(root, 750, 600);

        stage.setTitle("Smart Banking System - Staff Login");
        stage.setScene(scene);
        stage.setMinWidth(650);
        stage.setMinHeight(500);
        stage.show();
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    private void showDashboard(Stage stage) {

        Label heading = new Label("SMART BANKING");

        heading.setStyle(
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        Label subtitle = new Label("Banking Management Portal");

        subtitle.setStyle(
            "-fx-text-fill: #DCEAF0;" +
            "-fx-font-size: 12px;"
        );

        VBox brand = new VBox(5, heading, subtitle);

        Label staffLabel = new Label("Logged in: Admin");

        staffLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;"
        );

        Button logoutButton = new Button("Logout");

        logoutButton.setStyle(
            "-fx-background-color: #F1F7FA;" +
            "-fx-text-fill: " + navy + ";" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 9 20 9 20;" +
            "-fx-cursor: hand;"
        );

        logoutButton.setOnAction(e -> showLoginScreen(stage));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerContent = new HBox(
            15, brand, spacer, staffLabel, logoutButton
        );

        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setPadding(new Insets(18, 25, 18, 25));

        StackPane header = new StackPane();
        header.setMinHeight(100);
        header.setPrefHeight(100);
        header.setMaxHeight(100);

        header.setStyle(
            "-fx-background-color: linear-gradient(to right, " +
            navy + " 0%, #07536D 55%, " + teal + " 100%);" +
            "-fx-background-radius: 12;"
        );

        Canvas headerPattern = createHeaderPattern(header);
        header.getChildren().addAll(headerPattern, headerContent);

        Label welcome = new Label("Welcome back, Admin!");

        welcome.setStyle(
            "-fx-font-size: 23px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + navy + ";"
        );

        Label description = new Label(
            "Here's an overview of your banking operations."
        );

        description.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #71869A;"
        );

        VBox welcomeBox = new VBox(4, welcome, description);

        int totalAccounts = accountManager.getAllAccounts().size();

        double totalBalance = 0;

        for (Account account : accountManager.getAllAccounts()) {
            totalBalance += account.getBalance();
        }

        int totalTransactions = bankService.getTransactions().size();

        HBox statistics = new HBox(15);

        VBox customersCard = createStatCard(
            "TOTAL ACCOUNTS",
            String.valueOf(totalAccounts),
            "Registered bank accounts",
            "#DDF4F0",
            "#009F91"
        );

        VBox balanceCard = createStatCard(
            "TOTAL BALANCE",
            String.format("Rs. %.2f", totalBalance),
            "Combined customer balances",
            "#DFEAFC",
            "#438CE5"
        );

        VBox transactionsCard = createStatCard(
            "TRANSACTIONS",
            String.valueOf(totalTransactions),
            "Recorded transactions",
            "#EFE3FA",
            "#9560DA"
        );

        HBox.setHgrow(customersCard, Priority.ALWAYS);
        HBox.setHgrow(balanceCard, Priority.ALWAYS);
        HBox.setHgrow(transactionsCard, Priority.ALWAYS);

        statistics.getChildren().addAll(
            customersCard, balanceCard, transactionsCard
        );

        TabPane tabs = new TabPane();

        tabs.getTabs().add(createAccountTab());
        tabs.getTabs().add(createDepositTab());
        tabs.getTabs().add(createWithdrawTab());
        tabs.getTabs().add(createTransferTab());
        tabs.getTabs().add(createHistoryTab());
        tabs.getTabs().add(createStatementTab());

        tabs.setTabClosingPolicy(
            TabPane.TabClosingPolicy.UNAVAILABLE
        );

        styleNavigationTabs(tabs);

        VBox content = new VBox(
            14,
            header,
            welcomeBox,
            statistics,
            tabs
        );

        content.setPadding(new Insets(18));
        content.setStyle("-fx-background-color: transparent;");

        VBox.setVgrow(tabs, Priority.ALWAYS);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + background + ";");

        Canvas backgroundPattern = createDiagonalPattern(root);
        root.getChildren().addAll(backgroundPattern, content);

        refreshDisplays();
        refreshDashboardStatistics();

        Scene scene = new Scene(root, 1150, 780);

        stage.setTitle("Smart Banking System - Dashboard");
        stage.setMinWidth(950);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    // =====================================================
    // NAVIGATION TAB DESIGN
    // =====================================================

    private void styleNavigationTabs(TabPane tabs) {

        tabs.setTabMinHeight(54);
        tabs.setTabMaxHeight(54);
        tabs.setTabMinWidth(115);
        tabs.setTabMaxWidth(150);

        tabs.setStyle("-fx-background-color: transparent;");

        PseudoClass selectedClass =
            PseudoClass.getPseudoClass("selected");

        Runnable updateTabStyles = () -> {

            Node headerBackground =
                tabs.lookup(".tab-header-background");

            if (headerBackground != null) {
                headerBackground.setStyle(
                    "-fx-background-color: transparent;"
                );
            }

            for (Node tabNode : tabs.lookupAll(".tab")) {

                boolean selected =
                    tabNode.getPseudoClassStates()
                        .contains(selectedClass);

                if (selected) {

                    tabNode.setStyle(
                        "-fx-background-color: " + tealBright + ";" +
                        "-fx-background-radius: 8 8 0 0;" +
                        "-fx-border-color: " + tealBright + ";" +
                        "-fx-border-radius: 8 8 0 0;" +
                        "-fx-padding: 13 20 13 20;"
                    );

                } else {

                    tabNode.setStyle(
                        "-fx-background-color: #E8F1FA;" +
                        "-fx-background-radius: 8 8 0 0;" +
                        "-fx-border-color: #CFDFEC;" +
                        "-fx-border-radius: 8 8 0 0;" +
                        "-fx-padding: 13 20 13 20;"
                    );
                }

                Node tabLabel = tabNode.lookup(".tab-label");

                if (tabLabel instanceof Label) {

                    Label label = (Label) tabLabel;

                    if (selected) {
                        label.setStyle(
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;"
                        );
                    } else {
                        label.setStyle(
                            "-fx-text-fill: " + navy + ";" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;"
                        );
                    }
                }
            }
        };

        tabs.getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, oldTab, newTab) ->
                Platform.runLater(updateTabStyles)
            );

        Platform.runLater(() -> {
            tabs.applyCss();
            updateTabStyles.run();
        });
    }

    // =====================================================
    // STATISTICS CARDS
    // =====================================================

    private VBox createStatCard(
        String title,
        String value,
        String description,
        String cardColor,
        String accentColor
    ) {

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + navy + ";"
        );

        Label valueLabel = new Label(value);

        if (title.equals("TOTAL ACCOUNTS")) {
            totalAccountsValue = valueLabel;
        } else if (title.equals("TOTAL BALANCE")) {
            totalBalanceValue = valueLabel;
        } else if (title.equals("TRANSACTIONS")) {
            totalTransactionsValue = valueLabel;
        }

        valueLabel.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + navy + ";"
        );

        valueLabel.setWrapText(true);

        Label descriptionLabel = new Label(description);

        descriptionLabel.setStyle(
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #8499AD;"
        );

        Region topLine = new Region();
        topLine.setPrefHeight(5);
        topLine.setMaxWidth(Double.MAX_VALUE);

        topLine.setStyle(
            "-fx-background-color: " + accentColor + ";" +
            "-fx-background-radius: 5 5 0 0;"
        );

        VBox cardContent = new VBox(
            5,
            titleLabel,
            valueLabel,
            descriptionLabel
        );

        cardContent.setPadding(new Insets(11, 18, 12, 18));

        VBox card = new VBox(0, topLine, cardContent);

        card.setMinHeight(112);
        card.setPrefHeight(120);
        card.setMaxHeight(125);
        card.setMaxWidth(Double.MAX_VALUE);

        card.setStyle(
            "-fx-background-color: " + cardColor + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #D5E2EB;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, #B7C8D5, 10, 0.08, 0, 3);"
        );

        return card;
    }

    // =====================================================
    // ACCOUNT TAB
    // =====================================================

    private Tab createAccountTab() {

        TextField nameField = new TextField();
        nameField.setPromptText("Enter customer name");
        styleInput(nameField);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Savings", "Current");
        typeBox.setPromptText("Select account type");
        typeBox.setMaxWidth(Double.MAX_VALUE);
        typeBox.setPrefHeight(43);

        typeBox.setStyle(
            "-fx-background-color: #F7FAFC;" +
            "-fx-border-color: #D4E1E9;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;"
        );

        TextField balanceField = new TextField();
        balanceField.setPromptText("Enter opening balance");
        styleInput(balanceField);

        Button createButton = new Button("Create Account");
        styleButton(createButton);
        createButton.setMaxWidth(Double.MAX_VALUE);

        createButton.setOnAction(e -> {

            try {

                String name = nameField.getText();
                String type = typeBox.getValue();

                if (balanceField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException(
                        "Please enter opening balance"
                    );
                }

                double balance = Double.parseDouble(
                    balanceField.getText().trim()
                );

                Account account = accountManager.createAccount(
                    name, type, balance
                );

                fileManager.saveAccounts(accountManager);

                showMessage(
                    "Success",
                    "Account created successfully!\n\n" +
                    "Account Number: " +
                    maskAccountNumber(account.getAccountNumber())
                );

                nameField.clear();
                typeBox.setValue(null);
                balanceField.clear();

                refreshDisplays();
                refreshDashboardStatistics();

            } catch (NumberFormatException ex) {

                showMessage(
                    "Invalid Input",
                    "Please enter a valid numeric opening balance."
                );

            } catch (Exception ex) {
                showMessage("Error", ex.getMessage());
            }
        });

        // Search field

        searchField = new TextField();
        searchField.setPromptText(
            "Search by account number or customer name..."
        );
        styleInput(searchField);

        Button clearSearchButton = new Button("Clear");
        styleSecondaryButton(clearSearchButton);

        clearSearchButton.setOnAction(e -> {
            searchField.clear();
            searchField.requestFocus();
        });

        HBox searchRow = new HBox(
            10, searchField, clearSearchButton
        );

        searchRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // Account table

        accountTable = new TableView<>();

        TableColumn<Account, String> accountNumberColumn =
            new TableColumn<>("Account Number");

        accountNumberColumn.setCellValueFactory(
            new PropertyValueFactory<>("accountNumber")
        );

        // MASK ACCOUNT NUMBERS IN CUSTOMER ACCOUNTS TABLE

        accountNumberColumn.setCellFactory(column ->
            new TableCell<Account, String>() {

                @Override
                protected void updateItem(String accountNumber, boolean empty) {

                    super.updateItem(accountNumber, empty);

                    if (empty || accountNumber == null) {
                        setText(null);
                    } else {
                        setText(maskAccountNumber(accountNumber));
                    }
                }
            }
        );

        TableColumn<Account, String> customerNameColumn =
            new TableColumn<>("Customer Name");

        customerNameColumn.setCellValueFactory(
            new PropertyValueFactory<>("customerName")
        );

        TableColumn<Account, String> accountTypeColumn =
            new TableColumn<>("Account Type");

        accountTypeColumn.setCellValueFactory(
            new PropertyValueFactory<>("accountType")
        );

        TableColumn<Account, Number> balanceColumn =
            new TableColumn<>("Balance");

        balanceColumn.setCellValueFactory(
            new PropertyValueFactory<>("balance")
        );

        balanceColumn.setCellFactory(column ->
            new TableCell<Account, Number>() {

                @Override
                protected void updateItem(Number balance, boolean empty) {

                    super.updateItem(balance, empty);

                    if (empty || balance == null) {
                        setText(null);
                    } else {
                        setText(String.format(
                            "Rs. %.2f",
                            balance.doubleValue()
                        ));
                    }
                }
            }
        );

        accountTable.getColumns().addAll(
            accountNumberColumn,
            customerNameColumn,
            accountTypeColumn,
            balanceColumn
        );

        accountTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        accountTable.setPlaceholder(
            new Label("No customer accounts available.")
        );

        accountTable.setPrefHeight(380);
        accountTable.setMinHeight(250);

        styleTable(accountTable);

        accountData = FXCollections.observableArrayList(
            accountManager.getAllAccounts()
        );

        filteredAccounts = new FilteredList<>(
            accountData,
            account -> true
        );

        accountTable.setItems(filteredAccounts);

        // Search still uses the original full account number internally.

        searchField.textProperty().addListener(
            (observable, oldValue, newValue) -> {

                String searchText = newValue.trim().toLowerCase();

                filteredAccounts.setPredicate(account -> {

                    if (searchText.isEmpty()) return true;

                    String accountNumber =
                        account.getAccountNumber().toLowerCase();

                    String customerName =
                        account.getCustomerName().toLowerCase();

                    return accountNumber.contains(searchText) ||
                           customerName.contains(searchText);
                });
            }
        );

        // Two panels side by side.

        VBox form = createCard(
            "Create New Account",
            "Register a new bank customer.",
            nameField,
            typeBox,
            balanceField,
            createButton
        );

        VBox accountList = createCard(
            "Customer Accounts",
            "Search and view all registered accounts.",
            searchRow,
            accountTable
        );

        form.setPrefWidth(440);
        form.setMinWidth(380);
        form.setMaxWidth(480);

        accountList.setPrefWidth(650);
        accountList.setMinWidth(480);
        accountList.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(form, Priority.NEVER);
        HBox.setHgrow(accountList, Priority.ALWAYS);

        VBox.setVgrow(accountTable, Priority.ALWAYS);

        HBox content = new HBox(18, form, accountList);

        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        content.setFillHeight(true);

        ScrollPane scrollPane = new ScrollPane(content);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);

        scrollPane.setHbarPolicy(
            ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
            ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
            "-fx-background: transparent;" +
            "-fx-background-color: transparent;"
        );

        return new Tab("Accounts", scrollPane);
    }

    // =====================================================
    // DEPOSIT TAB
    // =====================================================

    private Tab createDepositTab() {

        TextField accountField = new TextField();
        accountField.setPromptText("Enter account number");
        styleInput(accountField);

        TextField amountField = new TextField();
        amountField.setPromptText("Enter deposit amount");
        styleInput(amountField);

        Button depositButton = new Button("Deposit Money");
        styleButton(depositButton);
        depositButton.setMaxWidth(Double.MAX_VALUE);

        depositButton.setOnAction(e -> {

            try {

                if (amountField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException(
                        "Please enter deposit amount"
                    );
                }

                double amount = Double.parseDouble(
                    amountField.getText().trim()
                );

                bankService.deposit(accountField.getText(), amount);
                saveData();

                showMessage("Success", "Money deposited successfully!");

                accountField.clear();
                amountField.clear();

                refreshDisplays();
                refreshDashboardStatistics();

            } catch (NumberFormatException ex) {

                showMessage(
                    "Invalid Input",
                    "Please enter a valid deposit amount."
                );

            } catch (Exception ex) {
                showMessage("Error", ex.getMessage());
            }
        });

        VBox card = createCard(
            "Deposit Money",
            "Add money to an existing customer account.",
            accountField,
            amountField,
            depositButton
        );

        return createStyledTab("Deposit", card);
    }

    // =====================================================
    // WITHDRAW TAB
    // =====================================================

    private Tab createWithdrawTab() {

        TextField accountField = new TextField();
        accountField.setPromptText("Enter account number");
        styleInput(accountField);

        TextField amountField = new TextField();
        amountField.setPromptText("Enter withdrawal amount");
        styleInput(amountField);

        Button withdrawButton = new Button("Withdraw Money");
        styleButton(withdrawButton);
        withdrawButton.setMaxWidth(Double.MAX_VALUE);

        withdrawButton.setOnAction(e -> {

            try {

                if (amountField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException(
                        "Please enter withdrawal amount"
                    );
                }

                double amount = Double.parseDouble(
                    amountField.getText().trim()
                );

                bankService.withdraw(accountField.getText(), amount);
                saveData();

                showMessage("Success", "Money withdrawn successfully!");

                accountField.clear();
                amountField.clear();

                refreshDisplays();
                refreshDashboardStatistics();

            } catch (NumberFormatException ex) {

                showMessage(
                    "Invalid Input",
                    "Please enter a valid withdrawal amount."
                );

            } catch (Exception ex) {
                showMessage("Error", ex.getMessage());
            }
        });

        VBox card = createCard(
            "Withdraw Money",
            "Withdraw money from a customer account.",
            accountField,
            amountField,
            withdrawButton
        );

        return createStyledTab("Withdraw", card);
    }

    // =====================================================
    // TRANSFER TAB
    // =====================================================

    private Tab createTransferTab() {

        TextField fromField = new TextField();
        fromField.setPromptText("Sender account number");
        styleInput(fromField);

        TextField toField = new TextField();
        toField.setPromptText("Receiver account number");
        styleInput(toField);

        TextField amountField = new TextField();
        amountField.setPromptText("Enter transfer amount");
        styleInput(amountField);

        Button transferButton = new Button("Transfer Money");
        styleButton(transferButton);
        transferButton.setMaxWidth(Double.MAX_VALUE);

        transferButton.setOnAction(e -> {

            try {

                if (amountField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException(
                        "Please enter transfer amount"
                    );
                }

                double amount = Double.parseDouble(
                    amountField.getText().trim()
                );

                bankService.transfer(
                    fromField.getText(),
                    toField.getText(),
                    amount
                );

                saveData();

                showMessage(
                    "Success",
                    "Money transferred successfully!"
                );

                fromField.clear();
                toField.clear();
                amountField.clear();

                refreshDisplays();
                refreshDashboardStatistics();

            } catch (NumberFormatException ex) {

                showMessage(
                    "Invalid Input",
                    "Please enter a valid transfer amount."
                );

            } catch (Exception ex) {
                showMessage("Error", ex.getMessage());
            }
        });

        VBox card = createCard(
            "Transfer Money",
            "Transfer funds between two customer accounts.",
            fromField,
            toField,
            amountField,
            transferButton
        );

        return createStyledTab("Transfer", card);
    }

    // =====================================================
    // TRANSACTION HISTORY TAB
    // =====================================================

    private Tab createHistoryTab() {

        transactionTable = new TableView<>();

        TableColumn<Transaction, String> idColumn =
            new TableColumn<>("Transaction ID");

        idColumn.setCellValueFactory(
            new PropertyValueFactory<>("transactionId")
        );

        TableColumn<Transaction, String> typeColumn =
            new TableColumn<>("Type");

        typeColumn.setCellValueFactory(
            new PropertyValueFactory<>("type")
        );

        TableColumn<Transaction, String> fromColumn =
            new TableColumn<>("From Account");

        fromColumn.setCellValueFactory(
            new PropertyValueFactory<>("fromAccount")
        );

        // MASK SENDER ACCOUNT NUMBER

        fromColumn.setCellFactory(column ->
            new TableCell<Transaction, String>() {

                @Override
                protected void updateItem(String accountNumber, boolean empty) {

                    super.updateItem(accountNumber, empty);

                    if (empty || accountNumber == null) {
                        setText(null);
                    } else {
                        setText(maskAccountNumber(accountNumber));
                    }
                }
            }
        );

        TableColumn<Transaction, String> toColumn =
            new TableColumn<>("To Account");

        toColumn.setCellValueFactory(
            new PropertyValueFactory<>("toAccount")
        );

        // MASK RECEIVER ACCOUNT NUMBER

        toColumn.setCellFactory(column ->
            new TableCell<Transaction, String>() {

                @Override
                protected void updateItem(String accountNumber, boolean empty) {

                    super.updateItem(accountNumber, empty);

                    if (empty || accountNumber == null) {
                        setText(null);
                    } else {
                        setText(maskAccountNumber(accountNumber));
                    }
                }
            }
        );

        TableColumn<Transaction, Number> amountColumn =
            new TableColumn<>("Amount");

        amountColumn.setCellValueFactory(
            new PropertyValueFactory<>("amount")
        );

        amountColumn.setCellFactory(column ->
            new TableCell<Transaction, Number>() {

                @Override
                protected void updateItem(Number amount, boolean empty) {

                    super.updateItem(amount, empty);

                    if (empty || amount == null) {
                        setText(null);
                    } else {
                        setText(String.format(
                            "Rs. %.2f",
                            amount.doubleValue()
                        ));
                    }
                }
            }
        );

        TableColumn<Transaction, Object> dateColumn =
            new TableColumn<>("Date / Time");

        dateColumn.setCellValueFactory(
            new PropertyValueFactory<>("date")
        );

        transactionTable.getColumns().addAll(
            idColumn,
            typeColumn,
            fromColumn,
            toColumn,
            amountColumn,
            dateColumn
        );

        transactionTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );

        transactionTable.setPlaceholder(
            new Label("No transactions available yet.")
        );

        transactionTable.setPrefHeight(450);
        transactionTable.setMinHeight(300);

        styleTable(transactionTable);

        transactionData = FXCollections.observableArrayList();
        transactionTable.setItems(transactionData);

        VBox card = createCard(
            "Transaction History",
            "View all completed deposits, withdrawals and transfers.",
            transactionTable
        );

        VBox.setVgrow(transactionTable, Priority.ALWAYS);

        return createStyledTab("History", card);
    }

    // =====================================================
    // STATEMENT TAB
    // =====================================================

    private Tab createStatementTab() {

        TextField accountField = new TextField();
        accountField.setPromptText("Enter account number");
        styleInput(accountField);

        Button statementButton = new Button("Generate Statement");
        styleButton(statementButton);
        statementButton.setMaxWidth(Double.MAX_VALUE);

        statementButton.setOnAction(e -> {

            try {

                String accountNumber = accountField.getText().trim();

                if (accountNumber.isEmpty()) {
                    throw new IllegalArgumentException(
                        "Please enter an account number"
                    );
                }

                Account account =
                    accountManager.getAccount(accountNumber);

                if (account == null) {
                    throw new IllegalArgumentException(
                        "Account not found"
                    );
                }

                statementManager.generateStatement(
                    account,
                    bankService
                );

                showMessage(
                    "Success",
                    "Statement generated successfully!\n\n" +
                    "Account Number: " +
                    maskAccountNumber(accountNumber) +
                    "\nLocation: Project folder"
                );

                accountField.clear();

            } catch (Exception ex) {
                showMessage("Error", ex.getMessage());
            }
        });

        VBox card = createCard(
            "Generate Account Statement",
            "Create a transaction statement for a customer.",
            accountField,
            statementButton
        );

        return createStyledTab("Statement", card);
    }

    // =====================================================
    // TABLE DESIGN
    // =====================================================

    private void styleTable(TableView<?> table) {

        table.setFixedCellSize(42);

        table.setStyle(
            "-fx-background-color: white;" +
            "-fx-control-inner-background: white;" +
            "-fx-control-inner-background-alt: #F3F8FB;" +
            "-fx-table-cell-border-color: #E2EAF0;" +
            "-fx-selection-bar: #B9DCEB;" +
            "-fx-selection-bar-non-focused: #DCECF4;" +
            "-fx-font-size: 12px;"
        );

        table.setFocusTraversable(false);

        table.skinProperty().addListener(
            (observable, oldSkin, newSkin) -> {
                Platform.runLater(() -> applyTableHeaderStyle(table));
            }
        );

        table.sceneProperty().addListener(
            (observable, oldScene, newScene) -> {
                if (newScene != null) {
                    Platform.runLater(() -> {
                        table.applyCss();
                        applyTableHeaderStyle(table);
                    });
                }
            }
        );

        Platform.runLater(() -> {
            table.applyCss();
            applyTableHeaderStyle(table);
        });
    }

    private void applyTableHeaderStyle(TableView<?> table) {

        for (Node node : table.lookupAll(".column-header")) {

            node.setStyle(
                "-fx-background-color: #D9EAF6;" +
                "-fx-border-color: #C6D9E8;" +
                "-fx-padding: 9 7 9 7;"
            );
        }

        for (Node node : table.lookupAll(".column-header .label")) {

            if (node instanceof Label) {

                ((Label) node).setStyle(
                    "-fx-text-fill: " + navy + ";" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 12px;"
                );
            }
        }

        for (Node node : table.lookupAll(".table-row-cell")) {

            node.setStyle(
                "-fx-border-color: transparent transparent #E5EDF2 transparent;"
            );
        }

        Node placeholder = table.lookup(".placeholder");

        if (placeholder != null) {
            placeholder.setStyle("-fx-background-color: white;");
        }
    }

    // =====================================================
    // DASHBOARD STATISTICS REFRESH
    // =====================================================

    private void refreshDashboardStatistics() {

        int totalAccounts = accountManager.getAllAccounts().size();

        double totalBalance = 0;

        for (Account account : accountManager.getAllAccounts()) {
            totalBalance += account.getBalance();
        }

        int totalTransactions = bankService.getTransactions().size();

        if (totalAccountsValue != null) {
            totalAccountsValue.setText(String.valueOf(totalAccounts));
        }

        if (totalBalanceValue != null) {
            totalBalanceValue.setText(
                String.format("Rs. %.2f", totalBalance)
            );
        }

        if (totalTransactionsValue != null) {
            totalTransactionsValue.setText(
                String.valueOf(totalTransactions)
            );
        }
    }

    // =====================================================
    // CONTENT CARD DESIGN
    // =====================================================

    private VBox createCard(
        String title,
        String subtitle,
        Node... controls
    ) {

        Label heading = new Label(title);

        heading.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );

        Label description = new Label(subtitle);
        description.setWrapText(true);

        description.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: #E0EDF2;"
        );

        VBox headingArea = new VBox(6, heading, description);
        headingArea.setPadding(new Insets(17, 20, 17, 20));

        headingArea.setStyle(
            "-fx-background-color: linear-gradient(to right, " +
            navy + ", " + teal + ");" +
            "-fx-background-radius: 12 12 0 0;"
        );

        VBox controlArea = new VBox(16);
        controlArea.getChildren().addAll(controls);
        controlArea.setPadding(new Insets(20));

        controlArea.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 0 0 12 12;"
        );

        VBox card = new VBox(0, headingArea, controlArea);
        card.setMaxWidth(Double.MAX_VALUE);

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #D5E2EB;" +
            "-fx-border-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, #B8C9D5, 12, 0.08, 0, 4);"
        );

        return card;
    }

    // =====================================================
    // OTHER TAB BACKGROUND
    // =====================================================

    private Tab createStyledTab(String title, VBox card) {

        VBox content = new VBox(card);

        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(25));

        content.setStyle(
            "-fx-background-color: " + background + ";"
        );

        card.setMaxWidth(850);

        return new Tab(title, content);
    }

    // =====================================================
    // INPUT DESIGN
    // =====================================================

    private void styleInput(TextField field) {

        field.setMaxWidth(Double.MAX_VALUE);
        field.setPrefHeight(43);

        field.setStyle(
            "-fx-background-color: #F7FAFC;" +
            "-fx-border-color: #D4E1E9;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 12 10 12;" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + navy + ";" +
            "-fx-prompt-text-fill: #A1B1BE;"
        );

        field.focusedProperty().addListener(
            (observable, oldValue, focused) -> {

                if (focused) {
                    field.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-border-color: " + teal + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 12 10 12;" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + navy + ";" +
                        "-fx-prompt-text-fill: #A1B1BE;"
                    );
                } else {
                    field.setStyle(
                        "-fx-background-color: #F7FAFC;" +
                        "-fx-border-color: #D4E1E9;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 12 10 12;" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + navy + ";" +
                        "-fx-prompt-text-fill: #A1B1BE;"
                    );
                }
            }
        );
    }

    // =====================================================
    // PRIMARY BUTTON DESIGN
    // =====================================================

    private void styleButton(Button button) {

        button.setPrefHeight(43);
        button.setMinHeight(43);
        button.setCursor(javafx.scene.Cursor.HAND);

        button.setStyle(
            "-fx-background-color: linear-gradient(to right, " +
            teal + ", " + blue + ");" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 22 10 22;" +
            "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> {

            button.setStyle(
                "-fx-background-color: linear-gradient(to right, " +
                "#006F78, #175DA9);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 22 10 22;" +
                "-fx-cursor: hand;"
            );
        });

        button.setOnMouseExited(e -> {

            button.setStyle(
                "-fx-background-color: linear-gradient(to right, " +
                teal + ", " + blue + ");" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 22 10 22;" +
                "-fx-cursor: hand;"
            );
        });
    }

    // =====================================================
    // SECONDARY BUTTON DESIGN
    // =====================================================

    private void styleSecondaryButton(Button button) {

        button.setPrefHeight(43);
        button.setMinHeight(43);
        button.setCursor(javafx.scene.Cursor.HAND);

        button.setStyle(
            "-fx-background-color: #E4EFF5;" +
            "-fx-text-fill: " + navy + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10 18 10 18;" +
            "-fx-border-color: #D0E0E9;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: #D3E6EF;" +
                "-fx-text-fill: " + navy + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18 10 18;" +
                "-fx-border-color: #B8D1DF;" +
                "-fx-border-radius: 8;" +
                "-fx-cursor: hand;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: #E4EFF5;" +
                "-fx-text-fill: " + navy + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18 10 18;" +
                "-fx-border-color: #D0E0E9;" +
                "-fx-border-radius: 8;" +
                "-fx-cursor: hand;"
            );
        });
    }

    // =====================================================
    // DATA MANAGEMENT
    // =====================================================

    private void saveData() {

        fileManager.saveAccounts(accountManager);
        fileManager.saveTransactions(bankService);
    }

    private void refreshDisplays() {

        if (accountTable != null && accountData != null) {
            accountData.setAll(accountManager.getAllAccounts());
        }

        if (transactionTable != null && transactionData != null) {
            transactionData.setAll(bankService.getTransactions());
        }
    }

    // =====================================================
    // ALERT MESSAGES
    // =====================================================

    private void showMessage(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (title.equals("Error") ||
            title.equals("Invalid Input")) {

            alert.setAlertType(Alert.AlertType.ERROR);
        }

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    // =====================================================
    // MAIN METHOD
    // =====================================================

    public static void main(String[] args) {
        launch(args);
    }
}
