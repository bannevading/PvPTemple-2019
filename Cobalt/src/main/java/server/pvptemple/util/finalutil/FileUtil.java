package server.pvptemple.util.finalutil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public final class FileUtil {
   public static byte[] createChecksum(String filename) throws Exception {
      InputStream fis = new FileInputStream(filename);
      byte[] buffer = new byte[1024];
      MessageDigest complete = MessageDigest.getInstance("MD5");

      int numRead;
      do {
         numRead = fis.read(buffer);
         if (numRead > 0) {
            complete.update(buffer, 0, numRead);
         }
      } while(numRead != -1);

      fis.close();
      return complete.digest();
   }

   public static String getMD5Checksum(String filename) throws Exception {
      byte[] b = createChecksum(filename);
      StringBuilder result = new StringBuilder();

      for(byte aB : b) {
         result.append(Integer.toString((aB & 255) + 256, 16).substring(1));
      }

      return result.toString();
   }
}
