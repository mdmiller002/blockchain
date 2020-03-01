package com.blockchain.core;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class Blockchain {

  private static final Logger LOG = Logger.getLogger(Blockchain.class);
  private static Blockchain instance;
  private ArrayList<Block> blockChain;
  private BigDecimal minimumTransaction;
  private int difficulty;
  private HashMap<String, TransactionOutput> UTXOs; //list of all unspent transactions.

  public static Blockchain getInstance() {
    if (instance == null) {
      instance = new Blockchain();
    }
    return instance;
  }

  private Blockchain() {
    blockChain = new ArrayList<>();
    UTXOs = new HashMap<>();
    difficulty = 3;
    minimumTransaction = new BigDecimal("1.00");
  }

  public void addBlock(Block block) {
    block.mineBlock(difficulty);
    blockChain.add(block);
  }

  public void setDifficulty(int difficulty) {
    if (difficulty > 0) {
      this.difficulty = difficulty;
    }
  }

  public HashMap<String, TransactionOutput> getUTXOs() {
    return UTXOs;
  }

  public void addUTXO(String outputId, TransactionOutput transactionOutput) {
    UTXOs.put(outputId, transactionOutput);
  }

  public Block getLastBlock() {
    return blockChain.get(blockChain.size() - 1);
  }

  public boolean isChainValid() {
    for (int i = 1; i < blockChain.size(); i++) {
      if (!isCurrentBlockValid(blockChain.get(i), blockChain.get(i - 1))) {
        LOG.error("Chain is not valid at block " + blockChain.get(i).getBlockHash());
        return false;
      }
    }
    return true;
  }

  public void setMinimumTransaction(BigDecimal minimumTransaction) {
    if (minimumTransaction.compareTo(new BigDecimal("0.00")) > 0) {
      this.minimumTransaction = minimumTransaction;
    }
  }

  public int size() {
    return blockChain.size();
  }

  private boolean isCurrentBlockValid(Block current, Block previous) {
    return current.getBlockHash().equals(current.calculateBlockHash()) &&
            current.getPrevBlockHash().equals(previous.getBlockHash());
  }

  public BigDecimal getMinimumTransaction() {
    return minimumTransaction;
  }

  public void clear() {
    blockChain.clear();
  }
}
