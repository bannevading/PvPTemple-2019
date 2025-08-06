package server.pvptemple.request;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import server.pvptemple.api.request.Request;

public class UpdateStatsRequest implements Request {
   private final UUID uuid;
   private final int nodebuffElo;
   private final int nodebuffEloParty;
   private final int nodebuffWins;
   private final int nodebuffLosses;
   private final int debuffElo;
   private final int debuffEloParty;
   private final int debuffWins;
   private final int debuffLosses;
   private final int classicElo;
   private final int classicEloParty;
   private final int classicWins;
   private final int classicLosses;
   private final int gappleElo;
   private final int gappleEloParty;
   private final int gappleWins;
   private final int gappleLosses;
   private final int archerElo;
   private final int archerEloParty;
   private final int archerWins;
   private final int archerLosses;
   private final int axeElo;
   private final int axeEloParty;
   private final int axeWins;
   private final int axeLosses;
   private final int hcfElo;
   private final int hcfEloParty;
   private final int hcfWins;
   private final int hcfLosses;
   private final int sumoElo;
   private final int sumoEloParty;
   private final int sumoWins;
   private final int sumoLosses;
   private final int builduhcElo;
   private final int builduhcEloParty;
   private final int builduhcWins;
   private final int builduhcLosses;
   private final int pingRange;
   private final int eloRange;
   private final int id;
   private final boolean scoreboardEnabled;
   private final boolean acceptingDuels;
   private final boolean allowingSpectators;

   public String getPath() {
      return "/practice/" + this.id + "/update";
   }

   public Map<String, Object> toMap() {
      Map<String, Object> map = new HashMap();
      map.put("allowing_spectators", this.allowingSpectators);
      map.put("scoreboard_enabled", this.scoreboardEnabled);
      map.put("accepting_duels", this.acceptingDuels);
      map.put("ping_range", this.pingRange);
      map.put("elo_range", this.eloRange);
      map.put("uuid", this.uuid.toString());
      map.put("id", this.id);
      map.put("nodebuff_elo_party", this.nodebuffEloParty);
      map.put("nodebuff_elo", this.nodebuffElo);
      map.put("nodebuff_wins", this.nodebuffWins);
      map.put("nodebuff_losses", this.nodebuffLosses);
      map.put("debuff_elo_party", this.debuffEloParty);
      map.put("debuff_losses", this.debuffLosses);
      map.put("debuff_wins", this.debuffWins);
      map.put("debuff_elo", this.debuffElo);
      map.put("gapple_elo_party", this.gappleEloParty);
      map.put("gapple_losses", this.gappleLosses);
      map.put("gapple_wins", this.gappleWins);
      map.put("gapple_elo", this.gappleElo);
      map.put("archer_elo_party", this.archerEloParty);
      map.put("archer_losses", this.archerLosses);
      map.put("archer_wins", this.archerWins);
      map.put("archer_elo", this.archerElo);
      map.put("classic_elo_party", this.classicEloParty);
      map.put("classic_losses", this.classicLosses);
      map.put("classic_wins", this.classicWins);
      map.put("classic_elo", this.classicElo);
      map.put("axe_elo_party", this.axeEloParty);
      map.put("axe_losses", this.axeLosses);
      map.put("axe_wins", this.axeWins);
      map.put("axe_elo", this.axeElo);
      map.put("soup_elo_party", this.axeEloParty);
      map.put("soup_losses", this.axeLosses);
      map.put("soup_wins", this.axeWins);
      map.put("soup_elo", this.axeElo);
      map.put("hcf_elo_party", this.hcfEloParty);
      map.put("hcf_losses", this.hcfLosses);
      map.put("hcf_wins", this.hcfWins);
      map.put("hcf_elo", this.hcfElo);
      map.put("sumo_elo_party", this.sumoEloParty);
      map.put("sumo_losses", this.sumoLosses);
      map.put("sumo_wins", this.sumoWins);
      map.put("sumo_elo", this.sumoElo);
      map.put("builduhc_elo_party", this.builduhcEloParty);
      map.put("builduhc_losses", this.builduhcLosses);
      map.put("builduhc_wins", this.builduhcWins);
      map.put("builduhc_elo", this.builduhcElo);
      return map;
   }

   @ConstructorProperties({"uuid", "nodebuffElo", "nodebuffEloParty", "nodebuffWins", "nodebuffLosses", "debuffElo", "debuffEloParty", "debuffWins", "debuffLosses", "classicElo", "classicEloParty", "classicWins", "classicLosses", "gappleElo", "gappleEloParty", "gappleWins", "gappleLosses", "archerElo", "archerEloParty", "archerWins", "archerLosses", "axeElo", "axeEloParty", "axeWins", "axeLosses", "hcfElo", "hcfEloParty", "hcfWins", "hcfLosses", "sumoElo", "sumoEloParty", "sumoWins", "sumoLosses", "builduhcElo", "builduhcEloParty", "builduhcWins", "builduhcLosses", "pingRange", "eloRange", "id", "scoreboardEnabled", "acceptingDuels", "allowingSpectators"})
   UpdateStatsRequest(UUID uuid, int nodebuffElo, int nodebuffEloParty, int nodebuffWins, int nodebuffLosses, int debuffElo, int debuffEloParty, int debuffWins, int debuffLosses, int classicElo, int classicEloParty, int classicWins, int classicLosses, int gappleElo, int gappleEloParty, int gappleWins, int gappleLosses, int archerElo, int archerEloParty, int archerWins, int archerLosses, int axeElo, int axeEloParty, int axeWins, int axeLosses, int hcfElo, int hcfEloParty, int hcfWins, int hcfLosses, int sumoElo, int sumoEloParty, int sumoWins, int sumoLosses, int builduhcElo, int builduhcEloParty, int builduhcWins, int builduhcLosses, int pingRange, int eloRange, int id, boolean scoreboardEnabled, boolean acceptingDuels, boolean allowingSpectators) {
      this.uuid = uuid;
      this.nodebuffElo = nodebuffElo;
      this.nodebuffEloParty = nodebuffEloParty;
      this.nodebuffWins = nodebuffWins;
      this.nodebuffLosses = nodebuffLosses;
      this.debuffElo = debuffElo;
      this.debuffEloParty = debuffEloParty;
      this.debuffWins = debuffWins;
      this.debuffLosses = debuffLosses;
      this.classicElo = classicElo;
      this.classicEloParty = classicEloParty;
      this.classicWins = classicWins;
      this.classicLosses = classicLosses;
      this.gappleElo = gappleElo;
      this.gappleEloParty = gappleEloParty;
      this.gappleWins = gappleWins;
      this.gappleLosses = gappleLosses;
      this.archerElo = archerElo;
      this.archerEloParty = archerEloParty;
      this.archerWins = archerWins;
      this.archerLosses = archerLosses;
      this.axeElo = axeElo;
      this.axeEloParty = axeEloParty;
      this.axeWins = axeWins;
      this.axeLosses = axeLosses;
      this.hcfElo = hcfElo;
      this.hcfEloParty = hcfEloParty;
      this.hcfWins = hcfWins;
      this.hcfLosses = hcfLosses;
      this.sumoElo = sumoElo;
      this.sumoEloParty = sumoEloParty;
      this.sumoWins = sumoWins;
      this.sumoLosses = sumoLosses;
      this.builduhcElo = builduhcElo;
      this.builduhcEloParty = builduhcEloParty;
      this.builduhcWins = builduhcWins;
      this.builduhcLosses = builduhcLosses;
      this.pingRange = pingRange;
      this.eloRange = eloRange;
      this.id = id;
      this.scoreboardEnabled = scoreboardEnabled;
      this.acceptingDuels = acceptingDuels;
      this.allowingSpectators = allowingSpectators;
   }

   public static UpdateStatsRequestBuilder builder() {
      return new UpdateStatsRequestBuilder();
   }

   public static class UpdateStatsRequestBuilder {
      private UUID uuid;
      private int nodebuffElo;
      private int nodebuffEloParty;
      private int nodebuffWins;
      private int nodebuffLosses;
      private int debuffElo;
      private int debuffEloParty;
      private int debuffWins;
      private int debuffLosses;
      private int classicElo;
      private int classicEloParty;
      private int classicWins;
      private int classicLosses;
      private int gappleElo;
      private int gappleEloParty;
      private int gappleWins;
      private int gappleLosses;
      private int archerElo;
      private int archerEloParty;
      private int archerWins;
      private int archerLosses;
      private int axeElo;
      private int axeEloParty;
      private int axeWins;
      private int axeLosses;
      private int hcfElo;
      private int hcfEloParty;
      private int hcfWins;
      private int hcfLosses;
      private int sumoElo;
      private int sumoEloParty;
      private int sumoWins;
      private int sumoLosses;
      private int builduhcElo;
      private int builduhcEloParty;
      private int builduhcWins;
      private int builduhcLosses;
      private int pingRange;
      private int eloRange;
      private int id;
      private boolean scoreboardEnabled;
      private boolean acceptingDuels;
      private boolean allowingSpectators;

      UpdateStatsRequestBuilder() {
      }

      public UpdateStatsRequestBuilder uuid(UUID uuid) {
         this.uuid = uuid;
         return this;
      }

      public UpdateStatsRequestBuilder nodebuffElo(int nodebuffElo) {
         this.nodebuffElo = nodebuffElo;
         return this;
      }

      public UpdateStatsRequestBuilder nodebuffEloParty(int nodebuffEloParty) {
         this.nodebuffEloParty = nodebuffEloParty;
         return this;
      }

      public UpdateStatsRequestBuilder nodebuffWins(int nodebuffWins) {
         this.nodebuffWins = nodebuffWins;
         return this;
      }

      public UpdateStatsRequestBuilder nodebuffLosses(int nodebuffLosses) {
         this.nodebuffLosses = nodebuffLosses;
         return this;
      }

      public UpdateStatsRequestBuilder debuffElo(int debuffElo) {
         this.debuffElo = debuffElo;
         return this;
      }

      public UpdateStatsRequestBuilder debuffEloParty(int debuffEloParty) {
         this.debuffEloParty = debuffEloParty;
         return this;
      }

      public UpdateStatsRequestBuilder debuffWins(int debuffWins) {
         this.debuffWins = debuffWins;
         return this;
      }

      public UpdateStatsRequestBuilder debuffLosses(int debuffLosses) {
         this.debuffLosses = debuffLosses;
         return this;
      }

      public UpdateStatsRequestBuilder classicElo(int classicElo) {
         this.classicElo = classicElo;
         return this;
      }

      public UpdateStatsRequestBuilder classicEloParty(int classicEloParty) {
         this.classicEloParty = classicEloParty;
         return this;
      }

      public UpdateStatsRequestBuilder classicWins(int classicWins) {
         this.classicWins = classicWins;
         return this;
      }

      public UpdateStatsRequestBuilder classicLosses(int classicLosses) {
         this.classicLosses = classicLosses;
         return this;
      }

      public UpdateStatsRequestBuilder gappleElo(int gappleElo) {
         this.gappleElo = gappleElo;
         return this;
      }

      public UpdateStatsRequestBuilder gappleEloParty(int gappleEloParty) {
         this.gappleEloParty = gappleEloParty;
         return this;
      }

      public UpdateStatsRequestBuilder gappleWins(int gappleWins) {
         this.gappleWins = gappleWins;
         return this;
      }

      public UpdateStatsRequestBuilder gappleLosses(int gappleLosses) {
         this.gappleLosses = gappleLosses;
         return this;
      }

      public UpdateStatsRequestBuilder archerElo(int archerElo) {
         this.archerElo = archerElo;
         return this;
      }

      public UpdateStatsRequestBuilder archerEloParty(int archerEloParty) {
         this.archerEloParty = archerEloParty;
         return this;
      }

      public UpdateStatsRequestBuilder archerWins(int archerWins) {
         this.archerWins = archerWins;
         return this;
      }

      public UpdateStatsRequestBuilder archerLosses(int archerLosses) {
         this.archerLosses = archerLosses;
         return this;
      }

      public UpdateStatsRequestBuilder axeElo(int axeElo) {
         this.axeElo = axeElo;
         return this;
      }

      public UpdateStatsRequestBuilder axeEloParty(int axeEloParty) {
         this.axeEloParty = axeEloParty;
         return this;
      }

      public UpdateStatsRequestBuilder axeWins(int axeWins) {
         this.axeWins = axeWins;
         return this;
      }

      public UpdateStatsRequestBuilder axeLosses(int axeLosses) {
         this.axeLosses = axeLosses;
         return this;
      }

      public UpdateStatsRequestBuilder hcfElo(int hcfElo) {
         this.hcfElo = hcfElo;
         return this;
      }

      public UpdateStatsRequestBuilder hcfEloParty(int hcfEloParty) {
         this.hcfEloParty = hcfEloParty;
         return this;
      }

      public UpdateStatsRequestBuilder hcfWins(int hcfWins) {
         this.hcfWins = hcfWins;
         return this;
      }

      public UpdateStatsRequestBuilder hcfLosses(int hcfLosses) {
         this.hcfLosses = hcfLosses;
         return this;
      }

      public UpdateStatsRequestBuilder sumoElo(int sumoElo) {
         this.sumoElo = sumoElo;
         return this;
      }

      public UpdateStatsRequestBuilder sumoEloParty(int sumoEloParty) {
         this.sumoEloParty = sumoEloParty;
         return this;
      }

      public UpdateStatsRequestBuilder sumoWins(int sumoWins) {
         this.sumoWins = sumoWins;
         return this;
      }

      public UpdateStatsRequestBuilder sumoLosses(int sumoLosses) {
         this.sumoLosses = sumoLosses;
         return this;
      }

      public UpdateStatsRequestBuilder builduhcElo(int builduhcElo) {
         this.builduhcElo = builduhcElo;
         return this;
      }

      public UpdateStatsRequestBuilder builduhcEloParty(int builduhcEloParty) {
         this.builduhcEloParty = builduhcEloParty;
         return this;
      }

      public UpdateStatsRequestBuilder builduhcWins(int builduhcWins) {
         this.builduhcWins = builduhcWins;
         return this;
      }

      public UpdateStatsRequestBuilder builduhcLosses(int builduhcLosses) {
         this.builduhcLosses = builduhcLosses;
         return this;
      }

      public UpdateStatsRequestBuilder pingRange(int pingRange) {
         this.pingRange = pingRange;
         return this;
      }

      public UpdateStatsRequestBuilder eloRange(int eloRange) {
         this.eloRange = eloRange;
         return this;
      }

      public UpdateStatsRequestBuilder id(int id) {
         this.id = id;
         return this;
      }

      public UpdateStatsRequestBuilder scoreboardEnabled(boolean scoreboardEnabled) {
         this.scoreboardEnabled = scoreboardEnabled;
         return this;
      }

      public UpdateStatsRequestBuilder acceptingDuels(boolean acceptingDuels) {
         this.acceptingDuels = acceptingDuels;
         return this;
      }

      public UpdateStatsRequestBuilder allowingSpectators(boolean allowingSpectators) {
         this.allowingSpectators = allowingSpectators;
         return this;
      }

      public UpdateStatsRequest build() {
         return new UpdateStatsRequest(this.uuid, this.nodebuffElo, this.nodebuffEloParty, this.nodebuffWins, this.nodebuffLosses, this.debuffElo, this.debuffEloParty, this.debuffWins, this.debuffLosses, this.classicElo, this.classicEloParty, this.classicWins, this.classicLosses, this.gappleElo, this.gappleEloParty, this.gappleWins, this.gappleLosses, this.archerElo, this.archerEloParty, this.archerWins, this.archerLosses, this.axeElo, this.axeEloParty, this.axeWins, this.axeLosses, this.hcfElo, this.hcfEloParty, this.hcfWins, this.hcfLosses, this.sumoElo, this.sumoEloParty, this.sumoWins, this.sumoLosses, this.builduhcElo, this.builduhcEloParty, this.builduhcWins, this.builduhcLosses, this.pingRange, this.eloRange, this.id, this.scoreboardEnabled, this.acceptingDuels, this.allowingSpectators);
      }

      public String toString() {
         return "UpdateStatsRequest.UpdateStatsRequestBuilder(uuid=" + this.uuid + ", nodebuffElo=" + this.nodebuffElo + ", nodebuffEloParty=" + this.nodebuffEloParty + ", nodebuffWins=" + this.nodebuffWins + ", nodebuffLosses=" + this.nodebuffLosses + ", debuffElo=" + this.debuffElo + ", debuffEloParty=" + this.debuffEloParty + ", debuffWins=" + this.debuffWins + ", debuffLosses=" + this.debuffLosses + ", classicElo=" + this.classicElo + ", classicEloParty=" + this.classicEloParty + ", classicWins=" + this.classicWins + ", classicLosses=" + this.classicLosses + ", gappleElo=" + this.gappleElo + ", gappleEloParty=" + this.gappleEloParty + ", gappleWins=" + this.gappleWins + ", gappleLosses=" + this.gappleLosses + ", archerElo=" + this.archerElo + ", archerEloParty=" + this.archerEloParty + ", archerWins=" + this.archerWins + ", archerLosses=" + this.archerLosses + ", axeElo=" + this.axeElo + ", axeEloParty=" + this.axeEloParty + ", axeWins=" + this.axeWins + ", axeLosses=" + this.axeLosses + ", hcfElo=" + this.hcfElo + ", hcfEloParty=" + this.hcfEloParty + ", hcfWins=" + this.hcfWins + ", hcfLosses=" + this.hcfLosses + ", sumoElo=" + this.sumoElo + ", sumoEloParty=" + this.sumoEloParty + ", sumoWins=" + this.sumoWins + ", sumoLosses=" + this.sumoLosses + ", builduhcElo=" + this.builduhcElo + ", builduhcEloParty=" + this.builduhcEloParty + ", builduhcWins=" + this.builduhcWins + ", builduhcLosses=" + this.builduhcLosses + ", pingRange=" + this.pingRange + ", eloRange=" + this.eloRange + ", id=" + this.id + ", scoreboardEnabled=" + this.scoreboardEnabled + ", acceptingDuels=" + this.acceptingDuels + ", allowingSpectators=" + this.allowingSpectators + ")";
      }
   }
}
