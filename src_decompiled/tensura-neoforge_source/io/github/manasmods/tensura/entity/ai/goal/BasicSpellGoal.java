package io.github.manasmods.tensura.entity.ai.goal;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

public class BasicSpellGoal extends Goal {
   protected final Mob entity;
   protected int castWarmupTime;
   protected int castingInterval;
   protected int attackWarmupDelay;
   protected int nextAttackTickCount;

   public BasicSpellGoal(Mob creature, int castWarmUpTime, int castingInterval) {
      this.entity = creature;
      this.castWarmupTime = castWarmUpTime;
      this.castingInterval = castingInterval;
   }

   public boolean canUse() {
      if (!this.entity.isAlive()) {
         return false;
      }

      LivingEntity livingentity = this.entity.getTarget();
      return (livingentity == null || !livingentity.isAlive()) && !this.noTargetActivation() ? false : this.entity.tickCount >= this.nextAttackTickCount;
   }

   public boolean canContinueToUse() {
      LivingEntity livingentity = this.entity.getTarget();
      return (livingentity == null || !livingentity.isAlive()) && !this.noTargetActivation() ? false : this.attackWarmupDelay > 0;
   }

   public void start() {
      this.attackWarmupDelay = this.adjustedTickDelay(this.castWarmupTime);
      this.nextAttackTickCount = this.entity.tickCount + this.getCastingInterval();
      SoundEvent sound = this.getSpellPrepareSound();
      if (sound != null) {
         this.entity.playSound(sound, 1.0F, 1.0F);
      }
   }

   public void tick() {
      this.attackWarmupDelay--;
      if (this.attackWarmupDelay == 0) {
         this.performSpellCasting();
         SoundEvent sound = this.getCastingSoundEvent();
         if (sound != null) {
            this.entity.playSound(sound, 1.0F, 1.0F);
         }
      }
   }

   protected void performSpellCasting() {
   }

   protected boolean noTargetActivation() {
      return false;
   }

   protected int getCastingInterval() {
      return this.castingInterval;
   }

   @Nullable
   protected SoundEvent getSpellPrepareSound() {
      return null;
   }

   @Nullable
   protected SoundEvent getCastingSoundEvent() {
      return null;
   }
}
