package com.bambank.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
    public static byte[] performHash(String value) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null; // won't happen
        }
        md.update(value.getBytes());
        return md.digest();
    }
}
