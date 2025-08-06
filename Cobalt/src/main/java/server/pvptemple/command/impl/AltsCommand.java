package server.pvptemple.command.impl;

import com.google.gson.JsonArray;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.impl.PlayerRequest;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.Color;

public class AltsCommand implements CommandHandler {
   @Command(
      name = {"alts", "alt"},
      rank = Rank.SENIORMOD,
      description = "View a player's alts."
   )
   public void altsCommand(Player player, @Param(name = "player") String target) {
      PlayerRequest.AltsRequest altsRequest = new PlayerRequest.AltsRequest(target);
      CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(altsRequest, (data) -> {
         player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 45)));
         player.sendMessage(Color.translate("&6Alternate accounts of: &f" + target));
         player.sendMessage("");
         JsonArray array = data.getAsJsonArray();
         StringBuilder sb = new StringBuilder();

         for(Object obj : array) {
            if (sb.length() > 0) {
               sb.append(", ");
            }

            sb.append(obj == null ? null : obj.toString().replace("\"", ""));
         }

         player.sendMessage(Color.translate("&eAccounts: &f" + sb.toString()));
         player.sendMessage(Color.translate("&7&m" + StringUtils.repeat("-", 45)));
      });
   }
}
