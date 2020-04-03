package com.blockchain.core;

import org.junit.Test;


public class PersistenceTest {

  CoreTestUtil coreTestUtil = new CoreTestUtil();

  @Test
  public void test_basicPersistence() {
    Persistence persistence = Persistence.getInstance();
    persistence.persistBlock(coreTestUtil.getLedger().getLastBlock());
  }
}