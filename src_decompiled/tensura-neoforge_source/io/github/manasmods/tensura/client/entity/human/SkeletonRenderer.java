package io.github.manasmods.tensura.client.entity.human;

import io.github.manasmods.tensura.client.entity.layer.SkeletonClothingLayer;
import io.github.manasmods.tensura.entity.human.undead.SkeletonHumanoidEntity;
import io.github.manasmods.tensura.entity.variant.SkeletonVariant;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SkeletonRenderer<T extends SkeletonHumanoidEntity> extends PlayerLikeSkeletonRenderer<T> {
   public SkeletonRenderer(Context pContext) {
      super(pContext, 0.5F);
      this.addLayer(new SkeletonClothingLayer(this, pContext.getModelSet(), ModelLayers.STRAY_OUTER_LAYER));
   }

   @NotNull
   public ResourceLocation getTextureLocation(T entity) {
      return SkeletonVariant.LOCATION_BY_VARIANT.get(entity.getVariant());
   }
}
