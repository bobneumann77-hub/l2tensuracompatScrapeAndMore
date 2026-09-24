package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.entity.layer.SlimeSantaHatLayer;
import io.github.manasmods.tensura.entity.monster.SlimeEntity;
import io.github.manasmods.tensura.util.client.ClientHelper;
import java.util.Objects;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;
import software.bernie.geckolib.util.Color;

public class SlimeRenderer extends GeoEntityRenderer<SlimeEntity> {
   private static final String HELMET = "HeadArmor";

   public SlimeRenderer(Context renderManager) {
      super(renderManager, new SlimeModel());
      this.addRenderLayer(new SlimeSantaHatLayer(this));
      this.addRenderLayer(new ItemArmorGeoLayer<SlimeEntity>(this) {
         @Nullable
         protected ItemStack getArmorItemForBone(GeoBone bone, SlimeEntity animatable) {
            return Objects.equals(bone.getName(), "HeadArmor") ? this.helmetStack : null;
         }

         @NotNull
         protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, SlimeEntity animatable) {
            return Objects.equals(bone.getName(), "HeadArmor") ? EquipmentSlot.HEAD : super.getEquipmentSlotForBone(bone, stack, animatable);
         }

         @NotNull
         protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, SlimeEntity animatable, HumanoidModel<?> baseModel) {
            return Objects.equals(bone.getName(), "HeadArmor") ? baseModel.head : super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
         }
      });
   }

   protected float getShadowRadius(SlimeEntity entity) {
      return 0.1F * entity.getSize();
   }

   public RenderType getRenderType(SlimeEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      RenderType type = super.getRenderType(animatable, texture, bufferSource, partialTick);
      return !ClientHelper.XMAS && animatable.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
         ? ClientHelper.getRiderFirstViewRenderType(type, animatable, 2.0, texture)
         : type;
   }

   public Color getRenderColor(SlimeEntity animatable, float partialTick, int packedLight) {
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      if (!ClientHelper.XMAS && animatable.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
         if (ClientHelper.isRiderFirstViewDistance(animatable, 2.0)) {
            color = Color.ofARGB(Mth.ceil(color.getAlpha() * 0.5F), color.getRed(), color.getGreen(), color.getBlue());
         }

         return color;
      } else {
         return color;
      }
   }

   public void preRender(
      PoseStack poseStack,
      SlimeEntity animatable,
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
      float scale = 0.175F * animatable.getSize();
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
