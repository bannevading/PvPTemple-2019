package server.pvptemple.board;

import java.text.DecimalFormat;

import lombok.Getter;
import org.apache.commons.lang.time.DurationFormatUtils;
import server.pvptemple.board.Board;

public class BoardTimer {
   private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");
   @Getter
   private final server.pvptemple.board.Board board;
   @Getter
   private final String id;
   @Getter
   private final double duration;
   @Getter private final long end;

   public BoardTimer(server.pvptemple.board.Board board, String id, double duration) {
      this.board = board;
      this.id = id;
      this.duration = duration;
      this.end = (long)((double)System.currentTimeMillis() + duration * (double)1000.0F);
      if (board != null) {
         board.getTimers().add(this);
      }

   }

   public String getFormattedString(TimerType format) {
      return format == TimerType.SECONDS ? SECONDS_FORMATTER.format((double)((float)(this.end - System.currentTimeMillis()) / 1000.0F)) : DurationFormatUtils.formatDuration(this.end - System.currentTimeMillis(), "mm:ss");
   }

   public void cancel() {
      if (this.board != null) {
         this.board.getTimers().remove(this);
      }

   }

   public static enum TimerType {
      SECONDS,
      MINUTES,
      HOURS;
   }
}
