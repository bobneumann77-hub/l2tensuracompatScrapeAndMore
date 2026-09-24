package io.github.manasmods.tensura.client.entity.layer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.client.entity.human.PlayerLikeModel;
import io.github.manasmods.tensura.entity.template.TensuraMerchantEntity;
import io.github.manasmods.tensura.entity.template.subclass.IGender;
import io.github.manasmods.tensura.registry.entity.ai.TensuraVillagerProfessions;
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
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.util.Color;

public class RoyalGuardArmorLayer {
   public static final LayerDefinition ARMOR_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.2F), false), 64, 64);
   public static final LayerDefinition HELMET_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.8F), false), 64, 64);
   public static final LayerDefinition CHEST_LAYER = HumanoidChestModel.createBodyLayer(0.5F);

   protected static ResourceLocation getTexture(TensuraMerchantEntity entity) {
      return entity.wantsToTrade()
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/royal_guard_no_helmet.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/royal_guard.png");
   }

   public static class Armor<T extends TensuraMerchantEntity> extends RenderLayer<T, HumanoidModel<T>> {
      public static ModelLayerLocation ARMORS = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "profession_armors"), "main");
      private final PlayerModel<T> model = new PlayerModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(ARMORS), false) {
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of();
         }

         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
         }
      };

      public Armor(RenderLayerParent<T, HumanoidModel<T>> pRenderer) {
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
         if (entity.getProfession().equals(TensuraVillagerProfessions.ROYAL_GUARD.get())) {
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
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(RoyalGuardArmorLayer.getTexture(entity)));
            model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            PlayerLikeModel.sittingPose(entity, model);
            pMatrixStack.pushPose();
            model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
            pMatrixStack.popPose();
         }
      }

      protected HumanoidModel<T> model() {
         return this.model;
      }
   }

   public static class Chest<T extends TensuraMerchantEntity> extends RenderLayer<T, HumanoidModel<T>> {
      public static ModelLayerLocation CHEST = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "profession_armors_chest"), "main");
      private final HumanoidChestModel<T> model = new HumanoidChestModel(Minecraft.getInstance().getEntityModels().bakeLayer(CHEST));

      public Chest(RenderLayerParent<T, HumanoidModel<T>> pRenderer) {
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
         if (!entity.isBaby()) {
            if (entity instanceof IGender gender && gender.isFemale()) {
               if (entity.getProfession().equals(TensuraVillagerProfessions.ROYAL_GUARD.get())) {
                  int color = Color.WHITE.argbInt();
                  if (entity.isInvisible()) {
                     Player player = Minecraft.getInstance().player;
                     if (player == null || entity.isInvisibleTo(player)) {
                        return;
                     }

                     color = TensuraColors.getARGBWithAlpha(color, 0.1F);
                  }

                  ModelPart parentBody = ((HumanoidModel)this.getParentModel()).body;
                  this.model.body.xRot = parentBody.xRot;
                  this.model.body.yRot = parentBody.yRot;
                  this.model.body.zRot = parentBody.zRot;
                  VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(RoyalGuardArmorLayer.getTexture(entity)));
                  pMatrixStack.pushPose();
                  this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
                  pMatrixStack.popPose();
               }
            }
         }
      }
   }

   public static class Helmet<T extends TensuraMerchantEntity> extends RenderLayer<T, HumanoidModel<T>> {
      public static ModelLayerLocation HELMET = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "profession_helmet"), "main");
      private final PlayerModel<T> model = new PlayerModel<T>(Minecraft.getInstance().getEntityModels().bakeLayer(HELMET), false) {
         @NotNull
         protected Iterable<ModelPart> headParts() {
            return ImmutableList.of(this.head, this.hat);
         }

         @NotNull
         protected Iterable<ModelPart> bodyParts() {
            return ImmutableList.of();
         }
      };

      public Helmet(RenderLayerParent<T, HumanoidModel<T>> pRenderer) {
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
         if (entity.getProfession().equals(TensuraVillagerProfessions.ROYAL_GUARD.get())) {
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
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(RoyalGuardArmorLayer.getTexture(entity)));
            model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            PlayerLikeModel.sittingPose(entity, model);
            pMatrixStack.pushPose();
            model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
            pMatrixStack.popPose();
         }
      }

      protected HumanoidModel<T> model() {
         return this.model;
      }
   }
}
