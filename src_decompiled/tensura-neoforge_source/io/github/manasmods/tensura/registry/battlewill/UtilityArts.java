package io.github.manasmods.tensura.registry.battlewill;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.ability.battlewill.utility.AirFlightArt;
import io.github.manasmods.tensura.ability.battlewill.utility.AuraShieldArt;
import io.github.manasmods.tensura.ability.battlewill.utility.BattlewillArt;
import io.github.manasmods.tensura.ability.battlewill.utility.DiamondPathArt;
import io.github.manasmods.tensura.ability.battlewill.utility.FormhideArt;
import io.github.manasmods.tensura.ability.battlewill.utility.HazeArt;
import io.github.manasmods.tensura.ability.battlewill.utility.InstantMoveArt;
import io.github.manasmods.tensura.ability.battlewill.utility.ViolentBreakArt;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class UtilityArts {
   public static final RegistrySupplier<AirFlightArt> AIR_FLIGHT = register("air_flight", AirFlightArt::new);
   public static final RegistrySupplier<AuraShieldArt> AURA_SHIELD = register("aura_shield", AuraShieldArt::new);
   public static final RegistrySupplier<BattlewillArt> BATTLEWILL = register("battlewill", BattlewillArt::new);
   public static final RegistrySupplier<DiamondPathArt> DIAMOND_PATH = register("diamond_path", DiamondPathArt::new);
   public static final RegistrySupplier<FormhideArt> FORMHIDE = register("formhide", FormhideArt::new);
   public static final RegistrySupplier<HazeArt> HAZE = register("haze", HazeArt::new);
   public static final RegistrySupplier<InstantMoveArt> INSTANT_MOVE = register("instant_move", InstantMoveArt::new);
   public static final RegistrySupplier<ViolentBreakArt> VIOLENT_BREAK = register("violent_break", ViolentBreakArt::new);

   private static <E extends ManasSkill> RegistrySupplier<E> register(String name, Supplier<E> supplier) {
      return SkillRegistry.SKILLS.register(ResourceLocation.fromNamespaceAndPath("tensura", name), supplier);
   }

   public static void init() {
   }
}
