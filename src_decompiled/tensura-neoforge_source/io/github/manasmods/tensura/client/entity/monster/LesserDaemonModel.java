package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.LesserDaemonEntity;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;

public class LesserDaemonModel extends TensuraEntityGeoModel<LesserDaemonEntity> {
   public LesserDaemonModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "lesser_daemon"), "head");
   }

   public RenderType getRenderType(LesserDaemonEntity animatable, ResourceLocation texture) {
      return RenderType.entityCutoutNoCull(texture, false);
   }

   public ResourceLocation getTextureResource(LesserDaemonEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/daemon/lesser_daemon.png");
   }

   public void setCustomAnimations(LesserDaemonEntity animatable, long instanceId, AnimationState<LesserDaemonEntity> animationState) {
      if (!animatable.isSleeping() && animatable.isAlive()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("right_wing", "left_wing");
   }
}
