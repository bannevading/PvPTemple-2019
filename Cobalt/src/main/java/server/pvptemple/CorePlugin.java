
package server.pvptemple;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import server.pvptemple.api.CoreProcessor;
import server.pvptemple.command.DisguiseCommand;
import server.pvptemple.command.MaxPlayersCommand;
import server.pvptemple.command.impl.AltsCommand;
import server.pvptemple.command.impl.BanInfoCommand;
import server.pvptemple.command.impl.ClearChatCommand;
import server.pvptemple.command.impl.ClearEntitiesCommand;
import server.pvptemple.command.impl.ColorCommand;
import server.pvptemple.command.impl.DevChatCommand;
import server.pvptemple.command.impl.FreezeCommand;
import server.pvptemple.command.impl.HostChatCommand;
import server.pvptemple.command.impl.IgnoreCommand;
import server.pvptemple.command.impl.InfoCommand;
import server.pvptemple.command.impl.MessageCommand;
import server.pvptemple.command.impl.PunishmentHistoryCommand;
import server.pvptemple.command.impl.RankCommand;
import server.pvptemple.command.impl.RegisterCommand;
import server.pvptemple.command.impl.ReplyCommand;
import server.pvptemple.command.impl.ReportCommand;
import server.pvptemple.command.impl.RequestCommand;
import server.pvptemple.command.impl.SettingsCommand;
import server.pvptemple.command.impl.ShutdownCommand;
import server.pvptemple.command.impl.SilenceChatCommand;
import server.pvptemple.command.impl.SlowChatCommand;
import server.pvptemple.command.impl.StaffChatCommand;
import server.pvptemple.command.impl.TeleportCommand;
import server.pvptemple.command.impl.ToggleChatCommand;
import server.pvptemple.command.impl.ToggleMessagesCommand;
import server.pvptemple.command.impl.ToggleStaffMessagesCommand;
import server.pvptemple.command.impl.VanishCommand;
import server.pvptemple.command.impl.WhoCommand;
import server.pvptemple.command.impl.essentials.BroadcastCommand;
import server.pvptemple.command.impl.essentials.ClearCommand;
import server.pvptemple.command.impl.essentials.CreativeCommand;
import server.pvptemple.command.impl.essentials.FeedCommand;
import server.pvptemple.command.impl.essentials.FlyCommand;
import server.pvptemple.command.impl.essentials.GamemodeCommand;
import server.pvptemple.command.impl.essentials.HealCommand;
import server.pvptemple.command.impl.essentials.InvseeCommand;
import server.pvptemple.command.impl.essentials.JoinCommand;
import server.pvptemple.command.impl.essentials.MoreCommand;
import server.pvptemple.command.impl.essentials.PingCommand;
import server.pvptemple.command.impl.essentials.RenameCommand;
import server.pvptemple.command.impl.essentials.SpawnerCommand;
import server.pvptemple.command.impl.essentials.SpeedCommand;
import server.pvptemple.command.impl.essentials.StoreAlertCommand;
import server.pvptemple.command.impl.essentials.SurvivalCommand;
import server.pvptemple.command.impl.essentials.TopCommand;
import server.pvptemple.command.impl.essentials.WorldCommand;
import server.pvptemple.command.impl.punish.BanCommand;
import server.pvptemple.command.impl.punish.BlacklistCommand;
import server.pvptemple.command.impl.punish.IPBanCommand;
import server.pvptemple.command.impl.punish.KickCommand;
import server.pvptemple.command.impl.punish.MuteCommand;
import server.pvptemple.command.impl.punish.UnbanCommand;
import server.pvptemple.command.impl.punish.UnblacklistCommand;
import server.pvptemple.command.impl.punish.UnmuteCommand;
import server.pvptemple.disguise.DisguiseManager;
import server.pvptemple.entity.EntityManager;
import server.pvptemple.gson.CustomLocationTypeAdapterFactory;
import server.pvptemple.gson.ItemStackTypeAdapterFactory;
import server.pvptemple.listener.BungeeListener;
import server.pvptemple.listener.PlayerListener;
import server.pvptemple.listener.RankListener;
import server.pvptemple.listener.ServerListener;
import server.pvptemple.listener.UIListener;
import server.pvptemple.manager.BoardManager;
import server.pvptemple.manager.FilterManager;
import server.pvptemple.manager.MinemanManager;
import server.pvptemple.manager.PunishmentManager;
import server.pvptemple.redis.BungeeRedisManager;
import server.pvptemple.redis.CoreRedisManager;
import server.pvptemple.redis.JedisConfig;
import server.pvptemple.redis.JedisSettings;
import server.pvptemple.redis.JedisSubscriber;
import server.pvptemple.server.ServerManager;
import server.pvptemple.settings.SettingsManager;
import server.pvptemple.task.AutoMessageTask;
import server.pvptemple.task.AutomaticShutdownTask;
import server.pvptemple.task.BroadcastTask;
import server.pvptemple.task.FetchAnnouncementsTask;
import server.pvptemple.task.ServerHeartbeatTask;
import server.pvptemple.task.ServerHeartbeatTimeoutTask;
import server.pvptemple.task.ShutdownTask;
import server.pvptemple.timer.TimerManager;
import server.pvptemple.util.Config;
import server.pvptemple.util.RedisCommand;
import server.pvptemple.util.cmd.CommandManager;

public class CorePlugin extends JavaPlugin {
    public static boolean SETUP = false;
    public static GsonBuilder GSONBUILDER;
    public static Gson GSON;
    private static Field bukkitCommandMap;
    @Getter
    private static CorePlugin instance;
    @Getter
    private List<String> announcements = new ArrayList();
    @Getter
    private Set<String> filteredPhrases;
    @Getter
    private Set<String> filteredWords;
    @Getter
    @Setter
    private ShutdownTask shutdownTask = null;
    @Getter
    private CoreRedisManager coreRedisManager;
    @Getter
    private PunishmentManager punishmentManager;
    @Getter
    private SettingsManager settingsManager;
    @Getter
    private CoreProcessor requestProcessor;
    @Getter
    private CommandManager commandManager;
    @Getter
    private MinemanManager playerManager;
    @Getter
    private EntityManager entityManager;
    @Getter
    private ServerManager serverManager;
    @Getter
    private BoardManager boardManager;
    @Getter
    private FilterManager filterManager;
    @Getter
    private TimerManager timerManager;
    @Getter
    private DisguiseManager disguiseManager;
    @Getter
    private JedisConfig jedisConfig;
    @Getter
    private String apiUrl;
    @Getter
    private String apiKey;
    @Getter
    private boolean sendJoinMessages;
    @Getter
    @Setter
    private BooleanSupplier setupSupplier = new BooleanSupplier() {
        private int attempts;

        public boolean getAsBoolean() {
            return this.attempts++ >= 3;
        }
    };

    public static String getRequestNameOrUUID(String name) {
        OfflinePlayer targetPlayer = getInstance().getServer().getPlayerExact(name);
        if (targetPlayer == null) {
            targetPlayer = getInstance().getServer().getOfflinePlayer(name);
        }

        return targetPlayer == null ? name : targetPlayer.getUniqueId().toString();
    }

    public void onEnable() {
        instance = this;
        GSONBUILDER = (new GsonBuilder()).registerTypeAdapterFactory(new CustomLocationTypeAdapterFactory()).registerTypeAdapterFactory(new ItemStackTypeAdapterFactory());
        GSON = GSONBUILDER.create();
        this.saveDefaultConfig();
        this.apiUrl = this.getConfig().getString("api.url");
        this.apiKey = this.getConfig().getString("api.key");
        this.sendJoinMessages = this.getConfig().getBoolean("join-messages", true);
        this.jedisConfig = new JedisConfig(this);
        this.coreRedisManager = new CoreRedisManager(this);
        this.disguiseManager = new DisguiseManager(this);
        this.commandManager = new CommandManager();
        this.entityManager = new EntityManager();
        this.playerManager = new MinemanManager();
        this.filterManager = new FilterManager();
        this.settingsManager = new SettingsManager(this);
        this.serverManager = new ServerManager(this);
        this.requestProcessor = new CoreProcessor(this, this.apiUrl, this.apiKey);
        this.punishmentManager = new PunishmentManager(this);
        this.timerManager = new TimerManager(this);
        Config config = new Config("filter", this);
        this.filteredPhrases = new HashSet(config.getConfig().getStringList("filtered-phrases"));
        this.filteredWords = new HashSet(config.getConfig().getStringList("filtered-words"));
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ServerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new UIListener(), this);
        this.getServer().getPluginManager().registerEvents(new RankListener(), this);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener(this));
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        new JedisSubscriber(this.jedisConfig.toJedisSettings(), "proxy-core", JsonObject.class, new BungeeRedisManager());
        FreezeCommand command = new FreezeCommand();
        this.getServer().getPluginManager().registerEvents(command, this);
        Arrays.asList(new BanCommand(), new MuteCommand(), new KickCommand(), new BroadcastCommand(), new StoreAlertCommand(), new TopCommand(), new UnbanCommand(), new UnmuteCommand(), new BlacklistCommand(), new UnblacklistCommand(), new IPBanCommand(), new ReportCommand(this), new RequestCommand(this), new CreativeCommand(), new GamemodeCommand(), new SurvivalCommand(), new FeedCommand(), new HealCommand(), new InvseeCommand(), new PingCommand(), new FlyCommand(), new JoinCommand(), new RenameCommand(), new ClearCommand(), new WorldCommand(), new MoreCommand(), new SpawnerCommand(), new SpeedCommand()).forEach(this::registerCommand);
        this.commandManager.registerAllClasses(Arrays.asList(new RankCommand(), new PunishmentHistoryCommand(), new BanInfoCommand(), command, new MessageCommand(), new ReplyCommand(), new IgnoreCommand(), new StaffChatCommand(), new VanishCommand(), new SilenceChatCommand(), new ToggleMessagesCommand(), new ToggleChatCommand(), new WhoCommand(), new ClearChatCommand(), new AltsCommand(), new ColorCommand(), new ShutdownCommand(), new SettingsCommand(), new DevChatCommand(), new SlowChatCommand(), new TeleportCommand(), new ClearEntitiesCommand(), new HostChatCommand(), new DisguiseCommand(this), new ToggleStaffMessagesCommand(), new MaxPlayersCommand(this), new RegisterCommand(), new InfoCommand()));
        (new BukkitRunnable() {
            public void run() {
                if (CorePlugin.this.setupSupplier.getAsBoolean()) {
                    CorePlugin.SETUP = true;
                    if (!CorePlugin.this.getConfig().getBoolean("server-data.private-server")) {
                        CorePlugin.this.getServerManager().setJoinable(true);
                    }

                    CorePlugin.this.getLogger().info("Server is now setup!");
                    this.cancel();
                } else {
                    CorePlugin.this.getLogger().info("Waiting to be setup...");
                }

            }
        }).runTaskTimerAsynchronously(this, 0L, 20L);
        (new ServerHeartbeatTask(this)).runTaskTimerAsynchronously(this, 20L, 20L);
        (new ServerHeartbeatTimeoutTask(this)).runTaskTimerAsynchronously(this, 20L, 20L);
        (new FetchAnnouncementsTask(this)).runTaskTimerAsynchronously(this, 20L, 1200L);
        new AutoMessageTask();
        (new BroadcastTask(this)).runTaskTimerAsynchronously(this, 1200L, 2400L);
        (new AutomaticShutdownTask()).runTaskTimerAsynchronously(this, 0L, 20L);
        Bukkit.getScheduler().runTaskTimer(this, () -> this.playerManager.getPlayers().values().removeIf((mineman) -> Bukkit.getPlayer(mineman.getUuid()) == null), 1200L, 1200L);
    }

    public void onDisable() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("server-name", this.getServerManager().getServerName());
        jsonObject.addProperty("action", "offline");
        this.getServerManager().getServerHeartbeatPublisher().write(jsonObject);
        JsonObject proxyObject = new JsonObject();
        proxyObject.addProperty("disable", true);
        proxyObject.addProperty("server", this.getServerManager().getServerName());
        this.getServerManager().getProxyPublisher().write(proxyObject);
        instance = null;
    }

    private void registerCommand(Command cmd) {
        this.registerCommand(cmd, this.getName());
    }

    public void registerCommand(Command cmd, String fallbackPrefix) {
        try {
            if (bukkitCommandMap == null) {
                bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                bukkitCommandMap.setAccessible(true);
            }

            CommandMap commandMap = (CommandMap)bukkitCommandMap.get(Bukkit.getServer());
            commandMap.register(cmd.getName(), fallbackPrefix, cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setBoardManager(BoardManager boardManager) {
        this.boardManager = boardManager;
        long interval = this.boardManager.getAdapter().getInterval();
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, this.boardManager, interval, interval);
    }

    public void runRedisCommand(RedisCommand<Jedis> jedisExecutor) {
        JedisSettings jedisSettings = this.jedisConfig.toJedisSettings();
        Jedis jedis = jedisSettings.getJedisPool().getResource();
        jedisExecutor.execute(jedis);
    }

    public int getServerPlayerCount() {
        return Bukkit.getOnlinePlayers().size();
    }

}
