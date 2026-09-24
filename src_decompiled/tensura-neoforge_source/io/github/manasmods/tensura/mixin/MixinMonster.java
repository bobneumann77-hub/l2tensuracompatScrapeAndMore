package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Monster.class)
public abstract class MixinMonster {
   @ModifyReturnValue(
      method = "isDarkEnoughToSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)Z",
      at = @At("RETURN")
   )
   private static boolean allowDaylightInCustomBiome(boolean original, ServerLevelAccessor level, BlockPos pos, RandomSource random) {
      if (original) {
         return true;
      } else {
         return level.getBiome(pos).is(TensuraBiomeTags.IS_MIASMIC) ? true : original;
      }
   }
}
