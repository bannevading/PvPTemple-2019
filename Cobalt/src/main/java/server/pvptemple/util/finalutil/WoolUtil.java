package server.pvptemple.util.finalutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import org.bukkit.ChatColor;
import server.pvptemple.util.finalutil.CC;

public final class WoolUtil {
   private static final ArrayList<ChatColor> woolColors;
   private static final ArrayList<String> woolCCs;

   public static int convertChatColorToWoolData(ChatColor color) {
      if (color == ChatColor.DARK_RED) {
         color = ChatColor.RED;
      }

      return woolColors.indexOf(color);
   }

   public static int convertCCToWoolData(String color) {
      if (Objects.equals(color, server.pvptemple.util.finalutil.CC.DARK_RED)) {
         color = server.pvptemple.util.finalutil.CC.RED;
      }

      return woolCCs.indexOf(color);
   }

   static {
      woolColors = new ArrayList(Arrays.asList(ChatColor.WHITE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE, ChatColor.BLUE, ChatColor.BLACK, ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.BLACK));
      woolCCs = new ArrayList(Arrays.asList(server.pvptemple.util.finalutil.CC.WHITE, server.pvptemple.util.finalutil.CC.GOLD, server.pvptemple.util.finalutil.CC.LIGHT_PURPLE, server.pvptemple.util.finalutil.CC.AQUA, server.pvptemple.util.finalutil.CC.YELLOW, server.pvptemple.util.finalutil.CC.GREEN, server.pvptemple.util.finalutil.CC.LIGHT_PURPLE, server.pvptemple.util.finalutil.CC.DARK_GRAY, server.pvptemple.util.finalutil.CC.GRAY, server.pvptemple.util.finalutil.CC.DARK_AQUA, server.pvptemple.util.finalutil.CC.DARK_PURPLE, server.pvptemple.util.finalutil.CC.BLUE, server.pvptemple.util.finalutil.CC.BLACK, server.pvptemple.util.finalutil.CC.DARK_GREEN, server.pvptemple.util.finalutil.CC.RED, CC.BLACK));
   }
}
