package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.entity.monster.LizardmanEntity;
import io.github.manasmods.tensura.entity.variant.LizardmanVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

public class LizardmanLayer {
   private static final ResourceLocation LIZARDMAN_PANTS = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/entity/lizardman/clothes/lizardman_pants.png"
   );
   private static final ResourceLocation LIZARDMAN_ARM_ACCESSORY = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/entity/lizardman/clothes/lizardman_arm_accessory.png"
   );

   public static class Bandage extends GeoRenderLayer<LizardmanEntity> {
      public Bandage(GeoRenderer<LizardmanEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         LizardmanEntity lizard,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         if (lizard.hasBandage()) {
            int color = Color.WHITE.argbInt();
            if (lizard.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || lizard.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            RenderType type = RenderType.entityTranslucent(LizardmanLayer.LIZARDMAN_ARM_ACCESSORY);
            this.getRenderer()
               .reRender(
                  this.getDefaultBakedModel(lizard),
                  poseStack,
                  bufferSource,
                  lizard,
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

   public static class Bottom extends GeoRenderLayer<LizardmanEntity> {
      public Bottom(GeoRenderer<LizardmanEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         LizardmanEntity lizard,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = lizard.getBottomColor() != 0 ? lizard.getBottomColor() : -1;
         if (lizard.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || lizard.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucent(LizardmanLayer.LIZARDMAN_PANTS);
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(lizard),
               poseStack,
               bufferSource,
               lizard,
               type,
               bufferSource.getBuffer(type),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               color
            );
      }
   }

   public static class Hair extends GeoRenderLayer<LizardmanEntity> {
      public Hair(GeoRenderer<LizardmanEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         LizardmanEntity lizard,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         if (lizard.getHair() != LizardmanVariant.Hair.BALD) {
            if (lizard.getHair() != LizardmanVariant.Hair.HELMET || lizard.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
               int color = lizard.getHairColor() != 0 ? lizard.getHairColor() : -1;
               if (lizard.isInvisible()) {
                  Player player = Minecraft.getInstance().player;
                  if (player == null || lizard.isInvisibleTo(player)) {
                     return;
                  }

                  color = TensuraColors.getARGBWithAlpha(color, 0.1F);
               }

               RenderType type = RenderType.entityTranslucent(lizard.getHair().getLocation());
               this.getRenderer()
                  .reRender(
                     this.getDefaultBakedModel(lizard),
                     poseStack,
                     bufferSource,
                     lizard,
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
   }

   public static class Top extends GeoRenderLayer<LizardmanEntity> {
      public Top(GeoRenderer<LizardmanEntity> renderer) {
         super(renderer);
      }

      public void render(
         PoseStack poseStack,
         LizardmanEntity lizard,
         BakedGeoModel bakedModel,
         RenderType renderType,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         int color = lizard.getTopColor() != 0 ? lizard.getTopColor() : -1;
         if (lizard.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || lizard.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityTranslucent(lizard.getTop().getLocation());
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(lizard),
               poseStack,
               bufferSource,
               lizard,
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
