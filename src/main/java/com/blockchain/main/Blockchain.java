package com.blockchain.main;

import java.util.ArrayList;
import java.util.HashMap;

public class Blockchain {

   private static Blockchain instance;
   private ArrayList<Block> blockChain;
   private float minimumTransaction = 0.1f;
   private int difficulty = 3;
   private HashMap<String,TransactionOutput> UTXOs; //list of all unspent transactions.

   public static Blockchain getInstance() {
      if (instance == null) {
         instance = new Blockchain();
      }
      return instance;
   }

   private Blockchain() {
      blockChain = new ArrayList<>();
      UTXOs = new HashMap<>();
   }

   public void addBlock(Block block) {
      block.mineBlock(difficulty);
      blockChain.add(block);
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
            return false;
         }
      }
      return true;
   }

   public void setMinimumTransaction(float minimumTransaction) {
      if (minimumTransaction > 0f) {
         this.minimumTransaction = minimumTransaction;
      }
   }

   private boolean isCurrentBlockValid(Block current, Block previous) {
      return current.getBlockHash().equals(current.calculateBlockHash()) &&
              current.getPrevBlockHash().equals(previous.getBlockHash());
   }

   public float getMinimumTransaction() {
      return minimumTransaction;
   }
}
