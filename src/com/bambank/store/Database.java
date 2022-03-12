package com.bambank.store;

import com.bambank.model.Transaction;
import com.bambank.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private final Map<String, Integer> usernameToId;
    private final Map<Integer, User> users;
    private final List<Transaction> ledger;

    public Database() {
        usernameToId = new HashMap<>();
        users = new HashMap<>();
        ledger = new ArrayList<>();
    }

    public Integer getUserID(String username) {
        return usernameToId.get(username);
    }

    public Integer addUser(User user) {
        if (user.getUsername() == null || user.getPasswordHash() == null)
            return null;
        if (usernameToId.containsKey(user.getUsername()))
            return null;
        int newId = users.size();
        users.put(newId, user);
        usernameToId.put(user.getUsername(), newId);
        return newId;
    }

    public User getUser(Integer id) {
        return users.get(id);
    }

    /**
     * Make transaction. Atomic
     */
    public synchronized boolean makeTransaction(Transaction transaction) throws Exception {
        // checks
        if (!users.containsKey(transaction.getToID()) || !users.containsKey(transaction.getFromID()))
            throw new Exception("Invalid user IDs");
        if (users.get(transaction.getFromID()).getWallet() < transaction.getValue())
            throw new Exception("Amount exceeds user wallet amount");

        User origin = users.get(transaction.getFromID());
        int originWallet = origin.getWallet();
        User dest = users.get(transaction.getToID());
        int destWallet = dest.getWallet();
        int transactionId = ledger.size();
        int value = transaction.getValue();

        try {
            transaction.setTransactionID(transactionId);
            origin.setWallet(origin.getWallet() - value);
            dest.setWallet(dest.getWallet() + value);

            ledger.add(transaction);
            origin.getLedger().add(transaction);
            dest.getLedger().add(transaction);
        } catch (Exception e) {
            // reverse changes
            origin.setWallet(originWallet);
            dest.setWallet(destWallet);

            Transaction reversal = transaction.generateReverse();
            ledger.add(reversal);
            origin.getLedger().add(reversal);
            dest.getLedger().add(reversal);
            return false;
        }

        return true;
    }
}
