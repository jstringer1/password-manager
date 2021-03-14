package uk.co.stringerj.passwordmanager.service;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;
import static org.apache.tomcat.util.codec.binary.Base64.encodeBase64String;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import uk.co.stringerj.passwordmanager.dao.CredentialsDao;
import uk.co.stringerj.passwordmanager.dao.UserDao;
import uk.co.stringerj.passwordmanager.dao.model.EncryptedCredentials;
import uk.co.stringerj.passwordmanager.dao.model.User;
import uk.co.stringerj.passwordmanager.mfa.TwoFactorAuthToken;
import uk.co.stringerj.passwordmanager.model.Credentials;
import uk.co.stringerj.passwordmanager.model.UserDetails;
import uk.co.stringerj.passwordmanager.service.SecretGenerator.Encoding;

@Service
public class UserService {

  @Autowired private UserDao userDao;
  @Autowired private CredentialsDao credentialsDao;
  @Autowired private SecretGenerator secretGenerator;
  @Autowired private QRCodeService qrCodeService;
  @Autowired private RNG rng;

  public UserDetails getUserDetails() {
    return userDao
        .findById(getUsername())
        .map(user -> new UserDetails(user.getUsername(), user.getSecret()))
        .orElse(new UserDetails("", ""));
  }

  public BufferedImage getQRCode() {
    return qrCodeService.getQrCode(getUserDetails().getSecret(), 200, 200);
  }

  public void saveCredentials(Credentials credentials) {
    byte[] iv = randomIV();
    String data = encrypt(credentials, iv);
    credentialsDao.save(new EncryptedCredentials(getUsername(), data, encodeBase64String(iv)));
  }

  public List<Credentials> getCredentials() {
    List<Credentials> result = new ArrayList<>();
    credentialsDao
        .findAllByUsername(getUsername())
        .forEach(creds -> result.add(decrypt(creds.getData(), creds.getIv())));
    return result;
  }

  public void resetSecret() {
    userDao.findById(getUsername()).ifPresent(this::resetSecret);
  }

  private void resetSecret(User user) {
    user.setSecret(secretGenerator.generateSecret(Encoding.BASE32_SECRET, 20));
    userDao.save(user);
  }

  private String encrypt(Credentials credentials, byte[] iv) {
    try {
      Cipher cipher = cipher(ENCRYPT_MODE, getKey(), iv);
      return encodeBase64String(cipher.doFinal(serialise(credentials)));
    } catch (IOException | GeneralSecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  private Credentials decrypt(String credentials, String iv) {
    try {
      Cipher cipher = cipher(DECRYPT_MODE, getKey(), decodeBase64(iv));
      return deserialise(cipher.doFinal(decodeBase64(credentials)));
    } catch (IOException | ClassNotFoundException | GeneralSecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  private Cipher cipher(int mode, byte[] key, byte[] iv) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(mode, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
    return cipher;
  }

  private byte[] serialise(Credentials credentials) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new ObjectOutputStream(out).writeObject(credentials);
    return out.toByteArray();
  }

  private Credentials deserialise(byte[] credentials) throws ClassNotFoundException, IOException {
    return (Credentials) new ObjectInputStream(new ByteArrayInputStream(credentials)).readObject();
  }

  private String getUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  private byte[] getKey() {
    return ((TwoFactorAuthToken.Credentials)
            SecurityContextHolder.getContext().getAuthentication().getCredentials())
        .getKey();
  }

  private byte[] randomIV() {
    byte[] iv = new byte[16];
    for (int offset = 0; offset < 16; offset += 4) {
      toBytes(rng.generateInt(), iv, offset);
    }
    return iv;
  }

  private void toBytes(int value, byte[] data, int offset) {
    data[offset] = (byte) (value & 0xFF);
    data[offset + 1] = (byte) ((value >>> 8) & 0xFF);
    data[offset + 2] = (byte) ((value >>> 16) & 0xFF);
    data[offset + 3] = (byte) ((value >>> 24) & 0xFF);
  }
}
