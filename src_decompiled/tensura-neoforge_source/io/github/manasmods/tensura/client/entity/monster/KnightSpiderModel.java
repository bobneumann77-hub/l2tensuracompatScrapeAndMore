package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.TensuraClient;
import io.github.manasmods.tensura.entity.monster.KnightSpiderEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class KnightSpiderModel extends DefaultedEntityGeoModel<KnightSpiderEntity> {
   public KnightSpiderModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "knight_spider"), true);
   }

   public ResourceLocation getTextureResource(KnightSpiderEntity instance) {
      return TensuraClient.CONFIG.arachnophobia
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/knight_spider/knight_spider_safe.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/knight_spider/knight_spider.png");
   }

   public RenderType getRenderType(KnightSpiderEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(KnightSpiderEntity spider, long instanceId, AnimationState<KnightSpiderEntity> animationState) {
      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (spider.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!spider.isSaddled());
      }

      GeoBone chest1 = this.getAnimationProcessor().getBone("Chest1");
      if (chest1.isHidden() == spider.getChests() >= 1) {
         chest1.setHidden(spider.getChests() < 1);
      }

      GeoBone chest2 = this.getAnimationProcessor().getBone("Chest2");
      if (chest2.isHidden() == spider.getChests() >= 2) {
         chest2.setHidden(spider.getChests() < 2);
      }

      GeoBone chest3 = this.getAnimationProcessor().getBone("Chest3");
      if (chest3.isHidden() == spider.getChests() >= 3) {
         chest3.setHidden(spider.getChests() < 3);
      }

      GeoBone chest4 = this.getAnimationProcessor().getBone("Chest4");
      if (chest4.isHidden() == spider.getChests() >= 4) {
         chest4.setHidden(spider.getChests() < 4);
      }

      super.setCustomAnimations(spider, instanceId, animationState);
   }
}
