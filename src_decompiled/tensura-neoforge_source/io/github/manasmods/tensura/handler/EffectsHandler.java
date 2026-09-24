package io.github.manasmods.tensura.handler;

import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.extra.BodyDoubleSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.item.armor.custom.HolyArmamentsArmorItem;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class EffectsHandler {
   public static void init() {
      TensuraEntityEvents.ENTER_SLEEP_MODE_EVENT.register((TensuraEntityEvents.EnterSleepModeEvent)(entity, time, resetMagicule) -> {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(entity).getSkill((ManasSkill)ExtraSkills.BODY_DOUBLE.get());
         if (optional.isPresent()) {
            DamageSource source = TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.ENERGY_SOURCE_LOST).tensura$setBarrierBypassLevel(3.0F);
            if (BodyDoubleSkill.transferToMainBody(optional.get(), entity, source)) {
               return EventResult.interruptFalse();
            }
         }

         return EventResult.pass();
      });
      TensuraEntityEvents.DIMENSION_TRAVEL_EVENT.register((TensuraEntityEvents.DimensionTravelEvent)(target, teleporter, dimension) -> {
         if (target instanceof LivingEntity entity && SkillUtils.shouldCancelDimensionTravel(entity)) {
            if ((teleporter != null ? teleporter : target) instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
            }

            return EventResult.interruptFalse();
         } else if (teleporter instanceof LivingEntity entity && SkillUtils.shouldCancelDimensionTravel(entity)) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
            }

            return EventResult.interruptFalse();
         } else {
            HolyArmamentsArmorItem.updateFlight(target);
            return EventResult.pass();
         }
      });
      TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.register((TensuraEntityEvents.SpatialMovementEvent)(target, teleporter, position, type) -> {
         if (type.equals(WarpPoint.TransmissionType.INSTANT_MOVE) || type.equals(WarpPoint.TransmissionType.FORCE_EXIT)) {
            return EventResult.pass();
         }

         if (type.equals(WarpPoint.TransmissionType.COMMANDS)) {
            if (target instanceof LivingEntity entity && !entity.hasInfiniteMaterials() && !entity.getActiveEffects().isEmpty()) {
               MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.SPATIAL_BLOCKADE));
               if (instance != null && instance.getAmplifier() >= 9) {
                  entity.sendSystemMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED));
                  return EventResult.interruptFalse();
               }
            }

            return EventResult.pass();
         } else if (target instanceof LivingEntity entity && SkillUtils.shouldCancelTeleportation(entity)) {
            if ((teleporter != null ? teleporter : target) instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
            }

            return EventResult.interruptFalse();
         } else if (teleporter instanceof LivingEntity entity && SkillUtils.shouldCancelTeleportation(entity)) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
            }

            return EventResult.interruptFalse();
         } else {
            return EventResult.pass();
         }
      });
   }
}
