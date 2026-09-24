package io.github.manasmods.tensura.client.entity.human;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.TamableAnimal;

public class PlayerLikeModel<T extends TamableAnimal> extends PlayerModel<T> {
   public PlayerLikeModel(ModelPart pRoot, boolean pSlim) {
      super(pRoot, pSlim);
   }

   public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
      super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
      sittingPose(pEntity, this);
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
