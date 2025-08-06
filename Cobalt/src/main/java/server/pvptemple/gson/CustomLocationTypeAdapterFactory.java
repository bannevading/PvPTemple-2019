package server.pvptemple.gson;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.beans.ConstructorProperties;
import java.io.IOException;

import lombok.Getter;
import server.pvptemple.CorePlugin;
import server.pvptemple.util.CustomLocation;

public class CustomLocationTypeAdapterFactory implements TypeAdapterFactory {
   public static LocationData serialize(CustomLocation customLocation) {
      Preconditions.checkNotNull(customLocation);
      return new LocationData(customLocation.getWorld(), customLocation.getX(), customLocation.getY(), customLocation.getZ(), customLocation.getYaw(), customLocation.getPitch());
   }

   public static CustomLocation deserialize(LocationData locationData) {
      Preconditions.checkNotNull(locationData);
      return new CustomLocation(locationData.getWorld(), locationData.getX(), locationData.getY(), locationData.getZ(), locationData.getYaw(), locationData.getPitch());
   }

   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      return !CustomLocation.class.isAssignableFrom(typeToken.getRawType()) ? null : new TypeAdapter<T>() {
         public void write(JsonWriter jsonWriter, T location) throws IOException {
            if (location == null) {
               jsonWriter.nullValue();
            } else {
               CorePlugin.GSON.toJson(CustomLocationTypeAdapterFactory.serialize((CustomLocation)location), LocationData.class, (JsonWriter)jsonWriter);
            }

         }

         public T read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == null) {
               jsonReader.nextNull();
               return null;
            } else {
               return (T)CustomLocationTypeAdapterFactory.deserialize((LocationData)CorePlugin.GSON.fromJson((JsonReader)jsonReader, LocationData.class));
            }
         }
      };
   }

   private static class LocationData {
      @Getter
      private final String world;
      @Getter
      private final double x;
      @Getter
      private final double y;
      @Getter
      private final double z;
      @Getter
      private final float yaw;
      @Getter
      private final float pitch;

       @ConstructorProperties({"world", "x", "y", "z", "yaw", "pitch"})
      public LocationData(String world, double x, double y, double z, float yaw, float pitch) {
         this.world = world;
         this.x = x;
         this.y = y;
         this.z = z;
         this.yaw = yaw;
         this.pitch = pitch;
      }
   }
}
