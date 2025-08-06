package server.pvptemple.util.cmd.param.impl;

import org.bukkit.command.CommandSender;
import server.pvptemple.util.cmd.param.Parameter;

public class StringParameter extends Parameter<String> {
   public String transfer(CommandSender sender, String argument) {
      return argument;
   }
}
