package io.github.manasmods.tensura.ability.magic.aspectual.space;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.SpaceCutProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class DimensionCutterMagic extends AspectualMagic {
   private static final AspectualMagicConfig.DimensionCutter CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).DimensionCutter;

   public DimensionCutterMagic() {
      super(AspectualMagic.AspectualType.SPACE);
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

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            1.0F,
            25,
            MagicCircleVariant.SPACE,
            entity,
            instance.getOrCreateTag(),
            0.75F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            this.shootDimensionCutter(instance, entity, mode);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         }
      }
   }

   private void shootDimensionCutter(ManasSkillInstance instance, LivingEntity entity, int mode) {
      SpaceCutProjectile blade = new SpaceCutProjectile(entity.level(), entity);
      blade.setSpeed(1.5F);
      blade.setVisible(true);
      blade.setPiercingEntity(true);
      blade.setPiercingBlock(true);
      blade.setDamage(instance.isMastered(entity) ? CONFIG.spatialDamageMastered : CONFIG.spatialDamage);
      blade.setSize(instance.isMastered(entity) ? CONFIG.sizeMastered : CONFIG.size);
      blade.setSkill(entity, instance, this, mode);
      blade.setNoGravity(true);
      blade.setPosDirection(entity, TensuraFlyingProjectile.PositionDirection.MIDDLE);
      blade.shootFromRot(entity.getLookAngle());
      entity.level().addFreshEntity(blade);
   }
}
