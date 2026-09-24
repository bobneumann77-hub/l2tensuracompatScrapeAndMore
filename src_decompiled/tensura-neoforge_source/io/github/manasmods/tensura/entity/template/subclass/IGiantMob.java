package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.magic.spike.SpikeEntity;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public interface IGiantMob {
   default int getBlockBrokenLimit() {
      return 8000;
   }

   default int getGriefInterval() {
      return 5;
   }

   default boolean shouldGrief(LivingEntity entity) {
      int interval = this.getGriefInterval();
      return interval <= 1 || entity.tickCount % interval == 0;
   }

   default int breakBlocks(LivingEntity entity, float inflation, boolean breakFloor) {
      return this.breakBlocks(entity, inflation, breakFloor, null);
   }

   default int breakBlocks(LivingEntity entity, float inflation, boolean breakFloor, @Nullable SimpleContainer container) {
      return this.breakBlocks(entity, inflation, breakFloor, 0, container);
   }

   default int breakBlocks(LivingEntity entity, float inflation, boolean breakFloor, int yStep, @Nullable SimpleContainer container) {
      return this.breakBlocks(entity, inflation, breakFloor, yStep, container, false);
   }

   default int breakBlocks(LivingEntity entity, float inflation, boolean breakFloor, int yStep, @Nullable SimpleContainer container, boolean ignoreCollision) {
      if (this.cantBreakBlock(entity, ignoreCollision)) {
         return 0;
      }

      if (!this.shouldGrief(entity)) {
         return 0;
      }

      AABB aabb = entity.getBoundingBox().inflate(inflation);
      Level level = entity.level();
      ServerLevel serverLevel = level instanceof ServerLevel sl ? sl : null;
      List<SpikeEntity> list = level.getEntitiesOfClass(SpikeEntity.class, aabb);
      if (!list.isEmpty()) {
         for (SpikeEntity spike : list) {
            spike.onBreak();
         }
      }

      int floorY = Mth.floor(aabb.minY) + yStep;
      if (!breakFloor) {
         floorY += (int)inflation;
      }

      int brokenBlocks = 0;

      for (BlockPos pos : ObjectSelectionHelper.getBlockCappedIteration(
         BlockPos.betweenClosed(Mth.floor(aabb.minX), floorY, Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY) + yStep, Mth.floor(aabb.maxZ)),
         this.getBlockBrokenLimit()
      )) {
         if (level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && this.breakableBlocks(entity, pos, state)) {
               boolean drop = this.dropBlockLoot(entity, state);
               if (container == null) {
                  if (state.getFluidState().isSource()) {
                     level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                  } else {
                     level.destroyBlock(pos, drop, entity);
                  }
               } else if (serverLevel != null) {
                  for (ItemStack item : blockDrops(entity, entity.getMainHandItem(), serverLevel, pos, state)) {
                     if (container.canAddItem(item)) {
                        container.addItem(item);
                     } else if (drop) {
                        serverLevel.addFreshEntity(new ItemEntity(serverLevel, pos.getX(), pos.getY(), pos.getZ(), item));
                     }
                  }

                  if (state.getFluidState().isSource()) {
                     level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                  } else {
                     level.destroyBlock(pos, false, entity);
                  }
               }

               brokenBlocks++;
            }
         }
      }

      return brokenBlocks;
   }

   default void digBlocks(LivingEntity entity, float inflation, int height, float extraXZ, boolean breakFloor, @Nullable SimpleContainer container) {
      if (!this.cantBreakBlock(entity, false)) {
         if (this.shouldGrief(entity)) {
            AABB aabb = entity.getBoundingBox().inflate(inflation);
            Level level = entity.level();
            ServerLevel serverLevel = level instanceof ServerLevel sl ? sl : null;
            int floorY = Mth.floor(aabb.minY) + height;
            int ceilY = Mth.floor(aabb.maxY) + height;
            if (!breakFloor) {
               floorY += (int)inflation;
            }

            float angle = (float) (Math.PI / 180.0) * entity.yBodyRot;
            double extraX = extraXZ * Mth.sin((float)(Math.PI + angle));
            double extraZ = extraXZ * Mth.cos(angle);

            for (BlockPos pos : ObjectSelectionHelper.getBlockCappedIteration(
               BlockPos.betweenClosed(
                  Mth.floor(aabb.minX + extraX), floorY, Mth.floor(aabb.minZ + extraZ), Mth.floor(aabb.maxX + extraX), ceilY, Mth.floor(aabb.maxZ + extraZ)
               ),
               this.getBlockBrokenLimit()
            )) {
               if (level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                  BlockState state = level.getBlockState(pos);
                  if (!state.isAir() && this.diggableBlocks(entity, pos, state)) {
                     boolean drop = this.dropBlockLoot(entity, state);
                     if (container == null) {
                        level.destroyBlock(pos, drop, entity);
                     } else if (serverLevel != null) {
                        for (ItemStack item : blockDrops(entity, entity.getMainHandItem(), serverLevel, pos, state)) {
                           if (container.canAddItem(item)) {
                              container.addItem(item);
                           } else if (drop) {
                              serverLevel.addFreshEntity(new ItemEntity(serverLevel, pos.getX(), pos.getY(), pos.getZ(), item));
                           }
                        }

                        level.destroyBlock(pos, false, entity);
                     }
                  }
               }
            }
         }
      }
   }

   default boolean removeFluid(LivingEntity entity, TagKey<Fluid> tag, float inflation, boolean breakFloor, int yStep) {
      if (this.cantBreakBlock(entity, true)) {
         return false;
      }

      if (!this.shouldGrief(entity)) {
         return false;
      }

      AABB aabb = entity.getBoundingBox().inflate(inflation);
      Level level = entity.level();
      int floorY = Mth.floor(aabb.minY) + yStep;
      if (!breakFloor) {
         floorY += (int)inflation;
      }

      boolean success = false;

      for (BlockPos pos : ObjectSelectionHelper.getBlockCappedIteration(
         BlockPos.betweenClosed(Mth.floor(aabb.minX), floorY, Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY) + yStep, Mth.floor(aabb.maxZ)),
         this.getBlockBrokenLimit()
      )) {
         if (level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
            FluidState fluidState = level.getFluidState(pos);
            if (!fluidState.isEmpty() && fluidState.is(tag)) {
               BlockState state = level.getBlockState(pos);
               if (fluidState.isSource()) {
                  level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
               } else if (state.hasProperty(BlockStateProperties.WATERLOGGED) && (Boolean)state.getValue(BlockStateProperties.WATERLOGGED)) {
                  level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, false), 11);
               }

               success = true;
            }
         }
      }

      return success;
   }

   default boolean breakableBlocks(LivingEntity entity, BlockPos pos, BlockState state) {
      return state.is(TensuraBlockTags.BREAKABLE_BY_MONSTER);
   }

   default boolean diggableBlocks(LivingEntity entity, BlockPos pos, BlockState state) {
      return state.is(TensuraBlockTags.DIGGABLE_BY_MONSTER);
   }

   default boolean cantBreakBlock(LivingEntity entity, boolean ignoreCollision) {
      if (!entity.isAlive()) {
         return true;
      } else if (entity.isBaby()) {
         return true;
      } else if (!entity.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         return true;
      } else if (this.isColliding(entity, ignoreCollision)) {
         return false;
      } else {
         LivingEntity controllingPassenger = entity.getControllingPassenger();
         if (controllingPassenger == null) {
            return true;
         } else {
            return ObjectSelectionHelper.getPlayerPOVHitResult(
                     entity.level(), entity, net.minecraft.world.level.ClipContext.Fluid.NONE, 1.0F + entity.getBbWidth() / 2.0F
                  )
                  .getType()
                  .equals(Type.BLOCK)
               ? false
               : !ObjectSelectionHelper.getPlayerPOVHitResult(
                     entity.level(), controllingPassenger, net.minecraft.world.level.ClipContext.Fluid.NONE, 2.0F + entity.getBbWidth() / 2.0F
                  )
                  .getType()
                  .equals(Type.BLOCK);
         }
      }
   }

   default boolean dropBlockLoot(LivingEntity entity, BlockState state) {
      return true;
   }

   default boolean isColliding(LivingEntity entity, boolean ignore) {
      return ignore ? true : entity.horizontalCollision || entity.verticalCollision && !entity.verticalCollisionBelow;
   }

   static List<ItemStack> blockDrops(LivingEntity entity, ItemStack tool, ServerLevel level, BlockPos pos, BlockState state) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      return Block.getDrops(state, level, pos, blockEntity, entity, tool);
   }
}
