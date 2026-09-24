package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.entity.monster.ElementalColossusEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

public class ColossusEmissiveLayer extends GeoRenderLayer<ElementalColossusEntity> {
   public ColossusEmissiveLayer(GeoRenderer<ElementalColossusEntity> renderer) {
      super(renderer);
   }

   public ResourceLocation getTextureResource() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/elemental_colossus/elemental_colossus_emissive.png");
   }

   public void render(
      PoseStack poseStack,
      ElementalColossusEntity entity,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      if (!entity.isSleeping() && !entity.isOrderedToSit()) {
         int color = Color.WHITE.argbInt();
         if (entity.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || entity.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucentEmissive(this.getTextureResource());
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(entity),
               poseStack,
               bufferSource,
               entity,
               type,
               bufferSource.getBuffer(type),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               color
            );
      }
   }
}
