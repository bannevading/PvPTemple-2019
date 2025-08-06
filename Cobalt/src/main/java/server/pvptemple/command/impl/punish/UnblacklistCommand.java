package server.pvptemple.command.impl.punish;

import server.pvptemple.command.impl.punish.PunishCommand;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;

public class UnblacklistCommand extends server.pvptemple.command.impl.punish.PunishCommand {
   public UnblacklistCommand() {
      super(Rank.PLATFORMADMIN, "unblacklist", "Un-blacklist a player.", CC.RED + "Usage: /unblacklist <player>", PunishType.UNBLACKLIST);
   }
}
