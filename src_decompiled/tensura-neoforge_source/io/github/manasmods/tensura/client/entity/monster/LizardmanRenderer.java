package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.client.entity.layer.LizardmanLayer;
import io.github.manasmods.tensura.entity.monster.LizardmanEntity;
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

public class LizardmanRenderer extends GeoEntityRenderer<LizardmanEntity> {
   private static final String LEFT_HAND = "leftItem";
   private static final String RIGHT_HAND = "rightItem";
   private static final String LEFT_BOOT = "leftBootArmor";
   private static final String RIGHT_BOOT = "rightBootArmor";
   private static final String LEFT_ARMOR_LEG = "leftLegArmor";
   private static final String RIGHT_ARMOR_LEG = "rightLegArmor";
   private static final String CHESTPLATE = "bodyArmor";
   private static final String RIGHT_SLEEVE = "rightArmArmor";
   private static final String LEFT_SLEEVE = "leftArmArmor";
   private static final String HELMET = "headArmor";
   protected ItemStack mainHandItem;
   protected ItemStack offhandItem;

   public LizardmanRenderer(Context renderManager) {
      super(renderManager, new LizardmanModel());
      this.addRenderLayer(new LizardmanLayer.Hair(this));
      this.addRenderLayer(new LizardmanLayer.Top(this));
      this.addRenderLayer(new LizardmanLayer.Bottom(this));
      this.addRenderLayer(new LizardmanLayer.Bandage(this));
      this.addRenderLayer(new ItemArmorGeoLayer<LizardmanEntity>(this) {
         @Nullable
         protected ItemStack getArmorItemForBone(GeoBone bone, LizardmanEntity animatable) {
            return switch (bone.getName()) {
               case "leftBootArmor", "rightBootArmor" -> this.bootsStack;
               case "leftLegArmor", "rightLegArmor" -> this.leggingsStack;
               case "bodyArmor", "rightArmArmor", "leftArmArmor" -> this.chestplateStack;
               case "headArmor" -> this.helmetStack;
               default -> null;
            };
         }

         @NotNull
         protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, LizardmanEntity animatable) {
            return switch (bone.getName()) {
               case "leftBootArmor", "rightBootArmor" -> EquipmentSlot.FEET;
               case "leftLegArmor", "rightLegArmor" -> EquipmentSlot.LEGS;
               case "rightArmArmor" -> !animatable.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
               case "leftArmArmor" -> animatable.isLeftHanded() ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
               case "bodyArmor" -> EquipmentSlot.CHEST;
               case "headArmor" -> EquipmentSlot.HEAD;
               default -> super.getEquipmentSlotForBone(bone, stack, animatable);
            };
         }

         @NotNull
         protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, LizardmanEntity animatable, HumanoidModel<?> baseModel) {
            return switch (bone.getName()) {
               case "leftBootArmor", "leftLegArmor" -> baseModel.leftLeg;
               case "rightBootArmor", "rightLegArmor" -> baseModel.rightLeg;
               case "rightArmArmor" -> baseModel.rightArm;
               case "leftArmArmor" -> baseModel.leftArm;
               case "bodyArmor" -> baseModel.body;
               case "headArmor" -> baseModel.head;
               default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
            };
         }
      });
      this.addRenderLayer(
         new BlockAndItemGeoLayer<LizardmanEntity>(this) {
            @Nullable
            protected ItemStack getStackForBone(GeoBone bone, LizardmanEntity animatable) {
               return switch (bone.getName()) {
                  case "leftItem" -> animatable.isLeftHanded() ? LizardmanRenderer.this.mainHandItem : LizardmanRenderer.this.offhandItem;
                  case "rightItem" -> animatable.isLeftHanded() ? LizardmanRenderer.this.offhandItem : LizardmanRenderer.this.mainHandItem;
                  default -> null;
               };
            }

            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, LizardmanEntity animatable) {
               return switch (bone.getName()) {
                  case "leftItem", "rightItem" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                  default -> ItemDisplayContext.NONE;
               };
            }

            protected void renderStackForBone(
               PoseStack poseStack,
               GeoBone bone,
               ItemStack stack,
               LizardmanEntity animatable,
               MultiBufferSource bufferSource,
               float partialTick,
               int packedLight,
               int packedOverlay
            ) {
               if (stack == LizardmanRenderer.this.mainHandItem || stack == LizardmanRenderer.this.offhandItem) {
                  poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                  if (stack == LizardmanRenderer.this.mainHandItem && !animatable.isLeftHanded()
                     || stack == LizardmanRenderer.this.offhandItem && animatable.isLeftHanded()) {
                     if (stack.getItem() instanceof ShieldItem || stack.getItem() instanceof SimpleShieldItem) {
                        poseStack.translate(0.0, 0.125, -0.25);
                     }
                  } else if ((
                        stack == LizardmanRenderer.this.mainHandItem && animatable.isLeftHanded()
                           || stack == LizardmanRenderer.this.offhandItem && !animatable.isLeftHanded()
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

   protected float getShadowRadius(LizardmanEntity entity) {
      return entity.isBaby() ? 0.25F : 0.5F;
   }

   public void preRender(
      PoseStack poseStack,
      LizardmanEntity lizardman,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      this.mainHandItem = lizardman.getMainHandItem();
      this.offhandItem = lizardman.getOffhandItem();
      this.entityRenderTranslations.set(poseStack.last().pose());
      float scale = lizardman.isBaby() ? 0.5F : 1.0F;
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, lizardman, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
