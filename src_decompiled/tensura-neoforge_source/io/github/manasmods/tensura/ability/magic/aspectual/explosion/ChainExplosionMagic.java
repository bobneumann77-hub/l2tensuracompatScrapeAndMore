package io.github.manasmods.tensura.ability.magic.aspectual.explosion;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.field.MagicExplosion;
import io.github.manasmods.tensura.entity.magic.misc.MagicLandmineEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ChainExplosionMagic extends AspectualMagic {
   private static final AspectualMagicConfig.ChainExplosion CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).ChainExplosion;

   public ChainExplosionMagic() {
      super(AspectualMagic.AspectualType.EXPLOSION);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public boolean isInstantCast(ManasSkillInstance instance, LivingEntity entity) {
      return false;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryGreat;
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

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.EXPLOSION.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((ExplosionMagic)AspectualMagics.EXPLOSION.get()).getChatDisplayName(false)}
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

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!this.isCastingBlocked(instance, entity)) {
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
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      int amount = CONFIG.explosionNumber;
      int radius = Math.round(instance.isMastered(entity) ? CONFIG.explosionRadiusMastered : CONFIG.explosionRadius);
      float distance = radius * 3 / 4.0F;
      int delay = 0;
      CompoundTag tag = instance.getOrCreateTag();
      Vec3 center = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY"), tag.getDouble("circleZ"));

      for (int i = 0; i < amount; i++) {
         double angle = (Math.PI * 2) / amount * i;
         double dx = Math.cos(angle) * distance;
         double dz = Math.sin(angle) * distance;
         Vec3 pos = center.add(dx, 0.0, dz);
         MagicCircle.castTargetedMagicCircle(
            "MagicCircleID" + i,
            radius / 5.0F,
            25 + delay,
            pos,
            MagicCircleVariant.EXPLOSION,
            true,
            entity,
            CONFIG.range,
            false,
            instance.getOrCreateTag(),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
         delay += CONFIG.explosionDelay;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            CompoundTag tag = instance.getOrCreateTag();
            Vec3 center = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY"), tag.getDouble("circleZ"));
            this.spreadExplosion(instance, entity, mode, center);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
         }
      }
   }

   private void spreadExplosion(ManasSkillInstance instance, LivingEntity entity, int mode, Vec3 center) {
      int amount = CONFIG.explosionNumber;
      float damage = instance.isMastered(entity) ? CONFIG.magicDamageMastered : CONFIG.magicDamage;
      int radius = Math.round(instance.isMastered(entity) ? CONFIG.explosionRadiusMastered : CONFIG.explosionRadius);
      float distance = radius * 2 / 3.0F;
      int delay = 0;

      for (int i = 0; i < amount; i++) {
         double angle = (Math.PI * 2) / amount * i;
         double dx = Math.cos(angle) * distance;
         double dz = Math.sin(angle) * distance;
         double x = center.x() + dx;
         double y = center.y();
         double z = center.z() + dz;
         if (i == 0) {
            MagicExplosion explosion = new MagicExplosion(entity.level(), entity);
            explosion.setPos(x, y, z);
            explosion.setTickEachHit(1);
            explosion.setLife(50);
            explosion.setSecondaryDamage(damage);
            explosion.setSize(radius);
            explosion.setElementalAttack(true);
            explosion.setSkill(entity, instance, this, mode);
            explosion.level().addFreshEntity(explosion);
         } else {
            MagicLandmineEntity landmine = new MagicLandmineEntity(entity.level(), x, y, z, entity);
            landmine.setSkill(entity, instance, this, mode);
            landmine.setSecondaryDamage(damage);
            landmine.setElementalAttack(true);
            landmine.setExplosionType(MagicLandmineEntity.ExplosionType.MAGIC_FULL);
            landmine.setRadius(radius);
            landmine.setTriggerTimer(delay);
            entity.level().addFreshEntity(landmine);
         }

         delay += CONFIG.explosionDelay;
      }
   }
}
