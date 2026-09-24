package io.github.manasmods.tensura.item;

import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;

public class TensuraFoodProperties {
   public static final FoodProperties DUBIOUS_FOOD = new Builder()
      .nutrition(100)
      .saturationModifier(0.0F)
      .effect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 200, 1), 0.8F)
      .effect(new MobEffectInstance(MobEffects.POISON, 200, 2), 0.8F)
      .effect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 100), 0.8F)
      .effect(new MobEffectInstance(MobEffects.HUNGER, 200, 1), 0.8F)
      .effect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1), 0.8F)
      .effect(new MobEffectInstance(MobEffects.WEAKNESS, 200), 0.8F)
      .effect(new MobEffectInstance(MobEffects.BLINDNESS, 200), 0.8F)
      .effect(new MobEffectInstance(MobEffects.CONFUSION, 200), 0.8F)
      .effect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1), 0.8F)
      .alwaysEdible()
      .build();
   public static final FoodProperties SISSIE_FIN = new Builder().nutrition(1).saturationModifier(0.7F).build();
   public static final FoodProperties COOKED_SISSIE_FIN = new Builder().nutrition(4).saturationModifier(1.27F).build();
   public static final FoodProperties SPEAR_TORO_FIN = new Builder().nutrition(1).saturationModifier(0.7F).build();
   public static final FoodProperties COOKED_SPEAR_TORO_FIN = new Builder().nutrition(4).saturationModifier(1.27F).build();
   public static final FoodProperties BLADE_TIGER_STEAK = new Builder().nutrition(14).saturationModifier(0.67F).build();
   public static final FoodProperties CATTLEDEER_BEEF = new Builder().nutrition(5).saturationModifier(0.33F).build();
   public static final FoodProperties CATTLEDEER_STEAK = new Builder().nutrition(10).saturationModifier(0.67F).build();
   public static final FoodProperties CHILLED_SLIME = new Builder().nutrition(1).saturationModifier(2.0F).alwaysEdible().build();
   public static final FoodProperties COOKED_ARMORSAURUS_MEAT = new Builder().nutrition(12).saturationModifier(0.8F).build();
   public static final FoodProperties COOKED_CHARYBDIS_MEAT = new Builder().nutrition(20).saturationModifier(1.33F).build();
   public static final FoodProperties COOKED_GIANT_ANT_LEG = new Builder().nutrition(12).saturationModifier(0.8F).build();
   public static final FoodProperties COOKED_GIANT_BAT_MEAT = new Builder()
      .nutrition(12)
      .saturationModifier(0.8F)
      .effect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION), 900, 0), 0.03F)
      .build();
   public static final FoodProperties COOKED_KNIGHT_SPIDER_LEG = new Builder().nutrition(12).saturationModifier(0.8F).build();
   public static final FoodProperties COOKED_MEGALODON_MEAT = new Builder().nutrition(16).saturationModifier(1.07F).build();
   public static final FoodProperties COOKED_SERPENT_MEAT = new Builder().nutrition(12).saturationModifier(0.8F).build();
   public static final FoodProperties COOKED_SISSIE_MEAT = new Builder().nutrition(12).saturationModifier(0.8F).build();
   public static final FoodProperties COOKED_SPEAR_TORO_MEAT = new Builder().nutrition(12).saturationModifier(0.8F).build();
   public static final FoodProperties RAW_ARMORSAURUS_MEAT = new Builder().nutrition(5).saturationModifier(0.36F).build();
   public static final FoodProperties RAW_BLADE_TIGER_MEAT = new Builder().nutrition(7).saturationModifier(0.36F).build();
   public static final FoodProperties RAW_CHARYBDIS_MEAT = new Builder().nutrition(8).saturationModifier(0.53F).build();
   public static final FoodProperties RAW_GIANT_ANT_LEG = new Builder().nutrition(5).saturationModifier(0.36F).build();
   public static final FoodProperties RAW_GIANT_BAT_MEAT = new Builder()
      .nutrition(5)
      .saturationModifier(0.36F)
      .effect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION), 900, 0), 0.05F)
      .build();
   public static final FoodProperties RAW_KNIGHT_SPIDER_LEG = new Builder().nutrition(5).saturationModifier(0.36F).build();
   public static final FoodProperties RAW_MEGALODON_MEAT = new Builder().nutrition(6).saturationModifier(0.4F).build();
   public static final FoodProperties RAW_SERPENT_MEAT = new Builder().nutrition(5).saturationModifier(0.36F).build();
   public static final FoodProperties RAW_SISSIE_MEAT = new Builder().nutrition(5).saturationModifier(0.36F).build();
   public static final FoodProperties RAW_SPEAR_TORO_MEAT = new Builder().nutrition(5).saturationModifier(0.36F).build();
   public static final FoodProperties SILVER_APPLE = new Builder()
      .nutrition(4)
      .saturationModifier(1.2F)
      .effect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MAGICULE_REGENERATION), 1200, 1), 1.0F)
      .effect(new MobEffectInstance(MobEffects.DIG_SPEED, 2400, 0), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties ENCHANTED_SILVER_APPLE = new Builder()
      .nutrition(4)
      .saturationModifier(1.2F)
      .effect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MAGICULE_REGENERATION), 1200, 3), 1.0F)
      .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0F)
      .effect(new MobEffectInstance(MobEffects.DIG_SPEED, 2400, 1), 1.0F)
      .alwaysEdible()
      .build();
   public static final FoodProperties DAEMON_ESSENCE = new Builder()
      .alwaysEdible()
      .nutrition(0)
      .saturationModifier(0.0F)
      .effect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FEAR), 100, 0), 1.0F)
      .build();
   public static final FoodProperties DRAGON_ESSENCE = new Builder()
      .alwaysEdible()
      .nutrition(0)
      .saturationModifier(0.0F)
      .effect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MAGICULE_POISON), 100, 0), 1.0F)
      .build();
   public static final FoodProperties ELEMENTAL_ESSENCE = new Builder()
      .alwaysEdible()
      .nutrition(0)
      .saturationModifier(0.0F)
      .effect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY), 100, 0), 1.0F)
      .build();
   public static final FoodProperties ROYAL_BLOOD = new Builder()
      .alwaysEdible()
      .nutrition(0)
      .saturationModifier(0.0F)
      .effect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), 100, 0), 1.0F)
      .build();
   public static final FoodProperties ZANE_BLOOD = new Builder()
      .alwaysEdible()
      .nutrition(0)
      .saturationModifier(0.0F)
      .effect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 100, 2), 1.0F)
      .build();
}
