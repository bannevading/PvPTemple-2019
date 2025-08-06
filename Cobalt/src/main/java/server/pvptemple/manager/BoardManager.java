package server.pvptemple.manager;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import server.pvptemple.CorePlugin;
import server.pvptemple.board.Board;
import server.pvptemple.board.BoardAdapter;
import server.pvptemple.board.BoardEntry;

public class BoardManager implements Runnable {
   private final Map<UUID, Board> playerBoards = new HashMap<>();
   private final BoardAdapter adapter;

   public void run() {
      this.adapter.preLoop();

      for(Player player : CorePlugin.getInstance().getServer().getOnlinePlayers()) {
         Board board = this.playerBoards.get(player.getUniqueId());
         if (board != null) {
            try {
               Scoreboard scoreboard = board.getScoreboard();
               List<String> scores = this.adapter.getScoreboard(player, board);
               if (scores != null) {
                  Collections.reverse(scores);
                  Objective objective = board.getObjective();
                  if (!objective.getDisplayName().equals(this.adapter.getTitle(player))) {
                     objective.setDisplayName(this.adapter.getTitle(player));
                  }

                  if (scores.isEmpty()) {
                     Iterator<BoardEntry> iter = board.getEntries().iterator();

                     while(iter.hasNext()) {
                        BoardEntry boardEntry = iter.next();
                        boardEntry.remove();
                        iter.remove();
                     }
                     continue;
                  }

                  label100:
                  for(int i = 0; i < scores.size(); ++i) {
                     String text = scores.get(i);
                     int position = i + 1;

                     for(BoardEntry boardEntry : new LinkedList<>(board.getEntries())) {
                        Score score = objective.getScore(boardEntry.getKey());
                        if (score != null && boardEntry.getText().equals(text) && score.getScore() == position) {
                           continue label100;
                        }
                     }

                     Iterator<BoardEntry> iter = board.getEntries().iterator();

                     while(iter.hasNext()) {
                        BoardEntry boardEntry = iter.next();
                        int entryPosition = scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(boardEntry.getKey()).getScore();
                        if (entryPosition > scores.size()) {
                           boardEntry.remove();
                           iter.remove();
                        }
                     }

                     int positionToSearch = position - 1;
                     BoardEntry entry = board.getByPosition(positionToSearch);
                     if (entry == null) {
                        (new BoardEntry(board, text)).send(position);
                     } else {
                        entry.setText(text).setup().send(position);
                     }

                     if (board.getEntries().size() > scores.size()) {
                        iter = board.getEntries().iterator();

                        while(iter.hasNext()) {
                           BoardEntry boardEntry = iter.next();
                           if (!scores.contains(boardEntry.getText()) || Collections.frequency(board.getBoardEntriesFormatted(), boardEntry.getText()) > 1) {
                              boardEntry.remove();
                              iter.remove();
                           }
                        }
                     }
                  }
               } else if (!board.getEntries().isEmpty()) {
                  board.getEntries().forEach(BoardEntry::remove);
                  board.getEntries().clear();
               }

               this.adapter.onScoreboardCreate(player, scoreboard);
               player.setScoreboard(scoreboard);
            } catch (Exception e) {
               e.printStackTrace();
               CorePlugin.getInstance().getLogger().severe("Something went wrong while updating " + player.getName() + "'s scoreboard " + board + " - " + board.getAdapter() + ")");
            }
         }
      }

   }

   public Map<UUID, Board> getPlayerBoards() {
      return this.playerBoards;
   }

   public BoardAdapter getAdapter() {
      return this.adapter;
   }

   @ConstructorProperties({"adapter"})
   public BoardManager(BoardAdapter adapter) {
      this.adapter = adapter;
   }
}
