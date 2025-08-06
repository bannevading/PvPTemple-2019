package server.pvptemple.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;
import server.pvptemple.util.finalutil.Color;

public class AutoMessageTask extends BukkitRunnable {
   private int lastCount;
   private List<String> defaultMessages = new ArrayList();
   private List<String> practiceMessages = new ArrayList();
   private List<String> uhcMessages = new ArrayList();
   private List<String> hcfMessages = new ArrayList();

   public AutoMessageTask() {
      this.setupMessages();
      this.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20L, 2400L);
   }

   public void run() {
      String server = CorePlugin.getInstance().getServerManager().getServerName();
      if (!server.contains("hcf") && !server.contains("powers")) {
         if (server.contains("practice-")) {
            this.sendMessage(this.practiceMessages);
         } else if (!server.contains("uhc-") && !server.contains("meetup-") && !server.equalsIgnoreCase("uhcgames")) {
            this.sendMessage(this.defaultMessages);
         } else {
            this.sendMessage(this.uhcMessages);
         }
      } else {
         this.sendMessage(this.hcfMessages);
      }

   }

   private void setupMessages() {
      this.defaultMessages.add("&8[&6TIP&8] &eFollow our Twitter account for news and giveaways - www.twitter.com/ThePvPTemple");
      this.defaultMessages.add("&8[&6TIP&8] &eJoin our Discord server to chat with players, get support, and see sneak peeks - www.pvptemple.com/discord");
      this.defaultMessages.add("&8[&6TIP&8] &eIn need of help? Join our TeamSpeak server - ts.pvptemple.com");
      this.defaultMessages.add("&8[&6TIP&8] &ePurchase ranks, perks, and more on our shop - shop.pvptemple.com");
      this.defaultMessages.add("&8[&6TIP&8] &eConfigure our systems to your liking by using /settings.");
      this.defaultMessages.add("&8[&6TIP&8] &ePunished? Appeal on our Discord server or purchase to remove your punishment.");
      this.defaultMessages.add("&8[&6TIP&8] &eStaff applications are always open - www.goo.gl/forms/RLuQiOiJLgugaYcl2");
      this.practiceMessages.addAll(this.defaultMessages);
      this.practiceMessages.add("&8[&6TIP&8] &eDonators can host events using /event.");
      this.practiceMessages.add("&8[&6TIP&8] &eButterfly clicking may result in a punishment! Use at your own risk.");
      this.practiceMessages.add("&8[&6TIP&8] &eView the leaderboards on our website - www.pvptemple.com/leaderboards");
      this.uhcMessages.addAll(this.defaultMessages);
      this.uhcMessages.add("&8[&6TIP&8] &eFollow our UHC Twitter to be notified when there is a UHC game - www.twitter.com/PvPTempleFeed");
      this.uhcMessages.add("&8[&6TIP&8] &eView the leaderboards on our website - www.pvptemple.com/leaderboards");
      this.hcfMessages.addAll(this.defaultMessages);
      this.hcfMessages.add("&8[&6TIP&8] &eUse /coords to list all event locations.");
      this.hcfMessages.add("&8[&6TIP&8] &eRemember that Enderpearls can pass through fence gates!");
      this.hcfMessages.add("&8[&6TIP&8] &eKicking and killing members is not allowed.");
      this.hcfMessages.add("&8[&6TIP&8] &eTruce with other factions is not allowed. Only two solo factions can truce.");
      this.hcfMessages.add("&8[&6TIP&8] &eDTR evading is not allowed.");
      this.hcfMessages.add("&8[&6TIP&8] &ePurchase lives and crates on our shop - shop.pvptemple.com");
      this.hcfMessages.add("&8[&6TIP&8] &eFor all faction related commands, use /faction.");
      this.hcfMessages.add("&8[&6TIP&8] &eFor all lives related commands, use /lives.");
      this.hcfMessages.add("&8[&6TIP&8] &eSuffocation traps are not allowed.");
   }

   private void sendMessage(List<String> input) {
      Bukkit.broadcastMessage("");
      int count = ThreadLocalRandom.current().nextInt(this.defaultMessages.size());
      Bukkit.broadcastMessage(Color.translate((String)input.get(this.lastCount == count ? ThreadLocalRandom.current().nextInt(input.size()) : count)));
      this.lastCount = count;
      Bukkit.broadcastMessage("");
   }
}
