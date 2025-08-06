package server.pvptemple.util.cmd.param.impl;

import com.google.common.primitives.Floats;
import org.bukkit.command.CommandSender;
import server.pvptemple.util.cmd.param.Parameter;
import server.pvptemple.util.finalutil.CC;

public class FloatParameter extends Parameter<Float> {
   public Float transfer(CommandSender sender, String argument) {
      Float floatValue = Floats.tryParse(argument);
      if (argument.toLowerCase().contains("e")) {
         sender.sendMessage(CC.RED + "'" + argument + "' is not a valid number!");
         return null;
      } else if (floatValue == null) {
         sender.sendMessage(CC.RED + "'" + argument + "' is not a valid number!");
         return null;
      } else if (!Float.isNaN(floatValue) && Float.isFinite(floatValue)) {
         return floatValue;
      } else {
         sender.sendMessage(CC.RED + "'" + argument + "' is not a valid number!");
         return null;
      }
   }
}
