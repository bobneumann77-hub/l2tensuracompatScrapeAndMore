package io.github.manasmods.tensura.registry.effect;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public class TensuraPotions {
   public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create("tensura", Registries.POTION);
   public static final RegistrySupplier<Potion> CHILL = POTIONS.register(
      "chill", () -> new Potion("chill", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CHILL), 900)})
   );
   public static final RegistrySupplier<Potion> LONG_CHILL = POTIONS.register(
      "long_chill", () -> new Potion("chill", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CHILL), 1800)})
   );
   public static final RegistrySupplier<Potion> STRONG_CHILL = POTIONS.register(
      "strong_chill",
      () -> new Potion("chill", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CHILL), 432, 1)})
   );
   public static final RegistrySupplier<Potion> CORROSION = POTIONS.register(
      "corrosion",
      () -> new Potion("corrosion", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), 900)})
   );
   public static final RegistrySupplier<Potion> LONG_CORROSION = POTIONS.register(
      "long_corrosion",
      () -> new Potion("corrosion", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), 1800)})
   );
   public static final RegistrySupplier<Potion> STRONG_CORROSION = POTIONS.register(
      "strong_corrosion",
      () -> new Potion("corrosion", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), 432, 1)})
   );
   public static final RegistrySupplier<Potion> FATAL_POISON = POTIONS.register(
      "fatal_poison",
      () -> new Potion("fatal_poison", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 900)})
   );
   public static final RegistrySupplier<Potion> LONG_FATAL_POISON = POTIONS.register(
      "long_fatal_poison",
      () -> new Potion("fatal_poison", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 1800)})
   );
   public static final RegistrySupplier<Potion> STRONG_FATAL_POISON = POTIONS.register(
      "strong_fatal_poison",
      () -> new Potion("fatal_poison", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 432, 1)})
   );
   public static final RegistrySupplier<Potion> FRAGILITY = POTIONS.register(
      "fragility",
      () -> new Potion("fragility", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 900)})
   );
   public static final RegistrySupplier<Potion> LONG_FRAGILITY = POTIONS.register(
      "long_fragility",
      () -> new Potion("fragility", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 1800)})
   );
   public static final RegistrySupplier<Potion> STRONG_FRAGILITY = POTIONS.register(
      "strong_fragility",
      () -> new Potion("fragility", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 432, 1)})
   );
   public static final RegistrySupplier<Potion> GLOWING = POTIONS.register(
      "glowing", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.GLOWING, 3600)})
   );
   public static final RegistrySupplier<Potion> LONG_GLOWING = POTIONS.register(
      "long_glowing", () -> new Potion("glowing", new MobEffectInstance[]{new MobEffectInstance(MobEffects.GLOWING, 9600)})
   );
   public static final RegistrySupplier<Potion> HYPNOSIS = POTIONS.register(
      "hypnosis", () -> new Potion("hypnosis", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS), 900)})
   );
   public static final RegistrySupplier<Potion> LONG_HYPNOSIS = POTIONS.register(
      "long_hypnosis",
      () -> new Potion("hypnosis", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS), 1800)})
   );
   public static final RegistrySupplier<Potion> STRONG_HYPNOSIS = POTIONS.register(
      "strong_hypnosis",
      () -> new Potion("hypnosis", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS), 432, 1)})
   );
   public static final RegistrySupplier<Potion> HYPNOTIC_EFFICIENCY = POTIONS.register(
      "hypnotic_efficiency",
      () -> new Potion(
         "hypnotic_efficiency", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ILLUSION_BOOST), 900)}
      )
   );
   public static final RegistrySupplier<Potion> LONG_HYPNOTIC_EFFICIENCY = POTIONS.register(
      "long_hypnotic_efficiency",
      () -> new Potion(
         "hypnotic_efficiency", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ILLUSION_BOOST), 1800)}
      )
   );
   public static final RegistrySupplier<Potion> STRONG_HYPNOTIC_EFFICIENCY = POTIONS.register(
      "strong_hypnotic_efficiency",
      () -> new Potion(
         "hypnotic_efficiency", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ILLUSION_BOOST), 432, 1)}
      )
   );
   public static final RegistrySupplier<Potion> NIGHT_OWL = POTIONS.register(
      "night_owl",
      () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(MobEffects.INVISIBILITY, 4800), new MobEffectInstance(MobEffects.NIGHT_VISION, 4800)})
   );
   public static final RegistrySupplier<Potion> LONG_NIGHT_OWL = POTIONS.register(
      "long_night_owl",
      () -> new Potion(
         "night_owl", new MobEffectInstance[]{new MobEffectInstance(MobEffects.INVISIBILITY, 12000), new MobEffectInstance(MobEffects.NIGHT_VISION, 12000)}
      )
   );
   public static final RegistrySupplier<Potion> PARALYSIS = POTIONS.register(
      "paralysis",
      () -> new Potion("paralysis", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 900, 1)})
   );
   public static final RegistrySupplier<Potion> LONG_PARALYSIS = POTIONS.register(
      "long_paralysis",
      () -> new Potion("paralysis", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 1800, 1)})
   );
   public static final RegistrySupplier<Potion> STRONG_PARALYSIS = POTIONS.register(
      "strong_paralysis",
      () -> new Potion("paralysis", new MobEffectInstance[]{new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 432, 2)})
   );

   public static Holder<Potion> getReference(RegistrySupplier<Potion> input) {
      return POTIONS.getRegistrar().getHolder(input.getId());
   }

   public static void init() {
      POTIONS.register();
   }
}
