package io.github.manasmods.tensura.client.entity.misc;

import io.github.manasmods.tensura.entity.magic.misc.DeathTornadoEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DeathTornadoModel extends DefaultedEntityGeoModel<DeathTornadoEntity> {
   public DeathTornadoModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "death_tornado"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(DeathTornadoEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
