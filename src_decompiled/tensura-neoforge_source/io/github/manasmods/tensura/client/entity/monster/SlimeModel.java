package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.layer.SlimeSantaHatLayer;
import io.github.manasmods.tensura.entity.monster.MetalSlimeEntity;
import io.github.manasmods.tensura.entity.monster.SlimeEntity;
import io.github.manasmods.tensura.entity.variant.SlimeColor;
import io.github.manasmods.tensura.util.client.ClientHelper;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SlimeModel extends DefaultedEntityGeoModel<SlimeEntity> {
   public SlimeModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "slime"), false);
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_rainbow.png"));
   }

   public ResourceLocation getTextureResource(SlimeEntity slime) {
      if (slime.getClass() == MetalSlimeEntity.class) {
         return shouldBeGolden(slime)
            ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_metal_golden.png")
            : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_metal.png");
      } else {
         return slime.hasCustomName() && "jeb_".equals(slime.getName().getString())
            ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/slime_rainbow.png")
            : SlimeColor.LOCATION_BY_VARIANT.get(slime.getColor());
      }
   }

   public static boolean shouldBeGolden(SlimeEntity slime) {
      if (!slime.hasCustomName()) {
         return false;
      } else {
         return "Minh".equalsIgnoreCase(slime.getName().getString()) ? true : "MinhEragon".equalsIgnoreCase(slime.getName().getString());
      }
   }

   public RenderType getRenderType(SlimeEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(SlimeEntity slime, long instanceId, AnimationState<SlimeEntity> customPredicate) {
      super.setCustomAnimations(slime, instanceId, customPredicate);
      GeoBone chest = this.getAnimationProcessor().getBone("Chest");
      if (slime.isChested() == chest.isHidden()) {
         chest.setHidden(!slime.isChested());
      }

      GeoBone saddle = this.getAnimationProcessor().getBone("Saddle");
      if (slime.isSaddled() == saddle.isHidden()) {
         saddle.setHidden(!slime.isSaddled());
      }

      boolean showSkull = !slime.isChested() && ClientHelper.HALLOWEEN;
      GeoBone skull = this.getAnimationProcessor().getBone("HalloweenSkull");
      if (showSkull == skull.isHidden()) {
         skull.setHidden(!showSkull);
      }

      boolean showSantaHat = SlimeSantaHatLayer.shouldShowSantaHat(slime);
      GeoBone hat = this.getAnimationProcessor().getBone("SantaHat");
      if (showSantaHat == hat.isHidden()) {
         hat.setHidden(!showSantaHat);
      }
   }

   public List<String> getHiddenInLayerBones() {
      return List.of("SantaHat", "HalloweenSkull", "HeadArmor");
   }
}
