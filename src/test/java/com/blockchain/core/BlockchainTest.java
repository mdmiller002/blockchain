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

public class BlockchainTest {

  private Blockchain blockchain = Blockchain.getInstance();
  private Wallet walletA;
  private Wallet walletB;
  private Wallet coinBase;
  private static final Logger LOG = Logger.getLogger(BlockchainTest.class);

  @Before
  public void before() {
    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    blockchain = Blockchain.getInstance();
    walletA = new Wallet();
    walletB = new Wallet();
    coinBase = new Wallet();
  }

  @After
  public void after() {
    blockchain.clear();
  }

  @Test
  public void test_simpleBlockchain() {

    blockchain.setDifficulty(3);
    blockchain.setMinimumTransaction(new BigDecimal("1.00"));
    assertEquals(blockchain.getMinimumTransaction().compareTo(new BigDecimal("1.00")), 0);

    Transaction genesisTransaction = new Transaction(coinBase.getPublicKey(), walletA.getPublicKey(), new BigDecimal("100.00"), null);
    genesisTransaction.generateSignature(coinBase.getPrivateKey());
    genesisTransaction.setGenesisTransaction();
    TransactionOutput genesisTxo = new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId());
    genesisTransaction.addOutput(genesisTxo);
    blockchain.addUTXO(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

    Block genesis = new Block("0", getTimestamp());
    genesis.addTransaction(genesisTransaction);
    blockchain.addBlock(genesis);

    assertEquals(blockchain.size(), 1);
    assertTrue(blockchain.isChainValid());
    assertEquals(blockchain.getLastBlock(), genesis);
    assertBalanceEquals(walletA, "100.00");
    assertBalanceEquals(walletB, "0.00");

    Block block1 = new Block(blockchain.getLastBlock().getBlockHash(), getTimestamp());
    block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), new BigDecimal("40.55")));
    blockchain.addBlock(block1);

    assertEquals(blockchain.size(), 2);
    assertTrue(blockchain.isChainValid());
    assertEquals(blockchain.getLastBlock(), block1);
    assertBalanceEquals(walletA, "59.45");
    assertBalanceEquals(walletB, "40.55");

    Block block2 = new Block(blockchain.getLastBlock().getBlockHash(), getTimestamp());
    boolean ret = block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), new BigDecimal("1000.00")));
    blockchain.addBlock(block2);

    assertFalse(ret);
    assertTrue(blockchain.isChainValid());
    assertEquals(blockchain.size(), 3);
    assertEquals(blockchain.getLastBlock().getMerkleRoot(), "");
    assertEquals(blockchain.getLastBlock(), block2);
    assertBalanceEquals(walletA, "59.45");
    assertBalanceEquals(walletB, "40.55");

    Block block3 = new Block(blockchain.getLastBlock().getBlockHash(), getTimestamp());
    block3.addTransaction(walletB.sendFunds(walletA.getPublicKey(), new BigDecimal("19.38")));
    block3.addTransaction(walletB.sendFunds(walletA.getPublicKey(), new BigDecimal("10.00")));
    blockchain.addBlock(block3);

    assertTrue(blockchain.isChainValid());
    assertEquals(blockchain.size(), 4);
    assertEquals(blockchain.getLastBlock(), block3);
    assertBalanceEquals(walletA, "88.83");
    assertBalanceEquals(walletB, "11.17");
  }

  @Test
  public void test_blockchainRandomTest() {

    Random rd = new Random();
    final BigDecimal TOTAL_VAL = new BigDecimal("10000000.00");


    Transaction genesisTransaction = new Transaction(coinBase.getPublicKey(), walletA.getPublicKey(), TOTAL_VAL, null);
    genesisTransaction.generateSignature(coinBase.getPrivateKey());
    genesisTransaction.setGenesisTransaction();
    TransactionOutput genesisTxo = new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId());
    genesisTransaction.addOutput(genesisTxo);
    blockchain.addUTXO(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

    Block genesis = new Block("0", getTimestamp());
    genesis.addTransaction(genesisTransaction);
    blockchain.addBlock(genesis);

    Block bBlock = new Block(blockchain.getLastBlock().getBlockHash(), getTimestamp());
    bBlock.addTransaction(walletA.sendFunds(walletB.getPublicKey(), new BigDecimal("5000000.00")));
    blockchain.addBlock(bBlock);

    BigDecimal aCurrentBalance = new BigDecimal("5000000.00");
    BigDecimal bCurrentBalance = new BigDecimal("5000000.00");
    assertTrue(blockchain.isChainValid());
    assertBalanceEquals(walletA, aCurrentBalance);
    assertBalanceEquals(walletB, bCurrentBalance);

    for (int i = 0; i < 250; i++) {
      int numTransactionsInBlock = rd.nextInt(20) + 1;
      Block block = new Block(blockchain.getLastBlock().getBlockHash(), getTimestamp());
      for (int j = 0; j < numTransactionsInBlock; j++) {

        Wallet sender;
        Wallet recipient;
        boolean walletASender = false;
        if (rd.nextBoolean()) {
          // Send from A to B
          sender = walletA;
          recipient = walletB;
          walletASender = true;
        } else {
          sender = walletB;
          recipient = walletA;
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
      blockchain.addBlock(block);
      assertTrue(blockchain.isChainValid());
      assertBalanceEquals(walletA, aCurrentBalance);
      assertBalanceEquals(walletB, bCurrentBalance);
      assertEquals(TOTAL_VAL, aCurrentBalance.add(bCurrentBalance));
      LOG.info("Permutation [" + (i + 1) + "] | WalletA [" + aCurrentBalance.toString() +
              "] | WalletB [" + bCurrentBalance.toString() + "] | Assertions passed");
    }
  }

  private String getTimestamp() {
    return new Timestamp(System.currentTimeMillis()).toString();
  }

  public void assertBalanceEquals(Wallet wallet, String balance) {
    assertEquals(wallet.getBalance().compareTo(new BigDecimal(balance)), 0);
  }

  private void assertBalanceEquals(Wallet wallet, BigDecimal balance) {
    assertEquals(wallet.getBalance().compareTo(balance), 0);
  }

}