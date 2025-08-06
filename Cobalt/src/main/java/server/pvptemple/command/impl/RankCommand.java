package server.pvptemple.command.impl;

import com.google.gson.JsonObject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.impl.PlayerRequest;
import server.pvptemple.event.player.RankChangeEvent;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;
import server.pvptemple.util.finalutil.TimeUtil;

public class RankCommand implements CommandHandler {
   @Command(
      name = {"group"},
      rank = Rank.ADMIN,
      description = "Set the rank of a player."
   )
   public void rank(CommandSender sender, @Param(name = "target") String target, @Param(name = "rank") Rank rank, @Param(name = "duration",defaultTo = "perm") String time) {
      if (PlayerUtil.testPermission(sender, rank)) {
         if (sender instanceof Player) {
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(((Player)sender).getUniqueId());
            if (mineman.hasRank(Rank.ADMIN) && !mineman.hasRank(Rank.PLATFORMADMIN) && rank.hasRank(Rank.TRAINEE)) {
               sender.sendMessage(CC.RED + "You cannot set staff ranks.");
               return;
            }
         }

         long duration = TimeUtil.toMillis(time.equals("perm") ? null : time);
         int giverId = sender instanceof Player ? CorePlugin.getInstance().getPlayerManager().getPlayer(((Player)sender).getUniqueId()).getId() : -1;
         CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new PlayerRequest.RankUpdateRequest(target, rank, duration, giverId), (element) -> {
            JsonObject data = element.getAsJsonObject();
            switch (data.get("response").getAsString()) {
               case "success":
                  CorePlugin.getInstance().getServer().getLogger().info(sender.getName() + " updated " + target + " permissive rank to " + rank.getName());
                  sender.sendMessage(CC.GREEN + "You have given " + target + " the " + rank.getColor() + rank.getName() + CC.GREEN + " rank.");
                  Player player = CorePlugin.getInstance().getServer().getPlayer(target);
                  if (player != null) {
                     Mineman targetMineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
                     if (targetMineman != null) {
                        CorePlugin.getInstance().getServer().getPluginManager().callEvent(new RankChangeEvent(targetMineman, targetMineman.getRank(), rank));
                        targetMineman.setRank(rank);
                     }
                  }
                  break;
               case "player-not-found":
                  sender.sendMessage(CC.RED + "Failed to find that player.");
            }

         });
      }
   }
}
