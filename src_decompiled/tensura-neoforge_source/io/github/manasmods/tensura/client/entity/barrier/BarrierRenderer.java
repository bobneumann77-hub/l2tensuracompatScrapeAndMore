package io.github.manasmods.tensura.client.entity.barrier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BarrierRenderer extends EntityRenderer<BarrierEntity> {
   public static final ModelLayerLocation BARRIER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "barrier"), "main");
   private final ModelPart barrier;

   public BarrierRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.bakeLayer(BARRIER);
      this.barrier = modelpart.getChild("Barrier");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild(
         "Barrier",
         CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -23.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 24.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void render(BarrierEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player == null || !entity.isInvisibleTo(minecraft.player)) {
         poseStack.pushPose();
         if (entity.getSize() > 3.0F) {
            poseStack.translate(0.0, -0.05 * entity.getVisualSize(), 0.0);
         } else {
            poseStack.translate(0.0, 0.1 * entity.getVisualSize(), 0.0);
         }

         float radius = entity.getVisualSize() * 2.0F;
         poseStack.scale(radius, radius, radius);
         VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
         this.barrier.render(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY);
         poseStack.popPose();
         super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
      }
   }

   public ResourceLocation getTextureLocation(BarrierEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/barrier.png");
   }
}
