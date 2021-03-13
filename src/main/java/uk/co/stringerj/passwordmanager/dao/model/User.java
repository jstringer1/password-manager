package uk.co.stringerj.passwordmanager.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USER")
public class User {

  @Id @Column private String username;
  @Column private String password;
  @Column private String secret;
  @Column private String role;

  public User() {}

  public User(String username, String password, String secret, String role) {
    this.username = username;
    this.password = password;
    this.secret = secret;
    this.role = role;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getSecret() {
    return secret;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
