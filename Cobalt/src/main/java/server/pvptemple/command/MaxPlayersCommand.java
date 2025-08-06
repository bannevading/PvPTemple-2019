package server.pvptemple.command;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import server.pvptemple.CorePlugin;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class MaxPlayersCommand implements CommandHandler {
   private static final int defaultTo = -157345;
   private static Method getHandleMethod;
   private static Field maxPlayersField;
   private final CorePlugin plugin;

   @Command(
      name = {"setmaxplayers", "maxplayers", "mp"},
      description = "Sets the maxplayers allowed inside the server",
      rank = Rank.PLATFORMADMIN
   )
   public void setMaxPlayers(CommandSender sender, @Param(name = "max players",defaultTo = "-157345") int maxPlayers) {
      if (maxPlayers == -157345) {
         sender.sendMessage(MessageFormat.format("{0}Currently allowing: {1}", CC.PRIMARY, this.plugin.getServer().getMaxPlayers()));
      } else if (maxPlayers <= 0) {
         sender.sendMessage(MessageFormat.format("{0}You cannot input a negative number.", CC.RED));
      } else {
         setSlots(maxPlayers);
         sender.sendMessage(MessageFormat.format("{0}You have set the max players to {1}{2}{0} from {1}.", CC.PRIMARY, CC.SECONDARY, maxPlayers));
      }
   }

   public static void setSlots(int slots) {
      slots = Math.abs(slots);

      try {
         Object invoke = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer").getDeclaredMethod("getHandle", new Class[0]).invoke(Bukkit.getServer());
         Field declaredField = invoke.getClass().getSuperclass().getDeclaredField("maxPlayers");
         declaredField.setAccessible(true);
         declaredField.set(invoke, slots);
         changeProperties(slots);
      } catch (ReflectiveOperationException e) {
         e.printStackTrace();
      }

   }

   private static void changeProperties(int slots) {
      Path resolve = Paths.get(CorePlugin.getInstance().getDataFolder().getParentFile().getAbsolutePath()).getParent().resolve("server.properties");

      try {
         List<String> allLines = Files.readAllLines(resolve);

         for(int i = 0; i < allLines.size(); ++i) {
            if (((String)allLines.get(i)).startsWith("max-players")) {
               allLines.remove(i);
            }
         }

         allLines.add("max-players=" + slots);
         Files.write(resolve, allLines, StandardOpenOption.TRUNCATE_EXISTING);
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   @ConstructorProperties({"plugin"})
   public MaxPlayersCommand(CorePlugin plugin) {
      this.plugin = plugin;
   }
}
