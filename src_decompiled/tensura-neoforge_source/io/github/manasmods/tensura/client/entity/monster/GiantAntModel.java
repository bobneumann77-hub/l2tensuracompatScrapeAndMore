package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.GiantAntEntity;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class GiantAntModel extends TensuraEntityGeoModel<GiantAntEntity> {
   public GiantAntModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "giant_ant"), "Head");
   }

   public ResourceLocation getTextureResource(GiantAntEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/giant_ant/giant_ant.png");
   }

   public RenderType getRenderType(GiantAntEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(GiantAntEntity spider, long instanceId, AnimationState<GiantAntEntity> animationState) {
      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (spider.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!spider.isSaddled());
      }

      GeoBone straps1 = this.getAnimationProcessor().getBone("ChestStraps");
      if (straps1.isHidden() == spider.getChests() >= 1) {
         straps1.setHidden(spider.getChests() < 1);
      }

      GeoBone chest1 = this.getAnimationProcessor().getBone("Chest1");
      if (chest1.isHidden() == spider.getChests() >= 1) {
         chest1.setHidden(spider.getChests() < 1);
      }

      GeoBone chest2 = this.getAnimationProcessor().getBone("Chest2");
      if (chest2.isHidden() == spider.getChests() >= 2) {
         chest2.setHidden(spider.getChests() < 2);
      }

      GeoBone straps2 = this.getAnimationProcessor().getBone("ChestStraps2");
      if (straps2.isHidden() == spider.getChests() >= 1) {
         straps2.setHidden(spider.getChests() < 3);
      }

      GeoBone chest3 = this.getAnimationProcessor().getBone("Chest3");
      if (chest3.isHidden() == spider.getChests() >= 3) {
         chest3.setHidden(spider.getChests() < 3);
      }

      GeoBone chest4 = this.getAnimationProcessor().getBone("Chest4");
      if (chest4.isHidden() == spider.getChests() >= 4) {
         chest4.setHidden(spider.getChests() < 4);
      }

      GeoBone straps3 = this.getAnimationProcessor().getBone("ChestStraps3");
      if (straps3.isHidden() == spider.getChests() >= 1) {
         straps3.setHidden(spider.getChests() < 5);
      }

      GeoBone chest5 = this.getAnimationProcessor().getBone("Chest5");
      if (chest5.isHidden() == spider.getChests() >= 4) {
         chest5.setHidden(spider.getChests() < 5);
      }

      GeoBone chest6 = this.getAnimationProcessor().getBone("Chest6");
      if (chest6.isHidden() == spider.getChests() >= 4) {
         chest6.setHidden(spider.getChests() < 6);
      }

      super.setCustomAnimations(spider, instanceId, animationState);
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("RightAntenna", "LeftAntenna", "Chest1", "Chest2", "Chest3", "Chest4", "Chest5", "Chest6", "ChestStraps", "ChestStraps2", "ChestStraps3");
   }
}
