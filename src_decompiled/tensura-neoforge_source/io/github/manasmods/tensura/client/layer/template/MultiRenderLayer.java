package io.github.manasmods.tensura.client.layer.template;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Generated;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public abstract class MultiRenderLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   protected GeoRenderer geoRenderer = null;

   public MultiRenderLayer(RenderLayerParent<T, M> renderLayerParent) {
      super(renderLayerParent);
   }

   public <E extends Entity & GeoEntity> MultiRenderLayer(GeoRenderer<E> entityRenderer) {
      super(null);
      this.geoRenderer = entityRenderer;
   }

   protected abstract <E extends Entity & GeoEntity> void renderGeo(
      PoseStack var1, E var2, BakedGeoModel var3, RenderType var4, MultiBufferSource var5, VertexConsumer var6, float var7, int var8, int var9
   );

   @Generated
   public GeoRenderer getGeoRenderer() {
      return this.geoRenderer;
   }
}
