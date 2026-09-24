package io.github.manasmods.tensura.ability.magic.aspectual.lightning;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.lightning.LightningBolt;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ThunderMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Thunder CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Thunder;

   public ThunderMagic() {
      super(AspectualMagic.AspectualType.LIGHTNING);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryHigh;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
      Vec3 pos;
      if (target != null) {
         pos = target.position();
      } else {
         BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, CONFIG.range);
         pos = result.getLocation();
      }

      CompoundTag tag = instance.getOrCreateTag();
      tag.putDouble("circleX", pos.x);
      tag.putDouble("circleY", pos.y);
      tag.putDouble("circleZ", pos.z);
      instance.markDirty();
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      this.applyCastingVisual(instance, entity, heldTicks, mode);
      CompoundTag tag = instance.getOrCreateTag();
      Vec3 targetPos = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY") + 0.2F, tag.getDouble("circleZ"));
      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      MagicCircle.castMagicCircle(
         instance.isMastered(entity) ? CONFIG.blastRadiusMastered : CONFIG.blastRadius,
         30,
         targetPos,
         MagicCircleVariant.LIGHTNING,
         entity,
         instance.getOrCreateTag(),
         instance,
         mode,
         cost
      );
      return true;
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            CompoundTag tag = instance.getOrCreateTag();
            LightningBolt bolt = new LightningBolt(entity.level(), entity);
            bolt.setCause(entity instanceof ServerPlayer serverPlayer ? serverPlayer : null);
            bolt.setSkill(entity, instance, this, mode);
            bolt.setTensuraDamage(0.0F);
            bolt.setSecondaryDamage(instance.isMastered(entity) ? CONFIG.magicDamageMastered : CONFIG.magicDamage);
            bolt.setAdditionalVisual(instance.isMastered(entity) ? 2 : 1);
            bolt.setRadius(instance.isMastered(entity) ? CONFIG.blastRadiusMastered : CONFIG.blastRadius);
            bolt.setPos(new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY") + 0.2F, tag.getDouble("circleZ")));
            entity.level().addFreshEntity(bolt);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         }
      }
   }
}
