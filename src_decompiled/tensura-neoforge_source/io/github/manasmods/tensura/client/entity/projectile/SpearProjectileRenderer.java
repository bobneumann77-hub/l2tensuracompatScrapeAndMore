package io.github.manasmods.tensura.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.projectile.SpearProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;

public class SpearProjectileRenderer extends EntityRenderer<SpearProjectile> {
   private final ItemRenderer itemRenderer;

   public SpearProjectileRenderer(Context renderManager) {
      super(renderManager);
      this.itemRenderer = renderManager.getItemRenderer();
   }

   public ResourceLocation getTextureLocation(SpearProjectile instance) {
      return InventoryMenu.BLOCK_ATLAS;
   }

   public void render(SpearProjectile spearProjectile, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
      matrixStack.pushPose();
      matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, spearProjectile.yRotO, spearProjectile.getYRot()) - 90.0F));
      matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, spearProjectile.xRotO, spearProjectile.getXRot()) + 45.0F));
      matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
      matrixStack.translate(0.0, -0.3, 0.0);
      matrixStack.scale(2.0F, 2.0F, 2.0F);
      this.itemRenderer
         .renderStatic(
            spearProjectile.getSourceItem(),
            ItemDisplayContext.GUI,
            i,
            OverlayTexture.NO_OVERLAY,
            matrixStack,
            vertexConsumerProvider,
            spearProjectile.level(),
            spearProjectile.getId()
         );
      matrixStack.popPose();
   }
}
