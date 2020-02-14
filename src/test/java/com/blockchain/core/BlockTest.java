package com.blockchain.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlockTest {

  @Test
  public void test_BlockCreation() {
    Block block = createTestBlock();
    assertEquals(block.getPrevBlockHash(), "hash");
    assertEquals(block.getBlockHash(), "d82a14296922144229eb87b8573d27357fb5bcda657cfffe413174f7e4744a2a");
    assertEquals(block.getTimestamp(), "2020-02-13 21:31:13.902");
    assertEquals(block.getNonce(), 0);
  }

  @Test
  public void test_MineBlockTest() {
    Block block1 = createTestBlock();
    String hash1 = block1.mineBlock(2);
    assertEquals(hash1, "00d9bbeaa72ef354875447c79662eaf2924f7cc557920c407a9894629a158efe");

    Block block2 = createTestBlock();
    String hash2 = block2.mineBlock(4);
    assertEquals(hash2, "0000f5583585cf0bc528d3bfc9258964094545548000b805437877579a8b67f4");
  }

  private Block createTestBlock() {
    return new Block("hash", "2020-02-13 21:31:13.902");
  }
}