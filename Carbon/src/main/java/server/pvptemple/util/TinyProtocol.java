package server.pvptemple.util;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class TinyProtocol {
   private static final AtomicInteger ID = new AtomicInteger(0);
   private static final Reflection.MethodInvoker getPlayerHandle = Reflection.getMethod("{obc}.entity.CraftPlayer", "getHandle");
   private static final Reflection.FieldAccessor<Object> getConnection = Reflection.getField("{nms}.EntityPlayer", "playerConnection", Object.class);
   private static final Reflection.FieldAccessor<Object> getManager = Reflection.getField("{nms}.PlayerConnection", "networkManager", Object.class);
   private static final Reflection.FieldAccessor<Channel> getChannel = Reflection.getField("{nms}.NetworkManager", Channel.class, 0);
   private static final Class<Object> minecraftServerClass = Reflection.getUntypedClass("{nms}.MinecraftServer");
   private static final Class<Object> serverConnectionClass = Reflection.getUntypedClass("{nms}.ServerConnection");
   private static final Reflection.FieldAccessor<Object> getMinecraftServer;
   private static final Reflection.FieldAccessor<Object> getServerConnection;
   private static final Reflection.MethodInvoker getNetworkMarkers;
   private static final Class<?> PACKET_LOGIN_IN_START;
   private static final Reflection.FieldAccessor<GameProfile> getGameProfile;
   private Map<String, Channel> channelLookup = (new MapMaker()).weakValues().makeMap();
   private Listener listener;
   private Set<Channel> uninjectedChannels = Collections.newSetFromMap((new MapMaker()).weakKeys().makeMap());
   private List<Object> networkManagers;
   private List<Channel> serverChannels = Lists.newArrayList();
   private ChannelInboundHandlerAdapter serverChannelHandler;
   private ChannelInitializer<Channel> beginInitProtocol;
   private ChannelInitializer<Channel> endInitProtocol;
   private String handlerName;
   protected volatile boolean closed;
   protected Plugin plugin;

   public TinyProtocol(final Plugin plugin) {
      this.plugin = plugin;
      this.handlerName = this.getHandlerName();
      this.registerBukkitEvents();

      try {
         this.registerChannelHandler();
         this.registerPlayers(plugin);
      } catch (IllegalArgumentException var3) {
         plugin.getLogger().info("[TinyProtocol] Delaying server channel injection due to late bind.");
         (new BukkitRunnable() {
            public void run() {
               TinyProtocol.this.registerChannelHandler();
               TinyProtocol.this.registerPlayers(plugin);
               plugin.getLogger().info("[TinyProtocol] Late bind injection successful.");
            }
         }).runTask(plugin);
      }

   }

   private void createServerChannelHandler() {
      this.endInitProtocol = new ChannelInitializer<Channel>() {
         protected void initChannel(Channel channel) throws Exception {
            try {
               synchronized(TinyProtocol.this.networkManagers) {
                  if (!TinyProtocol.this.closed) {
                     TinyProtocol.this.injectChannelInternal(channel);
                  }
               }
            } catch (Exception e) {
               TinyProtocol.this.plugin.getLogger().log(Level.SEVERE, "Cannot inject incomming channel " + channel, e);
            }

         }
      };
      this.beginInitProtocol = new ChannelInitializer<Channel>() {
         protected void initChannel(Channel channel) throws Exception {
            channel.pipeline().addLast(new ChannelHandler[]{TinyProtocol.this.endInitProtocol});
         }
      };
      this.serverChannelHandler = new ChannelInboundHandlerAdapter() {
         public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Channel channel = (Channel)msg;
            channel.pipeline().addFirst(new ChannelHandler[]{TinyProtocol.this.beginInitProtocol});
            ctx.fireChannelRead(msg);
         }
      };
   }

   private void registerBukkitEvents() {
      this.listener = new Listener() {
         @EventHandler(
            priority = EventPriority.LOWEST
         )
         public final void onPlayerLogin(PlayerLoginEvent e) {
            if (!TinyProtocol.this.closed) {
               Channel channel = TinyProtocol.this.getChannel(e.getPlayer());
               if (!TinyProtocol.this.uninjectedChannels.contains(channel)) {
                  TinyProtocol.this.injectPlayer(e.getPlayer());
               }

            }
         }

         @EventHandler
         public final void onPluginDisable(PluginDisableEvent e) {
            if (e.getPlugin().equals(TinyProtocol.this.plugin)) {
               TinyProtocol.this.close();
            }

         }
      };
      this.plugin.getServer().getPluginManager().registerEvents(this.listener, this.plugin);
   }

   private void registerChannelHandler() {
      Object mcServer = getMinecraftServer.get(Bukkit.getServer());
      Object serverConnection = getServerConnection.get(mcServer);
      boolean looking = true;
      this.networkManagers = (List)getNetworkMarkers.invoke((Object)null, serverConnection);
      this.createServerChannelHandler();

      for(int i = 0; looking; ++i) {
         for(Object item : (List)Reflection.getField(serverConnection.getClass(), List.class, i).get(serverConnection)) {
            if (!ChannelFuture.class.isInstance(item)) {
               break;
            }

            Channel serverChannel = ((ChannelFuture)item).channel();
            this.serverChannels.add(serverChannel);
            serverChannel.pipeline().addFirst(new ChannelHandler[]{this.serverChannelHandler});
            looking = false;
         }
      }

   }

   private void unregisterChannelHandler() {
      if (this.serverChannelHandler != null) {
         for(Channel serverChannel : this.serverChannels) {
            final ChannelPipeline pipeline = serverChannel.pipeline();
            serverChannel.eventLoop().execute(new Runnable() {
               public void run() {
                  try {
                     pipeline.remove(TinyProtocol.this.serverChannelHandler);
                  } catch (NoSuchElementException var2) {
                  }

               }
            });
         }

      }
   }

   private void registerPlayers(Plugin plugin) {
      for(Player player : plugin.getServer().getOnlinePlayers()) {
         this.injectPlayer(player);
      }

   }

   public Object onPacketOutAsync(Player reciever, Channel channel, Object packet) {
      return packet;
   }

   public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
      return packet;
   }

   public void sendPacket(Player player, Object packet) {
      this.sendPacket(this.getChannel(player), packet);
   }

   public void sendPacket(Channel channel, Object packet) {
      channel.pipeline().writeAndFlush(packet);
   }

   public void receivePacket(Player player, Object packet) {
      this.receivePacket(this.getChannel(player), packet);
   }

   public void receivePacket(Channel channel, Object packet) {
      channel.pipeline().context("encoder").fireChannelRead(packet);
   }

   protected String getHandlerName() {
      return "tiny-" + this.plugin.getName() + "-" + ID.incrementAndGet();
   }

   public void injectPlayer(Player player) {
      this.injectChannelInternal(this.getChannel(player)).player = player;
   }

   public void injectChannel(Channel channel) {
      this.injectChannelInternal(channel);
   }

   private PacketInterceptor injectChannelInternal(Channel channel) {
      try {
         PacketInterceptor interceptor = (PacketInterceptor)channel.pipeline().get(this.handlerName);
         if (interceptor == null) {
            interceptor = new PacketInterceptor();
            channel.pipeline().addBefore("packet_handler", this.handlerName, interceptor);
            this.uninjectedChannels.remove(channel);
         }

         return interceptor;
      } catch (IllegalArgumentException var3) {
         return (PacketInterceptor)channel.pipeline().get(this.handlerName);
      }
   }

   public Channel getChannel(Player player) {
      Channel channel = (Channel)this.channelLookup.get(player.getName());
      if (channel == null) {
         Object connection = getConnection.get(getPlayerHandle.invoke(player));
         Object manager = getManager.get(connection);
         this.channelLookup.put(player.getName(), channel = getChannel.get(manager));
      }

      return channel;
   }

   public void uninjectPlayer(Player player) {
      this.uninjectChannel(this.getChannel(player));
   }

   public void uninjectChannel(final Channel channel) {
      if (!this.closed) {
         this.uninjectedChannels.add(channel);
      }

      channel.eventLoop().execute(new Runnable() {
         public void run() {
            channel.pipeline().remove(TinyProtocol.this.handlerName);
         }
      });
   }

   public boolean hasInjected(Player player) {
      return this.hasInjected(this.getChannel(player));
   }

   public boolean hasInjected(Channel channel) {
      return channel.pipeline().get(this.handlerName) != null;
   }

   public final void close() {
      if (!this.closed) {
         this.closed = true;

         for(Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.uninjectPlayer(player);
         }

         HandlerList.unregisterAll(this.listener);
         this.unregisterChannelHandler();
      }

   }

   static {
      getMinecraftServer = Reflection.getField("{obc}.CraftServer", minecraftServerClass, 0);
      getServerConnection = Reflection.getField(minecraftServerClass, serverConnectionClass, 0);
      getNetworkMarkers = Reflection.getTypedMethod(serverConnectionClass, (String)null, List.class, serverConnectionClass);
      PACKET_LOGIN_IN_START = Reflection.getMinecraftClass("PacketLoginInStart");
      getGameProfile = Reflection.getField(PACKET_LOGIN_IN_START, GameProfile.class, 0);
   }

   private final class PacketInterceptor extends ChannelDuplexHandler {
      public volatile Player player;

      private PacketInterceptor() {
      }

      public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
         Channel channel = ctx.channel();
         this.handleLoginStart(channel, msg);

         try {
            msg = TinyProtocol.this.onPacketInAsync(this.player, channel, msg);
         } catch (Exception e) {
            TinyProtocol.this.plugin.getLogger().log(Level.SEVERE, "Error in onPacketInAsync().", e);
         }

         if (msg != null) {
            super.channelRead(ctx, msg);
         }

      }

      public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
         try {
            msg = TinyProtocol.this.onPacketOutAsync(this.player, ctx.channel(), msg);
         } catch (Exception e) {
            TinyProtocol.this.plugin.getLogger().log(Level.SEVERE, "Error in onPacketOutAsync().", e);
         }

         if (msg != null) {
            super.write(ctx, msg, promise);
         }

      }

      private void handleLoginStart(Channel channel, Object packet) {
         if (TinyProtocol.PACKET_LOGIN_IN_START.isInstance(packet)) {
            GameProfile profile = TinyProtocol.getGameProfile.get(packet);
            TinyProtocol.this.channelLookup.put(profile.getName(), channel);
         }

      }
   }
}
