package com.bambank.store;

import com.bambank.model.Transaction;
import com.bambank.model.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class StoreService {
    private final Database database;
    private final HashMap<Integer, Integer> sessionKeys;

    public StoreService(Database database) {
        this.database = database;
        this.sessionKeys = new HashMap<>();
    }

    public Integer getUserID(String username) {
        return database.getUserID(username);
    }

    public String getUsername(int userID) {
        return database.getUser(userID).getUsername();
    }

    public Integer getSessionKey(Integer userID, byte[] hash) {
        byte[] validHash = database.getUser(userID).getPasswordHash();
        if (!Arrays.equals(hash, validHash))
            return null;
        int sessionKey = ThreadLocalRandom.current().nextInt();
        sessionKeys.put(userID, sessionKey);
        return sessionKey;
    }

    public void deleteSession(Integer userID) {
        sessionKeys.remove(userID);
    }

    public boolean checkSession(int userID, int sessionKey) {
        return sessionKeys.get(userID) == sessionKey;
    }

    public Integer createUser(String username, byte[] hash) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordHash(hash);
        newUser.setWallet(100);
        return database.addUser(newUser);
    }

    public User getUserDetails(Integer currentUserID, Integer sessionKey) {
        if (!checkSession(currentUserID, sessionKey))
            return null;
        return database.getUser(currentUserID);
    }

    public String sendValue(Integer currentUserID, Integer sessionKey, String username, int amount, String description) {
        if (!checkSession(currentUserID, sessionKey))
            return "Invalid session";
        Transaction transaction = new Transaction();
        transaction.setFromID(currentUserID);
        transaction.setToID(getUserID(username));
        transaction.setValue(amount);
        transaction.setDescription(description);
        try {
            boolean ok = database.makeTransaction(transaction);
            if (!ok)
                return "Transaction failed";
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }
}
