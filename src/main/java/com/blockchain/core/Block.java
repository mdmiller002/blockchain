package com.blockchain.core;

import com.blockchain.utils.CryptoUtil;

import java.util.ArrayList;

/**
 * One block on the chain. Block has the capability to be created and mined.
 */
public class Block {

  private ArrayList<Transaction> transactions = new ArrayList<>();
  private String prevBlockHash;
  private String blockHash;
  private String timestamp;
  private String merkleRoot;
  private int nonce;

  public Block(String prevBlockHash, String timestamp) {
    this.prevBlockHash = prevBlockHash;
    this.timestamp = timestamp;
    this.nonce = 0;
    this.blockHash = calculateBlockHash();
  }

  /**
   * Mine a block, difficulty scales with a scale factor
   *
   * @param scale scale factor that sets difficulty to mine
   * @return current block's hash after mined successfully
   */
  public String mineBlock(int scale) {
    merkleRoot = CryptoUtil.getMerkleRoot(transactions);
    String target = new String(new char[scale]).replace('\0', '0');
    while (!blockHash.substring(0, scale).equals(target)) {
      nonce++;
      blockHash = calculateBlockHash();
    }
    return blockHash;
  }

  public String calculateBlockHash() throws IllegalStateException {
    return CryptoUtil.sha256(serializeBlock());
  }

  private String serializeBlock() {
    StringBuilder builder = new StringBuilder();
    builder.append(prevBlockHash);
    builder.append(timestamp);
    builder.append(nonce);
    builder.append(merkleRoot);
    for (Transaction tx : transactions) {
      builder.append(tx.getValue());
    }
    return builder.toString();
  }

  public boolean addTransaction(Transaction transaction) {
    if (transaction == null) {
      return false;
    }
    if (!"0".equals(prevBlockHash)) {
      if (!transaction.processTransaction()) {
        System.out.println("Transaction failed to process");
        return false;
      }
    }
    transactions.add(transaction);
    return true;
  }

  public ArrayList<Transaction> getTransactions() {
    return transactions;
  }

  public String getPrevBlockHash() {
    return prevBlockHash;
  }

  public String getBlockHash() {
    return blockHash;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public int getNonce() {
    return nonce;
  }

  public String getMerkleRoot() {
    return merkleRoot;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Block)) {
      return false;
    }
    Block other = (Block) o;
    return other.getBlockHash().equals(this.getBlockHash()) &&
            other.getPrevBlockHash().equals(this.getPrevBlockHash());
  }
}
