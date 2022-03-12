package com.bambank.ui;

import com.bambank.model.Transaction;
import com.bambank.model.User;
import com.bambank.store.StoreService;
import com.bambank.util.Hasher;

import javax.swing.*;
import java.awt.*;

public class Interface {
    StoreService storeService;

    Integer currentUserID;
    Integer sessionKey;

    JFrame frame;
    JPanel cards;
    CardLayout cardLayout;
    JLabel usernameLabel;
    JLabel walletLabel;
    JPanel transactionsPanel;

    public Interface(StoreService storeService) {
        this.storeService = storeService;
        frame = new JFrame("Title");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1280, 720));
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.add(createLoginPanel(), "login");
        cards.add(createRegisterPanel(), "register");
        cards.add(createAccountPanel(), "account");
        cards.add(createSendPanel(), "send");

        frame.getContentPane().add(cards);
        cardLayout.show(cards, "login");
    }

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();

        loginPanel.setLayout(new BorderLayout());
        JPanel middlePanel = new JPanel();
        loginPanel.add(BorderLayout.CENTER, middlePanel);

        middlePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridy = 0;
        middlePanel.add(new JLabel("Username: "), c);
        JTextField username = new JTextField(10);
        middlePanel.add(username, c);

        c.gridy = 1;
        c.insets = new Insets(10,0,0,0);
        middlePanel.add(new JLabel("Password: "), c);
        JTextField password = new JPasswordField(10);
        middlePanel.add(password, c);

        c.gridx = 1;
        c.gridy = 2;
        JButton login = new JButton("Login");
        middlePanel.add(login, c);

        c.gridy = 3;
        JLabel errorLabel = new JLabel("");
        middlePanel.add(errorLabel, c);

        login.addActionListener(e -> {
            String error = tryLogin(username, password);
            username.setText("");
            password.setText("");
            if (error != null) {
                errorLabel.setText(error);
            } else {
                errorLabel.setText("");
                cardLayout.show(cards, "account");
                updateAccount();
            }
        });

        JPanel bottomPanel = new JPanel();
        loginPanel.add(BorderLayout.SOUTH, bottomPanel);
        bottomPanel.setLayout(new GridBagLayout());

        c.gridx = 0;
        c.gridy = 0;
        bottomPanel.add(new JLabel("Don't have an account?"), c);

        c.gridx = 1;
        JButton register = new JButton("Register here");
        bottomPanel.add(register, c);
        register.addActionListener(e -> {
            username.setText("");
            password.setText("");
            errorLabel.setText("");
            cardLayout.show(cards, "register");
        });

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        bottomPanel.add(new JLabel("PROMOTION: 100 free Bambeuros when you sign up!"), c);
        return loginPanel;
    }

    private String tryLogin(JTextField usernameText, JTextField passwordText) {
        String username = usernameText.getText();
        Integer userID = storeService.getUserID(username);
        if (userID == null)
            return "Username is invalid";
        byte[] hash = Hasher.performHash(passwordText.getText() + username); // salt
        Integer sessionKey = storeService.getSessionKey(userID, hash);
        if (sessionKey == null)
            return "Password is incorrect";
        currentUserID = userID;
        this.sessionKey = sessionKey;
        return null;
    }

    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridy = 0;
        registerPanel.add(new JLabel("Username: "), c);
        JTextField username = new JTextField(10);
        registerPanel.add(username, c);

        c.gridy = 1;
        c.insets = new Insets(10,0,0,0);
        registerPanel.add(new JLabel("Password: "), c);
        JTextField password = new JPasswordField(10);
        registerPanel.add(password, c);

        c.gridy = 2;
        registerPanel.add(new JLabel("Repeat Password: "), c);
        JTextField repeatPassword = new JPasswordField(10);
        registerPanel.add(repeatPassword, c);

        c.gridx = 1;
        c.gridy = 3;
        JButton register = new JButton("Register");
        registerPanel.add(register, c);

        c.gridy = 4;
        JLabel errorLabel = new JLabel("");
        registerPanel.add(errorLabel, c);

        register.addActionListener(e -> {
            String error = tryRegister(username, password, repeatPassword);
            password.setText("");
            repeatPassword.setText("");
            if (error != null) {
                errorLabel.setText(error);
            } else {
                errorLabel.setText("");
                cardLayout.show(cards, "login");
            }
        });

        c.gridy = 5;
        JButton cancel = new JButton("Cancel");
        registerPanel.add(cancel, c);
        cancel.addActionListener(e -> {
            username.setText("");
            password.setText("");
            repeatPassword.setText("");
            cardLayout.show(cards, "login");
        });

        return registerPanel;
    }

    private String tryRegister(JTextField usernameText, JTextField passwordText, JTextField repeatPasswordText) {
        String username = usernameText.getText();
        String password = passwordText.getText();
        String repeatPassword = repeatPasswordText.getText();
        if (username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty())
            return "Invalid input";
        if (!password.equals(repeatPassword))
            return "Passwords do not match";
        if (storeService.getUserID(username) != null)
            return "Username already taken";
        byte[] hash = Hasher.performHash(password + username); // salt
        Integer userID = storeService.createUser(username, hash);
        if (userID == null)
            return "Error creating account";
        return null;
    }

    private JPanel createAccountPanel() {
        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        accountPanel.add(BorderLayout.NORTH, topPanel);
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,10,0,10);

        JButton logout = new JButton("Log Out");
        topPanel.add(logout, c);
        logout.addActionListener(e -> {
            storeService.deleteSession(currentUserID);
            currentUserID = null;
            sessionKey = null;
            cardLayout.show(cards, "login");
            updateAccount();
        });

        c.gridx = 1;
        usernameLabel = new JLabel("");
        topPanel.add(usernameLabel, c);
        c.gridx = 2;
        walletLabel = new JLabel("");
        topPanel.add(walletLabel, c);

        c.gridx = 3;
        JButton send = new JButton("Send Money");
        topPanel.add(send, c);
        send.addActionListener(e -> cardLayout.show(cards, "send"));

        JPanel middlePanel = new JPanel();
        accountPanel.add(BorderLayout.CENTER, middlePanel);
        middlePanel.setLayout(new BorderLayout());
        middlePanel.add(BorderLayout.NORTH, new JLabel("Transactions"));

        transactionsPanel = new JPanel();
        transactionsPanel.setLayout(new GridBagLayout());
        middlePanel.add(BorderLayout.CENTER, transactionsPanel);

        return accountPanel;
    }

    private void updateAccount() {
        User user = null;
        if (currentUserID != null && sessionKey != null)
            user = storeService.getUserDetails(currentUserID, sessionKey);
        if (user == null) {
            usernameLabel.setText("");
            walletLabel.setText("");
            transactionsPanel.removeAll();
            return;
        }
        usernameLabel.setText(user.getUsername());
        walletLabel.setText(user.getWallet() + " Bambeuros");

        GridBagConstraints c = new GridBagConstraints();
        transactionsPanel.removeAll();
        if (user.getLedger().isEmpty()) {
            transactionsPanel.add(new JLabel("No transactions yet"), c);
        } else {
            c.insets = new Insets(5, 0, 0, 0);
            for (int i = 0; i < user.getLedger().size(); i++) {
                c.gridy = i;
                Transaction transaction = user.getLedger().get(i);

                StringBuilder sb = new StringBuilder();
                if (currentUserID == transaction.getFromID())
                    sb.append("Sent ");
                else
                    sb.append("Received ");
                sb.append(transaction.getValue());
                sb.append(" Bambeuros");
                if (currentUserID != transaction.getFromID())
                    sb.append(" from ").append(storeService.getUsername(transaction.getFromID()));
                else
                    sb.append(" to ").append(storeService.getUsername(transaction.getToID()));
                sb.append(". Description: ").append(transaction.getDescription());
                transactionsPanel.add(new JLabel(sb.toString()), c);
            }
        }
    }

    private JPanel createSendPanel() {
        JPanel sendPanel = new JPanel();

        sendPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridy = 0;
        sendPanel.add(new JLabel("Send To: "), c);
        JTextField username = new JTextField(10);
        sendPanel.add(username, c);

        c.gridy = 1;
        c.insets = new Insets(10,0,0,0);
        sendPanel.add(new JLabel("Amount: "), c);
        JTextField amount = new JTextField(10);
        sendPanel.add(amount, c);

        c.gridy = 2;
        sendPanel.add(new JLabel("Description: "), c);
        JTextField description = new JTextField(10);
        sendPanel.add(description, c);

        c.gridx = 1;
        c.gridy = 3;
        JButton send = new JButton("Send");
        sendPanel.add(send, c);

        c.gridy = 4;
        JLabel errorLabel = new JLabel("");
        sendPanel.add(errorLabel, c);

        send.addActionListener(e -> {
            String error = sendValue(username, amount, description);
            if (error != null) {
                errorLabel.setText(error);
            } else {
                username.setText("");
                amount.setText("");
                description.setText("");
                errorLabel.setText("");
                cardLayout.show(cards, "account");
                updateAccount();
            }
        });

        c.gridy = 5;
        JButton cancel = new JButton("Cancel");
        sendPanel.add(cancel, c);
        cancel.addActionListener(e -> {
            username.setText("");
            amount.setText("");
            description.setText("");
            errorLabel.setText("");
            cardLayout.show(cards, "account");
        });

        return sendPanel;
    }

    private String sendValue(JTextField usernameText, JTextField amountText, JTextField descriptionText) {
        String username = usernameText.getText();
        String amount = amountText.getText();
        String description = descriptionText.getText();
        if (storeService.getUserID(username) == null)
            return "User does not exist";
        int value;
        try {
            value = Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            return "Invalid value";
        }
        return storeService.sendValue(currentUserID, sessionKey, username, value, description);
    }

    public void showFrame() {
        frame.setVisible(true);
    }
}
