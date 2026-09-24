package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.monster.GreaterDaemonEntity;
import io.github.manasmods.tensura.item.tool.SimpleShieldItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class GreaterDaemonRenderer extends GeoEntityRenderer<GreaterDaemonEntity> {
   private static final String LEFT_HAND = "left_item";
   private static final String RIGHT_HAND = "right_item";
   protected ItemStack mainHandItem;
   protected ItemStack offhandItem;

   public GreaterDaemonRenderer(Context renderManager) {
      super(renderManager, new GreaterDaemonModel());
      this.addRenderLayer(
         new BlockAndItemGeoLayer<GreaterDaemonEntity>(this) {
            @Nullable
            protected ItemStack getStackForBone(GeoBone bone, GreaterDaemonEntity animatable) {
               return switch (bone.getName()) {
                  case "left_item" -> animatable.isLeftHanded() ? GreaterDaemonRenderer.this.mainHandItem : GreaterDaemonRenderer.this.offhandItem;
                  case "right_item" -> animatable.isLeftHanded() ? GreaterDaemonRenderer.this.offhandItem : GreaterDaemonRenderer.this.mainHandItem;
                  default -> null;
               };
            }

            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, GreaterDaemonEntity animatable) {
               return switch (bone.getName()) {
                  case "left_item", "right_item" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                  default -> ItemDisplayContext.NONE;
               };
            }

            protected void renderStackForBone(
               PoseStack poseStack,
               GeoBone bone,
               ItemStack stack,
               GreaterDaemonEntity animatable,
               MultiBufferSource bufferSource,
               float partialTick,
               int packedLight,
               int packedOverlay
            ) {
               if (stack == GreaterDaemonRenderer.this.mainHandItem || stack == GreaterDaemonRenderer.this.offhandItem) {
                  poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                  if (stack == GreaterDaemonRenderer.this.mainHandItem && !animatable.isLeftHanded()
                     || stack == GreaterDaemonRenderer.this.offhandItem && animatable.isLeftHanded()) {
                     if (stack.getItem() instanceof ShieldItem || stack.getItem() instanceof SimpleShieldItem) {
                        poseStack.translate(0.0, 0.125, -0.25);
                     }
                  } else if ((
                        stack == GreaterDaemonRenderer.this.mainHandItem && animatable.isLeftHanded()
                           || stack == GreaterDaemonRenderer.this.offhandItem && !animatable.isLeftHanded()
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

   protected float getShadowRadius(GreaterDaemonEntity entity) {
      return 0.5F;
   }

   protected float getDeathMaxRotation(GreaterDaemonEntity animatable) {
      return 0.0F;
   }

   protected int getBlockLightLevel(GreaterDaemonEntity entity, BlockPos blockPos) {
      return 12;
   }

   public void preRender(
      PoseStack poseStack,
      GreaterDaemonEntity daemon,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      this.mainHandItem = daemon.getMainHandItem();
      this.offhandItem = daemon.getOffhandItem();
      super.preRender(poseStack, daemon, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
   }
}
