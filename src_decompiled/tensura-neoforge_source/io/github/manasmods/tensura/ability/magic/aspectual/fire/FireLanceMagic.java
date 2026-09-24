package io.github.manasmods.tensura.ability.magic.aspectual.fire;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.FireLanceProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FireLanceMagic extends AspectualMagic {
   private static final AspectualMagicConfig.FireLance CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).FireLance;

   public FireLanceMagic() {
      super(AspectualMagic.AspectualType.FIRE);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
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
      return mode == 1 ? "fire_lance.repeat" : "fire_lance.single";
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      if (mode == 0) {
         super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
         MagicCircle.castMagicCircle(
            1.0F,
            25,
            MagicCircleVariant.FLAME,
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
         if (heldTicks % 4 != 0 && heldTicks < castTime) {
            return;
         }

         int cast = CONFIG.castTimeRepeat;
         String sec = heldTicks >= cast ? SkillUtils.ROUND_DOUBLE.format(cast / 20.0) : SkillUtils.ROUND_DOUBLE.format(heldTicks / 20.0);
         entity.displayClientMessage(
            Component.translatable("tensura.magic.cast_time.max", new Object[]{sec, SkillUtils.ROUND_DOUBLE.format(cast / 20.0)})
               .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
            true
         );
         MagicCircle.castMagicCircle(
            4.0F,
            50,
            MagicCircleVariant.FLAME,
            true,
            entity,
            instance.getOrCreateTag(),
            -1.0F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0) {
         if (!this.isCastingBlocked(instance, entity)) {
            Level level = entity.level();
            CompoundTag tag = instance.getOrCreateTag();
            int id = tag.getInt("LanceID");
            if (id != 0 && level.getEntity(id) instanceof FireLanceProjectile lance) {
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
               FireLanceProjectile lance = new FireLanceProjectile(entity.level(), entity);
               lance.noPhysics = true;
               lance.setSkill(entity, instance, this, mode);
               lance.setSize(0.25F);
               lance.setSpeed(1.75F);
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
         if (!(level.getEntity(id) instanceof FireLanceProjectile lance)) {
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
         if (heldTicks > 0 && heldTicks % CONFIG.castTimeRepeat == 0) {
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return false;
            }

            FireLanceProjectile lance = new FireLanceProjectile(level, entity);
            lance.setSkill(entity, instance, this, mode);
            lance.setSecondaryDamage(CONFIG.magicDamage);
            lance.setDamage(CONFIG.fireDamage);
            lance.setSize(0.25F);
            lance.setSpeed(1.75F);
            lance.setNoGravity(true);
            lance.setBurnTicks(100);
            lance.setDelayTick(CONFIG.castTimeRepeat);
            lance.setDelaySizeChange(0.75F / lance.getDelayTick());
            lance.setLookDistance(20.0F);
            double offsetX = (entity.getRandom().nextFloat() - 0.5) * 4.0;
            double offsetY = (entity.getRandom().nextFloat() - 0.5) * 4.0;
            lance.setOwnerOffset(new Vec3(offsetX, offsetY, 0.5));
            lance.setTargetOffset(new Vec3(offsetX, offsetY, 0.5));
            if (lance.getMagicCircle() == null && tag.contains("MagicCircleID")) {
               lance.setMagicCircle(level.getEntity(tag.getInt("MagicCircleID")));
            }

            lance.updateDelayPosition();
            level.addFreshEntity(lance);
         }

         instance.markDirty();
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 0) {
         Level level = entity.level();
         CompoundTag tag = instance.getOrCreateTag();
         int id = tag.getInt("LanceID");
         if (level.getEntity(id) instanceof FireLanceProjectile lance) {
            if (heldTicks >= this.getCastingTime(instance, entity) && !EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               lance.noPhysics = false;
               lance.setAge(0);
               lance.setDamage(CONFIG.fireDamage);
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
      }
   }
}
