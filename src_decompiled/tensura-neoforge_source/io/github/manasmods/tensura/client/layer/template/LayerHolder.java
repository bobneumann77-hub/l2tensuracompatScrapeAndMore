package io.github.manasmods.tensura.client.layer.template;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.client.layer.multi.DiamondPathLayer;
import io.github.manasmods.tensura.client.layer.multi.EarthLockLayer;
import io.github.manasmods.tensura.client.layer.multi.EmbracementLayer;
import io.github.manasmods.tensura.client.layer.multi.FaultFieldLayer;
import io.github.manasmods.tensura.client.layer.multi.FrozenLayer;
import io.github.manasmods.tensura.client.layer.multi.HarvestFestivalLayer;
import io.github.manasmods.tensura.client.layer.multi.MadOgreLayer;
import io.github.manasmods.tensura.client.layer.multi.MagicBarrierLayer;
import io.github.manasmods.tensura.client.layer.multi.OppressionLayer;
import io.github.manasmods.tensura.client.layer.multi.PetrificationLayer;
import io.github.manasmods.tensura.client.layer.multi.WebbedLayer;
import io.github.manasmods.tensura.client.layer.multi.WindLayer;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class LayerHolder<T extends Entity & GeoEntity, M extends EntityModel<T>> extends GeoRenderLayer<T> {
   protected final List<MultiRenderLayer<T, M>> layers = Lists.newArrayList();

   public LayerHolder(GeoEntityRenderer<T> entityRenderer) {
      super(entityRenderer);
      this.addLayer(new FrozenLayer<>(entityRenderer));
      this.addLayer(new PetrificationLayer<>(entityRenderer));
      this.addLayer(new OppressionLayer<>(entityRenderer));
      this.addLayer(new WebbedLayer<>(entityRenderer));
      this.addLayer(new DiamondPathLayer<>(entityRenderer));
      this.addLayer(new EarthLockLayer<>(entityRenderer));
      this.addLayer(new EmbracementLayer<>(entityRenderer));
      this.addLayer(new FaultFieldLayer<>(entityRenderer));
      this.addLayer(new HarvestFestivalLayer<>(entityRenderer));
      this.addLayer(new MadOgreLayer<>(entityRenderer));
      this.addLayer(new MagicBarrierLayer<>(entityRenderer));
      this.addLayer(new WindLayer<>(entityRenderer));
   }

   protected final boolean addLayer(MultiRenderLayer<T, M> renderLayer) {
      return this.layers.add(renderLayer);
   }

   public void render(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      if (!animatable.isSpectator()) {
         List<GeoBone> list = Lists.newArrayList();
         if (this.getRenderer().getGeoModel() instanceof TensuraEntityGeoModel<T> model) {
            for (String string : model.getHiddenInLayerBones()) {
               Optional<GeoBone> optional = bakedModel.getBone(string);
               if (!optional.isEmpty() && !optional.get().isHidden()) {
                  optional.get().setHidden(true);
                  list.add(optional.get());
               }
            }
         }

         for (MultiRenderLayer<T, M> layer : this.layers) {
            layer.renderGeo(poseStack, animatable, bakedModel, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
         }

         for (GeoBone geoBone : list) {
            geoBone.setHidden(false);
         }
      }
   }
}
