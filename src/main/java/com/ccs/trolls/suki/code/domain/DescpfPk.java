package com.ccs.trolls.suki.code.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class DescpfPk implements Serializable {
  private String descpfx;

  private String desccoy;

  private String desctabl;

  private String descitem;

  private String language;

  public DescpfPk() {}

  public DescpfPk(String desccoy, String descitem) {
    this("IT", desccoy, "TABLE", descitem);
  }

  public DescpfPk(String desccoy, String desctabl, String descitem) {
    this("IT", desccoy, desctabl, descitem);
  }

  public DescpfPk(String descpfx, String desccoy, String desctabl, String descitem) {
    this(descpfx, desccoy, desctabl, descitem, "S");
  }

  public DescpfPk(
      String descpfx, String desccoy, String desctabl, String descitem, String language) {
    this.descpfx = descpfx;
    this.desccoy = desccoy;
    this.desctabl = desctabl;
    this.descitem = descitem;
    this.language = language;
  }
}
