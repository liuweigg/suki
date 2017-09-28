package com.ccs.trolls.suki.code.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import lombok.Data;

@Entity
@IdClass(DescpfPk.class)
@Table(name = "CSC_DESCPF")
@Data
public class Descpf {
  private Timestamp datime;

  @Id private String desccoy;

  @Id private String descitem;

  @Id private String descpfx;

  @Id private String desctabl;

  private String itemseq;

  @Column(name = "JOB_NAME")
  private String jobName;

  @Id private String language;

  private String longdesc;

  private String shortdesc;

  private String tranid;

  @Column(name = "USER_PROFILE")
  private String userProfile;

  public Descpf() {}
}
