package io.github.manasmods.tensura.client.entity.human;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.TamableAnimal;
import org.jetbrains.annotations.NotNull;

public class TrainingDummyRenderer<T extends TamableAnimal> extends PlayerLikeRenderer<T> {
   public TrainingDummyRenderer(Context pContext) {
      super(pContext, new PlayerLikeModel<>(pContext.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            new HumanoidModel(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            pContext.getModelManager()
         )
      );
   }

   @NotNull
   @Override
   public ResourceLocation getTextureLocation(T entity) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/golem/training_dummy.png");
   }
}
