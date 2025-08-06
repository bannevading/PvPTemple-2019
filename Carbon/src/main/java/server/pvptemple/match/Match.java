package server.pvptemple.match;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.netty.util.internal.ConcurrentSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.arena.Arena;
import server.pvptemple.arena.StandaloneArena;
import server.pvptemple.clickable.Clickable;
import server.pvptemple.inventory.InventorySnapshot;
import server.pvptemple.kit.Kit;
import server.pvptemple.queue.QueueType;
import server.pvptemple.util.Color;

public class Match {
   private final Carbon plugin;
   private final Map<UUID, InventorySnapshot> snapshots;
   private final Set<Entity> entitiesToRemove;
   private final LinkedList<BlockState> originalBlockChanges;
   private final Set<Location> placedBlockLocations;
   private final Set<UUID> spectators;
   private final Set<Integer> runnables;
   private final Set<UUID> haveSpectated;
   private final List<MatchTeam> teams;
   private final UUID matchId;
   private final QueueType type;
   private final Arena arena;
   private final Kit kit;
   private final boolean redrover;
   private StandaloneArena standaloneArena;
   private MatchState matchState;
   private int winningTeamId;
   private int countdown;

   public Match(Arena arena, Kit kit, QueueType type, MatchTeam... teams) {
      this(arena, kit, type, false, teams);
   }

   public Match(Arena arena, Kit kit, QueueType type, boolean redrover, MatchTeam... teams) {
      this.plugin = Carbon.getInstance();
      this.snapshots = new HashMap();
      this.entitiesToRemove = new HashSet();
      this.originalBlockChanges = Lists.newLinkedList();
      this.placedBlockLocations = Sets.newConcurrentHashSet();
      this.spectators = new ConcurrentSet();
      this.runnables = new HashSet();
      this.haveSpectated = new HashSet();
      this.matchId = UUID.randomUUID();
      this.matchState = MatchState.STARTING;
      this.countdown = 6;
      this.arena = arena;
      this.kit = kit;
      this.type = type;
      this.redrover = redrover;
      this.teams = Arrays.asList(teams);
   }

   public void addSpectator(UUID uuid) {
      this.spectators.add(uuid);
   }

   public void removeSpectator(UUID uuid) {
      this.spectators.remove(uuid);
   }

   public void addHaveSpectated(UUID uuid) {
      this.haveSpectated.add(uuid);
   }

   public boolean haveSpectated(UUID uuid) {
      return this.haveSpectated.contains(uuid);
   }

   public void addSnapshot(Player player) {
      this.snapshots.put(player.getUniqueId(), new InventorySnapshot(player, this));
   }

   public boolean hasSnapshot(UUID uuid) {
      return this.snapshots.containsKey(uuid);
   }

   public InventorySnapshot getSnapshot(UUID uuid) {
      return (InventorySnapshot)this.snapshots.get(uuid);
   }

   public void addEntityToRemove(Entity entity) {
      this.entitiesToRemove.add(entity);
   }

   public void removeEntityToRemove(Entity entity) {
      this.entitiesToRemove.remove(entity);
   }

   public void clearEntitiesToRemove() {
      this.entitiesToRemove.clear();
   }

   public void addRunnable(int id) {
      this.runnables.add(id);
   }

   public void addOriginalBlockChange(BlockState blockState) {
      this.originalBlockChanges.add(blockState);
   }

   public void removeOriginalBlockChange(BlockState blockState) {
      this.originalBlockChanges.remove(blockState);
   }

   public void addPlacedBlockLocation(Location location) {
      this.placedBlockLocations.add(location);
   }

   public void removePlacedBlockLocation(Location location) {
      this.placedBlockLocations.remove(location);
   }

   public void broadcastWithSound(String message, Sound sound) {
      this.teams.forEach((team) -> team.alivePlayers().forEach((player) -> {
            player.sendMessage(message);
            player.playSound(player.getLocation(), sound, 10.0F, 1.0F);
         }));
      this.spectatorPlayers().forEach((spectator) -> {
         spectator.sendMessage(message);
         spectator.playSound(spectator.getLocation(), sound, 10.0F, 1.0F);
      });
   }

   public void broadcast(BaseComponent[] message) {
      this.teams.forEach((team) -> team.alivePlayers().forEach((player) -> player.sendMessage(message)));
      this.spectatorPlayers().forEach((spectator) -> spectator.sendMessage(message));
   }

   public void broadcast(String message) {
      this.teams.forEach((team) -> team.alivePlayers().forEach((player) -> player.sendMessage(Color.translate(message))));
      this.spectatorPlayers().forEach((spectator) -> spectator.sendMessage(Color.translate(message)));
   }

   public void broadcast(Clickable message) {
      this.teams.forEach((team) -> team.alivePlayers().forEach(message::sendToPlayer));
      this.spectatorPlayers().forEach(message::sendToPlayer);
   }

   public Stream<Player> spectatorPlayers() {
      Stream<UUID> var10000 = this.spectators.stream();
      Server var10001 = this.plugin.getServer();
      return var10000.map(var10001::getPlayer).filter(Objects::nonNull);
   }

   public int decrementCountdown() {
      return --this.countdown;
   }

   public boolean isParty() {
      return this.isFFA() || ((MatchTeam)this.teams.get(0)).getPlayers().size() != 1 && ((MatchTeam)this.teams.get(1)).getPlayers().size() != 1;
   }

   public boolean isFFA() {
      return this.teams.size() == 1;
   }

   public void setStandaloneArena(StandaloneArena standaloneArena) {
      this.standaloneArena = standaloneArena;
   }

   public void setMatchState(MatchState matchState) {
      this.matchState = matchState;
   }

   public void setWinningTeamId(int winningTeamId) {
      this.winningTeamId = winningTeamId;
   }

   public void setCountdown(int countdown) {
      this.countdown = countdown;
   }

   public Map<UUID, InventorySnapshot> getSnapshots() {
      return this.snapshots;
   }

   public Set<Entity> getEntitiesToRemove() {
      return this.entitiesToRemove;
   }

   public LinkedList<BlockState> getOriginalBlockChanges() {
      return this.originalBlockChanges;
   }

   public Set<Location> getPlacedBlockLocations() {
      return this.placedBlockLocations;
   }

   public Set<UUID> getSpectators() {
      return this.spectators;
   }

   public Set<Integer> getRunnables() {
      return this.runnables;
   }

   public List<MatchTeam> getTeams() {
      return this.teams;
   }

   public UUID getMatchId() {
      return this.matchId;
   }

   public QueueType getType() {
      return this.type;
   }

   public Arena getArena() {
      return this.arena;
   }

   public Kit getKit() {
      return this.kit;
   }

   public boolean isRedrover() {
      return this.redrover;
   }

   public StandaloneArena getStandaloneArena() {
      return this.standaloneArena;
   }

   public MatchState getMatchState() {
      return this.matchState;
   }

   public int getWinningTeamId() {
      return this.winningTeamId;
   }

   public int getCountdown() {
      return this.countdown;
   }
}
