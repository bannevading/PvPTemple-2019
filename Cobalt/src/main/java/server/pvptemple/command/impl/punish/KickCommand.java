package server.pvptemple.command.impl.punish;

import server.pvptemple.command.impl.punish.PunishCommand;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;

public class KickCommand extends PunishCommand {
   public KickCommand() {
      super(Rank.TRAINEE, "kick", "Kick a player.", CC.RED + "Usage: /kick <player> [reason]", PunishType.KICK);
   }
}
