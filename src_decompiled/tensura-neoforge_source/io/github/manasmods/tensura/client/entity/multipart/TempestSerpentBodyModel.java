package io.github.manasmods.tensura.client.entity.multipart;

import io.github.manasmods.tensura.entity.multipart.TempestSerpentBody;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class TempestSerpentBodyModel extends DefaultedEntityGeoModel<TempestSerpentBody> {
   public TempestSerpentBodyModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "tempest_serpent"), false);
   }

   public ResourceLocation getModelResource(TempestSerpentBody object) {
      return object.isEndSegment()
         ? ResourceLocation.fromNamespaceAndPath("tensura", "geo/entity/tempest_serpent_tail.geo.json")
         : ResourceLocation.fromNamespaceAndPath("tensura", "geo/entity/tempest_serpent_body.geo.json");
   }

   public ResourceLocation getTextureResource(TempestSerpentBody instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/tempest_serpent/tempest_serpent.png");
   }

   public ResourceLocation getAnimationResource(TempestSerpentBody entity) {
      return entity.isEndSegment()
         ? ResourceLocation.fromNamespaceAndPath("tensura", "animations/entity/tempest_serpent_tail.animation.json")
         : ResourceLocation.fromNamespaceAndPath("tensura", "animations/entity/tempest_serpent_body.animation.json");
   }

   public void setCustomAnimations(TempestSerpentBody body, long instanceId, AnimationState<TempestSerpentBody> animationState) {
      if (!body.isEndSegment()) {
         GeoBone chest = this.getAnimationProcessor().getBone("Chest");
         if (body.isChested() == chest.isHidden()) {
            chest.setHidden(!body.isChested());
         }
      }
   }
}
