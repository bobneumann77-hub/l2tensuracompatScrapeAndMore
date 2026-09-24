package io.github.manasmods.tensura.ability.magic.spiritual.wind;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.LightningLanceProjectile;
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

public class LightningLanceMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.LightningLance CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).LightningLance;

   public LightningLanceMagic() {
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

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      MagicCircle.castMagicCircle(
         1.0F,
         25,
         MagicCircleVariant.WIND,
         entity,
         instance.getOrCreateTag(),
         1.0F,
         20.0F,
         new Vec3(0.0, 1.0, 0.0),
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!this.isCastingBlocked(instance, entity)) {
         Level level = entity.level();
         CompoundTag tag = instance.getOrCreateTag();
         int id = tag.getInt("LanceID");
         if (id != 0 && level.getEntity(id) instanceof LightningLanceProjectile lance) {
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
            LightningLanceProjectile lance = new LightningLanceProjectile(entity.level(), entity);
            lance.noPhysics = true;
            lance.setEffectRange(-1.0F);
            lance.setLife(141);
            lance.setSkill(entity, instance, this, mode);
            lance.setSize(0.25F);
            lance.setSpeed(1.75F);
            lance.setPiercingEntity(true);
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
         int id = tag.getInt("LanceID");
         if (!(level.getEntity(id) instanceof LightningLanceProjectile lance)) {
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
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      int id = tag.getInt("LanceID");
      if (level.getEntity(id) instanceof LightningLanceProjectile lance) {
         if (heldTicks >= this.getCastingTime(instance, entity) && !EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            lance.noPhysics = false;
            lance.setAge(0);
            lance.setDelayTick(5);
            lance.setDamage(instance.isMastered(entity) ? CONFIG.damageMastered : CONFIG.damage);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            tag.putInt("LanceID", 0);
            instance.markDirty();
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
   }
}
