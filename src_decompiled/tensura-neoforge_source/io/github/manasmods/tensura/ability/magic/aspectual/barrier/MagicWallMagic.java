package io.github.manasmods.tensura.ability.magic.aspectual.barrier;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.shield.MagicShieldEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class MagicWallMagic extends AspectualMagic {
   private static final AspectualMagicConfig.MagicWall CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).MagicWall;

   public MagicWallMagic() {
      super(AspectualMagic.AspectualType.BARRIER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
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
            MagicCircleVariant.BARRIER,
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
            MagicShieldEntity shield = new MagicShieldEntity(entity.level(), entity);
            shield.setLife(instance.isMastered(entity) ? CONFIG.wallDurationMastered : CONFIG.wallDuration);
            shield.setHealth(entity.getHealth());
            shield.setContactInterval(0);
            shield.setSize(CONFIG.width);
            shield.setContactSpeedMultiplier(CONFIG.speedMultiplier);
            shield.setProjectileMagicReduction(instance.isMastered(entity) ? CONFIG.magicProjectileReductionMastered : CONFIG.magicProjectileReduction);
            shield.setDistanceFromOwner(0.0F);
            shield.setSkill(entity, instance, this, 0);
            shield.setPos(entity.getEyePosition().add(entity.getLookAngle().normalize().scale(CONFIG.distance)));
            entity.level().addFreshEntity(shield);
            Vec2 view = ObjectSelectionHelper.getRotFromVector(entity.getLookAngle());
            shield.setRot(view.y, view.x);
            instance.addMasteryPoint(entity);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      }
   }
}
