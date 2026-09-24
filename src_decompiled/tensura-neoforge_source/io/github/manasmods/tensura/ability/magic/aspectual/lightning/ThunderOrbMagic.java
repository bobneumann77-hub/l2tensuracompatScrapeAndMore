package io.github.manasmods.tensura.ability.magic.aspectual.lightning;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.FireLanceProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.ThunderSphereProjectile;
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

public class ThunderOrbMagic extends AspectualMagic {
   private static final AspectualMagicConfig.ThunderOrb CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).ThunderOrb;

   public ThunderOrbMagic() {
      super(AspectualMagic.AspectualType.LIGHTNING);
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

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.THUNDER.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((ThunderMagic)AspectualMagics.THUNDER.get()).getChatDisplayName(false)}
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
      MagicCircle.castMagicCircle(
         1.0F,
         25,
         MagicCircleVariant.LIGHTNING,
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
         int id = tag.getInt("SphereID");
         if (id != 0 && level.getEntity(id) instanceof FireLanceProjectile lance) {
            tag.putInt("SphereID", 0);
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
            ThunderSphereProjectile sphere = new ThunderSphereProjectile(entity.level(), entity);
            sphere.noPhysics = true;
            sphere.setSkill(entity, instance, this, mode);
            sphere.setSize(0.1F);
            sphere.setSpeed(0.75F);
            sphere.setLookDistance(20.0F);
            sphere.setDelayTick(10);
            sphere.setOwnerOffset(new Vec3(0.0, 0.0, -1.0));
            this.applyCastingVisual(instance, entity, 0, mode);
            if (sphere.getMagicCircle() == null && tag.contains("MagicCircleID")) {
               sphere.setMagicCircle(entity.level().getEntity(tag.getInt("MagicCircleID")));
            }

            sphere.updateDelayPosition();
            entity.level().addFreshEntity(sphere);
            tag.putInt("SphereID", sphere.getId());
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
         int id = tag.getInt("SphereID");
         if (!(level.getEntity(id) instanceof ThunderSphereProjectile sphere)) {
            tag.putInt("SphereID", 0);
            instance.markDirty();
            return false;
         } else {
            int cast = this.getCastingTime(instance, entity);
            if (cast <= 1 || heldTicks >= cast) {
               sphere.setSize(1.1F);
            } else if (heldTicks > 0 && (int)(heldTicks % (cast / 10.0F)) == 0) {
               sphere.setSize(sphere.getSize() + 0.1F);
            }

            sphere.setAge(0);
            sphere.setDelayTick(10);
            instance.markDirty();
            this.applyCastingVisual(instance, entity, heldTicks, mode);
            return true;
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      int id = tag.getInt("SphereID");
      if (level.getEntity(id) instanceof ThunderSphereProjectile sphere) {
         if (heldTicks >= this.getCastingTime(instance, entity) && !EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            sphere.noPhysics = false;
            sphere.setAge(0);
            sphere.setDelayTick(5);
            sphere.setDamage(CONFIG.lightningDamage);
            sphere.setSecondaryDamage(CONFIG.magicDamage);
            sphere.setStrikeRadius(instance.isMastered(entity) ? CONFIG.strikeRadiusMastered : CONFIG.strikeRadius);
            sphere.setStrikeInterval(CONFIG.strikeInterval);
            tag.putInt("SphereID", 0);
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         } else {
            sphere.discard();
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
            tag.putInt("SphereID", 0);
            instance.markDirty();
         }
      } else {
         tag.putInt("SphereID", 0);
         instance.markDirty();
      }
   }
}
