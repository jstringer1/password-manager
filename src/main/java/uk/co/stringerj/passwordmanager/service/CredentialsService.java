package uk.co.stringerj.passwordmanager.service;

import static java.util.stream.Collectors.toList;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static org.apache.tomcat.util.codec.binary.Base64.decodeBase64;
import static org.apache.tomcat.util.codec.binary.Base64.encodeBase64String;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.stringerj.passwordmanager.dao.CredentialsDao;
import uk.co.stringerj.passwordmanager.dao.model.EncryptedCredentials;
import uk.co.stringerj.passwordmanager.model.Credentials;

@Service
public class CredentialsService {
  @Autowired private CredentialsDao credentialsDao;
  @Autowired private RNG rng;

  public void saveCredentials(Credentials credentials, String username, byte[] key) {
    streamCredentials(username)
        .map(record -> decrypt(record, key))
        .filter(creds -> creds.matches(credentials))
        .findAny()
        .orElse(new CredentialsWithRecord(credentials, username))
        .updateRecordAndSave(credentials, key);
  }

  public List<Credentials> getCredentials(String username, byte[] key) {
    return streamCredentials(username)
        .map(record -> decrypt(record, key).credentials)
        .collect(toList());
  }

  private Stream<EncryptedCredentials> streamCredentials(String username) {
    List<EncryptedCredentials> credentials = new ArrayList<>();
    credentialsDao.findAllByUsername(username).forEach(credentials::add);
    return credentials.stream();
  }

  private String encrypt(Credentials credentials, byte[] key, byte[] iv) {
    try {
      Cipher cipher = cipher(ENCRYPT_MODE, key, iv);
      return encodeBase64String(cipher.doFinal(serialise(credentials)));
    } catch (IOException | GeneralSecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  private CredentialsWithRecord decrypt(EncryptedCredentials record, byte[] key) {
    return new CredentialsWithRecord(
        decrypt(record.getData(), key, decodeBase64(record.getIv())), record);
  }

  private Credentials decrypt(String credentials, byte[] key, byte[] iv) {
    try {
      Cipher cipher = cipher(DECRYPT_MODE, key, iv);
      return deserialise(cipher.doFinal(decodeBase64(credentials)));
    } catch (IOException | ClassNotFoundException | GeneralSecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  private byte[] serialise(Credentials credentials) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    new ObjectOutputStream(out).writeObject(credentials);
    return out.toByteArray();
  }

  private Credentials deserialise(byte[] credentials) throws ClassNotFoundException, IOException {
    return (Credentials) new ObjectInputStream(new ByteArrayInputStream(credentials)).readObject();
  }

  private Cipher cipher(int mode, byte[] key, byte[] iv) throws GeneralSecurityException {
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(mode, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
    return cipher;
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

  private class CredentialsWithRecord {
    private final Credentials credentials;
    private final EncryptedCredentials record;

    public CredentialsWithRecord(Credentials credentials, String username) {
      this(credentials, new EncryptedCredentials(username, "", ""));
    }

    public CredentialsWithRecord(Credentials credentials, EncryptedCredentials record) {
      this.credentials = credentials;
      this.record = record;
    }

    public boolean matches(Credentials other) {
      return credentials.getService().equals(other.getService())
          && credentials.getUsername().equals(other.getUsername());
    }

    public void updateRecordAndSave(Credentials credentials, byte[] key) {
      byte[] iv = randomIV();
      record.setData(encrypt(credentials, key, iv));
      record.setIv(encodeBase64String(iv));
      credentialsDao.save(record);
    }
  }
}
