package server.pvptemple.util.cmd.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import server.pvptemple.CorePlugin;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.ParamType;
import server.pvptemple.util.cmd.param.ParamData;
import server.pvptemple.util.cmd.param.Parameter;
import server.pvptemple.util.finalutil.CC;
import server.pvptemple.util.finalutil.PlayerUtil;

public class Cmd extends Command {
   private final Permission permission;
   private final boolean requiresOp;
   private List<Pair<Parameter<?>, ParamData>> arguments = new ArrayList();
   private Pair<Method, Object> invoke;
   private Rank rank;
   private boolean mineman;

   public Cmd(String label, Rank rank, Permission permission, boolean requiresOp, boolean mineman) {
      super(label);
      this.rank = rank;
      this.permission = permission;
      this.requiresOp = requiresOp;
      this.mineman = mineman;
   }

   public boolean execute(CommandSender commandSender, String label, String[] args) {
      if (!PlayerUtil.testPermission(commandSender, this.rank, this.permission, this.requiresOp)) {
         return false;
      } else {
         int i = 0;
         ArrayList<Object> parameters = new ArrayList();
         if (commandSender instanceof Player && this.mineman) {
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(((Player)commandSender).getUniqueId());
            parameters.add(mineman);
         } else {
            parameters.add(commandSender);
         }

         StringBuilder usage = new StringBuilder(CC.RED + "Usage: /" + label + " ");

         for(Pair<Parameter<?>, ParamData> paramPair : this.arguments) {
            if (((ParamData)paramPair.getValue()).getType() == ParamType.MULTI) {
               usage.append(CC.RED).append("[").append(((ParamData)paramPair.getValue()).getName()).append(CC.RED).append("] ");
               parameters.add((paramPair.getKey()).transform(commandSender, StringUtils.join((Object[])args, ' ', i, args.length)));
               break;
            }

            if (((ParamData)paramPair.getValue()).getType() == ParamType.FLAG) {
               usage.append(CC.RED).append("(").append(CC.AQUA).append("-").append(((ParamData)paramPair.getValue()).getName()).append(CC.RED).append(") ");
            } else {
               usage.append(CC.RED).append("<").append(((ParamData)paramPair.getValue()).getName()).append(CC.RED).append("> ");
            }

            String argument;
            try {
               argument = args[i];
            } catch (ArrayIndexOutOfBoundsException var11) {
               argument = null;
            }

            if (argument == null) {
               if (((ParamData)paramPair.getValue()).getDefaultValue().equalsIgnoreCase("")) {
                  if (((ParamData)paramPair.getValue()).getType() != ParamType.FLAG) {
                     ++i;
                  } else {
                     parameters.add(false);
                  }
                  continue;
               }

               argument = ((ParamData)paramPair.getValue()).getDefaultValue();
            }

            if (((ParamData)paramPair.getValue()).getType() == ParamType.FLAG) {
               if (argument.startsWith("-") && argument.replace("-", "").equalsIgnoreCase(((ParamData)paramPair.getRight()).getName())) {
                  parameters.add(true);
                  ++i;
               } else {
                  parameters.add(false);
               }
            } else {
               Object object = (paramPair.getKey()).transform(commandSender, argument);
               if (object == null) {
                  return true;
               }

               parameters.add(object);
               ++i;
            }
         }

         try {
            ((Method)this.invoke.getKey()).setAccessible(true);
            ((Method)this.invoke.getKey()).invoke(this.invoke.getValue(), parameters.toArray());
            return true;
         } catch (IllegalArgumentException var12) {
            commandSender.sendMessage(usage.toString() + (this.description.equals("") ? "" : CC.GRAY + ": " + this.description));
            return false;
         } catch (InvocationTargetException ite) {
            if (ite.getTargetException() instanceof IllegalArgumentException) {
               commandSender.sendMessage(usage.toString() + (this.description.equals("") ? "" : CC.GRAY + ": " + this.description));
               return false;
            }

            ite.printStackTrace();
         } catch (Exception e) {
            e.printStackTrace();
         }

         return false;
      }
   }

   public List<Pair<Parameter<?>, ParamData>> getArguments() {
      return this.arguments;
   }

   public void setInvoke(Pair<Method, Object> invoke) {
      this.invoke = invoke;
   }
}
