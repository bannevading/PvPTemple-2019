package server.pvptemple.command.impl.punish;

import server.pvptemple.command.impl.punish.PunishCommand;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;

public class UnmuteCommand extends server.pvptemple.command.impl.punish.PunishCommand {
   public UnmuteCommand() {
      super(Rank.SENIORMOD, "unmute", "Unmute a player.", CC.RED + "Usage: /unmute <player>", PunishType.UNMUTE);
   }
}
