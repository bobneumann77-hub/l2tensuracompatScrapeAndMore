package io.github.manasmods.tensura.ability;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.skill.extra.FlameDominationSkill;
import io.github.manasmods.tensura.ability.skill.extra.FlameManipulationSkill;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.effect.debuff.SleepEffect;
import io.github.manasmods.tensura.item.armor.custom.HolyArmamentsArmorItem;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.text.DecimalFormat;
import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SkillUtils {
   public static final DecimalFormat ROUND_DOUBLE = new DecimalFormat("#.#");

   @Nullable
   public static ManasSkillInstance getSkillOrNull(@Nullable LivingEntity entity, @Nullable ManasSkill manasSkill) {
      if (entity != null && manasSkill != null) {
         Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(entity).getSkill(manasSkill);
         return instance.orElse(null);
      } else {
         return null;
      }
   }

   public static boolean hasSkill(LivingEntity entity, ManasSkill manasSkill) {
      ManasSkillInstance instance = getSkillOrNull(entity, manasSkill);
      return instance == null ? false : instance.getMastery() >= 0.0;
   }

   public static boolean hasSkillPermanently(LivingEntity entity, ManasSkill manasSkill) {
      ManasSkillInstance instance = getSkillOrNull(entity, manasSkill);
      return instance == null ? false : !instance.isTemporarySkill();
   }

   public static boolean hasSkillFully(LivingEntity entity, ManasSkill manasSkill) {
      ManasSkillInstance instance = getSkillOrNull(entity, manasSkill);
      if (instance == null) {
         return false;
      } else {
         return instance.getMastery() < 0.0 ? false : !instance.isTemporarySkill();
      }
   }

   public static boolean isSkillMastered(LivingEntity entity, ManasSkill manasSkill) {
      ManasSkillInstance instance = getSkillOrNull(entity, manasSkill);
      return instance == null ? false : instance.isMastered(entity);
   }

   public static boolean isSkillToggled(ManasSkillInstance instance) {
      return instance.isToggled() && instance.getMastery() >= 0.0;
   }

   public static boolean isSkillToggled(LivingEntity entity, ManasSkill skill) {
      ManasSkillInstance instance = getSkillOrNull(entity, skill);
      return instance != null && isSkillToggled(instance);
   }

   public static boolean inSpiritualWorld(ResourceKey<Level> dimension) {
      return dimension == TensuraDimensions.HELL || dimension == TensuraDimensions.LABYRINTH || dimension == TensuraDimensions.BOSS_AREA;
   }

   public static boolean canAutoSmelt(LivingEntity entity) {
      if (shouldCancelInteraction(entity)) {
         return false;
      } else if (entity.isShiftKeyDown()) {
         return false;
      } else if (isSkillToggled(entity, (ManasSkill)ExtraSkills.BLACK_FLAME.get())) {
         return true;
      } else {
         return (
                  !((FlameManipulationSkill)ExtraSkills.FLAME_MANIPULATION.get()).isInSlot(entity)
                     || !hasSkill(entity, (ManasSkill)ExtraSkills.FLAME_MANIPULATION.get())
               )
               && (
                  !((FlameDominationSkill)ExtraSkills.FLAME_DOMINATION.get()).isInSlot(entity)
                     || !hasSkill(entity, (ManasSkill)ExtraSkills.FLAME_DOMINATION.get())
               )
            ? false
            : FlameManipulationSkill.canUseFire(entity);
      }
   }

   public static boolean canBlockSoundDetect(Entity entity) {
      if (entity.getType().is(TensuraEntityTags.NO_SOUND)) {
         return entity.getType().equals(EntityType.ITEM) ? entity.onGround() : entity.getDeltaMovement().length() < 0.05;
      } else if (entity instanceof LivingEntity target) {
         return shouldCancelInteraction(target) ? true : canBlockSoundDetect(target);
      } else {
         return false;
      }
   }

   public static boolean canBlockSoundDetect(LivingEntity target) {
      if (target.getAttributeValue(TensuraAttributes.PRESENCE_CONCEALMENT) >= 2.0) {
         return true;
      } else if (isSkillToggled(target, (ManasSkill)UniqueSkills.MURDERER.get())) {
         return true;
      } else {
         return isSkillToggled(target, (ManasSkill)ExtraSkills.SOUND_DOMINATION.get())
            ? true
            : isSkillToggled(target, (ManasSkill)ExtraSkills.SOUND_MANIPULATION.get());
      }
   }

   public static boolean canFlyLegit(Player player) {
      if (player.isCreative() || player.isSpectator()) {
         return true;
      } else if (HolyArmamentsArmorItem.isFullSet(player)) {
         return true;
      } else {
         return !player.getActiveEffects().isEmpty() && player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.BATS_MODE))
            ? true
            : canFlyWithSkills(player);
      }
   }

   public static boolean canFlyWithSkills(Player player) {
      if (TensuraStorages.getExistenceFrom(player).isSpiritualForm()) {
         return true;
      }

      if (hasSkill(player, (ManasSkill)ExtraSkills.GRAVITY_MANIPULATION.get())) {
         return true;
      }

      if (hasSkill(player, (ManasSkill)ExtraSkills.GRAVITY_DOMINATION.get())) {
         return true;
      }

      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
      if (optional.isPresent() && optional.get().is(TensuraRaceTags.HAS_CREATIVE_FLIGHT)) {
         return true;
      }

      if (player.getActiveEffects().isEmpty()) {
         return false;
      }

      MobEffectInstance beast = player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.BEAST_TRANSFORMATION));
      return beast != null && beast.getAmplifier() >= 1;
   }

   public static boolean canSeeIllusion(LivingEntity user) {
      double sense = user.getAttributeValue(TensuraAttributes.PRESENCE_SENSE);
      return sense >= 3.0 ? true : isSkillToggled(user, (ManasSkill)UniqueSkills.FALSIFIER.get());
   }

   public static boolean shouldCancelCriticalChance(LivingEntity entity) {
      return isSkillToggled(entity, (ManasSkill)IntrinsicSkills.UNPREDICTABILITY.get());
   }

   public static boolean shouldCancelJump(LivingEntity entity) {
      if (SleepEffect.isForcedSleeping(entity)) {
         return true;
      } else if (entity.getActiveEffects().isEmpty()) {
         return false;
      } else if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FROST))) {
         return true;
      } else if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.WEBBED))) {
         return true;
      } else if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.LUST_EMBRACEMENT))) {
         return true;
      } else if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT))) {
         return true;
      } else {
         return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.REST))
            ? true
            : entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SHADOW_STEP));
      }
   }

   public static boolean shouldCancelInteraction(LivingEntity entity) {
      return shouldCancelInteraction(entity, false);
   }

   public static boolean shouldCancelInteraction(LivingEntity entity, boolean takeDamage) {
      if (takeDamage) {
         return entity.getActiveEffects().isEmpty() ? false : entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SHADOW_STEP));
      } else if (SleepEffect.isForcedSleeping(entity)) {
         return true;
      } else if (entity.getActiveEffects().isEmpty()) {
         return false;
      } else {
         return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.BATS_MODE))
            ? true
            : entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SHADOW_STEP));
      }
   }

   public static boolean shouldCancelInvisibility(LivingEntity user, Entity target) {
      if (user == target) {
         return true;
      }

      double concealment = 0.0;
      if (target instanceof LivingEntity entity) {
         if (!entity.getType().equals(EntityType.PLAYER) && SubordinateHelper.isSubordinate(user, entity)) {
            return true;
         }

         concealment = entity.getAttributeValue(TensuraAttributes.PRESENCE_CONCEALMENT);
      }

      double sense = user.getAttributeValue(TensuraAttributes.PRESENCE_SENSE);
      if (sense <= 0.5) {
         return false;
      } else if (concealment <= 2.0) {
         return sense > concealment ? true : isSkillToggled(user, (ManasSkill)UniqueSkills.FALSIFIER.get());
      } else {
         return sense > concealment;
      }
   }

   public static boolean shouldCancelHealing(LivingEntity entity) {
      if (entity.getActiveEffects().isEmpty()) {
         return false;
      }

      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT))) {
         return true;
      }

      MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSTANT_REGENERATION));
      if (instance != null && instance.getAmplifier() >= 1) {
         return false;
      }

      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.CURSE))) {
         return true;
      }

      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FROST))) {
         return true;
      }

      MobEffectInstance backBurnt = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.BLACK_BURN));
      return backBurnt != null && backBurnt.getAmplifier() >= 1;
   }

   public static boolean shouldCancelPainShake(LivingEntity entity) {
      if (TensuraStorages.getEffectFrom(entity).isIgnorePainNull()) {
         return false;
      } else {
         return shouldCancelPain(entity) ? true : isSkillToggled(entity, (ManasSkill)ResistanceSkills.PAIN_RESISTANCE.get());
      }
   }

   public static boolean shouldCancelPain(LivingEntity entity) {
      if (TensuraStorages.getEffectFrom(entity).isIgnorePainNull()) {
         return false;
      } else {
         return isSkillToggled(entity, (ManasSkill)UniqueSkills.SURVIVOR.get())
            ? true
            : isSkillToggled(entity, (ManasSkill)ResistanceSkills.PAIN_NULLIFICATION.get());
      }
   }

   public static boolean shouldCancelTeleportation(LivingEntity target) {
      if (target.getActiveEffects().isEmpty()) {
         return false;
      } else if (target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.LUST_EMBRACEMENT))) {
         return true;
      } else {
         return target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT))
            ? true
            : target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SPATIAL_BLOCKADE));
      }
   }

   public static boolean shouldCancelDimensionTravel(LivingEntity target) {
      if (target.getActiveEffects().isEmpty()) {
         return false;
      } else if (target.hasEffect(TensuraMobEffects.SHADOW_STEP)) {
         return true;
      } else if (target.hasEffect(TensuraMobEffects.LUST_EMBRACEMENT)) {
         return true;
      } else {
         return target.hasEffect(TensuraMobEffects.INFINITE_IMPRISONMENT) ? true : target.hasEffect(TensuraMobEffects.SPATIAL_BLOCKADE);
      }
   }

   public static boolean shouldCancelSeverance(LivingEntity target, @Nullable DamageSource source) {
      if (target.getType().is(TensuraEntityTags.NO_SEVERANCE)) {
         return true;
      } else {
         return source != null && source.tensura$isPhysicalConverted() ? true : isSkillMastered(target, (ManasSkill)UniqueSkills.SUPPRESSOR.get());
      }
   }
}
