package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.AquaFrogEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AquaFrogRenderer extends GeoEntityRenderer<AquaFrogEntity> {
   public AquaFrogRenderer(Context renderManager) {
      super(renderManager, new AquaFrogModel());
   }

   protected float getShadowRadius(AquaFrogEntity entity) {
      return 1.0F;
   }
}
