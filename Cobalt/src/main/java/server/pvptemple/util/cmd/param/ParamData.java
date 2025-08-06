package server.pvptemple.util.cmd.param;

import java.beans.ConstructorProperties;
import server.pvptemple.util.cmd.ParamType;

public class ParamData {
   private String name;
   private String defaultValue;
   private ParamType type;

   public String getName() {
      return this.name;
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }

   public ParamType getType() {
      return this.type;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   public void setType(ParamType type) {
      this.type = type;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ParamData)) {
         return false;
      } else {
         ParamData other = (ParamData)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null) {
               if (other$name != null) {
                  return false;
               }
            } else if (!this$name.equals(other$name)) {
               return false;
            }

            Object this$defaultValue = this.getDefaultValue();
            Object other$defaultValue = other.getDefaultValue();
            if (this$defaultValue == null) {
               if (other$defaultValue != null) {
                  return false;
               }
            } else if (!this$defaultValue.equals(other$defaultValue)) {
               return false;
            }

            Object this$type = this.getType();
            Object other$type = other.getType();
            if (this$type == null) {
               if (other$type != null) {
                  return false;
               }
            } else if (!this$type.equals(other$type)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof ParamData;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $defaultValue = this.getDefaultValue();
      result = result * 59 + ($defaultValue == null ? 43 : $defaultValue.hashCode());
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      return result;
   }

   public String toString() {
      return "ParamData(name=" + this.getName() + ", defaultValue=" + this.getDefaultValue() + ", type=" + this.getType() + ")";
   }

   @ConstructorProperties({"name", "defaultValue", "type"})
   public ParamData(String name, String defaultValue, ParamType type) {
      this.name = name;
      this.defaultValue = defaultValue;
      this.type = type;
   }
}
