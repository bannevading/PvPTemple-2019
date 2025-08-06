package server.pvptemple.ffa.killstreak.impl;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.ffa.killstreak.KillStreak;
import server.pvptemple.util.PlayerUtil;

public class DebuffKillStreak implements KillStreak {
   private static final ItemStack SLOWNESS;
   private static final ItemStack POISON;

   public void giveKillStreak(Player player) {
      PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, SLOWNESS.clone());
      PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, POISON.clone());
   }

   public List<Integer> getStreaks() {
      return Arrays.asList(7, 25);
   }

   static {
      SLOWNESS = new ItemStack(Material.POTION, 1, (short)16394);
      POISON = new ItemStack(Material.POTION, 1, (short)16388);
   }
}
