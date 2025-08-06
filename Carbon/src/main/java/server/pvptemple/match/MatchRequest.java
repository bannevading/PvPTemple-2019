package server.pvptemple.match;

import java.beans.ConstructorProperties;
import java.util.UUID;
import server.pvptemple.arena.Arena;

public class MatchRequest {
   private final UUID requester;
   private final UUID requested;
   private final Arena arena;
   private final String kitName;
   private final boolean party;

   public UUID getRequester() {
      return this.requester;
   }

   public UUID getRequested() {
      return this.requested;
   }

   public Arena getArena() {
      return this.arena;
   }

   public String getKitName() {
      return this.kitName;
   }

   public boolean isParty() {
      return this.party;
   }

   @ConstructorProperties({"requester", "requested", "arena", "kitName", "party"})
   public MatchRequest(UUID requester, UUID requested, Arena arena, String kitName, boolean party) {
      this.requester = requester;
      this.requested = requested;
      this.arena = arena;
      this.kitName = kitName;
      this.party = party;
   }
}
