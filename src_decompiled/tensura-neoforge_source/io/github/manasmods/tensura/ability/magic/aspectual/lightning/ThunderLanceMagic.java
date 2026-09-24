package io.github.manasmods.tensura.ability.magic.aspectual.lightning;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.ThunderLanceProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ThunderLanceMagic extends AspectualMagic {
   private static final AspectualMagicConfig.ThunderLance CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).ThunderLance;

   public ThunderLanceMagic() {
      super(AspectualMagic.AspectualType.LIGHTNING);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
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
      return mode == 1 ? "thunder_lance.rain" : "thunder_lance.single";
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (mode == 0) {
         MagicCircle.castMagicCircle(
            1.0F,
            25,
            MagicCircleVariant.LIGHTNING,
            entity,
            instance.getOrCreateTag(),
            1.0F,
            20.0F,
            new Vec3(0.0, 1.0, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      } else {
         MagicCircle.castMagicCircle(
            CONFIG.rainRadius,
            25,
            MagicCircleVariant.LIGHTNING,
            entity,
            instance.getOrCreateTag(),
            -2.0F,
            40.0F,
            new Vec3(0.0, 10.0, 0.0),
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
         if (mode != 1) {
            int id = tag.getInt("LanceID");
            if (id != 0 && level.getEntity(id) instanceof ThunderLanceProjectile lance) {
               tag.putInt("LanceID", 0);
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
               ThunderLanceProjectile lance = new ThunderLanceProjectile(entity.level(), entity);
               lance.noPhysics = true;
               lance.setSkill(entity, instance, this, mode);
               lance.setSize(0.25F);
               lance.setSpeed(2.0F);
               lance.setBurnTicks(100);
               lance.setNoGravity(true);
               lance.setLookDistance(20.0F);
               lance.setDelayTick(10);
               lance.setOwnerOffset(new Vec3(0.0, 0.0, -0.25));
               this.applyCastingVisual(instance, entity, 0, mode);
               if (lance.getMagicCircle() == null && tag.contains("MagicCircleID")) {
                  lance.setMagicCircle(entity.level().getEntity(tag.getInt("MagicCircleID")));
               }

               lance.updateDelayPosition();
               entity.level().addFreshEntity(lance);
               tag.putInt("LanceID", lance.getId());
               instance.markDirty();
            }
         } else {
            for (int i = 0; i <= CONFIG.rainNumber; i++) {
               int id = tag.getInt("LanceID_" + i);
               if (id != 0 && level.getEntity(id) instanceof ThunderLanceProjectile lance) {
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
                  ThunderLanceProjectile lance = new ThunderLanceProjectile(entity.level(), entity);
                  lance.noPhysics = true;
                  lance.setSkill(entity, instance, this, mode);
                  lance.setSize(0.25F);
                  lance.setSpeed(2.0F);
                  lance.setNoGravity(true);
                  lance.setLookDistance(40.0F);
                  lance.setDelayTick(10);
                  double offsetX = (entity.getRandom().nextFloat() - 0.5) * CONFIG.rainRadius;
                  double offsetY = (entity.getRandom().nextFloat() - 0.5) * CONFIG.rainRadius;
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
      Level level = entity.level();
      if (mode == 0) {
         int id = tag.getInt("LanceID");
         if (!(level.getEntity(id) instanceof ThunderLanceProjectile lance)) {
            tag.putInt("LanceID", 0);
            instance.markDirty();
            return false;
         } else {
            int cast = this.getCastingTime(instance, entity);
            if (cast <= 1 || heldTicks >= cast) {
               lance.setSize(1.0F);
            } else if (heldTicks > 0 && (int)(heldTicks % (cast / 10.0F)) == 0) {
               lance.setSize(lance.getSize() + 0.075F);
            }

            lance.setAge(0);
            lance.setDelayTick(10);
            instance.markDirty();
            this.applyCastingVisual(instance, entity, heldTicks, mode);
            return true;
         }
      } else {
         int cast = this.getCastingTime(instance, entity);

         for (int i = 0; i <= CONFIG.rainNumber; i++) {
            int id = tag.getInt("LanceID_" + i);
            if (level.getEntity(id) instanceof ThunderLanceProjectile lance) {
               if (cast <= 1 || heldTicks >= cast) {
                  lance.setSize(1.0F);
               } else if (heldTicks > 0 && (int)(heldTicks % (cast / 10.0F)) == 0) {
                  lance.setSize(lance.getSize() + 0.075F);
               }

               lance.setAge(0);
               lance.setDelayTick(10);
            } else {
               tag.putInt("LanceID_" + i, 0);
               instance.markDirty();
            }
         }

         instance.markDirty();
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTick, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      int cast = this.getCastingTime(instance, entity);
      if (mode != 1) {
         int id = tag.getInt("LanceID");
         if (level.getEntity(id) instanceof ThunderLanceProjectile lance) {
            if (heldTick >= cast && !EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               lance.noPhysics = false;
               lance.setAge(0);
               lance.setDamage(CONFIG.lightningDamage);
               lance.setSecondaryDamage(CONFIG.magicDamage);
               lance.setDelayTick(5);
               tag.putInt("LanceID", 0);
               entity.swing(InteractionHand.MAIN_HAND, true);
               instance.addMasteryPoint(entity);
            } else {
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
               tag.putInt("LanceID", 0);
               instance.markDirty();
            }
         } else {
            tag.putInt("LanceID", 0);
            instance.markDirty();
         }
      } else {
         boolean success = false;
         boolean outOfEnergy = false;

         for (int i = 0; i <= CONFIG.rainNumber; i++) {
            int id = tag.getInt("LanceID_" + i);
            if (level.getEntity(id) instanceof ThunderLanceProjectile lance) {
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
                  lance.setDamage(CONFIG.lightningDamageRain);
                  lance.setSecondaryDamage(CONFIG.magicDamageRain);
                  lance.setIgnoreInvulnerabilityOnHit(true);
                  lance.setHitRadius(1.0F);
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
            if (cast <= 1) {
               instance.setCoolDown(this.getCastingTime(instance, entity, this.getDefaultCastTime(), false) / 20, 1);
            }
         }
      }
   }
}
