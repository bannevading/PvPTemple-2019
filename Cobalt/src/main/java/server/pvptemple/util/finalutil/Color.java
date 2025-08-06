package server.pvptemple.util.finalutil;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;

public class Color {
   public static String translate(String text) {
      return ChatColor.translateAlternateColorCodes('&', text);
   }

   public static List<String> translate(List<String> text) {
      return (List)text.stream().map(Color::translate).collect(Collectors.toList());
   }
}
