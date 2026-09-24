package io.github.manasmods.tensura.entity.ai.goal;

import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class TensuraOwnerHurtGoal extends TargetGoal {
   private final Mob entity;
   private LivingEntity ownerLastHurt;
   private int timestamp;

   public TensuraOwnerHurtGoal(Mob mob) {
      super(mob, false);
      this.entity = mob;
      this.setFlags(EnumSet.of(Flag.TARGET));
   }

   public boolean canUse() {
      if (this.entity instanceof ISubordinate) {
         return false;
      } else {
         LivingEntity owner = SubordinateHelper.getSubordinateOwner(this.entity);
         if (owner == null) {
            return false;
         } else {
            this.ownerLastHurt = owner.getLastHurtMob();
            int i = owner.getLastHurtMobTimestamp();
            if (i == this.timestamp) {
               return false;
            } else {
               return !this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT)
                  ? false
                  : this.ownerLastHurt != null && !this.entity.isAlliedTo(this.ownerLastHurt);
            }
         }
      }
   }

   public void start() {
      this.mob.setTarget(this.ownerLastHurt);
      LivingEntity owner = SubordinateHelper.getSubordinateOwner(this.entity);
      if (owner != null) {
         this.timestamp = owner.getLastHurtMobTimestamp();
      }

      super.start();
   }
}
