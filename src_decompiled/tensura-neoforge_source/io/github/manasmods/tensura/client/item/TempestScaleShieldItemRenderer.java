package io.github.manasmods.tensura.client.item;

import io.github.manasmods.tensura.item.tool.custom.TempestScaleShieldItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TempestScaleShieldItemRenderer extends GeoItemRenderer<TempestScaleShieldItem> {
   public TempestScaleShieldItemRenderer() {
      super(new DefaultedItemGeoModel<TempestScaleShieldItem>(ResourceLocation.fromNamespaceAndPath("tensura", "tempest_scale_shield")) {
         public ResourceLocation getTextureResource(TempestScaleShieldItem object) {
            return ResourceLocation.fromNamespaceAndPath("tensura", "textures/item/tempest_scale_shield_model.png");
         }
      });
   }
}
