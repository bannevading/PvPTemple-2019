package server.pvptemple.commands.toggle;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.player.PlayerData;
import server.pvptemple.util.finalutil.CC;

public class ToggleSpectatorsCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public ToggleSpectatorsCommand() {
      super("tsp");
      this.setDescription("Toggles a player's ability to spectate you on or off.");
      this.setUsage(CC.RED + "Usage: /tsp");
      this.setAliases(Arrays.asList("togglesp", "togglespec", "togglespectator", "togglespectators"));
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         Player player = (Player)sender;
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         playerData.setAllowingSpectators(!playerData.isAllowingSpectators());
         player.sendMessage(playerData.isAllowingSpectators() ? CC.GOLD + "Spectators have been " + CC.WHITE + "enabled" + CC.GOLD + "." : CC.GOLD + "Spectators have been " + CC.WHITE + "disabled" + CC.GOLD + ".");
         return true;
      }
   }
}
