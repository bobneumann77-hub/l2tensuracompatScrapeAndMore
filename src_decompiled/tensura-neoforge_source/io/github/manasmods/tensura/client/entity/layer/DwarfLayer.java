package io.github.manasmods.tensura.client.entity.layer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.client.entity.human.PlayerLikeModel;
import io.github.manasmods.tensura.entity.human.DwarfEntity;
import io.github.manasmods.tensura.entity.variant.DwarfVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.util.Color;

public class DwarfLayer {
   public static final LayerDefinition FACE_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.015F), false), 64, 64);
   public static final LayerDefinition HAIR_HEAD_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.02F), false), 64, 64);
   public static final LayerDefinition HAIR_BODY_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.3F), false), 64, 64);
   public static final LayerDefinition FACIAL_HAIR_LAYER = LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.25F), 0.0F), 64, 64);
   public static final LayerDefinition TOP_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.25F), false), 64, 64);
   public static final LayerDefinition BOTTOM_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.2F), false), 64, 64);
   public static final LayerDefinition FEET_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.2F), false), 64, 64);
   public static final LayerDefinition CHEST_LAYER = HumanoidChestModel.createBodyLayer();

   private static RenderType getRenderType(ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public static class Bottom<T extends DwarfEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation BOTTOM = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_bottom"), "main");
      private final PlayerModel<T> model = new PlayerModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(BOTTOM), false) {
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of();
         }

         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
         }
      };

      public Bottom(RenderLayerParent<T, PlayerModel<T>> pRenderer) {
         super(pRenderer);
      }

      public void render(
         PoseStack pMatrixStack,
         MultiBufferSource pBuffer,
         int pPackedLight,
         T entity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float partialTicks,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         if (entity.showBottomClothes()) {
            int color = entity.getBottomColor() != 0 ? entity.getBottomColor() : -1;
            if (entity.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || entity.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            PlayerModel<T> model = this.model();
            model.prepareMobModel(entity, pLimbSwing, pLimbSwingAmount, partialTicks);
            ((PlayerModel)this.getParentModel()).copyPropertiesTo(model);
            VertexConsumer vertexconsumer = pBuffer.getBuffer(DwarfLayer.getRenderType(this.getBottomTexture(entity)));
            model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            PlayerLikeModel.sittingPose(entity, model);
            pMatrixStack.pushPose();
            model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
            pMatrixStack.popPose();
         }
      }

      protected PlayerModel<T> model() {
         return this.model;
      }

      private ResourceLocation getBottomTexture(T entity) {
         return DwarfVariant.Bottom.getTextureLocation(entity);
      }
   }

   public static class Chest<T extends DwarfEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation CHEST = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_chest"), "main");
      private final HumanoidChestModel<T> model = new HumanoidChestModel(Minecraft.getInstance().getEntityModels().bakeLayer(CHEST));

      public Chest(RenderLayerParent<T, PlayerModel<T>> pRenderer) {
         super(pRenderer);
      }

      public void render(
         PoseStack pMatrixStack,
         MultiBufferSource pBuffer,
         int pPackedLight,
         T entity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float partialTicks,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         if (entity.isFemale() && !entity.isBaby()) {
            if (entity.showTopClothes()) {
               int color = entity.getTopColor() != 0 ? entity.getTopColor() : -1;
               if (entity.isInvisible()) {
                  Player player = Minecraft.getInstance().player;
                  if (player == null || entity.isInvisibleTo(player)) {
                     return;
                  }

                  color = TensuraColors.getARGBWithAlpha(color, 0.1F);
               }

               ModelPart parentBody = ((PlayerModel)this.getParentModel()).body;
               this.model.body.xRot = parentBody.xRot;
               this.model.body.yRot = parentBody.yRot;
               this.model.body.zRot = parentBody.zRot;
               VertexConsumer vertexconsumer = pBuffer.getBuffer(DwarfLayer.getRenderType(DwarfVariant.Top.getTextureLocation(entity)));
               pMatrixStack.pushPose();
               this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
               pMatrixStack.popPose();
            }
         }
      }
   }

   public static class Face<T extends DwarfEntity> extends RenderLayer<T, HumanoidModel<T>> {
      public static ModelLayerLocation FACE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_face"), "main");
      private final HumanoidModel<T> model = new HumanoidModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(FACE)) {
         @NotNull
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of(this.head, this.hat);
         }

         @NotNull
         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of();
         }
      };

      public Face(RenderLayerParent<T, HumanoidModel<T>> pRenderer) {
         super(pRenderer);
      }

      public void render(
         PoseStack pMatrixStack,
         MultiBufferSource pBuffer,
         int pPackedLight,
         T entity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float partialTicks,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         int color = Color.WHITE.argbInt();
         if (entity.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || entity.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         HumanoidModel<T> model = this.model();
         model.prepareMobModel(entity, pLimbSwing, pLimbSwingAmount, partialTicks);
         ((HumanoidModel)this.getParentModel()).copyPropertiesTo(model);
         VertexConsumer vertexconsumer = pBuffer.getBuffer(DwarfLayer.getRenderType(DwarfVariant.Face.getTextureLocation(entity)));
         model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
         model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
      }

      protected HumanoidModel<T> model() {
         return this.model;
      }
   }

   public static class FacialHair<T extends DwarfEntity> extends RenderLayer<T, HumanoidModel<T>> {
      public static ModelLayerLocation FACIAL_HAIR = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_facial_hair"), "main");
      private final HumanoidModel<T> model = new HumanoidModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(FACIAL_HAIR)) {
         @NotNull
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of(this.head, this.hat);
         }

         @NotNull
         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of();
         }
      };

      public FacialHair(RenderLayerParent<T, HumanoidModel<T>> pRenderer) {
         super(pRenderer);
      }

      public void render(
         PoseStack pMatrixStack,
         MultiBufferSource pBuffer,
         int pPackedLight,
         T entity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float partialTicks,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         if ((Integer)entity.getEntityData().get(DwarfEntity.FACIAL_HAIR) != -1) {
            if (!entity.isBaby()) {
               int color = entity.getHairColor() != 0 ? entity.getHairColor() : -1;
               if (entity.isInvisible()) {
                  Player player = Minecraft.getInstance().player;
                  if (player == null || entity.isInvisibleTo(player)) {
                     return;
                  }

                  color = TensuraColors.getARGBWithAlpha(color, 0.1F);
               }

               HumanoidModel<T> model = this.model();
               model.prepareMobModel(entity, pLimbSwing, pLimbSwingAmount, partialTicks);
               ((HumanoidModel)this.getParentModel()).copyPropertiesTo(model);
               VertexConsumer vertexconsumer = pBuffer.getBuffer(DwarfLayer.getRenderType(this.getHeadTexture(entity)));
               model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
               pMatrixStack.pushPose();
               model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
               pMatrixStack.popPose();
            }
         }
      }

      protected HumanoidModel<T> model() {
         return this.model;
      }

      private ResourceLocation getHeadTexture(T entity) {
         return DwarfVariant.FacialHair.getTextureLocation(entity);
      }
   }

   public static class Feet<T extends DwarfEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation FEET = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_feet"), "main");
      private final PlayerModel<T> model = new PlayerModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(FEET), false) {
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of();
         }

         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
         }
      };

      public Feet(RenderLayerParent<T, PlayerModel<T>> pRenderer) {
         super(pRenderer);
      }

      public void render(
         PoseStack pMatrixStack,
         MultiBufferSource pBuffer,
         int pPackedLight,
         T entity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float partialTicks,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         if (entity.showBoots()) {
            int color = entity.getFeetColor() != 0 ? entity.getFeetColor() : -1;
            if (entity.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || entity.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            PlayerModel<T> model = this.model();
            model.prepareMobModel(entity, pLimbSwing, pLimbSwingAmount, partialTicks);
            ((PlayerModel)this.getParentModel()).copyPropertiesTo(model);
            VertexConsumer vertexconsumer = pBuffer.getBuffer(DwarfLayer.getRenderType(this.getFeetTexture(entity)));
            model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            PlayerLikeModel.sittingPose(entity, model);
            pMatrixStack.pushPose();
            model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
            pMatrixStack.popPose();
         }
      }

      protected PlayerModel<T> model() {
         return this.model;
      }

      private ResourceLocation getFeetTexture(T entity) {
         return DwarfVariant.Feet.getTextureLocation(entity);
      }
   }

   public static class Hair<T extends DwarfEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation HAIR = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_hair"), "main");
      private final PlayerModel<T> model = new PlayerModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(HAIR), false) {
         @NotNull
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of(this.head, this.hat);
         }

         @NotNull
         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of();
         }
      };

      public Hair(RenderLayerParent<T, PlayerModel<T>> pRenderer) {
         super(pRenderer);
      }

      public void render(
         PoseStack pMatrixStack,
         MultiBufferSource pBuffer,
         int pPackedLight,
         T entity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float partialTicks,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         if (entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            int color = entity.getHairColor() != 0 ? entity.getHairColor() : -1;
            if (entity.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || entity.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            PlayerModel<T> model = this.model();
            model.prepareMobModel(entity, pLimbSwing, pLimbSwingAmount, partialTicks);
            ((PlayerModel)this.getParentModel()).copyPropertiesTo(model);
            VertexConsumer vertexconsumer = pBuffer.getBuffer(DwarfLayer.getRenderType(this.getHairTexture(entity)));
            model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            pMatrixStack.pushPose();
            model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
            pMatrixStack.popPose();
         }
      }

      protected PlayerModel<T> model() {
         return this.model;
      }

      private ResourceLocation getHairTexture(T entity) {
         return DwarfVariant.Hair.getTextureLocation(entity);
      }
   }

   public static class HairBody<T extends DwarfEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation HAIR_BODY = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_hair_body"), "main");
      private final PlayerModel<T> model = new PlayerModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(HAIR_BODY), false) {
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of();
         }

         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
         }
      };

      public HairBody(RenderLayerParent<T, PlayerModel<T>> pRenderer) {
         super(pRenderer);
      }

      public void render(
         PoseStack pMatrixStack,
         MultiBufferSource pBuffer,
         int pPackedLight,
         T entity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float partialTicks,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         int color = entity.getHairColor() != 0 ? entity.getHairColor() : -1;
         if (entity.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || entity.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         PlayerModel<T> model = this.model();
         model.prepareMobModel(entity, pLimbSwing, pLimbSwingAmount, partialTicks);
         ((PlayerModel)this.getParentModel()).copyPropertiesTo(model);
         VertexConsumer vertexconsumer = pBuffer.getBuffer(DwarfLayer.getRenderType(this.getHairTexture(entity)));
         model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
         pMatrixStack.pushPose();
         model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
         pMatrixStack.popPose();
      }

      protected PlayerModel<T> model() {
         return this.model;
      }

      private ResourceLocation getHairTexture(T entity) {
         return DwarfVariant.Hair.getTextureLocation(entity);
      }
   }

   public static class Top<T extends DwarfEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation TOP = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_top"), "main");
      private final PlayerModel<T> model = new PlayerModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(TOP), false) {
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of();
         }

         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
         }
      };

      public Top(RenderLayerParent<T, PlayerModel<T>> pRenderer) {
         super(pRenderer);
      }

      public void render(
         PoseStack pMatrixStack,
         MultiBufferSource pBuffer,
         int pPackedLight,
         T entity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float partialTicks,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         if (entity.showTopClothes()) {
            int color = entity.getTopColor() != 0 ? entity.getTopColor() : -1;
            if (entity.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || entity.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            PlayerModel<T> model = this.model();
            model.prepareMobModel(entity, pLimbSwing, pLimbSwingAmount, partialTicks);
            ((PlayerModel)this.getParentModel()).copyPropertiesTo(model);
            VertexConsumer vertexconsumer = pBuffer.getBuffer(DwarfLayer.getRenderType(this.getTopTexture(entity)));
            model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            PlayerLikeModel.sittingPose(entity, model);
            pMatrixStack.pushPose();
            model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
            pMatrixStack.popPose();
         }
      }

      protected PlayerModel<T> model() {
         return this.model;
      }

      private ResourceLocation getTopTexture(T entity) {
         return DwarfVariant.Top.getTextureLocation(entity);
      }
   }
}
