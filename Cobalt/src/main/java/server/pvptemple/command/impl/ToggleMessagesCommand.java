package server.pvptemple.command.impl;

import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class ToggleMessagesCommand implements CommandHandler {
   @Command(
      name = {"togglemessages", "tpm", "toggleprivatemessages"},
      rank = Rank.NORMAL,
      description = "Toggle private messages"
   )
   public void toggleMessages(Mineman mineman) {
      mineman.setCanSeeMessages(!mineman.isCanSeeMessages());
      mineman.getPlayer().sendMessage(CC.GREEN + "You can " + (mineman.isCanSeeMessages() ? "now" : "no longer") + " see private messages.");
   }
}
