package io.github.manasmods.tensura.block.template;

import com.mojang.serialization.MapCodec;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.storage.Alignment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LivingEntity.Fallsounds;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.tslat.smartbrainlib.object.TriPredicate;
import org.jetbrains.annotations.NotNull;

public class LooseBlock extends Block {
   public static final MapCodec<LooseBlock> CODEC = simpleCodec(LooseBlock::new);
   private static final VoxelShape FALLING_COLLISION_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9F, 1.0);

   public MapCodec<LooseBlock> codec() {
      return CODEC;
   }

   public LooseBlock(Properties properties) {
      super(properties);
   }

   protected boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
      return blockState2.is(this) || super.skipRendering(blockState, blockState2, direction);
   }

   @NotNull
   protected VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      if (collisionContext instanceof EntityCollisionContext entityCollisionContext) {
         Entity entity = entityCollisionContext.getEntity();
         if (entity != null && entity.fallDistance > 2.5F) {
            return FALLING_COLLISION_SHAPE;
         }
      }

      return Shapes.empty();
   }

   protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if (entity instanceof LivingEntity living) {
         if (entity.getInBlockState().is(this)) {
            entity.makeStuckInBlock(blockState, new Vec3(0.5, 0.5, 0.5));
            if (entity.getAirSupply() >= entity.getMaxAirSupply() && shouldLoseAir(living)) {
               entity.setAirSupply(entity.getMaxAirSupply() - 10);
            }
         }
      } else {
         entity.makeStuckInBlock(blockState, new Vec3(0.5, 0.5, 0.5));
      }
   }

   public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float f) {
      if (f >= 4.0F && entity instanceof LivingEntity livingEntity) {
         Fallsounds fallsounds = livingEntity.getFallSounds();
         SoundEvent soundEvent = f < 7.0F ? fallsounds.small() : fallsounds.big();
         entity.playSound(soundEvent, 1.0F, 1.0F);
      }
   }

   protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
      return true;
   }

   public static boolean shouldLoseAir(LivingEntity entity) {
      return !Alignment.shouldConsumeAir(entity) ? false : isInBlock(entity, (state, pos, target) -> state.is(TensuraBlockTags.LOOSE_BLOCKS));
   }

   public static boolean isInBlock(Entity entity, TriPredicate<BlockState, BlockPos, Entity> predicate) {
      float f = entity.getBbWidth() * 0.8F;
      AABB aabb = AABB.ofSize(entity.getEyePosition(), f, 0.0, f);
      return BlockPos.betweenClosedStream(aabb).anyMatch(pos -> {
         BlockState blockstate = entity.level().getBlockState(pos);
         return !blockstate.isAir() && predicate.test(blockstate, pos, entity);
      });
   }
}
