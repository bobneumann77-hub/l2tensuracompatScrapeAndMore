package io.github.manasmods.tensura.registry.battlewill;

import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.impl.SkillRegistry;
import io.github.manasmods.tensura.ability.battlewill.projectile.DarkEightPalmsArt;
import io.github.manasmods.tensura.ability.battlewill.projectile.DeathMarchDanceArt;
import io.github.manasmods.tensura.ability.battlewill.projectile.ElephantStampedeArt;
import io.github.manasmods.tensura.ability.battlewill.projectile.MagicBulletArt;
import io.github.manasmods.tensura.ability.battlewill.projectile.MaximumMagicBulletArt;
import io.github.manasmods.tensura.ability.battlewill.projectile.OgreFlameArt;
import io.github.manasmods.tensura.ability.battlewill.projectile.OgreSwordCannonArt;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class ProjectileArts {
   public static final RegistrySupplier<DarkEightPalmsArt> DARK_EIGHT_PALMS = register("dark_eight_palms", DarkEightPalmsArt::new);
   public static final RegistrySupplier<DeathMarchDanceArt> DEATH_MARCH_DANCE = register("death_march_dance", DeathMarchDanceArt::new);
   public static final RegistrySupplier<ElephantStampedeArt> ELEPHANT_STAMPEDE = register("elephant_stampede", ElephantStampedeArt::new);
   public static final RegistrySupplier<MagicBulletArt> MAGIC_BULLET = register("magic_bullet", MagicBulletArt::new);
   public static final RegistrySupplier<MaximumMagicBulletArt> MAXIMUM_MAGIC_BULLET = register("maximum_magic_bullet", MaximumMagicBulletArt::new);
   public static final RegistrySupplier<OgreFlameArt> OGRE_FLAME = register("ogre_flame", OgreFlameArt::new);
   public static final RegistrySupplier<OgreSwordCannonArt> OGRE_SWORD_CANNON = register("ogre_sword_cannon", OgreSwordCannonArt::new);

   private static <E extends ManasSkill> RegistrySupplier<E> register(String name, Supplier<E> supplier) {
      return SkillRegistry.SKILLS.register(ResourceLocation.fromNamespaceAndPath("tensura", name), supplier);
   }

   public static void init() {
   }
}
