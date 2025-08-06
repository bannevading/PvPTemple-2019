package server.pvptemple.commands.toggle;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.player.PlayerData;
import server.pvptemple.util.finalutil.CC;

public class ToggleScoreboardCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public ToggleScoreboardCommand() {
      super("tsb");
      this.setDescription("Toggles a player's ability to see the sidebar.");
      this.setUsage(CC.RED + "Usage: /tsb");
      this.setAliases(Arrays.asList("togglescore", "togglescoreboard", "toggleside", "togglesidebar"));
   }

   public boolean execute(CommandSender sender, String s, String[] strings) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         Player player = (Player)sender;
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         playerData.setScoreboardEnabled(!playerData.isScoreboardEnabled());
         player.sendMessage(playerData.isScoreboardEnabled() ? CC.GOLD + "Your scoreboard has been " + CC.WHITE + "enabled" + CC.GOLD + "." : CC.GOLD + "Your scoreboard has been " + CC.WHITE + "disabled" + CC.GOLD + ".");
         return true;
      }
   }
}
