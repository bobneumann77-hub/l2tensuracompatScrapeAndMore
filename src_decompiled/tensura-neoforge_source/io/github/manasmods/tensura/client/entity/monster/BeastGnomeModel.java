package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.BeastGnomeEntity;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.texture.AnimatableTexture;

public class BeastGnomeModel extends TensuraEntityGeoModel<BeastGnomeEntity> {
   public BeastGnomeModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "beast_gnome"), "RotatingHead");
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/beast_gnome/beast_gnome.png"));
   }

   public ResourceLocation getTextureResource(BeastGnomeEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/beast_gnome/beast_gnome.png");
   }

   public RenderType getRenderType(BeastGnomeEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(BeastGnomeEntity animatable, long instanceId, AnimationState<BeastGnomeEntity> animationState) {
      if (!animatable.isSleeping()) {
         if (!animationState.isCurrentAnimation(BeastGnomeEntity.EAT)) {
            if (!animationState.isCurrentAnimation(BeastGnomeEntity.SLAM)) {
               if (!animationState.isCurrentAnimation(BeastGnomeEntity.YELL)) {
                  super.setCustomAnimations(animatable, instanceId, animationState);
               }
            }
         }
      }
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("Tail");
   }
}
