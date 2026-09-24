package com.github.b4ndithelps.l2tensura.handler;

import com.github.b4ndithelps.l2tensura.config.EPConfig;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "l2tensura", bus = Bus.FORGE)
public class SimpleEPHandler {
   private static final Map<LivingEntity, Double> pendingEPUpdates = new HashMap<>();
   private static final Map<LivingEntity, Boolean> processedEntities = new HashMap<>();
   private static final Map<LivingEntity, SimpleEPHandler.EntityState> originalStates = new HashMap<>();

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
      if (!event.getLevel().m_5776_()) {
         if (event.getEntity() instanceof LivingEntity livingEntity) {
            if (!(livingEntity instanceof Player)) {
               try {
                  if (MobTraitCap.HOLDER.isProper(livingEntity)) {
                     MobTraitCap mobCap = (MobTraitCap)MobTraitCap.HOLDER.get(livingEntity);
                     int hostilityLevel = mobCap.getLevel();
                     if (hostilityLevel > 0) {
                        if (isAlreadyProcessedByUs(livingEntity)) {
                           if (EPConfig.isDebugLoggingEnabled()) {
                              System.out.println(String.format("[L2Tensura] %s already processed by L2Tensura, skipping", livingEntity.m_6095_().m_20675_()));
                           }

                           return;
                        }

                        originalStates.put(livingEntity, new SimpleEPHandler.EntityState(livingEntity));
                        double epMultiplier = EPConfig.calculateEPMultiplier(hostilityLevel);
                        double baseEP = TensuraEPCapability.getEP(livingEntity);
                        if (baseEP <= 0.0) {
                           baseEP = EPConfig.calculateBaseEP(livingEntity.m_21233_());
                        }

                        double enhancedEP = baseEP * epMultiplier;
                        pendingEPUpdates.put(livingEntity, enhancedEP);
                     }
                  }
               } catch (Exception e) {
                  System.err.println("[L2Tensura] Error setting EP for entity: " + e.getMessage());
                  e.printStackTrace();
               }
            }
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void onLivingTick(LivingTickEvent event) {
      LivingEntity livingEntity = event.getEntity();
      if (!livingEntity.f_19853_.f_46443_) {
         if (!(livingEntity instanceof Player)) {
            if (!processedEntities.containsKey(livingEntity)) {
               try {
                  if (MobTraitCap.HOLDER.isProper(livingEntity)) {
                     MobTraitCap mobCap = (MobTraitCap)MobTraitCap.HOLDER.get(livingEntity);
                     int hostilityLevel = mobCap.getLevel();
                     if (hostilityLevel > 0) {
                        double currentEP = TensuraEPCapability.getEP(livingEntity);
                        if (currentEP > 0.0) {
                           if (isAlreadyProcessedByUs(livingEntity)) {
                              processedEntities.put(livingEntity, true);
                              if (EPConfig.isDebugLoggingEnabled()) {
                                 System.out
                                    .println(
                                       String.format("[L2Tensura] %s already processed by L2Tensura in LivingTick, skipping", livingEntity.m_6095_().m_20675_())
                                    );
                              }

                              return;
                           }

                           if (!isEntityStateValid(livingEntity)) {
                              processedEntities.put(livingEntity, true);
                              return;
                           }

                           double epMultiplier = EPConfig.calculateEPMultiplier(hostilityLevel);
                           double baseEP = currentEP;
                           double finalEP = baseEP * epMultiplier;
                           setEPInstantly(livingEntity, finalEP);
                           restoreHealthSafely(livingEntity);
                           markAsProcessedByUs(livingEntity);
                           processedEntities.put(livingEntity, true);
                        }
                     }
                  }
               } catch (Exception e) {
                  System.err.println("[L2Tensura] Error in LivingTick EP enhancement: " + e.getMessage());
                  e.printStackTrace();
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END && !pendingEPUpdates.isEmpty()) {
         cleanupDeadEntities();
         pendingEPUpdates.entrySet().removeIf(entry -> {
            LivingEntity entity = entry.getKey();
            Double enhancedEP = entry.getValue();

            try {
               if (entity.m_213877_() || !entity.m_6084_() || !isEntityStateValid(entity)) {
                  return true;
               }

               if (isAlreadyProcessedByUs(entity)) {
                  if (EPConfig.isDebugLoggingEnabled()) {
                     System.out.println(String.format("[L2Tensura] %s already processed by L2Tensura in ServerTick, skipping", entity.m_6095_().m_20675_()));
                  }

                  return true;
               } else {
                  setEPInstantly(entity, enhancedEP);
                  restoreHealthSafely(entity);
                  markAsProcessedByUs(entity);
                  return true;
               }
            } catch (Exception e) {
               System.err.println("[L2Tensura] Error processing delayed EP update: " + e.getMessage());
               return true;
            }
         });
      }
   }

   private static boolean isAlreadyProcessedByUs(LivingEntity entity) {
      try {
         CompoundTag persistentData = entity.getPersistentData();
         if (persistentData.m_128441_("L2TensuraProcessed")) {
            boolean processed = persistentData.m_128471_("L2TensuraProcessed");
            if (processed && EPConfig.isDebugLoggingEnabled()) {
               System.out.println(String.format("[L2Tensura] %s marked as processed in persistent data", entity.m_6095_().m_20675_()));
            }

            return processed;
         } else {
            return false;
         }
      } catch (Exception e) {
         if (EPConfig.isDebugLoggingEnabled()) {
            System.err.println("[L2Tensura] Error checking if entity processed: " + e.getMessage());
         }

         return false;
      }
   }

   private static void markAsProcessedByUs(LivingEntity entity) {
      try {
         CompoundTag persistentData = entity.getPersistentData();
         persistentData.m_128379_("L2TensuraProcessed", true);
         persistentData.m_128356_("L2TensuraProcessTime", System.currentTimeMillis());
         persistentData.m_128359_("L2TensuraVersion", "1.0");
         double currentEP = TensuraEPCapability.getEP(entity);
         if (MobTraitCap.HOLDER.isProper(entity)) {
            MobTraitCap mobCap = (MobTraitCap)MobTraitCap.HOLDER.get(entity);
            int hostilityLevel = mobCap.getLevel();
            persistentData.m_128347_("L2TensuraEnhancedEP", currentEP);
            persistentData.m_128405_("L2TensuraL2Level", hostilityLevel);
         }

         if (EPConfig.isDebugLoggingEnabled()) {
            System.out.println(String.format("[L2Tensura] Marked %s as processed (EP: %.1f)", entity.m_6095_().m_20675_(), currentEP));
         }
      } catch (Exception e) {
         System.err.println("[L2Tensura] Error marking entity as processed: " + e.getMessage());
         e.printStackTrace();
      }
   }

   private static void cleanupDeadEntities() {
      processedEntities.entrySet().removeIf(entry -> {
         LivingEntity entity = entry.getKey();
         return entity.m_213877_() || !entity.m_6084_();
      });
      originalStates.entrySet().removeIf(entry -> {
         LivingEntity entity = entry.getKey();
         return entity.m_213877_() || !entity.m_6084_();
      });
   }

   private static boolean isEntityStateValid(LivingEntity entity) {
      try {
         if (!entity.m_213877_() && entity.m_6084_()) {
            float health = entity.m_21223_();
            float maxHealth = entity.m_21233_();
            return health <= 0.0F || maxHealth <= 0.0F || Float.isNaN(health) || Float.isNaN(maxHealth) ? false : entity.f_20919_ <= 0;
         } else {
            return false;
         }
      } catch (Exception e) {
         return false;
      }
   }

   private static boolean safelyEnhanceEntity(LivingEntity entity, double targetEP) {
      try {
         if (!isEntityStateValid(entity)) {
            return false;
         } else {
            float currentHealth = entity.m_21223_();
            float currentMaxHealth = entity.m_21233_();
            boolean epSetSuccess = safelySetEP(entity, targetEP);
            if (!epSetSuccess) {
               return false;
            } else if (!isEntityStateValid(entity)) {
               restoreEntityState(entity);
               return false;
            } else {
               safelyHealToFullHealth(entity);
               return isEntityStateValid(entity);
            }
         }
      } catch (Exception e) {
         System.err.println("[L2Tensura] Error in safelyEnhanceEntity: " + e.getMessage());
         restoreEntityState(entity);
         return false;
      }
   }

   private static void setEPInstantly(LivingEntity entity, double ep) {
      try {
         if (!isEntityStateValid(entity)) {
            return;
         }

         TensuraEPCapability.setLivingEP(entity, ep);
         CompoundTag entityNBT = new CompoundTag();
         entity.m_20240_(entityNBT);
         if (!entityNBT.m_128441_("ForgeCaps")) {
            entityNBT.m_128365_("ForgeCaps", new CompoundTag());
         }

         CompoundTag forgeCaps = entityNBT.m_128469_("ForgeCaps");
         if (!forgeCaps.m_128441_("tensura:ep")) {
            forgeCaps.m_128365_("tensura:ep", new CompoundTag());
         }

         CompoundTag tensuraEP = forgeCaps.m_128469_("tensura:ep");
         tensuraEP.m_128347_("EP", ep);
         tensuraEP.m_128347_("currentEP", ep);
         tensuraEP.m_128347_("gainedEP", 0.0);
         entity.m_20258_(entityNBT);
         TensuraEPCapability.getFrom(entity).ifPresent(cap -> {
            try {
               cap.setEP(entity, ep, true);
               cap.setCurrentEP(entity, ep);
            } catch (Exception var5x) {
            }
         });
         if (EPConfig.isDebugLoggingEnabled()) {
            double verifyEP = TensuraEPCapability.getEP(entity);
            System.out.println(String.format("[L2Tensura] Instantly set %s EP: %.1f (Verified: %.1f)", entity.m_6095_().m_20675_(), ep, verifyEP));
         }
      } catch (Exception e) {
         System.err.println("[L2Tensura] Error setting EP instantly: " + e.getMessage());
         e.printStackTrace();
      }
   }

   private static boolean safelySetEP(LivingEntity entity, double ep) {
      try {
         TensuraEPCapability.setLivingEP(entity, ep);
         double verifyEP = TensuraEPCapability.getEP(entity);
         return Math.abs(verifyEP - ep) < 0.1 ? true : TensuraEPCapability.getFrom(entity).map(cap -> {
            try {
               cap.setEP(entity, ep, true);
               cap.setCurrentEP(entity, ep);
               return true;
            } catch (Exception e) {
               return false;
            }
         }).orElse(false);
      } catch (Exception e) {
         return false;
      }
   }

   private static void restoreEntityState(LivingEntity entity) {
      try {
         SimpleEPHandler.EntityState originalState = originalStates.get(entity);
         if (originalState != null && originalState.wasAlive) {
            entity.m_21153_(Math.min(originalState.originalHealth, entity.m_21233_()));
         }
      } catch (Exception var2) {
      }
   }

   @Deprecated
   private static void setEPDirectly(LivingEntity entity, double ep) {
      try {
         TensuraEPCapability.setLivingEP(entity, ep);
         CompoundTag entityNBT = new CompoundTag();
         entity.m_20240_(entityNBT);
         if (!entityNBT.m_128441_("ForgeCaps")) {
            entityNBT.m_128365_("ForgeCaps", new CompoundTag());
         }

         CompoundTag forgeCaps = entityNBT.m_128469_("ForgeCaps");
         if (!forgeCaps.m_128441_("tensura:ep")) {
            forgeCaps.m_128365_("tensura:ep", new CompoundTag());
         }

         CompoundTag tensuraEP = forgeCaps.m_128469_("tensura:ep");
         tensuraEP.m_128347_("EP", ep);
         tensuraEP.m_128347_("currentEP", ep);
         tensuraEP.m_128347_("gainedEP", 0.0);
         entity.m_20258_(entityNBT);
         TensuraEPCapability.getFrom(entity).ifPresent(cap -> {
            try {
               cap.setEP(entity, ep, true);
               cap.setCurrentEP(entity, ep);
            } catch (Exception var5x) {
            }
         });
      } catch (Exception e) {
         System.err.println("[L2Tensura] Error setting EP directly: " + e.getMessage());
         e.printStackTrace();
      }
   }

   private static void restoreHealthSafely(LivingEntity entity) {
      try {
         if (!isEntityStateValid(entity)) {
            return;
         }

         SimpleEPHandler.EntityState originalState = originalStates.get(entity);
         if (originalState == null) {
            if (EPConfig.isDebugLoggingEnabled()) {
               System.out.println(String.format("[L2Tensura] No original state for %s, skipping health adjustment", entity.m_6095_().m_20675_()));
            }

            return;
         }

         float expectedHealth = calculateExpectedHealth(entity, originalState);
         float currentHealth = entity.m_21223_();
         float currentMaxHealth = entity.m_21233_();
         float healthDifference = Math.abs(currentHealth - expectedHealth);
         float tolerance = Math.max(1.0F, currentMaxHealth * 0.01F);
         if (healthDifference > tolerance) {
            entity.m_21153_(expectedHealth);
            if (EPConfig.isDebugLoggingEnabled()) {
               System.out
                  .println(
                     String.format(
                        "[L2Tensura] Adjusted %s health: %.1f -> %.1f (Expected: %.1f, Max: %.1f)",
                        entity.m_6095_().m_20675_(),
                        currentHealth,
                        expectedHealth,
                        expectedHealth,
                        currentMaxHealth
                     )
                  );
            }
         } else if (EPConfig.isDebugLoggingEnabled()) {
            System.out
               .println(
                  String.format(
                     "[L2Tensura] %s health already correct: %.1f/%.1f (Expected: %.1f)",
                     entity.m_6095_().m_20675_(),
                     currentHealth,
                     currentMaxHealth,
                     expectedHealth
                  )
               );
         }

         if (!isEntityStateValid(entity)) {
            entity.m_21153_(Math.min(originalState.originalHealth, entity.m_21233_()));
            if (EPConfig.isDebugLoggingEnabled()) {
               System.out
                  .println(
                     String.format("[L2Tensura] Health adjustment caused invalid state, reverted %s to %.1f", entity.m_6095_().m_20675_(), entity.m_21223_())
                  );
            }
         }
      } catch (Exception e) {
         System.err.println("[L2Tensura] Error in smart health restore: " + e.getMessage());
         e.printStackTrace();
      }
   }

   private static float calculateExpectedHealth(LivingEntity entity, SimpleEPHandler.EntityState originalState) {
      try {
         float currentMaxHealth = entity.m_21233_();
         float originalMaxHealth = originalState.originalMaxHealth;
         float originalHealth = originalState.originalHealth;
         if (Math.abs(currentMaxHealth - originalMaxHealth) < 0.1F) {
            return originalHealth;
         }

         float healthGrowthRatio = currentMaxHealth / originalMaxHealth;
         float originalHealthRatio = originalHealth / originalMaxHealth;
         if (originalHealthRatio >= 0.99F) {
            return currentMaxHealth;
         }

         if (originalHealthRatio >= 0.95F) {
            return currentMaxHealth;
         }

         float expectedByRatio = currentMaxHealth * originalHealthRatio;
         float expectedByGrowth = originalHealth * healthGrowthRatio;
         if (healthGrowthRatio < 2.0F) {
            return expectedByRatio;
         }

         float maxReasonableHealth = originalHealth * 2.0F;
         return Math.min(expectedByGrowth, Math.min(maxReasonableHealth, currentMaxHealth));
      } catch (Exception e) {
         return entity.m_21223_();
      }
   }

   private static void safelyHealToFullHealth(LivingEntity entity) {
      try {
         if (!isEntityStateValid(entity)) {
            return;
         }

         float maxHealth = entity.m_21233_();
         if (maxHealth <= 0.0F || Float.isNaN(maxHealth)) {
            return;
         }

         entity.m_21153_(maxHealth);
         if (!isEntityStateValid(entity)) {
            restoreEntityState(entity);
            return;
         }

         if (EPConfig.isDebugLoggingEnabled()) {
            System.out
               .println(String.format("[L2Tensura] Safely healed %s to full health: %.1f/%.1f", entity.m_6095_().m_20675_(), entity.m_21223_(), maxHealth));
         }
      } catch (Exception e) {
         System.err.println("[L2Tensura] Error safely healing entity to full health: " + e.getMessage());
         restoreEntityState(entity);
      }
   }

   @Deprecated
   private static void healToFullHealth(LivingEntity entity) {
      safelyHealToFullHealth(entity);
   }

   private static class EntityState {
      final float originalHealth;
      final float originalMaxHealth;
      final boolean wasAlive;

      EntityState(LivingEntity entity) {
         this.originalHealth = entity.m_21223_();
         this.originalMaxHealth = entity.m_21233_();
         this.wasAlive = entity.m_6084_();
      }
   }
}
