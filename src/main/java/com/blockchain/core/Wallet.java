package com.blockchain.core;


import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

  private final Logger LOG = Logger.getLogger(Wallet.class);
  private PrivateKey privateKey;
  private PublicKey publicKey;
  private Blockchain blockchain;

  public HashMap<String, TransactionOutput> ownedUTXOs = new HashMap<>();

  public Wallet() {
    generateKeyPair();
    blockchain = Blockchain.getInstance();
  }

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
    return populateOwnedUtxosAndReturnBalance();
  }

  private BigDecimal populateOwnedUtxosAndReturnBalance() {
    BigDecimal total = new BigDecimal("0.00");
    for (Map.Entry<String, TransactionOutput> item : blockchain.getUTXOs().entrySet()) {
      TransactionOutput UTXO = item.getValue();
      if (UTXO.isMine(publicKey)) {
        ownedUTXOs.put(UTXO.getId(), UTXO);
        total = total.add(UTXO.getValue());
      }
    }
    return total;
  }

  public Transaction sendFunds(PublicKey recipient, BigDecimal value) {
    if (getBalance().compareTo(value) < 0) {
      LOG.debug("Insufficient funds: attempted to send " + value + " but balance is " + getBalance());
      return null;
    }

    ArrayList<TransactionInput> inputs = new ArrayList<>();

    BigDecimal total = new BigDecimal("0.00");
    for (Map.Entry<String, TransactionOutput> item : ownedUTXOs.entrySet()) {
      TransactionOutput UTXO = item.getValue();
      total = total.add(UTXO.getValue());
      inputs.add(new TransactionInput(UTXO.getId()));
      if (total.compareTo(value) > 0) {
        break;
      }
    }

    Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
    newTransaction.generateSignature(privateKey);

    for (TransactionInput input : inputs) {
      ownedUTXOs.remove(input.getTransactionOutputId());
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
