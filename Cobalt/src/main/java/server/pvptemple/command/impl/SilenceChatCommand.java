package server.pvptemple.command.impl;

import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.manager.MinemanManager;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class SilenceChatCommand implements CommandHandler {
   @Command(
      name = {"silencechat", "mutechat"},
      rank = Rank.TRAINEE,
      description = "Silence the chat."
   )
   public void onMuteChat(Player sender) {
      MinemanManager minemanManager = CorePlugin.getInstance().getPlayerManager();
      minemanManager.setChatSilenced(!minemanManager.isChatSilenced());
      CorePlugin.getInstance().getServer().broadcastMessage(minemanManager.isChatSilenced() ? CC.YELLOW + "Public chat has been muted by " + sender.getName() + "." : CC.YELLOW + "Public chat has been unmuted by " + sender.getName() + ".");
   }
}
