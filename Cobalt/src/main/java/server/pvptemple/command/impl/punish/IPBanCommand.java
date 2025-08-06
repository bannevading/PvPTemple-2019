package server.pvptemple.command.impl.punish;

import server.pvptemple.command.impl.punish.PunishCommand;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;

public class IPBanCommand extends PunishCommand {
   public IPBanCommand() {
      super(Rank.ADMIN, "ipban", "IP-Ban a player.", CC.RED + "Usage: /ipban <player> [reason]", PunishType.IPBAN);
   }
}
