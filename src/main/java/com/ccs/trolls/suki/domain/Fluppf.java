package com.ccs.trolls.suki.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.Data;

@Entity
@IdClass(FluppfPk.class)
@Table(name = "CSC_FLUPPF")
@Data
public class Fluppf {
  @Id private String chdrcoy;

  @Id private String chdrnum;

  private String clamnum;

  private Timestamp datime;

  private String fupcode;

  @Id private long fupno;

  private int fupremdt;

  private String fupremk;

  private String fupstat;

  private String fuptype;

  private String jlife;

  @Column(name = "JOB_NAME")
  private String jobName;

  private String life;

  private String termid;

  private long tranno;

  @Column(name = "TRANSACTION_DATE")
  private int transactionDate;

  @Column(name = "TRANSACTION_TIME")
  private int transactionTime;

  private long user;

  @Column(name = "USER_PROFILE")
  private String userProfile;

  private String zautoind;

  public Fluppf() {}

  //原始数据 FUPREMDT 如 20010505
  public LocalDate getFupremdtDate() {
    int day = fupremdt % 100;
    int year = (int) ((fupremdt - fupremdt % 10000) / 10000);
    int month = (int) ((fupremdt - year * 10000 - day) / 100);
    return LocalDate.of(year, month, day);
  }

  //原始数据 TRANSACTION_DATE/TRANSACTION_TIME 如 10505/141748
  public OffsetDateTime getTransactionDateTime() {
    int day = transactionDate % 100;
    int year = 2000 + (int) ((transactionDate - transactionDate % 10000) / 10000);
    int month = (int) ((transactionDate - (year - 2000) * 10000 - day) / 100);
    int second = transactionTime % 100;
    int hour = (int) ((transactionTime - transactionTime % 10000) / 10000);
    int minute = (int) ((transactionTime - hour * 10000 - second) / 100);
    return OffsetDateTime.of(year, month, day, hour, minute, second, 0, ZoneOffset.ofHours(8));
  }
}
