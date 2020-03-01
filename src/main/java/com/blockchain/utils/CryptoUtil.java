package com.blockchain.utils;

import com.blockchain.core.Transaction;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Static utility class with crypto helper functions
 */
public class CryptoUtil {

  private CryptoUtil() {
  }

  /**
   * sha256 hash on arbitrary data
   *
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
    if (hashBytes != null) {
      StringBuilder buff = new StringBuilder();
      for (byte b : hashBytes) {
        buff.append(String.format("%02x", b));
      }
      return buff.toString();
    } else {
      throw new IllegalStateException("Error hashing bytes for block");
    }
  }

  /**
   * Apply the ECDSA Signature on an input string for a private key
   *
   * @param privateKey private key
   * @param input      input string
   * @return byte array of signature
   */
  public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
    Signature dsa;
    byte[] output;
    try {
      dsa = Signature.getInstance("ECDSA", "BC");
      dsa.initSign(privateKey);
      byte[] strByte = input.getBytes();
      dsa.update(strByte);
      byte[] realSig = dsa.sign();
      output = realSig;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return output;
  }

  /**
   * Verify the ECDSA signature using the public key
   *
   * @param publicKey public key to verify with
   * @param data      initial data signed
   * @param signature signature from private key
   * @return true if verified, false else wise
   */
  public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
    try {
      Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
      ecdsaVerify.initVerify(publicKey);
      ecdsaVerify.update(data.getBytes());
      return ecdsaVerify.verify(signature);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Convert a key to a string
   *
   * @param key key to get string from
   * @return String representation of key
   */
  public static String getStringFromKey(Key key) {
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  /**
   * Get the merkle root from a list of transactions
   *
   * @param transactions list of transactions to get merkle root of
   * @return String transaction ID of the merkle root
   */
  public static String getMerkleRoot(ArrayList<Transaction> transactions) {
    int count = transactions.size();
    ArrayList<String> previousTreeLayer = new ArrayList<>();
    for (Transaction transaction : transactions) {
      previousTreeLayer.add(transaction.getTransactionId());
    }
    ArrayList<String> treeLayer = previousTreeLayer;
    while (count > 1) {
      treeLayer = new ArrayList<>();
      for (int i = 1; i < previousTreeLayer.size(); i++) {
        treeLayer.add(CryptoUtil.sha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
      }
      count = treeLayer.size();
      previousTreeLayer = treeLayer;
    }
    return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
  }

}
