package io.github.manasmods.tensura.ability.magic.aspectual.earth;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.StoneShotProjectile;
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

public class StoneShotMagic extends AspectualMagic {
   private static final AspectualMagicConfig.StoneShot CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).StoneShot;

   public StoneShotMagic() {
      super(AspectualMagic.AspectualType.EARTH);
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
      return mode == 1 ? "stone_shot.chain" : "stone_shot.spread";
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (mode == 1) {
         MagicCircle.castMagicCircle(
            1.0F,
            25,
            MagicCircleVariant.EARTH,
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
            2.0F,
            25,
            MagicCircleVariant.EARTH,
            entity,
            instance.getOrCreateTag(),
            1.0F,
            20.0F,
            new Vec3(0.0, 1.5, 0.0),
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

         for (int i = 0; i < CONFIG.stoneNumber; i++) {
            int id = tag.getInt("StoneID_" + i);
            if (id != 0 && level.getEntity(id) instanceof StoneShotProjectile lance) {
               tag.putInt("StoneID_" + i, 0);
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
               StoneShotProjectile lance = new StoneShotProjectile(entity.level(), entity);
               lance.noPhysics = true;
               lance.setSkill(entity, instance, this, mode);
               lance.setSize(0.1F);
               lance.setSpeed(2.0F);
               lance.setLookDistance(40.0F);
               lance.setDelayTick(10);
               if (mode == 0) {
                  double offsetX = (entity.getRandom().nextFloat() - 0.5) * 2.0;
                  double offsetY = (entity.getRandom().nextFloat() - 0.5) * 2.0;
                  lance.setOwnerOffset(new Vec3(offsetX, offsetY, -0.125));
                  lance.setTargetOffset(new Vec3(-offsetX, offsetY, 0.0));
               } else {
                  lance.setOwnerOffset(new Vec3(0.0, 0.0, -0.125));
               }

               this.applyCastingVisual(instance, entity, 0, mode);
               if (lance.getMagicCircle() == null && tag.contains("MagicCircleID")) {
                  lance.setMagicCircle(entity.level().getEntity(tag.getInt("MagicCircleID")));
               }

               lance.updateDelayPosition();
               entity.level().addFreshEntity(lance);
               tag.putInt("StoneID_" + i, lance.getId());
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

      CompoundTag tag = instance.getOrCreateTag();
      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      Level level = entity.level();
      int cast = this.getCastingTime(instance, entity);

      for (int i = 0; i <= CONFIG.stoneNumber; i++) {
         int id = tag.getInt("StoneID_" + i);
         if (level.getEntity(id) instanceof StoneShotProjectile lance) {
            if (cast <= 1 || heldTicks >= cast) {
               lance.setSize(0.5F);
            } else if (heldTicks > 0 && (int)(heldTicks % (cast / 10.0F)) == 0) {
               lance.setSize(lance.getSize() + 0.04F);
            }

            lance.setAge(0);
            lance.setDelayTick(10);
         } else {
            tag.putInt("StoneID_" + i, 0);
            instance.markDirty();
         }
      }

      instance.markDirty();
      this.applyCastingVisual(instance, entity, heldTicks, mode);
      return true;
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTick, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      int cast = this.getCastingTime(instance, entity);
      Level level = entity.level();
      int delay = 5;
      boolean outOfEnergy = heldTick >= cast && EnergyHelper.isOutOfEnergy(entity, instance, mode);

      for (int i = 0; i <= CONFIG.stoneNumber; i++) {
         int id = tag.getInt("StoneID_" + i);
         if (level.getEntity(id) instanceof StoneShotProjectile lance) {
            if (heldTick >= cast && !outOfEnergy) {
               lance.noPhysics = false;
               lance.setAge(0);
               lance.setSecondaryDamage(CONFIG.magicDamage);
               lance.setDamage(CONFIG.earthDamage);
               lance.setIgnoreInvulnerabilityOnHit(true);
               lance.setDelayTick(delay);
               tag.putInt("StoneID_" + i, 0);
               if (mode == 1) {
                  delay += 10;
               }
            } else {
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
               tag.putInt("StoneID_" + i, 0);
               instance.markDirty();
            }
         } else {
            tag.putInt("StoneID_" + i, 0);
            instance.markDirty();
         }
      }

      if (heldTick >= cast && !outOfEnergy) {
         entity.swing(InteractionHand.MAIN_HAND, true);
         instance.addMasteryPoint(entity);
         instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
      }
   }
}
