package com.blockchain.core;

import com.blockchain.props.PropertyReader;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Persistence {

  private static final String MONGO_DB_NAME = "blockchain";

  private static Persistence instance;
  MongoClient mongoClient;
  MongoDatabase mongoDatabase;
  PropertyReader propertyReader = new PropertyReader();

  private Persistence() {
    mongoClient = MongoClients.create(propertyReader.getMongoUrl());
    mongoDatabase = mongoClient.getDatabase(MONGO_DB_NAME);
  }

  public static Persistence getInstance() {
    if (instance == null) {
      instance = new Persistence();
    }
    return instance;
  }



}
