package server.pvptemple.command.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class ClearEntitiesCommand implements CommandHandler {
   @Command(
      name = {"clearentities", "clearent", "cent"},
      rank = Rank.PLATFORMADMIN,
      description = "Clear all entities in the world"
   )
   public void clearEntitiesCommand(Player commandSender) {
      int entitiesCleared = 0;
      int entitiesIgnored = 0;
      int entitiesTotal = commandSender.getWorld().getEntities().size();

      for(Entity entity : commandSender.getWorld().getEntities()) {
         if (!(entity instanceof Player) && !(entity.getPassenger() instanceof Player)) {
            entity.remove();
            ++entitiesCleared;
         } else {
            ++entitiesIgnored;
         }
      }

      commandSender.sendMessage(CC.GREEN + "Cleared " + CC.B_GREEN + entitiesCleared + CC.GREEN + " of " + CC.B_GREEN + entitiesTotal + CC.GREEN + " in the world. There was " + CC.B_GREEN + entitiesIgnored + CC.GREEN + " ignored entit" + (entitiesIgnored == 1 ? "y" : "ies") + ".");
   }
}
