package server.pvptemple.util.cmd.param.impl.serverdata;

import java.beans.ConstructorProperties;
import server.pvptemple.server.ServerData;

public class WrappedServerData {
   private String argument;
   private ServerData serverData;

   public String getArgument() {
      return this.argument;
   }

   public ServerData getServerData() {
      return this.serverData;
   }

   public void setArgument(String argument) {
      this.argument = argument;
   }

   public void setServerData(ServerData serverData) {
      this.serverData = serverData;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof WrappedServerData)) {
         return false;
      } else {
         WrappedServerData other = (WrappedServerData)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$argument = this.getArgument();
            Object other$argument = other.getArgument();
            if (this$argument == null) {
               if (other$argument != null) {
                  return false;
               }
            } else if (!this$argument.equals(other$argument)) {
               return false;
            }

            Object this$serverData = this.getServerData();
            Object other$serverData = other.getServerData();
            if (this$serverData == null) {
               if (other$serverData != null) {
                  return false;
               }
            } else if (!this$serverData.equals(other$serverData)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof WrappedServerData;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $argument = this.getArgument();
      result = result * 59 + ($argument == null ? 43 : $argument.hashCode());
      Object $serverData = this.getServerData();
      result = result * 59 + ($serverData == null ? 43 : $serverData.hashCode());
      return result;
   }

   public String toString() {
      return "WrappedServerData(argument=" + this.getArgument() + ", serverData=" + this.getServerData() + ")";
   }

   @ConstructorProperties({"argument", "serverData"})
   public WrappedServerData(String argument, ServerData serverData) {
      this.argument = argument;
      this.serverData = serverData;
   }
}
