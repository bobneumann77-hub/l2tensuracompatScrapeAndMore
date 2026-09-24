package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.TensuraClient;
import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.BlackSpiderEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class BlackSpiderModel extends TensuraEntityGeoModel<BlackSpiderEntity> {
   public BlackSpiderModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "black_spider"), "Head");
   }

   public ResourceLocation getTextureResource(BlackSpiderEntity instance) {
      if (instance.getCustomName() != null && instance.getCustomName().getString().equals("Kumoko")) {
         return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/black_spider/black_spider_kumoko.png");
      } else if (TensuraClient.CONFIG.arachnophobia) {
         return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/black_spider/black_spider_safe.png");
      } else {
         return instance.isStriped()
            ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/black_spider/black_spider_yellow.png")
            : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/black_spider/black_spider_black.png");
      }
   }

   public RenderType getRenderType(BlackSpiderEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(BlackSpiderEntity spider, long instanceId, AnimationState<BlackSpiderEntity> animationState) {
      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (spider.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!spider.isSaddled());
      }

      GeoBone saddleStraps = this.getAnimationProcessor().getBone("SaddleStraps");
      if (saddleStraps.isHidden() == (spider.getChests() > 0 || spider.isSaddled())) {
         saddleStraps.setHidden(spider.getChests() <= 0 && !spider.isSaddled());
      }

      GeoBone chestWrap = this.getAnimationProcessor().getBone("ChestWrap");
      if (chestWrap.isHidden() == spider.getChests() > 0) {
         chestWrap.setHidden(spider.getChests() <= 0);
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
