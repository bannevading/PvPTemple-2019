package server.pvptemple.board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import server.pvptemple.CorePlugin;
import server.pvptemple.board.BoardAdapter;
import server.pvptemple.board.BoardEntry;
import server.pvptemple.board.BoardTimer;

public class Board {
   @Getter
   private final BoardAdapter adapter;
   @Getter
   private final Player player;
   @Getter
   private List<BoardEntry> entries = new ArrayList<>();
   private Set<BoardTimer> timers = new HashSet<>();
   @Getter
   private Set<String> keys = new HashSet<>();
   @Getter
   private Scoreboard scoreboard;
   @Getter
   private Objective objective;

   public Board(Player player, BoardAdapter adapter) {
      this.adapter = adapter;
      this.player = player;
      this.init();
   }

   private void init() {
      if (!this.player.getScoreboard().equals(CorePlugin.getInstance().getServer().getScoreboardManager().getMainScoreboard())) {
         this.scoreboard = this.player.getScoreboard();
      } else {
         this.scoreboard = CorePlugin.getInstance().getServer().getScoreboardManager().getNewScoreboard();
      }

      this.objective = this.scoreboard.registerNewObjective("Default", "dummy");
      this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
      this.objective.setDisplayName(this.adapter.getTitle(this.player));
   }

   public String getNewKey(BoardEntry entry) {
      for(ChatColor color : ChatColor.values()) {
         String colorText = color + "" + ChatColor.WHITE;
         if (entry.getText().length() > 16) {
            String sub = entry.getText().substring(0, 16);
            colorText = colorText + ChatColor.getLastColors(sub);
         }

         if (!this.keys.contains(colorText)) {
            this.keys.add(colorText);
            return colorText;
         }
      }

      throw new IndexOutOfBoundsException("No more keys available!");
   }

   public List<String> getBoardEntriesFormatted() {
      List<String> toReturn = new ArrayList();

      for(BoardEntry entry : new ArrayList<>(this.entries)) {
         toReturn.add(entry.getText());
      }

      return toReturn;
   }

   public BoardEntry getByPosition(int position) {
      for(int i = 0; i < this.entries.size(); ++i) {
         if (i == position) {
            return (BoardEntry)this.entries.get(i);
         }
      }

      return null;
   }

   public BoardTimer getCooldown(String id) {
      for(BoardTimer cooldown : this.getTimers()) {
         if (cooldown.getId().equals(id)) {
            return cooldown;
         }
      }

      return null;
   }

   public Set<BoardTimer> getTimers() {
      this.timers.removeIf((cooldown) -> System.currentTimeMillis() >= cooldown.getEnd());
      return this.timers;
   }

}
