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

public class SummonMediumElementalMagic extends SummonElementalMagic {
   public static final SummoningMagicConfig.SummonMediumElemental CONFIG = ((SummoningMagicConfig)ConfigRegistry.getConfig(SummoningMagicConfig.class)).SummonMediumElemental;

   public SummonMediumElementalMagic() {
      super(SpiritualMagic.SpiritLevel.MEDIUM);
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
   public Component getModeName(ManasSkillInstance instance, int mode) {
      return (Component)(switch (mode) {
         case 0 -> Component.translatable("entity.tensura.beast_gnome");
         case 1 -> Component.translatable("entity.tensura.salamander");
         case 2 -> Component.translatable("entity.tensura.winged_cat");
         case 3 -> Component.translatable("entity.tensura.aqua_frog");
         case 4 -> Component.translatable("entity.tensura.feathered_serpent");
         default -> super.getModeName(instance, mode);
      });
   }

   @Override
   public EntityType<? extends TamableAnimal> getSummonedType(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return switch (mode) {
         case 0 -> (EntityType)MonsterEntityTypes.BEAST_GNOME.get();
         case 1 -> (EntityType)MonsterEntityTypes.SALAMANDER.get();
         case 2 -> (EntityType)MonsterEntityTypes.WINGED_CAT.get();
         case 3 -> (EntityType)MonsterEntityTypes.AQUA_FROG.get();
         case 4 -> (EntityType)MonsterEntityTypes.FEATHERED_SERPENT.get();
         default -> null;
      };
   }
}
