package server.pvptemple.command.impl.punish;

import server.pvptemple.command.impl.punish.PunishCommand;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;

public class BlacklistCommand extends server.pvptemple.command.impl.punish.PunishCommand {
   public BlacklistCommand() {
      super(Rank.PLATFORMADMIN, "blacklist", "Blacklist a player.", CC.RED + "Usage: /blacklist <player> [reason]", PunishType.BLACKLIST);
   }
}
