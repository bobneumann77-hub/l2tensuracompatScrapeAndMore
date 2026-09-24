package dev.xkmc.l2hostility.compat.data;

import com.cerbon.bosses_of_mass_destruction.entity.BMDEntities;
import com.cerbon.cerbons_api.api.registry.RegistryEntry;
import dev.xkmc.l2core.serial.config.ConfigDataProvider.Collector;
import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class BoMDData {
   public static void genConfig(Collector collector) {
      addEntity(
         collector,
         200,
         50,
         BMDEntities.LICH,
         List.of(
            EntityConfig.trait((MobTrait)LHTraits.TANK.get(), 2, 3),
            EntityConfig.trait((MobTrait)LHTraits.REPRINT.get(), 1, 1),
            EntityConfig.trait((MobTrait)LHTraits.FREEZING.get(), 2, 3)
         ),
         List.of()
      );
      addEntity(
         collector,
         200,
         50,
         BMDEntities.OBSIDILITH,
         List.of(
            EntityConfig.trait((MobTrait)LHTraits.TANK.get(), 2, 3),
            EntityConfig.trait((MobTrait)LHTraits.REFLECT.get(), 2, 3),
            EntityConfig.trait((MobTrait)LHTraits.WEAKNESS.get(), 5, 5)
         ),
         List.of()
      );
      addEntity(
         collector,
         200,
         50,
         BMDEntities.GAUNTLET,
         List.of(
            EntityConfig.trait((MobTrait)LHTraits.TANK.get(), 2, 3),
            EntityConfig.trait((MobTrait)LHTraits.REFLECT.get(), 2, 3),
            EntityConfig.trait((MobTrait)LHTraits.SOUL_BURNER.get(), 2, 3)
         ),
         List.of()
      );
      addEntity(
         collector,
         200,
         50,
         BMDEntities.VOID_BLOSSOM,
         List.of(
            EntityConfig.trait((MobTrait)LHTraits.TANK.get(), 2, 3),
            EntityConfig.trait((MobTrait)LHTraits.REGEN.get(), 5, 5),
            EntityConfig.trait((MobTrait)LHTraits.ADAPTIVE.get(), 2, 3)
         ),
         List.of()
      );
   }

   public static <T extends Entity> void addEntity(Collector collector, int min, int base, RegistryEntry<EntityType<T>> obj, EntityConfig.TraitBase... traits) {
      collector.add(L2Hostility.ENTITY, obj.getId(), new EntityConfig().putEntity(min, base, 0.0, 0.0, List.of((EntityType<?>)obj.get()), List.of(traits)));
   }

   public static <T extends Entity> void addEntity(
      Collector collector, int min, int base, RegistryEntry<EntityType<T>> obj, List<EntityConfig.TraitBase> traits, List<EntityConfig.ItemPool> items
   ) {
      collector.add(L2Hostility.ENTITY, obj.getId(), new EntityConfig().putEntityAndItem(min, base, 0.0, 0.0, List.of((EntityType<?>)obj.get()), traits, items));
   }
}
