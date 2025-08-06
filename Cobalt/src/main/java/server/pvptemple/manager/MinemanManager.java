package server.pvptemple.manager;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import server.pvptemple.mineman.Mineman;

public class MinemanManager {
   @Getter
   private final Map<UUID, Mineman> players = new HashMap<>();
   @Getter
   private final List<UUID> dummyPlayers = new ArrayList<>();
   @Getter
   @Setter
   private long chatSlowDownTime;
   @Setter @Getter
   private boolean chatSilenced;

   public Mineman addPlayer(UUID uuid, String name, InetAddress ipAddress) {
      Mineman mineman = new Mineman(uuid, name, ipAddress);
      this.players.put(uuid, mineman);
      return mineman;
   }

   public Mineman getPlayer(UUID uuid) {
      return this.players.get(uuid);
   }

   public void removePlayer(UUID player) {
      this.players.remove(player);
   }

}
