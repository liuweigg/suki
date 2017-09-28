package com.ccs.trolls.suki.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class ZhclpfPk implements Serializable {
  private String chdrcoy;

  private String chdrnum;

  private long fupno;

  private long hxclseqno;

  public ZhclpfPk() {}

  /* TODO
  public boolean equals(Object obj) {
  }
  */
}
