package com.blockchain.main;

import com.blockchain.crypto.Hashing;
import com.blockchain.utils.StringUtil;

import java.security.PublicKey;

public class TransactionOutput {
   private PublicKey recipient;
   private String id;
   private float value;
   private String parentTransactionId;

   public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
      this.recipient = recipient;
      this.value = value;
      this.parentTransactionId = parentTransactionId;
      this.id = Hashing.sha256(StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
   }

   public boolean isMine(PublicKey publicKey) {
      return publicKey == recipient;
   }

   public float getValue() {
      return value;
   }

   public String getParentTransactionId() {
      return parentTransactionId;
   }

   public String getId() {
      return id;
   }
}
