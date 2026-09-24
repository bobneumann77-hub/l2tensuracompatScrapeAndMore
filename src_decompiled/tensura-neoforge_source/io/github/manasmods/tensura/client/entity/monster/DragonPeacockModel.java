package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.DragonPeacockEntity;
import io.github.manasmods.tensura.entity.variant.PeacockVariant;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DragonPeacockModel extends DefaultedEntityGeoModel<DragonPeacockEntity> {
   public DragonPeacockModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "dragon_peacock"), true);
   }

   public ResourceLocation getTextureResource(DragonPeacockEntity instance) {
      return PeacockVariant.LOCATION_BY_VARIANT.get(instance.getVariant());
   }
}
