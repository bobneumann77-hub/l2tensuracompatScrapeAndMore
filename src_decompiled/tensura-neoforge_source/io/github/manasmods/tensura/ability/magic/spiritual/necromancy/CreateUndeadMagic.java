package io.github.manasmods.tensura.ability.magic.spiritual.necromancy;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.ability.magic.summon.ISummoning;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class CreateUndeadMagic<T extends Mob> extends NecromancyMagic implements ISummoning<T> {
   public CreateUndeadMagic(SpiritualMagic.SpiritLevel level) {
      super(level);
   }

   protected int getSpawnNumber(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return 1;
   }

   @Override
   public void addHeldAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (!instance.onCoolDown(mode)) {
         super.addHeldAttributeModifiers(instance, entity, mode);
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      this.startSummoning(instance, entity, mode);
   }

   protected String getSummonId(int index) {
      return index == 0 ? "SummonUUID" : "SummonUUID_" + index;
   }

   @Override
   public void startSummoning(ManasSkillInstance instance, LivingEntity entity, int mode) {
      int number = this.getSpawnNumber(instance, entity, mode);
      BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, 10.0);
      Vec3 pos = result.getLocation();
      CompoundTag tag = instance.getOrCreateTag();

      for (int i = 0; i < number; i++) {
         tag.remove(this.getSummonId(i));
      }

      tag.putDouble("circleX", pos.x);
      tag.putDouble("circleY", pos.y);
      tag.putDouble("circleZ", pos.z);
      instance.markDirty();
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

      int castTime = this.getCastingTime(instance, entity);
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      Vec3 vec3 = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY"), tag.getDouble("circleZ"));
      BlockPos pos = ObjectSelectionHelper.getBlockPos(vec3);
      if (level.getBlockState(pos.below()).isSolid() && level.getBlockState(pos.below(2)).isSolid()) {
         this.summonMagicCircle(instance, entity, vec3, heldTicks, mode);
         if (heldTicks >= castTime) {
            int summoningTime = heldTicks - castTime;
            int number = this.getSpawnNumber(instance, entity, mode);

            for (int i = 0; i < number; i++) {
               double angle = (Math.PI * 2) / number * i;
               double dx = number == 1 ? 0.0 : Math.cos(angle) * number * 0.5;
               double dz = number == 1 ? 0.0 : Math.sin(angle) * number * 0.5;
               if (heldTicks == castTime + 1) {
                  this.createSummon(instance, entity, mode, vec3.add(dx, 0.0, dz), this.getSummonId(i));
               }

               if (tag.hasUUID(this.getSummonId(i))) {
                  this.callForthSummon(instance, entity, mode, tag.getUUID(this.getSummonId(i)), summoningTime);
               }
            }

            this.applyCastingVisual(instance, entity, heldTicks, mode);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), this.getSummoningSound(instance, mode), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            return summoningTime < 40;
         } else {
            this.applyCastingVisual(instance, entity, heldTicks, mode);
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean callForthSummon(ManasSkillInstance instance, LivingEntity entity, int mode, UUID summonUUID, int summoningTime) {
      Level level = entity.level();
      Entity summon = ((ServerLevel)level).getEntity(summonUUID);
      if (summon instanceof Mob mob) {
         summon.setPos(summon.position().add(0.0, mob.getBbHeight() * 1.5 / 39.0, 0.0));
         TensuraParticleHelper.addServerParticlesAroundSelf(mob, this.getSummoningParticle(instance, mode));
         mob.lookAt(entity, 30.0F, 30.0F);
         if (summoningTime == 40) {
            if (this.isSummoningRequirementFulfilled(instance, entity, (T)mob, mode)) {
               instance.addMasteryPoint(entity);
               summon.noPhysics = false;
               mob.setNoAi(false);
               mob.playSound(this.getSummoningSound(instance, mode), 3.0F, 1.0F);
               this.onPostSummon(instance, entity, (T)mob, mode);
               instance.setCoolDowns(this.getSuccessCooldown(instance, entity));
               return true;
            }

            this.removeFailedSummon(instance, entity, mode);
            return false;
         }

         mob.noPhysics = true;
      }

      return false;
   }

   @Override
   public void onPostSummon(ManasSkillInstance instance, LivingEntity entity, T summon, int mode) {
      this.removeAttributeModifiers(instance, entity, mode);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      this.removeFailedSummon(instance, entity, mode);
   }

   @Override
   public void removeFailedSummon(ManasSkillInstance instance, LivingEntity entity, int mode) {
      CompoundTag tag = instance.getTag();
      if (tag != null) {
         int number = this.getSpawnNumber(instance, entity, mode);

         for (int i = 0; i < number; i++) {
            if (tag.hasUUID(this.getSummonId(i))) {
               Entity summon = ((ServerLevel)entity.level()).getEntity(tag.getUUID(this.getSummonId(i)));
               if (summon instanceof Mob mob && mob.isNoAi()) {
                  summon.discard();
                  mob.playSound(this.getFailSound(instance, mode), 3.0F, 1.0F);
                  TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.SQUID_INK, 0.5);
                  TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.SQUID_INK, 1.0);
                  tag.remove(this.getSummonId(i));
                  instance.markDirty();
               }
            }
         }
      }
   }

   @Override
   public void summonMagicCircle(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, int heldTicks, int mode) {
      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      MagicCircle.castMagicCircle(
         this.getSpawnNumber(instance, entity, mode), 30, pos, MagicCircleVariant.NECROMANCY, entity, instance.getOrCreateTag(), instance, mode, cost
      );
   }
}
