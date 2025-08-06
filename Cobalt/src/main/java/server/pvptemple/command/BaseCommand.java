package server.pvptemple.command;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.PlayerUtil;
import server.pvptemple.util.finalutil.StringUtil;

public abstract class BaseCommand extends Command {
   private Rank requiredRank;
   private boolean playerOnly;

   public BaseCommand(String name) {
      super(name);
      this.requiredRank = Rank.NORMAL;
      this.playerOnly = false;
   }

   public BaseCommand(String name, String description, String usageMessage, List<String> aliases) {
      super(name, description, usageMessage, aliases);
      this.requiredRank = Rank.NORMAL;
      this.playerOnly = false;
   }

   public abstract boolean onExecute(CommandSender var1, String var2, String[] var3);

   public boolean execute(CommandSender commandSender, String label, String[] args) {
      if (this.playerOnly && !(commandSender instanceof Player)) {
         commandSender.sendMessage(StringUtil.PLAYER_ONLY);
         return true;
      } else {
         return !PlayerUtil.testPermission(commandSender, this.requiredRank) || this.onExecute(commandSender, label, args);
      }
   }

   public Rank getRequiredRank() {
      return this.requiredRank;
   }

   public boolean isPlayerOnly() {
      return this.playerOnly;
   }

   public void setRequiredRank(Rank requiredRank) {
      this.requiredRank = requiredRank;
   }

   public void setPlayerOnly(boolean playerOnly) {
      this.playerOnly = playerOnly;
   }
}
