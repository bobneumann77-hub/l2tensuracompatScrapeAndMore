package io.github.manasmods.tensura.client.entity.human;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.human.GazelDwargoEntity;
import io.github.manasmods.tensura.item.tool.SimpleShieldItem;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class GazelDwargoRenderer extends GeoEntityRenderer<GazelDwargoEntity> {
   private static final String LEFT_HAND = "leftItem";
   private static final String RIGHT_HAND = "rightItem";
   protected ItemStack mainHandItem;
   protected ItemStack offhandItem;

   public GazelDwargoRenderer(Context renderManager) {
      super(renderManager, new GazelDwargoModel());
      this.addRenderLayer(
         new BlockAndItemGeoLayer<GazelDwargoEntity>(this) {
            @Nullable
            protected ItemStack getStackForBone(GeoBone bone, GazelDwargoEntity animatable) {
               return switch (bone.getName()) {
                  case "leftItem" -> animatable.isLeftHanded() ? GazelDwargoRenderer.this.mainHandItem : GazelDwargoRenderer.this.offhandItem;
                  case "rightItem" -> animatable.isLeftHanded() ? GazelDwargoRenderer.this.offhandItem : GazelDwargoRenderer.this.mainHandItem;
                  default -> null;
               };
            }

            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, GazelDwargoEntity animatable) {
               return switch (bone.getName()) {
                  case "leftItem", "rightItem" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                  default -> ItemDisplayContext.NONE;
               };
            }

            protected void renderStackForBone(
               PoseStack poseStack,
               GeoBone bone,
               ItemStack stack,
               GazelDwargoEntity animatable,
               MultiBufferSource bufferSource,
               float partialTick,
               int packedLight,
               int packedOverlay
            ) {
               if (stack == GazelDwargoRenderer.this.mainHandItem || stack == GazelDwargoRenderer.this.offhandItem) {
                  poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                  if (stack == GazelDwargoRenderer.this.mainHandItem && !animatable.isLeftHanded()
                     || stack == GazelDwargoRenderer.this.offhandItem && animatable.isLeftHanded()) {
                     if (stack.getItem() instanceof ShieldItem || stack.getItem() instanceof SimpleShieldItem) {
                        poseStack.translate(0.0, 0.125, -0.25);
                     }
                  } else if ((
                        stack == GazelDwargoRenderer.this.mainHandItem && animatable.isLeftHanded()
                           || stack == GazelDwargoRenderer.this.offhandItem && !animatable.isLeftHanded()
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

   protected float getShadowRadius(GazelDwargoEntity entity) {
      return entity.isBaby() ? 0.25F : 0.5F;
   }

   protected float getDeathMaxRotation(GazelDwargoEntity animatable) {
      return animatable.isAlive() ? super.getDeathMaxRotation(animatable) : 0.0F;
   }

   public void preRender(
      PoseStack poseStack,
      GazelDwargoEntity gazel,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      this.mainHandItem = gazel.getMainHandItem().is((Item)TensuraToolItems.RUHK.get()) ? ItemStack.EMPTY : gazel.getMainHandItem();
      this.offhandItem = gazel.getOffhandItem();
      this.entityRenderTranslations.set(poseStack.last().pose());
      float scale = gazel.isBaby() ? 0.5F : 1.0F;
      this.scaleModelForRender(this.scaleWidth * scale, this.scaleHeight * scale, poseStack, gazel, model, isReRender, partialTick, packedLight, packedOverlay);
   }
}
