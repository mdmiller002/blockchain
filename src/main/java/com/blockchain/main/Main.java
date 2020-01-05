package com.blockchain.main;

import java.security.Security;

public class Main {

   private static void runBlockchain() {
      Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
      Blockchain blockchain = Blockchain.getInstance();
      Wallet walletA = new Wallet();
      Wallet walletB = new Wallet();
      Wallet coinbase = new Wallet();



      Transaction genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f, null);
      genesisTransaction.generateSignature(coinbase.getPrivateKey());
      genesisTransaction.setGenesisTransaction();
      genesisTransaction.addOutput(new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId()));
      blockchain.addUTXO(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

      System.out.println("Creating and mining genesis block");
      Block genesis = new Block("0");
      genesis.addTransaction(genesisTransaction);
      blockchain.addBlock(genesis);

      Block block1 = new Block(genesis.getBlockHash());
      System.out.println("\nWalletA's balance is: " + walletA.getBalance());
      System.out.println("WalletA is attempting to send funds (40) to WalletB...");
      block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
      blockchain.addBlock(block1);
      System.out.println("\nWalletA's balance is: " + walletA.getBalance());
      System.out.println("WalletB's balance is: " + walletB.getBalance());

      Block block2 = new Block(block1.getBlockHash());
      System.out.println("\nWallet A attempting to send more funds (1000) than it has...");
      block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000));
      blockchain.addBlock(block2);
      System.out.println("Wallet A's balance is: " + walletA.getBalance());
      System.out.println("Wallet B's Balance is: " + walletB.getBalance());

      Block block3 = new Block(block2.getBlockHash());
      System.out.println("\nWallet B is attempting to send funds (20) to Wallet A...");
      block3.addTransaction(walletB.sendFunds(walletA.getPublicKey(), 20));
      blockchain.addBlock(block3);
      System.out.println("Wallet A's Balance: " + walletA.getBalance());
      System.out.println("Wallet B's Balance: " + walletB.getBalance());

      System.out.println(blockchain.isChainValid());
   }

   public static void main(String[] args) {
      runBlockchain();
   }
}
