package server.pvptemple.util;

public class EloUtil {
   private static final KFactor[] K_FACTORS = new KFactor[]{new KFactor(0, 1000, (double)25.0F), new KFactor(1001, 1400, (double)20.0F), new KFactor(1401, 1800, (double)15.0F), new KFactor(1801, 2200, (double)10.0F)};
   private static final int DEFAULT_K_FACTOR = 25;
   private static final int WIN = 1;
   private static final int LOSS = 0;

   public static int getNewRating(int rating, int opponentRating, boolean won) {
      return won ? getNewRating(rating, opponentRating, 1) : getNewRating(rating, opponentRating, 0);
   }

   public static int getNewRating(int rating, int opponentRating, int score) {
      double kFactor = getKFactor(rating);
      double expectedScore = getExpectedScore(rating, opponentRating);
      int newRating = calculateNewRating(rating, score, expectedScore, kFactor);
      if (score == 1 && newRating == rating) {
         ++newRating;
      }

      return newRating;
   }

   private static int calculateNewRating(int oldRating, int score, double expectedScore, double kFactor) {
      return oldRating + (int)(kFactor * ((double)score - expectedScore));
   }

   private static double getKFactor(int rating) {
      for(int i = 0; i < K_FACTORS.length; ++i) {
         if (rating >= K_FACTORS[i].getStartIndex() && rating <= K_FACTORS[i].getEndIndex()) {
            return K_FACTORS[i].getValue();
         }
      }

      return (double)25.0F;
   }

   private static double getExpectedScore(int rating, int opponentRating) {
      return (double)1.0F / ((double)1.0F + Math.pow((double)10.0F, (double)(opponentRating - rating) / (double)400.0F));
   }
}
