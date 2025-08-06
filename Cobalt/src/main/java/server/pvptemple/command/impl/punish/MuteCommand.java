package server.pvptemple.command.impl.punish;

import server.pvptemple.command.impl.punish.PunishCommand;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.CC;

public class MuteCommand extends PunishCommand {
   public MuteCommand() {
      super(Rank.TRAINEE, "mute", "Mute a player.", CC.RED + "Usage: /mute <player> [time] [reason]", PunishType.MUTE);
   }
}
