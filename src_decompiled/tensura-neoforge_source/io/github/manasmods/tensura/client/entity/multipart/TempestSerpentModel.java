package io.github.manasmods.tensura.client.entity.multipart;

import io.github.manasmods.tensura.entity.multipart.TempestSerpentEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class TempestSerpentModel extends DefaultedEntityGeoModel<TempestSerpentEntity> {
   public TempestSerpentModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "tempest_serpent_head"), false);
   }

   public ResourceLocation getTextureResource(TempestSerpentEntity instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/tempest_serpent/tempest_serpent.png");
   }
}
