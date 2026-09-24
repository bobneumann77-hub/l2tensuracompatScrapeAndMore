package io.github.manasmods.tensura.client.armor;

import io.github.manasmods.tensura.item.armor.custom.OrichalcumArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class OrichalcumArmorRenderer extends GeoArmorRenderer<OrichalcumArmorItem> {
   public OrichalcumArmorRenderer() {
      super(new DefaultedArmorGeoModel(ResourceLocation.fromNamespaceAndPath("tensura", "orichalcum")));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/armor/orichalcum.png"));
   }
}
