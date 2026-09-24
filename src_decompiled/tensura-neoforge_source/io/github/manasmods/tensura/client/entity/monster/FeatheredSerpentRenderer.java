package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.FeatheredSerpentEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FeatheredSerpentRenderer extends GeoEntityRenderer<FeatheredSerpentEntity> {
   public FeatheredSerpentRenderer(Context renderManager) {
      super(renderManager, new FeatheredSerpentModel());
   }

   protected float getShadowRadius(FeatheredSerpentEntity entity) {
      return 1.0F;
   }
}
