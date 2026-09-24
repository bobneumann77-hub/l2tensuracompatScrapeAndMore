package io.github.manasmods.tensura.neoforge.handler;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.block.template.LooseBlock;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.handler.AttributeHandler;
import io.github.manasmods.tensura.race.RaceHelper;
import io.github.manasmods.tensura.race.merfolk.MerfolkRace;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import java.util.Optional;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingDrownEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed;

@EventBusSubscriber(modid = "tensura")
public class AbilityHandlers {
   @SubscribeEvent(priority = EventPriority.HIGHEST)
   private static void onEffectRemoved(Remove event) {
      if (event.getCure() == EffectCures.MILK && event.getEffect().is(TensuraTags.MobEffects.INCURABLE_BY_MILK)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   private static void onHeal(LivingHealEvent event) {
      LivingEntity entity = event.getEntity();
      if (SkillUtils.shouldCancelHealing(entity)) {
         event.setCanceled(true);
      } else {
         IEffect effect = TensuraStorages.getEffectFrom(entity);
         if (effect.getSeveranceAmount() > 0.0F) {
            float severedHealth = entity.getMaxHealth() - effect.getSeveranceAmount();
            if (entity.getHealth() == severedHealth) {
               event.setCanceled(Boolean.TRUE);
            } else if (entity.getHealth() + event.getAmount() > severedHealth) {
               if (entity.getHealth() > severedHealth) {
                  entity.hurt(TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.SEVERANCE), entity.getHealth() - severedHealth);
                  event.setCanceled(Boolean.TRUE);
               } else {
                  event.setAmount(severedHealth - entity.getHealth());
               }
            }
         }

         if (!event.isCanceled()) {
            RaceHelper.awakenHeroDuringFight(entity);
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   private static void onBreath(LivingBreatheEvent event) {
      LivingEntity entity = event.getEntity();
      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SHADOW_STEP)) && Alignment.shouldConsumeAir(entity)) {
         event.setCanBreathe(false);
         event.setConsumeAirAmount(0);
      } else {
         if (!event.canBreathe() && !Alignment.shouldConsumeAir(entity)) {
            event.setCanBreathe(true);
         }

         Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(entity).getRace();
         if (race.isPresent()) {
            if (!event.canBreathe() || !MerfolkRace.shouldLoseMoistness(entity, race.get()) && !LooseBlock.shouldLoseAir(entity)) {
               if (!event.canBreathe() && race.get().is(TensuraRaceTags.CAN_BREATH_WATER)) {
                  event.setCanBreathe(true);
               }
            } else {
               event.setCanBreathe(false);
            }
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   private static void onDrown(LivingDrownEvent event) {
      LivingEntity entity = event.getEntity();
      Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(entity).getRace();
      if (race.isPresent() && MerfolkRace.shouldLoseMoistness(entity, race.get())) {
         if (event.isDrowning()) {
            event.setDrowning(false);
            entity.setAirSupply(-1);
            entity.hurt(entity.damageSources().dryOut(), 1.0F);
         }
      } else if (LooseBlock.shouldLoseAir(entity) && event.isDrowning()) {
         event.setDrowning(false);
         entity.setAirSupply(-1);
         entity.hurt(TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.SUFFOCATE), 1.0F);
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   private static void onWakeUp(PlayerWakeUpEvent event) {
      if (!event.wakeImmediately() && !event.updateLevel()) {
         AttributeHandler.restoreMagiculeOnWakingUp(event.getEntity());
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   private static void onBreakSpeed(BreakSpeed event) {
      Player player = event.getEntity();
      if (!player.onGround()) {
         ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
         if (!boots.isEmpty()) {
            int level = TensuraEnchantmentHelper.getEnchantmentLevel(player.level(), TensuraEnchantments.STURDY, boots);
            if (level > 0) {
               event.setNewSpeed(event.getNewSpeed() * 5.0F * level);
            }
         }
      }
   }
}
