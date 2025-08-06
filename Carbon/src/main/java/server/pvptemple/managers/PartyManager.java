package server.pvptemple.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.ttl.TtlHashMap;

public class PartyManager {
   private final Carbon plugin = Carbon.getInstance();
   private Map<UUID, List<UUID>> partyInvites;
   private Map<UUID, Party> parties;
   private Map<UUID, UUID> partyLeaders;

   public PartyManager() {
      this.partyInvites = new TtlHashMap(TimeUnit.SECONDS, 15L);
      this.parties = new HashMap();
      this.partyLeaders = new HashMap();
   }

   public boolean isLeader(UUID uuid) {
      return this.parties.containsKey(uuid);
   }

   public void removePartyInvites(UUID uuid) {
      this.partyInvites.remove(uuid);
   }

   public boolean hasPartyInvite(UUID player, UUID other) {
      return this.partyInvites.get(player) != null && ((List)this.partyInvites.get(player)).contains(other);
   }

   public void createPartyInvite(UUID requester, UUID requested) {
      ((List)this.partyInvites.computeIfAbsent(requested, (k) -> new ArrayList())).add(requester);
   }

   public boolean isInParty(UUID player, Party party) {
      Party targetParty = this.getParty(player);
      return targetParty != null && targetParty.getLeader() == party.getLeader();
   }

   public Party getParty(UUID player) {
      if (this.parties.containsKey(player)) {
         return (Party)this.parties.get(player);
      } else if (this.partyLeaders.containsKey(player)) {
         UUID leader = (UUID)this.partyLeaders.get(player);
         return (Party)this.parties.get(leader);
      } else {
         return null;
      }
   }

   public void createParty(Player player) {
      Party party = new Party(player.getUniqueId());
      if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
         player.sendMessage(CC.RED + "You need to leave the tournament and make a party with the amount of teammates the tournamenthas.");
      } else {
         this.parties.put(player.getUniqueId(), party);
         this.plugin.getInventoryManager().addParty(player);
         this.plugin.getPlayerManager().sendToSpawnAndReset(player);
         player.sendMessage(CC.GREEN + "You have created a party.");
      }
   }

   private void disbandParty(Party party, boolean tournament) {
      this.plugin.getInventoryManager().removeParty(party);
      this.parties.remove(party.getLeader());
      party.broadcast(CC.RED + "The party leader has disbanded the party" + (tournament ? " because one of your party members left during a tournament." : "."));
      party.members().forEach((member) -> {
         PlayerData memberData = this.plugin.getPlayerManager().getPlayerData(member.getUniqueId());
         if (this.partyLeaders.get(memberData.getUniqueId()) != null) {
            this.partyLeaders.remove(memberData.getUniqueId());
         }

         if (memberData.getPlayerState() == PlayerState.SPAWN) {
            this.plugin.getPlayerManager().sendToSpawnAndReset(member);
         }

      });
   }

   public void leaveParty(Player player) {
      Party party = this.getParty(player.getUniqueId());
      if (party != null) {
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         if (this.parties.containsKey(player.getUniqueId())) {
            this.disbandParty(party, false);
         } else if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
            this.disbandParty(party, true);
         } else {
            party.broadcast(CC.RED + player.getName() + CC.YELLOW + " has left the party.");
            party.removeMember(player.getUniqueId());
            this.partyLeaders.remove(player.getUniqueId());
            this.plugin.getInventoryManager().updateParty(party);
         }

         switch (playerData.getPlayerState()) {
            case FIGHTING:
               this.plugin.getMatchManager().removeFighter(player, playerData, false);
               break;
            case SPECTATING:
               this.plugin.getMatchManager().removeSpectator(player);
         }

         party.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach((member) -> {
            member.hidePlayer(player);
            player.hidePlayer(member);
         });
         this.plugin.getPlayerManager().sendToSpawnAndReset(player);
      }
   }

   public void joinParty(UUID leader, Player player) {
      Party party = this.getParty(leader);
      if (this.plugin.getTournamentManager().getTournament(leader) != null) {
         player.sendMessage(CC.RED + "This player is in a tournament.");
      } else {
         this.partyLeaders.put(player.getUniqueId(), leader);
         party.addMember(player.getUniqueId());
         this.plugin.getInventoryManager().updateParty(party);
         this.plugin.getPlayerManager().sendToSpawnAndReset(player);
         party.broadcast(CC.DARK_GREEN + player.getName() + CC.YELLOW + " has joined the party.");
      }
   }

   public Carbon getPlugin() {
      return this.plugin;
   }

   public Map<UUID, List<UUID>> getPartyInvites() {
      return this.partyInvites;
   }

   public Map<UUID, Party> getParties() {
      return this.parties;
   }

   public Map<UUID, UUID> getPartyLeaders() {
      return this.partyLeaders;
   }
}
