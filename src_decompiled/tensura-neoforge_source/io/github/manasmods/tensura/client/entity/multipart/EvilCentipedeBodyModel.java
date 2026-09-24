package io.github.manasmods.tensura.client.entity.multipart;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.multipart.EvilCentipedeBody;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class EvilCentipedeBodyModel extends TensuraEntityGeoModel<EvilCentipedeBody> {
   public EvilCentipedeBodyModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "evil_centipede"), "head");
   }

   public ResourceLocation getModelResource(EvilCentipedeBody object) {
      return object.isEndSegment()
         ? ResourceLocation.fromNamespaceAndPath("tensura", "geo/entity/evil_centipede_tail.geo.json")
         : ResourceLocation.fromNamespaceAndPath("tensura", "geo/entity/evil_centipede_body.geo.json");
   }

   public ResourceLocation getTextureResource(EvilCentipedeBody instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/evil_centipede/evil_centipede.png");
   }

   public ResourceLocation getAnimationResource(EvilCentipedeBody entity) {
      return entity.isEndSegment()
         ? ResourceLocation.fromNamespaceAndPath("tensura", "animations/entity/evil_centipede_tail.animation.json")
         : ResourceLocation.fromNamespaceAndPath("tensura", "animations/entity/evil_centipede_body.animation.json");
   }

   public void setCustomAnimations(EvilCentipedeBody body, long instanceId, AnimationState<EvilCentipedeBody> animationState) {
      GeoBone chest = this.getAnimationProcessor().getBone("Chest");
      if (body.isChested() == chest.isHidden()) {
         chest.setHidden(!body.isChested());
      }
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("La2", "Fa2");
   }
}
