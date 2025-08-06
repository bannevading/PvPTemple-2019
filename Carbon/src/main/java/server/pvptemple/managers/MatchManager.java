package server.pvptemple.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.arena.Arena;
import server.pvptemple.clickable.Clickable;
import server.pvptemple.event.match.MatchEndEvent;
import server.pvptemple.event.match.MatchStartEvent;
import server.pvptemple.inventory.InventorySnapshot;
import server.pvptemple.kit.Kit;
import server.pvptemple.kit.PlayerKit;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchRequest;
import server.pvptemple.match.MatchState;
import server.pvptemple.match.MatchTeam;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.queue.QueueType;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.PlayerUtil;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.ItemUtil;
import server.pvptemple.util.ttl.TtlHashMap;

public class MatchManager {
   private final Map<UUID, Set<MatchRequest>> matchRequests;
   private final Map<UUID, UUID> rematchUUIDs;
   private final Map<UUID, UUID> rematchInventories;
   private final Map<UUID, UUID> spectators;
   private final Map<UUID, Match> matches;
   private final Carbon plugin;

   public MatchManager() {
      this.matchRequests = new TtlHashMap(TimeUnit.SECONDS, 30L);
      this.rematchUUIDs = new TtlHashMap(TimeUnit.SECONDS, 30L);
      this.rematchInventories = new TtlHashMap(TimeUnit.SECONDS, 30L);
      this.spectators = new ConcurrentHashMap();
      this.matches = new ConcurrentHashMap();
      this.plugin = Carbon.getInstance();
   }

   public int getFighters() {
      int i = 0;

      for(Match match : this.matches.values()) {
         for(MatchTeam matchTeam : match.getTeams()) {
            i += matchTeam.getAlivePlayers().size();
         }
      }

      return i;
   }

   public int getFighters(String ladder, QueueType type) {
      return (int)this.matches.entrySet().stream().filter((match) -> ((Match)match.getValue()).getType() == type).filter((match) -> ((Match)match.getValue()).getKit().getName().equals(ladder)).count();
   }

   public void createMatchRequest(Player requester, Player requested, Arena arena, String kitName, boolean party) {
      MatchRequest request = new MatchRequest(requester.getUniqueId(), requested.getUniqueId(), arena, kitName, party);
      (this.matchRequests.computeIfAbsent(requested.getUniqueId(), (k) -> new HashSet())).add(request);
   }

   public MatchRequest getMatchRequest(UUID requester, UUID requested) {
      Set<MatchRequest> requests = this.matchRequests.get(requested);
      return requests == null ? null : requests.stream().filter((req) -> req.getRequester().equals(requester)).findAny().orElse(null);
   }

   public MatchRequest getMatchRequest(UUID requester, UUID requested, String kitName) {
      Set<MatchRequest> requests = this.matchRequests.get(requested);
      return requests == null ? null : requests.stream().filter((req) -> req.getRequester().equals(requester) && req.getKitName().equals(kitName)).findAny().orElse(null);
   }

   public Match getMatch(PlayerData playerData) {
      return (Match)this.matches.get(playerData.getCurrentMatchID());
   }

   public Match getMatch(UUID uuid) {
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(uuid);
      return this.getMatch(playerData);
   }

   public Match getMatchFromUUID(UUID uuid) {
      return (Match)this.matches.get(uuid);
   }

   public Match getSpectatingMatch(UUID uuid) {
      return (Match)this.matches.get(this.spectators.get(uuid));
   }

   public void removeMatchRequests(UUID uuid) {
      this.matchRequests.remove(uuid);
   }

   public void createMatch(Match match) {
      this.matches.put(match.getMatchId(), match);
      this.plugin.getServer().getPluginManager().callEvent(new MatchStartEvent(match));
   }

   public void removeFighter(Player player, PlayerData playerData, boolean spectateDeath) {
      Match match = (Match)this.matches.get(playerData.getCurrentMatchID());
      Player killer = player.getKiller();
      MatchTeam entityTeam = (MatchTeam)match.getTeams().get(playerData.getTeamID());
      MatchTeam winningTeam = match.isFFA() ? entityTeam : (MatchTeam)match.getTeams().get(entityTeam.getTeamID() == 0 ? 1 : 0);
      if (match.getMatchState() != MatchState.ENDING) {
         String deathMessage = CC.RED + player.getName() + CC.YELLOW + " was " + (killer != null ? "slain by " + CC.RED + killer.getName() + CC.YELLOW : "killed") + ".";
         match.broadcast(deathMessage);
         if (match.isRedrover()) {
            if (match.getMatchState() != MatchState.SWITCHING) {
               Clickable inventories = new Clickable(CC.AQUA + "Inventories: ");
               if (killer != null) {
                  InventorySnapshot snapshot = new InventorySnapshot(killer, match);
                  this.plugin.getInventoryManager().addSnapshot(snapshot);
                  inventories.add(CC.GREEN + killer.getName() + " ", CC.GREEN + "View Inventory", "/inv " + snapshot.getSnapshotId());
               }

               InventorySnapshot snapshot = new InventorySnapshot(player, match);
               this.plugin.getInventoryManager().addSnapshot(snapshot);
               inventories.add(CC.RED + player.getName() + " ", CC.GREEN + "View Inventory", "/inv " + snapshot.getSnapshotId());
               match.broadcast(inventories);
               match.setMatchState(MatchState.SWITCHING);
               match.setCountdown(4);
            }
         } else {
            match.addSnapshot(player);
         }

         entityTeam.killPlayer(player.getUniqueId());
         int remaining = entityTeam.getAlivePlayers().size();
         if (remaining != 0) {
            Set<Item> items = new HashSet();

            for(ItemStack item : player.getInventory().getContents()) {
               if (item != null && item.getType() != Material.AIR) {
                  items.add(player.getWorld().dropItemNaturally(player.getLocation(), item, player));
               }
            }

            for(ItemStack item : player.getInventory().getArmorContents()) {
               if (item != null && item.getType() != Material.AIR) {
                  items.add(player.getWorld().dropItemNaturally(player.getLocation(), item, player));
               }
            }

            this.plugin.getMatchManager().addDroppedItems(match, items);
         }

         if (spectateDeath) {
            PlayerUtil.updateNametag(player, false);
            this.addDeathSpectator(player, playerData, match);
         }

         if (match.isFFA() && remaining == 1 || remaining == 0) {
            winningTeam.getPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach((o) -> PlayerUtil.updateNametag(o, false));
            this.plugin.getServer().getPluginManager().callEvent(new MatchEndEvent(match, winningTeam, entityTeam));
         }

      }
   }

   public void removeMatch(Match match) {
      this.matches.remove(match.getMatchId());
   }

   public void giveKits(Player player, Kit kit) {
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      Collection<PlayerKit> playerKits = playerData.getPlayerKits(kit.getName()).values();
      if (playerKits.size() == 0) {
         kit.applyToPlayer(player);
      } else {
         player.getInventory().setItem(8, this.plugin.getItemManager().getDefaultBook());
         int slot = -1;

         for(PlayerKit playerKit : playerKits) {
            PlayerInventory var10000 = player.getInventory();
            ++slot;
            var10000.setItem(slot, ItemUtil.createItem(Material.ENCHANTED_BOOK, CC.PRIMARY + playerKit.getDisplayName()));
         }

         player.updateInventory();
      }

   }

   private void addDeathSpectator(Player player, PlayerData playerData, Match match) {
      this.spectators.put(player.getUniqueId(), match.getMatchId());
      playerData.setPlayerState(PlayerState.SPECTATING);
      PlayerUtil.clearPlayer(player);
      CraftPlayer playerCp = (CraftPlayer)player;
      EntityPlayer playerEp = playerCp.getHandle();
      playerEp.getDataWatcher().watch(6, 0.0F);
      playerEp.setFakingDeath(true);
      match.addSpectator(player.getUniqueId());
      match.addRunnable(this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
         match.getTeams().forEach((team) -> team.alivePlayers().forEach((member) -> member.hidePlayer(player)));
         match.spectatorPlayers().forEach((member) -> member.hidePlayer(player));
         player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
         player.setWalkSpeed(0.2F);
         player.setAllowFlight(true);
      }, 20L));
      if (match.isRedrover()) {
         for(MatchTeam team : match.getTeams()) {
            for(UUID alivePlayerUUID : team.getAlivePlayers()) {
               Player alivePlayer = this.plugin.getServer().getPlayer(alivePlayerUUID);
               if (alivePlayer != null) {
                  player.showPlayer(alivePlayer);
               }
            }
         }
      }

      player.setWalkSpeed(0.0F);
      player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000, -5));
      if (match.isParty() || match.isFFA()) {
         this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> player.getInventory().setContents(this.plugin.getItemManager().getPartySpecItems()), 1L);
      }

      player.updateInventory();
   }

   public void addRedroverSpectator(Player player, Match match) {
      this.spectators.put(player.getUniqueId(), match.getMatchId());
      player.setAllowFlight(true);
      player.setFlying(true);
      player.getInventory().setContents(this.plugin.getItemManager().getPartySpecItems());
      player.updateInventory();
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      playerData.setPlayerState(PlayerState.SPECTATING);
   }

   public void addSpectator(Player player, PlayerData playerData, Player target, Match targetMatch) {
      this.spectators.put(player.getUniqueId(), targetMatch.getMatchId());
      Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
      if (targetMatch.getMatchState() != MatchState.ENDING && !mineman.hasRank(Rank.TRAINEE) && !targetMatch.haveSpectated(player.getUniqueId())) {
         targetMatch.broadcast(CC.WHITE + player.getName() + CC.YELLOW + " is now spectating.");
      }

      targetMatch.addSpectator(player.getUniqueId());
      playerData.setPlayerState(PlayerState.SPECTATING);
      player.teleport(target);
      player.setAllowFlight(true);
      player.setFlying(true);
      player.getInventory().setContents(this.plugin.getItemManager().getSpecItems());
      player.updateInventory();
      this.plugin.getServer().getOnlinePlayers().forEach((online) -> {
         online.hidePlayer(player);
         player.hidePlayer(online);
      });
      targetMatch.getTeams().forEach((team) -> team.alivePlayers().forEach(player::showPlayer));
   }

   public void addDroppedItem(Match match, Item item) {
      match.addEntityToRemove(item);
      match.addRunnable(this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
         match.removeEntityToRemove(item);
         item.remove();
      }, 100L).getTaskId());
   }

   public void addDroppedItems(Match match, Set<Item> items) {
      for(Item item : items) {
         match.addEntityToRemove(item);
      }

      match.addRunnable(this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
         for(Item item : items) {
            match.removeEntityToRemove(item);
            item.remove();
         }

      }, 100L).getTaskId());
      List<Player> whitelisted = new ArrayList();
      match.getTeams().forEach((team) -> team.players().forEach(whitelisted::add));
      Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
         for(Player player : Bukkit.getOnlinePlayers()) {
            if (!whitelisted.contains(player)) {
               for(Item item : items) {
                  ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(new int[]{item.getEntityId()}));
               }
            }
         }

      }, 1L);
   }

   public void removeSpectator(Player player) {
      if (this.spectators.containsKey(player.getUniqueId())) {
         Match match = (Match)this.matches.get(this.spectators.get(player.getUniqueId()));
         match.removeSpectator(player.getUniqueId());
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         if (match.getTeams().size() > playerData.getTeamID() && playerData.getTeamID() >= 0) {
            MatchTeam entityTeam = (MatchTeam)match.getTeams().get(playerData.getTeamID());
            if (entityTeam != null) {
               entityTeam.killPlayer(player.getUniqueId());
            }
         }

         if (match.getMatchState() != MatchState.ENDING) {
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman != null && !mineman.hasRank(Rank.TRAINEE) && !match.haveSpectated(player.getUniqueId())) {
               match.broadcast(CC.WHITE + player.getName() + CC.YELLOW + " is no longer spectating.");
               match.addHaveSpectated(player.getUniqueId());
            }
         }

         this.spectators.remove(player.getUniqueId());
         this.plugin.getPlayerManager().sendToSpawnAndReset(player);
      }
   }

   public void pickPlayer(Match match) {
      Player playerA = this.plugin.getServer().getPlayer((UUID)((MatchTeam)match.getTeams().get(0)).getAlivePlayers().get(0));
      PlayerData playerDataA = this.plugin.getPlayerManager().getPlayerData(playerA.getUniqueId());
      if (playerDataA.getPlayerState() != PlayerState.FIGHTING) {
         playerA.teleport(match.getArena().getA().toBukkitLocation());
         PlayerUtil.clearPlayer(playerA);
         if (match.getKit().isCombo()) {
            playerA.setNoDamageTicks(0);
            playerA.setMaximumNoDamageTicks(2);
            playerA.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
            playerA.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999999, 0));
         }

         if (match.getKit().getName().toLowerCase().contains("soup")) {
            playerA.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
         }

         if (match.getKit().getName().toLowerCase().contains("gapple")) {
            playerA.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
            playerA.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999999, 1));
         }

         this.plugin.getMatchManager().giveKits(playerA, match.getKit());
         playerDataA.setPlayerState(PlayerState.FIGHTING);
      }

      Player playerB = this.plugin.getServer().getPlayer((UUID)((MatchTeam)match.getTeams().get(1)).getAlivePlayers().get(0));
      PlayerData playerDataB = this.plugin.getPlayerManager().getPlayerData(playerB.getUniqueId());
      if (playerDataB.getPlayerState() != PlayerState.FIGHTING) {
         playerB.teleport(match.getArena().getB().toBukkitLocation());
         PlayerUtil.clearPlayer(playerB);
         if (match.getKit().isCombo()) {
            playerB.setNoDamageTicks(0);
            playerB.setMaximumNoDamageTicks(2);
            playerB.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
            playerB.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999999, 0));
         }

         if (match.getKit().getName().toLowerCase().contains("soup")) {
            playerB.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
         }

         if (match.getKit().getName().toLowerCase().contains("gapple")) {
            playerB.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
            playerB.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999999, 1));
         }

         this.plugin.getMatchManager().giveKits(playerB, match.getKit());
         playerDataB.setPlayerState(PlayerState.FIGHTING);
      }

      for(MatchTeam team : match.getTeams()) {
         for(UUID uuid : team.getAlivePlayers()) {
            Player player = this.plugin.getServer().getPlayer(uuid);
            if (player != null && !playerA.equals(player) && !playerB.equals(player)) {
               playerA.hidePlayer(player);
               playerB.hidePlayer(player);
            }
         }
      }

      playerA.showPlayer(playerB);
      playerB.showPlayer(playerA);
      match.broadcast(CC.SECONDARY + playerA.getName() + CC.PRIMARY + " vs. " + CC.SECONDARY + playerB.getName());
   }

   public void saveRematches(Match match) {
      if (!match.isParty() && !match.isFFA()) {
         UUID playerOne = ((MatchTeam)match.getTeams().get(0)).getLeader();
         UUID playerTwo = ((MatchTeam)match.getTeams().get(1)).getLeader();
         PlayerData dataOne = this.plugin.getPlayerManager().getPlayerData(playerOne);
         PlayerData dataTwo = this.plugin.getPlayerManager().getPlayerData(playerTwo);
         if (dataOne != null) {
            this.rematchUUIDs.put(playerOne, playerTwo);
            InventorySnapshot snapshot = match.getSnapshot(playerTwo);
            if (snapshot != null) {
               this.rematchInventories.put(playerOne, snapshot.getSnapshotId());
            }

            if (dataOne.getRematchID() > -1) {
               this.plugin.getServer().getScheduler().cancelTask(dataOne.getRematchID());
            }
         }

         if (dataTwo != null) {
            this.rematchUUIDs.put(playerTwo, playerOne);
            InventorySnapshot snapshot = match.getSnapshot(playerOne);
            if (snapshot != null) {
               this.rematchInventories.put(playerTwo, snapshot.getSnapshotId());
            }

            if (dataTwo.getRematchID() > -1) {
               this.plugin.getServer().getScheduler().cancelTask(dataTwo.getRematchID());
            }
         }

      }
   }

   public void removeRematch(UUID uuid) {
      this.rematchUUIDs.remove(uuid);
      this.rematchInventories.remove(uuid);
   }

   public UUID getRematcher(UUID uuid) {
      return (UUID)this.rematchUUIDs.get(uuid);
   }

   public UUID getRematcherInventory(UUID uuid) {
      return (UUID)this.rematchInventories.get(uuid);
   }

   public boolean isRematching(UUID uuid) {
      return this.rematchUUIDs.containsKey(uuid);
   }

   public Map<UUID, Match> getMatches() {
      return this.matches;
   }
}
