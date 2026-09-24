package io.github.manasmods.tensura.ability.magic.aspectual.wind;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.WindTornadoProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class TornadoBladeMagic extends AspectualMagic {
   private static final AspectualMagicConfig.TornadoBlade CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).TornadoBlade;

   public TornadoBladeMagic() {
      super(AspectualMagic.AspectualType.WIND);
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
   public boolean canLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (!super.canLearnSkill(instance, entity)) {
         return false;
      }

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.WIND_CUTTER.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((WindCutterMagic)AspectualMagics.WIND_CUTTER.get()).getChatDisplayName(false)}
                  )
                  .withStyle(ChatFormatting.RED),
               true
            );
         }

         return false;
      } else {
         return true;
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
            this.shootWindSphere(instance, entity, mode);
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         }
      }
   }

   private void shootWindSphere(ManasSkillInstance instance, LivingEntity entity, int mode) {
      WindTornadoProjectile blade = new WindTornadoProjectile(entity.level(), entity);
      blade.setSpeed(1.0F);
      blade.setBurstDelay(CONFIG.burstDelay + 13);
      if (instance.isMastered(entity)) {
         blade.setHitRadius(CONFIG.hitRadiusMastered);
         blade.setDamage(CONFIG.windDamageMastered);
         blade.setSecondaryDamage(CONFIG.magicDamageMastered);
         blade.setPullForce(CONFIG.pullForce);
         blade.setOnExplodeBlades(CONFIG.bladeNumber);
         blade.setBladeDamage(CONFIG.bladeDamage);
      } else {
         blade.setHitRadius(CONFIG.hitRadius);
         blade.setDamage(CONFIG.windDamage);
         blade.setSecondaryDamage(CONFIG.magicDamage);
      }

      blade.setSkill(entity, instance, this, mode);
      blade.setNoGravity(true);
      blade.setPos(entity.getEyePosition().add(entity.getLookAngle().normalize()));
      blade.shootFromRot(entity.getLookAngle());
      entity.level().addFreshEntity(blade);
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }
}
