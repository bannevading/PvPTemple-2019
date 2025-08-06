package server.pvptemple.util.cmd.commands;

import com.google.common.base.Strings;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.annotation.commandTypes.SubCommand;
import server.pvptemple.util.cmd.commands.SubCommandComponent;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class BaseCommandComponent extends Command {
   private final Permission requiredPermission;
   private final boolean requiresOp;
   private Rank rank;
   private Map<String, server.pvptemple.util.cmd.commands.SubCommandComponent> subCommands = new HashMap();
   private Pair<Method, Object> invoke;

   public BaseCommandComponent(String name, Rank rank, Permission permission, boolean requiresOp) {
      super(name);
      this.rank = rank;
      this.requiredPermission = permission;
      this.requiresOp = requiresOp;
   }

   public boolean execute(CommandSender commandSender, String s, String[] args) {
      if (!PlayerUtil.testPermission(commandSender, this.rank, this.requiredPermission, this.requiresOp)) {
         return false;
      } else if (args.length >= 1 && this.subCommands.containsKey(args[0].toLowerCase())) {
         String argumentCombined = String.join(" ", args);
         argumentCombined = argumentCombined.replace(args[0].toLowerCase(), "");
         ((server.pvptemple.util.cmd.commands.SubCommandComponent)this.subCommands.get(args[0].toLowerCase())).execute(commandSender, s, args[0], argumentCombined.split(" "));
         return true;
      } else {
         try {
            if (!((Method)this.invoke.getKey()).isAnnotationPresent(SubCommand.class) && ((Method)this.invoke.getKey()).getParameters().length == 1) {
               ((Method)this.invoke.getKey()).setAccessible(true);
               ((Method)this.invoke.getKey()).invoke(this.invoke.getValue(), commandSender);
               return true;
            }
         } catch (InvocationTargetException ex) {
            if (!(ex.getTargetException() instanceof IllegalArgumentException)) {
               ex.printStackTrace();
            }
         } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
               e.printStackTrace();
            }
         }

         commandSender.sendMessage(CC.GRAY + CC.STRIKE_THROUGH + Strings.repeat("-", 52));

         for(Map.Entry<String, server.pvptemple.util.cmd.commands.SubCommandComponent> st : this.subCommands.entrySet()) {
            commandSender.sendMessage(CC.RED + "/" + s + " " + (String)st.getKey() + (((server.pvptemple.util.cmd.commands.SubCommandComponent)st.getValue()).getDescription().equals("") ? "" : CC.GRAY + ": " + CC.RED + ((server.pvptemple.util.cmd.commands.SubCommandComponent)st.getValue()).getDescription()));
         }

         commandSender.sendMessage(CC.GRAY + CC.STRIKE_THROUGH + Strings.repeat("-", 52));
         return true;
      }
   }

   public Permission getRequiredPermission() {
      return this.requiredPermission;
   }

   public boolean isRequiresOp() {
      return this.requiresOp;
   }

   public Rank getRank() {
      return this.rank;
   }

   public Map<String, SubCommandComponent> getSubCommands() {
      return this.subCommands;
   }

   public Pair<Method, Object> getInvoke() {
      return this.invoke;
   }

   public void setInvoke(Pair<Method, Object> invoke) {
      this.invoke = invoke;
   }
}
