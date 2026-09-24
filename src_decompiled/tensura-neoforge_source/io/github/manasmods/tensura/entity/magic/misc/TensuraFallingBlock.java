package io.github.manasmods.tensura.entity.magic.misc;

import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class TensuraFallingBlock extends FallingBlockEntity {
   public boolean indestructible;

   public TensuraFallingBlock(EntityType<? extends TensuraFallingBlock> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public TensuraFallingBlock(Level pLevel) {
      super((EntityType)MiscEntityTypes.FALLING_BLOCK.get(), pLevel);
   }

   public TensuraFallingBlock(Level pLevel, double pX, double pY, double pZ, BlockState pState) {
      this(pLevel);
      this.blockState = pState;
      this.blocksBuilding = true;
      this.setPos(pX, pY, pZ);
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = pX;
      this.yo = pY;
      this.zo = pZ;
      this.setStartPos(this.blockPosition());
   }

   public static TensuraFallingBlock fall(Level pLevel, BlockPos pPos, BlockState pBlockState) {
      TensuraFallingBlock core = new TensuraFallingBlock(
         pLevel,
         pPos.getX() + 0.5,
         pPos.getY(),
         pPos.getZ() + 0.5,
         pBlockState.hasProperty(BlockStateProperties.WATERLOGGED) ? (BlockState)pBlockState.setValue(BlockStateProperties.WATERLOGGED, false) : pBlockState
      );
      pLevel.addFreshEntity(core);
      return core;
   }

   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("Indestructible", this.isIndestructible());
   }

   protected void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setIndestructible(pCompound.getBoolean("Indestructible"));
   }

   public boolean isInvulnerableTo(DamageSource pSource) {
      if (super.isInvulnerableTo(pSource)) {
         return true;
      } else {
         return pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) ? false : this.isIndestructible();
      }
   }

   public void tick() {
      if (this.getBlockState().isAir()) {
         this.discard();
      } else {
         Block block = this.getBlockState().getBlock();
         this.time++;
         if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
         }

         this.move(MoverType.SELF, this.getDeltaMovement());
         if (!this.level().isClientSide) {
            BlockPos blockpos = this.blockPosition();
            boolean flag = this.getBlockState().getBlock() instanceof ConcretePowderBlock;
            boolean flag1 = flag && this.level().getFluidState(blockpos).is(FluidTags.WATER);
            double d0 = this.getDeltaMovement().lengthSqr();
            if (flag && d0 > 1.0) {
               BlockHitResult blockhitresult = this.level()
                  .clip(
                     new ClipContext(
                        new Vec3(this.xo, this.yo, this.zo), this.position(), net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.SOURCE_ONLY, this
                     )
                  );
               if (blockhitresult.getType() != Type.MISS && this.level().getFluidState(blockhitresult.getBlockPos()).is(FluidTags.WATER)) {
                  blockpos = blockhitresult.getBlockPos();
                  flag1 = true;
               }
            }

            if (!this.onGround() && !flag1) {
               if (!this.level().isClientSide
                  && (
                     this.time > 100 && (blockpos.getY() <= this.level().getMinBuildHeight() || blockpos.getY() > this.level().getMaxBuildHeight())
                        || this.time > 600
                  )) {
                  if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                     this.spawnBlockDrops();
                  }

                  this.discard();
               }
            } else {
               BlockState blockstate = this.level().getBlockState(blockpos);
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
               if (!blockstate.is(Blocks.MOVING_PISTON)) {
                  if (this.cancelDrop) {
                     this.discard();
                     this.callOnBrokenAfterFall(block, blockpos);
                  } else {
                     boolean flag2 = blockstate.canBeReplaced(
                        new DirectionalPlaceContext(this.level(), blockpos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)
                     );
                     boolean flag3 = FallingBlock.isFree(this.level().getBlockState(blockpos.below())) && (!flag || !flag1);
                     boolean flag4 = this.getBlockState().canSurvive(this.level(), blockpos) && !flag3;
                     if (flag2 && flag4) {
                        if (this.getBlockState().hasProperty(BlockStateProperties.WATERLOGGED)
                           && this.level().getFluidState(blockpos).getType() == Fluids.WATER) {
                           this.blockState = (BlockState)this.getBlockState().setValue(BlockStateProperties.WATERLOGGED, true);
                        }

                        if (this.level().setBlock(blockpos, this.getBlockState(), 3)) {
                           ((ServerLevel)this.level())
                              .getChunkSource()
                              .chunkMap
                              .broadcast(this, new ClientboundBlockUpdatePacket(blockpos, this.level().getBlockState(blockpos)));
                           this.discard();
                           if (block instanceof Fallable fallable) {
                              fallable.onLand(this.level(), blockpos, this.getBlockState(), blockstate, this);
                           }

                           if (this.blockData != null && this.getBlockState().hasBlockEntity()) {
                              BlockEntity blockentity = this.level().getBlockEntity(blockpos);
                              if (blockentity != null) {
                                 CompoundTag compoundtag = blockentity.saveWithoutMetadata(this.level().registryAccess());

                                 for (String s : this.blockData.getAllKeys()) {
                                    Tag tag = this.blockData.get(s);
                                    if (tag != null) {
                                       compoundtag.put(s, tag.copy());
                                    }
                                 }

                                 blockentity.loadCustomOnly(compoundtag, this.level().registryAccess());
                                 blockentity.setChanged();
                              }
                           }
                        } else if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                           this.discard();
                           this.callOnBrokenAfterFall(block, blockpos);
                           this.spawnBlockDrops();
                        }
                     } else {
                        this.discard();
                        if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                           this.callOnBrokenAfterFall(block, blockpos);
                           this.spawnBlockDrops();
                        }
                     }
                  }
               }
            }
         }

         this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
      }
   }

   private void spawnBlockDrops() {
      if (this.level() instanceof ServerLevel serverLevel) {
         Builder var6 = new Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.blockPosition()))
            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY);

         for (ItemStack drop : this.getBlockState().getDrops(var6)) {
            this.spawnAtLocation(drop);
         }
      }
   }

   @Generated
   public boolean isIndestructible() {
      return this.indestructible;
   }

   @Generated
   public void setIndestructible(boolean indestructible) {
      this.indestructible = indestructible;
   }
}
