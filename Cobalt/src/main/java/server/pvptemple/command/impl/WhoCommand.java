package server.pvptemple.command.impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import server.pvptemple.CorePlugin;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.PlayerList;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class WhoCommand implements CommandHandler {
   @Command(
      name = {"who", "list"},
      rank = Rank.NORMAL,
      description = "View all online players."
   )
   public void who(CommandSender sender) {
      StringBuilder builder = new StringBuilder();
      Rank[] ranks = (Rank[])Arrays.copyOf(Rank.RANKS, Rank.RANKS.length);
      ArrayUtils.reverse(ranks);
      Arrays.stream(ranks).forEach((rank) -> builder.append(rank.getColor()).append(rank.getName()).append(CC.WHITE).append(", "));
      builder.setCharAt(builder.length() - 2, '.');
      builder.append("\n");
      Stream<UUID> var10002 = CorePlugin.getInstance().getPlayerManager().getPlayers().keySet().stream().filter((uuid) -> Bukkit.getPlayer(uuid) != null);
      Server var10003 = CorePlugin.getInstance().getServer();
      List<String> players = (new PlayerList(var10002.map(var10003::getPlayer).collect(Collectors.toList()))).visibleRankSorted().asColoredNames();
      builder.append(CC.R).append("(").append(Bukkit.getOnlinePlayers().size()).append("/").append(CorePlugin.getInstance().getServer().getMaxPlayers()).append("): ").append(players.toString().replace("[", "").replace("]", ""));
      sender.sendMessage(builder.toString());
   }
}
