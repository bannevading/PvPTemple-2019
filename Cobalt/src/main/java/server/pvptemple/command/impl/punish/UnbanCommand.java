package server.pvptemple.command.impl.punish;

import server.pvptemple.command.impl.punish.PunishCommand;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;

public class UnbanCommand extends PunishCommand {
   public UnbanCommand() {
      super(Rank.ADMIN, "unban", "Unban a player.", CC.RED + "Usage: /unban <player>", PunishType.UNBAN);
   }
}
