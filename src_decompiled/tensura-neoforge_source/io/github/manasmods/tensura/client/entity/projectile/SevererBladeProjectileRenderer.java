package io.github.manasmods.tensura.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.projectile.SevererBladeProjectile;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
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

public class SevererBladeProjectileRenderer extends EntityRenderer<SevererBladeProjectile> {
   private final ItemRenderer itemRenderer;

   public SevererBladeProjectileRenderer(Context renderManager) {
      super(renderManager);
      this.itemRenderer = renderManager.getItemRenderer();
   }

   public ResourceLocation getTextureLocation(SevererBladeProjectile instance) {
      return InventoryMenu.BLOCK_ATLAS;
   }

   public void render(SevererBladeProjectile severerBlade, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
      matrixStack.pushPose();
      matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, severerBlade.yRotO, severerBlade.getYRot()) - 90.0F));
      matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, severerBlade.xRotO, severerBlade.getXRot()) + 45.0F));
      matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
      matrixStack.translate(0.0, -0.2, 0.0);
      this.itemRenderer
         .renderStatic(
            ((Item)TensuraToolItems.SEVERER_BLADE.get()).getDefaultInstance(),
            ItemDisplayContext.GUI,
            i,
            OverlayTexture.NO_OVERLAY,
            matrixStack,
            vertexConsumerProvider,
            severerBlade.level(),
            severerBlade.getId()
         );
      matrixStack.popPose();
   }
}
