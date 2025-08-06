package server.pvptemple.commands.management;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.CustomLocation;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class SpawnsCommand extends Command {
   private final Carbon plugin = Carbon.getInstance();

   public SpawnsCommand() {
      super("spawns");
      this.setDescription("Manage server spawns.");
      this.setUsage(CC.RED + "Usage: /spawn <subcommand>");
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if (sender instanceof Player && PlayerUtil.testPermission(sender, Rank.ADMIN)) {
         if (args.length < 1) {
            sender.sendMessage(this.usageMessage + " RUNNER");
            return true;
         } else {
            Player player = (Player)sender;
            switch (args[0].toLowerCase()) {
               case "spawnlocation":
                  this.plugin.getSpawnManager().setSpawnLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the spawn location.");
                  break;
               case "spawnmin":
                  this.plugin.getSpawnManager().setSpawnMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the spawn min.");
                  break;
               case "spawnmax":
                  this.plugin.getSpawnManager().setSpawnMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the spawn max.");
                  break;
               case "editorlocation":
                  this.plugin.getSpawnManager().setEditorLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the editor location.");
                  break;
               case "editormin":
                  this.plugin.getSpawnManager().setEditorMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the editor min.");
                  break;
               case "editormax":
                  this.plugin.getSpawnManager().setEditorMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the editor max.");
                  break;
               case "runner":
                  this.plugin.getSpawnManager().getRunnerLocations().add(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the Runner spawn-point #" + this.plugin.getSpawnManager().getRunnerLocations().size() + ".");
                  break;
               case "sumolocation":
                  this.plugin.getSpawnManager().setSumoLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the sumo location.");
                  break;
               case "sumofirst":
                  this.plugin.getSpawnManager().setSumoFirst(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the sumo location A.");
                  break;
               case "sumosecond":
                  this.plugin.getSpawnManager().setSumoSecond(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the sumo location B.");
                  break;
               case "lmslocation":
                  this.plugin.getSpawnManager().setLmsLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the lms location.");
                  break;
               case "lms":
                  this.plugin.getSpawnManager().getLmsLocations().add(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the LMS spawn-point #" + this.plugin.getSpawnManager().getLmsLocations().size() + ".");
                  break;
               case "parkourlocation":
                  this.plugin.getSpawnManager().setParkourLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the parkour location.");
                  break;
               case "parkourgamelocation":
                  this.plugin.getSpawnManager().setParkourGameLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the parkour Game location.");
                  break;
               case "oitclocation":
                  this.plugin.getSpawnManager().setOitcLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the OITC location.");
                  break;
               case "oitc":
                  this.plugin.getSpawnManager().getOitcLocations().add(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the OITC spawn-point #" + this.plugin.getSpawnManager().getOitcLocations().size() + ".");
                  break;
               case "cornerslocation":
                  this.plugin.getSpawnManager().setCornersLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the corners location.");
                  break;
               case "cornersmin":
                  this.plugin.getSpawnManager().setCornersMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the corners min.");
                  break;
               case "cornersmax":
                  this.plugin.getSpawnManager().setCornersMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                  player.sendMessage(CC.GREEN + "Successfully set the corners max.");
            }

            return false;
         }
      } else {
         return true;
      }
   }
}
