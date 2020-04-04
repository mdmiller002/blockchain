package com.blockchain.core;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Security;
import java.sql.Timestamp;
import java.util.Random;

import static org.junit.Assert.*;

public class LedgerTest {

  private Ledger ledger = Ledger.getInstance();
  private Account accountA;
  private Account accountB;
  private Account coinBase;
  private static final Logger LOG = Logger.getLogger(LedgerTest.class);

  @Before
  public void before() {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    ledger = Ledger.getInstance();
    accountA = Account.newAccount();
    accountB = Account.newAccount();
    coinBase = CoinbaseAccount.newAccount();
  }

  @After
  public void after() {
    ledger.clear();
  }

  @Test
  public void test_simpleBlockchain() {

    ledger.setDifficulty(2);
    ledger.setMinimumTransaction(new BigDecimal("1.00"));
    assertEquals(ledger.getMinimumTransaction().compareTo(new BigDecimal("1.00")), 0);

    Transaction genesisTransaction = new Transaction(coinBase.getPublicKey(), accountA.getPublicKey(), new BigDecimal("100.00"));
    genesisTransaction.generateSignature(coinBase.getPrivateKey());
    genesisTransaction.setGenesisTransaction();

    Block genesis = new Block("0", getTimestamp());
    genesis.addTransaction(genesisTransaction);
    ledger.addBlock(genesis);

    assertEquals(ledger.size(), 1);
    assertTrue(ledger.isChainValid());
    assertEquals(ledger.getLastBlock(), genesis);
    assertBalanceEquals(accountA, "100.00");
    assertBalanceEquals(accountB, "0.00");

    Block block1 = new Block(ledger.getLastBlock().getBlockHash(), getTimestamp());
    block1.addTransaction(accountA.sendFunds(accountB.getPublicKey(), new BigDecimal("40.55")));
    ledger.addBlock(block1);

    assertEquals(ledger.size(), 2);
    assertTrue(ledger.isChainValid());
    assertEquals(ledger.getLastBlock(), block1);
    assertBalanceEquals(accountA, "59.45");
    assertBalanceEquals(accountB, "40.55");

    Block block2 = new Block(ledger.getLastBlock().getBlockHash(), getTimestamp());
    boolean ret = block2.addTransaction(accountA.sendFunds(accountB.getPublicKey(), new BigDecimal("1000.00")));
    ledger.addBlock(block2);

    assertFalse(ret);
    assertTrue(ledger.isChainValid());
    assertEquals(ledger.size(), 3);
    assertEquals(ledger.getLastBlock().getMerkleRoot(), "");
    assertEquals(ledger.getLastBlock(), block2);
    assertBalanceEquals(accountA, "59.45");
    assertBalanceEquals(accountB, "40.55");

    Block block3 = new Block(ledger.getLastBlock().getBlockHash(), getTimestamp());
    block3.addTransaction(accountB.sendFunds(accountA.getPublicKey(), new BigDecimal("19.38")));
    block3.addTransaction(accountB.sendFunds(accountA.getPublicKey(), new BigDecimal("10.00")));
    ledger.addBlock(block3);

    assertTrue(ledger.isChainValid());
    assertEquals(ledger.size(), 4);
    assertEquals(ledger.getLastBlock(), block3);
    assertBalanceEquals(accountA, "88.83");
    assertBalanceEquals(accountB, "11.17");
  }

  @Test
  public void test_blockchainRandomTest() {

    Random rd = new Random();
    final BigDecimal TOTAL_VAL = new BigDecimal("10000000.00");


    Transaction genesisTransaction = new Transaction(coinBase.getPublicKey(), accountA.getPublicKey(), TOTAL_VAL);
    genesisTransaction.generateSignature(coinBase.getPrivateKey());
    genesisTransaction.setGenesisTransaction();

    Block genesis = new Block("0", getTimestamp());
    genesis.addTransaction(genesisTransaction);
    ledger.addBlock(genesis);

    Block bBlock = new Block(ledger.getLastBlock().getBlockHash(), getTimestamp());
    bBlock.addTransaction(accountA.sendFunds(accountB.getPublicKey(), new BigDecimal("5000000.00")));
    ledger.addBlock(bBlock);

    BigDecimal aCurrentBalance = new BigDecimal("5000000.00");
    BigDecimal bCurrentBalance = new BigDecimal("5000000.00");
    assertTrue(ledger.isChainValid());
    assertBalanceEquals(accountA, aCurrentBalance);
    assertBalanceEquals(accountB, bCurrentBalance);

    for (int i = 0; i < 250; i++) {
      int numTransactionsInBlock = rd.nextInt(20) + 1;
      Block block = new Block(ledger.getLastBlock().getBlockHash(), getTimestamp());
      for (int j = 0; j < numTransactionsInBlock; j++) {

        Account sender;
        Account recipient;
        boolean walletASender = false;
        if (rd.nextBoolean()) {
          // Send from A to B
          sender = accountA;
          recipient = accountB;
          walletASender = true;
        } else {
          sender = accountB;
          recipient = accountA;
        }

        // Send a random amount of funds to the recipient, up to half of your current balance
        BigDecimal current = walletASender ? aCurrentBalance : bCurrentBalance;

        BigDecimal max = current.divide(new BigDecimal("2.00"), 2, RoundingMode.CEILING);
        BigDecimal randFromDouble = BigDecimal.valueOf(Math.random());
        BigDecimal randomValue = randFromDouble.multiply(max);
        randomValue = randomValue
                .setScale(2, BigDecimal.ROUND_DOWN);

        Transaction transaction = sender.sendFunds(recipient.getPublicKey(), randomValue);
        block.addTransaction(transaction);

        if (walletASender) {
          aCurrentBalance = aCurrentBalance.subtract(randomValue);
          bCurrentBalance = bCurrentBalance.add(randomValue);
        } else {
          aCurrentBalance = aCurrentBalance.add(randomValue);
          bCurrentBalance = bCurrentBalance.subtract(randomValue);
        }
      }
      ledger.addBlock(block);
      assertTrue(ledger.isChainValid());
      assertBalanceEquals(accountA, aCurrentBalance);
      assertBalanceEquals(accountB, bCurrentBalance);
      assertEquals(TOTAL_VAL, aCurrentBalance.add(bCurrentBalance));
      LOG.info("Permutation [" + (i + 1) + "] | WalletA [" + aCurrentBalance.toString() +
              "] | WalletB [" + bCurrentBalance.toString() + "] | Assertions passed");
    }
  }

  private String getTimestamp() {
    return new Timestamp(System.currentTimeMillis()).toString();
  }

  public void assertBalanceEquals(Account account, String balance) {
    assertEquals(account.getBalance().compareTo(new BigDecimal(balance)), 0);
  }

  private void assertBalanceEquals(Account account, BigDecimal balance) {
    assertEquals(account.getBalance().compareTo(balance), 0);
  }

}