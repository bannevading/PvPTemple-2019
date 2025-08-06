package server.pvptemple.rank;

import java.util.Arrays;

import lombok.Getter;
import server.pvptemple.util.finalutil.CC;

public enum Rank {
   NORMAL("", CC.GRAY, "Normal"),
   BASIC(CC.WHITE + "❃", CC.GRAY, "Basic"),
   PRIME(CC.DARK_GREEN + "✵", CC.GREEN, "Prime"),
   ELITE(CC.YELLOW + "✽", CC.GOLD, "Elite"),
   MASTER(CC.AQUA + "❋", CC.DARK_AQUA, "Master"),
   YOUTUBER(CC.DARK_GRAY + "[" + CC.LIGHT_PURPLE + "YouTuber" + CC.DARK_GRAY + "] ", CC.LIGHT_PURPLE, "YouTuber"),
   FAMOUS(CC.DARK_GRAY + "[" + CC.LIGHT_PURPLE + "Famous" + CC.DARK_GRAY + "] ", CC.LIGHT_PURPLE, "Famous"),
   PARTNER(CC.DARK_GRAY + "[" + CC.LIGHT_PURPLE + "Partner" + CC.DARK_GRAY + "] ", CC.LIGHT_PURPLE + CC.I, "Partner"),
   HOST(CC.DARK_GRAY + "[" + CC.D_PURPLE + "Host" + CC.DARK_GRAY + "] ", CC.DARK_PURPLE, "Host"),
   TRAINEE(CC.DARK_GRAY + "[" + CC.YELLOW + "Trainee" + CC.DARK_GRAY + "] ", CC.YELLOW + CC.I, "Trainee"),
   MOD(CC.DARK_GRAY + "[" + CC.DARK_PURPLE + "Mod" + CC.DARK_GRAY + "] ", CC.DARK_PURPLE, "Mod"),
   SENIORMOD(CC.DARK_GRAY + "[" + CC.DARK_PURPLE + "Senior Mod" + CC.DARK_GRAY + "] ", CC.DARK_PURPLE + CC.I, "Senior-Mod"),
   ADMIN(CC.DARK_GRAY + "[" + CC.RED + "Admin" + CC.DARK_GRAY + "] ", CC.RED, "Admin"),
   SENIORADMIN(CC.DARK_GRAY + "[" + CC.RED + "Senior Admin" + CC.DARK_GRAY + "] ", CC.RED, "Senior-Admin"),
   PLATFORMADMIN(CC.DARK_GRAY + "[" + CC.RED + "Platform Admin" + CC.DARK_GRAY + "] ", CC.RED + CC.I, "Platform-Admin"),
   DEVELOPER(CC.DARK_GRAY + "[" + CC.AQUA + "Developer" + CC.DARK_GRAY + "] ", CC.AQUA, "Developer"),
   OWNER(CC.DARK_GRAY + "[" + CC.DARK_RED + "Owner" + CC.DARK_GRAY + "] ", CC.DARK_RED, "Owner");

   public static final Rank[] RANKS = values();
   @Getter
   private final String prefix;
   @Getter
   private final String color;
   @Getter
   private final String name;

   Rank(String prefix, String color, String name) {
      this.prefix = prefix;
      this.color = color;
      this.name = name;
   }

   Rank(String color, String name) {
      this(CC.D_GRAY + "[" + color + name + CC.D_GRAY + "] ", color, name);
   }

   public static Rank getByName(String name) {
      return Arrays.stream(RANKS).filter((rank) -> rank.getName().equalsIgnoreCase(name) || rank.name().equalsIgnoreCase(name)).findFirst().orElse( null);
   }

   public Rank max(Rank rank) {
      return this.getPriority() >= rank.getPriority() ? this : rank;
   }

   public int getPriority() {
      return this.ordinal();
   }

   public boolean hasRank(Rank requiredRank) {
      if (requiredRank == null) {
         return false;
      } else {
         return this.getPriority() >= requiredRank.getPriority();
      }
   }

   public boolean isAbove(Rank rank) {
      return this.ordinal() > rank.ordinal();
   }

}
