package server.pvptemple.command.impl;

import com.google.gson.JsonObject;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import server.pvptemple.CorePlugin;
import server.pvptemple.api.impl.RegisterRequest;
import server.pvptemple.mineman.Mineman;
import server.pvptemple.rank.Rank;
import server.pvptemple.util.cmd.CommandHandler;
import server.pvptemple.util.cmd.annotation.Param;
import server.pvptemple.util.cmd.annotation.commandTypes.Command;
import server.pvptemple.util.finalutil.CC;

public class RegisterCommand implements CommandHandler {
   @Command(
      name = {"register"},
      rank = Rank.NORMAL,
      description = "Register your account with our website."
   )
   public void register(Player player, @Param(name = "email") String emailAddress) {
      Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
      if (mineman != null) {
         if (emailAddress == null) {
            player.sendMessage(CC.RED + "You need to provide an e-mail address.");
         } else if (!this.isValidEmailAddress(emailAddress)) {
            player.sendMessage(CC.RED + "Please specify a valid email address.");
         } else {
            long current = System.currentTimeMillis();
            long last = mineman.getLastRegister();
            if (last != 0L && current < last + 120000L) {
               long timeLeft = last - current;
               player.sendMessage(CC.RED + "You must wait " + readableTime(timeLeft) + "before attempting to register again.");
            } else {
               mineman.setLastRegister(System.currentTimeMillis());
               String confirmationId = RandomStringUtils.randomAlphanumeric(16);
               RegisterRequest.InsertRequest request = new RegisterRequest.InsertRequest(player.getUniqueId(), confirmationId, emailAddress);
               CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(request, (data) -> {
                  JsonObject object = data.getAsJsonObject();
                  if (object.has("response")) {
                     String response = object.get("response").getAsString();
                     if (response.equalsIgnoreCase("success")) {
                        this.sendEmail(emailAddress, confirmationId);
                        player.sendMessage(CC.PRIMARY + "Please check your email at " + CC.SECONDARY + emailAddress + CC.PRIMARY + " to complete your registration.");
                     } else if (response.equalsIgnoreCase("already-registered")) {
                        player.sendMessage(CC.RED + "You have already registered. If you need assistance completing your registration, join our TeamSpeak.");
                     } else if (response.equalsIgnoreCase("player-not-found")) {
                        player.sendMessage(CC.RED + "Could not process your registration. Try re-logging.");
                     } else {
                        player.sendMessage(CC.RED + "Could not process your registration. Try again in a little bit.");
                     }
                  }

               });
            }

         }
      }
   }

   private static String readableTime(long time) {
      short second = 1000;
      int minute = 60 * second;
      int hour = 60 * minute;
      int day = 24 * hour;
      long ms = time;
      StringBuilder text = new StringBuilder("");
      if (time > (long)day) {
         text.append(time / (long)day).append(" days ");
         ms = time % (long)day;
      }

      if (ms > (long)hour) {
         text.append(ms / (long)hour).append(" hours ");
         ms %= (long)hour;
      }

      if (ms > (long)minute) {
         text.append(ms / (long)minute).append(" minutes ");
         ms %= (long)minute;
      }

      if (ms > (long)second) {
         text.append(ms / (long)second).append(" seconds ");
      }

      return text.toString();
   }

   private boolean isValidEmailAddress(String email) {
      Pattern p = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
      Matcher m = p.matcher(email);
      return m.matches();
   }

   private void sendEmail(final String email, final String confirmationId) {
      (new BukkitRunnable() {
         public void run() {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.port", "465");
            Session session = Session.getDefaultInstance(props, new Authenticator() {
               protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication("admin@pvptemple.com", "huilttsxlhgysmuj");
               }
            });

            try {
               MimeMessage message = new MimeMessage(session);
               message.setFrom((Address)(new InternetAddress("admin@pvptemple.com", "PvPTemple")));
               message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
               message.setSubject("PvPTemple Registration");
               message.setText("Thank you for registering, however, in order to complete your registration you must verify your email.\nPlease click on the following link to finish the registration process:\nhttps://www.pvptemple.com/confirm/" + confirmationId + "\n\nThanks,\nPvPTemple");
               Transport.send(message);
            } catch (UnsupportedEncodingException | MessagingException e) {
               ((Exception)e).printStackTrace();
            }

         }
      }).runTaskAsynchronously(CorePlugin.getInstance());
   }
}
