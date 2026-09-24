package io.github.manasmods.tensura.ability.magic.aspectual.gravity;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.FloatSphereProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.Objects;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FloatMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Float CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Float;

   public FloatMagic() {
      super(AspectualMagic.AspectualType.GRAVITY);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? (instance.isMastered(entity) ? 1 : -1) : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return mode == 1 ? "float.projectile" : super.getModeId(instance, mode);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         if (mode == 1) {
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
         } else {
            MagicCircle.castMagicCircle(
               entity.getBbWidth() * 2.0F,
               25,
               MagicCircleVariant.GRAVITY,
               true,
               entity,
               instance.getOrCreateTag(),
               0.0F,
               new Vec3(0.0, entity.getBbHeight() / 2.0F, 0.0),
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (mode == 0) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            MobEffectInstance levitation = new MobEffectInstance(
               MobEffects.LEVITATION, CONFIG.levitationDuration, CONFIG.levitationLevel - 1, true, false, true
            );
            TensuraMobEffect.addEffect(entity, levitation, entity, this);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            FloatSphereProjectile sphere = new FloatSphereProjectile(entity.level(), entity);
            sphere.setSpeed(1.0F);
            sphere.setDamage(CONFIG.projectileDamage);
            sphere.setMobEffect(new MobEffectInstance(MobEffects.LEVITATION, CONFIG.projectileDuration, CONFIG.projectileLevel - 1, true, false, true));
            sphere.setSkill(entity, instance, this, mode);
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

   @Override
   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      if (!(instance.getMastery() < 0.0) && !instance.isMastered(entity)) {
         MobEffectInstance levitation = entity.getEffect(MobEffects.LEVITATION);
         if (levitation == null) {
            return false;
         } else {
            return !Objects.equals(levitation.tensura$getSource(), entity.getUUID())
               ? false
               : levitation.tensura$getSourceAbility() != null && levitation.tensura$getSourceAbility().getSkill() == this;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      instance.addMasteryPoint(entity);
   }
}
