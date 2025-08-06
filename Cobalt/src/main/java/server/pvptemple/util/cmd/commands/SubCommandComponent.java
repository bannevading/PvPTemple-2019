package server.pvptemple.util.cmd.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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

public class SubCommandComponent {
   private final Permission permission;
   private final boolean requiresOp;
   private String commandName;
   private String description;
   private Rank rank;
   private List<String> alias;
   private List<Pair<Parameter<?>, ParamData>> arguments = new ArrayList();
   private Pair<Method, Object> invoke;
   private boolean mineman;

   public SubCommandComponent(String name, String description, Rank rank, Permission permission, boolean requiresOp, List<String> alias, boolean mineman) {
      this.commandName = name;
      this.description = description;
      this.rank = rank;
      this.permission = permission;
      this.requiresOp = requiresOp;
      this.alias = alias;
      this.mineman = mineman;
   }

   public void execute(CommandSender sender, String commandLabel, String argumentLabel, String[] args) {
      if (PlayerUtil.testPermission(sender, this.rank, this.permission, this.requiresOp)) {
         List<Object> parameters = new ArrayList();
         if (sender instanceof Player && this.mineman) {
            Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(((Player)sender).getUniqueId());
            parameters.add(mineman);
         } else {
            parameters.add(sender);
         }

         int i = 1;
         StringBuilder usage = new StringBuilder(CC.RED + "Usage: /" + commandLabel + " " + argumentLabel + " ");

         for(Pair<Parameter<?>, ParamData> paramPair : this.arguments) {
            if (((ParamData)paramPair.getValue()).getType() == ParamType.MULTI) {
               usage.append(CC.RED).append("[").append(((ParamData)paramPair.getValue()).getName()).append(CC.RED).append("] ");
               parameters.add((paramPair.getKey()).transform(sender, StringUtils.join((Object[])args, ' ', i, args.length)));
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
            } catch (ArrayIndexOutOfBoundsException var12) {
               argument = null;
            }

            if (argument == null) {
               if (((ParamData)paramPair.getValue()).getDefaultValue().equalsIgnoreCase("")) {
                  if (((ParamData)paramPair.getValue()).getType() == ParamType.FLAG) {
                     parameters.add(false);
                  } else {
                     ++i;
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
               Object object = (paramPair.getKey()).transform(sender, argument);
               if (object == null) {
                  sender.sendMessage(CC.RED + "Illegal argument: " + argument + " was not expected.");
                  return;
               }

               parameters.add(object);
               ++i;
            }
         }

         try {
            ((Method)this.invoke.getKey()).setAccessible(true);
            ((Method)this.invoke.getKey()).invoke(this.invoke.getValue(), parameters.toArray());
         } catch (IllegalArgumentException var13) {
            sender.sendMessage(usage.toString() + (this.description.equals("") ? "" : CC.GRAY + ": " + this.description));
         } catch (Exception e) {
            e.printStackTrace();
         }

      }
   }

   public Permission getPermission() {
      return this.permission;
   }

   public boolean isRequiresOp() {
      return this.requiresOp;
   }

   public String getCommandName() {
      return this.commandName;
   }

   public String getDescription() {
      return this.description;
   }

   public Rank getRank() {
      return this.rank;
   }

   public List<String> getAlias() {
      return this.alias;
   }

   public List<Pair<Parameter<?>, ParamData>> getArguments() {
      return this.arguments;
   }

   public Pair<Method, Object> getInvoke() {
      return this.invoke;
   }

   public boolean isMineman() {
      return this.mineman;
   }

   public void setCommandName(String commandName) {
      this.commandName = commandName;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public void setRank(Rank rank) {
      this.rank = rank;
   }

   public void setAlias(List<String> alias) {
      this.alias = alias;
   }

   public void setArguments(List<Pair<Parameter<?>, ParamData>> arguments) {
      this.arguments = arguments;
   }

   public void setInvoke(Pair<Method, Object> invoke) {
      this.invoke = invoke;
   }

   public void setMineman(boolean mineman) {
      this.mineman = mineman;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SubCommandComponent)) {
         return false;
      } else {
         SubCommandComponent other = (SubCommandComponent)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$permission = this.getPermission();
            Object other$permission = other.getPermission();
            if (this$permission == null) {
               if (other$permission != null) {
                  return false;
               }
            } else if (!this$permission.equals(other$permission)) {
               return false;
            }

            if (this.isRequiresOp() != other.isRequiresOp()) {
               return false;
            } else {
               Object this$commandName = this.getCommandName();
               Object other$commandName = other.getCommandName();
               if (this$commandName == null) {
                  if (other$commandName != null) {
                     return false;
                  }
               } else if (!this$commandName.equals(other$commandName)) {
                  return false;
               }

               Object this$description = this.getDescription();
               Object other$description = other.getDescription();
               if (this$description == null) {
                  if (other$description != null) {
                     return false;
                  }
               } else if (!this$description.equals(other$description)) {
                  return false;
               }

               Object this$rank = this.getRank();
               Object other$rank = other.getRank();
               if (this$rank == null) {
                  if (other$rank != null) {
                     return false;
                  }
               } else if (!this$rank.equals(other$rank)) {
                  return false;
               }

               Object this$alias = this.getAlias();
               Object other$alias = other.getAlias();
               if (this$alias == null) {
                  if (other$alias != null) {
                     return false;
                  }
               } else if (!this$alias.equals(other$alias)) {
                  return false;
               }

               Object this$arguments = this.getArguments();
               Object other$arguments = other.getArguments();
               if (this$arguments == null) {
                  if (other$arguments != null) {
                     return false;
                  }
               } else if (!this$arguments.equals(other$arguments)) {
                  return false;
               }

               Object this$invoke = this.getInvoke();
               Object other$invoke = other.getInvoke();
               if (this$invoke == null) {
                  if (other$invoke != null) {
                     return false;
                  }
               } else if (!this$invoke.equals(other$invoke)) {
                  return false;
               }

               if (this.isMineman() != other.isMineman()) {
                  return false;
               } else {
                  return true;
               }
            }
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof SubCommandComponent;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $permission = this.getPermission();
      result = result * 59 + ($permission == null ? 43 : $permission.hashCode());
      result = result * 59 + (this.isRequiresOp() ? 79 : 97);
      Object $commandName = this.getCommandName();
      result = result * 59 + ($commandName == null ? 43 : $commandName.hashCode());
      Object $description = this.getDescription();
      result = result * 59 + ($description == null ? 43 : $description.hashCode());
      Object $rank = this.getRank();
      result = result * 59 + ($rank == null ? 43 : $rank.hashCode());
      Object $alias = this.getAlias();
      result = result * 59 + ($alias == null ? 43 : $alias.hashCode());
      Object $arguments = this.getArguments();
      result = result * 59 + ($arguments == null ? 43 : $arguments.hashCode());
      Object $invoke = this.getInvoke();
      result = result * 59 + ($invoke == null ? 43 : $invoke.hashCode());
      result = result * 59 + (this.isMineman() ? 79 : 97);
      return result;
   }

   public String toString() {
      return "SubCommandComponent(permission=" + this.getPermission() + ", requiresOp=" + this.isRequiresOp() + ", commandName=" + this.getCommandName() + ", description=" + this.getDescription() + ", rank=" + this.getRank() + ", alias=" + this.getAlias() + ", arguments=" + this.getArguments() + ", invoke=" + this.getInvoke() + ", mineman=" + this.isMineman() + ")";
   }
}
