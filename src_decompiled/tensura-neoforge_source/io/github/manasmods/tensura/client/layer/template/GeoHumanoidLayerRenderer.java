package io.github.manasmods.tensura.client.layer.template;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtil;

public class GeoHumanoidLayerRenderer<T extends GeoAnimatable> extends HumanoidModel implements GeoRenderer<T> {
   protected final GeoModel<T> model;
   protected T animatable;
   protected LivingEntity currentEntity = null;
   protected HumanoidModel<?> baseModel;
   protected float scaleWidth = 1.0F;
   protected float scaleHeight = 1.0F;
   protected Matrix4f entityRenderTranslations = new Matrix4f();
   protected Matrix4f modelRenderTranslations = new Matrix4f();
   protected BakedGeoModel lastModel = null;
   protected GeoBone head = null;
   protected GeoBone body = null;
   protected GeoBone rightArm = null;
   protected GeoBone leftArm = null;
   protected GeoBone rightLeg = null;
   protected GeoBone leftLeg = null;
   protected GeoBone rightBoot = null;
   protected GeoBone leftBoot = null;
   protected MultiBufferSource bufferSource = null;
   protected float partialTick;
   protected float limbSwing;
   protected float limbSwingAmount;
   protected float netHeadYaw;
   protected float headPitch;

   public GeoHumanoidLayerRenderer(GeoModel<T> model) {
      super(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
      this.model = model;
      this.young = false;
   }

   public GeoModel<T> getGeoModel() {
      return this.model;
   }

   public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(texture);
   }

   public GeoHumanoidLayerRenderer<T> withScale(float scale) {
      return this.withScale(scale, scale);
   }

   public GeoHumanoidLayerRenderer<T> withScale(float scaleWidth, float scaleHeight) {
      this.scaleWidth = scaleWidth;
      this.scaleHeight = scaleHeight;
      return this;
   }

   @Nullable
   public GeoBone getHeadBone(GeoModel<T> model) {
      return (GeoBone)model.getBone("armorHead").orElse(null);
   }

   @Nullable
   public GeoBone getBodyBone(GeoModel<T> model) {
      return (GeoBone)model.getBone("armorBody").orElse(null);
   }

   @Nullable
   public GeoBone getRightArmBone(GeoModel<T> model) {
      return (GeoBone)model.getBone("armorRightArm").orElse(null);
   }

   @Nullable
   public GeoBone getLeftArmBone(GeoModel<T> model) {
      return (GeoBone)model.getBone("armorLeftArm").orElse(null);
   }

   @Nullable
   public GeoBone getRightLegBone(GeoModel<T> model) {
      return (GeoBone)model.getBone("armorRightLeg").orElse(null);
   }

   @Nullable
   public GeoBone getLeftLegBone(GeoModel<T> model) {
      return (GeoBone)model.getBone("armorLeftLeg").orElse(null);
   }

   @Nullable
   public GeoBone getRightBootBone(GeoModel<T> model) {
      return (GeoBone)model.getBone("armorRightBoot").orElse(null);
   }

   @Nullable
   public GeoBone getLeftBootBone(GeoModel<T> model) {
      return (GeoBone)model.getBone("armorLeftBoot").orElse(null);
   }

   public boolean isHeadModel() {
      return false;
   }

   public boolean shouldRender(LivingEntity entity) {
      return true;
   }

   public void render(
      PoseStack pMatrixStack,
      LivingEntity entity,
      GeoAnimatable animatable,
      HumanoidModel<?> baseModel,
      MultiBufferSource pBuffer,
      int pPackedLight,
      float pLimbSwing,
      float pLimbSwingAmount,
      float pPartialTicks,
      float pNetHeadYaw,
      float pHeadPitch
   ) {
      if (this.shouldRender(entity)) {
         this.prepForRender(entity, animatable, baseModel, pBuffer, pPartialTicks, pLimbSwing, pLimbSwingAmount, pNetHeadYaw, pHeadPitch);
         this.renderToBuffer(pMatrixStack, pBuffer, null, pPackedLight);
      }
   }

   public void preRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      this.entityRenderTranslations.set(poseStack.last().pose());
      this.applyBaseModel(this.baseModel);
      this.grabRelevantBones(model);
      this.applyBaseTransformations(this.baseModel);
      this.scaleModelForBaby(poseStack, animatable, partialTick, isReRender);
      this.scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
   }

   public void renderToBuffer(PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight) {
      Minecraft mc = Minecraft.getInstance();
      float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(true);
      RenderType renderType = this.getRenderType(this.animatable, this.getTextureLocation(this.animatable), bufferSource, partialTick);
      this.defaultRender(poseStack, this.animatable, bufferSource, renderType, buffer, 0.0F, partialTick, packedLight);
      this.animatable = null;
   }

   public void actuallyRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable RenderType renderType,
      MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int color
   ) {
      poseStack.pushPose();
      poseStack.translate(0.0F, 1.5F, 0.0F);
      poseStack.scale(-1.0F, -1.0F, 1.0F);
      if (!isReRender) {
         AnimationState<T> animationState = new AnimationState(animatable, 0.0F, 0.0F, partialTick, false);
         long instanceId = this.getInstanceId(animatable);
         GeoModel<T> currentModel = this.getGeoModel();
         animationState.setData(DataTickets.TICK, animatable.getTick(this.currentEntity));
         animationState.setData(DataTickets.ENTITY, this.currentEntity);
         currentModel.addAdditionalStateData(animatable, instanceId, animationState::setData);
         currentModel.handleAnimations(animatable, instanceId, animationState, partialTick);
      }

      this.modelRenderTranslations.set(poseStack.last().pose());
      if (buffer != null) {
         if (this.getCurrentEntity() != null && this.getCurrentEntity().isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && !this.getCurrentEntity().isInvisibleTo(player)) {
               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
               super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
            }
         } else {
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, color);
         }
      }

      poseStack.popPose();
   }

   public void doPostRenderCleanup() {
      this.baseModel = null;
      this.currentEntity = null;
      this.animatable = null;
      this.bufferSource = null;
      this.partialTick = 0.0F;
      this.limbSwing = 0.0F;
      this.limbSwingAmount = 0.0F;
      this.netHeadYaw = 0.0F;
      this.headPitch = 0.0F;
   }

   public void renderRecursively(
      PoseStack poseStack,
      T animatable,
      GeoBone bone,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      if (bone.isTrackingMatrices()) {
         Matrix4f poseState = new Matrix4f(poseStack.last().pose());
         bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
         bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations));
      }

      super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
   }

   protected void grabRelevantBones(BakedGeoModel bakedModel) {
      if (this.lastModel != bakedModel) {
         GeoModel<T> model = this.getGeoModel();
         this.lastModel = bakedModel;
         this.head = this.getHeadBone(model);
         this.body = this.getBodyBone(model);
         this.rightArm = this.getRightArmBone(model);
         this.leftArm = this.getLeftArmBone(model);
         this.rightLeg = this.getRightLegBone(model);
         this.leftLeg = this.getLeftLegBone(model);
         this.rightBoot = this.getRightBootBone(model);
         this.leftBoot = this.getLeftBootBone(model);
      }
   }

   public void prepForRender(
      LivingEntity entity,
      GeoAnimatable stack,
      HumanoidModel<?> baseModel,
      MultiBufferSource bufferSource,
      float partialTick,
      float limbSwing,
      float limbSwingAmount,
      float netHeadYaw,
      float headPitch
   ) {
      this.baseModel = baseModel;
      this.currentEntity = entity;
      this.animatable = (T)stack;
      this.bufferSource = bufferSource;
      this.partialTick = partialTick;
      this.limbSwing = limbSwing;
      this.limbSwingAmount = limbSwingAmount;
      this.netHeadYaw = netHeadYaw;
      this.headPitch = headPitch;
   }

   protected void applyBaseModel(HumanoidModel<?> baseModel) {
      HumanoidModel<?> self = this;
      self.young = baseModel.young;
      self.crouching = baseModel.crouching;
      self.riding = baseModel.riding;
      self.rightArmPose = baseModel.rightArmPose;
      self.leftArmPose = baseModel.leftArmPose;
      self.head.visible = baseModel.head.visible;
      self.hat.visible = baseModel.hat.visible;
      self.body.visible = baseModel.body.visible;
      self.rightArm.visible = baseModel.rightArm.visible;
      self.leftArm.visible = baseModel.leftArm.visible;
      self.rightLeg.visible = baseModel.rightLeg.visible;
      self.leftLeg.visible = baseModel.leftLeg.visible;
   }

   public void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
      this.setAllBonesVisible(false);
      HumanoidModel<?> model = this;
      switch (currentSlot) {
         case HEAD:
            this.setBoneVisible(this.head, model.head.visible);
            break;
         case CHEST:
            this.setBoneVisible(this.body, model.body.visible);
            this.setBoneVisible(this.rightArm, model.rightArm.visible);
            this.setBoneVisible(this.leftArm, model.leftArm.visible);
            break;
         case LEGS:
            this.setBoneVisible(this.rightLeg, model.rightLeg.visible);
            this.setBoneVisible(this.leftLeg, model.leftLeg.visible);
            break;
         case FEET:
            this.setBoneVisible(this.rightBoot, model.rightLeg.visible);
            this.setBoneVisible(this.leftBoot, model.leftLeg.visible);
      }
   }

   public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, HumanoidModel<?> model) {
      this.setAllVisible(false);
      currentPart.visible = true;
      GeoBone bone = null;
      if (currentPart == model.hat || currentPart == model.head) {
         bone = this.head;
      } else if (currentPart == model.body) {
         bone = this.body;
      } else if (currentPart == model.leftArm) {
         bone = this.leftArm;
      } else if (currentPart == model.rightArm) {
         bone = this.rightArm;
      } else if (currentPart == model.leftLeg) {
         bone = currentSlot == EquipmentSlot.FEET ? this.leftBoot : this.leftLeg;
      } else if (currentPart == model.rightLeg) {
         bone = currentSlot == EquipmentSlot.FEET ? this.rightBoot : this.rightLeg;
      }

      if (bone != null) {
         bone.setHidden(false);
      }
   }

   protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
      if (this.head != null) {
         ModelPart headPart = baseModel.head;
         RenderUtil.matchModelPartRot(headPart, this.head);
         this.head.updatePosition(headPart.x, -headPart.y, headPart.z);
      }

      if (this.body != null) {
         ModelPart bodyPart = baseModel.body;
         RenderUtil.matchModelPartRot(bodyPart, this.body);
         this.body.updatePosition(bodyPart.x, -bodyPart.y, bodyPart.z);
      }

      if (this.rightArm != null) {
         ModelPart rightArmPart = baseModel.rightArm;
         RenderUtil.matchModelPartRot(rightArmPart, this.rightArm);
         this.rightArm.updatePosition(rightArmPart.x + 5.0F, 2.0F - rightArmPart.y, rightArmPart.z);
      }

      if (this.leftArm != null) {
         ModelPart leftArmPart = baseModel.leftArm;
         RenderUtil.matchModelPartRot(leftArmPart, this.leftArm);
         this.leftArm.updatePosition(leftArmPart.x - 5.0F, 2.0F - leftArmPart.y, leftArmPart.z);
      }

      if (this.rightLeg != null) {
         ModelPart rightLegPart = baseModel.rightLeg;
         RenderUtil.matchModelPartRot(rightLegPart, this.rightLeg);
         this.rightLeg.updatePosition(rightLegPart.x + 2.0F, 12.0F - rightLegPart.y, rightLegPart.z);
         if (this.rightBoot != null) {
            RenderUtil.matchModelPartRot(rightLegPart, this.rightBoot);
            this.rightBoot.updatePosition(rightLegPart.x + 2.0F, 12.0F - rightLegPart.y, rightLegPart.z);
         }
      }

      if (this.leftLeg != null) {
         ModelPart leftLegPart = baseModel.leftLeg;
         RenderUtil.matchModelPartRot(leftLegPart, this.leftLeg);
         this.leftLeg.updatePosition(leftLegPart.x - 2.0F, 12.0F - leftLegPart.y, leftLegPart.z);
         if (this.leftBoot != null) {
            RenderUtil.matchModelPartRot(leftLegPart, this.leftBoot);
            this.leftBoot.updatePosition(leftLegPart.x - 2.0F, 12.0F - leftLegPart.y, leftLegPart.z);
         }
      }
   }

   public void setAllVisible(boolean visible) {
      super.setAllVisible(visible);
      this.setAllBonesVisible(visible);
   }

   protected void setAllBonesVisible(boolean visible) {
      this.setBoneVisible(this.head, visible);
      this.setBoneVisible(this.body, visible);
      this.setBoneVisible(this.rightArm, visible);
      this.setBoneVisible(this.leftArm, visible);
      this.setBoneVisible(this.rightLeg, visible);
      this.setBoneVisible(this.leftLeg, visible);
      this.setBoneVisible(this.rightBoot, visible);
      this.setBoneVisible(this.leftBoot, visible);
   }

   public void scaleModelForBaby(PoseStack poseStack, T animatable, float partialTick, boolean isReRender) {
      if (this.young && !isReRender) {
         if (this.isHeadModel()) {
            if (this.baseModel.scaleHead) {
               float headScale = 1.5F / this.baseModel.babyHeadScale;
               poseStack.scale(headScale, headScale, headScale);
            }

            poseStack.translate(0.0F, this.baseModel.babyYHeadOffset / 16.0F, this.baseModel.babyZHeadOffset / 16.0F);
         } else {
            float bodyScale = 1.0F / this.baseModel.babyBodyScale;
            poseStack.scale(bodyScale, bodyScale, bodyScale);
            poseStack.translate(0.0F, this.baseModel.bodyYOffset / 16.0F, 0.0F);
         }
      }
   }

   protected void setBoneVisible(@Nullable GeoBone bone, boolean visible) {
      if (bone != null) {
         bone.setHidden(!visible);
      }
   }

   public void updateAnimatedTextureFrame(T animatable) {
      if (this.currentEntity != null) {
         AnimatableTexture.setAndUpdate(this.getTextureLocation(animatable));
      }
   }

   public void fireCompileRenderLayersEvent() {
   }

   public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
      return true;
   }

   public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
   }

   @Generated
   public T getAnimatable() {
      return this.animatable;
   }

   @Generated
   public LivingEntity getCurrentEntity() {
      return this.currentEntity;
   }
}
