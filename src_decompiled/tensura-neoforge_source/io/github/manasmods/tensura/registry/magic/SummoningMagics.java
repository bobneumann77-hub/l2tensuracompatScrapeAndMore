package io.github.manasmods.tensura.registry.magic;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.ability.magic.summon.SummonBasiliskMagic;
import io.github.manasmods.tensura.ability.magic.summon.SummonDaemonMagic;
import io.github.manasmods.tensura.ability.magic.summon.SummonGreaterElementalMagic;
import io.github.manasmods.tensura.ability.magic.summon.SummonHoundDogMagic;
import io.github.manasmods.tensura.ability.magic.summon.SummonMediumElementalMagic;
import io.github.manasmods.tensura.ability.magic.summon.SummonOtherworlderMagic;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class SummoningMagics {
   public static final RegistrySupplier<SummonBasiliskMagic> SUMMON_BASILISK = register("summon_basilisk", SummonBasiliskMagic::new);
   public static final RegistrySupplier<SummonDaemonMagic> SUMMON_DAEMON = register("summon_daemon", SummonDaemonMagic::new);
   public static final RegistrySupplier<SummonHoundDogMagic> SUMMON_HOUND_DOG = register("summon_hound_dog", SummonHoundDogMagic::new);
   public static final RegistrySupplier<SummonMediumElementalMagic> SUMMON_MEDIUM_ELEMENTAL = register(
      "summon_medium_elemental", SummonMediumElementalMagic::new
   );
   public static final RegistrySupplier<SummonGreaterElementalMagic> SUMMON_GREATER_ELEMENTAL = register(
      "summon_greater_elemental", SummonGreaterElementalMagic::new
   );
   public static final RegistrySupplier<SummonOtherworlderMagic> SUMMON_OTHERWORLDER = register("summon_otherworlder", SummonOtherworlderMagic::new);

   private static <E extends ManasSkill> RegistrySupplier<E> register(String name, Supplier<E> supplier) {
      return SkillRegistry.SKILLS.register(ResourceLocation.fromNamespaceAndPath("tensura", name), supplier);
   }

   public static void init() {
   }
}
