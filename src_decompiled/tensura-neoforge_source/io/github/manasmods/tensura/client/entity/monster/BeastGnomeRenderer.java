package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.BeastGnomeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BeastGnomeRenderer extends GeoEntityRenderer<BeastGnomeEntity> {
   public BeastGnomeRenderer(Context renderManager) {
      super(renderManager, new BeastGnomeModel());
   }

   protected float getShadowRadius(BeastGnomeEntity entity) {
      return entity.isShrunk() ? 0.2F : 2.0F;
   }

   protected float getDeathMaxRotation(BeastGnomeEntity animatable) {
      return 0.0F;
   }
}
