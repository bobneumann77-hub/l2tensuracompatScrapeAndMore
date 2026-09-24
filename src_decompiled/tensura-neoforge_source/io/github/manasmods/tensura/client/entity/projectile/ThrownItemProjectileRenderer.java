package io.github.manasmods.tensura.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.projectile.ThrownItemProjectile;
import io.github.manasmods.tensura.item.weapon.TensuraSwordItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TieredItem;

public class ThrownItemProjectileRenderer extends EntityRenderer<ThrownItemProjectile> {
   private final ItemRenderer itemRenderer;

   public ThrownItemProjectileRenderer(Context renderManager) {
      super(renderManager);
      this.itemRenderer = renderManager.getItemRenderer();
   }

   public ResourceLocation getTextureLocation(ThrownItemProjectile instance) {
      return InventoryMenu.BLOCK_ATLAS;
   }

   public void render(ThrownItemProjectile projectile, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
      if (!projectile.isInvisible()) {
         matrixStack.pushPose();
         matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, projectile.yRotO, projectile.getYRot()) - 90.0F));
         matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, projectile.xRotO, projectile.getXRot()) + 45.0F));
         matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
         matrixStack.translate(0.0, -0.1, 0.0);
         Item item = projectile.getSourceItem().getItem();
         float scale = 1.0F;
         if (item instanceof TensuraSwordItem longSword && longSword.getRange() > 0.0) {
            scale = (float)(scale + 0.5 * longSword.getRange());
         } else if (item instanceof BowItem bowItem) {
            scale = (float)(scale + 0.1 * bowItem.getDefaultProjectileRange() / 15.0);
         } else if (item instanceof ShieldItem) {
            scale = 2.0F;
         } else if (!(item instanceof TieredItem) && !(item instanceof CrossbowItem)) {
            scale = 0.7F;
         }

         matrixStack.scale(scale, scale, scale);
         this.itemRenderer
            .renderStatic(
               projectile.getSourceItem(),
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
}
