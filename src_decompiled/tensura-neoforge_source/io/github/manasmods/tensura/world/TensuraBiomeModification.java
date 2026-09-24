package io.github.manasmods.tensura.world;

import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;

public class TensuraBiomeModification {
   public static final List<TensuraBiomeModification.MobSpawningTemplate> MOB_SPAWNING = new ArrayList<>();

   public static void initMobSpawning() {
      addSpawn(TensuraBiomeTags.EntitySpawn.OTHERWORLDER, (EntityType<?>)HumanEntityTypes.KYOYA_TACHIBANA.get(), 5, 1, 1, 4.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.AQUA_FROG, (EntityType<?>)MonsterEntityTypes.AQUA_FROG.get(), 5, 1, 1, 2.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.ARCH_DAEMON, (EntityType<?>)MonsterEntityTypes.ARCH_DAEMON.get(), 1, 1, 1, 4.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.ARMORSAURUS, (EntityType<?>)MonsterEntityTypes.ARMORSAURUS.get(), 60, 1, 1, 1.32);
      addSpawn(TensuraBiomeTags.EntitySpawn.ARMY_WASP, (EntityType<?>)MonsterEntityTypes.ARMY_WASP.get(), 40, 1, 2, 2.18);
      addSpawn(TensuraBiomeTags.EntitySpawn.BARGHEST, (EntityType<?>)MonsterEntityTypes.BARGHEST.get(), 80, 2, 4, 0.8);
      addSpawn(TensuraBiomeTags.EntitySpawn.BASILISK, (EntityType<?>)MonsterEntityTypes.BASILISK.get(), 30, 1, 2, 1.14);
      addSpawn(TensuraBiomeTags.EntitySpawn.BEAST_GNOME, (EntityType<?>)MonsterEntityTypes.BEAST_GNOME.get(), 5, 1, 1, 2.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.BLACK_SPIDER, (EntityType<?>)MonsterEntityTypes.BLACK_SPIDER.get(), 80, 1, 2, 1.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.BLADE_TIGER, (EntityType<?>)MonsterEntityTypes.BLADE_TIGER.get(), 60, 1, 1, 1.5);
      addSpawn(TensuraBiomeTags.EntitySpawn.CATTLEDEER, (EntityType<?>)MonsterEntityTypes.CATTLEDEER.get(), 82, 1, 4, 1.3);
      addSpawn(TensuraBiomeTags.EntitySpawn.DIREWOLF, (EntityType<?>)MonsterEntityTypes.DIREWOLF.get(), 10, 3, 7, 1.6);
      addSpawn(TensuraBiomeTags.EntitySpawn.DRAGON_PEACOCK, (EntityType<?>)MonsterEntityTypes.DRAGON_PEACOCK.get(), 80, 1, 3, 1.2);
      addSpawn(TensuraBiomeTags.EntitySpawn.EVIL_CENTIPEDE, (EntityType<?>)MonsterEntityTypes.EVIL_CENTIPEDE.get(), 40, 1, 1, 1.14);
      addSpawn(TensuraBiomeTags.EntitySpawn.FEATHERED_SERPENT, (EntityType<?>)MonsterEntityTypes.FEATHERED_SERPENT.get(), 5, 1, 1, 2.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.GIANT_ANT, (EntityType<?>)MonsterEntityTypes.GIANT_ANT.get(), 80, 1, 4, 1.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.GIANT_BAT, (EntityType<?>)MonsterEntityTypes.GIANT_BAT.get(), 80, 1, 2, 1.2);
      addSpawn(TensuraBiomeTags.EntitySpawn.GIANT_BEAR, (EntityType<?>)MonsterEntityTypes.GIANT_BEAR.get(), 10, 1, 1, 1.8);
      addSpawn(TensuraBiomeTags.EntitySpawn.GIANT_COD, (EntityType<?>)MonsterEntityTypes.GIANT_COD.get(), 2, 1, 1, 3.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.GIANT_SALMON, (EntityType<?>)MonsterEntityTypes.GIANT_SALMON.get(), 2, 1, 1, 3.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.GREATER_DAEMON, (EntityType<?>)MonsterEntityTypes.GREATER_DAEMON.get(), 30, 1, 1, 4.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.HELL_CATERPILLAR, (EntityType<?>)MonsterEntityTypes.HELL_CATERPILLAR.get(), 20, 1, 3, 1.2);
      addSpawn(TensuraBiomeTags.EntitySpawn.HELL_MOTH, (EntityType<?>)MonsterEntityTypes.HELL_MOTH.get(), 10, 1, 2, 2.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.HORNED_BEAR, (EntityType<?>)MonsterEntityTypes.HORNED_BEAR.get(), 20, 1, 1, 1.8);
      addSpawn(TensuraBiomeTags.EntitySpawn.HORNED_RABBIT, (EntityType<?>)MonsterEntityTypes.HORNED_RABBIT.get(), 20, 1, 2, 1.65);
      addSpawn(TensuraBiomeTags.EntitySpawn.HOUND_DOG, (EntityType<?>)MonsterEntityTypes.HOUND_DOG.get(), 80, 1, 3, 1.2);
      addSpawn(TensuraBiomeTags.EntitySpawn.HOVER_LIZARD, (EntityType<?>)MonsterEntityTypes.HOVER_LIZARD.get(), 8, 1, 3, 1.75);
      addSpawn(TensuraBiomeTags.EntitySpawn.KNIGHT_SPIDER, (EntityType<?>)MonsterEntityTypes.KNIGHT_SPIDER.get(), 80, 1, 1, 1.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.LANDFISH, (EntityType<?>)MonsterEntityTypes.LANDFISH.get(), 1, 1, 3, 4.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.LEECH_LIZARD, (EntityType<?>)MonsterEntityTypes.LEECH_LIZARD.get(), 80, 2, 5, 0.8);
      addSpawn(TensuraBiomeTags.EntitySpawn.LESSER_DAEMON, (EntityType<?>)MonsterEntityTypes.LESSER_DAEMON.get(), 60, 1, 1, 4.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.ONE_EYED_OWL, (EntityType<?>)MonsterEntityTypes.ONE_EYED_OWL.get(), 50, 1, 2, 1.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.ORC, (EntityType<?>)MonsterEntityTypes.ORC.get(), 80, 1, 4, 0.8);
      addSpawn(TensuraBiomeTags.EntitySpawn.PEGASUS, (EntityType<?>)MonsterEntityTypes.PEGASUS.get(), 7, 1, 3, 2.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.PHANTASPORE, (EntityType<?>)MonsterEntityTypes.PHANTASPORE.get(), 5, 1, 1, 2.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.SALAMANDER, (EntityType<?>)MonsterEntityTypes.SALAMANDER.get(), 5, 1, 1, 2.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.SISSIE, (EntityType<?>)MonsterEntityTypes.SISSIE.get(), 1, 1, 1, 4.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.SLIME, (EntityType<?>)MonsterEntityTypes.SLIME.get(), 80, 1, 5, 0.5);
      addSpawn(TensuraBiomeTags.EntitySpawn.SPEAR_TORO, (EntityType<?>)MonsterEntityTypes.SPEAR_TORO.get(), 2, 1, 1, 4.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.UNICORN, (EntityType<?>)MonsterEntityTypes.UNICORN.get(), 7, 1, 3, 2.0);
      addSpawn(TensuraBiomeTags.EntitySpawn.TEMPEST_SERPENT, (EntityType<?>)MonsterEntityTypes.TEMPEST_SERPENT.get(), 40, 1, 1, 1.5);
      addSpawn(TensuraBiomeTags.EntitySpawn.WINGED_CAT, (EntityType<?>)MonsterEntityTypes.WINGED_CAT.get(), 5, 1, 1, 2.0);
   }

   private static void addSpawn(TagKey<Biome> biome, EntityType<?> type, int weight, int min, int max, double charge) {
      addSpawn(biome, type, weight, min, max, 4.0, charge);
   }

   private static void addSpawn(TagKey<Biome> biome, EntityType<?> type, int weight, int min, int max, double budget, double charge) {
      MOB_SPAWNING.add(new TensuraBiomeModification.MobSpawningTemplate(type, biome, weight, min, max, budget, charge));
   }

   public static class MobSpawningTemplate {
      private final EntityType<?> type;
      private final TagKey<Biome> biome;
      private final int weight;
      private final int minPack;
      private final int maxPack;
      private final double budget;
      private final double charge;

      @Generated
      public EntityType<?> getType() {
         return this.type;
      }

      @Generated
      public TagKey<Biome> getBiome() {
         return this.biome;
      }

      @Generated
      public int getWeight() {
         return this.weight;
      }

      @Generated
      public int getMinPack() {
         return this.minPack;
      }

      @Generated
      public int getMaxPack() {
         return this.maxPack;
      }

      @Generated
      public double getBudget() {
         return this.budget;
      }

      @Generated
      public double getCharge() {
         return this.charge;
      }

      @Generated
      public MobSpawningTemplate(EntityType<?> type, TagKey<Biome> biome, int weight, int minPack, int maxPack, double budget, double charge) {
         this.type = type;
         this.biome = biome;
         this.weight = weight;
         this.minPack = minPack;
         this.maxPack = maxPack;
         this.budget = budget;
         this.charge = charge;
      }
   }
}
