package com.bambank;

import com.bambank.model.User;
import com.bambank.store.Database;
import com.bambank.store.StoreService;
import com.bambank.ui.Interface;
import com.bambank.util.Hasher;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.start();
    }

    private void start() {
        Database database = new Database();
        StoreService storeService = new StoreService(database);
        Interface anInterface = new Interface(storeService);
        anInterface.showFrame();

        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash(Hasher.performHash("admin" + "admin"));
        user.setWallet(Integer.MAX_VALUE);
        database.addUser(user);
    }
}
