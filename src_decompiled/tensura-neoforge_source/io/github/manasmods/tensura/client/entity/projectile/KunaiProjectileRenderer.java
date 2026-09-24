package io.github.manasmods.tensura.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.projectile.KunaiProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;

public class KunaiProjectileRenderer extends EntityRenderer<KunaiProjectile> {
   private final ItemRenderer itemRenderer;

   public KunaiProjectileRenderer(Context renderManager) {
      super(renderManager);
      this.itemRenderer = renderManager.getItemRenderer();
   }

   public ResourceLocation getTextureLocation(KunaiProjectile instance) {
      return InventoryMenu.BLOCK_ATLAS;
   }

   public void render(KunaiProjectile kunai, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
      matrixStack.pushPose();
      matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, kunai.yRotO, kunai.getYRot()) - 90.0F));
      matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, kunai.xRotO, kunai.getXRot()) + 45.0F));
      matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
      matrixStack.translate(0.0, -0.1, 0.0);
      matrixStack.scale(0.5F, 0.5F, 0.5F);
      this.itemRenderer
         .renderStatic(
            kunai.getSourceItem(), ItemDisplayContext.GUI, i, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumerProvider, kunai.level(), kunai.getId()
         );
      matrixStack.popPose();
   }
}
