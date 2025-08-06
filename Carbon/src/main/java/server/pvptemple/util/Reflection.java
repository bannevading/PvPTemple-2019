package server.pvptemple.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

public final class Reflection {
   private static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
   private static String NMS_PREFIX;
   private static String VERSION;
   private static Pattern MATCH_VARIABLE;

   private Reflection() {
   }

   public static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) {
      return getField(target, name, fieldType, 0);
   }

   public static <T> FieldAccessor<T> getField(String className, String name, Class<T> fieldType) {
      return getField(getClass(className), name, fieldType, 0);
   }

   public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
      return getField(target, (String)null, fieldType, index);
   }

   public static <T> FieldAccessor<T> getField(String className, Class<T> fieldType, int index) {
      return getField(getClass(className), fieldType, index);
   }

   private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
      for(final Field field : target.getDeclaredFields()) {
         if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
            field.setAccessible(true);
            return new FieldAccessor<T>() {
               public T get(Object target) {
                  try {
                     return (T)field.get(target);
                  } catch (IllegalAccessException e) {
                     throw new RuntimeException("Cannot access reflection.", e);
                  }
               }

               public void set(Object target, Object value) {
                  try {
                     field.set(target, value);
                  } catch (IllegalAccessException e) {
                     throw new RuntimeException("Cannot access reflection.", e);
                  }
               }

               public boolean hasField(Object target) {
                  return field.getDeclaringClass().isAssignableFrom(target.getClass());
               }
            };
         }
      }

      if (target.getSuperclass() != null) {
         return getField(target.getSuperclass(), name, fieldType, index);
      } else {
         throw new IllegalArgumentException("Cannot find field with type " + fieldType);
      }
   }

   public static MethodInvoker getMethod(String className, String methodName, Class<?>... params) {
      return getTypedMethod(getClass(className), methodName, (Class)null, params);
   }

   public static MethodInvoker getMethod(Class<?> clazz, String methodName, Class<?>... params) {
      return getTypedMethod(clazz, methodName, (Class)null, params);
   }

   public static MethodInvoker getTypedMethod(Class<?> clazz, String methodName, Class<?> returnType, Class<?>... params) {
      for(final Method method : clazz.getDeclaredMethods()) {
         if ((methodName == null || method.getName().equals(methodName)) && (returnType == null || method.getReturnType().equals(returnType)) && Arrays.equals(method.getParameterTypes(), params)) {
            method.setAccessible(true);
            return new MethodInvoker() {
               public Object invoke(Object target, Object... arguments) {
                  try {
                     return method.invoke(target, arguments);
                  } catch (Exception e) {
                     throw new RuntimeException("Cannot invoke method " + method, e);
                  }
               }
            };
         }
      }

      if (clazz.getSuperclass() != null) {
         return getMethod(clazz.getSuperclass(), methodName, params);
      } else {
         throw new IllegalStateException(String.format("Unable to find method %s (%s).", methodName, Arrays.asList(params)));
      }
   }

   public static ConstructorInvoker getConstructor(String className, Class<?>... params) {
      return getConstructor(getClass(className), params);
   }

   public static ConstructorInvoker getConstructor(Class<?> clazz, Class<?>... params) {
      for(final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
         if (Arrays.equals(constructor.getParameterTypes(), params)) {
            constructor.setAccessible(true);
            return new ConstructorInvoker() {
               public Object invoke(Object... arguments) {
                  try {
                     return constructor.newInstance(arguments);
                  } catch (Exception e) {
                     throw new RuntimeException("Cannot invoke constructor " + constructor, e);
                  }
               }
            };
         }
      }

      throw new IllegalStateException(String.format("Unable to find constructor for %s (%s).", clazz, Arrays.asList(params)));
   }

   public static Class<Object> getUntypedClass(String lookupName) {
      Class<Object> clazz = (Class<Object>) getClass(lookupName);
      return clazz;
   }

   public static Class<?> getClass(String lookupName) {
      return getCanonicalClass(expandVariables(lookupName));
   }

   public static Class<?> getMinecraftClass(String name) {
      return getCanonicalClass(NMS_PREFIX + "." + name);
   }

   public static Class<?> getCraftBukkitClass(String name) {
      return getCanonicalClass(OBC_PREFIX + "." + name);
   }

   private static Class<?> getCanonicalClass(String canonicalName) {
      try {
         return Class.forName(canonicalName);
      } catch (ClassNotFoundException e) {
         throw new IllegalArgumentException("Cannot find " + canonicalName, e);
      }
   }

   private static String expandVariables(String name) {
      StringBuffer output = new StringBuffer();

      Matcher matcher;
      String replacement;
      for(matcher = MATCH_VARIABLE.matcher(name); matcher.find(); matcher.appendReplacement(output, Matcher.quoteReplacement(replacement))) {
         String variable = matcher.group(1);
         replacement = "";
         if ("nms".equalsIgnoreCase(variable)) {
            replacement = NMS_PREFIX;
         } else if ("obc".equalsIgnoreCase(variable)) {
            replacement = OBC_PREFIX;
         } else {
            if (!"version".equalsIgnoreCase(variable)) {
               throw new IllegalArgumentException("Unknown variable: " + variable);
            }

            replacement = VERSION;
         }

         if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.') {
            replacement = replacement + ".";
         }
      }

      matcher.appendTail(output);
      return output.toString();
   }

   static {
      NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
      VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");
      MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");
   }

   public interface ConstructorInvoker {
      Object invoke(Object... var1);
   }

   public interface FieldAccessor<T> {
      T get(Object var1);

      void set(Object var1, Object var2);

      boolean hasField(Object var1);
   }

   public interface MethodInvoker {
      Object invoke(Object var1, Object... var2);
   }
}
