package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.entity.monster.HellCaterpillarEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class HellCaterpillarModel extends TensuraEntityGeoModel<HellCaterpillarEntity> {
   public HellCaterpillarModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "hell_caterpillar"), "Head");
   }

   public ResourceLocation getTextureResource(HellCaterpillarEntity instance) {
      return instance.isGehenna()
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hell_moth/gehenna_caterpillar.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hell_moth/hell_caterpillar.png");
   }

   public RenderType getRenderType(HellCaterpillarEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }
}
