package server.pvptemple.commands.toggle;

import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.util.tab.TabManager;

public class ToggleTabCommand extends Command {
   public ToggleTabCommand() {
      super("toggletab");
      this.setAliases(Arrays.asList("tab", "ttab", "tabt", "tb"));
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (sender instanceof ConsoleCommandSender) {
         sender.sendMessage("Only players can perform this command.");
         return false;
      } else {
         Player player = (Player)sender;
         TabManager.toggleTab(player);
         return false;
      }
   }
}
