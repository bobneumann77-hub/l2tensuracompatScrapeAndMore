package io.github.manasmods.tensura.registry.effect;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.ability.magic.aspectual.enhancement.ProtectionMagic;
import io.github.manasmods.tensura.ability.skill.extra.SenseSoundwaveSkill;
import io.github.manasmods.tensura.effect.BurdenEffect;
import io.github.manasmods.tensura.effect.ChillEffect;
import io.github.manasmods.tensura.effect.CorrosionEffect;
import io.github.manasmods.tensura.effect.CurseEffect;
import io.github.manasmods.tensura.effect.FatalPoisonEffect;
import io.github.manasmods.tensura.effect.FearEffect;
import io.github.manasmods.tensura.effect.FragilityEffect;
import io.github.manasmods.tensura.effect.FrostEffect;
import io.github.manasmods.tensura.effect.HolyDamageEffect;
import io.github.manasmods.tensura.effect.HypnosisEffect;
import io.github.manasmods.tensura.effect.IllusionBoostEffect;
import io.github.manasmods.tensura.effect.InfectionEffect;
import io.github.manasmods.tensura.effect.InsanityEffect;
import io.github.manasmods.tensura.effect.MagiculePoisonEffect;
import io.github.manasmods.tensura.effect.ParalysisEffect;
import io.github.manasmods.tensura.effect.PetrificationEffect;
import io.github.manasmods.tensura.effect.RampageEffect;
import io.github.manasmods.tensura.effect.SilenceEffect;
import io.github.manasmods.tensura.effect.WebbedEffect;
import io.github.manasmods.tensura.effect.ability.AllyBoostEffect;
import io.github.manasmods.tensura.effect.ability.AuraSwordEffect;
import io.github.manasmods.tensura.effect.ability.BatsModeEffect;
import io.github.manasmods.tensura.effect.ability.BeastTransformationEffect;
import io.github.manasmods.tensura.effect.ability.DiamondPathEffect;
import io.github.manasmods.tensura.effect.ability.DragonModeEffect;
import io.github.manasmods.tensura.effect.ability.EarthLockEffect;
import io.github.manasmods.tensura.effect.ability.EnemySearchEffect;
import io.github.manasmods.tensura.effect.ability.EngorgementEffect;
import io.github.manasmods.tensura.effect.ability.FalsifierEffect;
import io.github.manasmods.tensura.effect.ability.FateChangeEffect;
import io.github.manasmods.tensura.effect.ability.FutureSightEffect;
import io.github.manasmods.tensura.effect.ability.GuardedEffect;
import io.github.manasmods.tensura.effect.ability.HakiCoatEffect;
import io.github.manasmods.tensura.effect.ability.HealthcareEffect;
import io.github.manasmods.tensura.effect.ability.InspirationEffect;
import io.github.manasmods.tensura.effect.ability.InstantRegenerationEffect;
import io.github.manasmods.tensura.effect.ability.LustDrainEffect;
import io.github.manasmods.tensura.effect.ability.MadOgreEffect;
import io.github.manasmods.tensura.effect.ability.MagicAuraEffect;
import io.github.manasmods.tensura.effect.ability.MagicBarrierEffect;
import io.github.manasmods.tensura.effect.ability.MagicElementalEffect;
import io.github.manasmods.tensura.effect.ability.OgreBerserkerEffect;
import io.github.manasmods.tensura.effect.ability.OgreGuillotineEffect;
import io.github.manasmods.tensura.effect.ability.PhysicalBarrierEffect;
import io.github.manasmods.tensura.effect.ability.PresenceConcealmentEffect;
import io.github.manasmods.tensura.effect.ability.PresenceSenseEffect;
import io.github.manasmods.tensura.effect.ability.RestEffect;
import io.github.manasmods.tensura.effect.ability.SelfRegenerationEffect;
import io.github.manasmods.tensura.effect.ability.SeveranceBladeEffect;
import io.github.manasmods.tensura.effect.ability.ShadowStepEffect;
import io.github.manasmods.tensura.effect.ability.SpearheadEffect;
import io.github.manasmods.tensura.effect.ability.StrengthenEffect;
import io.github.manasmods.tensura.effect.ability.WarpingEffect;
import io.github.manasmods.tensura.effect.ability.WindProtectionEffect;
import io.github.manasmods.tensura.effect.debuff.BlackBurnEffect;
import io.github.manasmods.tensura.effect.debuff.ConfusionEffect;
import io.github.manasmods.tensura.effect.debuff.DisintegratingEffect;
import io.github.manasmods.tensura.effect.debuff.DrowsinessEffect;
import io.github.manasmods.tensura.effect.debuff.EnergyBlockadeEffect;
import io.github.manasmods.tensura.effect.debuff.FlashedBlindnessEffect;
import io.github.manasmods.tensura.effect.debuff.InfiniteImprisonmentEffect;
import io.github.manasmods.tensura.effect.debuff.LustEmbracementEffect;
import io.github.manasmods.tensura.effect.debuff.MagicInterferenceEffect;
import io.github.manasmods.tensura.effect.debuff.MindControlEffect;
import io.github.manasmods.tensura.effect.debuff.MovementInterferenceEffect;
import io.github.manasmods.tensura.effect.debuff.OppressionEffect;
import io.github.manasmods.tensura.effect.debuff.SleepEffect;
import io.github.manasmods.tensura.effect.debuff.SoulDrainEffect;
import io.github.manasmods.tensura.effect.debuff.SpatialBlockadeEffect;
import io.github.manasmods.tensura.effect.debuff.TrueBlindnessEffect;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class TensuraMobEffects {
   private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create("tensura", Registries.MOB_EFFECT);
   private static final Map<RegistrySupplier<MobEffect>, Holder<MobEffect>> HOLDER_CACHE = new ConcurrentHashMap<>();
   public static final RegistrySupplier<MobEffect> BURDEN = MOB_EFFECTS.register("burden", BurdenEffect::new);
   public static final RegistrySupplier<MobEffect> CHILL = MOB_EFFECTS.register("chill", ChillEffect::new);
   public static final RegistrySupplier<MobEffect> CORROSION = MOB_EFFECTS.register("corrosion", CorrosionEffect::new);
   public static final RegistrySupplier<MobEffect> CURSE = MOB_EFFECTS.register("curse", CurseEffect::new);
   public static final RegistrySupplier<MobEffect> FATAL_POISON = MOB_EFFECTS.register("fatal_poison", FatalPoisonEffect::new);
   public static final RegistrySupplier<MobEffect> FEAR = MOB_EFFECTS.register("fear", FearEffect::new);
   public static final RegistrySupplier<MobEffect> FRAGILITY = MOB_EFFECTS.register("fragility", FragilityEffect::new);
   public static final RegistrySupplier<MobEffect> FROST = MOB_EFFECTS.register("frost", FrostEffect::new);
   public static final RegistrySupplier<MobEffect> HOLY_DAMAGE = MOB_EFFECTS.register("holy_damage", HolyDamageEffect::new);
   public static final RegistrySupplier<MobEffect> HYPNOSIS = MOB_EFFECTS.register("hypnosis", HypnosisEffect::new);
   public static final RegistrySupplier<MobEffect> ILLUSION_BOOST = MOB_EFFECTS.register("illusion_boost", IllusionBoostEffect::new);
   public static final RegistrySupplier<MobEffect> INFECTION = MOB_EFFECTS.register("infection", InfectionEffect::new);
   public static final RegistrySupplier<MobEffect> INSANITY = MOB_EFFECTS.register("insanity", InsanityEffect::new);
   public static final RegistrySupplier<MobEffect> MAGICULE_POISON = MOB_EFFECTS.register("magicule_poison", MagiculePoisonEffect::new);
   public static final RegistrySupplier<MobEffect> MAGICULE_REGENERATION = MOB_EFFECTS.register(
      "magicule_regeneration",
      () -> new TensuraMobEffect(MobEffectCategory.BENEFICIAL, new Color(63, 196, 55).getRGB())
         .addAttributeModifier(
            TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER,
            ResourceLocation.fromNamespaceAndPath("tensura", "magicule_regeneration"),
            0.5,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final RegistrySupplier<MobEffect> PARALYSIS = MOB_EFFECTS.register("paralysis", ParalysisEffect::new);
   public static final RegistrySupplier<MobEffect> PETRIFICATION = MOB_EFFECTS.register("petrification", PetrificationEffect::new);
   public static final RegistrySupplier<MobEffect> SILENCE = MOB_EFFECTS.register("silence", SilenceEffect::new);
   public static final RegistrySupplier<MobEffect> SLEEP = MOB_EFFECTS.register("sleep", SleepEffect::new);
   public static final RegistrySupplier<MobEffect> RAMPAGE = MOB_EFFECTS.register("rampage", RampageEffect::new);
   public static final RegistrySupplier<MobEffect> WEBBED = MOB_EFFECTS.register("webbed", WebbedEffect::new);
   public static final RegistrySupplier<MobEffect> ALLY_BOOST = MOB_EFFECTS.register("ally_boost", AllyBoostEffect::new);
   public static final RegistrySupplier<MobEffect> AURA_SWORD = MOB_EFFECTS.register("aura_sword", AuraSwordEffect::new);
   public static final RegistrySupplier<MobEffect> AUDITORY_SENSE = MOB_EFFECTS.register(
      "auditory_sense",
      () -> new TensuraMobEffect(MobEffectCategory.BENEFICIAL, new Color(168, 187, 90).getRGB())
         .addAttributeModifier(TensuraAttributes.PRESENCE_SENSE_RADIUS, SenseSoundwaveSkill.SOUND_SENSE, 5.0, Operation.ADD_VALUE)
   );
   public static final RegistrySupplier<MobEffect> BATS_MODE = MOB_EFFECTS.register("bats_mode", BatsModeEffect::new);
   public static final RegistrySupplier<MobEffect> BEAST_TRANSFORMATION = MOB_EFFECTS.register("beast_transformation", BeastTransformationEffect::new);
   public static final RegistrySupplier<MobEffect> DIAMOND_PATH = MOB_EFFECTS.register("diamond_path", DiamondPathEffect::new);
   public static final RegistrySupplier<MobEffect> DRAGON_MODE = MOB_EFFECTS.register("dragon_mode", DragonModeEffect::new);
   public static final RegistrySupplier<MobEffect> EARTH_LOCK = MOB_EFFECTS.register("earth_lock", EarthLockEffect::new);
   public static final RegistrySupplier<MobEffect> ENEMY_SEARCH = MOB_EFFECTS.register("enemy_search", EnemySearchEffect::new);
   public static final RegistrySupplier<MobEffect> ENGORGEMENT = MOB_EFFECTS.register("engorgement", EngorgementEffect::new);
   public static final RegistrySupplier<MobEffect> FALSIFIER = MOB_EFFECTS.register("falsifier", FalsifierEffect::new);
   public static final RegistrySupplier<MobEffect> FATE_CHANGE = MOB_EFFECTS.register("fate_change", FateChangeEffect::new);
   public static final RegistrySupplier<MobEffect> FUTURE_VISION = MOB_EFFECTS.register("future_vision", FutureSightEffect::new);
   public static final RegistrySupplier<MobEffect> GUARDED = MOB_EFFECTS.register("guarded", GuardedEffect::new);
   public static final RegistrySupplier<MobEffect> HAKI_COAT = MOB_EFFECTS.register("haki_coat", HakiCoatEffect::new);
   public static final RegistrySupplier<MobEffect> HEALTHCARE = MOB_EFFECTS.register("healthcare", HealthcareEffect::new);
   public static final RegistrySupplier<MobEffect> INSPIRATION = MOB_EFFECTS.register("inspiration", InspirationEffect::new);
   public static final RegistrySupplier<MobEffect> INSTANT_REGENERATION = MOB_EFFECTS.register("instant_regeneration", InstantRegenerationEffect::new);
   public static final RegistrySupplier<MobEffect> LUST_DRAIN = MOB_EFFECTS.register("lust_drain", LustDrainEffect::new);
   public static final RegistrySupplier<MobEffect> MAD_OGRE = MOB_EFFECTS.register("mad_ogre", MadOgreEffect::new);
   public static final RegistrySupplier<MobEffect> MAGIC_AURA = MOB_EFFECTS.register("magic_aura", MagicAuraEffect::new);
   public static final RegistrySupplier<MobEffect> MAGIC_BARRIER = MOB_EFFECTS.register("magic_barrier", MagicBarrierEffect::new);
   public static final RegistrySupplier<MobEffect> MAGIC_ELEMENTAL_TRANSFORMATION = MOB_EFFECTS.register(
      "magic_elemental_transformation", MagicElementalEffect::new
   );
   public static final RegistrySupplier<MobEffect> OGRE_BERSERKER = MOB_EFFECTS.register("ogre_berserker", OgreBerserkerEffect::new);
   public static final RegistrySupplier<MobEffect> OGRE_GUILLOTINE = MOB_EFFECTS.register("ogre_guillotine", OgreGuillotineEffect::new);
   public static final RegistrySupplier<MobEffect> PHYSICAL_BARRIER = MOB_EFFECTS.register("physical_barrier", PhysicalBarrierEffect::new);
   public static final RegistrySupplier<MobEffect> PRESENCE_CONCEALMENT = MOB_EFFECTS.register("presence_concealment", PresenceConcealmentEffect::new);
   public static final RegistrySupplier<MobEffect> PRESENCE_SENSE = MOB_EFFECTS.register("presence_sense", PresenceSenseEffect::new);
   public static final RegistrySupplier<MobEffect> PROTECTION = MOB_EFFECTS.register(
      "protection",
      () -> new TensuraMobEffect(MobEffectCategory.BENEFICIAL, new Color(128, 128, 128).getRGB())
         .addAttributeModifier(Attributes.ARMOR, ProtectionMagic.PROTECTION, ProtectionMagic.CONFIG.protectionArmor, Operation.ADD_VALUE)
   );
   public static final RegistrySupplier<MobEffect> REINFORCEMENT = MOB_EFFECTS.register(
      "reinforcement", () -> new TensuraMobEffect(MobEffectCategory.BENEFICIAL, new Color(69, 68, 68).getRGB())
   );
   public static final RegistrySupplier<MobEffect> REST = MOB_EFFECTS.register("rest", RestEffect::new);
   public static final RegistrySupplier<MobEffect> SELF_REGENERATION = MOB_EFFECTS.register("self_regeneration", SelfRegenerationEffect::new);
   public static final RegistrySupplier<MobEffect> SEVERANCE_BLADE = MOB_EFFECTS.register("severance_blade", SeveranceBladeEffect::new);
   public static final RegistrySupplier<MobEffect> SHADOW_STEP = MOB_EFFECTS.register("shadow_step", ShadowStepEffect::new);
   public static final RegistrySupplier<MobEffect> SPEARHEAD = MOB_EFFECTS.register("spearhead", SpearheadEffect::new);
   public static final RegistrySupplier<MobEffect> STRENGTHEN = MOB_EFFECTS.register("strengthen", StrengthenEffect::new);
   public static final RegistrySupplier<MobEffect> WARPING = MOB_EFFECTS.register("warping", WarpingEffect::new);
   public static final RegistrySupplier<MobEffect> WIND_PROTECTION = MOB_EFFECTS.register("wind_protection", WindProtectionEffect::new);
   public static final RegistrySupplier<MobEffect> ANTI_SKILL = MOB_EFFECTS.register(
      "anti_skill", () -> new TensuraMobEffect(MobEffectCategory.HARMFUL, new Color(255, 0, 0).getRGB())
   );
   public static final RegistrySupplier<MobEffect> ANTI_MAGIC = MOB_EFFECTS.register(
      "anti_magic", () -> new TensuraMobEffect(MobEffectCategory.HARMFUL, new Color(20, 211, 125).getRGB())
   );
   public static final RegistrySupplier<MobEffect> ANTI_SHOCK = MOB_EFFECTS.register(
      "anti_shock", () -> new TensuraMobEffect(MobEffectCategory.NEUTRAL, new Color(20, 138, 211).getRGB())
   );
   public static final RegistrySupplier<MobEffect> BLACK_BURN = MOB_EFFECTS.register("black_burn", BlackBurnEffect::new);
   public static final RegistrySupplier<MobEffect> CONFUSION = MOB_EFFECTS.register("confusion", ConfusionEffect::new);
   public static final RegistrySupplier<MobEffect> DISINTEGRATING = MOB_EFFECTS.register("disintegrating", DisintegratingEffect::new);
   public static final RegistrySupplier<MobEffect> ENERGY_BLOCKADE = MOB_EFFECTS.register("energy_blockade", EnergyBlockadeEffect::new);
   public static final RegistrySupplier<MobEffect> DROWSINESS = MOB_EFFECTS.register("drowsiness", DrowsinessEffect::new);
   public static final RegistrySupplier<MobEffect> INFINITE_IMPRISONMENT = MOB_EFFECTS.register("infinite_imprisonment", InfiniteImprisonmentEffect::new);
   public static final RegistrySupplier<MobEffect> LUST_EMBRACEMENT = MOB_EFFECTS.register("lust_embracement", LustEmbracementEffect::new);
   public static final RegistrySupplier<MobEffect> MAGIC_INTERFERENCE = MOB_EFFECTS.register("magic_interference", MagicInterferenceEffect::new);
   public static final RegistrySupplier<MobEffect> MIND_CONTROL = MOB_EFFECTS.register("mind_control", MindControlEffect::new);
   public static final RegistrySupplier<MobEffect> MOVEMENT_INTERFERENCE = MOB_EFFECTS.register("movement_interference", MovementInterferenceEffect::new);
   public static final RegistrySupplier<MobEffect> OPPRESSION = MOB_EFFECTS.register("oppression", OppressionEffect::new);
   public static final RegistrySupplier<MobEffect> SOUL_DRAIN = MOB_EFFECTS.register("soul_drain", SoulDrainEffect::new);
   public static final RegistrySupplier<MobEffect> SPATIAL_BLOCKADE = MOB_EFFECTS.register("spatial_blockade", SpatialBlockadeEffect::new);
   public static final RegistrySupplier<MobEffect> FLASHED_BLINDNESS = MOB_EFFECTS.register("flashed_blindness", FlashedBlindnessEffect::new);
   public static final RegistrySupplier<MobEffect> TRUE_BLINDNESS = MOB_EFFECTS.register("true_blindness", TrueBlindnessEffect::new);

   public static Holder<MobEffect> getReference(RegistrySupplier<MobEffect> input) {
      return HOLDER_CACHE.computeIfAbsent(input, supplier -> MOB_EFFECTS.getRegistrar().getHolder(supplier.getId()));
   }

   public static void init() {
      MOB_EFFECTS.register();
   }
}
