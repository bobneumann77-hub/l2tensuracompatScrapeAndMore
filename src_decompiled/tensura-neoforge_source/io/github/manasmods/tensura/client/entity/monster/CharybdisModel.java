package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.CharybdisEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CharybdisModel extends DefaultedEntityGeoModel<CharybdisEntity> {
   public CharybdisModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "charybdis"), true);
   }

   public ResourceLocation getTextureResource(CharybdisEntity instance) {
      if (instance.hasCustomName()) {
         if ("Orcinus".equalsIgnoreCase(instance.getName().getString())) {
            return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/charybdis/orcinus_orta.png");
         }

         if ("Orcinus Orta".equalsIgnoreCase(instance.getName().getString())) {
            return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/charybdis/orcinus_orta.png");
         }
      }

      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/charybdis/charybdis.png");
   }
}
