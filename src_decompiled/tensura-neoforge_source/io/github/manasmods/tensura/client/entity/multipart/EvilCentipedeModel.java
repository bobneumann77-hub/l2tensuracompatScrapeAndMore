package io.github.manasmods.tensura.client.entity.multipart;

import io.github.manasmods.tensura.entity.multipart.EvilCentipedeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class EvilCentipedeModel extends DefaultedEntityGeoModel<EvilCentipedeEntity> {
   public EvilCentipedeModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "evil_centipede_head"), false);
   }

   public ResourceLocation getTextureResource(EvilCentipedeEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/evil_centipede/evil_centipede.png");
   }
}
