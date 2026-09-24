package io.github.manasmods.tensura.client.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.projectile.MonsterSpitProjectile;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MonsterSpitProjectileRenderer extends EntityRenderer<MonsterSpitProjectile> {
   private static final ResourceLocation LLAMA_SPIT_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/llama/spit.png");
   private final LlamaSpitModel<MonsterSpitProjectile> model;

   public MonsterSpitProjectileRenderer(Context renderManager) {
      super(renderManager);
      this.model = new LlamaSpitModel(renderManager.bakeLayer(ModelLayers.LLAMA_SPIT));
   }

   public void render(MonsterSpitProjectile pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      pMatrixStack.pushPose();
      pMatrixStack.translate(0.0, 0.15F, 0.0);
      pMatrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
      pMatrixStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot())));
      this.model.setupAnim(pEntity, pPartialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
      VertexConsumer vertexconsumer = pBuffer.getBuffer(this.model.renderType(this.getTextureLocation(pEntity)));
      this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY);
      pMatrixStack.popPose();
      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   public ResourceLocation getTextureLocation(MonsterSpitProjectile pEntity) {
      return pEntity.getOwner() != null && pEntity.getOwner().getType() == MonsterEntityTypes.GIANT_ANT.get()
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/blank_texture.png")
         : LLAMA_SPIT_LOCATION;
   }
}
