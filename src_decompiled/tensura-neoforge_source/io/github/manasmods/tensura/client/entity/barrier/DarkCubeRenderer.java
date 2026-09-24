package io.github.manasmods.tensura.client.entity.barrier;

import io.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class DarkCubeRenderer extends BarrierRenderer {
   public DarkCubeRenderer(Context context) {
      super(context);
   }

   protected int getBlockLightLevel(BarrierEntity entity, BlockPos blockPos) {
      return 15;
   }

   @Override
   public ResourceLocation getTextureLocation(BarrierEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/dark_cube.png");
   }
}
