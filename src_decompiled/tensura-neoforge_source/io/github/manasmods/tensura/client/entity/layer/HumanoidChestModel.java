package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

public class HumanoidChestModel<T extends LivingEntity> extends EntityModel<T> {
   private final ModelPart waist;
   public final ModelPart body;
   private final ModelPart chest;

   public HumanoidChestModel(ModelPart root) {
      this.waist = root.getChild("Waist");
      this.body = this.waist.getChild("Body");
      this.chest = this.body.getChild("Chest");
   }

   public static LayerDefinition createBodyLayer() {
      return createBodyLayer(0.0F);
   }

   public static LayerDefinition createBodyLayer(float extraInflation) {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition waist = partdefinition.addOrReplaceChild("Waist", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));
      PartDefinition body = waist.addOrReplaceChild("Body", CubeListBuilder.create(), PartPose.offset(0.0F, -12.0F, 0.0F));
      PartDefinition chest = body.addOrReplaceChild("Chest", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, -1.0F));
      chest.addOrReplaceChild(
         "Layer_r1",
         CubeListBuilder.create()
            .texOffs(17, 40)
            .addBox(-4.0F, 0.9F, -0.7F, 8.0F, 2.0F, 3.0F, new CubeDeformation(0.199F + extraInflation))
            .texOffs(17, 24)
            .addBox(-4.0F, 0.9F, -0.7F, 8.0F, 2.0F, 3.0F, new CubeDeformation(0.009F + extraInflation)),
         PartPose.offsetAndRotation(0.0F, 2.3952F, -1.6749F, 2.5744F, 0.0F, 0.0F)
      );
      chest.addOrReplaceChild(
         "Layer_r2",
         CubeListBuilder.create()
            .texOffs(17, 33)
            .addBox(-4.0F, -1.0F, -1.0F, 8.0F, 4.0F, 3.0F, new CubeDeformation(0.2F + extraInflation))
            .texOffs(17, 17)
            .addBox(-4.0F, -1.0F, -1.0F, 8.0F, 4.0F, 3.0F, new CubeDeformation(0.01F + extraInflation)),
         PartPose.offsetAndRotation(0.0F, -1.6048F, -0.6749F, -0.5672F, 0.0F, 0.0F)
      );
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
   }

   public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
      this.waist.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
   }
}
