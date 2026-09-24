package io.github.manasmods.tensura.client.entity.layer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.client.entity.human.PlayerLikeModel;
import io.github.manasmods.tensura.entity.monster.GoblinEntity;
import io.github.manasmods.tensura.entity.variant.GoblinVariant;
import java.util.EnumMap;
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

public class GoblinLayer {
   private static final EnumMap<GoblinVariant.Gender, ResourceLocation> CLOTHING_TEXTURES = buildClothingTextures();
   private static final ResourceLocation BANDAGES_TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/goblin/unisex/bandages.png");
   public static final LayerDefinition FACE_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.01F), false), 64, 64);
   public static final LayerDefinition HAIR_HEAD_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.02F), false), 64, 64);
   public static final LayerDefinition HAIR_BODY_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.3F), false), 64, 64);
   public static final LayerDefinition CLOTHING_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.15F), false), 64, 64);
   public static final LayerDefinition BANDAGES_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.2F), false), 64, 64);
   public static final LayerDefinition HEAD_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.25F), false), 64, 64);
   public static final LayerDefinition TOP_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.25F), false), 64, 64);
   public static final LayerDefinition BOTTOM_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.2F), false), 64, 64);

   private static EnumMap<GoblinVariant.Gender, ResourceLocation> buildClothingTextures() {
      EnumMap<GoblinVariant.Gender, ResourceLocation> map = new EnumMap<>(GoblinVariant.Gender.class);

      for (GoblinVariant.Gender g : GoblinVariant.Gender.values()) {
         map.put(
            g, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/goblin/" + g.getLocation() + "/clothing/loin_" + g.getLocation() + ".png")
         );
      }

      return map;
   }

   private static RenderType getRenderType(ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public static class Bandages<T extends GoblinEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation BANDAGES = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_bandages"), "main");
      private final PlayerModel<T> model = new PlayerModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(BANDAGES), false) {
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of();
         }

         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
         }
      };

      public Bandages(RenderLayerParent<T, PlayerModel<T>> pRenderer) {
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
         if (entity.hasBandages()) {
            int color = Color.WHITE.argbInt();
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
            VertexConsumer vertexconsumer = pBuffer.getBuffer(GoblinLayer.getRenderType(this.getBandagesTexture()));
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

      private ResourceLocation getBandagesTexture() {
         return GoblinLayer.BANDAGES_TEXTURE;
      }
   }

   public static class Bottom<T extends GoblinEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation BOTTOM = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_bottom"), "main");
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
         if (entity.isHobgoblin()) {
            if (entity.getItemBySlot(EquipmentSlot.LEGS).isEmpty()) {
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
               VertexConsumer vertexconsumer = pBuffer.getBuffer(GoblinLayer.getRenderType(this.getBottomTexture(entity)));
               model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
               PlayerLikeModel.sittingPose(entity, model);
               pMatrixStack.pushPose();
               model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
               pMatrixStack.popPose();
            }
         }
      }

      protected PlayerModel<T> model() {
         return this.model;
      }

      private ResourceLocation getBottomTexture(T entity) {
         return GoblinVariant.Bottom.getTextureLocation(entity);
      }
   }

   public static class Clothing<T extends GoblinEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation CLOTHING = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_clothing"), "main");
      private final PlayerModel<T> model = new PlayerModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(CLOTHING), false) {
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of();
         }

         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
         }
      };

      public Clothing(RenderLayerParent<T, PlayerModel<T>> pRenderer) {
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
         VertexConsumer vertexconsumer = pBuffer.getBuffer(GoblinLayer.getRenderType(this.getClothingTexture(entity)));
         model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
         PlayerLikeModel.sittingPose(entity, model);
         pMatrixStack.pushPose();
         model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
         pMatrixStack.popPose();
      }

      protected PlayerModel<T> model() {
         return this.model;
      }

      private ResourceLocation getClothingTexture(T entity) {
         return GoblinLayer.CLOTHING_TEXTURES.get(entity.getGender());
      }
   }

   public static class Face<T extends GoblinEntity> extends RenderLayer<T, HumanoidModel<T>> {
      public static ModelLayerLocation FACE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_face"), "main");
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
         VertexConsumer vertexconsumer = pBuffer.getBuffer(GoblinLayer.getRenderType(entity.getFace().getTextureLocation()));
         model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
         pMatrixStack.pushPose();
         model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
         pMatrixStack.popPose();
      }

      protected HumanoidModel<T> model() {
         return this.model;
      }
   }

   public static class Hair<T extends GoblinEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation HAIR = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_hair"), "main");
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
         VertexConsumer vertexconsumer = pBuffer.getBuffer(GoblinLayer.getRenderType(this.getHairTexture(entity)));
         model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
         pMatrixStack.pushPose();
         model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
         pMatrixStack.popPose();
      }

      protected PlayerModel<T> model() {
         return this.model;
      }

      private ResourceLocation getHairTexture(T entity) {
         return GoblinVariant.Hair.getTextureLocation(entity);
      }
   }

   public static class HairBody<T extends GoblinEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation HAIR_BODY = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_hair_body"), "main");
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
         VertexConsumer vertexconsumer = pBuffer.getBuffer(GoblinLayer.getRenderType(this.getHairTexture(entity)));
         model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
         pMatrixStack.pushPose();
         model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
         pMatrixStack.popPose();
      }

      protected PlayerModel<T> model() {
         return this.model;
      }

      private ResourceLocation getHairTexture(T entity) {
         return GoblinVariant.Hair.getTextureLocation(entity);
      }
   }

   public static class Head<T extends GoblinEntity> extends RenderLayer<T, HumanoidModel<T>> {
      public static ModelLayerLocation HEAD = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_head"), "main");
      private final HumanoidModel<T> model = new HumanoidModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(HEAD)) {
         @NotNull
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of(this.head, this.hat);
         }

         @NotNull
         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of();
         }
      };

      public Head(RenderLayerParent<T, HumanoidModel<T>> pRenderer) {
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
         if (entity.isHobgoblin()) {
            if ((Integer)entity.getEntityData().get(GoblinEntity.HEAD) != -1) {
               if (entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                  int color = entity.getHeadColor() != 0 ? entity.getHeadColor() : -1;
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
                  VertexConsumer vertexconsumer = pBuffer.getBuffer(GoblinLayer.getRenderType(this.getHeadTexture(entity)));
                  model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
                  pMatrixStack.pushPose();
                  model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
                  pMatrixStack.popPose();
               }
            }
         }
      }

      protected HumanoidModel<T> model() {
         return this.model;
      }

      private ResourceLocation getHeadTexture(T entity) {
         return GoblinVariant.Head.getTextureLocation(entity);
      }
   }

   public static class Top<T extends GoblinEntity> extends RenderLayer<T, PlayerModel<T>> {
      public static ModelLayerLocation TOP = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "goblin_top"), "main");
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
         if (entity.isHobgoblin()) {
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
            VertexConsumer vertexconsumer = pBuffer.getBuffer(GoblinLayer.getRenderType(this.getTopTexture(entity)));
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
         return GoblinVariant.Top.getTextureLocation(entity);
      }
   }
}
