package io.github.manasmods.tensura.ability.magic.aspectual.gravity;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.GravitySphereProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class BurdenMagic extends AspectualMagic {
   public static final AspectualMagicConfig.Burden CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Burden;

   public BurdenMagic() {
      super(AspectualMagic.AspectualType.GRAVITY);
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

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            0.5F,
            25,
            MagicCircleVariant.GRAVITY,
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
         instance.addMasteryPoint(entity);
         GravitySphereProjectile sphere = new GravitySphereProjectile(entity.level(), entity);
         sphere.setSize(0.3F);
         sphere.setSpeed(1.25F);
         if (instance.isMastered(entity)) {
            sphere.setMobEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, CONFIG.slownessDuration, CONFIG.slownessLevel - 1, true, false, true));
         }

         sphere.setSkill(entity, instance, this, mode);
         sphere.setPiercingEntity(false);
         sphere.setPos(entity.getEyePosition().add(0.0, -0.25, 0.0).add(entity.getLookAngle().normalize()));
         sphere.setNoGravity(true);
         sphere.shootFromRot(entity.getLookAngle());
         entity.level().addFreshEntity(sphere);
         entity.swing(InteractionHand.MAIN_HAND, true);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }
}
