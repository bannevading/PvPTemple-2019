package server.pvptemple.util.cmd.param;

import org.bukkit.command.CommandSender;

public abstract class Parameter<T> {
   private String argument;

   public T transform(CommandSender sender, String arguments) {
      this.argument = arguments;
      return (T)this.transfer(sender, arguments);
   }

   public abstract T transfer(CommandSender var1, String var2);

   public String getArgument() {
      return this.argument;
   }
}
