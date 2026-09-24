package io.github.manasmods.tensura.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.Nullable;

public class FollowSwarmLeaderGoal extends Goal {
   private final PathfinderMob mob;
   private final Class<? extends LivingEntity> leaderEntityType;
   private int timeToRecalcPath;
   private int nextStartTick;
   private LivingEntity leader = null;

   public FollowSwarmLeaderGoal(PathfinderMob entity, Class<? extends LivingEntity> leaderEntityType) {
      this.mob = entity;
      this.leaderEntityType = leaderEntityType;
      this.nextStartTick = this.nextStartTick(entity);
   }

   protected int nextStartTick(LivingEntity arg) {
      return reducedTickDelay(200 + arg.getRandom().nextInt(200) % 20);
   }

   public boolean canUse() {
      if (this.nextStartTick > 0) {
         this.nextStartTick--;
         return false;
      }

      this.nextStartTick = this.nextStartTick(this.mob);
      LivingEntity leaderEntity = this.getNearestLeader();
      if (leaderEntity == null) {
         return false;
      }

      this.leader = leaderEntity;
      return true;
   }

   @Nullable
   private LivingEntity getNearestLeader() {
      return this.mob
         .level()
         .getNearestEntity(
            this.leaderEntityType,
            TargetingConditions.forNonCombat(),
            this.mob,
            this.mob.getX(),
            this.mob.getY(),
            this.mob.getZ(),
            this.mob.getBoundingBox().inflate(32.0, 32.0, 32.0)
         );
   }

   public boolean canContinueToUse() {
      return this.leader != null && this.leader.isAlive();
   }

   public void start() {
      this.timeToRecalcPath = 0;
   }

   public void stop() {
      this.leader = null;
   }

   public void tick() {
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.adjustedTickDelay(10);
         this.mob.getNavigation().moveTo(this.leader, 1.2);
      }
   }
}
