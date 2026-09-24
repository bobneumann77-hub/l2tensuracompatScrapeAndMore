package io.github.manasmods.tensura.race;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.intrinsic.DivineKiReleaseSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.item.armor.custom.HolyArmamentsArmorItem;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;

public class RaceUtils {
   public static boolean isNonLiving(Entity entity) {
      return !entity.isAlive() ? false : entity.getType().is(TensuraEntityTags.NON_LIVING);
   }

   public static boolean isSpiritual(LivingEntity entity) {
      if (entity.getType().is(TensuraEntityTags.SPIRITUAL)) {
         return true;
      }

      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
      return optional.isPresent() && optional.get().is(TensuraRaceTags.SPIRITUAL) ? true : TensuraStorages.getExistenceFrom(entity).isSpiritualForm();
   }

   public static boolean isAffectedByHolyExposure(Entity entity) {
      if (entity.getType().is(TensuraEntityTags.MONSTER)) {
         return true;
      } else if (entity instanceof LivingEntity target) {
         IExistence existence = TensuraStorages.getExistenceFrom(target);
         return existence.getAlignment().isAffectedByHolyExposure();
      } else {
         return false;
      }
   }

   public static boolean isBloodless(Entity entity) {
      if (entity.getType().is(TensuraEntityTags.NO_BLOOD)) {
         return true;
      } else if (entity.getType().is(TensuraEntityTags.SPIRITUAL)) {
         return true;
      } else if (entity instanceof LivingEntity living) {
         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(living).getRace();
         return optional.isPresent() && optional.get().is(TensuraRaceTags.NO_BLOOD) ? true : TensuraStorages.getExistenceFrom(living).isSpiritualForm();
      } else {
         return isUndead(entity);
      }
   }

   public static boolean isUndead(Entity entity) {
      if (entity instanceof LivingEntity target) {
         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(target).getRace();
         if (optional.isPresent() && optional.get().is(TensuraRaceTags.UNDEAD)) {
            return true;
         }
      }

      return entity.getType().is(EntityTypeTags.UNDEAD);
   }

   public static boolean isAlreadyAwakened(LivingEntity entity, IExistence existence) {
      if (shouldNamingStopAwakening(entity, existence)) {
         return true;
      } else {
         return !existence.isTrueDemonLord() && existence.getHarvestTick() <= 0 ? existence.isTrueHero() : true;
      }
   }

   public static boolean canAwaken(LivingEntity entity, boolean isHero) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      if (existence.getHarvestGiftTick() > 0 || isAlreadyAwakened(entity, existence)) {
         return false;
      }

      if (isHero) {
         if (!existence.isHeroEgg()) {
            return false;
         }

         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
         return optional.isPresent() && optional.get().getRace() instanceof TensuraRace race && race.getAlignment().equals(Alignment.MAJIN)
            ? false
            : isFightingBossForHero(entity);
      } else {
         return existence.isDemonLordSeed() && existence.getSoulPoints() >= entity.level().getGameRules().getInt(TensuraGameRules.DEMON_LORD_AWAKEN) * 1000;
      }
   }

   public static boolean shouldNamingStopAwakening(LivingEntity entity, IExistence existence) {
      return !entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE)
         ? false
         : existence.getName() != null && existence.getPermanentOwner() != null;
   }

   public static boolean isFightingBossForHero(LivingEntity entity) {
      LivingEntity lastHurtBy = entity.getLastHurtByMob();
      if (lastHurtBy != null
         && lastHurtBy.getType().is(TensuraEntityTags.HERO_BOSS)
         && SubordinateHelper.getSubordinateOwnerUUID(lastHurtBy) == null
         && lastHurtBy.getHealth() < lastHurtBy.getMaxHealth() / 2.0F
         && EnergyHelper.getMaxEP(lastHurtBy) >= 400000.0) {
         return true;
      } else {
         LivingEntity lastHurt = entity.getLastHurtMob();
         if (lastHurt == null) {
            return false;
         } else if (!lastHurt.getType().is(TensuraEntityTags.HERO_BOSS)) {
            return false;
         } else if (SubordinateHelper.getSubordinateOwnerUUID(lastHurt) != null) {
            return false;
         } else {
            return lastHurt.getHealth() >= lastHurt.getMaxHealth() / 2.0F ? false : EnergyHelper.getMaxEP(lastHurt) >= 400000.0;
         }
      }
   }

   public static boolean isInWeather(Entity entity) {
      BlockPos blockPos = entity.blockPosition();
      return isWeatheringAt(entity.level(), blockPos)
         || isWeatheringAt(entity.level(), BlockPos.containing(blockPos.getX(), entity.getBoundingBox().maxY, blockPos.getZ()));
   }

   public static boolean isWeatheringAt(Level level, BlockPos blockPos) {
      if (!level.isRaining()) {
         return false;
      }

      Biome biome = (Biome)level.getBiome(blockPos).value();
      return biome.getPrecipitationAt(blockPos) != Precipitation.NONE;
   }

   public static boolean isUnderSun(LivingEntity entity) {
      if (!entity.isAlive()) {
         return false;
      }

      if (entity.hasInfiniteMaterials()) {
         return false;
      }

      if (!entity.level().isDay()) {
         return false;
      }

      if (entity.level().getBiome(entity.blockPosition()).is(TensuraBiomeTags.IS_MIASMIC)) {
         return false;
      }

      if (SkillUtils.shouldCancelInteraction(entity, true)) {
         return false;
      }

      if (!entity.isInLiquid() || !entity.isInWaterOrBubble() && !entity.isInPowderSnow && !entity.wasInPowderSnow) {
         if (isInWeather(entity)) {
            return false;
         }

         float f = entity.getLightLevelDependentMagicValue();
         return f > 0.5F && entity.level().canSeeSky(ObjectSelectionHelper.getBlockPos(entity.getEyePosition()));
      } else {
         return false;
      }
   }

   public static float getPhysicalAttackInputMultiplier(DamageSource damageSource) {
      if (!TensuraDamageHelper.isPhysicalAttack(damageSource)) {
         return 1.0F;
      }

      if (damageSource.getEntity() instanceof LivingEntity entity) {
         AttributeInstance degrade = entity.getAttribute(TensuraAttributes.PHYSICAL_RESIST_DEGRADATION);
         if (degrade != null && degrade.hasModifier(DivineKiReleaseSkill.DIVINE_KI)) {
            return 1.0F;
         }

         if (TensuraStorages.getAbilityFrom(entity).isAbilityInActivePreset((ManasSkill)UniqueSkills.ANTI_SKILL.get())) {
            return 1.0F;
         }

         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.HAKI_COAT));
         if (instance != null) {
            if (instance.getAmplifier() >= 1) {
               return 1.0F;
            }

            if (instance.getAmplifier() >= 0) {
               return 0.5F;
            }
         }

         if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_AURA))) {
            return 0.5F;
         }

         if (SkillUtils.isSkillToggled(entity, (ManasSkill)UniqueSkills.COOK.get())) {
            return 0.5F;
         }
      }

      return 0.01F;
   }

   public static boolean canStillFly(Player player, boolean spiritual, boolean bat, boolean race) {
      if (player.isCreative() || player.isSpectator()) {
         return true;
      }

      if (spiritual && TensuraStorages.getExistenceFrom(player).isSpiritualForm()) {
         return true;
      }

      if (bat && player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.BATS_MODE))) {
         return true;
      }

      if (race) {
         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
         if (optional.isPresent() && optional.get().is(TensuraRaceTags.HAS_CREATIVE_FLIGHT)) {
            return true;
         }
      }

      return HolyArmamentsArmorItem.isFullSet(player);
   }
}
