package com.blockchain.core;

import com.blockchain.utils.CryptoUtil;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Transaction {

  private static final Logger LOG = Logger.getLogger(Transaction.class);
  private String transactionId;
  private PublicKey sender;
  private PublicKey recipient;
  private BigDecimal value;
  private byte[] signature;

  private static int sequence = 0;

  public Transaction(PublicKey sender, PublicKey recipient, BigDecimal value) {
    this.sender = sender;
    this.recipient = recipient;
    this.value = value;
  }

  public boolean processTransaction() {
    if (!verifySignature()) {
      LOG.debug("Transaction Signature failed to verify");
      return false;
    }
    AccountManager.getInstance().updateAccountsOnTransaction(this);
    return true;
  }

  private String calculateHash() {
    sequence++;
    return CryptoUtil.sha256(getTransactionData() + sequence);
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

  public PublicKey getRecipient() {
    return recipient;
  }

  public PublicKey getSender() {
    return sender;
  }

  public void setGenesisTransaction() {
    transactionId = "0";
  }

  public String getSignature() {
    return new String(signature);
  }
}
