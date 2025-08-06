package server.pvptemple.util.cmd;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.ParamType;
import server.pvptemple.util.cmd.annotation.Flag;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.Text;
import server.pvptemple.util.cmd.annotation.commandTypes.BaseCommand;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.cmd.annotation.commandTypes.SubCommand;
import server.pvptemple.util.cmd.commands.BaseCommandComponent;
import server.pvptemple.util.cmd.commands.Cmd;
import server.pvptemple.util.cmd.commands.SubCommandComponent;
import server.pvptemple.util.cmd.param.ParamData;
import server.pvptemple.util.cmd.param.Parameter;
import server.pvptemple.util.cmd.param.impl.BooleanParameter;
import server.pvptemple.util.cmd.param.impl.DoubleParameter;
import server.pvptemple.util.cmd.param.impl.FloatParameter;
import server.pvptemple.util.cmd.param.impl.IntegerParameter;
import server.pvptemple.util.cmd.param.impl.RankParameter;
import server.pvptemple.util.cmd.param.impl.StringParameter;
import server.pvptemple.util.cmd.param.impl.bukkit.PlayerParameter;
import server.pvptemple.util.cmd.param.impl.bukkit.WorldParameter;
import server.pvptemple.util.cmd.param.impl.serverdata.ServerDataParameter;
import server.pvptemple.util.cmd.param.impl.serverdata.WrappedServerData;

public class CommandManager {
   @lombok.Getter
   private Map<Class, Parameter<?>> parameterMap = new HashMap();
   private Map<String, String> baseCommandMap = new HashMap();
   private CommandMap commandMap;

   public CommandManager() {
      try {
         Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
         bukkitCommandMap.setAccessible(true);
         this.commandMap = (CommandMap)bukkitCommandMap.get(Bukkit.getServer());
      } catch (Exception e) {
         e.printStackTrace();
      }

      this.registerParameter(String.class, new StringParameter());
      this.registerParameter(Boolean.TYPE, new BooleanParameter());
      this.registerParameter(Player.class, new PlayerParameter());
      this.registerParameter(Double.TYPE, new DoubleParameter());
      this.registerParameter(Float.TYPE, new FloatParameter());
      this.registerParameter(Integer.TYPE, new IntegerParameter());
      this.registerParameter(World.class, new WorldParameter());
      this.registerParameter(Rank.class, new RankParameter());
      this.registerParameter(WrappedServerData.class, new ServerDataParameter());
   }

   public void registerParameter(Class clazz, Parameter<?> parameter) {
      this.parameterMap.put(clazz, parameter);
   }

   public void registerAllClasses(Collection<server.pvptemple.util.cmd.CommandHandler> classes) {
      for(server.pvptemple.util.cmd.CommandHandler commandHandler : classes) {
         for(Method method : commandHandler.getClass().getMethods()) {
            if (method.isAnnotationPresent(BaseCommand.class)) {
               this.handleBaseCommand(method, commandHandler);
            }

            if (method.isAnnotationPresent(Command.class)) {
               this.handleCommand(method, commandHandler);
            }
         }

         for(Method method : commandHandler.getClass().getMethods()) {
            if (method.isAnnotationPresent(SubCommand.class)) {
               this.handleSubCommand(method, commandHandler);
            }
         }
      }

   }

   private void handleSubCommand(Method method, server.pvptemple.util.cmd.CommandHandler commandHandler) {
      SubCommand subCommand = (SubCommand)method.getAnnotation(SubCommand.class);
      String mainCommandName = (String)this.baseCommandMap.get(subCommand.baseCommand());
      if (mainCommandName == null) {
         throw new NullPointerException("SubCommand has a null name (" + subCommand.baseCommand() + ")");
      } else {
         BaseCommandComponent baseCommandComponent = (BaseCommandComponent)this.commandMap.getCommand(mainCommandName);
         if (baseCommandComponent == null) {
            throw new NullPointerException("BaseCommand was null for a subcommand (" + subCommand.baseCommand() + ")");
         } else {
            String commandName = subCommand.name()[0];
            List<String> alias = Arrays.asList(StringUtils.join((Object[])subCommand.name(), ' ', 1, subCommand.name().length).split(" "));
            alias.remove(commandName);
            Rank rank = baseCommandComponent.getRank().max(subCommand.rank());
            Permission permission = null;
            if (!subCommand.permission().isEmpty()) {
               permission = new Permission(subCommand.permission(), subCommand.permissionDefault());
            }

            SubCommandComponent subCommandComponent = new SubCommandComponent(subCommand.name()[0], subCommand.description(), rank, permission, subCommand.requiresOp(), alias, method.getParameters()[0].getType() == Mineman.class);
            subCommandComponent.setInvoke(Pair.of(method, commandHandler));
            subCommandComponent.getArguments().addAll(this.getArguments(method.getParameters()));

            for(String subCommands : subCommand.name()) {
               baseCommandComponent.getSubCommands().put(subCommands.toLowerCase(), subCommandComponent);
            }

         }
      }
   }

   private void handleBaseCommand(Method method, server.pvptemple.util.cmd.CommandHandler classWithCommand) {
      BaseCommand baseCommand = (BaseCommand)method.getAnnotation(BaseCommand.class);
      String commandName = baseCommand.name()[0];

      for(String alias : baseCommand.name()) {
         this.baseCommandMap.put(alias, commandName);
      }

      Permission permission = null;
      if (!baseCommand.permission().isEmpty()) {
         permission = new Permission(baseCommand.permission(), baseCommand.permissionDefault());
      }

      BaseCommandComponent baseCommandComponent = new BaseCommandComponent(commandName, baseCommand.rank(), permission, baseCommand.requiresOp());
      List<String> alias = Arrays.asList(StringUtils.join((Object[])baseCommand.name(), ' ', 1, baseCommand.name().length).split(" "));
      baseCommandComponent.setAliases(alias);
      baseCommandComponent.setInvoke(Pair.of(method, classWithCommand));
      if (method.getParameters().length > 1) {
         throw new IllegalStateException("There should only be 1 param for a basecommand (The sender)");
      } else {
         this.commandMap.register(commandName, baseCommandComponent);
      }
   }

   private void handleCommand(Method method, CommandHandler classWithCommand) {
      Command command = (Command)method.getAnnotation(Command.class);
      String commandName = command.name()[0];
      List<String> alias = Arrays.asList(StringUtils.join((Object[])command.name(), ' ', 1, command.name().length).split(" "));
      Permission permission = null;
      if (!command.permission().isEmpty()) {
         permission = new Permission(command.permission(), command.permissionDefault());
      }

      Cmd cmd = new Cmd(commandName, command.rank(), permission, command.requiresOp(), method.getParameters()[0].getType() == Mineman.class);
      cmd.setAliases(alias);
      cmd.setDescription(command.description());
      cmd.getArguments().addAll(this.getArguments(method.getParameters()));
      cmd.setInvoke(Pair.of(method, classWithCommand));
      this.commandMap.register(commandName, cmd);
   }

   private List<Pair<Parameter<?>, ParamData>> getArguments(java.lang.reflect.Parameter[] parameters) {
      List<Pair<Parameter<?>, ParamData>> arguments = new ArrayList();
      boolean finalValue = false;
      boolean firstRun = true;

      for(java.lang.reflect.Parameter parameter : parameters) {
         if (firstRun) {
            firstRun = false;
         } else {
            String paramName = parameter.getType().getSimpleName();
            String defaultValue = "";
            server.pvptemple.util.cmd.ParamType type = server.pvptemple.util.cmd.ParamType.ARGUMENT;
            if (finalValue) {
               throw new IllegalArgumentException("There was a argument after a multi argument, idk what to do!");
            }

            if (!this.parameterMap.containsKey(parameter.getType())) {
               throw new IllegalArgumentException("Parameter: " + parameter.getType() + " not registered!");
            }

            if (parameter.isAnnotationPresent(Text.class)) {
               finalValue = true;
               type = server.pvptemple.util.cmd.ParamType.MULTI;
               defaultValue = ((Text)parameter.getAnnotation(Text.class)).value();
               paramName = ((Text)parameter.getAnnotation(Text.class)).name();
            }

            if (parameter.isAnnotationPresent(Param.class)) {
               paramName = ((Param)parameter.getAnnotation(Param.class)).name();
               defaultValue = ((Param)parameter.getAnnotation(Param.class)).defaultTo();
            }

            if (parameter.isAnnotationPresent(Flag.class)) {
               if (parameter.getType() != Boolean.TYPE) {
                  throw new IllegalArgumentException("Parameter: " + parameter.getType() + " must be a boolean!");
               }

               paramName = parameter.getAnnotation(Flag.class).name();
               arguments.add(Pair.of(null, new ParamData(paramName, defaultValue, ParamType.FLAG)));
            } else {
               arguments.add(Pair.of(this.parameterMap.get(parameter.getType()), new ParamData(paramName, defaultValue, type)));
            }
         }
      }

      return arguments;
   }

}
