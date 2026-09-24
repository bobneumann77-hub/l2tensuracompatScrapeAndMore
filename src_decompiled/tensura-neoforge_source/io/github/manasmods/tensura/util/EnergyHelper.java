package io.github.manasmods.tensura.util;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.summon.ISummoning;
import io.github.manasmods.tensura.config.EnergyConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.entity.template.subclass.ILivingPartEntity;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnergyHelper {
   public static final EnergyConfig CONFIG = (EnergyConfig)ConfigRegistry.getConfig(EnergyConfig.class);
   public static final ResourceLocation SPIRITUAL_EP_LIMITED = ResourceLocation.fromNamespaceAndPath("tensura", "spiritual_ep_limited");

   public static double getMaxEP(LivingEntity entity) {
      return getMaxMagicule(entity) + getMaxAura(entity);
   }

   public static double getBaseMaxEP(LivingEntity entity) {
      return getBaseMaxMagicule(entity) + getBaseMaxAura(entity);
   }

   public static void setBaseMaxEP(LivingEntity entity, double amount) {
      setMaxMagicule(entity, amount / 2.0);
      setMaxAura(entity, amount / 2.0);
   }

   public static void increaseMaxEP(LivingEntity entity, double amount) {
      AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
      if (aura != null) {
         aura.setBaseValue(aura.getBaseValue() + amount);
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (magicule != null) {
         magicule.setBaseValue(magicule.getBaseValue() + amount);
      }
   }

   public static void multiplyMaxEP(LivingEntity entity, double amount) {
      AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
      if (aura != null) {
         aura.setBaseValue(aura.getBaseValue() * amount);
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (magicule != null) {
         magicule.setBaseValue(magicule.getBaseValue() * amount);
      }
   }

   public static double getMaxMagicule(LivingEntity entity) {
      double value = entity.getAttributeValue(TensuraAttributes.MAX_MAGICULE);
      double limited = entity.getAttributeValue(TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE);
      return limited > 0.0 ? Math.min(value, limited) : value;
   }

   public static double getBaseMaxMagicule(LivingEntity entity) {
      AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (instance == null) {
         return 0.0;
      }

      double limited = entity.getAttributeValue(TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE);
      return limited > 0.0 ? Math.min(instance.getBaseValue(), limited) : instance.getBaseValue();
   }

   public static void setMaxMagicule(LivingEntity entity, double amount) {
      AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (instance != null) {
         instance.setBaseValue(amount);
      }
   }

   public static double getMaxAura(LivingEntity entity) {
      double value = entity.getAttributeValue(TensuraAttributes.MAX_AURA);
      double limited = entity.getAttributeValue(TensuraAttributes.LIMITED_SPIRITUAL_MAX_AURA);
      return limited > 0.0 ? Math.min(value, limited) : value;
   }

   public static double getBaseMaxAura(LivingEntity entity) {
      AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_AURA);
      if (instance == null) {
         return 0.0;
      }

      double limited = entity.getAttributeValue(TensuraAttributes.LIMITED_SPIRITUAL_MAX_AURA);
      return limited > 0.0 ? Math.min(instance.getBaseValue(), limited) : instance.getBaseValue();
   }

   public static void setMaxAura(LivingEntity entity, double amount) {
      AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_AURA);
      if (instance != null) {
         instance.setBaseValue(amount);
      }
   }

   public static double getEPGain(LivingEntity pTarget) {
      return getEPGain(pTarget, null, false);
   }

   public static double getEPGain(LivingEntity pTarget, @Nullable LivingEntity attacker) {
      return getEPGain(pTarget, attacker, false);
   }

   public static double getEPGain(LivingEntity target, @Nullable LivingEntity attacker, boolean applyReduction) {
      if (target.getType().is(TensuraEntityTags.NO_EP_PLUNDER)) {
         return 0.0;
      }

      Level level = target.level();
      double EP = getBaseMaxEP(target);
      if (target.getType().equals(EntityType.PLAYER)) {
         return EP * level.getGameRules().getInt(TensuraGameRules.PLAYER_EP) / 100.0;
      }

      EP *= getEPMultiplierByNamespace(target);
      if (target instanceof Mob mob) {
         EP = getEPGain(mob, EP);
      }

      if (applyReduction && attacker != null) {
         int times = (int)(getBaseMaxEP(attacker) / EP);
         double percentage = Mth.clamp(0.01 * times, 0.0, CONFIG.maxEPReductionPercentage / 100.0);
         return EP * (1.0 - percentage);
      } else {
         return EP;
      }
   }

   public static double getEPGain(Mob mob, double EP) {
      IExistence existence = TensuraStorages.getExistenceFrom(mob);
      if (existence.getSpawnType() == MobSpawnType.MOB_SUMMONED) {
         return 0.0;
      }

      if (existence.getSpawnType() == MobSpawnType.TRIGGERED) {
         return 0.0;
      }

      if (existence.getSpawnType() == MobSpawnType.SPAWNER || existence.getSpawnType() == MobSpawnType.TRIAL_SPAWNER) {
         int spawnerEP = mob.level().getGameRules().getInt(TensuraGameRules.SPAWNER_EP);
         if (spawnerEP != 100) {
            EP *= spawnerEP / 100.0F;
         }
      }

      return EP;
   }

   public static float getEPMultiplierByNamespace(Entity entity) {
      Level level = entity.level();
      ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
      if (id.getNamespace().equals("minecraft")) {
         return level.getGameRules().getInt(TensuraGameRules.VANILLA_EP) / 100.0F;
      } else {
         return id.getNamespace().equals("tensura")
            ? level.getGameRules().getInt(TensuraGameRules.TENSURA_EP) / 100.0F
            : level.getGameRules().getInt(TensuraGameRules.MODDED_EP) / 100.0F;
      }
   }

   public static void applySpiritualEPLimit(LivingEntity entity, IExistence existence) {
      AttributeHelper.addPermanentAttribute(
         entity, TensuraAttributes.LIMITED_SPIRITUAL_MAX_AURA, SPIRITUAL_EP_LIMITED, TensuraRace.BASE_CONFIG.limitedSpiritualAura, Operation.ADD_VALUE
      );
      AttributeHelper.addPermanentAttribute(
         entity, TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE, SPIRITUAL_EP_LIMITED, TensuraRace.BASE_CONFIG.limitedSpiritualMagicule, Operation.ADD_VALUE
      );
      double maxAP = getMaxAura(entity);
      if (existence.getAura() > maxAP) {
         existence.setAura(maxAP);
      }

      double maxMP = getMaxMagicule(entity);
      if (existence.getMagicule() > maxMP) {
         existence.setMagicule(maxMP);
      }

      existence.markDirty();
   }

   public static void removeSpiritualEPLimit(LivingEntity entity) {
      AttributeInstance aura = entity.getAttribute(TensuraAttributes.LIMITED_SPIRITUAL_MAX_AURA);
      if (aura != null) {
         aura.removeModifier(ISummoning.SUMMONING_BOOST);
         aura.removeModifier(SPIRITUAL_EP_LIMITED);
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE);
      if (magicule != null) {
         magicule.removeModifier(ISummoning.SUMMONING_BOOST);
         magicule.removeModifier(SPIRITUAL_EP_LIMITED);
      }
   }

   public static boolean hasEnergyDrainImmunity(LivingEntity target, @Nullable Entity attacker) {
      if (target.hasInfiniteMaterials()) {
         return true;
      } else if (target.getType().is(TensuraEntityTags.NO_ENERGY_DRAIN)) {
         return true;
      } else if (TensuraGameRules.isLabyrinthPvpOff(target.level(), target, attacker)) {
         return true;
      } else {
         return target == attacker
            ? false
            : SkillUtils.isSkillToggled(target, (ManasSkill)UniqueSkills.ANTI_SKILL.get())
               || TensuraStorages.getAbilityFrom(target).isAbilityInActivePreset((ManasSkill)UniqueSkills.ANTI_SKILL.get());
      }
   }

   public static boolean drainEnergy(
      LivingEntity source, @Nullable Entity attacker, double amount, boolean percentage, EnergyHelper.DrainType drainType, EnergyHelper.GainType gainType
   ) {
      if (amount <= 0.0) {
         return false;
      }

      source = ILivingPartEntity.checkForHead(source);
      if (hasEnergyDrainImmunity(source, attacker)) {
         return false;
      }

      int protection = TensuraEnchantmentHelper.getEnchantmentLevel(source.level(), TensuraEnchantments.ENERGY_PROTECTION, source);
      if (protection > 0) {
         amount *= 1.0F - protection * 0.1F;
      }

      if (amount <= 0.0) {
         return false;
      }

      Changeable<Double> amountChangeable = Changeable.of(amount);
      Changeable<Boolean> drainingPercentage = Changeable.of(percentage);
      Changeable<EnergyHelper.DrainType> drainChangeable = Changeable.of(drainType);
      Changeable<EnergyHelper.GainType> gainChangeable = Changeable.of(gainType);
      if (!((TensuraEntityEvents.EnergyDrainEvent)TensuraEntityEvents.ENERGY_DRAIN_EVENT.invoker())
         .drain(source, attacker, drainChangeable, gainChangeable, amountChangeable, drainingPercentage)
         .isFalse()) {
         IExistence existence = TensuraStorages.getExistenceFrom(source);
         switch ((EnergyHelper.DrainType)drainChangeable.get()) {
            case AURA:
               AttributeInstance aura = source.getAttribute(TensuraAttributes.MAX_AURA);
               if (aura == null) {
                  return false;
               }

               double drain = Math.min(
                  existence.getAura(), drainingPercentage.get() ? existence.getAura() * (Double)amountChangeable.get() : (Double)amountChangeable.get()
               );
               if (attacker instanceof LivingEntity entity) {
                  gainAura(entity, drain, (EnergyHelper.GainType)gainChangeable.get());
               }

               existence.setAura(existence.getAura() - drain);
               break;
            case MAX_AURA:
               AttributeInstance aura = source.getAttribute(TensuraAttributes.MAX_AURA);
               if (aura == null) {
                  return false;
               }

               double drain = Math.min(
                  aura.getValue(), drainingPercentage.get() ? aura.getValue() * (Double)amountChangeable.get() : (Double)amountChangeable.get()
               );
               if (attacker instanceof LivingEntity entity) {
                  gainAura(entity, drain, (EnergyHelper.GainType)gainChangeable.get());
               }

               double minAP = ((RangedAttribute)TensuraAttributes.MAX_AURA.value()).getMinValue();
               aura.setBaseValue(Math.max(minAP, aura.getBaseValue() - drain));
               existence.setAura(existence.getAura() - drain);
               break;
            case MAGICULE:
               AttributeInstance magicule = source.getAttribute(TensuraAttributes.MAX_MAGICULE);
               if (magicule == null) {
                  return false;
               }

               double drain = Math.min(
                  existence.getMagicule(), drainingPercentage.get() ? existence.getMagicule() * (Double)amountChangeable.get() : (Double)amountChangeable.get()
               );
               if (attacker instanceof LivingEntity entity) {
                  gainMagicule(entity, drain, (EnergyHelper.GainType)gainChangeable.get());
               }

               existence.setMagicule(existence.getMagicule() - drain);
               break;
            case MAX_MAGICULE:
               AttributeInstance magicule = source.getAttribute(TensuraAttributes.MAX_MAGICULE);
               if (magicule == null) {
                  return false;
               }

               double drain = Math.min(
                  magicule.getValue(), drainingPercentage.get() ? magicule.getValue() * (Double)amountChangeable.get() : (Double)amountChangeable.get()
               );
               if (attacker instanceof LivingEntity entity) {
                  gainMagicule(entity, drain, (EnergyHelper.GainType)gainChangeable.get());
               }

               double minMP = ((RangedAttribute)TensuraAttributes.MAX_MAGICULE.value()).getMinValue();
               magicule.setBaseValue(Math.max(minMP, magicule.getBaseValue() - drain));
               existence.setMagicule(existence.getMagicule() - drain);
               break;
            case EP:
               boolean success = false;
               AttributeInstance aura = source.getAttribute(TensuraAttributes.MAX_AURA);
               if (aura != null) {
                  double drain = Math.min(
                     existence.getAura(), drainingPercentage.get() ? existence.getAura() * (Double)amountChangeable.get() : (Double)amountChangeable.get()
                  );
                  if (attacker instanceof LivingEntity entity) {
                     gainAura(entity, drain, (EnergyHelper.GainType)gainChangeable.get());
                  }

                  existence.setAura(existence.getAura() - drain);
                  success = true;
               }

               AttributeInstance magicule = source.getAttribute(TensuraAttributes.MAX_MAGICULE);
               if (magicule != null) {
                  double drain = Math.min(
                     existence.getMagicule(),
                     drainingPercentage.get() ? existence.getMagicule() * (Double)amountChangeable.get() : (Double)amountChangeable.get()
                  );
                  if (attacker instanceof LivingEntity entity) {
                     gainMagicule(entity, drain, (EnergyHelper.GainType)gainChangeable.get());
                  }

                  existence.setMagicule(existence.getMagicule() - drain);
                  success = true;
               }

               if (!success) {
                  return false;
               }
               break;
            case MAX_EP:
               boolean success = false;
               AttributeInstance aura = source.getAttribute(TensuraAttributes.MAX_AURA);
               if (aura != null) {
                  double drain = Math.min(
                     aura.getValue(), drainingPercentage.get() ? aura.getValue() * (Double)amountChangeable.get() : (Double)amountChangeable.get()
                  );
                  if (attacker instanceof LivingEntity entity) {
                     gainAura(entity, drain, (EnergyHelper.GainType)gainChangeable.get());
                  }

                  double minAP = ((RangedAttribute)TensuraAttributes.MAX_AURA.value()).getMinValue();
                  aura.setBaseValue(Math.max(minAP, aura.getBaseValue() - drain));
                  existence.setAura(existence.getAura() - drain);
                  success = true;
               }

               AttributeInstance magicule = source.getAttribute(TensuraAttributes.MAX_MAGICULE);
               if (magicule != null) {
                  double drain = Math.min(
                     magicule.getValue(), drainingPercentage.get() ? magicule.getValue() * (Double)amountChangeable.get() : (Double)amountChangeable.get()
                  );
                  if (attacker instanceof LivingEntity entity) {
                     gainMagicule(entity, drain, (EnergyHelper.GainType)gainChangeable.get());
                  }

                  double minMP = ((RangedAttribute)TensuraAttributes.MAX_MAGICULE.value()).getMinValue();
                  magicule.setBaseValue(Math.max(minMP, magicule.getBaseValue() - drain));
                  existence.setMagicule(existence.getMagicule() - drain);
                  success = true;
               }

               if (!success) {
                  return false;
               }
         }

         existence.markDirty();
         if (attacker != null) {
            TensuraDamageHelper.markHurt(source, attacker);
         }

         if (existence.getEP() <= 0.0 && source.isAlive()) {
            DamageSource drainSource = TensuraDamageTypes.getEntityDamageSource(source.level(), TensuraDamageTypes.ENERGY_DRAIN, attacker);
            source.getCombatTracker().recordDamage(drainSource, source.getHealth());
            source.setHealth(0.0F);
            source.setAbsorptionAmount(0.0F);
            source.die(drainSource);
         }

         return true;
      } else {
         return false;
      }
   }

   public static void gainAura(LivingEntity entity, double amount, EnergyHelper.GainType type) {
      if (!type.equals(EnergyHelper.GainType.NONE)) {
         if (!(amount <= 0.0) && entity.isAlive()) {
            entity = ILivingPartEntity.checkForHead(entity);
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_AURA);
            if (instance != null) {
               IExistence existence = TensuraStorages.getExistenceFrom(entity);
               switch (type) {
                  case NORMAL: {
                     double newAP = existence.getAura() + amount;
                     double max = instance.getValue();
                     double limited = entity.getAttributeValue(TensuraAttributes.LIMITED_SPIRITUAL_MAX_AURA);
                     if (limited > 0.0) {
                        max = Math.min(max, limited);
                     }

                     existence.setAura(existence.getAura() > max ? newAP : Math.min(max, newAP));
                     break;
                  }
                  case NORMAL_EXCEED_MAX: {
                     double newAP = existence.getAura() + amount;
                     existence.setAura(newAP);
                     break;
                  }
                  case MAX:
                     instance.setBaseValue(instance.getBaseValue() + amount);
                     double max = instance.getValue();
                     double limited = entity.getAttributeValue(TensuraAttributes.LIMITED_SPIRITUAL_MAX_AURA);
                     if (limited > 0.0) {
                        max = Math.min(max, limited);
                     }

                     existence.setAura(Math.min(existence.getAura() + amount, max));
               }

               existence.markDirty();
            }
         }
      }
   }

   public static void gainMagicule(LivingEntity entity, double amount, EnergyHelper.GainType type) {
      if (!type.equals(EnergyHelper.GainType.NONE)) {
         if (!(amount <= 0.0) && entity.isAlive()) {
            entity = ILivingPartEntity.checkForHead(entity);
            AttributeInstance instance = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
            if (instance != null) {
               IExistence existence = TensuraStorages.getExistenceFrom(entity);
               switch (type) {
                  case NORMAL: {
                     double newMP = existence.getMagicule() + amount;
                     double max = instance.getValue();
                     double limited = entity.getAttributeValue(TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE);
                     if (limited > 0.0) {
                        max = Math.min(max, limited);
                     }

                     existence.setMagicule(existence.getMagicule() > max ? newMP : Math.min(max, newMP));
                     break;
                  }
                  case NORMAL_EXCEED_MAX: {
                     double newMP = existence.getMagicule() + amount;
                     existence.setMagicule(newMP);
                     break;
                  }
                  case MAX:
                     instance.setBaseValue(instance.getBaseValue() + amount);
                     double max = instance.getValue();
                     double limited = entity.getAttributeValue(TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE);
                     if (limited > 0.0) {
                        max = Math.min(max, limited);
                     }

                     existence.setMagicule(Math.min(existence.getMagicule() + amount, max));
               }

               existence.markDirty();
            }
         }
      }
   }

   public static boolean isOutOfEnergy(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return isOutOfEnergy(entity, instance, mode, 1.0F);
   }

   public static boolean isOutOfEnergy(LivingEntity entity, ManasSkillInstance skillInstance, int mode, float costMultiplier) {
      return skillInstance.getSkill() instanceof TensuraSkill skill ? skill.isOutOfEnergy(entity, skillInstance, mode, costMultiplier) : false;
   }

   public static boolean isOutOfEnergy(LivingEntity entity, double apCost, double mpCost) {
      if ((!(mpCost <= 0.0) || !(apCost <= 0.0)) && !entity.hasInfiniteMaterials()) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         boolean enoughAP = apCost <= 0.0;
         if (!enoughAP) {
            if (existence.getAura() - apCost >= 0.0) {
               enoughAP = true;
            } else if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.lack_aura").withStyle(ChatFormatting.RED), true);
            }
         }

         boolean enoughMP = mpCost <= 0.0;
         if (!enoughMP) {
            if (existence.getMagicule() - mpCost >= 0.0) {
               enoughMP = true;
            } else if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.lack_magicule").withStyle(ChatFormatting.RED), true);
            }
         }

         if (enoughAP && enoughMP) {
            existence.setAura(existence.getAura() - apCost);
            existence.setMagicule(existence.getMagicule() - mpCost);
            existence.markDirty();
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static double isOutOfMagiculeConsuming(LivingEntity entity, double cost) {
      return isOutOfMagiculeConsuming(entity, cost, 0.0);
   }

   public static double isOutOfMagiculeConsuming(LivingEntity entity, double cost, double remain) {
      if (!(cost <= 0.0) && !entity.hasInfiniteMaterials()) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         double remaining = existence.getMagicule() - cost;
         if (remaining >= remain) {
            existence.setMagicule(remaining);
            existence.markDirty();
            return 0.0;
         }

         existence.setMagicule(remain);
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.skill.lack_magicule").withStyle(ChatFormatting.RED), true);
         }

         return Math.abs(remaining - remain);
      } else {
         return 0.0;
      }
   }

   public static double isOutOfAuraConsuming(LivingEntity entity, double cost) {
      return isOutOfAuraConsuming(entity, cost, 0.0);
   }

   public static double isOutOfAuraConsuming(LivingEntity entity, double cost, double remain) {
      if (!(cost <= 0.0) && !entity.hasInfiniteMaterials()) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         double remaining = existence.getAura() - cost;
         if (remaining >= remain) {
            existence.setAura(remaining);
            existence.markDirty();
            return 0.0;
         }

         existence.setAura(remain);
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.skill.lack_aura").withStyle(ChatFormatting.RED), true);
         }

         return Math.abs(remaining - remain);
      } else {
         return 0.0;
      }
   }

   public enum DrainType {
      AURA,
      MAX_AURA,
      MAGICULE,
      MAX_MAGICULE,
      EP,
      MAX_EP;
   }

   public enum GainType {
      NONE,
      NORMAL,
      NORMAL_EXCEED_MAX,
      MAX;
   }
}
