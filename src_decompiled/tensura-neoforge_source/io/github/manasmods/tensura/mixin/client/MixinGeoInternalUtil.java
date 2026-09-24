package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.network.c2s.RequestIllusionItemPacket;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import java.util.function.BiConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.util.InternalUtil;

@Mixin(InternalUtil.class)
public class MixinGeoInternalUtil {
   @Inject(
      method = "tryRenderGeoArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/client/model/HumanoidModel;FIFFFFFLjava/util/function/BiConsumer;)Z",
      at = @At("HEAD")
   )
   private static <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> void renderMainArmWithItem(
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      T entity,
      ItemStack stack,
      EquipmentSlot equipmentSlot,
      M parentModel,
      A baseModel,
      float partialTick,
      int packedLight,
      float limbSwing,
      float limbSwingAmount,
      float lerpedTickCount,
      float netHeadYaw,
      float headPitch,
      BiConsumer<A, EquipmentSlot> partVisibilitySetter,
      CallbackInfoReturnable<Boolean> cir,
      @Local(argsOnly = true) LocalRef<ItemStack> itemStack
   ) {
      itemStack.set(RequestIllusionItemPacket.getFalsifierItem(entity, (ManasSkill)UniqueSkills.FALSIFIER.get(), stack, equipmentSlot));
   }
}
