package io.github.manasmods.tensura.client.armor;

import io.github.manasmods.tensura.item.armor.custom.AdamantiteArmorItem;
import io.github.manasmods.tensura.item.armor.custom.PierrotMaskItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PierrotMaskRenderer extends GeoArmorRenderer<AdamantiteArmorItem> {
   public PierrotMaskRenderer(PierrotMaskItem.MaskType type) {
      super(new DefaultedArmorGeoModel(ResourceLocation.fromNamespaceAndPath("tensura", type.getId() + "_pierrot_mask")));
   }
}
