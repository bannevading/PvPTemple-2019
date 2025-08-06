package server.pvptemple.api.impl;

import java.beans.ConstructorProperties;
import java.util.Map;
import server.pvptemple.api.request.Request;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.MapUtil;
import server.pvptemple.util.finalutil.TimeUtil;

public abstract class PlayerRequest implements Request {
   private final String path;
   private final String name;

   public String getPath() {
      return "/player/" + this.name + "/" + this.path;
   }

   public Map<String, Object> toMap() {
      return null;
   }

   @ConstructorProperties({"path", "name"})
   public PlayerRequest(String path, String name) {
      this.path = path;
      this.name = name;
   }

   public static final class AltsRequest extends PlayerRequest {
      public AltsRequest(String name) {
         super("alts", name);
      }
   }

   public static final class BanInfoRequest extends PlayerRequest {
      public BanInfoRequest(String name) {
         super("ban-info", name);
      }
   }

   public static final class RankUpdateRequest extends PlayerRequest {
      private final Rank rank;
      private final long duration;
      private final int givenBy;

      public RankUpdateRequest(String name, Rank rank, long duration, int givenBy) {
         super("update-rank", name);
         this.rank = rank;
         this.duration = duration;
         this.givenBy = givenBy;
      }

      public Map<String, Object> toMap() {
         return MapUtil.of("given-by", this.givenBy, "rank", this.rank.getName(), "start-time", TimeUtil.getCurrentTimestamp(), "end-time", this.duration == -1L ? "PERM" : TimeUtil.addDuration(this.duration));
      }
   }
}
