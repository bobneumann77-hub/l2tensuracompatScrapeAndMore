package io.github.manasmods.tensura.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.projectile.magic.TempestScaleEntity;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;

public class TempestScaleEntityRenderer extends EntityRenderer<TempestScaleEntity> {
   private final ItemRenderer itemRenderer;

   public TempestScaleEntityRenderer(Context renderManager) {
      super(renderManager);
      this.itemRenderer = renderManager.getItemRenderer();
   }

   public ResourceLocation getTextureLocation(TempestScaleEntity instance) {
      return InventoryMenu.BLOCK_ATLAS;
   }

   public void render(TempestScaleEntity projectile, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
      matrixStack.pushPose();
      matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, projectile.yRotO, projectile.getYRot()) - 90.0F));
      matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, projectile.xRotO, projectile.getXRot()) + 45.0F));
      matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
      matrixStack.translate(0.0, -0.1, 0.0);
      float scale = 1.0F;
      matrixStack.scale(scale, scale, scale);
      this.itemRenderer
         .renderStatic(
            ((Item)TensuraMobDropItems.CHARYBDIS_SCALE.get()).getDefaultInstance(),
            ItemDisplayContext.GUI,
            i,
            OverlayTexture.NO_OVERLAY,
            matrixStack,
            vertexConsumerProvider,
            projectile.level(),
            projectile.getId()
         );
      matrixStack.popPose();
   }
}
