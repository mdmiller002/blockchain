package com.blockchain.core;

import com.blockchain.utils.CryptoUtil;

import java.math.BigDecimal;
import java.security.PublicKey;

public class TransactionOutput {
  private PublicKey recipient;
  private String id;
  private BigDecimal value;
  private String parentTransactionId;

  public TransactionOutput(PublicKey recipient, BigDecimal value, String parentTransactionId) {
    this.recipient = recipient;
    this.value = value;
    this.parentTransactionId = parentTransactionId;
    this.id = CryptoUtil.sha256(CryptoUtil.getStringFromKey(recipient) + value.toString() + parentTransactionId);
  }

  public boolean isMine(PublicKey publicKey) {
    return publicKey == recipient;
  }

  public BigDecimal getValue() {
    return value;
  }

  public String getParentTransactionId() {
    return parentTransactionId;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TransactionOutput)) {
      return false;
    }
    TransactionOutput other = (TransactionOutput) o;
    return other.getId().equals(this.getId()) && other.getValue().equals(this.getValue());
  }
}
