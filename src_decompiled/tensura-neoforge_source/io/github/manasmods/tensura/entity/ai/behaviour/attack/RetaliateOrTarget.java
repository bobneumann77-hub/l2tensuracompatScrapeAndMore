package io.github.manasmods.tensura.entity.ai.behaviour.attack;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.EntityEvents.LivingChangeTargetEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

public class RetaliateOrTarget<E extends Mob> extends TargetOrRetaliate<E> {
   protected void start(E entity) {
      Changeable<LivingEntity> target = Changeable.of(this.toTarget);
      if (!((LivingChangeTargetEvent)EntityEvents.LIVING_CHANGE_TARGET.invoker()).changeTarget(entity, target).isFalse()) {
         LivingEntity existingTarget = BrainUtils.getTargetOfEntity(entity);
         BrainUtils.setTargetOfEntity(entity, (LivingEntity)target.get());
         BrainUtils.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
         if (target.get() != null && this.alertAlliesPredicate.test(entity, (Entity)target.get()) && existingTarget == null) {
            this.alertAllies((ServerLevel)entity.level(), entity);
         }
      }

      this.toTarget = null;
   }

   protected void alertAllies(ServerLevel level, E owner) {
      double followRange = owner.getAttributeValue(Attributes.FOLLOW_RANGE);

      for (LivingEntity ally : EntityRetrievalUtil.getEntities(
         owner, followRange, 10.0, followRange, LivingEntity.class, entity -> this.allyPredicate.test(owner, entity)
      )) {
         Changeable<LivingEntity> target = Changeable.of(this.toTarget);
         if (!((LivingChangeTargetEvent)EntityEvents.LIVING_CHANGE_TARGET.invoker()).changeTarget(ally, target).isFalse() && target.get() != null) {
            BrainUtils.setTargetOfEntity(ally, this.toTarget);
         }
      }
   }
}
