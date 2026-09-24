package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.entity.monster.OrcEntity;
import io.github.manasmods.tensura.entity.variant.OrcVariant;
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

public class OrcLayer {
   private static final ResourceLocation ORC_PANTS = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/clothes/orc_pants.png");
   private static final ResourceLocation ORC_BELT = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/clothes/orc_belt.png");
   private static final ResourceLocation ORC_BOOTS = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/clothes/orc_boots.png");
   private static final ResourceLocation ORC_BANDAGE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/clothes/orc_bandage.png");
   private static final ResourceLocation ORC_NECKLACE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/clothes/orc_necklace.png");

   public static class Bandage extends GeoRenderLayer<OrcEntity> {
      public Bandage(GeoRenderer<OrcEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         OrcEntity orc,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         if (orc.hasBandage()) {
            int color = Color.WHITE.argbInt();
            if (orc.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || orc.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            RenderType type = RenderType.entityTranslucent(OrcLayer.ORC_BANDAGE);
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(orc),
                  poseStack,
                  bufferSource,
                  orc,
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

   public static class Belt extends GeoRenderLayer<OrcEntity> {
      public Belt(GeoRenderer<OrcEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         OrcEntity orc,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         if (orc.getBeltColor() != 0) {
            int color = orc.getBeltColor();
            if (orc.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || orc.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            poseStack.pushPose();
            poseStack.scale(1.001F, 1.001F, 1.001F);
            RenderType type = RenderType.entityTranslucent(OrcLayer.ORC_BELT);
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(orc),
                  poseStack,
                  bufferSource,
                  orc,
                  type,
                  bufferSource.getBuffer(type),
                  partialTick,
                  packedLight,
                  OverlayTexture.NO_OVERLAY,
                  color
               );
            poseStack.popPose();
         }
      }
   }

   public static class Boots extends GeoRenderLayer<OrcEntity> {
      public Boots(GeoRenderer<OrcEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         OrcEntity orc,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = orc.getBootsColor() != 0 ? orc.getBootsColor() : -1;
         if (orc.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || orc.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucent(OrcLayer.ORC_BOOTS);
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(orc),
               poseStack,
               bufferSource,
               orc,
               type,
               bufferSource.getBuffer(type),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               color
            );
      }
   }

   public static class Bottom extends GeoRenderLayer<OrcEntity> {
      public Bottom(GeoRenderer<OrcEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         OrcEntity orc,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = orc.getBottomColor() != 0 ? orc.getBottomColor() : -1;
         if (orc.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || orc.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucent(OrcLayer.ORC_PANTS);
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(orc),
               poseStack,
               bufferSource,
               orc,
               type,
               bufferSource.getBuffer(type),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               color
            );
      }
   }

   public static class Neck extends GeoRenderLayer<OrcEntity> {
      public Neck(GeoRenderer<OrcEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         OrcEntity orc,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         if (orc.getNeck() != OrcVariant.Neck.EMPTY) {
            int color = orc.getNeckColor() != 0 ? orc.getNeckColor() : -1;
            if (orc.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || orc.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            poseStack.pushPose();
            poseStack.scale(1.001F, 1.001F, 1.001F);
            RenderType type = RenderType.entityTranslucent(orc.getNeck().getLocation());
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(orc),
                  poseStack,
                  bufferSource,
                  orc,
                  type,
                  bufferSource.getBuffer(type),
                  partialTick,
                  packedLight,
                  OverlayTexture.NO_OVERLAY,
                  color
               );
            poseStack.popPose();
         }
      }
   }

   public static class Necklace extends GeoRenderLayer<OrcEntity> {
      public Necklace(GeoRenderer<OrcEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         OrcEntity orc,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         if (orc.hasNecklace()) {
            int color = Color.WHITE.argbInt();
            if (orc.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || orc.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            RenderType type = RenderType.entityTranslucent(OrcLayer.ORC_NECKLACE);
            poseStack.pushPose();
            poseStack.scale(1.002F, 1.002F, 1.002F);
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(orc),
                  poseStack,
                  bufferSource,
                  orc,
                  type,
                  bufferSource.getBuffer(type),
                  partialTick,
                  packedLight,
                  OverlayTexture.NO_OVERLAY,
                  color
               );
            poseStack.popPose();
         }
      }
   }

   public static class Top extends GeoRenderLayer<OrcEntity> {
      public Top(GeoRenderer<OrcEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         OrcEntity orc,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = orc.getTopColor() != 0 ? orc.getTopColor() : -1;
         if (orc.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || orc.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucent(orc.getTop().getLocation());
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(orc),
               poseStack,
               bufferSource,
               orc,
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
