package com.blockchain.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {

    /**
     * sha256 hash on arbitrary data
     * @param data String data to hash
     * @return String of hash
     */
    public static String sha256(String data) {
        byte[] hashBytes = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            hashBytes = messageDigest.digest(data.getBytes());
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
        if(hashBytes != null) {
            StringBuilder buff = new StringBuilder();
            for (byte b : hashBytes) {
                buff.append(String.format("%02x", b));
            }
            return buff.toString();
        }
        else {
            throw new IllegalStateException("Error hashing bytes for block");
        }
    }

}
