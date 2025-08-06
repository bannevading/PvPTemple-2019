package server.pvptemple.util.cmd.param.impl;

import com.google.common.primitives.Doubles;
import org.bukkit.command.CommandSender;
import server.pvptemple.util.cmd.param.Parameter;
import server.pvptemple.util.finalutil.CC;

public class DoubleParameter extends Parameter<Double> {
   public Double transfer(CommandSender sender, String argument) {
      Double doubleValue = Doubles.tryParse(argument);
      if (argument.toLowerCase().contains("e")) {
         sender.sendMessage(CC.RED + "'" + argument + "' is not a valid number!");
         return null;
      } else if (doubleValue == null) {
         sender.sendMessage(CC.RED + "'" + argument + "' is not a valid number!");
         return null;
      } else if (!doubleValue.isNaN() && Double.isFinite(doubleValue)) {
         return doubleValue;
      } else {
         sender.sendMessage(CC.RED + "'" + argument + "' is not a valid number!");
         return null;
      }
   }
}
