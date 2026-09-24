package io.github.manasmods.tensura.registry.skill;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.common.CoercionSkill;
import io.github.manasmods.tensura.ability.skill.common.CorrosionSkill;
import io.github.manasmods.tensura.ability.skill.common.FarsightSkill;
import io.github.manasmods.tensura.ability.skill.common.GravityFieldSkill;
import io.github.manasmods.tensura.ability.skill.common.GravityFlightSkill;
import io.github.manasmods.tensura.ability.skill.common.HydraulicPropulsionSkill;
import io.github.manasmods.tensura.ability.skill.common.ParalysisSkill;
import io.github.manasmods.tensura.ability.skill.common.PoisonSkill;
import io.github.manasmods.tensura.ability.skill.common.RangedBarrierSkill;
import io.github.manasmods.tensura.ability.skill.common.SelfRegenerationSkill;
import io.github.manasmods.tensura.ability.skill.common.StrengthSkill;
import io.github.manasmods.tensura.ability.skill.common.TelepathySkill;
import io.github.manasmods.tensura.ability.skill.common.ThoughtCommunicationSkill;
import io.github.manasmods.tensura.ability.skill.common.VoiceCannonSkill;
import io.github.manasmods.tensura.ability.skill.common.WaterBladeSkill;
import io.github.manasmods.tensura.ability.skill.common.WaterCurrentControlSkill;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class CommonSkills {
   public static final RegistrySupplier<CoercionSkill> COERCION = register("coercion", CoercionSkill::new);
   public static final RegistrySupplier<CorrosionSkill> CORROSION = register("corrosion", CorrosionSkill::new);
   public static final RegistrySupplier<FarsightSkill> FARSIGHT = register("farsight", FarsightSkill::new);
   public static final RegistrySupplier<GravityFieldSkill> GRAVITY_FIELD = register("gravity_field", GravityFieldSkill::new);
   public static final RegistrySupplier<GravityFlightSkill> GRAVITY_FLIGHT = register("gravity_flight", GravityFlightSkill::new);
   public static final RegistrySupplier<HydraulicPropulsionSkill> HYDRAULIC_PROPULSION = register("hydraulic_propulsion", HydraulicPropulsionSkill::new);
   public static final RegistrySupplier<ParalysisSkill> PARALYSIS = register("paralysis", ParalysisSkill::new);
   public static final RegistrySupplier<PoisonSkill> POISON = register("poison", PoisonSkill::new);
   public static final RegistrySupplier<RangedBarrierSkill> RANGED_BARRIER = register("ranged_barrier", RangedBarrierSkill::new);
   public static final RegistrySupplier<SelfRegenerationSkill> SELF_REGENERATION = register("self_regeneration", SelfRegenerationSkill::new);
   public static final RegistrySupplier<StrengthSkill> STRENGTH = register("strength", StrengthSkill::new);
   public static final RegistrySupplier<Skill> TELEPATHY = register("telepathy", TelepathySkill::new);
   public static final RegistrySupplier<ThoughtCommunicationSkill> THOUGHT_COMMUNICATION = register("thought_communication", ThoughtCommunicationSkill::new);
   public static final RegistrySupplier<VoiceCannonSkill> VOICE_CANNON = register("voice_cannon", VoiceCannonSkill::new);
   public static final RegistrySupplier<WaterBladeSkill> WATER_BLADE = register("water_blade", WaterBladeSkill::new);
   public static final RegistrySupplier<WaterCurrentControlSkill> WATER_CURRENT_CONTROL = register("water_current_control", WaterCurrentControlSkill::new);

   private static <E extends ManasSkill> RegistrySupplier<E> register(String name, Supplier<E> supplier) {
      return SkillRegistry.SKILLS.register(ResourceLocation.fromNamespaceAndPath("tensura", name), supplier);
   }

   public static void init() {
   }
}
