package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.client.entity.layer.ArchDaemonLayer;
import io.github.manasmods.tensura.entity.monster.ArchDaemonEntity;
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

public class ArchDaemonRenderer extends GeoEntityRenderer<ArchDaemonEntity> {
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

   public ArchDaemonRenderer(Context renderManager) {
      super(renderManager, new ArchDaemonModel());
      this.addRenderLayer(new ArchDaemonLayer.Hair(this));
      this.addRenderLayer(new ArchDaemonLayer.FacialHair(this));
      this.addRenderLayer(new ArchDaemonLayer.Eyes(this));
      this.addRenderLayer(new ArchDaemonLayer.EyeBrow(this));
      this.addRenderLayer(new ArchDaemonLayer.EyeLiner(this));
      this.addRenderLayer(new ArchDaemonLayer.Teeth(this));
      this.addRenderLayer(new ArchDaemonLayer.Horn(this));
      this.addRenderLayer(new ArchDaemonLayer.Wings(this));
      this.addRenderLayer(new ArchDaemonLayer.Top(this));
      this.addRenderLayer(new ArchDaemonLayer.Bottom(this));
      this.addRenderLayer(new ArchDaemonLayer.Shoe(this));
      this.addRenderLayer(new ArchDaemonLayer.Coat(this));
      this.addRenderLayer(new ArchDaemonLayer.Armband(this));
      this.addRenderLayer(new ArchDaemonLayer.NeckAccessory(this));
      this.addRenderLayer(new ItemArmorGeoLayer<ArchDaemonEntity>(this) {
         @Nullable
         protected ItemStack getArmorItemForBone(GeoBone bone, ArchDaemonEntity animatable) {
            return switch (bone.getName()) {
               case "leftBootArmor", "rightBootArmor" -> this.bootsStack;
               case "leftLegArmor", "rightLegArmor" -> this.leggingsStack;
               case "bodyArmor", "rightArmArmor", "leftArmArmor" -> this.chestplateStack;
               case "headArmor" -> this.helmetStack;
               default -> null;
            };
         }

         @NotNull
         protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, ArchDaemonEntity animatable) {
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
         protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, ArchDaemonEntity animatable, HumanoidModel<?> baseModel) {
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
         new BlockAndItemGeoLayer<ArchDaemonEntity>(this) {
            @Nullable
            protected ItemStack getStackForBone(GeoBone bone, ArchDaemonEntity animatable) {
               return switch (bone.getName()) {
                  case "leftItem" -> animatable.isLeftHanded() ? ArchDaemonRenderer.this.mainHandItem : ArchDaemonRenderer.this.offhandItem;
                  case "rightItem" -> animatable.isLeftHanded() ? ArchDaemonRenderer.this.offhandItem : ArchDaemonRenderer.this.mainHandItem;
                  default -> null;
               };
            }

            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, ArchDaemonEntity animatable) {
               return switch (bone.getName()) {
                  case "leftItem", "rightItem" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                  default -> ItemDisplayContext.NONE;
               };
            }

            protected void renderStackForBone(
               PoseStack poseStack,
               GeoBone bone,
               ItemStack stack,
               ArchDaemonEntity animatable,
               MultiBufferSource bufferSource,
               float partialTick,
               int packedLight,
               int packedOverlay
            ) {
               if (stack == ArchDaemonRenderer.this.mainHandItem || stack == ArchDaemonRenderer.this.offhandItem) {
                  poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                  if (stack == ArchDaemonRenderer.this.mainHandItem && !animatable.isLeftHanded()
                     || stack == ArchDaemonRenderer.this.offhandItem && animatable.isLeftHanded()) {
                     if (stack.getItem() instanceof ShieldItem || stack.getItem() instanceof SimpleShieldItem) {
                        poseStack.translate(0.0, 0.125, -0.25);
                     }
                  } else if ((
                        stack == ArchDaemonRenderer.this.mainHandItem && animatable.isLeftHanded()
                           || stack == ArchDaemonRenderer.this.offhandItem && !animatable.isLeftHanded()
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

   protected float getShadowRadius(ArchDaemonEntity entity) {
      return entity.isBaby() ? 0.25F : 0.5F;
   }

   protected float getDeathMaxRotation(ArchDaemonEntity animatable) {
      return 0.0F;
   }

   public void preRender(
      PoseStack poseStack,
      ArchDaemonEntity daemon,
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
      this.entityRenderTranslations.set(poseStack.last().pose());
      float scale = daemon.isBaby() ? 0.5F : 1.0F;
      this.scaleModelForRender(this.scaleWidth * scale, this.scaleHeight * scale, poseStack, daemon, model, isReRender, partialTick, packedLight, packedOverlay);
   }
}
