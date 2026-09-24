package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.MegalodonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MegalodonModel extends DefaultedEntityGeoModel<MegalodonEntity> {
   public MegalodonModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "megalodon"), true);
   }

   public ResourceLocation getTextureResource(MegalodonEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/megalodon/megalodon.png");
   }

   public void setCustomAnimations(MegalodonEntity bear, long instanceId, AnimationState<MegalodonEntity> animationState) {
      GeoBone chest = this.getAnimationProcessor().getBone("Chests");
      if (bear.isChested() == chest.isHidden()) {
         chest.setHidden(!bear.isChested());
      }

      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (bear.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!bear.isSaddled());
      }
   }
}
