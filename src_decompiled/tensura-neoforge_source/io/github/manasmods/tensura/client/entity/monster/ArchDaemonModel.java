package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.ArchDaemonEntity;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;

public class ArchDaemonModel extends TensuraEntityGeoModel<ArchDaemonEntity> {
   public ArchDaemonModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "arch_daemon"), "head");
   }

   public ResourceLocation getTextureResource(ArchDaemonEntity instance) {
      return instance.getVariant().getSkinLocation(instance);
   }

   public RenderType getRenderType(ArchDaemonEntity animatable, ResourceLocation texture) {
      return RenderType.entityCutoutNoCull(texture, false);
   }

   public void setCustomAnimations(ArchDaemonEntity daemon, long instanceId, AnimationState<ArchDaemonEntity> animationState) {
      if (!daemon.isSleeping() && !daemon.isInSittingPose() && daemon.isAlive()) {
         super.setCustomAnimations(daemon, instanceId, animationState);
      }
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("wings", "horns");
   }
}
