package com.blockchain.main;


import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

   private PrivateKey privateKey;
   private PublicKey publicKey;
   private Blockchain blockchain;

   public HashMap<String, TransactionOutput> ownedUTXOs = new HashMap<>();

   public Wallet() {
      generateKeyPair();
      blockchain = Blockchain.getInstance();
   }

   public void generateKeyPair() {
      try {
         KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
         SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
         ECGenParameterSpec ecGenParameterSpec= new ECGenParameterSpec("prime192v1");

         keyPairGenerator.initialize(ecGenParameterSpec, random);
         KeyPair keyPair = keyPairGenerator.generateKeyPair();

         privateKey = keyPair.getPrivate();
         publicKey = keyPair.getPublic();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public float getBalance() {
      float total = 0;
      for (Map.Entry<String, TransactionOutput> item : blockchain.getUTXOs().entrySet()) {
         TransactionOutput UTXO = item.getValue();
         if (UTXO.isMine(publicKey)) {
            ownedUTXOs.put(UTXO.getId(), UTXO);
            total += UTXO.getValue();
         }
      }
      return total;
   }

   public Transaction sendFunds(PublicKey recipient, float value) {
      if (getBalance() < value) {
         System.out.println("Not enough funds");
         return null;
      }

      ArrayList<TransactionInput> inputs = new ArrayList<>();

      float total = 0;
      for (Map.Entry<String, TransactionOutput> item : ownedUTXOs.entrySet()) {
         TransactionOutput UTXO = item.getValue();
         total += UTXO.getValue();
         inputs.add(new TransactionInput(UTXO.getId()));
         if (total > value) {
            break;
         }
      }

      Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
      newTransaction.generateSignature(privateKey);

      for (TransactionInput input : inputs) {
         ownedUTXOs.remove(input.transactionOutputId);
      }
      return newTransaction;
   }

   public PublicKey getPublicKey() {
      return publicKey;
   }

   public PrivateKey getPrivateKey() {
      return privateKey;
   }
}
