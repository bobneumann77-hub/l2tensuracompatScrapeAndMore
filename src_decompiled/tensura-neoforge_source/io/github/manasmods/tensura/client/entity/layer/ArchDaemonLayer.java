package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.entity.monster.ArchDaemonEntity;
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

public class ArchDaemonLayer {
   private static final ResourceLocation EYES_INNER = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/entity/daemon/arch/face/eyes/archdemon_eyes_inner.png"
   );
   private static final ResourceLocation EYES_OUTER = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/entity/daemon/arch/face/eyes/archdemon_eyes_outer.png"
   );
   private static final ResourceLocation EYES_THIRD_INNER = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/entity/daemon/arch/face/eyes/archdemon_eyes_third_inner.png"
   );
   private static final ResourceLocation EYES_THIRD_OUTER = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/entity/daemon/arch/face/eyes/archdemon_eyes_third_outer.png"
   );
   private static final RenderType EYES_INNER_TYPE = RenderType.entityTranslucent(EYES_INNER);
   private static final RenderType EYES_OUTER_TYPE = RenderType.entityTranslucent(EYES_OUTER);
   private static final RenderType EYES_THIRD_INNER_TYPE = RenderType.entityTranslucent(EYES_THIRD_INNER);
   private static final RenderType EYES_THIRD_OUTER_TYPE = RenderType.entityTranslucent(EYES_THIRD_OUTER);
   private static final ResourceLocation ACCESSORY_ARMBANDS = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/entity/daemon/arch/clothing/accessories/archdemon_accessory_armbands.png"
   );

   public static class Armband extends GeoRenderLayer<ArchDaemonEntity> {
      public Armband(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = daemon.getArmbandColor() != 0 ? daemon.getArmbandColor() : -1;
         if (color != -1) {
            if (daemon.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || daemon.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            RenderType type = RenderType.entityTranslucent(ArchDaemonLayer.ACCESSORY_ARMBANDS);
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(daemon),
                  poseStack,
                  bufferSource,
                  daemon,
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

   public static class Bottom extends GeoRenderLayer<ArchDaemonEntity> {
      public Bottom(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = daemon.getBottomColor() != 0 ? daemon.getBottomColor() : -1;
         if (daemon.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || daemon.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucent(daemon.getBottom().getTextureLocation());
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(daemon),
               poseStack,
               bufferSource,
               daemon,
               type,
               bufferSource.getBuffer(type),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               color
            );
      }
   }

   public static class Coat extends GeoRenderLayer<ArchDaemonEntity> {
      public Coat(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = daemon.getCoatColor() != 0 ? daemon.getCoatColor() : -1;
         if (color != -1) {
            if (daemon.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || daemon.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            RenderType type = RenderType.entityTranslucent(daemon.getCoat().getTextureLocation());
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(daemon),
                  poseStack,
                  bufferSource,
                  daemon,
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

   public static class EyeBrow extends GeoRenderLayer<ArchDaemonEntity> {
      public EyeBrow(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = daemon.getHairColor() != 0 ? daemon.getHairColor() : -1;
         if (daemon.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || daemon.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucent(daemon.getEyeBrow().getTextureLocation());
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(daemon),
               poseStack,
               bufferSource,
               daemon,
               type,
               bufferSource.getBuffer(type),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               color
            );
      }
   }

   public static class EyeLiner extends GeoRenderLayer<ArchDaemonEntity> {
      public EyeLiner(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = daemon.getEyeLinerColor() != 0 ? daemon.getEyeLinerColor() : -1;
         if (color != -1) {
            if (daemon.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || daemon.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            RenderType type = RenderType.entityTranslucent(daemon.getEyeLiner().getTextureLocation());
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(daemon),
                  poseStack,
                  bufferSource,
                  daemon,
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

   public static class Eyes extends GeoRenderLayer<ArchDaemonEntity> {
      public Eyes(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         if (daemon.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && !daemon.isInvisibleTo(player)) {
               int color = TensuraColors.getARGBWithAlpha(daemon.getEyePupilColor() != 0 ? daemon.getEyePupilColor() : -1, 0.1F);
               this.getRenderer()
                  .reRender(
                     this.getDefaultBakedModel(daemon),
                     poseStack,
                     bufferSource,
                     daemon,
                     ArchDaemonLayer.EYES_INNER_TYPE,
                     bufferSource.getBuffer(ArchDaemonLayer.EYES_INNER_TYPE),
                     partialTick,
                     packedLight,
                     OverlayTexture.NO_OVERLAY,
                     color
                  );
               color = TensuraColors.getARGBWithAlpha(daemon.getEyeOuterColor() != 0 ? daemon.getEyeOuterColor() : -1, 0.1F);
               this.getRenderer()
                  .reRender(
                     this.getDefaultBakedModel(daemon),
                     poseStack,
                     bufferSource,
                     daemon,
                     ArchDaemonLayer.EYES_OUTER_TYPE,
                     bufferSource.getBuffer(ArchDaemonLayer.EYES_OUTER_TYPE),
                     partialTick,
                     packedLight,
                     OverlayTexture.NO_OVERLAY,
                     color
                  );
               color = daemon.getThirdEyePupilColor() != 0 ? daemon.getThirdEyePupilColor() : -1;
               if (color != -1) {
                  this.getRenderer()
                     .reRender(
                        this.getDefaultBakedModel(daemon),
                        poseStack,
                        bufferSource,
                        daemon,
                        ArchDaemonLayer.EYES_THIRD_INNER_TYPE,
                        bufferSource.getBuffer(ArchDaemonLayer.EYES_THIRD_INNER_TYPE),
                        partialTick,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        TensuraColors.getARGBWithAlpha(color, 0.1F)
                     );
                  color = TensuraColors.getARGBWithAlpha(daemon.getThirdEyeOuterColor() != 0 ? daemon.getThirdEyeOuterColor() : -1, 0.1F);
                  this.getRenderer()
                     .reRender(
                        this.getDefaultBakedModel(daemon),
                        poseStack,
                        bufferSource,
                        daemon,
                        ArchDaemonLayer.EYES_THIRD_OUTER_TYPE,
                        bufferSource.getBuffer(ArchDaemonLayer.EYES_THIRD_OUTER_TYPE),
                        partialTick,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        color
                     );
               }
            }
         }
      }
   }

   public static class FacialHair extends GeoRenderLayer<ArchDaemonEntity> {
      public FacialHair(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         if (daemon.hasFacialHair()) {
            int color = daemon.getHairColor() != 0 ? daemon.getHairColor() : -1;
            if (daemon.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || daemon.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            RenderType type = RenderType.entityTranslucent(daemon.getFacialHair().getTextureLocation());
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(daemon),
                  poseStack,
                  bufferSource,
                  daemon,
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

   public static class Hair extends GeoRenderLayer<ArchDaemonEntity> {
      public Hair(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = daemon.getHairColor() != 0 ? daemon.getHairColor() : -1;
         if (daemon.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || daemon.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucent(daemon.getHair().getTextureLocation());
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(daemon),
               poseStack,
               bufferSource,
               daemon,
               type,
               bufferSource.getBuffer(type),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               color
            );
      }
   }

   public static class Horn extends GeoRenderLayer<ArchDaemonEntity> {
      public Horn(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = daemon.getHornColor() != 0 ? daemon.getHornColor() : -1;
         if (color != -1) {
            if (daemon.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || daemon.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            RenderType type = RenderType.entityTranslucent(daemon.getHorn().getTextureLocation());
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(daemon),
                  poseStack,
                  bufferSource,
                  daemon,
                  type,
                  bufferSource.getBuffer(type),
                  partialTick,
                  packedLight,
                  OverlayTexture.NO_OVERLAY,
                  color
               );
            if (daemon.hasSecondHorn()) {
               RenderType secondType = RenderType.entityTranslucent(daemon.getSecondHorn().getTextureLocation());
               this.getRenderer()
                  .reRender(
                     this.getDefaultBakedModel(daemon),
                     poseStack,
                     bufferSource,
                     daemon,
                     secondType,
                     bufferSource.getBuffer(secondType),
                     partialTick,
                     packedLight,
                     OverlayTexture.NO_OVERLAY,
                     color
                  );
               if (daemon.hasThirdHorn()) {
                  RenderType thirdType = RenderType.entityTranslucent(daemon.getThirdHorn().getTextureLocation());
                  this.getRenderer()
                     .reRender(
                        this.getDefaultBakedModel(daemon),
                        poseStack,
                        bufferSource,
                        daemon,
                        thirdType,
                        bufferSource.getBuffer(thirdType),
                        partialTick,
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        color
                     );
               }
            }
         }
      }
   }

   public static class NeckAccessory extends GeoRenderLayer<ArchDaemonEntity> {
      public NeckAccessory(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = daemon.getNeckColor() != 0 ? daemon.getNeckColor() : -1;
         if (color != -1) {
            if (daemon.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || daemon.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            RenderType type = RenderType.entityTranslucent(daemon.getNeckAccessory().getTextureLocation());
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(daemon),
                  poseStack,
                  bufferSource,
                  daemon,
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

   public static class Shoe extends GeoRenderLayer<ArchDaemonEntity> {
      public Shoe(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = daemon.getShoeColor() != 0 ? daemon.getShoeColor() : -1;
         if (daemon.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || daemon.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucent(daemon.getShoe().getTextureLocation());
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(daemon),
               poseStack,
               bufferSource,
               daemon,
               type,
               bufferSource.getBuffer(type),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               color
            );
      }
   }

   public static class Teeth extends GeoRenderLayer<ArchDaemonEntity> {
      public Teeth(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         if (daemon.hasTeeth()) {
            int color = Color.WHITE.argbInt();
            if (daemon.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || daemon.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            RenderType type = RenderType.entityTranslucent(daemon.getTeeth().getTextureLocation());
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(daemon),
                  poseStack,
                  bufferSource,
                  daemon,
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

   public static class Top extends GeoRenderLayer<ArchDaemonEntity> {
      public Top(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = daemon.getTopColor() != 0 ? daemon.getTopColor() : -1;
         if (daemon.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || daemon.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucent(daemon.getTop().getTextureLocation());
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(daemon),
               poseStack,
               bufferSource,
               daemon,
               type,
               bufferSource.getBuffer(type),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               color
            );
      }
   }

   public static class Wings extends GeoRenderLayer<ArchDaemonEntity> {
      public Wings(GeoRenderer<ArchDaemonEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         ArchDaemonEntity daemon,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = Color.WHITE.argbInt();
         if (daemon.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || daemon.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucent(daemon.getWings().getTextureLocation());
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(daemon),
               poseStack,
               bufferSource,
               daemon,
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
