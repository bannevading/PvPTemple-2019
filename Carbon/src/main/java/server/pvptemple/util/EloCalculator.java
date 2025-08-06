package server.pvptemple.util;

public class EloCalculator {
   public static double[] getEstimations(double rankingA, double rankingB) {
      double[] ret = new double[2];
      double estA = (double)1.0F / ((double)1.0F + Math.pow((double)10.0F, (rankingB - rankingA) / (double)400.0F));
      double estB = (double)1.0F / ((double)1.0F + Math.pow((double)10.0F, (rankingA - rankingB) / (double)400.0F));
      ret[0] = estA;
      ret[1] = estB;
      return ret;
   }

   public static int getConstant(int ranking) {
      if (ranking < 1000) {
         return 32;
      } else {
         return ranking < 1401 ? 24 : 16;
      }
   }

   public static int[] getNewRankings(int rankingA, int rankingB, boolean victoryA) {
      int[] ret = new int[2];
      double[] ests = getEstimations((double)rankingA, (double)rankingB);
      int newRankA = (int)((double)rankingA + (double)getConstant(rankingA) * ((double)(victoryA ? 1 : 0) - ests[0]));
      ret[0] = Math.round((float)newRankA);
      ret[1] = Math.round((float)(rankingB - (newRankA - rankingA)));
      return ret;
   }
}
