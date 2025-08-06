package server.pvptemple.util.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import server.pvptemple.util.finalutil.Color;

public class TabLayout {
   public static int WIDTH = 3;
   public static int HEIGHT = 20;
   private static AtomicReference<Object> layout18 = new AtomicReference();
   private static AtomicReference<Object> layoutDefault = new AtomicReference();
   private static Map<String, TabLayout> tabLayouts = new HashMap();
   private static List<String> emptyStrings = new ArrayList();
   private String[] zeroValue;
   private String[] zeroValue18;
   private String[] tabNames;
   private int[] tabPings;
   private boolean is18;

   private TabLayout(boolean is18) {
      this(is18, false);
   }

   private TabLayout(boolean is18, boolean fill) {
      this.zeroValue = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
      this.zeroValue18 = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
      this.is18 = is18;
      this.tabNames = is18 ? (String[])this.zeroValue18.clone() : (String[])this.zeroValue.clone();
      this.tabPings = is18 ? new int[WIDTH * HEIGHT + 20] : new int[WIDTH * HEIGHT];
      if (fill) {
         for(int i = 0; i < this.tabNames.length; ++i) {
            this.tabNames[i] = generateEmpty();
            this.tabPings[i] = 0;
         }
      }

      Arrays.sort(this.tabNames);
   }

   public void forceSet(int pos, String name) {
      this.tabNames[pos] = Color.translate(name);
      this.tabPings[pos] = 0;
   }

   public void forceSet(int x, int y, String name) {
      int pos = this.is18 ? y + x * HEIGHT : x + y * WIDTH;
      this.tabNames[pos] = Color.translate(name);
      this.tabPings[pos] = 0;
   }

   public void set(int x, int y, String name, int ping) {
      if (this.validate(x, y, true)) {
         int pos = this.is18 ? y + x * HEIGHT : x + y * WIDTH;
         this.tabNames[pos] = Color.translate(name);
         this.tabPings[pos] = ping;
      }
   }

   public void set(int x, int y, String name) {
      this.set(x, y, name, 0);
   }

   public void set(int x, int y, Player player) {
      this.set(x, y, player.getName(), ((CraftPlayer)player).getHandle().ping);
   }

   public String getStringAt(int x, int y) {
      this.validate(x, y);
      int pos = this.is18 ? y + x * HEIGHT : x + y * WIDTH;
      return this.tabNames[pos];
   }

   public int getPingAt(int x, int y) {
      this.validate(x, y);
      int pos = this.is18 ? y + x * HEIGHT : x + y * WIDTH;
      return this.tabPings[pos];
   }

   public boolean validate(int x, int y, boolean silent) {
      if (x >= WIDTH) {
         if (!silent) {
            throw new IllegalArgumentException("x >= WIDTH (" + WIDTH + ")");
         } else {
            return false;
         }
      } else if (y < HEIGHT) {
         return true;
      } else if (!silent) {
         throw new IllegalArgumentException("y >= HEIGHT (" + HEIGHT + ")");
      } else {
         return false;
      }
   }

   public boolean validate(int x, int y) {
      return this.validate(x, y, false);
   }

   private static String generateEmpty() {
      String colorChars = "abcdefghijpqrstuvwxyz0123456789";
      StringBuilder builder = new StringBuilder();

      for(int i = 0; i < 8; ++i) {
         builder.append('§').append(colorChars.charAt((new Random()).nextInt(colorChars.length())));
      }

      String s = builder.toString();
      if (emptyStrings.contains(s)) {
         return generateEmpty();
      } else {
         emptyStrings.add(s);
         return s;
      }
   }

   public void reset() {
      this.tabNames = this.is18 ? (String[])this.zeroValue18.clone() : (String[])this.zeroValue.clone();
      this.tabPings = this.is18 ? new int[WIDTH * HEIGHT + 20] : new int[WIDTH * HEIGHT];
   }

   public static TabLayout create(Player player) {
      if (tabLayouts.containsKey(player.getName())) {
         TabLayout layout = (TabLayout)tabLayouts.get(player.getName());
         layout.reset();
         return layout;
      } else {
         tabLayouts.put(player.getName(), new TabLayout(TabManager.is18(player)));
         return (TabLayout)tabLayouts.get(player.getName());
      }
   }

   public static void remove(Player player) {
      tabLayouts.remove(player.getName());
   }

   public static TabLayout createEmpty(Player player) {
      return TabManager.is18(player) ? getLayout18() : getLayoutDefault();
   }

   private static TabLayout getLayout18() {
      Object value = layout18.get();
      if (value == null) {
         synchronized(layout18) {
            value = layout18.get();
            if (value == null) {
               value = new TabLayout(true, true);
               layout18.set(value);
            }
         }
      }

      return (TabLayout)(value == layout18 ? null : value);
   }

   private static TabLayout getLayoutDefault() {
      Object value = layoutDefault.get();
      if (value == null) {
         synchronized(layoutDefault) {
            value = layoutDefault.get();
            if (value == null) {
               value = new TabLayout(false, true);
               layoutDefault.set(value);
            }
         }
      }

      return (TabLayout)(value == layoutDefault ? null : value);
   }

   public String[] getZeroValue() {
      return this.zeroValue;
   }

   public String[] getZeroValue18() {
      return this.zeroValue18;
   }

   public String[] getTabNames() {
      return this.tabNames;
   }

   public int[] getTabPings() {
      return this.tabPings;
   }

   public boolean is18() {
      return this.is18;
   }
}
