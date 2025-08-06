package server.pvptemple.command.impl;

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

public class ReportCommand extends Command {
   private CorePlugin plugin;

   public ReportCommand(CorePlugin plugin) {
      super("report");
      this.usageMessage = CC.RED + "Usage: /report <player> <reason>";
      this.plugin = plugin;
   }

   public boolean execute(CommandSender sender, String s, String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage("die");
         return true;
      } else {
         Player player = (Player)sender;
         if (args.length < 2) {
            player.sendMessage(this.usageMessage);
            return true;
         } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
               player.sendMessage(CC.RED + "Failed to find that player.");
               return true;
            } else if (target.equals(player)) {
               sender.sendMessage(CC.RED + "You cannot report yourself.");
               return true;
            } else {
               Mineman mineman = this.plugin.getPlayerManager().getPlayer(player.getUniqueId());
               if (!mineman.isBanned() && !mineman.isIpBanned()) {
                  if (mineman.getReportCooldown() > System.currentTimeMillis()) {
                     sender.sendMessage(CC.RED + "Please wait before doing this again.");
                     return true;
                  } else {
                     Mineman targetMineman = this.plugin.getPlayerManager().getPlayer(target.getUniqueId());
                     String reason = StringUtil.buildMessage(args, 1);
                     player.sendMessage(CC.GREEN + "Your report has been submitted.");
                     Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> CorePlugin.getInstance().getCoreRedisManager().sendMessage(CoreRedisManager.getServerMessagePrefix() + CC.DARK_GRAY + "[" + CC.GOLD + "Report" + CC.DARK_GRAY + "] " + mineman.getDisplayRank().getColor() + player.getName() + CC.YELLOW + " reported " + targetMineman.getDisplayRank().getColor() + target.getName() + CC.YELLOW + " for " + CC.RESET + reason, Rank.TRAINEE));
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
}
