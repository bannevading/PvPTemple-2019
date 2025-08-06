package server.pvptemple.commands.toggle;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.player.PlayerData;
import server.pvptemple.util.finalutil.CC;

public class ToggleDuelCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public ToggleDuelCommand() {
      super("tdr");
      this.setDescription("Toggles a player's duel requests on or off.");
      this.setUsage(CC.RED + "Usage: /tdr");
      this.setAliases(Arrays.asList("toggleduel", "toggleduels", "td"));
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         Player player = (Player)sender;
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         playerData.setAcceptingDuels(!playerData.isAcceptingDuels());
         player.sendMessage(playerData.isAcceptingDuels() ? CC.GOLD + "Duel requests are now " + CC.WHITE + "enabled" + CC.GOLD + "." : CC.GOLD + "Duel requests are now " + CC.WHITE + "disabled" + CC.GOLD + ".");
         return true;
      }
   }
}
