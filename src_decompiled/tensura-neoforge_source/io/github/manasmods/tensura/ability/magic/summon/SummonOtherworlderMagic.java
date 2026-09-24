package io.github.manasmods.tensura.ability.magic.summon;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.config.ability.magic.SummoningMagicConfig;
import io.github.manasmods.tensura.data.otherworlder.OtherworlderSpawnDistribution;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.magic.SummoningMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SummonOtherworlderMagic extends SummoningMagic<AgeableMob> {
   public static final SummoningMagicConfig.SummonOtherworlder CONFIG = ((SummoningMagicConfig)ConfigRegistry.getConfig(SummoningMagicConfig.class)).SummonOtherworlder;

   @Override
   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return false;
   }

   @Override
   public boolean canRemoveSummon(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return false;
   }

   @Override
   public boolean isSummoningDisabled(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.onCoolDown(mode);
   }

   public boolean isSummoningRequirementFulfilled(ManasSkillInstance instance, LivingEntity entity, AgeableMob summon, int mode) {
      return true;
   }

   @Override
   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      MagicCircle circle = ObjectSelectionHelper.getTargetingEntity(MagicCircle.class, entity, CONFIG.castRange, 0.5, false, true, false);
      if (circle == null || circle.getChargedEnergy() < 0.0F || circle.getSkill().getSkill() != SummoningMagics.SUMMON_OTHERWORLDER.get()) {
         CompoundTag tag = instance.getOrCreateTag();
         tag.remove("SummonUUID");
         instance.markDirty();
         BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, CONFIG.castRange);
         Vec3 pos = result.getLocation();
         Level level = entity.level();
         if (!level.getBlockState(result.getBlockPos().below()).isSolid() || !level.getBlockState(result.getBlockPos().below(2)).isSolid()) {
            return;
         }

         this.summonMagicCircle(instance, entity, pos, 0, mode);
         level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), this.getSummoningSound(instance, mode), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
      } else if (circle.getOwner() == entity && circle.getChargedEnergy() >= CONFIG.magiculeCostTotal) {
         circle.setChargedEnergy(CONFIG.magiculeCostTotal);
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (this.isSummoningDisabled(instance, entity, mode)) {
         this.removeFailedSummon(instance, entity, mode);
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      MagicCircle circle = ObjectSelectionHelper.getTargetingEntity(MagicCircle.class, entity, CONFIG.castRange, 0.5, false, true, false);
      if (circle != null && circle.getChargedEnergy() >= 0.0F && circle.getSkill().getSkill() == SummoningMagics.SUMMON_OTHERWORLDER.get()) {
         Level level = entity.level();
         if (heldTicks > 0 && heldTicks % CONFIG.castInterval == 0 && circle.getChargedEnergy() < CONFIG.magiculeCostTotal) {
            float cost = instance.isMastered(entity) ? CONFIG.magiculeCostIntervalMastered : CONFIG.magiculeCostInterval;
            float magicule = Math.min(CONFIG.magiculeCostTotal - circle.getChargedEnergy(), cost);
            if (this.isOutOfEnergy(entity, instance, 0, magicule)) {
               level.playSound(null, circle.getX(), circle.getY(), circle.getZ(), this.getFailSound(instance, mode), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               return false;
            } else {
               circle.setAge(0);
               circle.setChargedEnergy(Math.min(circle.getChargedEnergy() + magicule, CONFIG.magiculeCostTotal));
               level.playSound(null, circle.getX(), circle.getY(), circle.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F);
               return true;
            }
         } else if (circle.getOwner() == entity && circle.getChargedEnergy() >= CONFIG.magiculeCostTotal) {
            float magicule = circle.getChargedEnergy() - CONFIG.magiculeCostTotal;
            circle.setChargedEnergy(circle.getChargedEnergy() + 1.0F);
            CompoundTag tag = instance.getOrCreateTag();
            circle.setAge(circle.getLife() - 25);
            if (magicule == 0.0F && !tag.hasUUID("SummonUUID")) {
               this.createSummon(instance, entity, mode, circle.position(), "SummonUUID");
            }

            if (tag.hasUUID("SummonUUID") && this.callForthSummon(instance, entity, mode, tag.getUUID("SummonUUID"), (int)magicule)) {
               circle.setChargedEnergy(-1.0F);
            }

            level.playSound(null, circle.getX(), circle.getY(), circle.getZ(), this.getSummoningSound(instance, mode), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
            return magicule < 40.0F;
         } else {
            if (entity instanceof Player player) {
               player.displayClientMessage(
                  Component.translatable("tensura.skill.stored_magicule", new Object[]{circle.getChargedEnergy(), CONFIG.magiculeCostTotal})
                     .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                  true
               );
            }

            level.playSound(null, circle.getX(), circle.getY(), circle.getZ(), this.getSummoningSound(instance, mode), TensuraSkill.ABILITY_SOUND, 0.25F, 2.0F);
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void createSummon(ManasSkillInstance instance, LivingEntity entity, int mode, Vec3 position, String summonId) {
      EntityType<AgeableMob> type = this.getSummonedType(instance, entity, mode);
      if (type != null) {
         Level level = entity.level();
         AgeableMob mob = (AgeableMob)type.create(level);
         if (mob != null) {
            mob.setNoAi(true);
            mob.noPhysics = true;
            mob.setPos(position.add(0.0, -1.5 * mob.getBbHeight(), 0.0));
            mob.finalizeSpawn(
               (ServerLevelAccessor)level, level.getCurrentDifficultyAt(ObjectSelectionHelper.getBlockPos(position)), MobSpawnType.TRIGGERED, null
            );
            this.addAdditionalSummonData(instance, entity, mob, mode);
            level.addFreshEntity(mob);
            mob.lookAt(Anchor.EYES, entity.getEyePosition());
            instance.getOrCreateTag().putUUID(summonId, mob.getUUID());
         }
      }
   }

   @Override
   public void removeFailedSummon(ManasSkillInstance instance, LivingEntity entity, int mode) {
      CompoundTag tag = instance.getTag();
      if (tag != null && tag.hasUUID("SummonUUID")) {
         Entity summon = ((ServerLevel)entity.level()).getEntity(tag.getUUID("SummonUUID"));
         if (summon instanceof Mob mob) {
            if (!mob.isNoAi()) {
               return;
            }

            summon.discard();
            mob.playSound(this.getFailSound(instance, mode), 3.0F, 1.0F);
            instance.setCoolDowns(this.getSuccessCooldown(instance, entity));
            this.removeAttributeModifiers(instance, entity, mode);
            TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.FLASH);
            TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.FLASH, 2.0);
            tag.remove("SummonUUID");
            instance.markDirty();
         }
      }
   }

   @Nullable
   @Override
   public EntityType<AgeableMob> getSummonedType(ManasSkillInstance instance, LivingEntity entity, int mode) {
      double chance = entity.getRandom().nextFloat();

      for (OtherworlderSpawnDistribution spawning : entity.registryAccess().registryOrThrow(TensuraCustomData.OTHERWORLDER_SPAWN_DISTRIBUTION)) {
         if (!(chance >= spawning.chance())) {
            Optional<EntityType<?>> entityType = EntityType.byString(spawning.entity().toString());
            if (!entityType.isEmpty()) {
               return (EntityType<AgeableMob>)entityType.get();
            }
            break;
         }

         chance -= spawning.chance();
      }

      return null;
   }

   public void addAdditionalSummonData(ManasSkillInstance instance, LivingEntity entity, AgeableMob summon, int mode) {
      float chance = instance.isMastered(entity) ? CONFIG.failChanceMastered : CONFIG.failChance;
      if (entity.getRandom().nextFloat() < chance) {
         summon.setPos(summon.position().add(0.0, 1.5 * summon.getBbHeight(), 0.0));
         summon.setBaby(true);
         summon.setPos(summon.position().add(0.0, -1.5 * summon.getBbHeight(), 0.0));
      }

      IExistence existence = TensuraStorages.getExistenceFrom(summon);
      existence.setTemporaryOwner(entity.getUUID());
      if (summon instanceof ISubordinate subordinate && entity instanceof Player player) {
         subordinate.tame(player);
      }

      existence.markDirty();
   }

   @Override
   public void summonMagicCircle(ManasSkillInstance instance, LivingEntity owner, Vec3 pos, int heldTicks, int mode) {
      MagicCircle circle = new MagicCircle(owner.level(), owner);
      circle.setLife(CONFIG.circleDuration);
      circle.setChargedEnergy(0.0F);
      circle.setPos(pos);
      circle.setSize(5.0F);
      circle.setVariant(MagicCircleVariant.OTHERWORLDER);
      circle.setSkill(instance);
      circle.setMode(mode);
      owner.level().addFreshEntity(circle);
      circle.triggerAnim("controller", "start");
      owner.swing(InteractionHand.MAIN_HAND, true);
   }

   @Override
   public int getSuccessCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return CONFIG.cooldown;
   }

   @Override
   public void onSubordinateDeath(ManasSkillInstance instance, LivingEntity owner, LivingEntity subordinate, DamageSource source) {
   }

   @Override
   public ParticleOptions getSummoningParticle(ManasSkillInstance instance, int mode) {
      return ParticleTypes.ENCHANT;
   }

   @Override
   public SoundEvent getSummoningSound(ManasSkillInstance instance, int mode) {
      return (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get();
   }

   @Override
   public SoundEvent getFailSound(ManasSkillInstance instance, int mode) {
      return (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get();
   }
}
