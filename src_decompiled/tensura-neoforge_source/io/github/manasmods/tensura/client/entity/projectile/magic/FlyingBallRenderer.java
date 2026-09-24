package io.github.manasmods.tensura.client.entity.projectile.magic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import java.util.Arrays;
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
import net.minecraft.util.Mth;

public class FlyingBallRenderer extends EntityRenderer<TensuraFlyingProjectile> {
   public static final ModelLayerLocation FLYING_BALL = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "flying_ball"), "main");
   private final ModelPart bolt;

   public FlyingBallRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.bakeLayer(FLYING_BALL);
      this.bolt = modelpart.getChild("Ball");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild(
         "Ball",
         CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, 0.0F, -5.3333F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
         PartPose.offset(0.0F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 48, 44);
   }

   public void render(TensuraFlyingProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player == null || !entity.isInvisibleTo(minecraft.player)) {
         poseStack.pushPose();
         float f = entity.getAge() + partialTicks;
         poseStack.pushPose();
         float swirlX = Mth.cos(0.05F * f * 2.0F) * 90.0F;
         float swirlY = Mth.sin(0.05F * f * 2.0F) * 90.0F;
         float swirlZ = Mth.cos(0.05F * f * 2.0F + 5464.0F) * 90.0F;
         poseStack.mulPose(Axis.XP.rotationDegrees(swirlX * 0.45F));
         poseStack.mulPose(Axis.YP.rotationDegrees(swirlY * 0.45F));
         poseStack.mulPose(Axis.ZP.rotationDegrees(swirlZ * 0.45F));
         float scale = entity.getVisualSize();
         poseStack.scale(scale, scale, scale);
         VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
         this.bolt.render(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY);
         poseStack.popPose();
         poseStack.mulPose(Axis.XP.rotationDegrees(swirlZ));
         poseStack.mulPose(Axis.YP.rotationDegrees(swirlX));
         poseStack.mulPose(Axis.ZP.rotationDegrees(swirlY));
         poseStack.popPose();
         super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
      }
   }

   public ResourceLocation getTextureLocation(TensuraFlyingProjectile instance) {
      ResourceLocation[] resourceLocations = instance.getTextureLocation();
      return resourceLocations == null
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/blank_texture.png")
         : Arrays.stream(resourceLocations).toList().get(instance.getAge() % resourceLocations.length);
   }
}
