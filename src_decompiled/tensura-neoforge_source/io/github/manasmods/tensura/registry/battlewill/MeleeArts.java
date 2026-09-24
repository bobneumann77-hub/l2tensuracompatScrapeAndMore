package io.github.manasmods.tensura.registry.battlewill;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.ability.battlewill.melee.AuraSlashArt;
import io.github.manasmods.tensura.ability.battlewill.melee.AuraSwordArt;
import io.github.manasmods.tensura.ability.battlewill.melee.EarthshatterKickArt;
import io.github.manasmods.tensura.ability.battlewill.melee.EightPetalsFlashArt;
import io.github.manasmods.tensura.ability.battlewill.melee.FivePetalsThrustArt;
import io.github.manasmods.tensura.ability.battlewill.melee.HeavySlashArt;
import io.github.manasmods.tensura.ability.battlewill.melee.OgreSwordGuillotineArt;
import io.github.manasmods.tensura.ability.battlewill.melee.RoaringLionPunchArt;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class MeleeArts {
   public static final RegistrySupplier<AuraSlashArt> AURA_SLASH = register("aura_slash", AuraSlashArt::new);
   public static final RegistrySupplier<AuraSwordArt> AURA_SWORD = register("aura_sword", AuraSwordArt::new);
   public static final RegistrySupplier<EarthshatterKickArt> EARTHSHATTER_KICK = register("earthshatter_kick", EarthshatterKickArt::new);
   public static final RegistrySupplier<HeavySlashArt> HEAVY_SLASH = register("heavy_slash", HeavySlashArt::new);
   public static final RegistrySupplier<OgreSwordGuillotineArt> OGRE_SWORD_GUILLOTINE = register("ogre_sword_guillotine", OgreSwordGuillotineArt::new);
   public static final RegistrySupplier<RoaringLionPunchArt> ROARING_LION_PUNCH = register("roaring_lion_punch", RoaringLionPunchArt::new);
   public static final RegistrySupplier<FivePetalsThrustArt> FIVE_PETALS_THRUST = register("five_petals_thrust", FivePetalsThrustArt::new);
   public static final RegistrySupplier<EightPetalsFlashArt> EIGHT_PETALS_SLASH = register("eight_petals_flash", EightPetalsFlashArt::new);

   private static <E extends ManasSkill> RegistrySupplier<E> register(String name, Supplier<E> supplier) {
      return SkillRegistry.SKILLS.register(ResourceLocation.fromNamespaceAndPath("tensura", name), supplier);
   }

   public static void init() {
   }
}
