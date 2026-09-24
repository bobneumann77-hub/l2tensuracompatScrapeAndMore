package io.github.manasmods.tensura.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.tensura.entity.projectile.InvisibleArrow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class InvisibleArrowRenderer extends ArrowRenderer<InvisibleArrow> {
   public InvisibleArrowRenderer(Context pContext) {
      super(pContext);
   }

   public void render(InvisibleArrow pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player == null || !pEntity.isInvisibleTo(minecraft.player)) {
         super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
      }
   }

   public ResourceLocation getTextureLocation(InvisibleArrow pEntity) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/invisible_arrow.png");
   }
}
