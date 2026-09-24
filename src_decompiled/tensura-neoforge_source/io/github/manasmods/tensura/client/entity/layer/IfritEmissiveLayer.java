package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

public class IfritEmissiveLayer<E extends LivingEntity & GeoEntity> extends GeoRenderLayer<E> {
   public IfritEmissiveLayer(GeoRenderer<E> renderer) {
      super(renderer);
   }

   public ResourceLocation getTextureResource() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/ifrit/ifrit_emissive.png");
   }

   public void render(
      PoseStack poseStack,
      E ifrit,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      int color = Color.WHITE.argbInt();
      if (ifrit.isInvisible()) {
         Player player = Minecraft.getInstance().player;
         if (player == null || ifrit.isInvisibleTo(player)) {
            return;
         }

         color = TensuraColors.getARGBWithAlpha(color, 0.1F);
      }

      RenderType type = RenderType.entityTranslucentEmissive(this.getTextureResource());
      this.getRenderer()
         .reRender(
            this.getDefaultBakedModel(ifrit),
            poseStack,
            bufferSource,
            ifrit,
            type,
            bufferSource.getBuffer(type),
            partialTick,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            color
         );
   }
}
