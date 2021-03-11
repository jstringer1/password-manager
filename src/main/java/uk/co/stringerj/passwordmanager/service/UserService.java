package uk.co.stringerj.passwordmanager.service;

import static com.google.zxing.BarcodeFormat.QR_CODE;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import uk.co.stringerj.passwordmanager.dao.UserDao;
import uk.co.stringerj.passwordmanager.model.UserDetails;

@Service
public class UserService {

  private static final String QR_FORMAT = "otpauth://totp/PasswordManager?secret=%s";

  @Autowired private UserDao userDao;

  public UserDetails getUserDetails() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userDao
        .findById(username)
        .map(user -> new UserDetails(username, user.getSecret()))
        .orElse(new UserDetails("", ""));
  }

  public BufferedImage getQRCode() {
    try {
      BitMatrix qr =
          new QRCodeWriter()
              .encode(String.format(QR_FORMAT, getUserDetails().getSecret()), QR_CODE, 200, 200);
      return toBufferedImage(qr);
    } catch (WriterException e) {
      throw new RuntimeException(e);
    }
  }

  private BufferedImage toBufferedImage(BitMatrix qr) {
    BufferedImage image = new BufferedImage(qr.getWidth(), qr.getHeight(), TYPE_INT_RGB);
    for (int x = 0; x < qr.getWidth(); x++) {
      for (int y = 0; y < qr.getHeight(); y++) {
        image.setRGB(x, y, qr.get(x, y) ? 0 : -1);
      }
    }
    return image;
  }
}
