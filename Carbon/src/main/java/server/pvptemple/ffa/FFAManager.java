package server.pvptemple.ffa;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import server.pvptemple.Carbon;
import server.pvptemple.ffa.killstreak.KillStreak;
import server.pvptemple.ffa.killstreak.impl.DebuffKillStreak;
import server.pvptemple.ffa.killstreak.impl.GappleKillStreak;
import server.pvptemple.ffa.killstreak.impl.GodAppleKillStreak;
import server.pvptemple.kit.Kit;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.finalutil.CC;

public class FFAManager {
   private final Map<Item, Long> itemTracker = new HashMap();
   private final Map<UUID, Integer> killStreakTracker = new HashMap();
   private final Set<KillStreak> killStreaks = new HashSet();
   private final Carbon plugin = Carbon.getInstance();
   private final CustomLocation spawnPoint;
   private final Kit kit;

   public void addPlayer(Player player) {
      if (this.killStreaks.isEmpty()) {
         this.killStreaks.add(new GappleKillStreak());
         this.killStreaks.add(new DebuffKillStreak());
         this.killStreaks.add(new GodAppleKillStreak());
      }

      PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
      playerData.setPlayerState(PlayerState.FFA);
      player.teleport(this.spawnPoint.toBukkitLocation());
      player.sendMessage(CC.SECONDARY + "Welcome to the FFA arena!");
      this.kit.applyToPlayer(player);

      for(int i = 0; i < player.getInventory().getContents().length; ++i) {
         ItemStack itemStack = player.getInventory().getContents()[i];
         if (itemStack != null && itemStack.getType() == Material.POTION) {
            player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
         }
      }

      player.getActivePotionEffects().forEach((potionEffect) -> player.removePotionEffect(potionEffect.getType()));
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

      for(PlayerData data : this.plugin.getPlayerManager().getAllData()) {
         Player player1 = this.plugin.getServer().getPlayer(data.getUniqueId());
         if (data.getPlayerState() == PlayerState.FFA) {
            player.showPlayer(player1);
            player1.showPlayer(player);
         } else {
            player.hidePlayer(player1);
            player1.hidePlayer(player);
         }
      }

   }

   public void removePlayer(Player player) {
      for(PlayerData data : this.plugin.getPlayerManager().getAllData()) {
         Player player1 = this.plugin.getServer().getPlayer(data.getUniqueId());
         if (data.getPlayerState() == PlayerState.FFA) {
            player.hidePlayer(player1);
            player1.hidePlayer(player);
         }
      }

      this.plugin.getPlayerManager().sendToSpawnAndReset(player);
   }

   @ConstructorProperties({"spawnPoint", "kit"})
   public FFAManager(CustomLocation spawnPoint, Kit kit) {
      this.spawnPoint = spawnPoint;
      this.kit = kit;
   }

   public Map<Item, Long> getItemTracker() {
      return this.itemTracker;
   }

   public Map<UUID, Integer> getKillStreakTracker() {
      return this.killStreakTracker;
   }

   public Set<KillStreak> getKillStreaks() {
      return this.killStreaks;
   }
}
