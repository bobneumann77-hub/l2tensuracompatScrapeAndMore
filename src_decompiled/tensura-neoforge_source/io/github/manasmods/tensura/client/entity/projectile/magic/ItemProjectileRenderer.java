package io.github.manasmods.tensura.client.entity.projectile.magic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemProjectileRenderer extends EntityRenderer<TensuraProjectile> {
   private final ItemRenderer itemRenderer;
   private final Item item;
   private float xRot = 45.0F;

   public ItemProjectileRenderer(Context context, Item item) {
      super(context);
      this.itemRenderer = context.getItemRenderer();
      this.item = item;
   }

   public ItemProjectileRenderer setXRotation(float x) {
      this.xRot = x;
      return this;
   }

   protected int getBlockLightLevel(TensuraProjectile entity, BlockPos blockPos) {
      return 10;
   }

   @NotNull
   public ResourceLocation getTextureLocation(TensuraProjectile instance) {
      return InventoryMenu.BLOCK_ATLAS;
   }

   public void render(TensuraProjectile projectile, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
      matrixStack.pushPose();
      matrixStack.translate(0.0F, projectile.getBbHeight() / 2.0F, 0.0F);
      matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(g, projectile.yRotO, projectile.getYRot()) - 90.0F));
      matrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(g, projectile.xRotO, projectile.getXRot()) + this.xRot));
      matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
      float scale = projectile.getVisualSize() * 1.5F;
      matrixStack.scale(scale, scale, scale);
      ItemStack stack = this.item.getDefaultInstance();
      this.itemRenderer
         .renderStatic(stack, ItemDisplayContext.GUI, i, OverlayTexture.NO_OVERLAY, matrixStack, vertexConsumerProvider, projectile.level(), projectile.getId());
      matrixStack.popPose();
   }
}
