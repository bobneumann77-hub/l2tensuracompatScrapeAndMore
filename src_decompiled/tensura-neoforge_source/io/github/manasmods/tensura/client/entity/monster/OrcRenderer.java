package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.client.entity.layer.OrcLayer;
import io.github.manasmods.tensura.entity.monster.OrcEntity;
import io.github.manasmods.tensura.item.tool.SimpleShieldItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

public class OrcRenderer extends GeoEntityRenderer<OrcEntity> {
   private static final String LEFT_HAND = "LeftHand";
   private static final String RIGHT_HAND = "RightHand";
   private static final String LEFT_BOOT = "LeftBootArmor";
   private static final String RIGHT_BOOT = "RightBootArmor";
   private static final String LEFT_ARMOR_LEG = "LeftLegArmor";
   private static final String RIGHT_ARMOR_LEG = "RightLegArmor";
   private static final String CHESTPLATE = "ChestArmor";
   private static final String RIGHT_SLEEVE = "RightArmArmor";
   private static final String LEFT_SLEEVE = "LeftArmArmor";
   private static final String HELMET = "HeadArmor";
   protected ItemStack mainHandItem;
   protected ItemStack offhandItem;

   public OrcRenderer(Context renderManager) {
      super(renderManager, new OrcModel());
      this.addRenderLayer(new OrcLayer.Neck(this));
      this.addRenderLayer(new OrcLayer.Top(this));
      this.addRenderLayer(new OrcLayer.Necklace(this));
      this.addRenderLayer(new OrcLayer.Bottom(this));
      this.addRenderLayer(new OrcLayer.Belt(this));
      this.addRenderLayer(new OrcLayer.Boots(this));
      this.addRenderLayer(new OrcLayer.Bandage(this));
      this.addRenderLayer(new ItemArmorGeoLayer<OrcEntity>(this) {
         @Nullable
         protected ItemStack getArmorItemForBone(GeoBone bone, OrcEntity animatable) {
            return switch (bone.getName()) {
               case "LeftBootArmor", "RightBootArmor" -> this.bootsStack;
               case "LeftLegArmor", "RightLegArmor" -> this.leggingsStack;
               case "ChestArmor", "RightArmArmor", "LeftArmArmor" -> this.chestplateStack;
               case "HeadArmor" -> this.helmetStack;
               default -> null;
            };
         }

         @NotNull
         protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, OrcEntity animatable) {
            return switch (bone.getName()) {
               case "LeftBootArmor", "RightBootArmor" -> EquipmentSlot.FEET;
               case "LeftLegArmor", "RightLegArmor" -> EquipmentSlot.LEGS;
               case "RightArmArmor" -> !animatable.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
               case "LeftArmArmor" -> animatable.isLeftHanded() ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
               case "ChestArmor" -> EquipmentSlot.CHEST;
               case "HeadArmor" -> EquipmentSlot.HEAD;
               default -> super.getEquipmentSlotForBone(bone, stack, animatable);
            };
         }

         @NotNull
         protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, OrcEntity animatable, HumanoidModel<?> baseModel) {
            return switch (bone.getName()) {
               case "LeftBootArmor", "LeftLegArmor" -> baseModel.leftLeg;
               case "RightBootArmor", "RightLegArmor" -> baseModel.rightLeg;
               case "RightArmArmor" -> baseModel.rightArm;
               case "LeftArmArmor" -> baseModel.leftArm;
               case "ChestArmor" -> baseModel.body;
               case "HeadArmor" -> baseModel.head;
               default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
            };
         }
      });
      this.addRenderLayer(
         new BlockAndItemGeoLayer<OrcEntity>(this) {
            @Nullable
            protected ItemStack getStackForBone(GeoBone bone, OrcEntity animatable) {
               return switch (bone.getName()) {
                  case "LeftHand" -> animatable.isLeftHanded() ? OrcRenderer.this.mainHandItem : OrcRenderer.this.offhandItem;
                  case "RightHand" -> animatable.isLeftHanded() ? OrcRenderer.this.offhandItem : OrcRenderer.this.mainHandItem;
                  default -> null;
               };
            }

            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, OrcEntity animatable) {
               return switch (bone.getName()) {
                  case "LeftHand", "RightHand" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
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
               if (stack == OrcRenderer.this.mainHandItem || stack == OrcRenderer.this.offhandItem) {
                  poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                  if (stack == OrcRenderer.this.mainHandItem && !animatable.isLeftHanded()
                     || stack == OrcRenderer.this.offhandItem && animatable.isLeftHanded()) {
                     if (stack.getItem() instanceof ShieldItem || stack.getItem() instanceof SimpleShieldItem) {
                        poseStack.translate(0.0, 0.125, -0.25);
                     }
                  } else if ((
                        stack == OrcRenderer.this.mainHandItem && animatable.isLeftHanded()
                           || stack == OrcRenderer.this.offhandItem && !animatable.isLeftHanded()
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
      int tick = 40 - orc.getEvolving();
      if (orc.getEvolving() > 0 && tick > 0) {
         scale *= 1.0F + 0.5F * (tick / 40.0F);
      }

      this.scaleModelForRender(this.scaleWidth * scale, this.scaleHeight * scale, poseStack, orc, model, isReRender, partialTick, packedLight, packedOverlay);
   }
}
