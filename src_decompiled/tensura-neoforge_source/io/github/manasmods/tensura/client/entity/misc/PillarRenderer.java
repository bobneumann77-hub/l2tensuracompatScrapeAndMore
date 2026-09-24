package io.github.manasmods.tensura.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.tensura.entity.magic.spike.PillarEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class PillarRenderer extends EntityRenderer<PillarEntity> {
   private final BlockRenderDispatcher dispatcher;

   public PillarRenderer(Context context) {
      super(context);
      this.shadowRadius = 0.5F;
      this.dispatcher = context.getBlockRenderDispatcher();
   }

   public void render(PillarEntity spike, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player == null || !spike.isInvisibleTo(minecraft.player)) {
         BlockState blockstate = spike.getBlockState();
         if (blockstate.getRenderShape() == RenderShape.MODEL) {
            Level level = spike.level();
            pMatrixStack.pushPose();
            pMatrixStack.translate(-0.5, 0.0, -0.5);
            float height = spike.getVisualSize();
            float tick = spike.getExtendingTick();
            if (spike.getAge() < tick) {
               height *= Math.min(spike.getAge(), tick) / tick;
            } else {
               int time = spike.getLife() - spike.getAge();
               if (time <= tick) {
                  height *= time / tick;
               }
            }

            pMatrixStack.scale(spike.getVisualSize(), height, spike.getVisualSize());

            for (int i = 0; i < spike.getHeight(); i++) {
               BakedModel model = this.dispatcher.getBlockModel(blockstate);
               this.dispatcher
                  .getModelRenderer()
                  .tesselateBlock(
                     level,
                     model,
                     blockstate,
                     spike.blockPosition().above(i),
                     pMatrixStack,
                     pBuffer.getBuffer(RenderType.translucentMovingBlock()),
                     false,
                     RandomSource.create(),
                     blockstate.getSeed(spike.blockPosition().above(i)),
                     OverlayTexture.NO_OVERLAY
                  );
               pMatrixStack.translate(0.0F, 1.0F, 0.0F);
            }

            pMatrixStack.popPose();
            super.render(spike, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
         }
      }
   }

   public ResourceLocation getTextureLocation(PillarEntity instance) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
