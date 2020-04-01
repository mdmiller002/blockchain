package com.blockchain.core;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.security.Security;
import java.sql.Timestamp;


public class PersistenceTest {

  private Account accountA;
  private Account coinBase;
  private Ledger ledger = Ledger.getInstance();

  @Before
  public void before() {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    accountA = new Account();
    coinBase = new Account();
  }

  @Test
  public void test() {
    ledger.setDifficulty(3);
    ledger.setMinimumTransaction(new BigDecimal("1.00"));

    Transaction genesisTransaction = new Transaction(coinBase.getPublicKey(), accountA.getPublicKey(), new BigDecimal("100.00"), null);
    genesisTransaction.generateSignature(coinBase.getPrivateKey());
    genesisTransaction.setGenesisTransaction();
    TransactionOutput genesisTxo = new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId());
    genesisTransaction.addOutput(genesisTxo);
    ledger.addUTXO(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

    Block genesis = new Block("0", new Timestamp(System.currentTimeMillis()).toString());
    genesis.addTransaction(genesisTransaction);
    Block minedBlock = ledger.addBlock(genesis);

    Persistence persistence = Persistence.getInstance();
    persistence.PersistBlock(minedBlock);
  }

}