package io.github.manasmods.tensura.client.entity.barrier;

import io.github.manasmods.tensura.entity.magic.barrier.MegiddoBubbleEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MegiddoBubbleModel<E extends MegiddoBubbleEntity> extends DefaultedEntityGeoModel<E> {
   public MegiddoBubbleModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "megiddo_bubble"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(E animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public ResourceLocation getTextureResource(E instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/megiddo_bubble.png");
   }
}
