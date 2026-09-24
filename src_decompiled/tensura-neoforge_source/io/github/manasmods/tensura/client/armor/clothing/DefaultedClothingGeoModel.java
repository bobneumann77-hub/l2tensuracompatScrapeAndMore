package io.github.manasmods.tensura.client.armor.clothing;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DefaultedClothingGeoModel<T extends GeoAnimatable> extends DefaultedItemGeoModel<T> {
   public DefaultedClothingGeoModel(ResourceLocation resourceLocation) {
      super(resourceLocation);
   }

   protected String subtype() {
      return "clothing";
   }

   public ResourceLocation buildFormattedTexturePath(ResourceLocation basePath) {
      return basePath.withPath("textures/models/" + this.subtype() + "/" + basePath.getPath() + ".png");
   }

   public ResourceLocation buildFormattedModelPath(ResourceLocation basePath) {
      return basePath.withPath("geo/armor/generic_clothing.geo.json");
   }
}
