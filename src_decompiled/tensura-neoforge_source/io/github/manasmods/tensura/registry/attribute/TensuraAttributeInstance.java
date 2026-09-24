package io.github.manasmods.tensura.registry.attribute;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface TensuraAttributeInstance {
   @Nullable
   default LivingEntity tensura$getOwner() {
      throw new AssertionError();
   }

   default void tensura$setOwner(@Nullable LivingEntity owner) {
      throw new AssertionError();
   }
}
