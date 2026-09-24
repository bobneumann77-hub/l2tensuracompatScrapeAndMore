package io.github.manasmods.tensura.mixin.accessor;

import net.minecraft.world.level.GameRules.Category;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Category.class)
public interface AccessorGameRuleCategory {
   @Accessor("$VALUES")
   static Category[] getValues() {
      throw new IllegalStateException("Mixin GameRules.Category failed.");
   }

   @Mutable
   @Accessor("$VALUES")
   static void setValues(Category[] variants) {
      throw new IllegalStateException("Mixin GameRules.Category failed.");
   }

   @Invoker("<init>")
   static Category create(String enumName, int ordinal, String descriptionId) {
      throw new IllegalStateException("Mixin GameRules.Category failed.");
   }
}
