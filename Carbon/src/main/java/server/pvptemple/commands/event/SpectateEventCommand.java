package server.pvptemple.commands.event;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.events.EventState;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.events.corners.FourCornersEvent;
import server.pvptemple.events.lms.LMSEvent;
import server.pvptemple.events.oitc.OITCEvent;
import server.pvptemple.events.parkour.ParkourEvent;
import server.pvptemple.events.runner.RunnerEvent;
import server.pvptemple.events.sumo.SumoEvent;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.util.Color;
import server.pvptemple.util.finalutil.CC;

public class SpectateEventCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public SpectateEventCommand() {
      super("eventspectate");
      this.setDescription("Spectate an event.");
      this.setUsage(ChatColor.RED + "Usage: /eventspectate <event>");
      this.setAliases(Arrays.asList("eventspec", "specevent"));
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (!(sender instanceof Player)) {
         return true;
      } else {
         Player player = (Player)sender;
         if (args.length < 1) {
            player.sendMessage(this.usageMessage);
            return true;
         } else {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
            if (party == null && (playerData.getPlayerState() == PlayerState.SPAWN || playerData.getPlayerState() == PlayerState.SPECTATING)) {
               PracticeEvent event = this.plugin.getEventManager().getByName(args[0]);
               if (event == null) {
                  player.sendMessage(ChatColor.RED + "That player is currently not in an event.");
                  return true;
               } else if (event.getState() != EventState.STARTED) {
                  player.sendMessage(ChatColor.RED + "That event hasn't started, please wait.");
                  return true;
               } else {
                  if (playerData.getPlayerState() == PlayerState.SPECTATING) {
                     if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You are already spectating this event.");
                        return true;
                     }

                     this.plugin.getEventManager().removeSpectator(player);
                  }

                  player.sendMessage(Color.translate("&aYou are now spectating &b" + event.getName() + " Event&a."));
                  if (event instanceof SumoEvent) {
                     this.plugin.getEventManager().addSpectatorSumo(player, playerData, (SumoEvent)event);
                  } else if (event instanceof RunnerEvent) {
                     this.plugin.getEventManager().addSpectatorRunner(player, playerData, (RunnerEvent)event);
                  } else if (event instanceof LMSEvent) {
                     this.plugin.getEventManager().addSpectatorLMS(player, playerData, (LMSEvent)event);
                  } else if (event instanceof ParkourEvent) {
                     this.plugin.getEventManager().addSpectatorParkour(player, playerData, (ParkourEvent)event);
                  } else if (event instanceof OITCEvent) {
                     this.plugin.getEventManager().addSpectatorOITC(player, playerData, (OITCEvent)event);
                  } else if (event instanceof FourCornersEvent) {
                     this.plugin.getEventManager().addSpectatorCorners(player, playerData, (FourCornersEvent)event);
                  }

                  return true;
               }
            } else {
               player.sendMessage(CC.RED + "You can't do this in your current state.");
               return true;
            }
         }
      }
   }
}
