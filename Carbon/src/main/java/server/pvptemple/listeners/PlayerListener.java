package server.pvptemple.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.Bed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import server.pvptemple.Carbon;
import server.pvptemple.arena.StandaloneArena;
import server.pvptemple.event.player.MinemanRetrieveEvent;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.events.lms.LMSEvent;
import server.pvptemple.events.lms.LMSPlayer;
import server.pvptemple.events.oitc.OITCEvent;
import server.pvptemple.events.oitc.OITCPlayer;
import server.pvptemple.events.parkour.ParkourEvent;
import server.pvptemple.kit.Kit;
import server.pvptemple.kit.PlayerKit;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchState;
import server.pvptemple.match.MatchTeam;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.util.PlayerUtil;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.ItemUtil;

public class PlayerListener implements Listener {
   private final Carbon plugin = Carbon.getInstance();

   @EventHandler
   public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
      if (event.getItem().getType() == Material.GOLDEN_APPLE) {
         if (!event.getItem().hasItemMeta() || !event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
            return;
         }

         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(event.getPlayer().getUniqueId());
         if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            Player player = event.getPlayer();
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
            player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
         }
      }

   }

   @EventHandler
   public void onRegenerate(EntityRegainHealthEvent event) {
      if (event.getEntity() instanceof Player) {
         if (event.getRegainReason() == RegainReason.SATIATED) {
            Player player = (Player)event.getEntity();
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData.getPlayerState() == PlayerState.FIGHTING) {
               Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
               if (match.getKit().isBuild()) {
                  event.setCancelled(true);
               }
            }

         }
      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      this.plugin.getPlayerManager().createPlayerData(player);
      this.plugin.getPlayerManager().sendToSpawnAndReset(player);
      player.teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
      if (Bukkit.getOnlinePlayers().size() == 400) {
         this.plugin.getEventManager().setEnabled(false);
      }

   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      if (playerData != null) {
         switch (playerData.getPlayerState()) {
            case FIGHTING:
               this.plugin.getMatchManager().removeFighter(player, playerData, false);
               break;
            case SPECTATING:
               this.plugin.getMatchManager().removeSpectator(player);
               break;
            case EDITING:
               this.plugin.getEditorManager().removeEditor(player.getUniqueId());
               break;
            case QUEUE:
               if (party == null) {
                  this.plugin.getQueueManager().removePlayerFromQueue(player);
               } else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                  this.plugin.getQueueManager().removePartyFromQueue(party);
               }
               break;
            case EVENT:
               PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
               if (practiceEvent != null) {
                  practiceEvent.leave(player);
               }
         }

         this.plugin.getTournamentManager().leaveTournament(player);
         this.plugin.getPartyManager().leaveParty(player);
         this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
         this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
         this.plugin.getPlayerManager().removePlayerData(player.getUniqueId());

         for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer)onlinePlayer).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{((CraftPlayer)event.getPlayer()).getHandle()}));
         }

         if (Bukkit.getOnlinePlayers().size() <= 350) {
            this.plugin.getEventManager().setEnabled(true);
         }

      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerKickEvent event) {
      Player player = event.getPlayer();
      Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      if (playerData != null) {
         switch (playerData.getPlayerState()) {
            case FIGHTING:
               this.plugin.getMatchManager().removeFighter(player, playerData, false);
               break;
            case SPECTATING:
               this.plugin.getMatchManager().removeSpectator(player);
               break;
            case EDITING:
               this.plugin.getEditorManager().removeEditor(player.getUniqueId());
               break;
            case QUEUE:
               if (party == null) {
                  this.plugin.getQueueManager().removePlayerFromQueue(player);
               } else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                  this.plugin.getQueueManager().removePartyFromQueue(party);
               }
               break;
            case EVENT:
               PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
               if (practiceEvent != null) {
                  practiceEvent.leave(player);
               }
         }

         this.plugin.getTournamentManager().leaveTournament(player);
         this.plugin.getPartyManager().leaveParty(player);
         this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());
         this.plugin.getPartyManager().removePartyInvites(player.getUniqueId());
         this.plugin.getPlayerManager().removePlayerData(player.getUniqueId());

         for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer)onlinePlayer).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[]{((CraftPlayer)event.getPlayer()).getHandle()}));
         }

         if (Bukkit.getOnlinePlayers().size() <= 350) {
            this.plugin.getEventManager().setEnabled(true);
         }

      }
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (player.getGameMode() != GameMode.CREATIVE) {
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         if (playerData.getPlayerState() == PlayerState.SPECTATING) {
            event.setCancelled(true);
         }

         if (event.getAction().name().endsWith("_BLOCK")) {
            if (event.getClickedBlock().getType().name().contains("SIGN") && event.getClickedBlock().getState() instanceof Sign) {
               Sign sign = (Sign)event.getClickedBlock().getState();
               if (ChatColor.stripColor(sign.getLine(1)).equals("[Soup]")) {
                  event.setCancelled(true);
                  Inventory inventory = this.plugin.getServer().createInventory((InventoryHolder)null, 54, CC.DARK_GRAY + "Soup Refill");

                  for(int i = 0; i < 54; ++i) {
                     inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                  }

                  event.getPlayer().openInventory(inventory);
               }
            }

            if (event.getClickedBlock().getType() == Material.CHEST && playerData.getPlayerState() != PlayerState.EDITING || event.getClickedBlock().getType() == Material.ENDER_CHEST) {
               event.setCancelled(true);
            }
         }

         if (event.getAction().name().startsWith("RIGHT_")) {
            ItemStack item = event.getItem();
            Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
            switch (playerData.getPlayerState()) {
               case FIGHTING:
                  if (item == null) {
                     return;
                  }

                  Match match = this.plugin.getMatchManager().getMatch(playerData);
                  switch (item.getType()) {
                     case MUSHROOM_SOUP:
                        if (player.getHealth() <= (double)19.0F && !player.isDead()) {
                           if (player.getHealth() < (double)20.0F || player.getFoodLevel() < 20) {
                              player.getItemInHand().setType(Material.BOWL);
                           }

                           player.setHealth(player.getHealth() + (double)7.0F > (double)20.0F ? (double)20.0F : player.getHealth() + (double)7.0F);
                           player.setFoodLevel(player.getFoodLevel() + 2 > 20 ? 20 : player.getFoodLevel() + 2);
                           player.setSaturation(12.8F);
                           player.updateInventory();
                        }

                        return;
                     case ENDER_PEARL:
                        if (match.getMatchState() == MatchState.STARTING) {
                           event.setCancelled(true);
                           player.sendMessage(CC.RED + "Must wait before throwing an enderpearl.");
                           player.updateInventory();
                        }

                        return;
                     case ENCHANTED_BOOK:
                        Kit kit = match.getKit();
                        PlayerInventory inventory = player.getInventory();
                        int kitIndex = inventory.getHeldItemSlot();
                        if (kitIndex == 8) {
                           kit.applyToPlayer(player);
                        } else {
                           Map<Integer, PlayerKit> kits = playerData.getPlayerKits(kit.getName());
                           PlayerKit playerKit = (PlayerKit)kits.get(kitIndex + 1);
                           if (playerKit != null) {
                              playerKit.applyToPlayer(player);
                              return;
                           }
                        }

                        return;
                     default:
                        return;
                  }
               case SPECTATING:
                  if (item == null) {
                     return;
                  }

                  if (item.getType() == Material.NETHER_STAR) {
                     if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                        this.plugin.getEventManager().removeSpectator(player);
                     } else if (party == null) {
                        this.plugin.getMatchManager().removeSpectator(player);
                     } else {
                        this.plugin.getPartyManager().leaveParty(player);
                     }
                  }
                  break;
               case EDITING:
                  if (event.getClickedBlock() == null) {
                     return;
                  }

                  switch (event.getClickedBlock().getType()) {
                     case ENDER_PEARL:
                        event.setCancelled(true);
                        return;
                     case ENCHANTED_BOOK:
                     case DIAMOND_SWORD:
                     case IRON_SWORD:
                     case BLAZE_POWDER:
                     case PAPER:
                     case NAME_TAG:
                     case BOOK:
                     case WATCH:
                     case DIAMOND_AXE:
                     case IRON_AXE:
                     case NETHER_STAR:
                     default:
                        return;
                     case WALL_SIGN:
                     case SIGN:
                     case SIGN_POST:
                        this.plugin.getEditorManager().removeEditor(player.getUniqueId());
                        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
                        return;
                     case CHEST:
                        Kit kit = this.plugin.getKitManager().getKit(this.plugin.getEditorManager().getEditingKit(player.getUniqueId()));
                        if (kit.getKitEditContents()[0] != null) {
                           Inventory editorInventory = this.plugin.getServer().createInventory((InventoryHolder)null, 36);
                           editorInventory.setContents(kit.getKitEditContents());
                           player.openInventory(editorInventory);
                        }

                        event.setCancelled(true);
                        return;
                     case ANVIL:
                        player.openInventory(this.plugin.getInventoryManager().getEditingKitInventory(player.getUniqueId()).getCurrentPage());
                        event.setCancelled(true);
                        return;
                  }
               case QUEUE:
                  if (item == null) {
                     return;
                  }

                  if (item.getType() == Material.REDSTONE) {
                     if (party == null) {
                        this.plugin.getQueueManager().removePlayerFromQueue(player);
                     } else {
                        this.plugin.getQueueManager().removePartyFromQueue(party);
                     }
                  }
                  break;
               case EVENT:
                  if (item == null) {
                     return;
                  }

                  PracticeEvent practiceEvent = this.plugin.getEventManager().getEventPlaying(player);
                  if (item.getType() == Material.NETHER_STAR) {
                     if (practiceEvent != null) {
                        practiceEvent.leave(player);
                     }
                  } else if (item.getType() == Material.WATCH) {
                     player.performCommand("settings");
                  } else if (item.getType() == Material.EYE_OF_ENDER) {
                     if (practiceEvent != null && practiceEvent instanceof ParkourEvent) {
                        ((ParkourEvent)practiceEvent).toggleVisibility(player);
                     }
                  } else if (item.getType() == Material.MUSHROOM_SOUP && player.getHealth() <= (double)19.0F && !player.isDead()) {
                     if (player.getHealth() < (double)20.0F || player.getFoodLevel() < 20) {
                        player.getItemInHand().setType(Material.BOWL);
                     }

                     player.setHealth(player.getHealth() + (double)7.0F > (double)20.0F ? (double)20.0F : player.getHealth() + (double)7.0F);
                     player.setFoodLevel(player.getFoodLevel() + 2 > 20 ? 20 : player.getFoodLevel() + 2);
                     player.setSaturation(12.8F);
                     player.updateInventory();
                  }
                  break;
               case LOADING:
                  player.sendMessage(CC.RED + "You must wait until your player data has loaded before you can use items.");
                  break;
               case FFA:
                  if (item == null) {
                     return;
                  }

                  switch (item.getType()) {
                     case MUSHROOM_SOUP:
                        if (player.getHealth() <= (double)19.0F && !player.isDead()) {
                           if (player.getHealth() < (double)20.0F || player.getFoodLevel() < 20) {
                              player.getItemInHand().setType(Material.BOWL);
                           }

                           player.setHealth(player.getHealth() + (double)7.0F > (double)20.0F ? (double)20.0F : player.getHealth() + (double)7.0F);
                           player.setFoodLevel(player.getFoodLevel() + 2 > 20 ? 20 : player.getFoodLevel() + 2);
                           player.setSaturation(12.8F);
                           player.updateInventory();
                        }

                        return;
                     default:
                        return;
                  }
               case SPAWN:
                  if (item == null) {
                     return;
                  }

                  switch (item.getType()) {
                     case DIAMOND_SWORD:
                        if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                           player.sendMessage(CC.RED + "Only the party leader can join the 2v2 queue.");
                           return;
                        }

                        player.openInventory(this.plugin.getInventoryManager().getRankedInventory().getCurrentPage());
                        break;
                     case IRON_SWORD:
                        if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                           player.sendMessage(CC.RED + "Only the party leader can join the 2v2 queue.");
                           return;
                        }

                        player.openInventory(this.plugin.getInventoryManager().getUnrankedInventory().getCurrentPage());
                        break;
                     case BLAZE_POWDER:
                        UUID rematching = this.plugin.getMatchManager().getRematcher(player.getUniqueId());
                        Player rematcher = this.plugin.getServer().getPlayer(rematching);
                        if (rematcher == null) {
                           player.sendMessage(CC.RED + "Failed to find that player.");
                           return;
                        }

                        if (this.plugin.getMatchManager().getMatchRequest(rematcher.getUniqueId(), player.getUniqueId()) != null) {
                           this.plugin.getServer().dispatchCommand(player, "accept " + rematcher.getName());
                        } else {
                           this.plugin.getServer().dispatchCommand(player, "duel " + rematcher.getName());
                        }
                        break;
                     case PAPER:
                        player.performCommand("party list");
                        break;
                     case NAME_TAG:
                        this.plugin.getPartyManager().createParty(player);
                        break;
                     case BOOK:
                        player.openInventory(this.plugin.getInventoryManager().getEditorInventory().getCurrentPage());
                        break;
                     case WATCH:
                        player.performCommand("settings");
                        break;
                     case DIAMOND_AXE:
                        if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                           player.sendMessage(CC.RED + "Only the party leader can start events.");
                           return;
                        }

                        player.openInventory(this.plugin.getInventoryManager().getPartyEventInventory().getCurrentPage());
                        break;
                     case IRON_AXE:
                        if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                           player.sendMessage(CC.RED + "Only the party leader can start events.");
                           return;
                        }

                        player.openInventory(this.plugin.getInventoryManager().getPartyInventory().getCurrentPage());
                        break;
                     case NETHER_STAR:
                        this.plugin.getPartyManager().leaveParty(player);
                        this.plugin.getTournamentManager().leaveTournament(player);
                  }
            }
         }

      }
   }

   @EventHandler
   public void onPlayerDropItem(PlayerDropItemEvent event) {
      Player player = event.getPlayer();
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      Material drop = event.getItemDrop().getItemStack().getType();
      switch (playerData.getPlayerState()) {
         case FIGHTING:
            if (drop == Material.ENCHANTED_BOOK) {
               event.setCancelled(true);
            } else if (drop == Material.GLASS_BOTTLE) {
               event.getItemDrop().remove();
            } else {
               Match match = this.plugin.getMatchManager().getMatch(event.getPlayer().getUniqueId());
               this.plugin.getMatchManager().addDroppedItem(match, event.getItemDrop());
               List<Player> whitelisted = new ArrayList();
               match.getTeams().forEach((team) -> team.players().forEach(whitelisted::add));
               Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                  for(Player player1 : Bukkit.getOnlinePlayers()) {
                     if (!whitelisted.contains(player1)) {
                        ((CraftPlayer)player1).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(new int[]{event.getItemDrop().getEntityId()}));
                     }
                  }

               }, 1L);
            }
            break;
         case EVENT:
            event.setCancelled(true);
            PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);
            if (currentEvent != null && currentEvent instanceof LMSEvent) {
               LMSEvent oitcEvent = (LMSEvent)currentEvent;
               LMSPlayer oitcPlayer = (LMSPlayer)oitcEvent.getPlayer(player);
               if (oitcPlayer.getState() == LMSPlayer.LMSState.FIGHTING) {
                  if (drop != Material.BOWL) {
                     event.setCancelled(true);
                  } else {
                     event.setCancelled(false);
                     event.getItemDrop().remove();
                  }
               } else {
                  event.setCancelled(true);
               }
            }
            break;
         case FFA:
            if (drop != Material.BOWL) {
               event.setCancelled(true);
            } else {
               event.getItemDrop().remove();
            }
            break;
         default:
            event.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerPickupItem(PlayerPickupItemEvent event) {
      Player player = event.getPlayer();
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      if (playerData.getPlayerState() == PlayerState.FIGHTING) {
         Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
         if (match.getEntitiesToRemove().contains(event.getItem())) {
            match.removeEntityToRemove(event.getItem());
         } else {
            event.setCancelled(true);
         }
      } else if (playerData.getPlayerState() != PlayerState.FFA) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
      String chatMessage = event.getMessage();
      if (party != null) {
         if (chatMessage.startsWith("!") || chatMessage.startsWith("@")) {
            event.setCancelled(true);
            String message = CC.GRAY + "[" + CC.BLUE + "Party" + CC.GRAY + "] " + CC.BLUE + player.getName() + CC.AQUA + ": " + chatMessage.replaceFirst("!", "").replaceFirst("@", "");
            party.broadcast(message);
         }
      } else {
         PlayerKit kitRenaming = this.plugin.getEditorManager().getRenamingKit(player.getUniqueId());
         if (kitRenaming != null) {
            kitRenaming.setDisplayName(ChatColor.translateAlternateColorCodes('&', chatMessage));
            event.setCancelled(true);
            event.getPlayer().sendMessage(CC.GREEN + "Successfully renamed kit #" + kitRenaming.getIndex() + "'s name to " + kitRenaming.getDisplayName() + ".");
            this.plugin.getEditorManager().removeRenamingKit(event.getPlayer().getUniqueId());
         }
      }

   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      event.setDeathMessage((String)null);
      Player player = event.getEntity();
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      playerData.getPotions().clear();
      playerData.getPackets().clear();
      switch (playerData.getPlayerState()) {
         case FIGHTING:
            Match match = this.plugin.getMatchManager().getMatch(playerData);
            if (match.getKit().isBedwars() && !match.isParty()) {
               StandaloneArena arena = match.getStandaloneArena();
               MatchTeam team1 = (MatchTeam)match.getTeams().get(0);
               MatchTeam team2 = (MatchTeam)match.getTeams().get(1);
               if (team1.getTeamID() == playerData.getTeamID()) {
                  if (team1.getTeamID() == 1) {
                     BlockState state = arena.getBedA().toBukkitLocation().getBlock().getState();
                     if (!arena.getBedA().toBukkitLocation().getBlock().getType().equals(Material.BED_BLOCK) && (this.adjustBlockForDoubleBlocks(state) == null || !this.adjustBlockForDoubleBlocks(state).getBlock().getType().equals(Material.BED_BLOCK))) {
                        this.plugin.getMatchManager().removeFighter(player, playerData, true);
                     } else {
                        this.respawnPlayer(player, arena.getA().toBukkitLocation());
                     }
                  } else {
                     BlockState state = arena.getBedB().toBukkitLocation().getBlock().getState();
                     if (!arena.getBedB().toBukkitLocation().getBlock().getType().equals(Material.BED_BLOCK) && (this.adjustBlockForDoubleBlocks(state) == null || !this.adjustBlockForDoubleBlocks(state).getBlock().getType().equals(Material.BED_BLOCK))) {
                        this.plugin.getMatchManager().removeFighter(player, playerData, true);
                     } else {
                        this.respawnPlayer(player, arena.getB().toBukkitLocation());
                     }
                  }
               } else if (team2.getTeamID() == 1) {
                  BlockState state = arena.getBedA().toBukkitLocation().getBlock().getState();
                  if (!arena.getBedA().toBukkitLocation().getBlock().getType().equals(Material.BED_BLOCK) && (this.adjustBlockForDoubleBlocks(state) == null || !this.adjustBlockForDoubleBlocks(state).getBlock().getType().equals(Material.BED_BLOCK))) {
                     this.plugin.getMatchManager().removeFighter(player, playerData, true);
                  } else {
                     this.respawnPlayer(player, arena.getA().toBukkitLocation());
                  }
               } else {
                  BlockState state = arena.getBedB().toBukkitLocation().getBlock().getState();
                  if (!arena.getBedB().toBukkitLocation().getBlock().getType().equals(Material.BED_BLOCK) && (this.adjustBlockForDoubleBlocks(state) == null || !this.adjustBlockForDoubleBlocks(state).getBlock().getType().equals(Material.BED_BLOCK))) {
                     this.plugin.getMatchManager().removeFighter(player, playerData, true);
                  } else {
                     this.respawnPlayer(player, arena.getB().toBukkitLocation());
                  }
               }
            } else {
               this.plugin.getMatchManager().removeFighter(player, playerData, true);
            }
            break;
         case EVENT:
            PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);
            if (currentEvent != null) {
               if (currentEvent instanceof OITCEvent) {
                  OITCEvent oitcEvent = (OITCEvent)currentEvent;
                  OITCPlayer oitcKiller = (OITCPlayer)oitcEvent.getPlayer(player.getKiller());
                  OITCPlayer oitcPlayer = (OITCPlayer)oitcEvent.getPlayer(player);
                  oitcPlayer.setLastKiller(oitcKiller);
                  PlayerUtil.respawnPlayer(event);
               } else if (currentEvent.onDeath() != null) {
                  currentEvent.onDeath().accept(player);
               }
            }
      }

      event.getDrops().clear();
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPlayerRespawn(PlayerRespawnEvent event) {
      Player player = event.getPlayer();
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      switch (playerData.getPlayerState()) {
         case EVENT:
            PracticeEvent currentEvent = this.plugin.getEventManager().getEventPlaying(player);
            if (currentEvent != null && currentEvent instanceof OITCEvent) {
               event.setRespawnLocation(player.getLocation());
               currentEvent.onDeath().accept(player);
            }
         default:
      }
   }

   @EventHandler
   public void onFoodLevelChange(FoodLevelChangeEvent event) {
      Player player = (Player)event.getEntity();
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      if (playerData.getPlayerState() == PlayerState.FIGHTING) {
         Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());
         if (match.getKit().isSumo()) {
            event.setCancelled(true);
         }
      } else {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onProjectileLaunch(ProjectileLaunchEvent event) {
      if (event.getEntity().getShooter() instanceof Player) {
         Player shooter = (Player)event.getEntity().getShooter();
         PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
         if (shooterData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());
            match.addEntityToRemove(event.getEntity());
         }
      }

   }

   @EventHandler
   public void onProjectileHit(ProjectileHitEvent event) {
      if (event.getEntity().getShooter() instanceof Player) {
         Player shooter = (Player)event.getEntity().getShooter();
         PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
         if (shooterData != null && shooterData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(shooter.getUniqueId());
            match.removeEntityToRemove(event.getEntity());
            if (event.getEntityType() == EntityType.ARROW) {
               event.getEntity().remove();
            }
         }
      }

   }

   @EventHandler
   public void onMinemanRetrieve(MinemanRetrieveEvent event) {
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(event.getUniqueId());
      if (playerData != null) {
         playerData.setMinemanID(event.getMineman().getId());
      }

   }

   private Location adjustBlockForDoubleBlocks(BlockState blockState) {
      if (blockState instanceof Bed) {
         Bed bed = (Bed)blockState.getData();
         return bed.isHeadOfBed() ? blockState.getBlock().getRelative(bed.getFacing().getOppositeFace()).getState().getLocation() : blockState.getLocation();
      } else {
         if (blockState.getLocation().getBlock().getType().equals(Material.AIR)) {
            if (blockState.getBlock().getRelative(BlockFace.EAST).getType().equals(Material.BED_BLOCK)) {
               return blockState.getLocation();
            }

            if (blockState.getBlock().getRelative(BlockFace.NORTH).getType().equals(Material.BED_BLOCK)) {
               return blockState.getLocation();
            }

            if (blockState.getBlock().getRelative(BlockFace.WEST).getType().equals(Material.BED_BLOCK)) {
               return blockState.getLocation();
            }

            if (blockState.getBlock().getRelative(BlockFace.SOUTH).getType().equals(Material.BED_BLOCK)) {
               return blockState.getLocation();
            }
         }

         return null;
      }
   }

   @EventHandler
   public void onFnsInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      ItemStack fns = player.getItemInHand();
      if (fns.getType() == Material.FLINT_AND_STEEL && fns.getType() != null) {
         if (fns.getDurability() > 10) {
            fns.setDurability((short)100);
         }

         if (fns.getDurability() < 10) {
            fns.setDurability((short)32);
         }
      }

   }

   private void respawnPlayer(Player player, Location location) {
      Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
         player.spigot().respawn();
         player.teleport(location);
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         Match match = this.plugin.getMatchManager().getMatch(playerData);
         Collection<PlayerKit> playerKits = playerData.getPlayerKits(match.getKit().getName()).values();
         if (playerKits.size() == 0) {
            match.getKit().applyToPlayer(player);
         } else {
            player.getInventory().setItem(8, this.plugin.getItemManager().getDefaultBook());
            int slot = -1;

            for(PlayerKit playerKit : playerKits) {
               PlayerInventory var10000 = player.getInventory();
               ++slot;
               var10000.setItem(slot, ItemUtil.createItem(Material.ENCHANTED_BOOK, CC.PRIMARY + playerKit.getDisplayName()));
            }
         }

         player.updateInventory();
      }, 2L);
   }
}
