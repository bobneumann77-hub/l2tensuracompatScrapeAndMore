package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.LandfishEntity;
import net.minecraft.resources.ResourceLocation;

public class LandfishModel extends TensuraEntityGeoModel<LandfishEntity> {
   public LandfishModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "landfish"), "Head");
   }

   public ResourceLocation getTextureResource(LandfishEntity instance) {
      return instance.getVariant().getTextureLocation();
   }
}
