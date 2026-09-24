package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.SalamanderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SalamanderRenderer extends GeoEntityRenderer<SalamanderEntity> {
   public SalamanderRenderer(Context renderManager) {
      super(renderManager, new SalamanderModel());
   }

   protected float getShadowRadius(SalamanderEntity entity) {
      return 0.5F;
   }
}
