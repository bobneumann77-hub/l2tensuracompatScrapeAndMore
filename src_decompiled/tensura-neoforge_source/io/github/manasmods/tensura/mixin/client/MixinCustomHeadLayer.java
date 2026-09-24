package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.network.c2s.RequestIllusionItemPacket;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CustomHeadLayer.class)
public class MixinCustomHeadLayer {
   @WrapOperation(
      method = "render*(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
      )
   )
   private ItemStack renderHelmet(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
      ItemStack originalStack = (ItemStack)original.call(new Object[]{entity, slot});
      return RequestIllusionItemPacket.getFalsifierItem(entity, (ManasSkill)UniqueSkills.FALSIFIER.get(), originalStack, slot);
   }
}
