package com.blockchain.core;

import java.security.PublicKey;
import java.util.HashMap;

/**
 * Account manager handles record keeping of individual accounts in the system
 */
public class AccountManager {

  private static AccountManager instance;

  private HashMap<PublicKey, Account> accounts;

  public static AccountManager getInstance() {
    if (instance == null) {
      instance = new AccountManager();
    }
    return instance;
  }

  private AccountManager() {
    accounts = new HashMap<>();
  }

  public void registerAccount(Account account) {
    accounts.put(account.getPublicKey(), account);
  }

  public void updateAccountsOnTransaction(Transaction transaction){
    Account sender = accounts.get(transaction.getSender());
    Account recipient = accounts.get(transaction.getRecipient());
    sender.subtractBalance(transaction.getValue());
    recipient.addBalance(transaction.getValue());
  }
}
