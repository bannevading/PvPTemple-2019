package server.pvptemple.util;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.CorePlugin;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class PlayerList {
   private final List<Player> players;

   public static PlayerList getVisiblyOnline(CommandSender sender) {
      return getOnline().visibleTo(sender);
   }

   public static PlayerList getOnline() {
      return new PlayerList(new ArrayList(CorePlugin.getInstance().getServer().getOnlinePlayers()));
   }

   public PlayerList visibleTo(CommandSender sender) {
      if (sender instanceof Player) {
         Player player = (Player)sender;
         this.players.removeIf((other) -> other != player && !player.canSee(other));
      }

      return this;
   }

   public PlayerList canSee(CommandSender sender) {
      if (sender instanceof Player) {
         Player player = (Player)sender;
         this.players.removeIf((other) -> other == player || !other.canSee(player));
      }

      return this;
   }

   public PlayerList visibleRankSorted() {
      this.players.sort(PlayerUtil.VISIBLE_RANK_ORDER);
      return this;
   }

   public List<String> asColoredNames() {
      return (List)this.players.stream().map(OfflinePlayer::getUniqueId).map((uuid) -> CorePlugin.getInstance().getPlayerManager().getPlayer(uuid)).map((mineman) -> mineman.getDisplayRank().getColor() + mineman.getDisplayName() + CC.R).collect(Collectors.toList());
   }

   public List<Player> getPlayers() {
      return this.players;
   }

   @ConstructorProperties({"players"})
   public PlayerList(List<Player> players) {
      this.players = players;
   }
}
