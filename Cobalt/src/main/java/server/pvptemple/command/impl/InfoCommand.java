package server.pvptemple.command.impl;

import org.bukkit.entity.Player;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.Color;

public class InfoCommand implements CommandHandler {
   @Command(
      name = {"teamspeak", "ts", "teams", "tspeak"},
      rank = Rank.NORMAL
   )
   public void teamspeak(Player player) {
      player.sendMessage(Color.translate("&eTeamspeak ip is &6ts.pvptemple.com"));
   }

   @Command(
      name = {"discord"},
      rank = Rank.NORMAL
   )
   public void discord(Player player) {
      player.sendMessage(Color.translate("&eDiscord link is &6pvptemple.com/discord"));
   }

   @Command(
      name = {"store", "shop", "donate"},
      rank = Rank.NORMAL
   )
   public void store(Player player) {
      player.sendMessage(Color.translate("&eShop link is &6shop.pvptemple.com"));
   }
}
