package com.bambank.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String username;
    private byte[] passwordHash;
    private int wallet;
    private final List<Transaction> ledger;

    public User() {
        ledger = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    public List<Transaction> getLedger() {
        return ledger;
    }
}
