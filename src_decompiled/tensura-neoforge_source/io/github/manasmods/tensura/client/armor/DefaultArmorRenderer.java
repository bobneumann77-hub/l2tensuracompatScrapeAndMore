package io.github.manasmods.tensura.client.armor;

import io.github.manasmods.tensura.item.armor.custom.ArmorsaurusArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class DefaultArmorRenderer extends GeoArmorRenderer<ArmorsaurusArmorItem> {
   public DefaultArmorRenderer(ResourceLocation armorTexture) {
      super(new DefaultedArmorGeoModel(armorTexture));
   }
}
