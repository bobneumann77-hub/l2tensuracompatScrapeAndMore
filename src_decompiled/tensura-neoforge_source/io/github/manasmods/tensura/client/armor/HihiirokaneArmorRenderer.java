package io.github.manasmods.tensura.client.armor;

import io.github.manasmods.tensura.item.armor.custom.HihiirokaneArmorItem;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class HihiirokaneArmorRenderer extends GeoArmorRenderer<HihiirokaneArmorItem> {
   public HihiirokaneArmorRenderer() {
      super(new DefaultedArmorGeoModel(ResourceLocation.fromNamespaceAndPath("tensura", "hihiirokane")));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/armor/hihiirokane.png"));
   }

   public ResourceLocation getTextureLocation(HihiirokaneArmorItem animatable) {
      if (this.currentStack != null) {
         double inactive = ((Float)this.currentStack.getOrDefault((DataComponentType)TensuraDataComponents.TSUKUMOGAMI_INACTIVE.get(), 0.0F)).floatValue();
         if (inactive >= 0.5) {
            return ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/armor/hihiirokane_inactive.png");
         }
      }

      return super.getTextureLocation(animatable);
   }
}
