package com.blockchain.main;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BlockchainTest {

   private Blockchain blockchain = Blockchain.getInstance();

//   @Test
//   public void testBlockchain() {
//      Block genesis = new Block(getTxs(0), "0", "2020-01-01 21:36:29.383");
//      blockchain.insertBlock(genesis);
//      blockchain.getLastBlock().mineBlock(2);
//
//      Block b2 = new Block(getTxs(5), genesis.getBlockHash(), "2020-01-01 22:36:29.383");
//      blockchain.insertBlock(b2);
//      blockchain.getLastBlock().mineBlock(2);
//
//       blockchain.insertBlock(new Block(getTxs(10), b2.getBlockHash(), "2020-01-02 22:19:33.2"));
//       blockchain.getLastBlock().mineBlock(2);
//
//      assertTrue(blockchain.isChainValid());
//   }
//
//   private ArrayList<String> getTxs(int numTxs) {
//      ArrayList<String> txs = new ArrayList<>();
//      for (int i = 0; i < numTxs; i++) {
//         String tx = "tx" + (i + 1);
//         txs.add(tx);
//      }
//      return txs;
//   }

}