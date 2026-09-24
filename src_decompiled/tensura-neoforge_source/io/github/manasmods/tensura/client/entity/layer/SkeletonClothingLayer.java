package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.tensura.client.entity.human.PlayerLikeSkeletonModel;
import io.github.manasmods.tensura.entity.human.undead.SkeletonHumanoidEntity;
import io.github.manasmods.tensura.entity.variant.SkeletonVariant;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class SkeletonClothingLayer<T extends SkeletonHumanoidEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private final PlayerLikeSkeletonModel<T> layerModel;

   public SkeletonClothingLayer(RenderLayerParent<T, M> renderLayerParent, EntityModelSet entityModelSet, ModelLayerLocation modelLayerLocation) {
      super(renderLayerParent);
      this.layerModel = new PlayerLikeSkeletonModel<>(entityModelSet.bakeLayer(modelLayerLocation));
   }

   public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T mob, float f, float g, float h, float j, float k, float l) {
      ResourceLocation location = SkeletonVariant.OVERLAY_BY_VARIANT.get(mob.getVariant());
      if (location != null) {
         coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, location, poseStack, multiBufferSource, i, mob, f, g, j, k, l, h, -1);
      }
   }
}
