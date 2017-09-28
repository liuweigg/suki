package com.ccs.trolls.suki.code;

public enum CodeType {
  FOLLOWUP_CODE("T5661"),
  FOLLOWUP_TYPE("T5615"),
  FOLLOWUP_LETTYP("TH585"),
  FOLLOWUP_STATUS("TABLE");

  private String table;

  CodeType(String table) {
    this.table = table;
  }

  public String getTable() {
    return table;
  }
}
