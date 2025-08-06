package server.pvptemple.kit;

import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import server.pvptemple.Carbon;
import server.pvptemple.util.finalutil.CC;

public class PlayerKit {
   private final String name;
   private final int index;
   private ItemStack[] contents;
   private String displayName;

   public void applyToPlayer(Player player) {
      for(ItemStack itemStack : this.contents) {
         if (itemStack != null && itemStack.getAmount() <= 0) {
            itemStack.setAmount(1);
         }
      }

      player.getInventory().setContents(this.contents);
      player.getInventory().setArmorContents(Carbon.getInstance().getKitManager().getKit(this.name).getArmor());
      player.updateInventory();
      player.sendMessage(CC.GREEN + "Giving you your custom " + this.displayName + CC.GREEN + " loadout.");
   }

   public String getName() {
      return this.name;
   }

   public int getIndex() {
      return this.index;
   }

   public ItemStack[] getContents() {
      return this.contents;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public void setContents(ItemStack[] contents) {
      this.contents = contents;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   @ConstructorProperties({"name", "index", "contents", "displayName"})
   public PlayerKit(String name, int index, ItemStack[] contents, String displayName) {
      this.name = name;
      this.index = index;
      this.contents = contents;
      this.displayName = displayName;
   }
}
