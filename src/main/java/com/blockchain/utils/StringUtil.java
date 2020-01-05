package com.blockchain.utils;

import com.blockchain.crypto.Hashing;
import com.blockchain.main.Transaction;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

public class StringUtil {

   private StringUtil() {}

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

   public static String getStringFromKey(Key key) {
      return Base64.getEncoder().encodeToString(key.getEncoded());
   }

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
            treeLayer.add(Hashing.sha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
         }
         count = treeLayer.size();
         previousTreeLayer = treeLayer;
      }
      return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
   }

}
