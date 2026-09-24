package io.github.manasmods.tensura.mixin.accessor;

import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SoundSource.class)
public interface AccessorSoundSource {
   @Accessor("$VALUES")
   static SoundSource[] getValues() {
      throw new IllegalStateException("Mixin SoundSource failed.");
   }

   @Mutable
   @Accessor("$VALUES")
   static void setValues(SoundSource[] variants) {
      throw new IllegalStateException("Mixin SoundSource failed.");
   }

   @Invoker("<init>")
   static SoundSource create(String enumName, int ordinal, String name) {
      throw new IllegalStateException("Mixin SoundSource failed.");
   }
}
