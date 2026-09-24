package io.github.manasmods.tensura.entity.ai.behaviour.path;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

public class SetSwimTargetToAttackTarget<E extends Mob> extends SetWalkTargetToAttackTarget<E> {
   protected void start(E entity) {
      Brain<?> brain = entity.getBrain();
      LivingEntity target = BrainUtils.getTargetOfEntity(entity);
      if (entity.getSensing().hasLineOfSight(target) && BehaviorUtils.isWithinAttackRange(entity, target, 1)) {
         BrainUtils.clearMemory(brain, MemoryModuleType.WALK_TARGET);
      } else {
         BrainUtils.setMemory(brain, MemoryModuleType.LOOK_TARGET, new EntityTracker(target, true));
         PositionTracker tracker = new BlockPosTracker(entity.level().getHeightmapPos(Types.MOTION_BLOCKING, target.blockPosition()));
         BrainUtils.setMemory(
            brain,
            MemoryModuleType.WALK_TARGET,
            new WalkTarget(tracker, (Float)this.speedMod.apply(entity, target), this.closeEnoughWhen.applyAsInt(entity, target))
         );
      }
   }
}
