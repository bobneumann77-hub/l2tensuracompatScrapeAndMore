package io.github.manasmods.tensura.client.item;

import io.github.manasmods.tensura.item.tool.custom.ArmorsaurusShieldItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ArmorsaurusShieldItemRenderer extends GeoItemRenderer<ArmorsaurusShieldItem> {
   public ArmorsaurusShieldItemRenderer() {
      super(new DefaultedItemGeoModel<ArmorsaurusShieldItem>(ResourceLocation.fromNamespaceAndPath("tensura", "armorsaurus_shield")) {
         public ResourceLocation getTextureResource(ArmorsaurusShieldItem object) {
            return ResourceLocation.fromNamespaceAndPath("tensura", "textures/item/armorsaurus_shield_model.png");
         }
      });
   }
}
