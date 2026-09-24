package io.github.manasmods.tensura.effect.template;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface TensuraMobEffectInstance {
   @Nullable
   default UUID tensura$getSource() {
      throw new AssertionError();
   }

   default void tensura$setSource(@Nullable UUID source) {
      throw new AssertionError();
   }

   @Nullable
   default AbilitySlot tensura$getSourceAbility() {
      throw new AssertionError();
   }

   default void tensura$setSourceAbility(@Nullable AbilitySlot source) {
      throw new AssertionError();
   }

   default void tensura$setSourceAbility(@Nullable ManasSkill skill, int mode) {
      this.tensura$setSourceAbility(skill != null ? new AbilitySlot(skill, mode) : null);
   }

   @Nullable
   default CompoundTag tensura$getTag() {
      throw new AssertionError();
   }

   default CompoundTag tensura$getOrCreateTag() {
      throw new AssertionError();
   }

   default void tensura$setTag(@Nullable CompoundTag tag) {
      throw new AssertionError();
   }

   default boolean tensura$shouldOverride() {
      throw new AssertionError();
   }

   default void tensura$setOverride(boolean override) {
      throw new AssertionError();
   }

   default boolean tensura$hasSource() {
      throw new AssertionError();
   }

   default boolean tensura$hasAbility() {
      throw new AssertionError();
   }

   default boolean tensura$hasTag() {
      throw new AssertionError();
   }
}
