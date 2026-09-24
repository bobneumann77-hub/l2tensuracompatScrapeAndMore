package io.github.manasmods.tensura.entity.ai.sensor;

import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.registry.entity.ai.TensuraSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

public class NearbyWantedItemSensor<E extends LivingEntity> extends PredicateSensor<ItemEntity, E> {
   private static final List<MemoryModuleType<?>> MEMORY_REQUIREMENTS = ObjectArrayList.of(new MemoryModuleType[]{MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM});
   protected SquareRadius radius = new SquareRadius(8.0, 3.0);
   private Function<E, Boolean> shouldScan = entity -> true;

   public NearbyWantedItemSensor<E> setRadius(double radius) {
      return this.setRadius(radius, radius);
   }

   public NearbyWantedItemSensor<E> setRadius(double xz, double y) {
      this.radius = new SquareRadius(xz, y);
      return this;
   }

   public NearbyWantedItemSensor<E> shouldScan(Function<E, Boolean> scan) {
      this.shouldScan = scan;
      return this;
   }

   public List<MemoryModuleType<?>> memoriesUsed() {
      return MEMORY_REQUIREMENTS;
   }

   public SensorType<? extends ExtendedSensor<?>> type() {
      return (SensorType<? extends ExtendedSensor<?>>)TensuraSensors.NEARBY_WANTED_ITEM.get();
   }

   protected void doTick(ServerLevel level, E entity) {
      if (!BrainUtils.hasMemory(entity, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM)) {
         if (this.shouldScan.apply(entity)) {
            Vec3 center = entity.position();
            int xzRadius = (int)this.radius.xzRadius();
            int yRadius = (int)this.radius.xzRadius();
            AABB aabb = new AABB(center.add(-xzRadius, -yRadius, -xzRadius), center.add(xzRadius, yRadius, xzRadius));
            ItemEntity items = (ItemEntity)EntityRetrievalUtil.getNearestEntity(
               level, aabb, entity.position(), stack -> stack instanceof ItemEntity item ? this.predicate().test(item, entity) : false
            );
            BrainUtils.setMemory(entity, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, items);
         }
      }
   }

   public static <E extends PlayerLikeEntity> BiPredicate<ItemEntity, E> getProfessionItemPredicate() {
      return (item, entity) -> !entity.hasLineOfSight(item) ? false : entity.shouldPickUpLoot(item.getItem());
   }
}
