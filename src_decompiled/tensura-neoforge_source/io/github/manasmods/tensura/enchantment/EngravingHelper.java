package io.github.manasmods.tensura.enchantment;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.Tensura;
import io.github.manasmods.tensura.config.EnchantmentConfig;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class EngravingHelper {
   public static EnchantmentConfig CONFIG = (EnchantmentConfig)ConfigRegistry.getConfig(EnchantmentConfig.class);
   private static final List<ResourceKey<Enchantment>> curseList = CONFIG.curseEngraving
      .stream()
      .<ResourceLocation>map(ResourceLocation::parse)
      .map(location -> ResourceKey.create(Registries.ENCHANTMENT, location))
      .toList();
   private static final List<ResourceKey<Enchantment>> commonList = CONFIG.commonEngraving
      .stream()
      .<ResourceLocation>map(ResourceLocation::parse)
      .map(location -> ResourceKey.create(Registries.ENCHANTMENT, location))
      .toList();
   private static final List<ResourceKey<Enchantment>> uncommonList = CONFIG.uncommonEngraving
      .stream()
      .<ResourceLocation>map(ResourceLocation::parse)
      .map(location -> ResourceKey.create(Registries.ENCHANTMENT, location))
      .toList();
   private static final List<ResourceKey<Enchantment>> rareList = CONFIG.rareEngraving
      .stream()
      .<ResourceLocation>map(ResourceLocation::parse)
      .map(location -> ResourceKey.create(Registries.ENCHANTMENT, location))
      .toList();
   private static final List<ResourceKey<Enchantment>> epicList = CONFIG.epicEngraving
      .stream()
      .<ResourceLocation>map(ResourceLocation::parse)
      .map(location -> ResourceKey.create(Registries.ENCHANTMENT, location))
      .toList();

   private static boolean shouldAddEngraving(ItemStack stack, int tier) {
      int currentLevel = stack.has((DataComponentType)TensuraDataComponents.RANDOM_ENGRAVING_LEVEL.get())
         ? (Integer)stack.get((DataComponentType)TensuraDataComponents.RANDOM_ENGRAVING_LEVEL.get())
         : 0;
      if (currentLevel < tier) {
         stack.set((DataComponentType)TensuraDataComponents.RANDOM_ENGRAVING_LEVEL.get(), tier);
         return true;
      } else {
         return false;
      }
   }

   public static void grantRandomEngraving(LivingEntity entity, ItemStack stack, double epAfter) {
      int tier = 0;
      if (epAfter >= CONFIG.uniqueEP && shouldAddEngraving(stack, 1)) {
         tier = 1;
      } else if (epAfter >= CONFIG.legendEP && shouldAddEngraving(stack, 2)) {
         tier = 2;
      } else if (epAfter >= CONFIG.godEP && shouldAddEngraving(stack, 3)) {
         tier = 3;
      } else {
         int boost = TensuraEnchantmentHelper.getEnchantmentLevel(entity.level(), TensuraEnchantments.TRANSCENDENCE, stack);
         if (boost > 0) {
            for (int i = 1; i <= boost; i++) {
               if (!(epAfter < CONFIG.godEP + CONFIG.transcendenceEP * i) && shouldAddEngraving(stack, 3 + i)) {
                  tier = 3 + i;
                  break;
               }
            }
         }
      }

      if (tier != 0) {
         Rarity rarity = getRarity(tier, entity.getRandom());

         List<ResourceKey<Enchantment>> list = switch (rarity) {
            case COMMON -> commonList;
            case UNCOMMON -> uncommonList;
            case RARE -> rareList;
            case EPIC -> epicList;
            default -> throw new MatchException(null, null);
         };
         Holder<Enchantment> engraving = getRandomEngraving(list, entity.level(), stack);
         if (engraving != null) {
            int baseLevel = switch (rarity) {
               case COMMON, UNCOMMON -> 1;
               case RARE -> 2;
               case EPIC -> 3;
               default -> throw new MatchException(null, null);
            };
            increaseEngraving(entity, stack, engraving, baseLevel);
            if (entity.getRandom().nextFloat() * 100.0F < CONFIG.curseChance) {
               applyCurseEngraving(entity, stack, baseLevel);
            }

            if (tier == 3 && !EnchantmentHelper.hasTag(stack, TensuraTags.Enchantments.TSUKUMOGAMI)) {
               stack.enchant(entity.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(TensuraEnchantments.TSUKUMOGAMI), 1);
            }
         }
      }
   }

   public static void applyCurseEngraving(LivingEntity entity, ItemStack stack, int baseLevel) {
      if (!stack.is(TensuraItemTags.NO_CURSE)) {
         List<Holder<Enchantment>> curses = curseList.stream()
            .map(key -> TensuraEnchantmentHelper.getEnchantment(entity.level(), (ResourceKey<Enchantment>)key))
            .toList();
         if (!curses.isEmpty()) {
            Holder<Enchantment> curse = curses.get(entity.level().getRandom().nextInt(curses.size()));
            increaseEngraving(entity, stack, curse, baseLevel);
         }
      }
   }

   public static void increaseEngraving(LivingEntity entity, ItemStack stack, Holder<Enchantment> engraving, int baseLevel) {
      if (engraving != null) {
         int oldLevel = EnchantmentHelper.getItemEnchantmentLevel(engraving, stack);
         int finalLevel;
         if (oldLevel >= ((Enchantment)engraving.value()).getMaxLevel()) {
            finalLevel = oldLevel;
         } else {
            finalLevel = Math.min(oldLevel + (oldLevel == baseLevel ? baseLevel + 1 : baseLevel), ((Enchantment)engraving.value()).getMaxLevel());
         }

         Changeable<Holder<Enchantment>> engrave = Changeable.of(engraving);
         Changeable<Integer> newLevel = Changeable.of(finalLevel);
         if (!((TensuraEntityEvents.EngraveEvent)TensuraEntityEvents.ENGRAVE_EVENT.invoker()).engrave(entity, stack, engrave, newLevel).isFalse()) {
            EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set((Holder)engrave.get(), (Integer)newLevel.get()));
         }
      }
   }

   private static Holder<Enchantment> getRandomEngraving(List<ResourceKey<Enchantment>> list, Level level, ItemStack stack) {
      List<Holder<Enchantment>> filtered = list.stream()
         .map(key -> TensuraEnchantmentHelper.getEnchantment(level, (ResourceKey<Enchantment>)key))
         .filter(enchantment -> ((Enchantment)enchantment.value()).canEnchant(stack))
         .toList();
      return filtered.isEmpty() ? null : filtered.get(level.getRandom().nextInt(filtered.size()));
   }

   private static Rarity getRarity(int tier, RandomSource random) {
      int roll = random.nextInt(100);

      List<EngravingHelper.RarityWeight> weights = switch (tier) {
         case 0, 1 -> List.of(
            new EngravingHelper.RarityWeight(Rarity.EPIC, CONFIG.uniqueEpic),
            new EngravingHelper.RarityWeight(Rarity.RARE, CONFIG.uniqueRare),
            new EngravingHelper.RarityWeight(Rarity.UNCOMMON, CONFIG.uniqueUncommon),
            new EngravingHelper.RarityWeight(Rarity.COMMON, 100)
         );
         case 2 -> List.of(
            new EngravingHelper.RarityWeight(Rarity.EPIC, CONFIG.legendEpic),
            new EngravingHelper.RarityWeight(Rarity.RARE, CONFIG.legendRare),
            new EngravingHelper.RarityWeight(Rarity.UNCOMMON, 100)
         );
         default -> List.of(new EngravingHelper.RarityWeight(Rarity.EPIC, CONFIG.godEpic), new EngravingHelper.RarityWeight(Rarity.RARE, 100));
      };
      return getRandomRarity(roll, weights);
   }

   private static Rarity getRandomRarity(int roll, List<EngravingHelper.RarityWeight> weights) {
      double cumulative = 0.0;

      for (EngravingHelper.RarityWeight entry : weights) {
         cumulative += entry.weight();
         if (roll < cumulative) {
            return entry.rarity();
         }
      }

      Tensura.LOG.error("Rarity weights do not sum to 100: roll=" + roll);
      return Rarity.COMMON;
   }

   private record RarityWeight(Rarity rarity, int weight) {
   }
}
