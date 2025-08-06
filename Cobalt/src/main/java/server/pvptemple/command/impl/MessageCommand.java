package server.pvptemple.command.impl;

import java.sql.Timestamp;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.event.player.PrivateMessageEvent;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.MessageFilter;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.Text;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.PlayerUtil;
import server.pvptemple.util.finalutil.StringUtil;
import server.pvptemple.util.finalutil.TimeUtil;

public class MessageCommand implements CommandHandler {
   @Command(
      name = {"msg", "tell", "w", "m", "message"},
      rank = Rank.NORMAL,
      description = "Message a player."
   )
   public void message(Player player, @Param(name = "player") String targetName, @Text(name = "message") String message) {
      Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
      if (mineman.isBanned()) {
         player.sendMessage(CC.RED + "You cannot speak while you are banned.");
      } else {
         if (mineman.isMuted()) {
            if (mineman.getMuteTime() == null || System.currentTimeMillis() - mineman.getMuteTime().getTime() <= 0L) {
               if (mineman.getMuteTime() == null) {
                  player.sendMessage(StringUtil.PERMANENT_MUTE);
               } else {
                  player.sendMessage(String.format(StringUtil.TEMPORARY_MUTE, TimeUtil.millisToRoundedTime(Math.abs(System.currentTimeMillis() - mineman.getMuteTime().getTime()))));
               }

               return;
            }

            mineman.setMuted(false);
            mineman.setMuteTime(new Timestamp(0L));
         }

         Player target = CorePlugin.getInstance().getServer().getPlayer(targetName);
         if (target == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, targetName));
         } else if (target.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(CC.RED + "You cannot message yourself.");
         } else {
            Mineman targetMineman = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
            if (targetMineman == null) {
               player.sendMessage(CC.RED + "Failed to find that player.");
            } else if (!mineman.hasRank(Rank.HOST) && targetMineman.isMuted()) {
               player.sendMessage(CC.RED + "This player is currently muted.");
            } else if (!targetMineman.isCanSeeMessages() && !mineman.hasRank(Rank.HOST)) {
               player.sendMessage(CC.RED + "This player has messages toggled off.");
            } else {
               PrivateMessageEvent privateMessageEvent = new PrivateMessageEvent(mineman, targetMineman, mineman.getDisplayRank().getColor() + player.getName(), targetMineman.getDisplayRank().getColor() + target.getName());
               CorePlugin.getInstance().getServer().getPluginManager().callEvent(privateMessageEvent);
               if (!privateMessageEvent.isCancelled()) {
                  String[] messages = StringUtil.formatPrivateMessage(mineman.getDisplayRank().getColor() + player.getName(), targetMineman.getDisplayRank().getColor() + target.getName(), message);
                  player.sendMessage(messages[0]);
                  mineman.setLastConversation(target.getName());
                  boolean shouldFilter = MessageFilter.shouldFilter(message);
                  if (message.contains("IÌ‡")) {
                     CorePlugin.getInstance().getFilterManager().handleCommand("mute " + player.getName() + " Sending crash codes -s");
                     player.sendMessage(Color.translate("&cYou have been muted for &eCrash Codes&c."));
                     player.sendMessage(Color.translate("&cIf you beleive this is false, join our TeamSpeak (ts.pvptemple.com)"));
                  } else {
                     if (shouldFilter && !mineman.hasRank(Rank.HOST)) {
                        PlayerUtil.messageRank(CC.RED + "[Filtered] " + CC.DARK_GRAY + "(" + mineman.getDisplayRank().getColor() + player.getName() + CC.DARK_GRAY + " -> " + targetMineman.getDisplayRank().getColor() + target.getName() + CC.DARK_GRAY + ") " + CC.RED + message);
                     } else {
                        if (shouldFilter) {
                           player.sendMessage(CC.RED + "That would have been filtered.");
                        }

                        if (!targetMineman.isIgnoring(mineman.getId()) || mineman.hasRank(Rank.HOST)) {
                           target.sendMessage(messages[1]);
                           targetMineman.setLastConversation(player.getName());
                        }
                     }

                  }
               }
            }
         }
      }
   }
}
