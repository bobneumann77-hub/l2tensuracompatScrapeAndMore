package io.github.manasmods.tensura.entity.ai.goal;

import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class TensuraOwnerHurtByTargetGoal extends TargetGoal {
   private final Mob entity;
   private LivingEntity ownerLastHurtBy;
   private int timestamp;

   public TensuraOwnerHurtByTargetGoal(Mob mob) {
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
            this.ownerLastHurtBy = owner.getLastHurtByMob();
            int i = owner.getLastHurtByMobTimestamp();
            if (i == this.timestamp) {
               return false;
            } else {
               return !this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT)
                  ? false
                  : this.ownerLastHurtBy != null && !this.entity.isAlliedTo(this.ownerLastHurtBy);
            }
         }
      }
   }

   public void start() {
      this.mob.setTarget(this.ownerLastHurtBy);
      LivingEntity owner = SubordinateHelper.getSubordinateOwner(this.entity);
      if (owner != null) {
         this.timestamp = owner.getLastHurtByMobTimestamp();
      }

      super.start();
   }
}
