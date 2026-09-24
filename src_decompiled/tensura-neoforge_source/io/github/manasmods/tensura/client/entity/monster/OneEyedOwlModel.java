package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.OneEyedOwlEntity;
import io.github.manasmods.tensura.entity.variant.OneEyedOwlVariant;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class OneEyedOwlModel extends DefaultedEntityGeoModel<OneEyedOwlEntity> {
   public OneEyedOwlModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "one_eyed_owl"), true);
   }

   public ResourceLocation getTextureResource(OneEyedOwlEntity instance) {
      return instance.hasCustomName() && "Sauron".equalsIgnoreCase(instance.getName().getString())
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/one_eyed_owl/one_eyed_owl_sauron.png")
         : OneEyedOwlVariant.LOCATION_BY_VARIANT.get(instance.getVariant());
   }
}
