package com.blockchain.core;

import java.math.BigDecimal;
import java.security.Security;
import java.sql.Timestamp;

public class CoreTestUtil {

  private Account accountA;
  private Account accountB;
  private Account coinBase;
  private static Ledger ledger = Ledger.getInstance();

  public CoreTestUtil() {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    accountA = new Account();
    accountB = new Account();
    coinBase = new Account();

    ledger.setDifficulty(3);
    ledger.setMinimumTransaction(new BigDecimal("1.00"));

    Transaction genesisTransaction = new Transaction(coinBase.getPublicKey(), accountA.getPublicKey(), new BigDecimal("10000.00"), null);
    genesisTransaction.generateSignature(coinBase.getPrivateKey());
    genesisTransaction.setGenesisTransaction();
    TransactionOutput genesisTxo = new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId());
    genesisTransaction.addOutput(genesisTxo);
    ledger.addUTXO(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

    Block genesis = new Block("0", new Timestamp(System.currentTimeMillis()).toString());
    genesis.addTransaction(genesisTransaction);
    ledger.addBlock(genesis);

    sendAToBSingleBlock(new BigDecimal("5000.00"));
  }

  public void sendAToBSingleBlock(BigDecimal amount) {
    Block block = new Block(ledger.getLastBlock().getPrevBlockHash(), getTimestamp());
    block.addTransaction(accountA.sendFunds(accountB.getPublicKey(), amount));
    ledger.addBlock(block);
  }

  public void sendBToASingleBlock(BigDecimal amount) {
    Block block = new Block(ledger.getLastBlock().getPrevBlockHash(), getTimestamp());
    block.addTransaction(accountA.sendFunds(accountB.getPublicKey(), amount));
    ledger.addBlock(block);
  }

  private String getTimestamp() {
    return new Timestamp(System.currentTimeMillis()).toString();
  }

  public Ledger getLedger() {
    return ledger;
  }
}
