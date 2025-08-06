package server.pvptemple.commands.warp;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.command.BaseCommand;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class WarpCommand extends BaseCommand {
   private final Carbon plugin = Carbon.getInstance();

   public WarpCommand() {
      super("spawn");
      this.setPlayerOnly(true);
      this.setAliases(Arrays.asList("ffa", "eventworld"));
   }

   public boolean onExecute(CommandSender commandSender, String label, String[] args) {
      Player player = (Player)commandSender;
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      if (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.FFA) {
         player.sendMessage(CC.RED + "You can't do this in your current state.");
         return true;
      } else {
         switch (label.toLowerCase()) {
            case "spawn":
               this.plugin.getPlayerManager().sendToSpawnAndReset(player);
            case "ffa":
            default:
               break;
            case "eventworld":
               if (!PlayerUtil.testPermission(player, Rank.ADMIN)) {
                  return false;
               }

               player.teleport(new Location(Bukkit.getWorld("event"), (double)0.0F, (double)150.0F, (double)0.0F));
         }

         return true;
      }
   }
}
