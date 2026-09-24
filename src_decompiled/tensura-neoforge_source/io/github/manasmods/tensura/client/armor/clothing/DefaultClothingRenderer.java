package io.github.manasmods.tensura.client.armor.clothing;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class DefaultClothingRenderer<E extends Item & GeoItem> extends GeoArmorRenderer<E> {
   public DefaultClothingRenderer(ResourceLocation texture) {
      super(new DefaultedClothingGeoModel(texture));
   }
}
