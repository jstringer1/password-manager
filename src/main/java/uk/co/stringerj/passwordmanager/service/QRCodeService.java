package uk.co.stringerj.passwordmanager.service;

import static com.google.zxing.BarcodeFormat.QR_CODE;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.image.BufferedImage;

import org.springframework.stereotype.Service;

import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QRCodeService {

  private static final String QR_FORMAT = "otpauth://totp/PasswordManager?secret=%s";

  public BufferedImage getQrCode(String secret, int width, int height) {
    try {
      BitMatrix qr =
          new QRCodeWriter().encode(String.format(QR_FORMAT, secret), QR_CODE, width, height);
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
