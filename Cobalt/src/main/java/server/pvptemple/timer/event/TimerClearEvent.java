package server.pvptemple.timer.event;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import server.pvptemple.CorePlugin;
import server.pvptemple.timer.Timer;

public class TimerClearEvent extends Event {
   private static final HandlerList HANDLERS = new HandlerList();
   @Getter
   private final Optional<UUID> userUUID;
   @Getter
   private final Timer timer;
   private Optional<Player> player;

   public TimerClearEvent(Timer timer) {
      this.userUUID = Optional.empty();
      this.timer = timer;
   }

   public TimerClearEvent(UUID userUUID, Timer timer) {
      this.userUUID = Optional.of(userUUID);
      this.timer = timer;
   }

   public TimerClearEvent(Player player, Timer timer) {
      Objects.requireNonNull(player);
      this.player = Optional.of(player);
      this.userUUID = Optional.of(player.getUniqueId());
      this.timer = timer;
   }

   public static HandlerList getHandlerList() {
      return HANDLERS;
   }

   public Optional<Player> getPlayer() {
      if (!this.player.isPresent()) {
         this.player = this.userUUID.map(uuid -> CorePlugin.getInstance().getServer().getPlayer(uuid));
      }

      return this.player;
   }

    public HandlerList getHandlers() {
      return HANDLERS;
   }
}
