package io.github.manasmods.tensura.ability.magic.spiritual.earth;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.MagmaShotProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class MagmaSurgeMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.MagmaSurge CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).MagmaSurge;

   public MagmaSurgeMagic() {
      super(Element.EARTH, SpiritualMagic.SpiritLevel.GREATER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
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
            MagicCircleVariant.EARTH,
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
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);

            for (int i = 0; i < 4; i++) {
               this.shootMagmaBall(instance, entity, mode);
            }
         }
      }
   }

   private void shootMagmaBall(ManasSkillInstance instance, LivingEntity entity, int mode) {
      MagmaShotProjectile magmaBall = new MagmaShotProjectile(entity.level(), entity);
      magmaBall.setBurnTicks(10);
      magmaBall.setSpeed(1.0F);
      magmaBall.setDamage(CONFIG.magmaDamage);
      if (instance.isMastered(entity)) {
         MobEffectInstance burden = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.BURDEN), CONFIG.burdenDuration, CONFIG.burdenLevel - 1, true, false, true
         );
         magmaBall.setMobEffect(burden);
         magmaBall.setDamage(CONFIG.magmaDamageMastered);
      }

      magmaBall.setSkill(entity, instance, this, mode);
      magmaBall.setPosAndShoot(entity, 5.0F);
      magmaBall.setPos(
         entity.position().add(0.0, entity.getEyeHeight() - magmaBall.getBoundingBox().getYsize() * 0.5, 0.0).add(entity.getLookAngle().normalize())
      );
      Vec3 vector = entity.getViewVector(2.0F);
      magmaBall.shoot(vector.x(), vector.y(), vector.z(), magmaBall.getSpeed(), 5.0F);
      entity.level().addFreshEntity(magmaBall);
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }
}
