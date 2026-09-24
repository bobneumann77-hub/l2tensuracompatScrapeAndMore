package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.effect.ability.OgreGuillotineEffect;
import io.github.manasmods.tensura.network.c2s.RequestIllusionItemPacket;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class MixinItemInHandLayer {
   @WrapOperation(
      method = "render*(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getOffhandItem()Lnet/minecraft/world/item/ItemStack;")
   )
   public ItemStack renderOffHandWithItem(LivingEntity pLivingEntity, Operation<ItemStack> original) {
      ItemStack originalStack = (ItemStack)original.call(new Object[]{pLivingEntity});
      return RequestIllusionItemPacket.getFalsifierItem(pLivingEntity, (ManasSkill)UniqueSkills.FALSIFIER.get(), originalStack, EquipmentSlot.OFFHAND);
   }

   @WrapOperation(
      method = "render*(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMainHandItem()Lnet/minecraft/world/item/ItemStack;")
   )
   public ItemStack renderMainHandWithItem(LivingEntity pLivingEntity, Operation<ItemStack> original) {
      ItemStack originalStack = (ItemStack)original.call(new Object[]{pLivingEntity});
      return RequestIllusionItemPacket.getFalsifierItem(pLivingEntity, (ManasSkill)UniqueSkills.FALSIFIER.get(), originalStack, EquipmentSlot.MAINHAND);
   }

   @Inject(
      method = "renderArmWithItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V", shift = Shift.AFTER)
   )
   public void renderArmWithItem(
      LivingEntity livingEntity,
      ItemStack itemStack,
      ItemDisplayContext itemDisplayContext,
      HumanoidArm humanoidArm,
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int i,
      CallbackInfo ci
   ) {
      AttributeInstance speed = livingEntity.getAttribute(Attributes.ATTACK_SPEED);
      if (speed != null && speed.hasModifier(OgreGuillotineEffect.GUILLOTINE)) {
         poseStack.scale(1.5F, 1.5F, 1.5F);
      }
   }
}
