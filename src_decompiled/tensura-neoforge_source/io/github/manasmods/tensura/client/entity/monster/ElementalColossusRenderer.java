package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.layer.ColossusEmissiveLayer;
import io.github.manasmods.tensura.entity.monster.ElementalColossusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ElementalColossusRenderer extends GeoEntityRenderer<ElementalColossusEntity> {
   public ElementalColossusRenderer(Context renderManager) {
      super(renderManager, new ElementalColossusModel());
      this.addRenderLayer(new ColossusEmissiveLayer(this));
   }

   protected float getShadowRadius(ElementalColossusEntity entity) {
      return 1.0F;
   }

   protected float getDeathMaxRotation(ElementalColossusEntity animatable) {
      return 0.0F;
   }
}
