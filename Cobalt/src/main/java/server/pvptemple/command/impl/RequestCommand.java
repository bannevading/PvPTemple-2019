package server.pvptemple.command.impl;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.redis.CoreRedisManager;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.Color;
import server.pvptemple.util.finalutil.StringUtil;

public class RequestCommand extends Command {
   private CorePlugin plugin;

   public RequestCommand(CorePlugin plugin) {
      super("helpop");
      this.usageMessage = CC.RED + "Usage: /helpop <reason>";
      this.plugin = plugin;
      this.setAliases(Arrays.asList("request"));
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage("die");
         return true;
      } else {
         Player player = (Player)sender;
         if (args.length == 0) {
            player.sendMessage(this.usageMessage);
            return true;
         } else {
            Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
            if (!mineman.isBanned() && !mineman.isIpBanned()) {
               if (mineman.getReportCooldown() > System.currentTimeMillis()) {
                  sender.sendMessage(CC.RED + "Please wait before doing this again.");
                  return true;
               } else {
                  String reason = StringUtil.buildMessage(args, 0);
                  player.sendMessage(CC.GREEN + "Your request has been submitted.");
                  Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> CorePlugin.getInstance().getCoreRedisManager().sendMessage(CoreRedisManager.getServerMessagePrefix() + CC.DARK_GRAY + "[" + CC.GOLD + "Request" + CC.DARK_GRAY + "] " + mineman.getDisplayRank().getColor() + player.getName() + CC.YELLOW + " requested " + CC.RESET + reason, Rank.HOST));
                  mineman.setReportCooldown(System.currentTimeMillis() + 30000L);
                  return true;
               }
            } else {
               player.sendMessage(Color.translate("&cYou cannot use this command."));
               return true;
            }
         }
      }
   }
}
