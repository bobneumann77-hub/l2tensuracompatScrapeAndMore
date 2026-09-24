package io.github.manasmods.tensura.entity.ai.behaviour.attack;

import java.util.function.BiConsumer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

public class InvalidateNeutralAttackTarget<E extends LivingEntity & NeutralMob> extends InvalidateAttackTarget<E> {
   protected BiConsumer<E, LivingEntity> onInvalidate = (entity, target) -> entity.forgetCurrentTargetAndRefreshUniversalAnger();

   public InvalidateNeutralAttackTarget<E> onInvalidate(BiConsumer<E, LivingEntity> consumer) {
      this.onInvalidate = consumer;
      return this;
   }

   protected void start(E entity) {
      LivingEntity target = BrainUtils.getTargetOfEntity(entity);
      if (target != null) {
         if (this.isTargetInvalid(entity, target)
            || !this.canAttack(entity, target)
            || this.isTiredOfPathing(entity)
            || this.customPredicate.test(entity, target)) {
            this.onInvalidate.accept(entity, target);
            BrainUtils.clearMemory(entity, MemoryModuleType.ATTACK_TARGET);
         }
      }
   }
}
