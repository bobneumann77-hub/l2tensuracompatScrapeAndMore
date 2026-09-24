package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.data.TensuraItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PowderSnowBlock.class)
public class MixinPowderSnowBlock {
   @ModifyReturnValue(method = "canEntityWalkOnPowderSnow(Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "RETURN", ordinal = 1))
   private static boolean canEntityWalkOnPowderSnow(boolean original, Entity entity) {
      return entity instanceof LivingEntity living && living.getItemBySlot(EquipmentSlot.FEET).is(TensuraItemTags.CAN_WALK_ON_POWDER_SNOW) ? true : original;
   }
}
