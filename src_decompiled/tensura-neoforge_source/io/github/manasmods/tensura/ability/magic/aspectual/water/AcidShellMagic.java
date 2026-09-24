package io.github.manasmods.tensura.ability.magic.aspectual.water;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.AcidBallProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AcidShellMagic extends AspectualMagic {
   private static final AspectualMagicConfig.AcidShell CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).AcidShell;

   public AcidShellMagic() {
      super(AspectualMagic.AspectualType.WATER);
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
      MagicCircle.castMagicCircle(
         1.0F,
         25,
         MagicCircleVariant.WATER,
         entity,
         instance.getOrCreateTag(),
         1.0F,
         20.0F,
         Vec3.ZERO,
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!this.isCastingBlocked(instance, entity)) {
         Level level = entity.level();
         CompoundTag tag = instance.getOrCreateTag();
         int id = tag.getInt("ShellID");
         if (id != 0 && level.getEntity(id) instanceof AcidBallProjectile lance) {
            tag.putInt("ShellID", 0);
            instance.markDirty();
            lance.discard();
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
         } else {
            AcidBallProjectile lance = new AcidBallProjectile(entity.level(), entity);
            lance.noPhysics = true;
            lance.setSkill(entity, instance, this, mode);
            lance.setSize(0.1F);
            lance.setSpeed(1.25F);
            lance.setLookDistance(20.0F);
            lance.setDelayTick(10);
            lance.setOwnerOffset(new Vec3(0.0, 0.0, -0.125));
            this.applyCastingVisual(instance, entity, 0, mode);
            if (lance.getMagicCircle() == null && tag.contains("MagicCircleID")) {
               lance.setMagicCircle(entity.level().getEntity(tag.getInt("MagicCircleID")));
            }

            lance.updateDelayPosition();
            entity.level().addFreshEntity(lance);
            tag.putInt("ShellID", lance.getId());
            instance.markDirty();
         }
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      } else if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      } else {
         CompoundTag tag = instance.getOrCreateTag();
         Level level = entity.level();
         int id = tag.getInt("ShellID");
         if (!(level.getEntity(id) instanceof AcidBallProjectile ball)) {
            tag.putInt("ShellID", 0);
            instance.markDirty();
            return false;
         } else {
            int cast = this.getCastingTime(instance, entity);
            if (cast <= 1 || heldTicks >= cast) {
               ball.setSize(0.5F);
            } else if (heldTicks > 0 && (int)(heldTicks % (cast / 10.0F)) == 0) {
               ball.setSize(ball.getSize() + 0.04F);
            }

            ball.setAge(0);
            ball.setDelayTick(10);
            instance.markDirty();
            this.applyCastingVisual(instance, entity, heldTicks, mode);
            return true;
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      int id = tag.getInt("ShellID");
      if (level.getEntity(id) instanceof AcidBallProjectile ball) {
         if (heldTicks >= this.getCastingTime(instance, entity) && !EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            ball.noPhysics = false;
            ball.setAge(0);
            ball.setArmorHurt(CONFIG.armorHurt);
            ball.setSecondaryDamage(CONFIG.magicDamage);
            ball.setDelayTick(5);
            if (instance.isMastered(entity)) {
               MobEffectInstance corrosion = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), CONFIG.corrosionDuration, CONFIG.corrosionLevel - 1, true, false, true
               );
               ball.setMobEffect(corrosion);
            }

            tag.putInt("ShellID", 0);
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
         } else {
            ball.discard();
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            tag.putInt("ShellID", 0);
            instance.markDirty();
         }
      } else {
         tag.putInt("ShellID", 0);
         instance.markDirty();
      }
   }
}
