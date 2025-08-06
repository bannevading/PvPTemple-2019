package server.pvptemple.commands;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import server.pvptemple.util.finalutil.CC;

public class EloCommand extends Command {
   public EloCommand() {
      super("elo");
      this.setDescription("View a player's Elo.");
      this.setUsage(CC.RED + "Usage: /elo [player]");
      this.setAliases(Arrays.asList("stats", "lb", "leaderboard", "leaderboards"));
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      sender.sendMessage(CC.PRIMARY + "You can view leaderboards and player ELO at our website.");
      sender.sendMessage(CC.SECONDARY + "http://pvptemple.com/");
      return true;
   }
}
