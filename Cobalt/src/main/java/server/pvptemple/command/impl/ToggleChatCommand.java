package server.pvptemple.command.impl;

import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class ToggleChatCommand implements CommandHandler {
   @Command(
      name = {"togglechat", "tgc", "globalchat"},
      rank = Rank.NORMAL,
      description = "Toggle chat to all messages except staff messages"
   )
   public void toggleChat(Mineman mineman) {
      mineman.setChatEnabled(!mineman.isChatEnabled());
      mineman.getPlayer().sendMessage(CC.GREEN + "You can " + (mineman.isChatEnabled() ? "now" : "no longer") + " see public chat.");
   }
}
