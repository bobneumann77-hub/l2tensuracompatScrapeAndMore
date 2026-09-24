package io.github.manasmods.tensura.client.armor;

import io.github.manasmods.tensura.item.armor.custom.PureMagisteelArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PureMagisteelArmorRenderer extends GeoArmorRenderer<PureMagisteelArmorItem> {
   public PureMagisteelArmorRenderer() {
      super(new DefaultedArmorGeoModel(ResourceLocation.fromNamespaceAndPath("tensura", "pure_magisteel")));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/armor/pure_magisteel.png"));
   }
}
