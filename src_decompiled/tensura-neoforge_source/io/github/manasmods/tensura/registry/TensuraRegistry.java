package io.github.manasmods.tensura.registry;

import com.google.common.collect.Sets;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.Registrar;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.item.misc.BattlewillManualItem;
import io.github.manasmods.tensura.item.misc.MagicTomeItem;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.battlewill.MeleeArts;
import io.github.manasmods.tensura.registry.battlewill.ProjectileArts;
import io.github.manasmods.tensura.registry.battlewill.UtilityArts;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.block.TensuraPoiTypes;
import io.github.manasmods.tensura.registry.data.TensuraCritereonPredicates;
import io.github.manasmods.tensura.registry.data.TensuraLootFunctions;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.effect.TensuraPotions;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraSmithingSchematicItems;
import io.github.manasmods.tensura.registry.item.TensuraSpawnEggs;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraArmorMaterials;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.magic.SummoningMagics;
import io.github.manasmods.tensura.registry.menu.TensuraMenuTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.registry.world.TensuraFeatures;
import io.github.manasmods.tensura.registry.world.TensuraFoliagePlacers;
import io.github.manasmods.tensura.registry.world.TensuraGameEvents;
import io.github.manasmods.tensura.registry.world.TensuraStructureTypes;
import io.github.manasmods.tensura.registry.world.TensuraTrunkPlacers;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class TensuraRegistry {
   public static void init() {
      TensuraAttributes.init();
      MeleeArts.init();
      ProjectileArts.init();
      UtilityArts.init();
      AspectualMagics.init();
      SpiritualMagics.init();
      SummoningMagics.init();
      CommonSkills.init();
      ExtraSkills.init();
      IntrinsicSkills.init();
      ResistanceSkills.init();
      UniqueSkills.init();
      TensuraSoundEvents.init();
      TensuraParticleTypes.init();
      TensuraMobEffects.init();
      TensuraPotions.init();
      TensuraDataComponents.init();
      TensuraEnchantmentEffectComponents.init();
      TensuraCreativeTabs.init();
      TensuraBlocks.init();
      TensuraBlockEntities.init();
      TensuraPoiTypes.init();
      TensuraEntityTypes.init();
      TensuraSpawnEggs.init();
      TensuraConsumableItems.init();
      TensuraMobDropItems.init();
      TensuraMaterialItems.init();
      TensuraToolItems.init();
      TensuraSmithingSchematicItems.init();
      TensuraRecipes.init();
      TensuraArmorMaterials.init();
      TensuraArmorItems.init();
      TensuraStats.init();
      TensuraRaces.init();
      TensuraMenuTypes.init();
      TensuraFoliagePlacers.init();
      TensuraTrunkPlacers.init();
      TensuraFeatures.init();
      TensuraStructureTypes.init();
      TensuraCritereonPredicates.init();
      TensuraCriteriaTriggers.init();
      TensuraLootFunctions.init();
      TensuraGameEvents.init();
      CreativeTabRegistry.modify(
         TensuraCreativeTabs.LEARNABLE,
         (flags, output, canUseGamemasterBlocks) -> {
            Registrar<ManasSkill> registrar = SkillAPI.getSkillRegistry();
            List<ManasSkill> magics = new ArrayList<>();

            for (Entry<ResourceKey<ManasSkill>, ManasSkill> entry : registrar.entrySet()) {
               if (registrar.delegate(entry.getValue().getRegistryName()).is(TensuraSkillTags.FOUND_IN_TOME)) {
                  magics.add(entry.getValue());
               }
            }

            magics.sort(Comparator.comparingInt(registrar::getRawId));
            output.acceptAllBefore((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get(), magics.stream().map(MagicTomeItem::createForMagic).toList());
            output.acceptBefore((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get(), (ItemLike)TensuraMaterialItems.MAGIC_TOME.get());
            output.acceptBefore((ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get(), (ItemLike)TensuraMaterialItems.UNBOUND_TOME.get());
            List<ManasSkill> battlewills = new ArrayList<>();

            for (Entry<ResourceKey<ManasSkill>, ManasSkill> entry : registrar.entrySet()) {
               if (registrar.delegate(entry.getValue().getRegistryName()).is(TensuraSkillTags.BATTLEWILL)) {
                  battlewills.add(entry.getValue());
               }
            }

            battlewills.sort(Comparator.comparing(ManasSkill::getRegistryName).reversed());
            output.acceptAllBefore(
               (ItemLike)TensuraMaterialItems.BATTLEWILL_MANUAL.get(), battlewills.stream().map(BattlewillManualItem::createForBattlewill).toList()
            );
         }
      );
      LifecycleEvent.SETUP.register((Runnable)() -> addBedToPOI((BedBlock)TensuraBlocks.THATCH_BED.get()));
   }

   public static void addBedToPOI(BedBlock bedBlock) {
      Set<BlockState> blockStates = bedBlock.getStateDefinition()
         .getPossibleStates()
         .stream()
         .filter(blockState -> blockState.getValue(BedBlock.PART) == BedPart.HEAD)
         .collect(Collectors.toSet());
      Holder<PoiType> holder = (Holder<PoiType>)BuiltInRegistries.POINT_OF_INTEREST_TYPE.getHolder(PoiTypes.HOME).orElseThrow();
      PoiTypes.BEDS = Sets.newHashSet(PoiTypes.BEDS);
      PoiTypes.registerBlockStates(holder, blockStates);
      PoiTypes.BEDS.addAll(blockStates);
   }
}
