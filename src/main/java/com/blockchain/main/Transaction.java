package com.blockchain.main;

import com.blockchain.crypto.Hashing;
import com.blockchain.utils.StringUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

   private String transactionId;
   private PublicKey sender;
   private PublicKey recipient;
   private float value;
   private byte[] signature;
   private Blockchain blockchain;

   private ArrayList<TransactionInput> inputs;
   private ArrayList<TransactionOutput> outputs = new ArrayList<>();

   private static int sequence = 0;

   public Transaction(PublicKey sender, PublicKey recipient, float value, ArrayList<TransactionInput> inputs) {
      this.sender = sender;
      this.recipient = recipient;
      this.value = value;
      this.inputs = inputs;
      blockchain = Blockchain.getInstance();
   }

   public boolean processTransaction() {
      if (!verifySignature()) {
         System.out.println("Transaction Signature failed to verify");
         return false;
      }

      for (TransactionInput input : inputs) {
         input.setUTXO(blockchain.getUTXOs().get(input.transactionOutputId));
      }

      if (getInputsValue() < blockchain.getMinimumTransaction()) {
         System.out.println("Transaction inputs too small: " + getInputsValue());
         return false;
      }

      float leftOver = getInputsValue() - value;
      transactionId = calculateHash();
      outputs.add(new TransactionOutput(this.recipient, value, transactionId));
      outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

      for (TransactionOutput o : outputs) {
         blockchain.getUTXOs().put(o.getId(), o);
      }

      for (TransactionInput input : inputs) {
         if (input.getUTXO() == null) {
            continue;
         }
         blockchain.getUTXOs().remove(input.getUTXO().getId());
      }
      return true;
   }

   public void addOutput(TransactionOutput output) {
      outputs.add(output);
   }

   public void addInput(TransactionInput input) {
      inputs.add(input);
   }

   public float getInputsValue() {
      float total = 0;
      for (TransactionInput input : inputs) {
         if (input.getUTXO() == null) {
            continue;
         }
         total += input.getUTXO().getValue();
      }
      return total;
   }

   public float getOutputsValue() {
      float total = 0;
      for (TransactionOutput output : outputs) {
         total += output.getValue();
      }
      return total;
   }

   private String calculateHash() {
      sequence++;
      return Hashing.sha256(getTransactionData() + Integer.toString(sequence));
   }

   public void generateSignature(PrivateKey privateKey) {
      signature = StringUtil.applyECDSASig(privateKey, getTransactionData());
   }

   public boolean verifySignature() {
      return StringUtil.verifyECDSASig(sender, getTransactionData(), signature);
   }

   private String getTransactionData() {
      return StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value);
   }

   public float getValue() {
      return value;
   }

   public String getTransactionId() {
      return transactionId;
   }

   public ArrayList<TransactionOutput> getOutputs() {
      return outputs;
   }

   public ArrayList<TransactionInput> getInputs() {
      return inputs;
   }

   public PublicKey getRecipient() {
      return recipient;
   }

   public PublicKey getSender() {
      return sender;
   }

   public void setGenesisTransaction() {
      transactionId = "0";
   }
}
