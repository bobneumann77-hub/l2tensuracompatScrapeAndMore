package io.github.manasmods.tensura.client.entity.human;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.human.GazelDwargoEntity;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class GazelDwargoModel extends TensuraEntityGeoModel<GazelDwargoEntity> {
   public GazelDwargoModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "gazel_dwargo"), "head");
   }

   public RenderType getRenderType(GazelDwargoEntity animatable, ResourceLocation texture) {
      return RenderType.entityCutoutNoCull(texture, false);
   }

   public ResourceLocation getTextureResource(GazelDwargoEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/gazel_dwargo.png");
   }

   public void setCustomAnimations(GazelDwargoEntity gazel, long instanceId, AnimationState<GazelDwargoEntity> animationState) {
      boolean hasItem = !gazel.getMainHandItem().is((Item)TensuraToolItems.RUHK.get());
      GeoBone blade = this.getAnimationProcessor().getBone("blade");
      if (blade.isHidden() != hasItem) {
         blade.setHidden(hasItem);
      }

      if (gazel.isAlive() && !gazel.isInSittingPose() && gazel.getPhase() != 0) {
         super.setCustomAnimations(gazel, instanceId, animationState);
      }
   }
}
