package server.pvptemple.util.cmd.param.impl;

import com.google.common.primitives.Ints;
import org.bukkit.command.CommandSender;
import server.pvptemple.util.cmd.param.Parameter;
import server.pvptemple.util.finalutil.CC;

public class IntegerParameter extends Parameter<Integer> {
   public Integer transfer(CommandSender sender, String argument) {
      Integer integer = Ints.tryParse(argument);
      if (integer == null) {
         sender.sendMessage(CC.RED + "'" + argument + "' is not a number!");
         return null;
      } else {
         return integer;
      }
   }
}
