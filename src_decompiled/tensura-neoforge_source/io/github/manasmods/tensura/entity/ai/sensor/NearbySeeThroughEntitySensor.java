package io.github.manasmods.tensura.entity.ai.sensor;

import java.util.Comparator;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

public class NearbySeeThroughEntitySensor<E extends LivingEntity> extends NearbyLivingEntitySensor<E> {
   protected void doTick(ServerLevel level, E entity) {
      SquareRadius radius = this.radius;
      if (radius == null) {
         double dist = entity.getAttributeValue(Attributes.FOLLOW_RANGE);
         radius = new SquareRadius(dist, dist);
      }

      List<LivingEntity> entities = EntityRetrievalUtil.getEntities(
         entity, radius.xzRadius(), radius.yRadius(), radius.xzRadius(), LivingEntity.class, livingEntity -> this.predicate().test(livingEntity, entity)
      );
      entities.sort(Comparator.comparingDouble(entity::distanceToSqr));
      BrainUtils.setMemory(entity, MemoryModuleType.NEAREST_LIVING_ENTITIES, entities);
      BrainUtils.setMemory(entity, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, new SeeThroughNearestVisibleLivingEntities(entity, entities));
   }
}
