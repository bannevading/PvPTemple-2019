package server.pvptemple.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;

public class KillableTeam {
   protected final Carbon plugin = Carbon.getInstance();
   private final List<UUID> players;
   private final List<UUID> alivePlayers = new ArrayList();
   private final String leaderName;
   private UUID leader;

   public KillableTeam(UUID leader, List<UUID> players) {
      this.leader = leader;
      this.leaderName = this.plugin.getServer().getPlayer(leader).getName();
      this.players = players;
      this.alivePlayers.addAll(players);
   }

   public void killPlayer(UUID playerUUID) {
      this.alivePlayers.remove(playerUUID);
   }

   public Stream<Player> alivePlayers() {
      Stream<UUID> var10000 = this.alivePlayers.stream();
      Server var10001 = this.plugin.getServer();
      return var10000.map(var10001::getPlayer).filter(Objects::nonNull);
   }

   public Stream<Player> players() {
      Stream<UUID> var10000 = this.players.stream();
      Server var10001 = this.plugin.getServer();
      return var10000.map(var10001::getPlayer).filter(Objects::nonNull);
   }

   public Carbon getPlugin() {
      return this.plugin;
   }

   public List<UUID> getPlayers() {
      return this.players;
   }

   public List<UUID> getAlivePlayers() {
      return this.alivePlayers;
   }

   public String getLeaderName() {
      return this.leaderName;
   }

   public UUID getLeader() {
      return this.leader;
   }

   public void setLeader(UUID leader) {
      this.leader = leader;
   }
}
