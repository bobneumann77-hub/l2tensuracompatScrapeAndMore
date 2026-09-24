package io.github.manasmods.tensura.block.entity;

import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class OrcDisasterHeadBlockEntity extends BlockEntity implements GeoBlockEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public OrcDisasterHeadBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType)TensuraBlockEntities.ORC_DISASTER_HEAD.get(), pPos, pBlockState);
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
