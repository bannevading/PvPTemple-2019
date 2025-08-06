package server.pvptemple.events;

import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.clickable.Clickable;
import server.pvptemple.util.Color;
import server.pvptemple.util.finalutil.CC;

public abstract class EventCountdownTask extends BukkitRunnable {
   private final PracticeEvent event;
   private final int countdownTime;
   private int timeUntilStart;
   private boolean ended;

   public EventCountdownTask(PracticeEvent event, int countdownTime) {
      this.event = event;
      this.countdownTime = countdownTime;
      this.timeUntilStart = countdownTime;
   }

   public void run() {
      if (!this.isEnded()) {
         if (this.timeUntilStart <= 0) {
            if (this.canStart()) {
               this.event.start();
            } else {
               this.onCancel();
            }

            this.ended = true;
         } else {
            if (this.shouldAnnounce(this.timeUntilStart)) {
               Clickable message = new Clickable(Color.translate("&8[&9Event&8] " + this.event.getHost().getDisplayName() + CC.GOLD + " is hosting a " + CC.YELLOW + this.event.getName() + CC.GOLD + " event starting in " + this.timeUntilStart + " seconds! " + CC.GRAY + "[Click Here]"), CC.GREEN + "Click to join!", "/joinevent " + this.event.getName());
               this.event.getPlugin().getServer().getOnlinePlayers().forEach(message::sendToPlayer);
            }

            --this.timeUntilStart;
         }
      }
   }

   public abstract boolean shouldAnnounce(int var1);

   public abstract boolean canStart();

   public abstract void onCancel();

   public void setTimeUntilStart(int timeUntilStart) {
      this.timeUntilStart = timeUntilStart;
   }

   public void setEnded(boolean ended) {
      this.ended = ended;
   }

   public PracticeEvent getEvent() {
      return this.event;
   }

   public int getCountdownTime() {
      return this.countdownTime;
   }

   public int getTimeUntilStart() {
      return this.timeUntilStart;
   }

   public boolean isEnded() {
      return this.ended;
   }
}
