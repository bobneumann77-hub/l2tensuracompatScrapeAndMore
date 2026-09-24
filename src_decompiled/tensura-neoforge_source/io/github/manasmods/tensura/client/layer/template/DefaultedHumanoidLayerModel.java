package io.github.manasmods.tensura.client.layer.template;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedGeoModel;

public class DefaultedHumanoidLayerModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {
   public DefaultedHumanoidLayerModel(ResourceLocation assetSubpath) {
      super(assetSubpath);
   }

   protected String subtype() {
      return "layer";
   }

   public DefaultedHumanoidLayerModel<T> withAltModel(ResourceLocation altPath) {
      return (DefaultedHumanoidLayerModel<T>)super.withAltModel(altPath);
   }

   public DefaultedHumanoidLayerModel<T> withAltAnimations(ResourceLocation altPath) {
      return (DefaultedHumanoidLayerModel<T>)super.withAltAnimations(altPath);
   }

   public DefaultedHumanoidLayerModel<T> withAltTexture(ResourceLocation altPath) {
      return (DefaultedHumanoidLayerModel<T>)super.withAltTexture(altPath);
   }

   public ResourceLocation buildFormattedTexturePath(ResourceLocation basePath) {
      return basePath.withPath("textures/models/" + this.subtype() + "/geo/" + basePath.getPath() + ".png");
   }
}
