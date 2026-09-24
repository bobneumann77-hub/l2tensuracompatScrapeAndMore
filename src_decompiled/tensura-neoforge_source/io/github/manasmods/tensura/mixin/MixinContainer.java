package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Container.class)
public interface MixinContainer {
   @ModifyReturnValue(method = "getMaxStackSize()I", at = @At("RETURN"))
   private int getMaxStackSize(int original) {
      return 100;
   }
}
