package server.pvptemple.runnable;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.events.corners.FourCornersEvent;
import server.pvptemple.events.corners.FourCornersPlayer;
import server.pvptemple.events.lms.LMSEvent;
import server.pvptemple.events.lms.LMSPlayer;
import server.pvptemple.events.oitc.OITCEvent;
import server.pvptemple.events.oitc.OITCPlayer;
import server.pvptemple.events.parkour.ParkourEvent;
import server.pvptemple.events.parkour.ParkourPlayer;
import server.pvptemple.events.runner.RunnerEvent;
import server.pvptemple.events.runner.RunnerPlayer;
import server.pvptemple.timer.impl.EnderpearlTimer;

public class ExpBarRunnable implements Runnable {
   private final Carbon plugin = Carbon.getInstance();

   public void run() {
      EnderpearlTimer timer = (EnderpearlTimer)CorePlugin.getInstance().getTimerManager().getTimer(EnderpearlTimer.class);

      for(UUID uuid : timer.getCooldowns().keySet()) {
         Player player = this.plugin.getServer().getPlayer(uuid);
         if (player != null) {
            long time = timer.getRemaining(player);
            int seconds = (int)Math.round((double)time / (double)1000.0F);
            player.setLevel(seconds);
            player.setExp((float)time / 15000.0F);
         }
      }

      Bukkit.getOnlinePlayers().forEach((playerx) -> {
         PracticeEvent event = this.plugin.getEventManager().getEventPlaying(playerx);
         if (event != null) {
            if (event instanceof RunnerEvent) {
               RunnerEvent runnerEvent = (RunnerEvent)event;
               RunnerPlayer runnerPlayer = (RunnerPlayer)runnerEvent.getPlayer(playerx.getUniqueId());
               if (runnerPlayer != null && runnerPlayer.getState() != RunnerPlayer.RunnerState.WAITING && runnerEvent.getGameTask() != null) {
                  int seconds = runnerEvent.getGameTask().getTime();
                  if (seconds >= 0) {
                     playerx.setLevel(seconds);
                  }
               }
            } else if (event instanceof LMSEvent) {
               LMSEvent lmsEvent = (LMSEvent)event;
               LMSPlayer lmsPlayer = (LMSPlayer)lmsEvent.getPlayer(playerx.getUniqueId());
               if (lmsPlayer != null && lmsPlayer.getState() != LMSPlayer.LMSState.WAITING && lmsEvent.getGameTask() != null) {
                  int seconds = lmsEvent.getGameTask().getTime();
                  if (seconds >= 0) {
                     playerx.setLevel(seconds);
                  }
               }
            } else if (event instanceof ParkourEvent) {
               ParkourEvent parkourEvent = (ParkourEvent)event;
               ParkourPlayer parkourPlayer = (ParkourPlayer)parkourEvent.getPlayer(playerx.getUniqueId());
               if (parkourPlayer != null && parkourPlayer.getState() != ParkourPlayer.ParkourState.WAITING && parkourEvent.getGameTask() != null) {
                  int seconds = parkourEvent.getGameTask().getTime();
                  if (seconds >= 0) {
                     playerx.setLevel(seconds);
                  }
               }
            } else if (event instanceof OITCEvent) {
               OITCEvent oitcEvent = (OITCEvent)event;
               OITCPlayer oitcPlayer = (OITCPlayer)oitcEvent.getPlayer(playerx.getUniqueId());
               if (oitcPlayer != null && oitcPlayer.getState() != OITCPlayer.OITCState.WAITING && oitcEvent.getGameTask() != null) {
                  int seconds = oitcEvent.getGameTask().getTime();
                  if (seconds >= 0) {
                     playerx.setLevel(seconds);
                  }
               }
            } else if (event instanceof FourCornersEvent) {
               FourCornersEvent fourCornersEvent = (FourCornersEvent)event;
               FourCornersPlayer fourCornersPlayer = (FourCornersPlayer)fourCornersEvent.getPlayer(playerx.getUniqueId());
               if (fourCornersPlayer != null && fourCornersPlayer.getState() != FourCornersPlayer.FourCornerState.WAITING && fourCornersEvent.getGameTask() != null) {
                  int seconds = fourCornersEvent.getGameTask().getTime();
                  if (seconds >= 0) {
                     playerx.setLevel(seconds);
                  }
               }
            }
         }

      });
   }
}
