package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.AquaFrogEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AquaFrogModel extends DefaultedEntityGeoModel<AquaFrogEntity> {
   public AquaFrogModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "aqua_frog"), false);
   }

   public ResourceLocation getTextureResource(AquaFrogEntity instance) {
      return instance.isKermit()
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/aqua_frog/kermit.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/aqua_frog/aqua_frog.png");
   }

   public RenderType getRenderType(AquaFrogEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
