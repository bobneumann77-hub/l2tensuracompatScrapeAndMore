package io.github.manasmods.tensura.client.entity.human;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PlayerLikeSkeletonModel<T extends TamableAnimal> extends HumanoidModel<T> {
   public PlayerLikeSkeletonModel(ModelPart pRoot) {
      super(pRoot);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshDefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partDefinition = meshDefinition.getRoot();
      createDefaultSkeletonMesh(partDefinition);
      return LayerDefinition.create(meshDefinition, 64, 32);
   }

   protected static void createDefaultSkeletonMesh(PartDefinition partDefinition) {
      partDefinition.addOrReplaceChild(
         "right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-5.0F, 2.0F, 0.0F)
      );
      partDefinition.addOrReplaceChild(
         "left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F)
      );
      partDefinition.addOrReplaceChild(
         "right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(-2.0F, 12.0F, 0.0F)
      );
      partDefinition.addOrReplaceChild(
         "left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F), PartPose.offset(2.0F, 12.0F, 0.0F)
      );
   }

   public void prepareMobModel(T mob, float f, float g, float h) {
      this.rightArmPose = ArmPose.EMPTY;
      this.leftArmPose = ArmPose.EMPTY;
      ItemStack itemStack = mob.getItemInHand(InteractionHand.MAIN_HAND);
      if (itemStack.is(Items.BOW) && mob.isAggressive()) {
         if (mob.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArmPose = ArmPose.BOW_AND_ARROW;
         } else {
            this.leftArmPose = ArmPose.BOW_AND_ARROW;
         }
      }

      super.prepareMobModel(mob, f, g, h);
   }

   public void setupAnim(T mob, float f, float g, float h, float i, float j) {
      super.setupAnim(mob, f, g, h, i, j);
      ItemStack itemStack = mob.getMainHandItem();
      if (mob.isAggressive() && (itemStack.isEmpty() || !itemStack.is(Items.BOW))) {
         float k = Mth.sin(this.attackTime * (float) Math.PI);
         float l = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float) Math.PI);
         this.rightArm.zRot = 0.0F;
         this.leftArm.zRot = 0.0F;
         this.rightArm.yRot = -(0.1F - k * 0.6F);
         this.leftArm.yRot = 0.1F - k * 0.6F;
         this.rightArm.xRot = (float) (-Math.PI / 2);
         this.leftArm.xRot = (float) (-Math.PI / 2);
         ModelPart var10000 = this.rightArm;
         var10000.xRot -= k * 1.2F - l * 0.4F;
         var10000 = this.leftArm;
         var10000.xRot -= k * 1.2F - l * 0.4F;
         AnimationUtils.bobArms(this.rightArm, this.leftArm, h);
      }

      sittingPose(mob, this);
   }

   public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
      float f = humanoidArm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      ModelPart modelPart = this.getArm(humanoidArm);
      modelPart.x += f;
      modelPart.translateAndRotate(poseStack);
      modelPart.x -= f;
   }

   public static void sittingPose(TamableAnimal pEntity, HumanoidModel<?> model) {
      if (!pEntity.isSleeping()) {
         if (pEntity.isInSittingPose()) {
            model.rightArm.xRot += (float) (-Math.PI / 5);
            model.leftArm.xRot += (float) (-Math.PI / 5);
            model.rightLeg.xRot = -1.4137167F;
            model.rightLeg.yRot = (float) (Math.PI / 10);
            model.rightLeg.zRot = 0.07853982F;
            model.leftLeg.xRot = -1.4137167F;
            model.leftLeg.yRot = (float) (-Math.PI / 10);
            model.leftLeg.zRot = -0.07853982F;
            if (model instanceof PlayerModel<?> playerModel) {
               playerModel.leftPants.copyFrom(playerModel.leftLeg);
               playerModel.rightPants.copyFrom(playerModel.rightLeg);
               playerModel.leftSleeve.copyFrom(playerModel.leftArm);
               playerModel.rightSleeve.copyFrom(playerModel.rightArm);
            }
         }
      }
   }
}
