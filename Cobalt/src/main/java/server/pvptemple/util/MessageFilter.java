package server.pvptemple.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import server.pvptemple.CorePlugin;

public class MessageFilter {
   private static final Pattern URL_REGEX = Pattern.compile("^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
   private static final Pattern IP_REGEX = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
   private static final List<String> LINK_WHITELIST = Arrays.asList("pvptemple.us", "sa.pvptemple.com", "pvptemple.com", "pvptemple.eu", "ts.pvptemple.com", "shop.pvptemple.com", "youtube.com", "youtu.be", "discord.gg", "twitter.com", "prnt.sc", "gyazo.com", "imgur.com");

   public static boolean shouldFilter(String message) {
      String msg = message.toLowerCase().replace("3", "e").replace("1", "i").replace("!", "i").replace("@", "a").replace("7", "t").replace("0", "o").replace("5", "s").replace("8", "b").replaceAll("\\p{Punct}|\\d", "").trim();
      String[] words = msg.trim().split(" ");

      for(String word : words) {
         for(String filteredWord : CorePlugin.getInstance().getFilteredWords()) {
            if (word.equalsIgnoreCase(filteredWord)) {
               return true;
            }
         }
      }

      for(String word : message.replace("(dot)", ".").replace("[dot]", ".").trim().split(" ")) {
         boolean continueIt = false;

         for(String phrase : LINK_WHITELIST) {
            if (word.toLowerCase().contains(phrase)) {
               continueIt = true;
               break;
            }
         }

         if (!continueIt) {
            Matcher matcher = IP_REGEX.matcher(word);
            if (matcher.matches()) {
               return true;
            }

            matcher = URL_REGEX.matcher(word);
            if (matcher.matches()) {
               return true;
            }
         }
      }

      Stream<String> var10000 = CorePlugin.getInstance().getFilteredPhrases().stream();
      Optional<String> optional = var10000.filter(msg::contains).findFirst();
      return optional.isPresent();
   }
}
