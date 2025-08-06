package server.pvptemple.entity.wrapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import javax.crypto.SecretKey;
import net.minecraft.server.v1_8_R3.EnumProtocol;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;

public class NetworkManagerWrapper extends NetworkManager {
   private IChatBaseComponent disconnectReason;

   public NetworkManagerWrapper() {
      super(EnumProtocolDirection.SERVERBOUND);
   }

   public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
   }

   public void a(EnumProtocol enumprotocol) {
   }

   public void channelInactive(ChannelHandlerContext channelhandlercontext) {
   }

   public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {
   }

   protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) {
   }

   public void handle(Packet packet) {
      super.handle(packet);
   }

   @SafeVarargs
   public final void a(Packet packet, GenericFutureListener<? extends Future<? super Void>> genericFutureListener, GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
   }

   public SocketAddress getSocketAddress() {
      return new SocketAddress() {
      };
   }

   public void close(IChatBaseComponent ichatbasecomponent) {
      this.disconnectReason = ichatbasecomponent;
   }

   public boolean c() {
      return false;
   }

   public void a(SecretKey secretkey) {
   }

   public IChatBaseComponent j() {
      return this.disconnectReason;
   }

   public void k() {
   }

   protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet object) throws Exception {
   }
}
