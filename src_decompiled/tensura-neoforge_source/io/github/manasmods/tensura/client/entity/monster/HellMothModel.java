package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.HellMothEntity;
import net.minecraft.resources.ResourceLocation;

public class HellMothModel extends TensuraEntityGeoModel<HellMothEntity> {
   public HellMothModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "hell_moth"), "head");
   }

   public ResourceLocation getTextureResource(HellMothEntity instance) {
      if (instance.hasCustomName() && "Mothra".equalsIgnoreCase(instance.getName().getString())) {
         return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hell_moth/mothra.png");
      } else {
         return instance.isGehenna()
            ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hell_moth/gehenna_moth.png")
            : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hell_moth/hell_moth.png");
      }
   }
}
