package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.PegasusEntity;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class PegasusModel extends TensuraEntityGeoModel<PegasusEntity> {
   public PegasusModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "horse"), "head");
   }

   public ResourceLocation getModelResource(PegasusEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "geo/entity/pegasus.geo.json");
   }

   public ResourceLocation getTextureResource(PegasusEntity instance) {
      return instance.isBlack()
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/horse/pegasus_black.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/horse/pegasus.png");
   }

   public void setCustomAnimations(PegasusEntity animatable, long instanceId, AnimationState<PegasusEntity> animationState) {
      GeoBone horn = this.getAnimationProcessor().getBone("Horn");
      if (animatable.getType().equals(MonsterEntityTypes.PEGACORN.get()) == horn.isHidden()) {
         horn.setHidden(!animatable.getType().equals(MonsterEntityTypes.PEGACORN.get()));
      }

      GeoBone saddle = this.getAnimationProcessor().getBone("saddle");
      if (animatable.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!animatable.isSaddled());
      }

      GeoBone chest = this.getAnimationProcessor().getBone("chest");
      if (animatable.isChested() == chest.isHidden()) {
         chest.setHidden(!animatable.isChested());
      }

      if (!animatable.isSleeping() && animatable.isAlive()) {
         super.setCustomAnimations(animatable, instanceId, animationState);
      }
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("Horn");
   }
}
