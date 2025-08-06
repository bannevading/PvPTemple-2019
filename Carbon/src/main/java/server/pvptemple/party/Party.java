package server.pvptemple.party;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.match.MatchTeam;

public class Party {
   private final Carbon plugin = Carbon.getInstance();
   private final UUID leader;
   private final Set<UUID> members = new HashSet();
   private int limit = 50;
   private boolean open;

   public Party(UUID leader) {
      this.leader = leader;
      this.members.add(leader);
   }

   public void addMember(UUID uuid) {
      this.members.add(uuid);
   }

   public void removeMember(UUID uuid) {
      this.members.remove(uuid);
   }

   public void broadcast(String message) {
      this.members().forEach((member) -> member.sendMessage(message));
   }

   public MatchTeam[] split() {
      List<UUID> teamA = new ArrayList();
      List<UUID> teamB = new ArrayList();

      for(UUID member : this.members) {
         if (teamA.size() == teamB.size()) {
            teamA.add(member);
         } else {
            teamB.add(member);
         }
      }

      return new MatchTeam[]{new MatchTeam((UUID)teamA.get(0), teamA, (List)null, 0), new MatchTeam((UUID)teamB.get(0), teamB, (List)null, 1)};
   }

   public Stream<Player> members() {
      Stream<UUID> var10000 = this.members.stream();
      Server var10001 = this.plugin.getServer();
      return var10000.map(var10001::getPlayer).filter(Objects::nonNull);
   }

   public Carbon getPlugin() {
      return this.plugin;
   }

   public UUID getLeader() {
      return this.leader;
   }

   public Set<UUID> getMembers() {
      return this.members;
   }

   public int getLimit() {
      return this.limit;
   }

   public boolean isOpen() {
      return this.open;
   }

   public void setLimit(int limit) {
      this.limit = limit;
   }

   public void setOpen(boolean open) {
      this.open = open;
   }
}
