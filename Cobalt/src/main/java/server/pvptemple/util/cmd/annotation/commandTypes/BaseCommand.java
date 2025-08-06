package server.pvptemple.util.cmd.annotation.commandTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bukkit.permissions.PermissionDefault;
import server.pvptemple.rank.Rank;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseCommand {
   String[] name();

   Rank rank() default Rank.NORMAL;

   String description() default "";

   String permission() default "";

   PermissionDefault permissionDefault() default PermissionDefault.FALSE;

   boolean requiresOp() default false;
}
