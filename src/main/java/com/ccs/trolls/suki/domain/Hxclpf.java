package com.ccs.trolls.suki.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import lombok.Data;

@Entity
@IdClass(HxclpfPk.class)
@Table(name = "CSC_HXCLPF")
@Data
public class Hxclpf {
  @Id private String chdrcoy;

  @Id private String chdrnum;

  private Timestamp datime;

  private String fupcode;

  @Id private long fupno;

  private String hxcllettyp;

  private String hxclnote01;

  private String hxclnote02;

  private String hxclnote03;

  private String hxclnote04;

  private String hxclnote05;

  private String hxclnote06;

  @Id private long hxclseqno;

  @Column(name = "JOB_NAME")
  private String jobName;

  private long user;

  @Column(name = "USER_PROFILE")
  private String userProfile;

  public Hxclpf() {}

  public String getHxclnote() {
    return StringUtils.trimWhitespace(hxclnote01)
        + StringUtils.trimWhitespace(hxclnote02)
        + StringUtils.trimWhitespace(hxclnote03)
        + StringUtils.trimWhitespace(hxclnote04)
        + StringUtils.trimWhitespace(hxclnote05)
        + StringUtils.trimWhitespace(hxclnote06);
  }
}
