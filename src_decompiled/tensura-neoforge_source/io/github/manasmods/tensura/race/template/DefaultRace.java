package io.github.manasmods.tensura.race.template;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.effect.debuff.SleepEffect;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.ExistenceStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public abstract class DefaultRace extends TensuraRace {
   public DefaultRace(Difficulty difficulty) {
      super(difficulty);
   }

   public abstract RaceConfig.Default getDefaultConfig();

   public void applyDefaultAttributeModifiers() {
      this.addAttributeModifier(Attributes.SCALE, DEFAULT_RACE_ID, this.getDefaultConfig().getSize(), Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.MAX_HEALTH, DEFAULT_RACE_ID, this.getDefaultConfig().getMaxHealth(), Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.MAX_SPIRITUAL_HEALTH, DEFAULT_RACE_ID, this.getDefaultConfig().getMaxSpiritualHealth(), Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, DEFAULT_RACE_ID, this.getDefaultConfig().getAttack(), Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, DEFAULT_RACE_ID, this.getDefaultConfig().getAttackSpeed(), Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, DEFAULT_RACE_ID, this.getDefaultConfig().getKnockbackResistance(), Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, DEFAULT_RACE_ID, this.getDefaultConfig().getMovementSpeed(), Operation.ADD_VALUE);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, DEFAULT_RACE_ID, this.getDefaultConfig().getSwimSpeed(), Operation.ADD_VALUE);
   }

   @Override
   public Pair<Double, Double> getBaseAuraRange() {
      return Pair.of(this.getDefaultConfig().getMinAura(), this.getDefaultConfig().getMaxAura());
   }

   @Override
   public Pair<Double, Double> getBaseMagiculeRange() {
      return Pair.of(this.getDefaultConfig().getMinMagicule(), this.getDefaultConfig().getMaxMagicule());
   }

   public boolean canActivateAbility(ManasRaceInstance instance, LivingEntity entity) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      if (ExistenceStorage.isInSleepMode(existence) || SleepEffect.isSleeping(entity)) {
         return false;
      } else if (!instance.is(TensuraRaceTags.SPIRITUAL) && !entity.hasInfiniteMaterials() && existence.isSpiritualForm()) {
         return false;
      } else if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE))) {
         return false;
      } else if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT))) {
         return false;
      } else {
         return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.REST))
            ? false
            : !entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SHADOW_STEP));
      }
   }

   public List<ManasSkill> getRenderingIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      return this.getIntrinsicSkills(instance, entity);
   }
}
