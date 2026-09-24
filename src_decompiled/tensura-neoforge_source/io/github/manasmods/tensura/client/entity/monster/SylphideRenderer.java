package io.github.manasmods.tensura.client.entity.monster;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SylphideRenderer<E extends LivingEntity & GeoEntity> extends GeoEntityRenderer<E> {
   public SylphideRenderer(Context renderManager) {
      super(renderManager, new SylphideModel());
   }

   protected float getShadowRadius(E entity) {
      return 0.5F;
   }

   protected float getDeathMaxRotation(E animatable) {
      return 0.0F;
   }
}
