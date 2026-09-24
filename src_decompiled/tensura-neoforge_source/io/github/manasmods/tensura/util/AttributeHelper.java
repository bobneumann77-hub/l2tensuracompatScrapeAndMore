package io.github.manasmods.tensura.util;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.attribute.TensuraGlobalAttributeIds;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class AttributeHelper {
   public static AttributeMap getAttributeMap(AttributeSupplier supplier) {
      AttributeMap map = new AttributeMap(supplier);

      for (Holder<Attribute> holder : supplier.instances.keySet()) {
         map.getInstance(holder);
      }

      return map;
   }

   public static void addAnalysisAttributes(ServerPlayer player, int level, int distance) {
      addAnalysisAttributes(player, level, distance, 0.0, TensuraGlobalAttributeIds.ANALYSIS);
   }

   public static void addAnalysisAttributes(ServerPlayer player, int level, int distance, ResourceLocation id) {
      addAnalysisAttributes(player, level, distance, 0.0, id);
   }

   public static void addAnalysisAttributes(ServerPlayer player, int level, int distance, double zoomRange, ResourceLocation id) {
      addPermanentAttributeIfHigher(player, TensuraAttributes.ANALYSIS_LEVEL, id, level, Operation.ADD_VALUE);
      addPermanentAttributeIfHigher(player, TensuraAttributes.ANALYSIS_DISTANCE, id, distance, Operation.ADD_VALUE);
      addPermanentAttributeIfHigher(player, TensuraAttributes.VIEW_ZOOM, id, zoomRange, Operation.ADD_VALUE);
   }

   public static void removeAnalysisAttributes(ServerPlayer player, boolean level, boolean distance, boolean zoom) {
      removeAnalysisAttributes(player, level, distance, zoom, TensuraGlobalAttributeIds.ANALYSIS);
   }

   public static void removeAnalysisAttributes(ServerPlayer player, boolean level, boolean distance, boolean zoom, ResourceLocation id) {
      if (level) {
         AttributeInstance levelAttribute = player.getAttribute(TensuraAttributes.ANALYSIS_LEVEL);
         if (levelAttribute != null) {
            levelAttribute.removeModifier(id);
         }
      }

      if (distance) {
         AttributeInstance distanceAttribute = player.getAttribute(TensuraAttributes.ANALYSIS_DISTANCE);
         if (distanceAttribute != null) {
            distanceAttribute.removeModifier(id);
         }
      }

      if (zoom) {
         AttributeInstance zoomAttribute = player.getAttribute(TensuraAttributes.VIEW_ZOOM);
         if (zoomAttribute != null) {
            zoomAttribute.removeModifier(id);
         }
      }
   }

   public static void multiplyChantSpeed(LivingEntity entity, double level) {
      addPermanentAttributeIfHigher(entity, TensuraAttributes.CHANT_SPEED, TensuraGlobalAttributeIds.CHANT_SPEED, level, Operation.ADD_VALUE);
   }

   public static void removeChantSpeed(LivingEntity entity, double level) {
      removeAttributeIfCorrect(entity, TensuraAttributes.CHANT_SPEED, TensuraGlobalAttributeIds.CHANT_SPEED, level);
   }

   public static void addPresenceSense(LivingEntity entity, double level) {
      addPermanentAttributeIfHigher(entity, TensuraAttributes.PRESENCE_SENSE, TensuraGlobalAttributeIds.PRESENCE_SENSE, level, Operation.ADD_VALUE);
   }

   public static void removePresenceSense(LivingEntity entity, double level) {
      removeAttributeIfCorrect(entity, TensuraAttributes.PRESENCE_SENSE, TensuraGlobalAttributeIds.PRESENCE_SENSE, level);
   }

   public static void multiplyElementalBoost(LivingEntity entity, Holder<Attribute> attribute, double level) {
      addPermanentAttributeIfHigher(entity, attribute, TensuraGlobalAttributeIds.ELEMENTAL_BOOST, level - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public static void removeElementalMultiplier(LivingEntity entity, Holder<Attribute> attribute, double level) {
      removeAttributeIfCorrect(entity, attribute, TensuraGlobalAttributeIds.ELEMENTAL_BOOST, level - 1.0);
   }

   public static void applyDominationDegradation(LivingEntity entity, Holder<Attribute> attribute, double requirementEP) {
      if (EnergyHelper.getMaxEP(entity) >= requirementEP) {
         AttributeInstance degrade = entity.getAttribute(attribute);
         if (degrade != null) {
            degrade.addOrReplacePermanentModifier(new AttributeModifier(TensuraGlobalAttributeIds.DOMINATION_DEGRADATION, 1.0, Operation.ADD_VALUE));
         }
      }
   }

   public static void removeDominationDegradation(LivingEntity entity, Holder<Attribute> attribute) {
      AttributeInstance degrade = entity.getAttribute(attribute);
      if (degrade != null) {
         degrade.removeModifier(TensuraGlobalAttributeIds.DOMINATION_DEGRADATION);
      }
   }

   public static boolean addPermanentAttributeIfHigher(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id, double level, Operation operation) {
      AttributeInstance instance = entity.getAttribute(attribute);
      if (instance != null) {
         AttributeModifier oldModifier = instance.getModifier(id);
         if (oldModifier != null && oldModifier.amount() >= level) {
            return false;
         }

         AttributeModifier modifier = new AttributeModifier(id, level, operation);
         instance.removeModifier(modifier.id());
         instance.addPermanentModifier(modifier);
         return true;
      } else {
         return false;
      }
   }

   public static void addPermanentAttribute(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id, double level, Operation operation) {
      AttributeInstance instance = entity.getAttribute(attribute);
      if (instance != null) {
         AttributeModifier modifier = new AttributeModifier(id, level, operation);
         instance.addOrReplacePermanentModifier(modifier);
      }
   }

   public static boolean removeAttributeIfCorrect(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id, double level) {
      AttributeInstance instance = entity.getAttribute(attribute);
      if (instance != null) {
         AttributeModifier oldModifier = instance.getModifier(id);
         if (oldModifier != null && oldModifier.amount() == level) {
            instance.removeModifier(id);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void removeAttribute(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id) {
      AttributeInstance instance = entity.getAttribute(attribute);
      if (instance != null) {
         instance.removeModifier(id);
      }
   }

   public static void applySleepModeAttribute(LivingEntity entity, ResourceLocation id) {
      addPermanentAttribute(entity, Attributes.ATTACK_DAMAGE, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, Attributes.ATTACK_SPEED, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, Attributes.FLYING_SPEED, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, Attributes.MOVEMENT_SPEED, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, Attributes.JUMP_STRENGTH, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, Attributes.ENTITY_INTERACTION_RANGE, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, Attributes.BLOCK_INTERACTION_RANGE, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, Attributes.FOLLOW_RANGE, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, ManasCoreAttributes.LAVA_SPEED_MULTIPLIER, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, ManasCoreAttributes.GLIDE_SPEED_MULTIPLIER, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, TensuraAttributes.AUTO_MELEE_DODGE_CHANCE, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE, id, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      addPermanentAttribute(entity, TensuraAttributes.DARK_VISION, id, 0.8, Operation.ADD_VALUE);
   }

   public static void removeSleepModeAttribute(LivingEntity entity, ResourceLocation id) {
      removeAttribute(entity, Attributes.ATTACK_DAMAGE, id);
      removeAttribute(entity, Attributes.ATTACK_SPEED, id);
      removeAttribute(entity, Attributes.FLYING_SPEED, id);
      removeAttribute(entity, Attributes.MOVEMENT_SPEED, id);
      removeAttribute(entity, Attributes.JUMP_STRENGTH, id);
      removeAttribute(entity, Attributes.ENTITY_INTERACTION_RANGE, id);
      removeAttribute(entity, Attributes.BLOCK_INTERACTION_RANGE, id);
      removeAttribute(entity, Attributes.FOLLOW_RANGE, id);
      removeAttribute(entity, ManasCoreAttributes.LAVA_SPEED_MULTIPLIER, id);
      removeAttribute(entity, ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, id);
      removeAttribute(entity, ManasCoreAttributes.GLIDE_SPEED_MULTIPLIER, id);
      removeAttribute(entity, TensuraAttributes.AUTO_MELEE_DODGE_CHANCE, id);
      removeAttribute(entity, TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE, id);
      removeAttribute(entity, TensuraAttributes.DARK_VISION, id);
   }
}
