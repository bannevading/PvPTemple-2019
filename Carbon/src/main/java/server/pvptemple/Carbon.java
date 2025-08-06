package server.pvptemple;

import java.util.Arrays;
import java.util.Collections;
import com.minexd.spigot.SpigotX;
import org.bukkit.plugin.java.JavaPlugin;
import server.pvptemple.commands.EloCommand;
import server.pvptemple.commands.InvCommand;
import server.pvptemple.commands.PartyCommand;
import server.pvptemple.commands.ResetStatsCommand;
import server.pvptemple.commands.TournamentCommand;
import server.pvptemple.commands.duel.AcceptCommand;
import server.pvptemple.commands.duel.DuelCommand;
import server.pvptemple.commands.duel.SpectateCommand;
import server.pvptemple.commands.event.EventManagerCommand;
import server.pvptemple.commands.event.HostCommand;
import server.pvptemple.commands.event.JoinEventCommand;
import server.pvptemple.commands.event.LeaveEventCommand;
import server.pvptemple.commands.event.SpectateEventCommand;
import server.pvptemple.commands.management.ArenaCommand;
import server.pvptemple.commands.management.EventsCommand;
import server.pvptemple.commands.management.KitCommand;
import server.pvptemple.commands.management.RankedCommand;
import server.pvptemple.commands.management.SpawnsCommand;
import server.pvptemple.commands.time.DayCommand;
import server.pvptemple.commands.time.NightCommand;
import server.pvptemple.commands.time.SunsetCommand;
import server.pvptemple.commands.toggle.ToggleDuelCommand;
import server.pvptemple.commands.toggle.ToggleScoreboardCommand;
import server.pvptemple.commands.toggle.ToggleSpectatorsCommand;
import server.pvptemple.commands.warp.WarpCommand;
import server.pvptemple.handler.CustomMovementHandler;
import server.pvptemple.listeners.EntityListener;
import server.pvptemple.listeners.InventoryListener;
import server.pvptemple.listeners.MatchListener;
import server.pvptemple.listeners.PlayerListener;
import server.pvptemple.listeners.ShutdownListener;
import server.pvptemple.listeners.WorldListener;
import server.pvptemple.manager.BoardManager;
import server.pvptemple.managers.ArenaManager;
import server.pvptemple.managers.ChunkManager;
import server.pvptemple.managers.EditorManager;
import server.pvptemple.managers.EventManager;
import server.pvptemple.managers.InventoryManager;
import server.pvptemple.managers.ItemManager;
import server.pvptemple.managers.KitManager;
import server.pvptemple.managers.MatchManager;
import server.pvptemple.managers.PartyManager;
import server.pvptemple.managers.PlayerManager;
import server.pvptemple.managers.QueueManager;
import server.pvptemple.managers.SpawnManager;
import server.pvptemple.managers.TournamentManager;
import server.pvptemple.providers.PracticeBoard;
import server.pvptemple.runnable.ExpBarRunnable;
import server.pvptemple.runnable.SaveDataRunnable;
import server.pvptemple.settings.PracticeSettingsHandler;
import server.pvptemple.timer.impl.EnderpearlTimer;
import server.pvptemple.util.Config;

public class Carbon extends JavaPlugin {
   private static Carbon instance;
   private Config mainConfig;
   private InventoryManager inventoryManager;
   private EditorManager editorManager;
   private PlayerManager playerManager;
   private ArenaManager arenaManager;
   private MatchManager matchManager;
   private PartyManager partyManager;
   private QueueManager queueManager;
   private EventManager eventManager;
   private ItemManager itemManager;
   private KitManager kitManager;
   private SpawnManager spawnManager;
   private TournamentManager tournamentManager;
   private ChunkManager chunkManager;
   private long lastTournamentHostTime;

   public void onDisable() {
      this.arenaManager.saveArenas();
      this.kitManager.saveKits();
      this.spawnManager.saveConfig();
   }

   public void onEnable() {
      instance = this;
      this.mainConfig = new Config("config", this);
      if (CorePlugin.getInstance().getTimerManager().getTimer(EnderpearlTimer.class) == null) {
         CorePlugin.getInstance().getTimerManager().registerTimer(new EnderpearlTimer());
      }

      CorePlugin.getInstance().setBoardManager(new BoardManager(new PracticeBoard()));
      CorePlugin.getInstance().getSettingsManager().addSettingsHandler(new PracticeSettingsHandler());
      SpigotX.INSTANCE.addMovementHandler(new CustomMovementHandler());
      this.registerCommands();
      this.registerListeners();
      this.registerManagers();
      this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveDataRunnable(), 6000L, 6000L);
      this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ExpBarRunnable(), 2L, 2L);
   }

   private void registerCommands() {
      Arrays.asList(new ToggleDuelCommand(), new ToggleSpectatorsCommand(), new ToggleScoreboardCommand(), new ResetStatsCommand(), new AcceptCommand(), new RankedCommand(), new SunsetCommand(), new HostCommand(), new ArenaCommand(), new NightCommand(), new EventManagerCommand(), new PartyCommand(), new DuelCommand(), new SpectateCommand(), new DayCommand(), new KitCommand(), new LeaveEventCommand(), new EloCommand(), new EventsCommand(), new InvCommand(), new SpawnsCommand(), new WarpCommand(), new SpectateEventCommand(), new TournamentCommand()).forEach((command) -> CorePlugin.getInstance().registerCommand(command, this.getName()));
      CorePlugin.getInstance().getCommandManager().registerAllClasses(Collections.singletonList(new JoinEventCommand()));
   }

   private void registerListeners() {
      Arrays.asList(new EntityListener(), new PlayerListener(), new MatchListener(), new WorldListener(), new ShutdownListener(), new InventoryListener()).forEach((listener) -> this.getServer().getPluginManager().registerEvents(listener, this));
   }

   private void registerManagers() {
      this.spawnManager = new SpawnManager();
      this.arenaManager = new ArenaManager();
      this.chunkManager = new ChunkManager();
      this.editorManager = new EditorManager();
      this.itemManager = new ItemManager();
      this.kitManager = new KitManager();
      this.matchManager = new MatchManager();
      this.partyManager = new PartyManager();
      this.playerManager = new PlayerManager();
      this.queueManager = new QueueManager();
      this.inventoryManager = new InventoryManager();
      this.eventManager = new EventManager();
      this.tournamentManager = new TournamentManager();
   }

   public Config getMainConfig() {
      return this.mainConfig;
   }

   public InventoryManager getInventoryManager() {
      return this.inventoryManager;
   }

   public EditorManager getEditorManager() {
      return this.editorManager;
   }

   public PlayerManager getPlayerManager() {
      return this.playerManager;
   }

   public ArenaManager getArenaManager() {
      return this.arenaManager;
   }

   public MatchManager getMatchManager() {
      return this.matchManager;
   }

   public PartyManager getPartyManager() {
      return this.partyManager;
   }

   public QueueManager getQueueManager() {
      return this.queueManager;
   }

   public EventManager getEventManager() {
      return this.eventManager;
   }

   public ItemManager getItemManager() {
      return this.itemManager;
   }

   public KitManager getKitManager() {
      return this.kitManager;
   }

   public SpawnManager getSpawnManager() {
      return this.spawnManager;
   }

   public TournamentManager getTournamentManager() {
      return this.tournamentManager;
   }

   public ChunkManager getChunkManager() {
      return this.chunkManager;
   }

   public long getLastTournamentHostTime() {
      return this.lastTournamentHostTime;
   }

   public static Carbon getInstance() {
      return instance;
   }

   public void setLastTournamentHostTime(long lastTournamentHostTime) {
      this.lastTournamentHostTime = lastTournamentHostTime;
   }
}
