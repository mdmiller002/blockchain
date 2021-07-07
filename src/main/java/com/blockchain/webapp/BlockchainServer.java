package com.blockchain.webapp;

import com.blockchain.core.Ledger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BlockchainServer {

  public static void main(String[] args) {
    SpringApplication.run(BlockchainServer.class, args);
    Ledger ledger = Ledger.getInstance();
    
  }

}
