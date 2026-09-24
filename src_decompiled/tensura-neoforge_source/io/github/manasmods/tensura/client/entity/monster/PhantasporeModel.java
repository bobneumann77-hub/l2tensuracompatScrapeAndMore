package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.PhantasporeEntity;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class PhantasporeModel extends TensuraEntityGeoModel<PhantasporeEntity> {
   public PhantasporeModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "phantaspore"), "head");
   }

   public ResourceLocation getTextureResource(PhantasporeEntity instance) {
      return instance.getVariant().getTexture();
   }

   public RenderType getRenderType(PhantasporeEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(PhantasporeEntity spider, long instanceId, AnimationState<PhantasporeEntity> animationState) {
      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (spider.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!spider.isSaddled());
      }

      super.setCustomAnimations(spider, instanceId, animationState);
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("Saddle");
   }
}
