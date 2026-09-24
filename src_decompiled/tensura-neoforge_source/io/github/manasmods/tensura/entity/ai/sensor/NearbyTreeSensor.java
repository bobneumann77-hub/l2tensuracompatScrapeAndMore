package io.github.manasmods.tensura.entity.ai.sensor;

import io.github.manasmods.tensura.registry.entity.ai.TensuraMemoryModules;
import io.github.manasmods.tensura.registry.entity.ai.TensuraSensors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;

public class NearbyTreeSensor<E extends LivingEntity> extends ExtendedSensor<E> {
   private static final List<MemoryModuleType<?>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
      new MemoryModuleType[]{(MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get(), (MemoryModuleType)TensuraMemoryModules.BUILDING_LOGS.get()}
   );
   protected SquareRadius radius = new SquareRadius(12.0, 3.0);
   private Function<E, Boolean> shouldScan = entity -> true;
   private BiFunction<E, BlockPos, Boolean> isTreePlantable = (entity, pos) -> entity.level().getBlockState(pos).is(BlockTags.DIRT);
   private BiFunction<E, BlockPos, Boolean> isLog = (entity, pos) -> entity.level().getBlockState(pos).is(BlockTags.LOGS);
   private BiFunction<E, BlockPos, Boolean> isLeaves = (entity, pos) -> entity.level().getBlockState(pos).is(BlockTags.LEAVES);

   public NearbyTreeSensor() {
      this.setScanRate(entity -> 100);
   }

   public NearbyTreeSensor<E> setRadius(double radius) {
      return this.setRadius(radius, radius);
   }

   public NearbyTreeSensor<E> setRadius(double xz, double y) {
      this.radius = new SquareRadius(xz, y);
      return this;
   }

   public NearbyTreeSensor<E> shouldScan(Function<E, Boolean> scan) {
      this.shouldScan = scan;
      return this;
   }

   public NearbyTreeSensor<E> setTreePlantable(BiFunction<E, BlockPos, Boolean> function) {
      this.isTreePlantable = function;
      return this;
   }

   public NearbyTreeSensor<E> setLog(BiFunction<E, BlockPos, Boolean> function) {
      this.isLog = function;
      return this;
   }

   public NearbyTreeSensor<E> setLeaves(BiFunction<E, BlockPos, Boolean> function) {
      this.isLeaves = function;
      return this;
   }

   public List<MemoryModuleType<?>> memoriesUsed() {
      return MEMORY_REQUIREMENTS;
   }

   public SensorType<? extends ExtendedSensor<?>> type() {
      return (SensorType<? extends ExtendedSensor<?>>)TensuraSensors.NEARBY_TREE.get();
   }

   protected void doTick(@NotNull ServerLevel level, @NotNull E entity) {
      if (!BrainUtils.hasMemory(entity, (MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get())) {
         if (this.shouldScan.apply(entity)) {
            Set<BlockPos> notTrees = (Set<BlockPos>)BrainUtils.memoryOrDefault(entity, (MemoryModuleType)TensuraMemoryModules.BUILDING_LOGS.get(), HashSet::new);
            BlockPos center = entity.blockPosition();
            int xzRadius = (int)this.radius.xzRadius();
            int yRadius = (int)this.radius.xzRadius();
            int cx = center.getX();
            int cy = center.getY();
            int cz = center.getZ();
            MutableBlockPos cursor = new MutableBlockPos();
            MutableBlockPos below = new MutableBlockPos();
            List<BlockPos> canChop = new ObjectArrayList();

            for (int dx = -xzRadius; dx <= xzRadius; dx++) {
               for (int dy = -yRadius; dy <= yRadius; dy++) {
                  for (int dz = -xzRadius; dz <= xzRadius; dz++) {
                     cursor.set(cx + dx, cy + dy, cz + dz);
                     if (this.isLog.apply(entity, cursor)) {
                        below.set(cx + dx, cy + dy - 1, cz + dz);
                        if (this.isTreePlantable.apply(entity, below)) {
                           BlockPos imm = cursor.immutable();
                           if (!notTrees.contains(imm)) {
                              canChop.add(imm);
                           }
                        }
                     }
                  }
               }
            }

            boolean foundTree = false;
            if (!canChop.isEmpty()) {
               canChop.sort(new NearbyTreeSensor.Sorter(entity));

               for (BlockPos tree : canChop) {
                  if (this.isTree(entity, tree)) {
                     boolean isReachable = false;

                     for (Direction direction : Plane.HORIZONTAL) {
                        if (level.isEmptyBlock(tree.relative(direction))) {
                           isReachable = true;
                           break;
                        }
                     }

                     if (isReachable) {
                        BrainUtils.setMemory(entity, (MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get(), tree);
                        foundTree = true;
                        break;
                     }
                  } else {
                     notTrees.add(tree);
                     BrainUtils.setMemory(entity, (MemoryModuleType)TensuraMemoryModules.BUILDING_LOGS.get(), notTrees);
                  }
               }
            }

            if (!foundTree) {
               BrainUtils.clearMemory(entity, (MemoryModuleType)TensuraMemoryModules.NEAREST_TREE.get());
            }
         }
      }
   }

   private boolean isTree(E entity, BlockPos tree) {
      if (this.isLog.apply(entity, tree)) {
         BlockPos top = new BlockPos(tree);

         while (!entity.level().isEmptyBlock(top.above()) && top.getY() < entity.level().getMaxBuildHeight()) {
            top = top.above();
         }

         if (this.isLeaves.apply(entity, top)) {
            BlockPos log = this.getTreeStump(entity, top);
            return this.isLog.apply(entity, log);
         }
      }

      return false;
   }

   private BlockPos getTreeStump(E entity, BlockPos log) {
      MutableBlockPos cursor = new MutableBlockPos();
      MutableBlockPos below = new MutableBlockPos();
      int lx = log.getX();
      int ly = log.getY();
      int lz = log.getZ();

      for (int dx = -4; dx <= 4; dx++) {
         for (int dy = -4; dy <= 0; dy++) {
            for (int dz = -4; dz <= 4; dz++) {
               cursor.set(lx + dx, ly + dy, lz + dz);
               below.set(lx + dx, ly + dy - 1, lz + dz);
               if (this.isLog.apply(entity, below) || this.isLeaves.apply(entity, below)) {
                  return this.getTreeStump(entity, below.immutable());
               }
            }
         }
      }

      return log;
   }

   public static class Sorter implements Comparator<BlockPos> {
      private final Entity entity;

      public Sorter(Entity entity) {
         this.entity = entity;
      }

      public int compare(BlockPos pos1, BlockPos pos2) {
         return Double.compare(this.getDistance(pos1), this.getDistance(pos2));
      }

      private double getDistance(BlockPos pos) {
         double x = this.entity.getX() - (pos.getX() + 0.5);
         double y = this.entity.getEyeY() - (pos.getY() + 0.5);
         double z = this.entity.getZ() - (pos.getZ() + 0.5);
         return x * x + y * y + z * z;
      }
   }
}
