package io.github.manasmods.tensura.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.block.entity.OrcDisasterHeadBlockEntity;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class OrcDisasterHeadRenderer extends GeoBlockRenderer<OrcDisasterHeadBlockEntity> {
   private Integer rotation = 0;
   private Level level;
   private BlockPos blockPos;

   public OrcDisasterHeadRenderer(Context context) {
      super(new DefaultedBlockGeoModel<OrcDisasterHeadBlockEntity>(ResourceLocation.fromNamespaceAndPath("tensura", "orc_disaster_head")) {
         public ResourceLocation getTextureResource(OrcDisasterHeadBlockEntity object) {
            return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/orc_disaster.png");
         }
      });
   }

   public void render(
      OrcDisasterHeadBlockEntity tile, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      this.rotation = (Integer)tile.getBlockState().getValue(BlockStateProperties.ROTATION_16);
      this.level = tile.getLevel();
      this.blockPos = tile.getBlockPos();
      super.render(tile, partialTicks, poseStack, bufferSource, packedLight, packedOverlay);
   }

   protected void rotateBlock(Direction facing, PoseStack poseStack) {
      if (this.level != null && this.level.getBlockState(this.blockPos).getBlock() == TensuraBlocks.ORC_DISASTER_HEAD.get()) {
         if (facing != Direction.UP && facing != Direction.DOWN) {
            super.rotateBlock(facing, poseStack);
            poseStack.translate(0.0, 0.2, 0.2);
         } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(this.rotation.intValue() * 22.5F * -1.0F));
            poseStack.translate(0.0, -0.01, 0.0);
         }
      }
   }
}
