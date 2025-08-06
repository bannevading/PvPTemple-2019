package server.pvptemple.util.finalutil;

public final class MathUtil {
   private MathUtil() {
      throw new RuntimeException("Cannot instantiate a utility class.");
   }

   public static double roundOff(double x, int places) {
      double pow = Math.pow((double)10.0F, (double)places);
      return (double)Math.round(x * pow) / pow;
   }
}
