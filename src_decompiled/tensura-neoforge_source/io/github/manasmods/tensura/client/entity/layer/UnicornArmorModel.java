package io.github.manasmods.tensura.client.entity.layer;

import io.github.manasmods.tensura.entity.monster.UnicornEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class UnicornArmorModel extends DefaultedEntityGeoModel<UnicornEntity> {
   public UnicornArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "horse"), false);
   }

   public ResourceLocation getModelResource(UnicornEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "geo/layer/unicorn_armor.geo.json");
   }
}
