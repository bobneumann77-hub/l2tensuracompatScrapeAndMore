package io.github.manasmods.tensura.entity.ai.goal;

import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.EnumSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class AvoidFearedEntityGoal extends Goal {
   protected final PathfinderMob mob;
   private final double walkSpeedModifier;
   private final double sprintSpeedModifier;
   @Nullable
   protected Entity toAvoid;
   protected final float maxDist;
   @Nullable
   protected Path path;
   protected final PathNavigation pathNav;

   public AvoidFearedEntityGoal(PathfinderMob pMob, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier) {
      this.mob = pMob;
      this.maxDist = pMaxDistance;
      this.walkSpeedModifier = pWalkSpeedModifier;
      this.sprintSpeedModifier = pSprintSpeedModifier;
      this.pathNav = pMob.getNavigation();
      this.setFlags(EnumSet.of(Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))) {
         return false;
      }

      MobEffectInstance instance = this.mob.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FEAR));
      if (instance != null && instance.getAmplifier() >= 4) {
         if (this.mob.level() instanceof ServerLevel level) {
            this.toAvoid = level.getEntity(instance.tensura$getSource());
            if (this.toAvoid == null) {
               return false;
            }

            Vec3 posAway = DefaultRandomPos.getPosAway(this.mob, 20, 7, this.toAvoid.position());
            if (posAway == null) {
               return false;
            }

            if (this.toAvoid.distanceToSqr(posAway.x, posAway.y, posAway.z) < this.toAvoid.distanceToSqr(this.mob)) {
               return false;
            }

            this.path = this.pathNav.createPath(posAway.x, posAway.y, posAway.z, 0);
            return this.path != null;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      return !this.pathNav.isDone();
   }

   public void start() {
      this.pathNav.moveTo(this.path, this.walkSpeedModifier);
   }

   public void stop() {
      this.toAvoid = null;
   }

   public void tick() {
      if (this.toAvoid != null) {
         if (this.mob.distanceToSqr(this.toAvoid) < 100.0) {
            this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
         } else {
            this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
         }

         if (this.mob.getTarget() == this.toAvoid) {
            SubordinateHelper.removeTarget(this.mob);
         }
      }
   }
}
