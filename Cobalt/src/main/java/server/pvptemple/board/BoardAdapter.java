package server.pvptemple.board;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import server.pvptemple.board.Board;

public interface BoardAdapter {
   List<String> getScoreboard(Player var1, Board var2);

   String getTitle(Player var1);

   long getInterval();

   void onScoreboardCreate(Player var1, Scoreboard var2);

   void preLoop();
}
