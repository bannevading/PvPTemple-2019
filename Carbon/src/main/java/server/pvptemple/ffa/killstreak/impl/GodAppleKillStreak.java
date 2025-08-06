package server.pvptemple.ffa.killstreak.impl;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.ffa.killstreak.KillStreak;
import server.pvptemple.util.PlayerUtil;

public class GodAppleKillStreak implements KillStreak {
   public void giveKillStreak(Player player) {
      PlayerUtil.setFirstSlotOfType(player, Material.MUSHROOM_SOUP, new ItemStack(Material.GOLDEN_APPLE, 1, (short)1));
   }

   public List<Integer> getStreaks() {
      return Arrays.asList(30, 40, 60, 75, 100);
   }
}
