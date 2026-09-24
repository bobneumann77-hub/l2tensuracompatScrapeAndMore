package io.github.manasmods.tensura.ability.magic.aspectual.space;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.MagicUtils;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EscapeMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Escape CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Escape;

   public EscapeMagic() {
      super(AspectualMagic.AspectualType.SPACE);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   @Override
   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled() && instance.getOrCreateTag().contains("circleX");
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      CompoundTag tag = instance.getOrCreateTag();
      MagicCircle.castMagicCircle(
         entity.getBbWidth() * 2.0F,
         25,
         MagicCircleVariant.SPACE,
         tag.contains("circleX"),
         entity,
         tag,
         0.0F,
         Vec3.ZERO,
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
   }

   @Override
   public int getCastingTime(ManasSkillInstance instance, LivingEntity entity, int time, boolean chantAnnulment) {
      CompoundTag tag = instance.getOrCreateTag();
      if (tag.contains("circleX")) {
         return !entity.isShiftKeyDown() && !instance.isMastered(entity) ? Math.max(1, CONFIG.warpChargeTick) : 0;
      }

      if (chantAnnulment && this.isInstantCast(instance, entity)) {
         return 1;
      }

      int castTime = instance.getRemoveTime() == -3 ? (int)(time * MAGIC_CONFIG.unlearntCastMultiplier) : time;
      return MagicUtils.getChantTime(entity, castTime);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (SkillUtils.shouldCancelTeleportation(entity)) {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
         }

         entity.level()
            .playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
               TensuraSkill.ABILITY_SOUND,
               1.0F,
               1.0F
            );
      } else if (heldTicks >= this.getCastingTime(instance, entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         if (!tag.contains("circleX")) {
            if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               MagicCircle circle = MagicCircle.castMagicCircle(
                  entity.getBbWidth() * 2.0F,
                  25,
                  MagicCircleVariant.SPACE,
                  true,
                  entity,
                  tag,
                  0.0F,
                  Vec3.ZERO,
                  instance,
                  mode,
                  Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
               );
               circle.setFollowPosition(false);
               tag.putDouble("circleX", circle.getX());
               tag.putDouble("circleY", circle.getY());
               tag.putDouble("circleZ", circle.getZ());
               tag.putString("dimension", entity.level().dimension().location().toString());
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }
         } else if (entity.isShiftKeyDown()) {
            tag.remove("circleX");
            tag.remove("circleY");
            tag.remove("circleZ");
            tag.remove("dimension");
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
         } else if (!entity.level().dimension().location().toString().equals(tag.getString("dimension"))) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.teleport.warp_point.wrong_dimension").withStyle(ChatFormatting.RED), true);
            }

            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
         } else {
            Vec3 targetPos = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY"), tag.getDouble("circleZ"));
            double maxRange = instance.isMastered(entity) ? CONFIG.maxRangeMastered : CONFIG.maxRange;
            if (entity.distanceToSqr(targetPos) > maxRange * maxRange) {
               if (entity instanceof Player player) {
                  player.displayClientMessage(Component.translatable("tensura.skill.escape.too_far").withStyle(ChatFormatting.RED), true);
               }

               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            } else {
               Changeable<Vec3> position = Changeable.of(targetPos);
               if (!((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
                  .transmission(entity, null, position, WarpPoint.TransmissionType.ABILITY)
                  .isFalse()) {
                  double x = ((Vec3)position.get()).x();
                  double y = ((Vec3)position.get()).y();
                  double z = ((Vec3)position.get()).z();
                  if (entity.level().getWorldBorder().isWithinBounds(new BlockPos((int)x, (int)y, (int)z))) {
                     entity.unRide();
                     entity.resetFallDistance();
                     entity.teleportTo(x, y, z);
                     instance.addMasteryPoint(entity);
                     entity.swing(InteractionHand.MAIN_HAND, true);
                     instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
                     entity.level()
                        .playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                  }
               }

               tag.remove("circleX");
               tag.remove("circleY");
               tag.remove("circleZ");
               tag.remove("dimension");
            }
         }
      }
   }

   @Override
   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (this.shouldAutoEscape(entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         if (entity.level().dimension().location().toString().equals(tag.getString("dimension"))) {
            Vec3 targetPos = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY"), tag.getDouble("circleZ"));
            double maxRange = instance.isMastered(entity) ? CONFIG.maxRangeMastered : CONFIG.maxRange;
            if (!(entity.distanceToSqr(targetPos) > maxRange * maxRange)) {
               Changeable<Vec3> position = Changeable.of(targetPos);
               if (!((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
                  .transmission(entity, null, position, WarpPoint.TransmissionType.ABILITY)
                  .isFalse()) {
                  double x = ((Vec3)position.get()).x();
                  double y = ((Vec3)position.get()).y();
                  double z = ((Vec3)position.get()).z();
                  if (entity.level().getWorldBorder().isWithinBounds(new BlockPos((int)x, (int)y, (int)z))) {
                     entity.unRide();
                     entity.resetFallDistance();
                     entity.teleportTo(x, y, z);
                     entity.swing(InteractionHand.MAIN_HAND, true);
                     instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, 0);
                     entity.level()
                        .playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                  }
               }

               tag.remove("circleX");
               tag.remove("circleY");
               tag.remove("circleZ");
               tag.remove("dimension");
            }
         }
      }
   }

   private boolean shouldAutoEscape(LivingEntity entity) {
      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_MAGIC))) {
         return false;
      } else {
         return entity.getHealth() <= entity.getMaxHealth() * CONFIG.autoEscapeHP
            ? true
            : TensuraStorages.getExistenceFrom(entity).getMagicule() < EnergyHelper.getBaseMaxMagicule(entity) * CONFIG.autoEscapeMP;
      }
   }
}
