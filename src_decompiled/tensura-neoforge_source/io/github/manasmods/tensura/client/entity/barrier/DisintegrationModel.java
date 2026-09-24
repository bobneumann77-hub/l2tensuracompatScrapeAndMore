package io.github.manasmods.tensura.client.entity.barrier;

import io.github.manasmods.tensura.entity.magic.barrier.DisintegrationEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DisintegrationModel extends DefaultedEntityGeoModel<DisintegrationEntity> {
   public DisintegrationModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "disintegration"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(DisintegrationEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
