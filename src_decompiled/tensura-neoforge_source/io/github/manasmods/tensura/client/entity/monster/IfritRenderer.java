package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.layer.IfritEmissiveLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IfritRenderer<E extends LivingEntity & GeoEntity> extends GeoEntityRenderer<E> {
   public IfritRenderer(Context renderManager) {
      super(renderManager, new IfritModel());
      this.addRenderLayer(new IfritEmissiveLayer(this));
   }

   protected float getShadowRadius(E entity) {
      return 0.5F;
   }

   protected float getDeathMaxRotation(E animatable) {
      return 0.0F;
   }
}
