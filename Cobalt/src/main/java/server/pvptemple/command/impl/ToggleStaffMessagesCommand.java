package server.pvptemple.command.impl;

import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class ToggleStaffMessagesCommand implements CommandHandler {
   @Command(
      name = {"togglestaffmessages", "tsm"},
      rank = Rank.TRAINEE,
      description = "Disable appearance of staff messages."
   )
   public void toggleStaffMessages(Mineman player) {
      player.setCanSeeStaffMessages(!player.isCanSeeStaffMessages());
      player.getPlayer().sendMessage(player.isCanSeeStaffMessages() ? CC.GREEN + "You can now see staff messages." : CC.RED + "You can no longer see staff messages.");
   }
}
