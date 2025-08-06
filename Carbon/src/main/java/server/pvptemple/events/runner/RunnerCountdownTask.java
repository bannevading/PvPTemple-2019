package server.pvptemple.events.runner;

import java.util.Arrays;
import server.pvptemple.events.EventCountdownTask;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.util.finalutil.CC;

public class RunnerCountdownTask extends EventCountdownTask {
   public RunnerCountdownTask(PracticeEvent event) {
      super(event, 45);
   }

   public boolean shouldAnnounce(int timeUntilStart) {
      return Arrays.asList(90, 60, 30, 15, 10, 5).contains(timeUntilStart);
   }

   public boolean canStart() {
      return this.getEvent().getPlayers().size() >= 2;
   }

   public void onCancel() {
      this.getEvent().sendMessage(CC.RED + "There were not enough players to start the event.");
      this.getEvent().end();
      this.getEvent().getPlugin().getEventManager().setCooldown(0L);
   }
}
