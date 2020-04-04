package com.blockchain.core;

import java.math.BigDecimal;
import java.security.PublicKey;

/**
 * The CoinBase account is an account that has special privileges.
 * This type of account can inject new value into the network,
 * thus there isn't a concept of a "balance" for this account.
 */
public class CoinbaseAccount extends Account {

  public static Account newAccount() {
    CoinbaseAccount newAccount = new CoinbaseAccount();
    AccountManager.getInstance().registerAccount(newAccount);
    return newAccount;
  }

  @Override
  public void subtractBalance(BigDecimal amount) { }

  @Override
  public void addBalance(BigDecimal amount) { }

  @Override
  public Transaction sendFunds(PublicKey recipient, BigDecimal value) {
    Transaction newTransaction = new Transaction(getPublicKey(), recipient, value);
    newTransaction.generateSignature(getPrivateKey());
    return newTransaction;
  }

}
