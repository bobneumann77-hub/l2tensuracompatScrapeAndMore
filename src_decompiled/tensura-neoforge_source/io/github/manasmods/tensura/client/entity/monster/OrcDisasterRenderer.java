package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.monster.OrcEntity;
import io.github.manasmods.tensura.item.tool.SimpleShieldItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class OrcDisasterRenderer extends GeoEntityRenderer<OrcEntity> {
   private static final String LEFT_HAND = "LeftItem";
   private static final String RIGHT_HAND = "RightItem";
   protected ItemStack mainHandItem;
   protected ItemStack offhandItem;

   public OrcDisasterRenderer(Context renderManager) {
      super(renderManager, new OrcDisasterModel());
      this.addRenderLayer(
         new BlockAndItemGeoLayer<OrcEntity>(this) {
            @Nullable
            protected ItemStack getStackForBone(GeoBone bone, OrcEntity animatable) {
               return switch (bone.getName()) {
                  case "LeftItem" -> animatable.isLeftHanded() ? OrcDisasterRenderer.this.mainHandItem : OrcDisasterRenderer.this.offhandItem;
                  case "RightItem" -> animatable.isLeftHanded() ? OrcDisasterRenderer.this.offhandItem : OrcDisasterRenderer.this.mainHandItem;
                  default -> null;
               };
            }

            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, OrcEntity animatable) {
               return switch (bone.getName()) {
                  case "LeftItem", "RightItem" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                  default -> ItemDisplayContext.NONE;
               };
            }

            protected void renderStackForBone(
               PoseStack poseStack,
               GeoBone bone,
               ItemStack stack,
               OrcEntity animatable,
               MultiBufferSource bufferSource,
               float partialTick,
               int packedLight,
               int packedOverlay
            ) {
               if (stack == OrcDisasterRenderer.this.mainHandItem || stack == OrcDisasterRenderer.this.offhandItem) {
                  poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                  if (stack == OrcDisasterRenderer.this.mainHandItem && !animatable.isLeftHanded()
                     || stack == OrcDisasterRenderer.this.offhandItem && animatable.isLeftHanded()) {
                     if (stack.getItem() instanceof ShieldItem || stack.getItem() instanceof SimpleShieldItem) {
                        poseStack.translate(0.0, 0.125, -0.25);
                     }
                  } else if ((
                        stack == OrcDisasterRenderer.this.mainHandItem && animatable.isLeftHanded()
                           || stack == OrcDisasterRenderer.this.offhandItem && !animatable.isLeftHanded()
                     )
                     && (stack.getItem() instanceof ShieldItem || stack.getItem() instanceof SimpleShieldItem)) {
                     poseStack.translate(0.0, 0.125, 0.25);
                     poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                  }
               }

               super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
         }
      );
   }

   protected float getShadowRadius(OrcEntity entity) {
      return entity.isBaby() ? 0.25F : 0.5F;
   }

   protected float getDeathMaxRotation(OrcEntity animatable) {
      return animatable.isAlive() ? super.getDeathMaxRotation(animatable) : 0.0F;
   }

   public void preRender(
      PoseStack poseStack,
      OrcEntity orc,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      this.mainHandItem = orc.getMainHandItem();
      this.offhandItem = orc.getOffhandItem();
      this.entityRenderTranslations.set(poseStack.last().pose());
      float scale = orc.isBaby() ? 0.5F : 1.0F;
      this.scaleModelForRender(this.scaleWidth * scale, this.scaleHeight * scale, poseStack, orc, model, isReRender, partialTick, packedLight, packedOverlay);
   }
}
