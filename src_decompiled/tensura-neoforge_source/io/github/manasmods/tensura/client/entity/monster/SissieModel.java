package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.SissieEntity;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SissieModel extends DefaultedEntityGeoModel<SissieEntity> {
   public SissieModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "sissie"), true);
   }

   public ResourceLocation getTextureResource(SissieEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/sissie/sissie.png");
   }

   public RenderType getRenderType(SissieEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(SissieEntity fish, long instanceId, AnimationState<SissieEntity> animationState) {
      GeoBone chest = this.getAnimationProcessor().getBone("Chest");
      if (fish.isChested() == chest.isHidden()) {
         chest.setHidden(!fish.isChested());
      }

      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (fish.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!fish.isSaddled());
      }
   }

   public List<String> getHiddenInLayerBones() {
      return List.of("Spear", "LeftAntenna", "MiddleRightFin", "MiddleLeftFin", "TopFin");
   }
}
