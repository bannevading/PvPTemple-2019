package server.pvptemple.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.kit.Kit;
import server.pvptemple.kit.PlayerKit;
import server.pvptemple.util.Color;
import server.pvptemple.util.PlayerUtil;

public class EditorManager {
   private final Carbon plugin = Carbon.getInstance();
   private final Map<UUID, String> editing = new HashMap();
   private final Map<UUID, PlayerKit> renaming = new HashMap();

   public void addEditor(Player player, Kit kit) {
      this.editing.put(player.getUniqueId(), kit.getName());
      this.plugin.getInventoryManager().addEditingKitInventory(player, kit);
      PlayerUtil.clearPlayer(player);
      player.teleport(this.plugin.getSpawnManager().getEditorLocation().toBukkitLocation());
      player.getInventory().setContents(kit.getContents());
      player.sendMessage(Color.translate("&aYou are now editing &2" + kit.getName() + "&a."));
   }

   public void removeEditor(UUID editor) {
      this.renaming.remove(editor);
      this.editing.remove(editor);
      this.plugin.getInventoryManager().removeEditingKitInventory(editor);
   }

   public String getEditingKit(UUID editor) {
      return (String)this.editing.get(editor);
   }

   public void addRenamingKit(UUID uuid, PlayerKit playerKit) {
      this.renaming.put(uuid, playerKit);
   }

   public void removeRenamingKit(UUID uuid) {
      this.renaming.remove(uuid);
   }

   public PlayerKit getRenamingKit(UUID uuid) {
      return (PlayerKit)this.renaming.get(uuid);
   }
}
