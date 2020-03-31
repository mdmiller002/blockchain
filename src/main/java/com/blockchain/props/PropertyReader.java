package com.blockchain.props;

import java.util.Properties;

public class PropertyReader {

  private Properties properties = new Properties();

  public PropertyReader() {
    setMongoUrl();
  }

  public String getMongoUrl() {
    return properties.getProperty(PropertyKeys.MONGO_URL);
  }

  public void setMongoUrl() {
    properties.setProperty(PropertyKeys.MONGO_URL, "mongodb://localhost:27017");
  }

}
