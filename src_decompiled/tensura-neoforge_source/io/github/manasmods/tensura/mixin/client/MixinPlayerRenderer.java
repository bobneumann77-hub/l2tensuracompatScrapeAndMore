package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.network.c2s.RequestIllusionItemPacket;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {
   @WrapOperation(
      method = "getArmPose(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/AbstractClientPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"
      )
   )
   private static ItemStack getArmPose(AbstractClientPlayer player, InteractionHand hand, Operation<ItemStack> original) {
      ItemStack originalStack = (ItemStack)original.call(new Object[]{player, hand});
      return RequestIllusionItemPacket.getFalsifierItem(player, (ManasSkill)UniqueSkills.FALSIFIER.get(), originalStack, LivingEntity.getSlotForHand(hand));
   }

   @Inject(
      method = "getArmPose(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
      at = @At("RETURN"),
      cancellable = true
   )
   private static void getArmPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<ArmPose> cir) {
      if (cir.getReturnValue() == ArmPose.ITEM) {
         ItemStack itemStack = RequestIllusionItemPacket.getFalsifierItem(
            player, (ManasSkill)UniqueSkills.FALSIFIER.get(), player.getItemInHand(hand), LivingEntity.getSlotForHand(hand)
         );
         if (itemStack.is(TensuraItemTags.EMPTY_HAND_POSE)) {
            cir.setReturnValue(ArmPose.EMPTY);
         }
      }
   }
}
