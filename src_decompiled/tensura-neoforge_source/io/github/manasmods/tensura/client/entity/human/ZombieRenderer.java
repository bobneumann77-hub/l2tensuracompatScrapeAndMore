package io.github.manasmods.tensura.client.entity.human;

import io.github.manasmods.tensura.entity.human.undead.ZombieHumanoidEntity;
import io.github.manasmods.tensura.entity.variant.ZombieVariant;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ZombieRenderer<T extends ZombieHumanoidEntity> extends PlayerLikeRenderer<T> {
   public ZombieRenderer(Context pContext) {
      super(pContext, new PlayerLikeModel<T>(pContext.bakeLayer(ModelLayers.PLAYER), false) {
         public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            if (!pEntity.isInSittingPose()) {
               AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, pEntity.isAngry(), this.attackTime, pAgeInTicks);
            }
         }
      }, 0.5F);
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            new HumanoidModel(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            pContext.getModelManager()
         )
      );
   }

   @NotNull
   public ResourceLocation getTextureLocation(T entity) {
      return ZombieVariant.LOCATION_BY_VARIANT.get(entity.getVariant());
   }
}
