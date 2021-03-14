package uk.co.stringerj.passwordmanager.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CREDENTIALS")
public class EncryptedCredentials {
  @Id @GeneratedValue @Column long id;
  @Column private String username;
  @Column private String data;
  @Column private String iv;

  public EncryptedCredentials() {}

  public EncryptedCredentials(String username, String data, String iv) {
    this.username = username;
    this.data = data;
    this.iv = iv;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getIv() {
    return iv;
  }

  public void setIv(String iv) {
    this.iv = iv;
  }
}
