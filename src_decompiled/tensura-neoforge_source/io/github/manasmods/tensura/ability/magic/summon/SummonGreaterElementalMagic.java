package io.github.manasmods.tensura.ability.magic.summon;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SummoningMagicConfig;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SummonGreaterElementalMagic extends SummonElementalMagic {
   public static final SummoningMagicConfig.SummonGreaterElemental CONFIG = ((SummoningMagicConfig)ConfigRegistry.getConfig(SummoningMagicConfig.class)).SummonGreaterElemental;

   public SummonGreaterElementalMagic() {
      super(SpiritualMagic.SpiritLevel.GREATER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public Pair<Double, Double> getSummonedCostPerSecond() {
      return Pair.of(0.0, CONFIG.magiculeCostSecond);
   }

   @Override
   public int getSuccessCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown;
   }

   @Override
   protected int getSpiritDuration() {
      return CONFIG.spiritDuration;
   }

   @Override
   public void addAdditionalSummonData(ManasSkillInstance instance, LivingEntity entity, TamableAnimal summon, int mode) {
      super.addAdditionalSummonData(instance, entity, summon, mode);
      AttributeInstance attack = summon.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attack != null) {
         attack.setBaseValue(attack.getBaseValue() * CONFIG.attackMultiplier);
      }

      AttributeInstance health = summon.getAttribute(Attributes.MAX_HEALTH);
      if (health != null) {
         health.setBaseValue(health.getBaseValue() * CONFIG.healthMultiplier);
      }

      summon.setHealth(summon.getMaxHealth());
   }

   @Override
   public Component getModeName(ManasSkillInstance instance, int mode) {
      return (Component)(switch (mode) {
         case 0 -> Component.translatable("entity.tensura.war_gnome");
         case 1 -> Component.translatable("entity.tensura.ifrit");
         case 2 -> Component.translatable("entity.tensura.akash");
         case 3 -> Component.translatable("entity.tensura.undine");
         case 4 -> Component.translatable("entity.tensura.sylphide");
         default -> super.getModeName(instance, mode);
      });
   }

   @Override
   public EntityType<? extends TamableAnimal> getSummonedType(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return switch (mode) {
         case 0 -> (EntityType)MonsterEntityTypes.WAR_GNOME.get();
         case 1 -> (EntityType)MonsterEntityTypes.IFRIT.get();
         case 2 -> (EntityType)MonsterEntityTypes.AKASH.get();
         case 3 -> (EntityType)MonsterEntityTypes.UNDINE.get();
         case 4 -> (EntityType)MonsterEntityTypes.SYLPHIDE.get();
         default -> null;
      };
   }
}
