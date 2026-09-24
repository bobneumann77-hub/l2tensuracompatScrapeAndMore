package io.github.manasmods.tensura.race;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.Nullable;

public abstract class TensuraRace extends ManasRace {
   public static final RaceConfig BASE_CONFIG = (RaceConfig)ConfigRegistry.getConfig(RaceConfig.class);
   public static final ResourceLocation DEFAULT_RACE_ID = ResourceLocation.fromNamespaceAndPath("tensura", "race_stats");

   public TensuraRace(Difficulty difficulty) {
      super(difficulty);
   }

   public abstract Pair<Double, Double> getBaseAuraRange();

   public abstract Pair<Double, Double> getBaseMagiculeRange();

   public double getMinBaseAura() {
      return (Double)this.getBaseAuraRange().getFirst();
   }

   public double getMaxBaseAura() {
      return (Double)this.getBaseAuraRange().getSecond();
   }

   public double getMinBaseMagicule() {
      return (Double)this.getBaseMagiculeRange().getFirst();
   }

   public double getMaxBaseMagicule() {
      return (Double)this.getBaseMagiculeRange().getSecond();
   }

   public void resetExistenceData(LivingEntity entity) {
      entity.setHealth(entity.getMaxHealth());
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      existence.setOriginalAlignment(this.getAlignment());
      existence.setAlignment(this.getAlignment());
      AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
      if (aura != null) {
         double newAura = Math.round(this.getMinBaseAura() + (this.getMaxBaseAura() - this.getMinBaseAura()) * entity.getRandom().nextFloat());
         aura.setBaseValue(newAura);
         existence.setAura(newAura);
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (magicule != null) {
         double newMagicule = Math.round(this.getMinBaseMagicule() + (this.getMaxBaseMagicule() - this.getMinBaseMagicule()) * entity.getRandom().nextFloat());
         magicule.setBaseValue(newMagicule);
         existence.setMagicule(newMagicule);
      }

      existence.markDirty();
   }

   public void learnIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      for (ManasSkill skill : instance.getIntrinsicSkills(entity)) {
         TensuraSkillInstance skillInstance = new TensuraSkillInstance(skill);
         if (skillInstance.canBeToggled(entity)) {
            skillInstance.setToggled(true);
         }

         skillInstance.getOrCreateTag().putBoolean("NoMagiculeCost", true);
         if (SkillHelper.learnSkill(entity, skillInstance)) {
            if (skillInstance.isToggled()) {
               skillInstance.onToggleOn(entity);
            }

            instance.addIntrinsicSkill(skill);
            instance.markDirty();
         }
      }

      this.gainIntrinsicLearnable(instance, entity);
   }

   public void gainIntrinsicLearnable(ManasRaceInstance instance, LivingEntity entity) {
      for (TensuraSkill skill : this.getIntrinsicLearnable(instance, entity)) {
         if (SkillHelper.learnSkill(entity, skill.createLearningInstance(entity))) {
            instance.addIntrinsicSkill(skill);
            instance.markDirty();
         }
      }
   }

   @Nullable
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return null;
   }

   @Nullable
   public ManasRace getHarvestFestivalEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return null;
   }

   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      return Map.of(new EvolutionRequirement.EPRequirement(this.getMinBaseAura() + this.getMinBaseMagicule()), 100.0F);
   }

   public float getEvolutionProgress(ManasRaceInstance instance, LivingEntity entity, ManasRace evolution) {
      if (!(evolution instanceof TensuraRace race)) {
         return 0.0F;
      } else {
         float progress = 0.0F;

         for (Entry<EvolutionRequirement, Float> entry : race.getEvolutionRequirements(instance, entity).entrySet()) {
            progress += entry.getValue() * Math.min(1.0F, entry.getKey().getProgress(instance, entity));
         }

         return Math.min(100.0F, progress);
      }
   }

   public void triggerEvolutionRewards(ManasRaceInstance instance, LivingEntity entity) {
      entity.setHealth(entity.getMaxHealth());
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      existence.setSpiritualHealth(entity.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH));
      existence.markDirty();
      AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
      if (aura != null && aura.getBaseValue() < this.getMinBaseAura()) {
         aura.setBaseValue(this.getMinBaseAura());
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (magicule != null && magicule.getBaseValue() < this.getMinBaseMagicule()) {
         magicule.setBaseValue(this.getMinBaseMagicule());
      }
   }

   public Alignment getAlignment() {
      return Alignment.DEFAULT;
   }

   public List<TensuraSkill> getIntrinsicLearnable(ManasRaceInstance instance, LivingEntity entity) {
      return new ArrayList<>();
   }

   public boolean hasGuaranteeElemental() {
      return false;
   }

   public double getElementalSpiritsChance(Element element, SpiritualMagic.SpiritLevel level) {
      return getDefaultElementalSpiritsChance(level);
   }

   public static double getDefaultElementalSpiritsChance(SpiritualMagic.SpiritLevel level) {
      return switch (level) {
         case LESSER -> BASE_CONFIG.Spirit.lesserSpiritPercentage;
         case MEDIUM -> BASE_CONFIG.Spirit.mediumSpiritPercentage;
         case GREATER -> BASE_CONFIG.Spirit.greaterSpiritPercentage;
         case LORD -> BASE_CONFIG.Spirit.lordSpiritPercentage;
      };
   }
}
