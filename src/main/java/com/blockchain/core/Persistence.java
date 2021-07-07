package com.blockchain.core;

import com.blockchain.props.PropertyReader;
import com.blockchain.utils.CryptoUtil;
import com.mongodb.BasicDBObject;

import com.mongodb.client.*;

import java.util.ArrayList;
import java.util.List;

public class Persistence {

  private static final String MONGO_DB_NAME = "primary";
  private static final String COLLECTION_NAME = "ledger";

  private static Persistence instance;
  MongoClient mongoClient;
  MongoDatabase mongoDatabase;
  MongoCollection<BasicDBObject> collection;
  PropertyReader propertyReader = new PropertyReader();

  private Persistence() {
    mongoClient = MongoClients.create(propertyReader.getMongoUrl());
    mongoDatabase = mongoClient.getDatabase(MONGO_DB_NAME);
    collection = mongoDatabase.getCollection(COLLECTION_NAME, BasicDBObject.class);
  }

  public static Persistence getInstance() {
    if (instance == null) {
      instance = new Persistence();
    }
    return instance;
  }

  public void persistBlock(Block block) {
    BasicDBObject blockObject = new BasicDBObject();
    blockObject.put("blockHash", block.getBlockHash());
    blockObject.put("prevBlockHash", block.getPrevBlockHash());
    blockObject.put("nonce", block.getNonce());
    blockObject.put("timestamp", block.getTimestamp());
    blockObject.put("merkleRoot", block.getMerkleRoot());

    List<BasicDBObject> txObjectList = new ArrayList<>();
    for (Transaction tx : block.getTransactions()) {
      BasicDBObject txObject = new BasicDBObject();
      txObject.put("transactionId", tx.getTransactionId());
      txObject.put("sender", CryptoUtil.getStringFromKey(tx.getSender()));
      txObject.put("recipient", CryptoUtil.getStringFromKey(tx.getRecipient()));
      txObject.put("value", tx.getValue().toString());
      txObject.put("signature", tx.getSignature());
      txObjectList.add(txObject);
    }
    blockObject.put("transactions", txObjectList);
    collection.insertOne(blockObject);
  }

}
