package io.github.manasmods.tensura.entity.ai.behaviour.path;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class SetWalkTargetToWantedItem<E extends LivingEntity> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(3)
      .usesMemories(new MemoryModuleType[]{MemoryModuleType.WALK_TARGET, MemoryModuleType.ATTACK_TARGET})
      .hasMemories(new MemoryModuleType[]{MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM});

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected void start(E entity) {
      ItemEntity item = (ItemEntity)BrainUtils.getMemory(entity, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
      if (item != null && item.isAlive()) {
         if (entity.distanceToSqr(item) < 1.0) {
            BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
         } else {
            BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(item, false));
            BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(item, 1.0F, 1));
         }
      } else {
         BrainUtils.clearMemory(entity, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
      }
   }
}
