package io.github.manasmods.tensura.entity.ai.behaviour.attack;

import java.util.function.Function;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.tslat.smartbrainlib.util.BrainUtils;

public class CrossbowAttack<E extends LivingEntity & RangedAttackMob> extends CustomDelayRangedAttack<E> {
   private final InteractionHand hand;
   protected Function<E, Float> shootPower = entity -> BowItem.getPowerForTime(entity.getTicksUsingItem());

   public CrossbowAttack(InteractionHand hand) {
      this.hand = hand;
   }

   public final CrossbowAttack<E> shootPower(Function<E, Float> shootPower) {
      this.shootPower = shootPower;
      return this;
   }

   @Override
   protected void start(E entity) {
      BehaviorUtils.lookAtEntity(entity, this.target);
      entity.startUsingItem(this.hand);
   }

   @Override
   protected void doDelayedAction(E entity) {
      if (this.target != null) {
         ChargedProjectiles charged = (ChargedProjectiles)entity.getMainHandItem().get(DataComponents.CHARGED_PROJECTILES);
         if (charged != null && !charged.isEmpty()) {
            if (!BrainUtils.canSee(entity, this.target) || entity.distanceToSqr(this.target) > this.attackRadius) {
               return;
            }

            entity.performRangedAttack(this.target, this.shootPower.apply(entity));
            entity.stopUsingItem();
            BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.attackIntervalSupplier.apply(entity));
         } else {
            entity.releaseUsingItem();
         }
      }
   }
}
