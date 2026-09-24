package io.github.manasmods.tensura.client.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DefaultedArmorGeoModel<T extends GeoAnimatable> extends DefaultedItemGeoModel<T> {
   public DefaultedArmorGeoModel(ResourceLocation resourceLocation) {
      super(resourceLocation);
   }

   protected String subtype() {
      return "armor";
   }

   public ResourceLocation buildFormattedTexturePath(ResourceLocation basePath) {
      return basePath.withPath("textures/models/" + this.subtype() + "/" + basePath.getPath() + ".png");
   }
}
