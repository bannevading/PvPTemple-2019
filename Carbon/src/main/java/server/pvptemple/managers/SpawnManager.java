package server.pvptemple.managers;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import server.pvptemple.Carbon;
import server.pvptemple.util.CustomLocation;

public class SpawnManager {
   private final Carbon plugin = Carbon.getInstance();
   private CustomLocation spawnLocation;
   private CustomLocation spawnMin;
   private CustomLocation cornersMin;
   private CustomLocation spawnMax;
   private CustomLocation cornersMax;
   private CustomLocation editorLocation;
   private CustomLocation editorMin;
   private CustomLocation editorMax;
   private CustomLocation sumoLocation;
   private CustomLocation lmsLocation;
   private CustomLocation parkourLocation;
   private CustomLocation parkourGameLocation;
   private CustomLocation oitcLocation;
   private CustomLocation cornersLocation;
   private CustomLocation sumoFirst;
   private CustomLocation sumoSecond;
   private List<CustomLocation> runnerLocations = new ArrayList();
   private List<CustomLocation> lmsLocations = new ArrayList();
   private List<CustomLocation> oitcLocations = new ArrayList();

   public SpawnManager() {
      this.loadConfig();
   }

   private void loadConfig() {
      FileConfiguration config = this.plugin.getMainConfig().getConfig();
      if (config.contains("spawnLocation")) {
         this.spawnLocation = CustomLocation.stringToLocation(config.getString("spawnLocation"));
         this.spawnMin = CustomLocation.stringToLocation(config.getString("spawnMin"));
         this.spawnMax = CustomLocation.stringToLocation(config.getString("spawnMax"));
         this.editorLocation = CustomLocation.stringToLocation(config.getString("editorLocation"));
         this.editorMin = CustomLocation.stringToLocation(config.getString("editorMin"));
         this.editorMax = CustomLocation.stringToLocation(config.getString("editorMax"));
         config.getStringList("runnerSpawnpoints").forEach((point) -> this.runnerLocations.add(CustomLocation.stringToLocation(point)));
         config.getStringList("lmsSpawnpoints").forEach((point) -> this.lmsLocations.add(CustomLocation.stringToLocation(point)));
         config.getStringList("oitcSpawnpoints").forEach((point) -> this.oitcLocations.add(CustomLocation.stringToLocation(point)));
      }

      if (config.contains("sumoLocation")) {
         this.sumoLocation = CustomLocation.stringToLocation(config.getString("sumoLocation"));
         this.sumoFirst = CustomLocation.stringToLocation(config.getString("sumoFirst"));
         this.sumoSecond = CustomLocation.stringToLocation(config.getString("sumoSecond"));
      }

      if (config.contains("lmsLocation")) {
         this.lmsLocation = CustomLocation.stringToLocation(config.getString("lmsLocation"));
      }

      if (config.contains("parkourLocation")) {
         this.parkourLocation = CustomLocation.stringToLocation(config.getString("parkourLocation"));
      }

      if (config.contains("parkourGameLocation")) {
         this.parkourGameLocation = CustomLocation.stringToLocation(config.getString("parkourGameLocation"));
      }

      if (config.contains("oitcLocation")) {
         this.oitcLocation = CustomLocation.stringToLocation(config.getString("oitcLocation"));
      }

      if (config.contains("cornersLocation")) {
         this.cornersLocation = CustomLocation.stringToLocation(config.getString("cornersLocation"));
         this.cornersMax = CustomLocation.stringToLocation(config.getString("cornersMax"));
         this.cornersMin = CustomLocation.stringToLocation(config.getString("cornersMin"));
      }

   }

   public void saveConfig() {
      FileConfiguration config = this.plugin.getMainConfig().getConfig();
      if (this.spawnLocation != null) {
         config.set("spawnLocation", CustomLocation.locationToString(this.spawnLocation));
      }

      if (this.spawnMin != null) {
         config.set("spawnMin", CustomLocation.locationToString(this.spawnMin));
      }

      if (this.spawnMax != null) {
         config.set("spawnMax", CustomLocation.locationToString(this.spawnMax));
      }

      if (this.editorLocation != null) {
         config.set("editorLocation", CustomLocation.locationToString(this.editorLocation));
      }

      if (this.editorMin != null) {
         config.set("editorMin", CustomLocation.locationToString(this.editorMin));
      }

      if (this.editorMax != null) {
         config.set("editorMax", CustomLocation.locationToString(this.editorMax));
      }

      if (this.runnerLocations.size() > 0) {
         config.set("runnerSpawnpoints", this.getLocations(this.runnerLocations));
      }

      if (this.sumoLocation != null) {
         config.set("sumoLocation", CustomLocation.locationToString(this.sumoLocation));
      }

      if (this.sumoFirst != null) {
         config.set("sumoFirst", CustomLocation.locationToString(this.sumoFirst));
      }

      if (this.sumoSecond != null) {
         config.set("sumoSecond", CustomLocation.locationToString(this.sumoSecond));
      }

      if (this.lmsLocation != null) {
         config.set("lmsLocation", CustomLocation.locationToString(this.lmsLocation));
      }

      if (this.lmsLocations.size() > 0) {
         config.set("lmsSpawnpoints", this.getLocations(this.lmsLocations));
      }

      if (this.parkourLocation != null) {
         config.set("parkourLocation", CustomLocation.locationToString(this.parkourLocation));
      }

      if (this.parkourGameLocation != null) {
         config.set("parkourGameLocation", CustomLocation.locationToString(this.parkourGameLocation));
      }

      if (this.oitcLocation != null) {
         config.set("oitcLocation", CustomLocation.locationToString(this.oitcLocation));
      }

      if (this.oitcLocations.size() > 0) {
         config.set("oitcSpawnpoints", this.getLocations(this.oitcLocations));
      }

      if (this.cornersLocation != null) {
         config.set("cornersLocation", CustomLocation.locationToString(this.cornersLocation));
      }

      if (this.cornersMax != null) {
         config.set("cornersMax", CustomLocation.locationToString(this.cornersMax));
      }

      if (this.cornersMin != null) {
         config.set("cornersMin", CustomLocation.locationToString(this.cornersMin));
      }

      this.plugin.getMainConfig().save();
   }

   private List<String> getLocations(List<CustomLocation> locations) {
      List<String> toReturn = new ArrayList();
      locations.forEach((location) -> toReturn.add(CustomLocation.locationToString(location)));
      return toReturn;
   }

   public Carbon getPlugin() {
      return this.plugin;
   }

   public CustomLocation getSpawnLocation() {
      return this.spawnLocation;
   }

   public CustomLocation getSpawnMin() {
      return this.spawnMin;
   }

   public CustomLocation getCornersMin() {
      return this.cornersMin;
   }

   public CustomLocation getSpawnMax() {
      return this.spawnMax;
   }

   public CustomLocation getCornersMax() {
      return this.cornersMax;
   }

   public CustomLocation getEditorLocation() {
      return this.editorLocation;
   }

   public CustomLocation getEditorMin() {
      return this.editorMin;
   }

   public CustomLocation getEditorMax() {
      return this.editorMax;
   }

   public CustomLocation getSumoLocation() {
      return this.sumoLocation;
   }

   public CustomLocation getLmsLocation() {
      return this.lmsLocation;
   }

   public CustomLocation getParkourLocation() {
      return this.parkourLocation;
   }

   public CustomLocation getParkourGameLocation() {
      return this.parkourGameLocation;
   }

   public CustomLocation getOitcLocation() {
      return this.oitcLocation;
   }

   public CustomLocation getCornersLocation() {
      return this.cornersLocation;
   }

   public CustomLocation getSumoFirst() {
      return this.sumoFirst;
   }

   public CustomLocation getSumoSecond() {
      return this.sumoSecond;
   }

   public List<CustomLocation> getRunnerLocations() {
      return this.runnerLocations;
   }

   public List<CustomLocation> getLmsLocations() {
      return this.lmsLocations;
   }

   public List<CustomLocation> getOitcLocations() {
      return this.oitcLocations;
   }

   public void setSpawnLocation(CustomLocation spawnLocation) {
      this.spawnLocation = spawnLocation;
   }

   public void setSpawnMin(CustomLocation spawnMin) {
      this.spawnMin = spawnMin;
   }

   public void setCornersMin(CustomLocation cornersMin) {
      this.cornersMin = cornersMin;
   }

   public void setSpawnMax(CustomLocation spawnMax) {
      this.spawnMax = spawnMax;
   }

   public void setCornersMax(CustomLocation cornersMax) {
      this.cornersMax = cornersMax;
   }

   public void setEditorLocation(CustomLocation editorLocation) {
      this.editorLocation = editorLocation;
   }

   public void setEditorMin(CustomLocation editorMin) {
      this.editorMin = editorMin;
   }

   public void setEditorMax(CustomLocation editorMax) {
      this.editorMax = editorMax;
   }

   public void setSumoLocation(CustomLocation sumoLocation) {
      this.sumoLocation = sumoLocation;
   }

   public void setLmsLocation(CustomLocation lmsLocation) {
      this.lmsLocation = lmsLocation;
   }

   public void setParkourLocation(CustomLocation parkourLocation) {
      this.parkourLocation = parkourLocation;
   }

   public void setParkourGameLocation(CustomLocation parkourGameLocation) {
      this.parkourGameLocation = parkourGameLocation;
   }

   public void setOitcLocation(CustomLocation oitcLocation) {
      this.oitcLocation = oitcLocation;
   }

   public void setCornersLocation(CustomLocation cornersLocation) {
      this.cornersLocation = cornersLocation;
   }

   public void setSumoFirst(CustomLocation sumoFirst) {
      this.sumoFirst = sumoFirst;
   }

   public void setSumoSecond(CustomLocation sumoSecond) {
      this.sumoSecond = sumoSecond;
   }

   public void setRunnerLocations(List<CustomLocation> runnerLocations) {
      this.runnerLocations = runnerLocations;
   }

   public void setLmsLocations(List<CustomLocation> lmsLocations) {
      this.lmsLocations = lmsLocations;
   }

   public void setOitcLocations(List<CustomLocation> oitcLocations) {
      this.oitcLocations = oitcLocations;
   }
}
