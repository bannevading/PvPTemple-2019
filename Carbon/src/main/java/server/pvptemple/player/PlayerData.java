package server.pvptemple.player;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Location;
import server.pvptemple.Carbon;
import server.pvptemple.kit.PlayerKit;

public class PlayerData {
   public static final int DEFAULT_ELO = 1000;
   private final Map<String, Map<Integer, PlayerKit>> playerKits = new HashMap();
   private final Map<String, Integer> rankedLosses = new HashMap();
   private final Map<String, Integer> rankedWins = new HashMap();
   private final Map<String, Integer> rankedElo = new HashMap();
   private final Map<String, Integer> partyElo = new HashMap();
   private final UUID uniqueId;
   private PlayerState playerState;
   private List<Location> potions;
   private List<Packet> packets;
   private UUID currentMatchID;
   private UUID duelSelecting;
   private boolean acceptingDuels;
   private boolean allowingSpectators;
   private boolean scoreboardEnabled;
   private boolean tab;
   private int minemanID;
   private int eloRange;
   private int pingRange;
   private int teamID;
   private int rematchID;
   private int missedPots;
   private int longestCombo;
   private int combo;
   private int hits;

   public int getWins(String kitName) {
      return (Integer)this.rankedWins.computeIfAbsent(kitName, (k) -> 0);
   }

   public void setWins(String kitName, int wins) {
      this.rankedWins.put(kitName, wins);
   }

   public int getLosses(String kitName) {
      return (Integer)this.rankedLosses.computeIfAbsent(kitName, (k) -> 0);
   }

   public void setLosses(String kitName, int losses) {
      this.rankedLosses.put(kitName, losses);
   }

   public int getElo(String kitName) {
      return (Integer)this.rankedElo.computeIfAbsent(kitName, (k) -> 1000);
   }

   public void setElo(String kitName, int elo) {
      this.rankedElo.put(kitName, elo);
   }

   public int getPartyElo(String kitName) {
      return (Integer)this.partyElo.computeIfAbsent(kitName, (k) -> 1000);
   }

   public void setPartyElo(String kitName, int elo) {
      this.partyElo.put(kitName, elo);
   }

   public void addPlayerKit(int index, PlayerKit playerKit) {
      this.getPlayerKits(playerKit.getName()).put(index, playerKit);
   }

   public Map<Integer, PlayerKit> getPlayerKits(String kitName) {
      return (Map)this.playerKits.computeIfAbsent(kitName, (k) -> new HashMap());
   }

   public int getGlobalElo() {
      int[] i = new int[]{0};
      int[] count = new int[]{0};
      Carbon.getInstance().getKitManager().getKits().forEach((kit) -> {
         i[0] += this.getElo(kit.getName());
         int var10002 = count[0]++;
      });
      i[0] = i[0] == 0 ? 1 : i[0];
      count[0] = count[0] == 0 ? 1 : count[0];
      return Math.round((float)(i[0] / count[0]));
   }

   public Map<String, Map<Integer, PlayerKit>> getPlayerKits() {
      return this.playerKits;
   }

   public Map<String, Integer> getRankedLosses() {
      return this.rankedLosses;
   }

   public Map<String, Integer> getRankedWins() {
      return this.rankedWins;
   }

   public Map<String, Integer> getRankedElo() {
      return this.rankedElo;
   }

   public Map<String, Integer> getPartyElo() {
      return this.partyElo;
   }

   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public PlayerState getPlayerState() {
      return this.playerState;
   }

   public List<Location> getPotions() {
      return this.potions;
   }

   public List<Packet> getPackets() {
      return this.packets;
   }

   public UUID getCurrentMatchID() {
      return this.currentMatchID;
   }

   public UUID getDuelSelecting() {
      return this.duelSelecting;
   }

   public boolean isAcceptingDuels() {
      return this.acceptingDuels;
   }

   public boolean isAllowingSpectators() {
      return this.allowingSpectators;
   }

   public boolean isScoreboardEnabled() {
      return this.scoreboardEnabled;
   }

   public boolean isTab() {
      return this.tab;
   }

   public int getMinemanID() {
      return this.minemanID;
   }

   public int getEloRange() {
      return this.eloRange;
   }

   public int getPingRange() {
      return this.pingRange;
   }

   public int getTeamID() {
      return this.teamID;
   }

   public int getRematchID() {
      return this.rematchID;
   }

   public int getMissedPots() {
      return this.missedPots;
   }

   public int getLongestCombo() {
      return this.longestCombo;
   }

   public int getCombo() {
      return this.combo;
   }

   public int getHits() {
      return this.hits;
   }

   public void setPlayerState(PlayerState playerState) {
      this.playerState = playerState;
   }

   public void setPotions(List<Location> potions) {
      this.potions = potions;
   }

   public void setPackets(List<Packet> packets) {
      this.packets = packets;
   }

   public void setCurrentMatchID(UUID currentMatchID) {
      this.currentMatchID = currentMatchID;
   }

   public void setDuelSelecting(UUID duelSelecting) {
      this.duelSelecting = duelSelecting;
   }

   public void setAcceptingDuels(boolean acceptingDuels) {
      this.acceptingDuels = acceptingDuels;
   }

   public void setAllowingSpectators(boolean allowingSpectators) {
      this.allowingSpectators = allowingSpectators;
   }

   public void setScoreboardEnabled(boolean scoreboardEnabled) {
      this.scoreboardEnabled = scoreboardEnabled;
   }

   public void setTab(boolean tab) {
      this.tab = tab;
   }

   public void setMinemanID(int minemanID) {
      this.minemanID = minemanID;
   }

   public void setEloRange(int eloRange) {
      this.eloRange = eloRange;
   }

   public void setPingRange(int pingRange) {
      this.pingRange = pingRange;
   }

   public void setTeamID(int teamID) {
      this.teamID = teamID;
   }

   public void setRematchID(int rematchID) {
      this.rematchID = rematchID;
   }

   public void setMissedPots(int missedPots) {
      this.missedPots = missedPots;
   }

   public void setLongestCombo(int longestCombo) {
      this.longestCombo = longestCombo;
   }

   public void setCombo(int combo) {
      this.combo = combo;
   }

   public void setHits(int hits) {
      this.hits = hits;
   }

   @ConstructorProperties({"uniqueId"})
   public PlayerData(UUID uniqueId) {
      this.playerState = PlayerState.LOADING;
      this.potions = new ArrayList();
      this.packets = new ArrayList();
      this.acceptingDuels = true;
      this.allowingSpectators = true;
      this.scoreboardEnabled = true;
      this.tab = true;
      this.minemanID = -1;
      this.eloRange = 250;
      this.pingRange = 50;
      this.teamID = -1;
      this.rematchID = -1;
      this.uniqueId = uniqueId;
   }
}
