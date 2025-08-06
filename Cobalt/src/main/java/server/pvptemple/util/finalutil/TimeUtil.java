package server.pvptemple.util.finalutil;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import server.pvptemple.util.Duration;

public final class TimeUtil {
   private TimeUtil() {
      throw new RuntimeException("Cannot instantiate a utility class.");
   }

   public static Timestamp addDuration(long duration) {
      return truncateTimestamp(new Timestamp(System.currentTimeMillis() + duration));
   }

   public static Timestamp truncateTimestamp(Timestamp timestamp) {
      if (timestamp.toLocalDateTime().getYear() > 2037) {
         timestamp.setYear(2037);
      }

      return timestamp;
   }

   public static Timestamp addDuration(Timestamp timestamp) {
      return truncateTimestamp(new Timestamp(System.currentTimeMillis() + timestamp.getTime()));
   }

   public static Timestamp fromMillis(long millis) {
      return new Timestamp(millis);
   }

   public static Timestamp getCurrentTimestamp() {
      return new Timestamp(System.currentTimeMillis());
   }

   public static long toMillis(String time) {
      if (time == null) {
         return -1L;
      } else {
         String type = time.substring(time.length() - 1, time.length());
         Duration duration = Duration.getByName(type);
         if (duration == null) {
            return -1L;
         } else {
            int rawTime = Integer.parseInt(time.substring(0, time.length() - 1));
            return (long)rawTime * duration.getDuration();
         }
      }
   }

   public static String millisToRoundedTime(long millis) {
      ++millis;
      long seconds = millis / 1000L;
      long minutes = seconds / 60L;
      long hours = minutes / 60L;
      long days = hours / 24L;
      long weeks = days / 7L;
      long months = weeks / 4L;
      long years = months / 12L;
      if (years > 0L) {
         return years + " year" + (years == 1L ? "" : "s");
      } else if (months > 0L) {
         return months + " month" + (months == 1L ? "" : "s");
      } else if (weeks > 0L) {
         return weeks + " week" + (weeks == 1L ? "" : "s");
      } else if (days > 0L) {
         return days + " day" + (days == 1L ? "" : "s");
      } else if (hours > 0L) {
         return hours + " hour" + (hours == 1L ? "" : "s");
      } else {
         return minutes > 0L ? minutes + " minute" + (minutes == 1L ? "" : "s") : seconds + " second" + (seconds == 1L ? "" : "s");
      }
   }

   public static long parseTime(String time) {
      long totalTime = 0L;
      boolean found = false;
      Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);

      while(matcher.find()) {
         String s = matcher.group();
         Long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
         switch (s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1]) {
            case "s":
               totalTime += value;
               found = true;
               break;
            case "m":
               totalTime += value * 60L;
               found = true;
               break;
            case "h":
               totalTime += value * 60L * 60L;
               found = true;
               break;
            case "d":
               totalTime += value * 60L * 60L * 24L;
               found = true;
               break;
            case "w":
               totalTime += value * 60L * 60L * 24L * 7L;
               found = true;
               break;
            case "M":
               totalTime += value * 60L * 60L * 24L * 30L;
               found = true;
               break;
            case "y":
               totalTime += value * 60L * 60L * 24L * 365L;
               found = true;
         }
      }

      return !found ? -1L : totalTime * 1000L;
   }
}
