package io.github.manasmods.tensura.ability.magic.aspectual.fire;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.FireBallProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FireBallMagic extends AspectualMagic {
   private static final AspectualMagicConfig.FireBall CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).FireBall;

   public FireBallMagic() {
      super(AspectualMagic.AspectualType.FIRE);
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

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.FIRE.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((FireMagic)AspectualMagics.FIRE.get()).getChatDisplayName(false)}
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
      if (instance.isMastered(entity) && heldTicks >= castTime) {
         int max = this.getCastingTime(instance, entity, this.getDefaultCastTime(), false);
         int tick = heldTicks - castTime;
         String sec = tick >= max ? SkillUtils.ROUND_DOUBLE.format(max / 20.0) : SkillUtils.ROUND_DOUBLE.format(tick / 20.0);
         entity.displayClientMessage(
            Component.translatable("tensura.magic.cast_time.remaining", new Object[]{sec, SkillUtils.ROUND_DOUBLE.format(max / 20.0)})
               .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
            true
         );
      } else {
         super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      }

      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            0.75F,
            25,
            MagicCircleVariant.FLAME,
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

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTime, int keyNumber, int mode) {
      int castingTime = this.getCastingTime(instance, entity);
      if (heldTime >= castingTime) {
         boolean charged = instance.isMastered(entity) && heldTime >= this.getCastingTime(instance, entity, this.getDefaultCastTime(), false);
         if (charged) {
            if (this.isOutOfEnergy(entity, instance, (double)0.0, (double)CONFIG.magiculeCostCharged)) {
               return;
            }
         } else if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return;
         }

         FireBallProjectile ball = new FireBallProjectile(entity.level(), entity);
         ball.setSize(charged ? 3.0F : 2.0F);
         ball.setDamage(charged ? CONFIG.fireDamageCharged : CONFIG.fireDamage);
         ball.setSecondaryDamage(charged ? CONFIG.magicDamageCharged : CONFIG.magicDamage);
         ball.setSkill(entity, instance, this, mode);
         float radius = charged ? CONFIG.explosionRadiusCharged : CONFIG.explosionRadius;
         ball.setExplosionRadius(radius);
         ball.setHitRadius(radius);
         ball.setNoGravity(true);
         ball.setSpeed(1.5F);
         ball.setBurnTicks(100);
         ball.setImpactParticleCount(4);
         ball.setPos(entity.getEyePosition().add(0.0, -0.25, 0.0).add(entity.getLookAngle().normalize()));
         ball.shootFromRot(entity.getLookAngle());
         entity.level().addFreshEntity(ball);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         entity.swing(InteractionHand.MAIN_HAND, true);
         instance.addMasteryPoint(entity);
         instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
      }
   }
}
