package io.github.manasmods.tensura.client.entity.monster;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AkashRenderer<E extends LivingEntity & GeoEntity> extends GeoEntityRenderer<E> {
   public AkashRenderer(Context renderManager) {
      super(renderManager, new AkashModel());
   }

   protected float getShadowRadius(E entity) {
      return 0.5F;
   }

   protected float getDeathMaxRotation(E animatable) {
      return 0.0F;
   }
}
