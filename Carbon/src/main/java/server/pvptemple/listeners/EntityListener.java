package server.pvptemple.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import server.pvptemple.Carbon;
import server.pvptemple.events.PracticeEvent;
import server.pvptemple.events.lms.LMSEvent;
import server.pvptemple.events.lms.LMSPlayer;
import server.pvptemple.events.oitc.OITCEvent;
import server.pvptemple.events.oitc.OITCPlayer;
import server.pvptemple.events.parkour.ParkourEvent;
import server.pvptemple.events.runner.RunnerEvent;
import server.pvptemple.events.sumo.SumoEvent;
import server.pvptemple.events.sumo.SumoPlayer;
import server.pvptemple.match.Match;
import server.pvptemple.match.MatchState;
import server.pvptemple.player.PlayerData;
import server.pvptemple.player.PlayerState;
import server.pvptemple.util.finalutil.CC;

public class EntityListener implements Listener {
   private final Carbon plugin = Carbon.getInstance();

   @EventHandler
   public void onEntityDamage(EntityDamageEvent e) {
      if (e.getEntity() instanceof Player) {
         Player player = (Player)e.getEntity();
         PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
         switch (playerData.getPlayerState()) {
            case FIGHTING:
               Match match = this.plugin.getMatchManager().getMatch(playerData);
               if (match.getMatchState() != MatchState.FIGHTING) {
                  e.setCancelled(true);
               }

               if (e.getCause() == DamageCause.VOID && !match.getKit().isBedwars()) {
                  this.plugin.getMatchManager().removeFighter(player, playerData, true);
               }
               break;
            case EVENT:
               PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);
               if (event instanceof SumoEvent) {
                  SumoEvent sumoEvent = (SumoEvent)event;
                  SumoPlayer sumoPlayer = (SumoPlayer)sumoEvent.getPlayer(player);
                  if (sumoPlayer != null && sumoPlayer.getState() != SumoPlayer.SumoState.FIGHTING) {
                     e.setCancelled(true);
                  }
               } else {
                  if (event instanceof RunnerEvent) {
                     e.setCancelled(true);
                     return;
                  }

                  if (event instanceof ParkourEvent) {
                     e.setCancelled(true);
                     return;
                  }

                  if (event instanceof OITCEvent) {
                     OITCEvent oitcEvent = (OITCEvent)event;
                     OITCPlayer oitcPlayer = (OITCPlayer)oitcEvent.getPlayer(player);
                     if (oitcPlayer != null && oitcPlayer.getState() == OITCPlayer.OITCState.FIGHTING && e.getCause() != DamageCause.FALL) {
                        e.setCancelled(false);
                     } else {
                        e.setCancelled(true);
                     }
                  } else if (event instanceof LMSEvent) {
                     LMSEvent sumoEvent = (LMSEvent)event;
                     LMSPlayer sumoPlayer = (LMSPlayer)sumoEvent.getPlayer(player);
                     if (sumoPlayer != null && sumoPlayer.getState() != LMSPlayer.LMSState.FIGHTING) {
                        e.setCancelled(true);
                     }
                  }
               }
               break;
            default:
               if (e.getCause() == DamageCause.VOID) {
                  e.getEntity().teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
               }

               if (playerData.getPlayerState() != PlayerState.EVENT) {
                  e.setCancelled(true);
               }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
      Player entity = (Player)e.getEntity();
      Player damager;
      if (e.getDamager() instanceof Player) {
         damager = (Player)e.getDamager();
      } else {
         if (!(e.getDamager() instanceof Projectile)) {
            return;
         }

         damager = (Player)((Projectile)e.getDamager()).getShooter();
      }

      PlayerData entityData = this.plugin.getPlayerManager().getPlayerData(entity.getUniqueId());
      PlayerData damagerData = this.plugin.getPlayerManager().getPlayerData(damager.getUniqueId());
      PracticeEvent event = this.plugin.getEventManager().getEventPlaying(damager);
      if (event != null) {
         if (event instanceof SumoEvent) {
            SumoEvent sumoEvent = (SumoEvent)event;
            if (((SumoPlayer)sumoEvent.getPlayer(damager)).getState() == SumoPlayer.SumoState.FIGHTING) {
               e.setCancelled(false);
               e.setDamage((double)0.0F);
               return;
            }
         } else {
            if (event instanceof RunnerEvent) {
               e.setCancelled(true);
               return;
            }

            if (event instanceof LMSEvent) {
               LMSEvent lmsEvent = (LMSEvent)event;
               if (((LMSPlayer)lmsEvent.getPlayer(damager)).getState() == LMSPlayer.LMSState.FIGHTING) {
                  e.setCancelled(false);
                  return;
               }
            } else {
               if (event instanceof ParkourEvent) {
                  e.setCancelled(true);
                  return;
               }

               if (event instanceof OITCEvent) {
                  OITCEvent oitcEvent = (OITCEvent)event;
                  OITCPlayer oitcKiller = (OITCPlayer)oitcEvent.getPlayer(damager);
                  OITCPlayer oitcPlayer = (OITCPlayer)oitcEvent.getPlayer(entity);
                  if (oitcKiller.getState() == OITCPlayer.OITCState.FIGHTING && oitcPlayer.getState() == OITCPlayer.OITCState.FIGHTING) {
                     if (e.getDamager() instanceof Arrow) {
                        Arrow arrow = (Arrow)e.getDamager();
                        if (arrow.getShooter() instanceof Player && damager != entity) {
                           oitcPlayer.setLastKiller(oitcKiller);
                           e.setDamage((double)0.0F);
                           event.onDeath().accept(entity);
                        }
                     }

                     return;
                  }

                  e.setCancelled(true);
                  return;
               }
            }
         }
      }

      if (damagerData.getPlayerState() == PlayerState.FIGHTING && entityData.getPlayerState() == PlayerState.FIGHTING) {
         Match match = this.plugin.getMatchManager().getMatch(entityData);
         if (damagerData.getTeamID() == entityData.getTeamID() && !match.isFFA()) {
            e.setCancelled(true);
         } else {
            if (match.getKit().isSpleef() || match.getKit().isSumo()) {
               e.setDamage((double)0.0F);
            }

            if (e.getDamager() instanceof Player) {
               damagerData.setCombo(damagerData.getCombo() + 1);
               damagerData.setHits(damagerData.getHits() + 1);
               if (damagerData.getCombo() > damagerData.getLongestCombo()) {
                  damagerData.setLongestCombo(damagerData.getCombo());
               }

               entityData.setCombo(0);
               if (match.getKit().isSpleef()) {
                  e.setCancelled(true);
               }
            } else if (e.getDamager() instanceof Arrow) {
               Arrow arrow = (Arrow)e.getDamager();
               if (arrow.getShooter() instanceof Player) {
                  Player shooter = (Player)arrow.getShooter();
                  if (!entity.getName().equals(shooter.getName())) {
                     double health = Math.ceil(entity.getHealth() - e.getFinalDamage()) / (double)2.0F;
                     if (health > (double)0.0F) {
                        shooter.sendMessage(CC.WHITE + entity.getName() + CC.YELLOW + " is now at " + CC.RED + health + CC.DARK_RED + "❤" + CC.YELLOW + ".");
                     }
                  }
               }
            }

         }
      } else {
         e.setCancelled(true);
      }
   }

   @EventHandler
   public void onPotionSplash(PotionSplashEvent e) {
      if (e.getEntity().getShooter() instanceof Player) {
         for(PotionEffect effect : e.getEntity().getEffects()) {
            if (effect.getType().equals(PotionEffectType.HEAL)) {
               Player shooter = (Player)e.getEntity().getShooter();
               if (e.getIntensity(shooter) <= (double)0.5F) {
                  PlayerData shooterData = this.plugin.getPlayerManager().getPlayerData(shooter.getUniqueId());
                  if (shooterData != null) {
                     shooterData.setMissedPots(shooterData.getMissedPots() + 1);
                  }
               }
               break;
            }
         }

      }
   }
}
