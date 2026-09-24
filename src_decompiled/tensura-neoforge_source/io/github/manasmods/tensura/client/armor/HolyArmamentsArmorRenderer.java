package io.github.manasmods.tensura.client.armor;

import io.github.manasmods.tensura.item.armor.custom.HolyArmamentsArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class HolyArmamentsArmorRenderer extends GeoArmorRenderer<HolyArmamentsArmorItem> {
   public HolyArmamentsArmorRenderer() {
      super(new DefaultedArmorGeoModel(ResourceLocation.fromNamespaceAndPath("tensura", "holy_armaments")));
   }

   public RenderType getRenderType(HolyArmamentsArmorItem animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.itemEntityTranslucentCull(texture);
   }
}
