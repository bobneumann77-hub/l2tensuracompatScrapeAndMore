package io.github.manasmods.tensura.client.entity.human;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PlayerLikeRenderer<T extends TamableAnimal> extends LivingEntityRenderer<T, PlayerLikeModel<T>> {
   private static final ResourceLocation BLANK_TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/blank_texture.png");
   private static final Vec3 SHIFT_OFFSET = new Vec3(0.0, -0.125, 0.0);

   public PlayerLikeRenderer(Context pContext, PlayerLikeModel<T> pModel, float pShadowRadius) {
      super(pContext, pModel, pShadowRadius);
      this.addLayer(new ItemInHandLayer(this, pContext.getItemInHandRenderer()));
      this.addLayer(new ArrowLayer(pContext, this));
      this.addLayer(new CustomHeadLayer(this, pContext.getModelSet(), pContext.getItemInHandRenderer()));
      this.addLayer(new ElytraLayer(this, pContext.getModelSet()));
      this.addLayer(new SpinAttackEffectLayer(this, pContext.getModelSet()));
   }

   public ResourceLocation getTextureLocation(T pEntity) {
      return BLANK_TEXTURE;
   }

   protected void scale(T entity, PoseStack pMatrixStack, float pPartialTickTime) {
      float scale = 0.9375F;
      pMatrixStack.scale(scale, scale, scale);
   }

   public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      this.setModelProperties(pEntity);
      super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
   }

   protected void renderNameTag(T pEntity, Component pDisplayName, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, float f) {
      if (this.shouldShowName(pEntity)) {
         super.renderNameTag(pEntity, pDisplayName, pMatrixStack, pBuffer, pPackedLight, f);
      }
   }

   protected boolean shouldShowName(T pEntity) {
      return super.shouldShowName(pEntity)
         && (pEntity.shouldShowName() || pEntity.hasCustomName() && pEntity == this.entityRenderDispatcher.crosshairPickEntity);
   }

   public Vec3 getRenderOffset(T pEntity, float pPartialTicks) {
      return pEntity.isShiftKeyDown() ? SHIFT_OFFSET : super.getRenderOffset(pEntity, pPartialTicks);
   }

   private void setModelProperties(T entity) {
      PlayerModel<T> model = (PlayerModel<T>)this.getModel();
      if (entity.isSpectator()) {
         model.setAllVisible(false);
         model.head.visible = true;
         model.hat.visible = true;
      } else {
         model.setAllVisible(true);
         model.crouching = entity.isShiftKeyDown();
         ArmPose armPose = this.getArmPose(entity, InteractionHand.MAIN_HAND);
         ArmPose armPoseOffhand = this.getArmPose(entity, InteractionHand.OFF_HAND);
         if (armPose.isTwoHanded()) {
            armPoseOffhand = entity.getOffhandItem().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
         }

         if (entity.getMainArm() == HumanoidArm.RIGHT) {
            model.rightArmPose = armPose;
            model.leftArmPose = armPoseOffhand;
         } else {
            model.rightArmPose = armPoseOffhand;
            model.leftArmPose = armPose;
         }
      }
   }

   protected ArmPose getArmPose(T entity, InteractionHand pHand) {
      ItemStack itemstack = entity.getItemInHand(pHand);
      if (itemstack.isEmpty()) {
         return ArmPose.EMPTY;
      }

      if (!entity.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
         return ArmPose.CROSSBOW_HOLD;
      }

      if (entity.getUsedItemHand() == pHand && entity.getUseItemRemainingTicks() > 0) {
         ArmPose armPose = switch (itemstack.getUseAnimation()) {
            case BLOCK -> ArmPose.BLOCK;
            case BOW -> ArmPose.BOW_AND_ARROW;
            case SPEAR -> ArmPose.THROW_SPEAR;
            case CROSSBOW -> ArmPose.CROSSBOW_CHARGE;
            case SPYGLASS -> ArmPose.SPYGLASS;
            case TOOT_HORN -> ArmPose.TOOT_HORN;
            default -> null;
         };
         if (armPose != null) {
            return armPose;
         }
      }

      return ArmPose.ITEM;
   }

   protected void setupRotations(T entity, PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks, float i) {
      float f = entity.getSwimAmount(pPartialTicks);
      if (entity.isFallFlying()) {
         super.setupRotations(entity, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks, i);
         float f1 = entity.getFallFlyingTicks() + pPartialTicks;
         float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
         if (!entity.isAutoSpinAttack()) {
            pMatrixStack.mulPose(Axis.XP.rotationDegrees(f2 * (-90.0F - entity.getXRot())));
         }

         Vec3 vec3 = entity.getViewVector(pPartialTicks);
         Vec3 vec31 = entity.getDeltaMovement();
         double d0 = vec31.horizontalDistanceSqr();
         double d1 = vec3.horizontalDistanceSqr();
         if (d0 > 0.0 && d1 > 0.0) {
            double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
            double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
            pMatrixStack.mulPose(Axis.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
         }
      } else if (f > 0.0F) {
         super.setupRotations(entity, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks, i);
         float f3 = this.shouldSwim(entity) ? -90.0F - entity.getXRot() : -90.0F;
         float f4 = Mth.lerp(f, 0.0F, f3);
         pMatrixStack.mulPose(Axis.XP.rotationDegrees(f4));
         pMatrixStack.translate(0.0, -1.0, 0.3F);
      } else {
         if (entity.getPose() == Pose.STANDING && (entity.isInSittingPose() || entity.isPassenger())) {
            pMatrixStack.translate(0.0, this.getSittingYOffset(entity), 0.0);
         }

         super.setupRotations(entity, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks, i);
      }
   }

   protected double getSittingYOffset(T pEntityLiving) {
      return pEntityLiving.isBaby() ? -0.25 : -0.5;
   }

   protected double getSleepingXOffset(T pEntityLiving) {
      return pEntityLiving.isBaby() ? 0.5 : 1.0;
   }

   protected boolean shouldSwim(T entity) {
      return false;
   }
}
