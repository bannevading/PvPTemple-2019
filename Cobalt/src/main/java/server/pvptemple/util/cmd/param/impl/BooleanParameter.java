package server.pvptemple.util.cmd.param.impl;

import org.bukkit.command.CommandSender;
import server.pvptemple.util.cmd.param.Parameter;

public class BooleanParameter extends Parameter<Boolean> {
   public Boolean transfer(CommandSender sender, String argument) {
      return !argument.equalsIgnoreCase("yes") && !argument.equalsIgnoreCase("yep") && !argument.equalsIgnoreCase("yea") && !argument.equalsIgnoreCase("true") ? false : true;
   }
}
