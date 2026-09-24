package io.github.manasmods.tensura.mixin.accessor;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net/minecraft/core/component/DataComponentType$Builder$SimpleType")
public interface AccessorDataComponentSimpleType {
   @Accessor("codec")
   @Mutable
   void setCodec(Codec<?> var1);
}
