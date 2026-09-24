package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.WingedCatEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WingedCatRenderer extends GeoEntityRenderer<WingedCatEntity> {
   public WingedCatRenderer(Context renderManager) {
      super(renderManager, new WingedCatModel());
   }

   protected float getShadowRadius(WingedCatEntity entity) {
      return 1.0F;
   }
}
