package io.github.manasmods.tensura.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.block.CharybdisCoreBlock;
import io.github.manasmods.tensura.entity.magic.misc.PrimedCharybdisCoreEntity;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import org.jetbrains.annotations.NotNull;

public class PrimedCharybdisCoreRenderer extends EntityRenderer<PrimedCharybdisCoreEntity> {
   private final BlockRenderDispatcher blockRenderer;

   public PrimedCharybdisCoreRenderer(Context pContext) {
      super(pContext);
      this.shadowRadius = 0.5F;
      this.blockRenderer = pContext.getBlockRenderDispatcher();
   }

   public void render(
      PrimedCharybdisCoreEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight
   ) {
      pMatrixStack.pushPose();
      pMatrixStack.translate(0.0, 0.5, 0.0);
      int i = pEntity.getFuse();
      if (i - pPartialTicks + 1.0F < 10.0F) {
         float f = 1.0F - (i - pPartialTicks + 1.0F) / 10.0F;
         f = Mth.clamp(f, 0.0F, 1.0F);
         f *= f;
         f *= f;
         float f1 = 1.0F + f * 0.3F;
         pMatrixStack.scale(f1, f1, f1);
      }

      pMatrixStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
      pMatrixStack.translate(-0.5, -0.5, 0.5);
      pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
      renderWhiteSolidBlock(
         this.blockRenderer,
         (BlockState)((Block)TensuraBlocks.CHARYBDIS_CORE.get()).defaultBlockState().setValue(CharybdisCoreBlock.MODE, SculkSensorPhase.COOLDOWN),
         pMatrixStack,
         pBuffer,
         pPackedLight,
         i / 5 % 2 == 0
      );
      pMatrixStack.popPose();
      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   @NotNull
   public ResourceLocation getTextureLocation(PrimedCharybdisCoreEntity pEntity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   public static void renderWhiteSolidBlock(
      BlockRenderDispatcher pBlockRenderDispatcher, BlockState pState, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, boolean pWhiteOverlay
   ) {
      int i;
      if (pWhiteOverlay) {
         i = OverlayTexture.pack(OverlayTexture.u(1.0F), 10);
      } else {
         i = OverlayTexture.NO_OVERLAY;
      }

      pBlockRenderDispatcher.renderSingleBlock(pState, pPoseStack, pBuffer, pPackedLight, i);
   }
}
