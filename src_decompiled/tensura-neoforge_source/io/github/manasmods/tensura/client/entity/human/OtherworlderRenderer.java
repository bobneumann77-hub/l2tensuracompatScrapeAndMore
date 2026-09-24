package io.github.manasmods.tensura.client.entity.human;

import io.github.manasmods.tensura.entity.template.subclass.IOtherworlder;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.TamableAnimal;
import org.jetbrains.annotations.NotNull;

public class OtherworlderRenderer<T extends TamableAnimal & IOtherworlder> extends PlayerLikeRenderer<T> {
   public OtherworlderRenderer(Context pContext, boolean slim) {
      super(pContext, new PlayerLikeModel<>(pContext.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim), 0.5F);
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            new HumanoidModel(pContext.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(pContext.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)),
            pContext.getModelManager()
         )
      );
   }

   @NotNull
   @Override
   public ResourceLocation getTextureLocation(T entity) {
      return entity.getTextureLocation();
   }
}
