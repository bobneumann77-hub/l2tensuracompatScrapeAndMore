package io.github.manasmods.tensura.mixin.accessor;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RangedAttribute.class)
public interface AccessorRangedAttribute {
   @Accessor("maxValue")
   @Mutable
   void setMaxValue(double var1);

   @Accessor("minValue")
   @Mutable
   void setMinValue(double var1);
}
