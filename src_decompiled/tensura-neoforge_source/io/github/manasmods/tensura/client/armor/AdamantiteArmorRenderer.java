package io.github.manasmods.tensura.client.armor;

import io.github.manasmods.tensura.item.armor.custom.AdamantiteArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class AdamantiteArmorRenderer extends GeoArmorRenderer<AdamantiteArmorItem> {
   public AdamantiteArmorRenderer() {
      super(new DefaultedArmorGeoModel(ResourceLocation.fromNamespaceAndPath("tensura", "adamantite")));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/armor/adamantite.png"));
   }
}
