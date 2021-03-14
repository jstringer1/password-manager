package uk.co.stringerj.passwordmanager.service;

import static java.lang.management.ManagementFactory.getGarbageCollectorMXBeans;
import static java.util.stream.Collectors.summingLong;

import java.lang.management.GarbageCollectorMXBean;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.stereotype.Service;

/**
 * Just for fun a SHA1-PRNG. Takes a "seed" from SecureRandom along with the previous SHA1 and a
 * incrementing counter and data from a few other places, the seed is the checksummed using SHA1,
 * then takes XOR checksums of 5 byte blocks of the SHA1 checksum to reduce the 20 byte checksum to
 * 4 bytes and convert to a 32-bit integer.
 */
@Service
public class RNG {

  private final MessageDigest digest;
  private final SecureRandom secureRandom = new SecureRandom();

  private byte[] state = new byte[0];
  private long counter = Long.MIN_VALUE;

  public RNG() {
    try {
      digest = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public int generateInt(int exclusiveUpperBound) {
    long maxUnscaled = 0xFFFFFFFFL / exclusiveUpperBound * exclusiveUpperBound;
    long unscaled = 0;
    while ((unscaled = generateInt() & 0xFFFFFFFFL) >= maxUnscaled) ;
    return (int) (unscaled % exclusiveUpperBound);
  }

  public int generateInt() {
    byte[] hash = generateHash();
    return (xor(hash, 0, 5) & 0xFF)
        | ((xor(hash, 5, 5) & 0xFF) << 8)
        | ((xor(hash, 10, 5) & 0xFF) << 16)
        | ((xor(hash, 15, 5) & 0xFF) << 24);
  }

  private byte[] generateHash() {
    digest.update(state);
    digest.update(ByteBuffer.allocate(8).putLong(counter++).array());
    digest.update(secureRandom.generateSeed(20));
    digest.update(twoBytes(System.currentTimeMillis()));
    digest.update(twoBytes(Runtime.getRuntime().freeMemory()));
    digest.update(twoBytes(garbageCollectionStats()));
    return (state = digest.digest());
  }

  private byte xor(byte[] input, int offset, int count) {
    byte result = 0;
    for (int i = offset; i < offset + count; i++) {
      result ^= input[i];
    }
    return result;
  }

  private long garbageCollectionStats() {
    return getGarbageCollectorMXBeans()
        .stream()
        .collect(summingLong(GarbageCollectorMXBean::getCollectionTime));
  }

  private byte[] twoBytes(long value) {
    return new byte[] {(byte) (value & 0xFF), (byte) ((value >>> 8) & 0xFF)};
  }
}
