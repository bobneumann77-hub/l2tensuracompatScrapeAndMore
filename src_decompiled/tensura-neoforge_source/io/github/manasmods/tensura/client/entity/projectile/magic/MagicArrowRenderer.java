package io.github.manasmods.tensura.client.entity.projectile.magic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import java.util.Arrays;
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
import net.minecraft.world.phys.Vec3;

public class MagicArrowRenderer extends EntityRenderer<TensuraFlyingProjectile> {
   public static final ModelLayerLocation ARROW = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "magic_arrow"), "main");
   private final ModelPart spike;

   public MagicArrowRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.bakeLayer(ARROW);
      this.spike = modelpart.getChild("spike");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition spike = partdefinition.addOrReplaceChild("spike", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
      spike.addOrReplaceChild(
         "plane_1",
         CubeListBuilder.create().texOffs(16, 0).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -20.0F, 0.0F, 0.0F, 0.7854F, 0.0F)
      );
      spike.addOrReplaceChild(
         "plane_2",
         CubeListBuilder.create().texOffs(0, -16).addBox(0.0F, -8.0F, -8.0F, 0.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)),
         PartPose.offsetAndRotation(0.0F, -20.0F, 0.0F, 0.7854F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 16);
   }

   public void render(TensuraFlyingProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.pushPose();
      Vec3 motion = entity.getDeltaMovement();
      float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
      poseStack.scale(entity.getVisualSize(), entity.getVisualSize(), entity.getVisualSize());
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
      this.spike.render(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY);
      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(TensuraFlyingProjectile instance) {
      ResourceLocation[] resourceLocations = instance.getTextureLocation();
      return resourceLocations == null
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/blank_texture.png")
         : Arrays.stream(resourceLocations).toList().get(instance.getAge() % resourceLocations.length);
   }
}
