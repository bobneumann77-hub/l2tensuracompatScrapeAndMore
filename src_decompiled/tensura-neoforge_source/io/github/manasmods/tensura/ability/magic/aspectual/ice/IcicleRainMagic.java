package io.github.manasmods.tensura.ability.magic.aspectual.ice;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.IceLanceProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IcicleRainMagic extends AspectualMagic {
   private static final AspectualMagicConfig.IcicleRain CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).IcicleRain;

   public IcicleRainMagic() {
      super(AspectualMagic.AspectualType.ICE);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryHigh;
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
      return mode == 1 ? "icicle_rain.repeat" : super.getModeId(instance, mode);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 1 ? CONFIG.magiculeCostRepeat : CONFIG.magiculeCost;
   }

   @Override
   public boolean canLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (!super.canLearnSkill(instance, entity)) {
         return false;
      }

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.ICICLE_LANCE.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((IcicleLanceMagic)AspectualMagics.ICICLE_LANCE.get()).getChatDisplayName(false)}
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
      if (mode == 0) {
         MagicCircle.castMagicCircle(
            CONFIG.radius,
            25,
            MagicCircleVariant.ICE,
            entity,
            instance.getOrCreateTag(),
            -2.0F,
            40.0F,
            new Vec3(0.0, 20.0, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      } else {
         MagicCircle.castMagicCircle(
            CONFIG.radius,
            this.getCastingTime(instance, entity, CONFIG.castTimeRepeat, false),
            MagicCircleVariant.ICE,
            entity,
            instance.getOrCreateTag(),
            -2.0F,
            40.0F,
            new Vec3(0.0, 20.0, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!this.isCastingBlocked(instance, entity)) {
         Level level = entity.level();
         CompoundTag tag = instance.getOrCreateTag();

         for (int i = 0; i <= CONFIG.lanceNumber; i++) {
            int id = tag.getInt("LanceID_" + i);
            if (id != 0 && level.getEntity(id) instanceof IceLanceProjectile lance) {
               tag.putInt("LanceID_" + i, 0);
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
               IceLanceProjectile lance = new IceLanceProjectile(entity.level(), entity);
               lance.noPhysics = true;
               lance.setSkill(entity, instance, this, mode);
               lance.setSize(0.25F);
               lance.setSpeed(1.75F);
               lance.setNoGravity(true);
               lance.setLookDistance(40.0F);
               lance.setDelayTick(10);
               double offsetX = (entity.getRandom().nextFloat() - 0.5) * CONFIG.radius;
               double offsetY = (entity.getRandom().nextFloat() - 0.5) * CONFIG.radius;
               lance.setOwnerOffset(new Vec3(offsetX, offsetY, 0.25));
               lance.setTargetOffset(new Vec3(-offsetX, offsetY, 0.0));
               this.applyCastingVisual(instance, entity, 0, mode);
               if (lance.getMagicCircle() == null && tag.contains("MagicCircleID")) {
                  lance.setMagicCircle(entity.level().getEntity(tag.getInt("MagicCircleID")));
               }

               lance.updateDelayPosition();
               entity.level().addFreshEntity(lance);
               tag.putInt("LanceID_" + i, lance.getId());
            }
         }

         instance.markDirty();
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      CompoundTag tag = instance.getOrCreateTag();
      boolean success = false;
      int cast = this.getCastingTime(instance, entity);
      if (mode == 0 || heldTicks <= cast) {
         Level level = entity.level();

         for (int i = 0; i <= CONFIG.lanceNumber; i++) {
            int id = tag.getInt("LanceID_" + i);
            if (level.getEntity(id) instanceof IceLanceProjectile lance) {
               if (cast <= 1 || heldTicks >= cast) {
                  lance.setSize(1.0F);
               } else if (heldTicks > 0 && (int)(heldTicks % (cast / 10.0F)) == 0) {
                  lance.setSize(lance.getSize() + 0.075F);
               }

               lance.setAge(0);
               if (mode == 1 && heldTicks == cast) {
                  if (!success) {
                     if (EnergyHelper.isOutOfEnergy(entity, instance, 0)) {
                        break;
                     }

                     success = true;
                  }

                  lance.noPhysics = false;
                  lance.setSecondaryDamage(CONFIG.magicDamage);
                  lance.setIgnoreInvulnerabilityOnHit(true);
                  lance.setHitRadius(1.5F);
                  lance.setDelayTick(5);
                  tag.putInt("LanceID_" + i, 0);
               } else {
                  lance.setDelayTick(10);
               }
            } else {
               tag.putInt("LanceID_" + i, 0);
               instance.markDirty();
            }
         }
      }

      if (mode == 1 && heldTicks >= cast) {
         success = success || heldTicks > cast;
         if (success && (heldTicks - cast) % this.getCastingTime(instance, entity, CONFIG.castTimeRepeat, false) == 0) {
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return false;
            }

            for (int i = 0; i < CONFIG.lanceNumber; i++) {
               this.shootLance(instance, entity, mode, tag);
            }
         }
      }

      instance.markDirty();
      if (mode != 0 && heldTicks >= cast) {
         int tick = heldTicks - cast;
         double holdDuration = this.getCastingTime(instance, entity, CONFIG.castTimeRepeat, false) * (CONFIG.maxTimeRepeat - 0.5);
         this.renderRemainingTime(entity, tick, (int)holdDuration);
         return tick < holdDuration;
      } else {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTick, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      int cast = this.getCastingTime(instance, entity);
      if (mode == 1 && cast <= 1 && heldTick >= cast && tag.getInt("LanceID_0") == 0) {
         instance.setCoolDown(this.getCastingTime(instance, entity, this.getDefaultCastTime(), false) / 20, mode);
      } else {
         Level level = entity.level();
         boolean success = false;
         boolean outOfEnergy = false;

         for (int i = 0; i <= CONFIG.lanceNumber; i++) {
            int id = tag.getInt("LanceID_" + i);
            if (level.getEntity(id) instanceof IceLanceProjectile lance) {
               if (heldTick >= cast && !outOfEnergy) {
                  if (!success) {
                     if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                        lance.discard();
                        level.playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                        tag.putInt("LanceID_" + i, 0);
                        instance.markDirty();
                        outOfEnergy = true;
                        continue;
                     }

                     success = true;
                  }

                  lance.noPhysics = false;
                  lance.setAge(0);
                  lance.setSecondaryDamage(CONFIG.magicDamage);
                  lance.setIgnoreInvulnerabilityOnHit(true);
                  lance.setHitRadius(1.5F);
                  lance.setDelayTick(5);
                  tag.putInt("LanceID_" + i, 0);
               } else {
                  lance.discard();
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.5F,
                     1.0F
                  );
                  tag.putInt("LanceID_" + i, 0);
                  instance.markDirty();
               }
            } else {
               tag.putInt("LanceID_" + i, 0);
               instance.markDirty();
            }
         }

         if (success) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            if (mode == 0 && cast <= 1) {
               instance.setCoolDown(this.getCastingTime(instance, entity, this.getDefaultCastTime(), false) / 20, 0);
            }
         }
      }
   }

   private void shootLance(ManasSkillInstance instance, LivingEntity entity, int mode, CompoundTag tag) {
      IceLanceProjectile lance = new IceLanceProjectile(entity.level(), entity);
      lance.setSkill(entity, instance, this, mode);
      lance.setSecondaryDamage(CONFIG.magicDamage);
      lance.setIgnoreInvulnerabilityOnHit(true);
      lance.setHitRadius(1.5F);
      lance.setSize(0.25F);
      lance.setSpeed(1.75F);
      lance.setNoGravity(true);
      lance.setDelayTick(this.getCastingTime(instance, entity, CONFIG.castTimeRepeat, false));
      lance.setDelaySizeChange(0.75F / lance.getDelayTick());
      lance.setLookDistance(40.0F);
      double offsetX = (entity.getRandom().nextFloat() - 0.5) * CONFIG.radius;
      double offsetY = (entity.getRandom().nextFloat() - 0.5) * CONFIG.radius;
      lance.setOwnerOffset(new Vec3(offsetX, offsetY, 0.25));
      lance.setTargetOffset(new Vec3(-offsetX, offsetY, 0.0));
      if (lance.getMagicCircle() == null && tag.contains("MagicCircleID")) {
         lance.setMagicCircle(entity.level().getEntity(tag.getInt("MagicCircleID")));
      }

      lance.updateDelayPosition();
      entity.level().addFreshEntity(lance);
   }
}
