package server.pvptemple.listeners;

import java.util.HashSet;
import java.util.UUID;

import com.minexd.spigot.SpigotX;
import com.minexd.spigot.knockback.KnockbackProfile;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import server.pvptemple.Carbon;
import server.pvptemple.clickable.Clickable;
import server.pvptemple.event.match.MatchEndEvent;
import server.pvptemple.event.match.MatchStartEvent;
import server.pvptemple.kit.Kit;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchState;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.queue.QueueType;
import server.pvptemple.runnable.MatchRunnable;
import server.pvptemple.util.ChatComponentBuilder;
import server.pvptemple.util.Color;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.EloCalculator;
import server.pvptemple.util.PlayerUtil;
import server.pvptemple.util.finalutil.CC;

public class MatchListener implements Listener {
   private final Carbon plugin = Carbon.getInstance();

   @EventHandler
   public void onMatchStart(MatchStartEvent event) {
      Match match = event.getMatch();
      Kit kit = match.getKit();
      if (!kit.isEnabled()) {
         match.broadcast(CC.RED + "This kit is currently disabled, try another kit.");
         this.plugin.getMatchManager().removeMatch(match);
      } else if (match.getArena().getAvailableArenas().size() <= 0) {
         match.broadcast(CC.RED + "There is no available arenas.");
         this.plugin.getMatchManager().removeMatch(match);
      } else {
         match.setStandaloneArena(match.getArena().getAvailableArena());
         this.plugin.getArenaManager().setArenaMatchUUID(match.getStandaloneArena(), match.getMatchId());
         HashSet<Player> matchPlayers = new HashSet<>();
         match.getTeams().forEach((team) -> team.alivePlayers().forEach((player) -> {
               matchPlayers.add(player);
               this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
               PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
               player.setAllowFlight(false);
               player.setFlying(false);
               playerData.setCurrentMatchID(match.getMatchId());
               playerData.setTeamID(team.getTeamID());
               playerData.setMissedPots(0);
               playerData.setLongestCombo(0);
               playerData.setCombo(0);
               playerData.setHits(0);
               PlayerUtil.clearPlayer(player);
               PlayerUtil.updateNametag(player, kit.isBuild());
               CustomLocation locationA = match.getStandaloneArena().getA();
               CustomLocation locationB = match.getStandaloneArena().getB();
               player.teleport(team.getTeamID() == 1 ? locationA.toBukkitLocation() : locationB.toBukkitLocation());
               if (kit.isCombo()) {
                  player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
                  player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999999, 0));
                  EntityHuman entityhuman = ((CraftPlayer)player).getHandle();
                  KnockbackProfile profile = SpigotX.INSTANCE.getConfig().getKbProfileByName("combo");
                  if (entityhuman.getKnockbackProfile() == null || !entityhuman.getKnockbackProfile().getName().equals("combo")) {
                     entityhuman.setKnockbackProfile(profile);
                  }
               }

               if (kit.isBuild()) {
                  EntityHuman entityHuman = ((CraftPlayer)player).getHandle();
                  KnockbackProfile profile = SpigotX.INSTANCE.getConfig().getKbProfileByName("uhc");
                  if (entityHuman.getKnockbackProfile() == null || !entityHuman.getKnockbackProfile().getName().equals("uhc")) {
                     entityHuman.setKnockbackProfile(profile);
                  }
               }

               if (kit.isSumo()) {
                  EntityHuman entityHuman = ((CraftPlayer)player).getHandle();
                  KnockbackProfile profile = SpigotX.INSTANCE.getConfig().getKbProfileByName("sumo");
                  if (entityHuman.getKnockbackProfile() == null || !entityHuman.getKnockbackProfile().getName().equals("sumo")) {
                     entityHuman.setKnockbackProfile(profile);
                  }
               }

               if (kit.getName().toLowerCase().contains("soup")) {
                  player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
               }

               if (kit.getName().toLowerCase().contains("gapple")) {
                  player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
                  player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 99999999, 1));
               }

               if (!match.isRedrover()) {
                  this.plugin.getMatchManager().giveKits(player, kit);
                  playerData.setPlayerState(PlayerState.FIGHTING);
               } else {
                  this.plugin.getMatchManager().addRedroverSpectator(player, match);
               }

            }));

         for(Player player : matchPlayers) {
            for(Player online : Bukkit.getOnlinePlayers()) {
               online.hidePlayer(player);
               player.hidePlayer(online);
            }
         }

         for(Player player : matchPlayers) {
            for(Player other : matchPlayers) {
               player.showPlayer(other);
            }
         }

         (new MatchRunnable(match)).runTaskTimer(this.plugin, 20L, 20L);
      }
   }

   @EventHandler
   public void onMatchEnd(MatchEndEvent event) {
      Match match = event.getMatch();
      match.broadcast("&7&m" + StringUtils.repeat("-", 45));
      match.broadcast("&6Match Results: &7(Click names to view inventories)");
      match.broadcast("");
      ChatComponentBuilder wInventories = new ChatComponentBuilder("");
      ChatComponentBuilder lInventories = new ChatComponentBuilder("");
      match.getTeams().forEach((team) -> team.players().forEach((player) -> {
            PlayerData data = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            data.getPotions().clear();
            data.getPackets().clear();
         }));
      match.setMatchState(MatchState.ENDING);
      match.setWinningTeamId(event.getWinningTeam().getTeamID());
      match.setCountdown(4);
      if (match.isFFA()) {
         Player winner = Bukkit.getPlayer((UUID)event.getWinningTeam().getAlivePlayers().get(0));
         lInventories.append("Losers: ").color(ChatColor.RED);
         event.getWinningTeam().players().forEach((player) -> {
            if (!match.hasSnapshot(player.getUniqueId())) {
               match.addSnapshot(player);
            }

            if (!player.getUniqueId().equals(winner.getUniqueId())) {
               HoverEvent hover = new HoverEvent(Action.SHOW_TEXT, (new ChatComponentBuilder(CC.RED + "View inventory.")).create());
               ClickEvent click = new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/inv " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
               lInventories.append(player.getName()).color(ChatColor.YELLOW);
               lInventories.setCurrentHoverEvent(hover).setCurrentClickEvent(click).append(", ").color(ChatColor.GRAY);
            }

         });
         match.getSnapshots().values().forEach((snapshot) -> this.plugin.getInventoryManager().addSnapshot(snapshot));
         Clickable winnerClickable = new Clickable(Color.translate("&aWinner: &e"));
         winnerClickable.add(winner.getName(), CC.GREEN + "View inventory", "/inv " + match.getSnapshot(winner.getUniqueId()).getSnapshotId());
         lInventories.getCurrent().setText(lInventories.getCurrent().getText().substring(0, lInventories.getCurrent().getText().length() - 2));
         match.broadcast(winnerClickable);
         match.broadcast(lInventories.create());
      } else if (match.isRedrover()) {
         match.broadcast(CC.GREEN + event.getWinningTeam().getLeaderName() + CC.GREEN + " has won the redrover!");
      } else {
         wInventories.append(match.isParty() ? "Winning Team: " : "Winner: ").color(ChatColor.GREEN);
         lInventories.append(match.isParty() ? "Losing Team: " : "Loser: ").color(ChatColor.RED);
         match.getTeams().forEach((team) -> team.players().forEach((player) -> {
               if (!match.hasSnapshot(player.getUniqueId())) {
                  match.addSnapshot(player);
               }

               boolean onWinningTeam = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getTeamID() == event.getWinningTeam().getTeamID();
               if (onWinningTeam) {
                  HoverEvent hover = new HoverEvent(Action.SHOW_TEXT, (new ChatComponentBuilder(CC.GREEN + "View inventory.")).create());
                  ClickEvent click = new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/inv " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
                  wInventories.append(player.getName()).color(ChatColor.YELLOW);
                  wInventories.setCurrentHoverEvent(hover).setCurrentClickEvent(click).append(", ").color(ChatColor.GRAY);
               } else {
                  HoverEvent hover = new HoverEvent(Action.SHOW_TEXT, (new ChatComponentBuilder(CC.RED + "View inventory.")).create());
                  ClickEvent click = new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/inv " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
                  lInventories.append(player.getName()).color(ChatColor.YELLOW);
                  lInventories.setCurrentHoverEvent(hover).setCurrentClickEvent(click).append(", ").color(ChatColor.GRAY);
               }

            }));
         match.getSnapshots().values().forEach((snapshot) -> this.plugin.getInventoryManager().addSnapshot(snapshot));
         wInventories.getCurrent().setText(wInventories.getCurrent().getText().substring(0, wInventories.getCurrent().getText().length() - 2));
         lInventories.getCurrent().setText(lInventories.getCurrent().getText().substring(0, lInventories.getCurrent().getText().length() - 2));
         match.broadcast(wInventories.create());
         match.broadcast(lInventories.create());
         if (match.getType().isRanked()) {
            Player winnerLeader = Bukkit.getPlayer((UUID)event.getWinningTeam().getPlayers().get(0));
            PlayerData winnerLeaderData = this.plugin.getPlayerManager().getPlayerData(winnerLeader.getUniqueId());
            Player loserLeader = Bukkit.getPlayer((UUID)event.getLosingTeam().getPlayers().get(0));
            PlayerData loserLeaderData = this.plugin.getPlayerManager().getPlayerData(loserLeader.getUniqueId());
            String kitName = match.getKit().getName();
            String eloMessage;
            if (event.getWinningTeam().getPlayers().size() == 2) {
               Player winnerMember = Bukkit.getPlayer((UUID)event.getWinningTeam().getPlayers().get(1));
               PlayerData winnerMemberData = this.plugin.getPlayerManager().getPlayerData(winnerMember.getUniqueId());
               Player loserMember = Bukkit.getPlayer((UUID)event.getLosingTeam().getPlayers().get(1));
               PlayerData loserMemberData = this.plugin.getPlayerManager().getPlayerData(loserMember.getUniqueId());
               int winnerElo = winnerLeaderData.getPartyElo(kitName);
               int loserElo = loserLeaderData.getPartyElo(kitName);
               int[] rankings = EloCalculator.getNewRankings(winnerElo, loserElo, true);
               winnerMemberData.setPartyElo(kitName, rankings[0]);
               loserMemberData.setPartyElo(kitName, rankings[1]);
               eloMessage = CC.YELLOW + "Updated ELO: " + CC.GREEN + winnerLeader.getName() + ", " + winnerMember.getName() + " +" + (rankings[0] - winnerElo) + " (" + rankings[0] + ") " + CC.GRAY + ", " + CC.RED + loserLeader.getName() + ", -" + loserMember.getName() + " " + (loserElo - rankings[1]) + " (" + rankings[1] + ")";
            } else {
               int winnerElo = winnerLeaderData.getElo(kitName);
               int loserElo = loserLeaderData.getElo(kitName);
               int[] rankings = EloCalculator.getNewRankings(winnerElo, loserElo, true);
               eloMessage = CC.YELLOW + "Updated ELO: " + CC.GREEN + winnerLeader.getName() + " +" + (rankings[0] - winnerElo) + " (" + rankings[0] + ")" + CC.GRAY + ", " + CC.RED + loserLeader.getName() + " -" + (loserElo - rankings[1]) + " (" + rankings[1] + ")";
               if (match.getType() == QueueType.RANKED) {
                  winnerLeaderData.setElo(kitName, rankings[0]);
                  loserLeaderData.setElo(kitName, rankings[1]);
                  winnerLeaderData.setWins(kitName, winnerLeaderData.getWins(kitName) + 1);
                  loserLeaderData.setLosses(kitName, loserLeaderData.getLosses(kitName) + 1);
               }
            }

            match.broadcast("");
            match.broadcast(eloMessage);
         }

         this.plugin.getMatchManager().saveRematches(match);
      }

      match.broadcast("&7&m" + StringUtils.repeat("-", 45));
   }
}
