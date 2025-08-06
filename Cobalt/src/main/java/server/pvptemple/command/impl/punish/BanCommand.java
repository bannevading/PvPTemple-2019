package server.pvptemple.command.impl.punish;

import server.pvptemple.command.impl.punish.PunishCommand;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;

public class BanCommand extends PunishCommand {
   public BanCommand() {
      super(Rank.MOD, "ban", "Ban a player.", CC.RED + "Usage: /ban <player> [time] [reason]", PunishType.BAN);
   }
}
