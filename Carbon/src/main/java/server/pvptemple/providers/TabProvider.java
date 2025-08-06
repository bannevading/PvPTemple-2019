package server.pvptemple.providers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import server.pvptemple.Carbon;
import server.pvptemple.CorePlugin;
import server.pvptemple.party.Party;
import server.pvptemple.player.PlayerData;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.finalutil.PlayerUtil;
import server.pvptemple.util.tab.LayoutProvider;
import server.pvptemple.util.tab.TabLayout;

public class TabProvider implements LayoutProvider {
   public TabLayout getLayout(Player player) {
      TabLayout layout = TabLayout.create(player);
      layout.set(1, 0, (String)("&6&lPractice-" + Carbon.getInstance().getConfig().getString("REGION")));
      layout.set(0, 2, (String)"&e&lYou");
      if (CorePlugin.getInstance() != null && CorePlugin.getInstance().getPlayerManager() != null && CorePlugin.getInstance().getPlayerManager().getPlayers().containsKey(player.getUniqueId())) {
         layout.set(0, 3, (String)("&7Name: " + CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getDisplayRank().getColor() + player.getName()));
         Rank rank = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId()).getRank();
         layout.set(0, 4, (String)("&7Rank: &f" + (rank != null ? rank.getName() : "Loading")));
      }

      layout.set(0, 5, (String)("&7Ping: &f" + PlayerUtil.getPing(player) + " ms"));
      Party party = Carbon.getInstance().getPartyManager().getParty(player.getUniqueId());
      if (party != null) {
         layout.set(1, 2, (String)"&9&lYour Party");
         int[] count = new int[]{3};
         party.getMembers().stream().map(Bukkit::getPlayer).forEach((member) -> {
            layout.set(1, count[0], (String)("&9" + member.getName() + (party.getLeader().equals(member.getUniqueId()) ? "&r &7*" : "")));
            if (count[0] != 17) {
               int var10002 = count[0]++;
            }

         });
      }

      PlayerData data = Carbon.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
      layout.set(0, 7, (String)"&e&lYour Ratings");
      layout.set(0, 8, (String)("&7Global: &f" + data.getGlobalElo()));
      int[] first = new int[]{9};
      Carbon.getInstance().getKitManager().getRankedKits().forEach((kit) -> {
         layout.set(0, first[0], (String)("&7" + kit + ": &f" + data.getElo(kit)));
         if (first[0] != 17) {
            int var10002 = first[0]++;
         }

      });
      layout.set(2, 2, (String)"&e&lPractice");
      layout.set(2, 3, (String)("&7Players: &f" + Bukkit.getOnlinePlayers().size()));
      layout.set(2, 4, (String)("&7Fighting: &f" + Carbon.getInstance().getMatchManager().getFighters()));
      layout.set(2, 5, (String)("&7Queuing: &f" + Carbon.getInstance().getQueueManager().getQueuing()));
      layout.set(0, 19, (String)"&bwww.pvptemple.com");
      layout.set(1, 19, (String)"&bshop.pvptemple.com");
      layout.set(2, 19, (String)"&bts.pvptemple.com");
      return layout;
   }
}
