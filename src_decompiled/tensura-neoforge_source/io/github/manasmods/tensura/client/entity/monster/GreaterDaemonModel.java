package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.GreaterDaemonEntity;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;

public class GreaterDaemonModel extends TensuraEntityGeoModel<GreaterDaemonEntity> {
   public GreaterDaemonModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "greater_daemon"), "head");
   }

   public RenderType getRenderType(GreaterDaemonEntity animatable, ResourceLocation texture) {
      return RenderType.entityCutoutNoCull(texture, false);
   }

   public ResourceLocation getTextureResource(GreaterDaemonEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/greater_daemon.png");
   }

   public void setCustomAnimations(GreaterDaemonEntity animatable, long instanceId, AnimationState<GreaterDaemonEntity> animationState) {
      if (!animatable.isSleeping() && animatable.isAlive()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("right_wing", "left_wing");
   }
}
