package io.github.manasmods.tensura.client.entity.field;

import io.github.manasmods.tensura.entity.magic.field.haki.HakiField;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HakiModel extends DefaultedEntityGeoModel<HakiField> {
   public HakiModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "haki"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public ResourceLocation getTextureResource(HakiField instance) {
      ResourceLocation location = ResourceLocation.fromNamespaceAndPath(
         "tensura", "textures/entity/field/haki/haki_" + instance.getVariant().getName() + ".png"
      );
      AnimatableTexture.setAndUpdate(location);
      return location;
   }

   public RenderType getRenderType(HakiField animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
