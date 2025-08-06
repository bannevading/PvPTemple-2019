package server.pvptemple.util.finalutil;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.FontRenderer;

public final class StringUtil {
   public static final String NO_PERMISSION;
   public static final String IP_BAN;
   public static final String IP_BAN_OTHER;
   public static final String PERMANENT_BAN;
   public static final String TEMPORARY_BAN;
   public static final String PERMANENT_MUTE;
   public static final String TEMPORARY_MUTE;
   public static final String BLACKLIST;
   public static final String COMMAND_COOLDOWN;
   public static final String SLOW_CHAT;
   public static final String PLAYER_ONLY;
   public static final String CHAT_COOLDOWN;
   public static final String PLAYER_NOT_FOUND;
   public static final String LOAD_ERROR;
   public static final String SPLIT_PATTERN;
   public static final FontRenderer FONT_RENDERER;
   private static final String MAX_LENGTH = "11111111111111111111111111111111111111111111111111111";
   private static final List<String> VOWELS;

   private StringUtil() {
      throw new RuntimeException("Cannot instantiate a utility class.");
   }

   public static String[] formatPrivateMessage(String from, String to, String message) {
      String toMessage = server.pvptemple.util.finalutil.CC.GRAY + "(To " + to + server.pvptemple.util.finalutil.CC.GRAY + ") " + message;
      String fromMessage = server.pvptemple.util.finalutil.CC.GRAY + "(From " + from + server.pvptemple.util.finalutil.CC.GRAY + ") " + message;
      return new String[]{toMessage, fromMessage};
   }

   public static String getBorderLine() {
      int chatWidth = FONT_RENDERER.getWidth("11111111111111111111111111111111111111111111111111111") / 10 * 9;
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < 100; ++i) {
         sb.append("-");
         if (FONT_RENDERER.getWidth(sb.toString()) >= chatWidth) {
            break;
         }
      }

      return server.pvptemple.util.finalutil.CC.SECONDARY + server.pvptemple.util.finalutil.CC.S + sb.toString();
   }

   public static String center(String string) {
      StringBuilder preColors;
      for(preColors = new StringBuilder(); string.startsWith("§"); string = string.substring(2, string.length())) {
         preColors.append(string.substring(0, 2));
      }

      int width = FONT_RENDERER.getWidth(string);
      int chatWidth = FONT_RENDERER.getWidth("11111111111111111111111111111111111111111111111111111");
      if (width == chatWidth) {
         return string;
      } else if (width > chatWidth) {
         String[] words = string.split(" ");
         if (words.length == 1) {
            return string;
         } else {
            StringBuilder sb = new StringBuilder();
            int total = 0;

            for(String word : words) {
               int wordWidth = FONT_RENDERER.getWidth(word + " ");
               if (total + wordWidth > chatWidth) {
                  sb.append("\n");
                  total = 0;
               }

               total += wordWidth;
               sb.append(word).append(" ");
            }

            return center(preColors + sb.toString().trim());
         }
      } else {
         StringBuilder sb = new StringBuilder();
         int diff = chatWidth - width;
         diff /= 3;

         for(int i = 0; i < 100; ++i) {
            sb.append(" ");
            if (FONT_RENDERER.getWidth(sb.toString()) >= diff) {
               break;
            }
         }

         sb.append(string);
         return preColors + sb.toString();
      }
   }

   public static String toNiceString(String string) {
      string = ChatColor.stripColor(string).replace('_', ' ').toLowerCase();
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < string.toCharArray().length; ++i) {
         char c = string.toCharArray()[i];
         if (i > 0) {
            char prev = string.toCharArray()[i - 1];
            if ((prev == ' ' || prev == '[' || prev == '(') && (i == string.toCharArray().length - 1 || c != 'x' || !Character.isDigit(string.toCharArray()[i + 1]))) {
               c = Character.toUpperCase(c);
            }
         } else if (c != 'x' || !Character.isDigit(string.toCharArray()[i + 1])) {
            c = Character.toUpperCase(c);
         }

         sb.append(c);
      }

      return sb.toString();
   }

   public static String buildMessage(String[] args, int start) {
      return start >= args.length ? "" : ChatColor.stripColor(String.join(" ", (CharSequence[])Arrays.copyOfRange(args, start, args.length)));
   }

   public static String getFirstSplit(String s) {
      return s.split(SPLIT_PATTERN)[0];
   }

   public static String getAOrAn(String input) {
      return VOWELS.contains(input.substring(0, 1).toLowerCase()) ? "an" : "a";
   }

   static {
      NO_PERMISSION = server.pvptemple.util.finalutil.CC.RED + "Insufficient permissions.";
      IP_BAN = server.pvptemple.util.finalutil.CC.RED + "Your account has been suspended from the PvPTemple Network.\n\n" + server.pvptemple.util.finalutil.CC.RED + "To appeal, visit www.PvPTemple.com/appeal.";
      IP_BAN_OTHER = server.pvptemple.util.finalutil.CC.RED + "Your account has been banned from the PvPTemple Network.\n" + server.pvptemple.util.finalutil.CC.RED + "This punishment is in relation to %s.\n" + server.pvptemple.util.finalutil.CC.RED + "To appeal, visit www.PvPTemple.com/appeal";
      PERMANENT_BAN = server.pvptemple.util.finalutil.CC.RED + "Your account has been suspended from the PvPTemple Network.\n\n" + server.pvptemple.util.finalutil.CC.RED + "To appeal, visit www.PvPTemple.com/appeal.";
      TEMPORARY_BAN = server.pvptemple.util.finalutil.CC.RED + "Your account has been temporarily suspended from the PvPTemple Network for %s.\n" + server.pvptemple.util.finalutil.CC.RED + "To appeal, visit www.PvPTemple.com/appeal.";
      PERMANENT_MUTE = server.pvptemple.util.finalutil.CC.RED + "You are permanently muted.";
      TEMPORARY_MUTE = server.pvptemple.util.finalutil.CC.RED + "You are temporarily muted for %s.";
      BLACKLIST = server.pvptemple.util.finalutil.CC.RED + "Your account has been blacklisted from the PvPTemple Network.\n\n" + server.pvptemple.util.finalutil.CC.RED + "This punishment cannot be appealed.";
      COMMAND_COOLDOWN = server.pvptemple.util.finalutil.CC.RED + "You cannot use command that fast.";
      SLOW_CHAT = server.pvptemple.util.finalutil.CC.RED + "Public chat is currently slowed.\nYou may speak again in 5 seconds.";
      PLAYER_ONLY = server.pvptemple.util.finalutil.CC.RED + "Only players can use this command.";
      CHAT_COOLDOWN = server.pvptemple.util.finalutil.CC.RED + "You cannot chat that fast.";
      PLAYER_NOT_FOUND = server.pvptemple.util.finalutil.CC.RED + "Failed to find that player.";
      LOAD_ERROR = CC.RED + "An error occurred while loading your player data. Try again later.";
      SPLIT_PATTERN = Pattern.compile("\\s").pattern();
      FONT_RENDERER = new FontRenderer();
      VOWELS = Arrays.asList("a", "e", "u", "i", "o");
   }
}
