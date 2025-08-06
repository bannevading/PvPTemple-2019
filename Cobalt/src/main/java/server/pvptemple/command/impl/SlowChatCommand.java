package server.pvptemple.command.impl;

import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.manager.MinemanManager;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Text;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class SlowChatCommand implements CommandHandler {
   @Command(
      name = {"slowchat"},
      rank = Rank.TRAINEE,
      description = "Slow down the chat."
   )
   public void slowChat(Player sender, @Text(name = "time") String text) {
      try {
         int time = Integer.parseInt(text);
         MinemanManager minemanManager = CorePlugin.getInstance().getPlayerManager();
         minemanManager.setChatSlowDownTime((long)time * 1000L);
         PlayerUtil.messageRank(minemanManager.getChatSlowDownTime() > 0L ? CC.YELLOW + "Public chat has been slowed for " + time + " seconds." : CC.YELLOW + "Public chat is no longer slowed.");
      } catch (NumberFormatException var5) {
         sender.sendMessage(CC.RED + "Invalid number.");
      }

   }
}
