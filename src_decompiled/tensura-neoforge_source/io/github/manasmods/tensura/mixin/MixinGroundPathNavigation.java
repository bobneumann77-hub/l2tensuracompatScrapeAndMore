package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GroundPathNavigation.class)
public abstract class MixinGroundPathNavigation {
   @WrapOperation(method = "trimPath()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;canSeeSky(Lnet/minecraft/core/BlockPos;)Z"))
   private boolean canSeeSky(Level instance, BlockPos pos, Operation<Boolean> original) {
      return !original.call(new Object[]{instance, pos}) ? false : !instance.getBiome(pos).is(TensuraBiomeTags.IS_MIASMIC);
   }
}
