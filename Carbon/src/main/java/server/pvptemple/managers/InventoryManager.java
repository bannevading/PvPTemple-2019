package server.pvptemple.managers;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.arena.Arena;
import server.pvptemple.clickable.Clickable;
import server.pvptemple.inventory.InventorySnapshot;
import server.pvptemple.inventory.InventoryUI;
import server.pvptemple.kit.Kit;
import server.pvptemple.kit.PlayerKit;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchTeam;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.queue.QueueType;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.Color;
import server.pvptemple.util.ItemBuilder;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.ItemUtil;
import server.pvptemple.util.finalutil.PlayerUtil;
import server.pvptemple.util.finalutil.StringUtil;

public class InventoryManager {
   private static final String MORE_PLAYERS;
   private final Carbon plugin = Carbon.getInstance();
   private final InventoryUI unrankedInventory;
   private final InventoryUI rankedInventory;
   private final InventoryUI editorInventory;
   private final InventoryUI duelInventory;
   private final InventoryUI partySplitInventory;
   private final InventoryUI partyFFAInventory;
   private final InventoryUI redroverInventory;
   private final InventoryUI joinPremiumInventory;
   private final InventoryUI partyEventInventory;
   private final InventoryUI partyInventory;
   private final InventoryUI hostInventory;
   private final Map<String, InventoryUI> duelMapInventories;
   private final Map<String, InventoryUI> partySplitMapInventories;
   private final Map<String, InventoryUI> partyFFAMapInventories;
   private final Map<String, InventoryUI> redroverMapInventories;
   private final Map<UUID, InventoryUI> editorInventories;
   private final Map<UUID, InventorySnapshot> snapshots;

   public InventoryManager() {
      this.unrankedInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select an unranked kit...", 2, 0);
      this.rankedInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select a ranked kit...", 2, 0);
      this.editorInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select an editable kit...", 2, 0);
      this.duelInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select a kit...", 2, 0);
      this.partySplitInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select a Party Split kit...", 2, 0);
      this.partyFFAInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select a Party FFA kit...", 2, 0);
      this.redroverInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select a Redrover kit...", 2, 0);
      this.joinPremiumInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Confirm Joining Premium", 1, 0);
      this.partyEventInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select an event...", 1, 0);
      this.partyInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Duel a party...", 6, 0);
      this.hostInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select an event...", 3, 0);
      this.duelMapInventories = new HashMap();
      this.partySplitMapInventories = new HashMap();
      this.partyFFAMapInventories = new HashMap();
      this.redroverMapInventories = new HashMap();
      this.editorInventories = new HashMap();
      this.snapshots = new HashMap();
      this.setupInventories();
      this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, this::updateInventories, 20L, 20L);
   }

   private void setupInventories() {
      for(final Kit kit : this.plugin.getKitManager().getKits()) {
         if (kit.isEnabled()) {
            this.unrankedInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
               public void onClick(InventoryClickEvent event) {
                  Player player = (Player)event.getWhoClicked();
                  InventoryManager.this.addToQueue(player, InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()), QueueType.UNRANKED);
               }
            });
            if (kit.isRanked()) {
               this.rankedInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
                  public void onClick(InventoryClickEvent event) {
                     Player player = (Player)event.getWhoClicked();
                     InventoryManager.this.addToQueue(player, InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()), QueueType.RANKED);
                  }
               });
            }

            this.editorInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getEditorStack()) {
               public void onClick(InventoryClickEvent event) {
                  Player player = (Player)event.getWhoClicked();
                  InventoryManager.this.plugin.getEditorManager().addEditor(player, kit);
                  InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).setPlayerState(PlayerState.EDITING);
               }
            });
            this.duelInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
               public void onClick(InventoryClickEvent event) {
                  InventoryManager.this.handleDuelClick((Player)event.getWhoClicked(), kit);
               }
            });
            this.partySplitInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
               public void onClick(InventoryClickEvent event) {
                  InventoryManager.this.handlePartySplitClick((Player)event.getWhoClicked(), kit);
               }
            });
            this.partyFFAInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
               public void onClick(InventoryClickEvent event) {
                  InventoryManager.this.handleFFAClick((Player)event.getWhoClicked(), kit);
               }
            });
            this.redroverInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
               public void onClick(InventoryClickEvent event) {
                  InventoryManager.this.handleRedroverClick((Player)event.getWhoClicked(), kit);
               }
            });
         }
      }

      this.hostInventory.setItem(10, new InventoryUI.AbstractClickableItem((new ItemBuilder(Material.WOOL)).durability(3).name("&a&l4Corners").lore(Arrays.asList("", "&7Stand on one of the", "&7color coordinated corners", "&7before the bridges and one", "&7corner is removed", "", "&6Needed rank: &fPrime", "", "&a» Click to host «")).build()) {
         public void onClick(InventoryClickEvent event) {
            Player player = (Player)event.getWhoClicked();
            if (!PlayerUtil.testPermission(player, Rank.PRIME)) {
               Rank rank = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getDisplayRank();
               player.sendMessage("");
               player.sendMessage(Color.translate("&cYou cannot host this event with " + rank.getColor() + rank.getName() + " &crank."));
               player.sendMessage(Color.translate("&7Purchase the Prime rank at shop.pvptemple.com to host events of your own."));
               player.sendMessage("");
            } else {
               player.performCommand("hostevent 4corners");
            }
         }
      });
      this.hostInventory.setItem(11, new InventoryUI.AbstractClickableItem((new ItemBuilder(Material.IRON_BOOTS)).name("&a&lRunner").lore(Arrays.asList("", "&7Run around the map while", "&7the blocks below you", "&7disappear. Last player", "&7alive wins.", "", "&6Needed rank: &fPrime", "", "&a» Click to host «")).build()) {
         public void onClick(InventoryClickEvent event) {
            Player player = (Player)event.getWhoClicked();
            if (!PlayerUtil.testPermission(player, Rank.PRIME)) {
               Rank rank = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getDisplayRank();
               player.sendMessage("");
               player.sendMessage(Color.translate("&cYou cannot host this event with " + rank.getColor() + rank.getName() + " &crank."));
               player.sendMessage(Color.translate("&7Purchase the Prime rank at shop.pvptemple.com to host events of your own."));
               player.sendMessage("");
            } else {
               player.performCommand("hostevent runner");
            }
         }
      });
      this.hostInventory.setItem(12, new InventoryUI.AbstractClickableItem((new ItemBuilder(Material.DIAMOND_SWORD)).name("&a&lLast Man Standing").lore(Arrays.asList("", "&7Players fight until death", "&7in an arena styled game mode.", "&7The last player standing wins.", "", "&6Needed rank: &fPrime", "", "&a» Click to host «")).build()) {
         public void onClick(InventoryClickEvent event) {
            Player player = (Player)event.getWhoClicked();
            if (!PlayerUtil.testPermission(player, Rank.PRIME)) {
               Rank rank = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getDisplayRank();
               player.sendMessage("");
               player.sendMessage(Color.translate("&cYou cannot host this event with " + rank.getColor() + rank.getName() + " &crank."));
               player.sendMessage(Color.translate("&7Purchase the Prime rank at shop.pvptemple.com to host events of your own."));
               player.sendMessage("");
            } else {
               player.performCommand("hostevent lms");
            }
         }
      });
      this.hostInventory.setItem(13, new InventoryUI.AbstractClickableItem((new ItemBuilder(Material.LEASH)).name("&6&lSumo").lore(Arrays.asList("", "&7Knock your opponents off", "&7the platform until you", "&7are the last player alive.", "", "&6Needed rank: &fElite", "", "&a» Click to host «")).build()) {
         public void onClick(InventoryClickEvent event) {
            Player player = (Player)event.getWhoClicked();
            if (!PlayerUtil.testPermission(player, Rank.ELITE)) {
               Rank rank = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getDisplayRank();
               player.sendMessage("");
               player.sendMessage(Color.translate("&cYou cannot host this event with " + rank.getColor() + rank.getName() + " &crank."));
               player.sendMessage(Color.translate("&7Purchase the Elite rank at shop.pvptemple.com to host events of your own."));
               player.sendMessage("");
               player.sendMessage("");
            } else {
               player.performCommand("hostevent sumo");
            }
         }
      });
      this.hostInventory.setItem(14, new InventoryUI.AbstractClickableItem((new ItemBuilder(Material.FEATHER)).name("&6&lParkour").lore(Arrays.asList("", "&7Jump through a series of", "&7obstacles to reach the end of the", "&7course. First player to the end wins.", "", "&6Needed rank: &fElite", "", "&a» Click to host «")).build()) {
         public void onClick(InventoryClickEvent event) {
            Player player = (Player)event.getWhoClicked();
            if (!PlayerUtil.testPermission(player, Rank.ELITE)) {
               Rank rank = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getDisplayRank();
               player.sendMessage("");
               player.sendMessage(Color.translate("&cYou cannot host this event with " + rank.getColor() + rank.getName() + " &crank."));
               player.sendMessage(Color.translate("&7Purchase the Elite rank at shop.pvptemple.com to host events of your own."));
               player.sendMessage("");
            } else {
               player.performCommand("hostevent parkour");
            }
         }
      });
      this.hostInventory.setItem(15, new InventoryUI.AbstractClickableItem((new ItemBuilder(Material.BOW)).name("&3&lOITC").lore(Arrays.asList("", "&7Be the first player to", "&7reach 20 kills. Players", "&7get one arrow per life which", "&7is an instant kill.", "", "&6Needed rank: &fMaster", "", "&a» Click to host «")).build()) {
         public void onClick(InventoryClickEvent event) {
            Player player = (Player)event.getWhoClicked();
            if (!PlayerUtil.testPermission(player, Rank.ELITE)) {
               Rank rank = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getDisplayRank();
               player.sendMessage("");
               player.sendMessage(Color.translate("&cYou cannot host this event with " + rank.getColor() + rank.getName() + " &crank."));
               player.sendMessage(Color.translate("&7Purchase the Master rank at shop.pvptemple.com to host events of your own."));
               player.sendMessage("");
            } else {
               player.performCommand("hostevent oitc");
            }
         }
      });
      this.hostInventory.setItem(16, new InventoryUI.AbstractClickableItem((new ItemBuilder(Material.INK_SACK)).durability(6).name("&3&lTournaments").lore(Arrays.asList("", "&7Players face opponents with", "&7a specific kit and advance", "&7to the upcomming rounds. Last", "&7player standing wins.", "", "&6Needed rank: &fMaster", "", "&a» Click to host «")).build()) {
         public void onClick(InventoryClickEvent event) {
            Player player = (Player)event.getWhoClicked();
            if (!PlayerUtil.testPermission(player, Rank.MASTER)) {
               Rank rank = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getDisplayRank();
               player.sendMessage(Color.translate("&eYou cannot host this event with " + rank.getColor() + rank.getName() + " &erank."));
               player.sendMessage(Color.translate("&7Purchase the Master rank at shop.pvptemple.com to host events of your own."));
            } else {
               player.performCommand("tournament create NoDebuff");
            }
         }
      });
      this.partyEventInventory.setItem(2, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.LEASH, CC.PRIMARY + "Party Split")) {
         public void onClick(InventoryClickEvent event) {
            Player player = (Player)event.getWhoClicked();
            player.closeInventory();
            player.openInventory(InventoryManager.this.getPartySplitInventory().getCurrentPage());
         }
      });
      this.partyEventInventory.setItem(4, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.BLAZE_ROD, CC.PRIMARY + "Party FFA")) {
         public void onClick(InventoryClickEvent event) {
            Player player = (Player)event.getWhoClicked();
            player.closeInventory();
            player.openInventory(InventoryManager.this.getPartyFFAInventory().getCurrentPage());
         }
      });
      this.partyEventInventory.setItem(6, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.REDSTONE, CC.PRIMARY + "Redrover")) {
         public void onClick(InventoryClickEvent event) {
            Player player = (Player)event.getWhoClicked();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cRedrover is currently disabled."));
         }
      });

      for(int i = 0; i < 9; ++i) {
         this.joinPremiumInventory.setItem(i, new InventoryUI.AbstractClickableItem(ItemUtil.createItem(Material.DIAMOND, CC.PRIMARY + "Confirm Joining Premium")) {
            public void onClick(InventoryClickEvent event) {
               Player player = (Player)event.getWhoClicked();
               ItemStack item = event.getCurrentItem();
               if (item != null && item.getType() == Material.DIAMOND) {
                  InventoryManager.this.plugin.getQueueManager().addPlayerToQueue(player, InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), "NoDebuff", QueueType.PREMIUM);
               }

            }
         });
      }

      for(final Kit kit : this.plugin.getKitManager().getKits()) {
         InventoryUI duelInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select a map...", 3, 0);
         InventoryUI partySplitInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select a Party Split map...", 3, 0);
         InventoryUI partyFFAInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select a Party FFA map...", 3, 0);
         InventoryUI redroverInventory = new InventoryUI(CC.BLUE + CC.BOLD + "Select a Redrover map...", 3, 0);

         for(final Arena arena : this.plugin.getArenaManager().getArenas().values()) {
            if (arena.isEnabled() && !kit.getExcludedArenas().contains(arena.getName()) && (kit.getArenaWhiteList().size() <= 0 || kit.getArenaWhiteList().contains(arena.getName()))) {
               ItemStack book = ItemUtil.createItem(Material.BOOK, CC.GREEN + arena.getName());
               duelInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                  public void onClick(InventoryClickEvent event) {
                     InventoryManager.this.handleDuelMapClick((Player)event.getWhoClicked(), arena, kit);
                  }
               });
               partySplitInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                  public void onClick(InventoryClickEvent event) {
                     InventoryManager.this.handlePartySplitMapClick((Player)event.getWhoClicked(), arena, kit);
                  }
               });
               partyFFAInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                  public void onClick(InventoryClickEvent event) {
                     InventoryManager.this.handlePartyFFAMapClick((Player)event.getWhoClicked(), arena, kit);
                  }
               });
               redroverInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
                  public void onClick(InventoryClickEvent event) {
                     InventoryManager.this.handleRedroverMapClick((Player)event.getWhoClicked(), arena, kit);
                  }
               });
            }
         }

         this.duelMapInventories.put(kit.getName(), duelInventory);
         this.partySplitMapInventories.put(kit.getName(), partySplitInventory);
         this.partyFFAMapInventories.put(kit.getName(), partyFFAInventory);
         this.redroverMapInventories.put(kit.getName(), redroverInventory);
      }

   }

   private void updateInventories() {
      for(int i = 0; i < 18; ++i) {
         InventoryUI.ClickableItem unrankedItem = this.unrankedInventory.getItem(i);
         if (unrankedItem != null) {
            unrankedItem.setItemStack(this.updateQueueLore(unrankedItem.getItemStack(), QueueType.UNRANKED));
            this.unrankedInventory.setItem(i, unrankedItem);
         }

         InventoryUI.ClickableItem rankedItem = this.rankedInventory.getItem(i);
         if (rankedItem != null) {
            rankedItem.setItemStack(this.updateQueueLore(rankedItem.getItemStack(), QueueType.RANKED));
            this.rankedInventory.setItem(i, rankedItem);
         }
      }

   }

   private ItemStack updateQueueLore(ItemStack itemStack, QueueType type) {
      if (itemStack == null) {
         return null;
      } else if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
         String ladder = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
         int queueSize = this.plugin.getQueueManager().getQueueSize(ladder, type);
         int inGameSize = this.plugin.getMatchManager().getFighters(ladder, type);
         return ItemUtil.reloreItem(itemStack, new String[]{CC.GRAY + " ", CC.YELLOW + "Playing: " + CC.WHITE + inGameSize, CC.YELLOW + "Queued: " + CC.WHITE + queueSize, CC.PRIMARY + "   ", CC.GREEN + "» Click to queue «"});
      } else {
         return null;
      }
   }

   private void addToQueue(Player player, PlayerData playerData, Kit kit, Party party, QueueType queueType) {
      if (kit != null) {
         if (party == null) {
            this.plugin.getQueueManager().addPlayerToQueue(player, playerData, kit.getName(), queueType);
         } else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
            this.plugin.getQueueManager().addPartyToQueue(player, party, kit.getName(), queueType);
         }
      }

   }

   public void addSnapshot(InventorySnapshot snapshot) {
      this.snapshots.put(snapshot.getSnapshotId(), snapshot);
      this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.removeSnapshot(snapshot.getSnapshotId()), 600L);
   }

   public void removeSnapshot(UUID snapshotId) {
      InventorySnapshot snapshot = (InventorySnapshot)this.snapshots.get(snapshotId);
      if (snapshot != null) {
         this.snapshots.remove(snapshotId);
      }

   }

   public InventorySnapshot getSnapshot(UUID snapshotId) {
      return (InventorySnapshot)this.snapshots.get(snapshotId);
   }

   public void addParty(final Player player) {
      ItemStack skull = ItemUtil.createItem(Material.SKULL_ITEM, CC.PRIMARY + player.getName() + " (" + CC.SECONDARY + "1" + CC.PRIMARY + ")");
      this.partyInventory.addItem(new InventoryUI.AbstractClickableItem(skull) {
         public void onClick(InventoryClickEvent inventoryClickEvent) {
            player.closeInventory();
            if (inventoryClickEvent.getWhoClicked() instanceof Player) {
               Player sender = (Player)inventoryClickEvent.getWhoClicked();
               sender.performCommand("duel " + player.getName());
            }

         }
      });
   }

   public void updateParty(Party party) {
      Player player = this.plugin.getServer().getPlayer(party.getLeader());

      for(int i = 0; i < this.partyInventory.getSize(); ++i) {
         InventoryUI.ClickableItem item = this.partyInventory.getItem(i);
         if (item != null) {
            ItemStack stack = item.getItemStack();
            if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
               List<String> lores = new ArrayList();
               party.members().forEach((member) -> lores.add(CC.PRIMARY + member.getName()));
               ItemUtil.reloreItem(stack, (String[])lores.toArray(new String[0]));
               ItemUtil.renameItem(stack, CC.PRIMARY + player.getName() + " (" + CC.SECONDARY + party.getMembers().size() + CC.PRIMARY + ")");
               item.setItemStack(stack);
               break;
            }
         }
      }

      this.handleInventoryUpdate(this.partyInventory.getCurrentPage());
   }

   public void removeParty(Party party) {
      Player player = this.plugin.getServer().getPlayer(party.getLeader());

      for(int i = 0; i < this.partyInventory.getSize(); ++i) {
         InventoryUI.ClickableItem item = this.partyInventory.getItem(i);
         if (item != null) {
            ItemStack stack = item.getItemStack();
            if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
               this.partyInventory.removeItem(i);
               break;
            }
         }
      }

      this.handleInventoryUpdate(this.partyInventory.getCurrentPage());
   }

   public void addEditingKitInventory(final Player player, final Kit kit) {
      final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      final Map<Integer, PlayerKit> kitMap = playerData.getPlayerKits(kit.getName());
      final InventoryUI inventory = new InventoryUI(CC.B_BLUE + "Edit loadouts...", 4, 0);

      for(int i = 1; i <= 7; ++i) {
         ItemStack save = ItemUtil.createItem(Material.CHEST, CC.GREEN + "Save kit " + CC.WHITE + kit.getName() + " #" + i);
         final ItemStack load = ItemUtil.createItem(Material.BOOK, CC.YELLOW + "Load kit " + CC.WHITE + kit.getName() + " #" + i);
         final ItemStack rename = ItemUtil.createItem(Material.NAME_TAG, CC.AQUA + "Rename kit " + CC.WHITE + kit.getName() + " #" + i);
         final ItemStack delete = ItemUtil.createItem(Material.FLINT, CC.RED + "Delete kit " + CC.WHITE + kit.getName() + " #" + i);
         inventory.setItem(i, new InventoryUI.AbstractClickableItem(save) {
            public void onClick(InventoryClickEvent event) {
               final int kitIndex = event.getSlot();
               InventoryManager.this.handleSavingKit(player, playerData, kit, kitMap, kitIndex);
               inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
                  public void onClick(InventoryClickEvent event) {
                     InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
                  }
               });
               inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
                  public void onClick(InventoryClickEvent event) {
                     InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
                  }
               });
               inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
                  public void onClick(InventoryClickEvent event) {
                     InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
                  }
               });
            }
         });
         if (kitMap != null && kitMap.containsKey(i)) {
            int finalI2 = i;
            inventory.setItem(i + 1, 2, new InventoryUI.AbstractClickableItem(load) {
               public void onClick(InventoryClickEvent event) {
                  InventoryManager.this.handleLoadKit(player, finalI2, kitMap);
               }
            });
            int finalI = i;
            inventory.setItem(i + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
               public void onClick(InventoryClickEvent event) {
                  InventoryManager.this.handleRenamingKit(player, finalI, kitMap);
               }
            });
            int finalI1 = i;
            inventory.setItem(i + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
               public void onClick(InventoryClickEvent event) {
                  InventoryManager.this.handleDeleteKit(player, finalI1, kitMap, inventory);
               }
            });
         }
      }

      this.editorInventories.put(player.getUniqueId(), inventory);
   }

   public void removeEditingKitInventory(UUID uuid) {
      InventoryUI inventoryUI = (InventoryUI)this.editorInventories.get(uuid);
      if (inventoryUI != null) {
         this.editorInventories.remove(uuid);
      }

   }

   public InventoryUI getEditingKitInventory(UUID uuid) {
      return (InventoryUI)this.editorInventories.get(uuid);
   }

   private void handleSavingKit(Player player, PlayerData playerData, Kit kit, Map<Integer, PlayerKit> kitMap, int kitIndex) {
      if (kitMap != null && kitMap.containsKey(kitIndex)) {
         ((PlayerKit)kitMap.get(kitIndex)).setContents((ItemStack[])player.getInventory().getContents().clone());
         player.sendMessage(CC.GREEN + "Successfully saved " + kit.getName() + " kit #" + kitIndex + ".");
      } else {
         PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, (ItemStack[])player.getInventory().getContents().clone(), kit.getName() + " Kit " + kitIndex);
         playerData.addPlayerKit(kitIndex, playerKit);
         player.sendMessage(CC.GREEN + "Successfully saved " + kit.getName() + " kit #" + kitIndex + ".");
      }
   }

   private void handleLoadKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap) {
      if (kitMap != null && kitMap.containsKey(kitIndex)) {
         ItemStack[] contents = ((PlayerKit)kitMap.get(kitIndex)).getContents();

         for(ItemStack itemStack : contents) {
            if (itemStack != null && itemStack.getAmount() <= 0) {
               itemStack.setAmount(1);
            }
         }

         player.getInventory().setContents(contents);
         player.updateInventory();
      }

   }

   private void handleRenamingKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap) {
      if (kitMap != null && kitMap.containsKey(kitIndex)) {
         this.plugin.getEditorManager().addRenamingKit(player.getUniqueId(), (PlayerKit)kitMap.get(kitIndex));
         player.closeInventory();
         player.sendMessage(CC.YELLOW + "Please enter a name for this kit.");
      }

   }

   private void handleDeleteKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap, InventoryUI inventory) {
      if (kitMap != null && kitMap.containsKey(kitIndex)) {
         this.plugin.getEditorManager().removeRenamingKit(player.getUniqueId());
         kitMap.remove(kitIndex);
         player.sendMessage(CC.GREEN + "Successfully deleted kit " + kitIndex + ".");
         inventory.setItem(kitIndex + 1, 2, (InventoryUI.ClickableItem)null);
         inventory.setItem(kitIndex + 1, 3, (InventoryUI.ClickableItem)null);
         inventory.setItem(kitIndex + 1, 4, (InventoryUI.ClickableItem)null);
      }

   }

   private void handleDuelClick(Player player, Kit kit) {
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
      if (selected == null) {
         player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
      } else {
         PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
         if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "Player is not in spawn.");
         } else {
            Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
            Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
            boolean partyDuel = party != null;
            if (partyDuel && targetParty == null) {
               player.sendMessage(CC.RED + "That player is not in a party.");
            } else {
               Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
               if (mineman != null && mineman.getRank().hasRank(Rank.ELITE)) {
                  player.closeInventory();
                  player.openInventory(((InventoryUI)this.duelMapInventories.get(kit.getName())).getCurrentPage());
               } else if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
                  player.sendMessage(CC.RED + "You already sent a match request to that player. Please wait until it expires.");
               } else {
                  Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
                  if (arena == null) {
                     player.sendMessage(CC.RED + "No available arenas found.");
                  } else {
                     this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
                  }
               }
            }
         }
      }
   }

   private void handlePartySplitClick(Player player, Kit kit) {
      Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
      if (party != null && kit != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
         player.closeInventory();
         if (party.getMembers().size() < 2) {
            player.sendMessage(MORE_PLAYERS);
         } else {
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman != null && mineman.getRank().hasRank(Rank.ELITE)) {
               player.closeInventory();
               player.openInventory(((InventoryUI)this.partySplitMapInventories.get(kit.getName())).getCurrentPage());
               return;
            }

            Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
            if (arena == null) {
               player.sendMessage(CC.RED + "No available arenas found.");
               return;
            }

            this.createPartySplitMatch(party, arena, kit);
         }

      }
   }

   private void handleFFAClick(Player player, Kit kit) {
      Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
      if (party != null && kit != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
         player.closeInventory();
         if (party.getMembers().size() < 2) {
            player.sendMessage(MORE_PLAYERS);
         } else {
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman != null && mineman.getRank().hasRank(Rank.ELITE)) {
               player.closeInventory();
               player.openInventory(((InventoryUI)this.partyFFAMapInventories.get(kit.getName())).getCurrentPage());
               return;
            }

            Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
            if (arena == null) {
               player.sendMessage(CC.RED + "No available arenas found.");
               return;
            }

            this.createFFAMatch(party, arena, kit);
         }

      }
   }

   private void handleRedroverClick(Player player, Kit kit) {
      Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
      if (party != null && kit != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
         player.closeInventory();
         if (party.getMembers().size() < 4) {
            player.sendMessage(CC.RED + "You need more 4 or more players in your party to start an event.");
         } else {
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            if (mineman != null && mineman.getRank().hasRank(Rank.ELITE)) {
               player.closeInventory();
               player.openInventory(((InventoryUI)this.redroverMapInventories.get(kit.getName())).getCurrentPage());
               return;
            }

            Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
            if (arena == null) {
               player.sendMessage(CC.RED + "No available arenas found.");
               return;
            }

            this.createRedroverMatch(party, arena, kit);
         }

      }
   }

   private void handleDuelMapClick(Player player, Arena arena, Kit kit) {
      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
      if (selected == null) {
         player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
      } else {
         PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
         if (targetData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(CC.RED + "Player is not in spawn.");
         } else {
            Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
            Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
            boolean partyDuel = party != null;
            if (partyDuel && targetParty == null) {
               player.sendMessage(CC.RED + "That player is not in a party.");
            } else if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
               player.sendMessage(CC.RED + "You already sent a match request to that player. Please wait until it expires.");
            } else {
               this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
            }
         }
      }
   }

   private void handleRedroverMapClick(Player player, Arena arena, Kit kit) {
      Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
      if (party != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
         player.closeInventory();
         if (party.getMembers().size() < 4) {
            player.sendMessage(MORE_PLAYERS);
         } else {
            this.createRedroverMatch(party, arena, kit);
         }

      }
   }

   private void handlePartyFFAMapClick(Player player, Arena arena, Kit kit) {
      Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
      if (party != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
         player.closeInventory();
         if (party.getMembers().size() < 2) {
            player.sendMessage(MORE_PLAYERS);
         } else {
            this.createFFAMatch(party, arena, kit);
         }

      }
   }

   private void handlePartySplitMapClick(Player player, Arena arena, Kit kit) {
      Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
      if (party != null && this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
         player.closeInventory();
         if (party.getMembers().size() < 2) {
            player.sendMessage(MORE_PLAYERS);
         } else {
            this.createPartySplitMatch(party, arena, kit);
         }

      }
   }

   private void sendDuel(Player player, Player selected, Kit kit, boolean partyDuel, Party party, Party targetParty, Arena arena) {
      this.plugin.getMatchManager().createMatchRequest(player, selected, arena, kit.getName(), partyDuel);
      player.closeInventory();
      Clickable requestMessage = new Clickable(CC.WHITE + player.getDisplayName() + CC.GREEN + " requested to" + (partyDuel ? " party" : "") + " duel with " + CC.GREEN + kit.getName() + (arena == null ? "" : CC.GREEN + " on " + CC.AQUA + arena.getName()) + CC.GREEN + ". " + CC.GRAY + "[Click Here]", CC.GREEN + "Click to accept the duel.", "/accept " + player.getName() + " " + kit.getName());
      if (partyDuel) {
         targetParty.members().forEach(requestMessage::sendToPlayer);
         party.broadcast(CC.GREEN + "Sent a " + CC.GREEN + kit.getName() + CC.GREEN + " party duel request to " + CC.WHITE + selected.getDisplayName() + (arena == null ? "." : CC.GREEN + " on arena " + CC.AQUA + arena.getName()) + CC.GREEN + ".");
      } else {
         requestMessage.sendToPlayer(selected);
         player.sendMessage(CC.GREEN + "Sent a " + CC.GREEN + kit.getName() + CC.GREEN + " duel request to " + selected.getDisplayName() + (arena == null ? "." : CC.GREEN + " on arena " + CC.AQUA + arena.getName()) + CC.GREEN + ".");
      }

   }

   private void createPartySplitMatch(Party party, Arena arena, Kit kit) {
      MatchTeam[] teams = party.split();
      Match match = new Match(arena, kit, QueueType.UNRANKED, teams);
      Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
      Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());
      match.broadcast("&eStarting &6" + kit.getName() + " &eParty Split match between " + leaderA.getDisplayName() + "&e's team and " + leaderB.getDisplayName() + "&e's team.");
      this.plugin.getMatchManager().createMatch(match);
   }

   private void createFFAMatch(Party party, Arena arena, Kit kit) {
      MatchTeam team = new MatchTeam(party.getLeader(), Lists.newArrayList(party.getMembers()), (List)null, 0);
      Match match = new Match(arena, kit, QueueType.UNRANKED, new MatchTeam[]{team});
      match.broadcast("&eStarting &6" + kit.getName() + " &eFFA match...");
      this.plugin.getMatchManager().createMatch(match);
   }

   private void createRedroverMatch(Party party, Arena arena, Kit kit) {
      MatchTeam[] teams = party.split();
      Match match = new Match(arena, kit, QueueType.UNRANKED, true, teams);
      Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
      Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());
      match.broadcast("&eStarting &6" + kit.getName() + " &eRedrover match between " + leaderA.getDisplayName() + "&e's team and " + leaderB.getDisplayName() + "&e's team.");
      this.plugin.getMatchManager().createMatch(match);
   }

   private void handleInventoryUpdate(Inventory inventory) {
      inventory.getViewers().stream().filter((entity) -> entity instanceof Player).forEach((entity) -> ((Player)entity).updateInventory());
   }

   public InventoryUI getUnrankedInventory() {
      return this.unrankedInventory;
   }

   public InventoryUI getRankedInventory() {
      return this.rankedInventory;
   }

   public InventoryUI getEditorInventory() {
      return this.editorInventory;
   }

   public InventoryUI getDuelInventory() {
      return this.duelInventory;
   }

   public InventoryUI getPartySplitInventory() {
      return this.partySplitInventory;
   }

   public InventoryUI getPartyFFAInventory() {
      return this.partyFFAInventory;
   }

   public InventoryUI getRedroverInventory() {
      return this.redroverInventory;
   }

   public InventoryUI getJoinPremiumInventory() {
      return this.joinPremiumInventory;
   }

   public InventoryUI getPartyEventInventory() {
      return this.partyEventInventory;
   }

   public InventoryUI getPartyInventory() {
      return this.partyInventory;
   }

   public InventoryUI getHostInventory() {
      return this.hostInventory;
   }

   static {
      MORE_PLAYERS = CC.RED + "You need more 2 or more players in your party to start an event.";
   }
}
