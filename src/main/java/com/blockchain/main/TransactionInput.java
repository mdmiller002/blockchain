package com.blockchain.main;

public class TransactionInput {
   public String transactionOutputId;
   private TransactionOutput UTXO;

   public TransactionInput(String transactionOutputId) {
      this.transactionOutputId = transactionOutputId;
   }

   public TransactionOutput getUTXO() {
      return UTXO;
   }

   public void setUTXO(TransactionOutput UTXO) {
      this.UTXO = UTXO;
   }
}
