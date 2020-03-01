package com.blockchain.core;

import com.blockchain.utils.CryptoUtil;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

  private String transactionId;
  private PublicKey sender;
  private PublicKey recipient;
  private BigDecimal value;
  private byte[] signature;
  private Blockchain blockchain;

  private ArrayList<TransactionInput> inputs;
  private ArrayList<TransactionOutput> outputs = new ArrayList<>();

  private static int sequence = 0;

  public Transaction(PublicKey sender, PublicKey recipient, BigDecimal value, ArrayList<TransactionInput> inputs) {
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
      input.setUTXO(blockchain.getUTXOs().get(input.getTransactionOutputId()));
    }

    if (getInputsValue().compareTo(blockchain.getMinimumTransaction()) < 0) {
      System.out.println("Transaction inputs too small: " + getInputsValue());
      return false;
    }

    BigDecimal leftOver = getInputsValue().subtract(value);
    transactionId = calculateHash();
    outputs.add(new TransactionOutput(this.recipient, value, transactionId));
    outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

    for (TransactionOutput o : outputs) {
      blockchain.addUTXO(o.getId(), o);
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

  public BigDecimal getInputsValue() {
    BigDecimal total = new BigDecimal("0.00");
    for (TransactionInput input : inputs) {
      if (input.getUTXO() == null) {
        continue;
      }
      total = total.add(input.getUTXO().getValue());
    }
    return total;
  }

  public BigDecimal getOutputsValue() {
    BigDecimal total = new BigDecimal("0.00");
    for (TransactionOutput output : outputs) {
      total = total.add(output.getValue());
    }
    return total;
  }

  private String calculateHash() {
    sequence++;
    return CryptoUtil.sha256(getTransactionData() + Integer.toString(sequence));
  }

  public void generateSignature(PrivateKey privateKey) {
    signature = CryptoUtil.applyECDSASig(privateKey, getTransactionData());
  }

  public boolean verifySignature() {
    return CryptoUtil.verifyECDSASig(sender, getTransactionData(), signature);
  }

  private String getTransactionData() {
    return CryptoUtil.getStringFromKey(sender) + CryptoUtil.getStringFromKey(recipient) + value.toString();
  }

  public BigDecimal getValue() {
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
