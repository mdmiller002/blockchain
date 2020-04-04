package com.blockchain.core;


import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

/**
 * A single account in the network
 */
public class Account {

  private final Logger LOG = Logger.getLogger(Account.class);
  private PrivateKey privateKey;
  private PublicKey publicKey;
  private BigDecimal balance;

  protected Account() {
    generateKeyPair();
    balance = new BigDecimal("0.00");
  }

  public static Account newAccount() {
    Account newAccount = new Account();
    AccountManager.getInstance().registerAccount(newAccount);
    return newAccount;
  }

  public void subtractBalance(BigDecimal amount) {
    balance = balance.subtract(amount);
  }

  public void addBalance(BigDecimal amount) {
    balance = balance.add(amount);
  }

  /**
   * Generates a private and public key-pair for this account
   */
  private void generateKeyPair() {
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("prime192v1");

      keyPairGenerator.initialize(ecGenParameterSpec, random);
      KeyPair keyPair = keyPairGenerator.generateKeyPair();

      privateKey = keyPair.getPrivate();
      publicKey = keyPair.getPublic();
    } catch (Exception e) {
      LOG.error("Failed to generate key pair: ", e);
      throw new RuntimeException(e);
    }
  }

  public BigDecimal getBalance() {
    return balance;
  }

  /**
   * Send funds to another account
   * @param recipient receiver of funds
   * @param value amount to send
   * @return Transaction encapsulating this fund sending
   */
  public Transaction sendFunds(PublicKey recipient, BigDecimal value) {
    if (getBalance().compareTo(value) < 0) {
      LOG.debug("Insufficient funds: attempted to send " + value + " but balance is " + getBalance());
      return null;
    }

    Transaction newTransaction = new Transaction(publicKey, recipient, value);
    newTransaction.generateSignature(privateKey);
    return newTransaction;
  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public PrivateKey getPrivateKey() {
    return privateKey;
  }
}
