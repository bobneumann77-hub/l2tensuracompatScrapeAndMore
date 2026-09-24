package io.github.manasmods.tensura.damage;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.Nullable;

public interface TensuraDamageSource {
   default DamageSource tensura$setAbilityInstance(@Nullable ManasSkillInstance instance) {
      throw new AssertionError();
   }

   default ManasSkillInstance tensura$getAbilityInstance() {
      throw new AssertionError();
   }

   default DamageSource tensura$setAbilityMode(int mode) {
      throw new AssertionError();
   }

   default int tensura$getAbilityMode() {
      throw new AssertionError();
   }

   default DamageSource tensura$setSkillType(@Nullable Skill.SkillType type) {
      throw new AssertionError();
   }

   default Skill.SkillType tensura$getSkillType() {
      throw new AssertionError();
   }

   default DamageSource tensura$setMagicType(@Nullable Magic.MagicType type) {
      throw new AssertionError();
   }

   default Magic.MagicType tensura$getMagicType() {
      throw new AssertionError();
   }

   default DamageSource tensura$setElement(@Nullable Element element) {
      throw new AssertionError();
   }

   default Element tensura$getElement() {
      throw new AssertionError();
   }

   default DamageSource tensura$setSlotting() {
      throw new AssertionError();
   }

   default boolean tensura$isSlotting() {
      throw new AssertionError();
   }

   default DamageSource tensura$setSpiritual() {
      throw new AssertionError();
   }

   default boolean tensura$isSpiritual() {
      throw new AssertionError();
   }

   default DamageSource tensura$setPhysicalConverted() {
      throw new AssertionError();
   }

   default boolean tensura$isPhysicalConverted() {
      throw new AssertionError();
   }

   default DamageSource tensura$setBarrierBypassLevel(float level) {
      throw new AssertionError();
   }

   default float tensura$getBarrierBypassLevel() {
      throw new AssertionError();
   }

   default DamageSource tensura$setResistanceBypassLevel(float level) {
      throw new AssertionError();
   }

   default float tensura$getResistanceBypassLevel() {
      throw new AssertionError();
   }

   default DamageSource tensura$setDodgeBypass() {
      throw new AssertionError();
   }

   default boolean tensura$isDodgeBypass() {
      throw new AssertionError();
   }

   default DamageSource tensura$setAuraCost(double cost) {
      throw new AssertionError();
   }

   default double tensura$getAuraCost() {
      throw new AssertionError();
   }

   default DamageSource tensura$setMagiculeCost(double cost) {
      throw new AssertionError();
   }

   default double tensura$getMagiculeCost() {
      throw new AssertionError();
   }

   default DamageSource tensura$setCustomMessage(@Nullable String message) {
      throw new AssertionError();
   }

   default String tensura$getCustomMessage() {
      throw new AssertionError();
   }

   default DamageSource tensura$setNotActualDeath(boolean not) {
      throw new AssertionError();
   }

   default boolean tensura$isNotActualDeath() {
      throw new AssertionError();
   }
}
