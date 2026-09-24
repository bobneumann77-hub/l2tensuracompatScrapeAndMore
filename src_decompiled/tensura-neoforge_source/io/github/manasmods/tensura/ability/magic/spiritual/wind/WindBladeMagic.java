package io.github.manasmods.tensura.ability.magic.spiritual.wind;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.MagicUtils;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.WindBladeProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class WindBladeMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.WindBlade CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).WindBlade;

   public WindBladeMagic() {
      super(Element.WIND, SpiritualMagic.SpiritLevel.MEDIUM);
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

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.isMastered(entity);
   }

   @Override
   public boolean isInstantCast(ManasSkillInstance instance, LivingEntity entity) {
      if (instance.isMastered(entity)) {
         return instance.onCoolDown(0) ? true : MagicUtils.hasChantAnnulment(entity);
      } else {
         return false;
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            1.0F,
            25,
            MagicCircleVariant.WIND,
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
            WindBladeProjectile blade = new WindBladeProjectile(entity.level(), entity);
            blade.setSpeed(1.5F);
            blade.setDamage(CONFIG.damage);
            blade.setKnockForce(2.0F);
            blade.setSkill(entity, instance, this, mode);
            blade.setNoGravity(true);
            blade.setPos(entity.getEyePosition().add(0.0, -0.25, 0.0).add(entity.getLookAngle().normalize()));
            blade.shootFromRot(entity.getLookAngle());
            entity.level().addFreshEntity(blade);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            if (instance.isMastered(entity)) {
               if (instance.onCoolDown(mode)) {
                  instance.setCoolDown(0, mode);
               } else {
                  instance.setCoolDown(CONFIG.cooldown, mode);
               }
            }
         }
      }
   }
}
