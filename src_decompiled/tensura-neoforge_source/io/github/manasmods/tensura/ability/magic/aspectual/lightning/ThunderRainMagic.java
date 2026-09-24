package io.github.manasmods.tensura.ability.magic.aspectual.lightning;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.ThunderRainEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ThunderRainMagic extends AspectualMagic {
   private static final AspectualMagicConfig.ThunderRain CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).ThunderRain;

   public ThunderRainMagic() {
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
      return MAGIC_CONFIG.AspectualMagic.masteryGreat;
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
      return mode == 1 ? "thunder_rain.coat" : "thunder_rain.rain";
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
      if (castTime > 1) {
         if (mode == 0) {
            MagicCircle.castMagicCircle(
               CONFIG.radius,
               25,
               MagicCircleVariant.LIGHTNING,
               entity,
               instance.getOrCreateTag(),
               0.0F,
               new Vec3(0.0, CONFIG.radius * 1.5 + 2.0, 0.0),
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
         } else {
            MagicCircle.castMagicCircle(
               1.5F,
               25,
               MagicCircleVariant.LIGHTNING,
               entity,
               instance.getOrCreateTag(),
               0.0F,
               new Vec3(0.0, entity.getBbHeight() / 2.0F, 0.0),
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
         }
      }
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (target.isAlive() && !source.is(TensuraDamageTypes.LIGHTNING_ELEMENTAL)) {
         CompoundTag tag = instance.getOrCreateTag();
         if (tag.getInt("CoatTime") <= 0) {
            return true;
         }

         if (source.getDirectEntity() != entity) {
            return true;
         }

         if (!TensuraDamageHelper.isPhysicalAttack(source)) {
            return true;
         }

         tag.putInt("CoatTime", tag.getInt("CoatTime") - 1);
         instance.markDirty();
         target.invulnerableTime = 0;
         DamageSource lightningSource = this.createSource(instance, entity, TensuraDamageTypes.LIGHTNING_ELEMENTAL, 1);
         if (target.hurt(lightningSource, CONFIG.coatDamage) && target.isAlive()) {
            target.invulnerableTime = 0;
            target.hurtTime = 0;
         }

         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get());
         return true;
      } else {
         return true;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (mode == 1) {
            CompoundTag tag = instance.getOrCreateTag();
            if (tag.getInt("CoatTime") > 0) {
               entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
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
            } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               tag.putInt("CoatTime", CONFIG.coatNumber);
               instance.markDirty();
               entity.swing(InteractionHand.MAIN_HAND, true);
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
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(),
                  entity.getX(),
                  entity.getY() + entity.getBbHeight() / 2.0F,
                  entity.getZ(),
                  15,
                  0.08,
                  0.08,
                  0.08,
                  0.1,
                  true
               );
            }
         } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            ThunderRainEntity rain = new ThunderRainEntity(entity.level(), entity);
            rain.setLife(CONFIG.duration);
            rain.setSize(CONFIG.radius);
            rain.setSecondaryDamage(CONFIG.thunderDamage);
            rain.setTickEachHit(CONFIG.thunderInterval);
            rain.setFollowOwner(true);
            rain.setSkill(entity, instance, this, mode);
            rain.setPos(entity.getX(), entity.getY() + rain.getSize() * 1.5, entity.getZ());
            entity.level().addFreshEntity(rain);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(CONFIG.cooldown, mode);
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
            entity.swing(InteractionHand.MAIN_HAND, true);
         }
      }
   }
}
