package io.github.manasmods.tensura.client.entity.field;

import io.github.manasmods.tensura.client.TensuraRenderTypes;
import io.github.manasmods.tensura.entity.magic.field.MagicExplosion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MagicExplosionModel extends DefaultedEntityGeoModel<MagicExplosion> {
   public MagicExplosionModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "magic_explosion"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(MagicExplosion animatable, ResourceLocation texture) {
      return TensuraRenderTypes.getUnlitTranslucent(texture);
   }
}
