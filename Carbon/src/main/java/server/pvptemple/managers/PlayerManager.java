package server.pvptemple.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.abstr.AbstractBukkitCallback;
import server.pvptemple.kit.Kit;
import server.pvptemple.kit.PlayerKit;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.request.PracticeFetchStatsRequest;
import server.pvptemple.request.UpdateStatsRequest;
import server.pvptemple.timer.impl.EnderpearlTimer;
import server.pvptemple.util.Config;
import server.pvptemple.util.PlayerUtil;

public class PlayerManager {
   private final Carbon plugin = Carbon.getInstance();
   private final Map<UUID, PlayerData> playerData = new ConcurrentHashMap();

   public void createPlayerData(Player player) {
      PlayerData data = new PlayerData(player.getUniqueId());
      this.playerData.put(data.getUniqueId(), data);
      this.loadData(data);
   }

   private void loadData(final PlayerData playerData) {
      Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(playerData.getUniqueId());
      if (mineman != null && mineman.isDataLoaded() && !mineman.isErrorLoadingData()) {
         playerData.setMinemanID(mineman.getId());
         playerData.setPlayerState(PlayerState.SPAWN);
         CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new PracticeFetchStatsRequest(playerData.getUniqueId()), new AbstractBukkitCallback() {
            public void callback(JsonElement jsonElement) {
               if (!jsonElement.isJsonNull()) {
                  JsonObject stats = jsonElement.getAsJsonObject();
                  JsonElement element = stats.get("allowingSpectators");
                  if (element != null && !element.isJsonNull()) {
                     playerData.setAllowingSpectators(element.getAsBoolean());
                  }

                  element = stats.get("acceptingDuels");
                  if (element != null && !element.isJsonNull()) {
                     playerData.setAcceptingDuels(element.getAsBoolean());
                  }

                  element = stats.get("scoreboardEnabled");
                  if (element != null && !element.isJsonNull()) {
                     playerData.setScoreboardEnabled(element.getAsBoolean());
                  }

                  element = stats.get("pingRange");
                  if (element != null && !element.isJsonNull()) {
                     playerData.setPingRange(element.getAsInt());
                  }

                  element = stats.get("eloRange");
                  if (element != null && !element.isJsonNull()) {
                     playerData.setEloRange(element.getAsInt());
                  }

                  for(Kit kit : Carbon.getInstance().getKitManager().getKits()) {
                     String kitName = kit.getName();
                     element = stats.get(kitName.toLowerCase() + "Elo");
                     if (element != null && !element.isJsonNull()) {
                        playerData.setElo(kitName, element.getAsInt());
                     }

                     element = stats.get(kitName + "Wins");
                     if (element != null && !element.isJsonNull()) {
                        playerData.setWins(kitName, element.getAsInt());
                     }

                     element = stats.get(kitName + "Losses");
                     if (element != null && !element.isJsonNull()) {
                        playerData.setLosses(kitName, element.getAsInt());
                     }

                     element = stats.get(kitName + "EloParty");
                     if (element != null && !element.isJsonNull()) {
                        playerData.setPartyElo(kitName, element.getAsInt());
                     }
                  }
               }

               Config config = new Config("/players/" + playerData.getUniqueId().toString(), PlayerManager.this.plugin);
               ConfigurationSection playerKitsSection = config.getConfig().getConfigurationSection("playerkits");
               if (playerKitsSection != null) {
                  PlayerManager.this.plugin.getKitManager().getKits().forEach((kitx) -> {
                     ConfigurationSection kitSection = playerKitsSection.getConfigurationSection(kitx.getName());
                     if (kitSection != null) {
                        kitSection.getKeys(false).forEach((kitKey) -> {
                           Integer kitIndex = Integer.parseInt(kitKey);
                           String displayName = kitSection.getString(kitKey + ".displayName");
                           ItemStack[] contents = (ItemStack[])((List)kitSection.get(kitKey + ".contents")).toArray(new ItemStack[0]);
                           PlayerKit playerKit = new PlayerKit(kitx.getName(), kitIndex, contents, displayName);
                           playerData.addPlayerKit(kitIndex, playerKit);
                        });
                     }

                  });
               }

            }

            public void onError(String message) {
               super.onError(message);
               PlayerManager.this.plugin.getLogger().severe("Error fetching practice stats for " + playerData.getUniqueId());
            }
         });
      }
   }

   public void removePlayerData(UUID uuid) {
      this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
         this.saveData((PlayerData)this.playerData.get(uuid));
         this.playerData.remove(uuid);
      });
   }

   public void saveData(PlayerData playerData) {
      if (playerData == null) {
         throw new IllegalArgumentException("playerData null");
      } else {
         Config config = new Config("/players/" + playerData.getUniqueId().toString(), this.plugin);
         this.plugin.getKitManager().getKits().forEach((kit) -> {
            Map<Integer, PlayerKit> playerKits = playerData.getPlayerKits(kit.getName());
            if (playerKits != null) {
               playerKits.forEach((key, value) -> {
                  config.getConfig().set("playerkits." + kit.getName() + "." + key + ".displayName", value.getDisplayName());
                  config.getConfig().set("playerkits." + kit.getName() + "." + key + ".contents", value.getContents());
               });
            }

         });
         config.save();
         JSONObject data = new JSONObject();

         for(String kitName : this.plugin.getKitManager().getRankedKits()) {
            JSONObject kitData = new JSONObject();
            kitData.put("elo", playerData.getElo(kitName));
            kitData.put("party-elo", playerData.getPartyElo(kitName));
            data.put(kitName, kitData);
         }

         UpdateStatsRequest request = UpdateStatsRequest.builder().uuid(playerData.getUniqueId()).scoreboardEnabled(playerData.isScoreboardEnabled()).acceptingDuels(playerData.isAcceptingDuels()).allowingSpectators(playerData.isAllowingSpectators()).pingRange(playerData.getPingRange()).eloRange(playerData.getEloRange()).id(playerData.getMinemanID()).nodebuffEloParty(playerData.getPartyElo("NoDebuff")).nodebuffLosses(playerData.getLosses("NoDebuff")).nodebuffWins(playerData.getWins("NoDebuff")).nodebuffElo(playerData.getElo("NoDebuff")).debuffEloParty(playerData.getPartyElo("Debuff")).debuffLosses(playerData.getLosses("Debuff")).debuffWins(playerData.getWins("Debuff")).debuffElo(playerData.getElo("Debuff")).gappleEloParty(playerData.getPartyElo("Gapple")).gappleLosses(playerData.getLosses("Gapple")).gappleWins(playerData.getWins("Gapple")).gappleElo(playerData.getElo("Gapple")).archerEloParty(playerData.getPartyElo("Archer")).archerLosses(playerData.getLosses("Archer")).archerWins(playerData.getWins("Archer")).archerElo(playerData.getElo("Archer")).axeEloParty(playerData.getPartyElo("Axe")).axeLosses(playerData.getLosses("Axe")).axeWins(playerData.getWins("Axe")).axeElo(playerData.getElo("Axe")).classicEloParty(playerData.getPartyElo("Classic")).classicLosses(playerData.getLosses("Classic")).classicWins(playerData.getWins("Classic")).classicElo(playerData.getElo("Classic")).hcfEloParty(playerData.getPartyElo("HCF")).hcfLosses(playerData.getLosses("HCF")).hcfWins(playerData.getWins("HCF")).hcfElo(playerData.getElo("HCF")).sumoEloParty(playerData.getPartyElo("Sumo")).sumoLosses(playerData.getLosses("Sumo")).sumoWins(playerData.getWins("Sumo")).sumoElo(playerData.getElo("Sumo")).builduhcEloParty(playerData.getPartyElo("BuildUHC")).builduhcLosses(playerData.getLosses("BuildUHC")).builduhcWins(playerData.getWins("BuildUHC")).builduhcElo(playerData.getElo("BuildUHC")).build();
         CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(request);
      }
   }

   public Collection<PlayerData> getAllData() {
      return this.playerData.values();
   }

   public PlayerData getPlayerData(UUID uuid) {
      return (PlayerData)this.playerData.get(uuid);
   }

   public void giveLobbyItems(Player player) {
      boolean inParty = this.plugin.getPartyManager().getParty(player.getUniqueId()) != null;
      boolean inTournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null;
      boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;
      ItemStack[] items = this.plugin.getItemManager().getSpawnItems();
      if (inTournament) {
         items = this.plugin.getItemManager().getTournamentItems();
      } else if (inEvent) {
         items = this.plugin.getItemManager().getEventItems();
      } else if (inParty) {
         items = this.plugin.getItemManager().getPartyItems();
      }

      player.getInventory().setContents(items);
      player.updateInventory();
   }

   public void sendToSpawnAndReset(Player player) {
      PlayerUtil.clearPlayer(player);
      ((EnderpearlTimer)CorePlugin.getInstance().getTimerManager().getTimer(EnderpearlTimer.class)).clearCooldown(player.getUniqueId());
      PlayerData playerData = this.getPlayerData(player.getUniqueId());
      if (!playerData.getPlayerState().equals(PlayerState.SPAWN)) {
         playerData.setPlayerState(PlayerState.SPAWN);
         player.teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
      }

      this.giveLobbyItems(player);
      if (player.isOnline()) {
         Bukkit.getOnlinePlayers().forEach((p) -> {
            player.hidePlayer(p);
            p.hidePlayer(player);
         });
         Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
         if (party != null) {
            party.getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach((member) -> {
               member.showPlayer(player);
               player.showPlayer(member);
            });
         }

      }
   }
}
