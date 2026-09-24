package dev.xkmc.l2hostility.content.config;

import dev.xkmc.l2core.serial.config.BaseConfig;
import dev.xkmc.l2core.serial.config.CollectType;
import dev.xkmc.l2core.serial.config.ConfigCollect;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.apache.commons.lang3.mutable.MutableBoolean;

@SerialClass
public class WorldDifficultyConfig extends BaseConfig {
   @ConfigCollect(CollectType.MAP_OVERWRITE)
   @SerialField
   public final HashMap<ResourceLocation, WorldDifficultyConfig.DifficultyConfig> levelMap = new HashMap<>();
   @ConfigCollect(CollectType.MAP_OVERWRITE)
   @SerialField
   public final HashMap<ResourceLocation, WorldDifficultyConfig.DifficultyConfig> biomeMap = new HashMap<>();
   @ConfigCollect(CollectType.MAP_COLLECT)
   @SerialField
   public final HashMap<ResourceLocation, ArrayList<EntityConfig.Config>> levelDefaultTraits = new HashMap<>();
   @ConfigCollect(CollectType.MAP_COLLECT)
   @SerialField
   public final HashMap<ResourceLocation, ArrayList<EntityConfig.Config>> structureDefaultTraits = new HashMap<>();

   public static WorldDifficultyConfig.DifficultyConfig defaultLevel() {
      int base = (Integer)LHConfig.SERVER.defaultLevelBase.get();
      double var = (Double)LHConfig.SERVER.defaultLevelVar.get();
      double scale = (Double)LHConfig.SERVER.defaultLevelScale.get();
      return new WorldDifficultyConfig.DifficultyConfig(0, base, var, scale, 1.0, 1.0, 0.0);
   }

   @Nullable
   public EntityConfig.Config get(ServerLevel level, BlockPos pos, EntityType<?> type) {
      if (!(Boolean)LHConfig.SERVER.enableEntitySpecificDatapack.get()) {
         return null;
      }

      if (!(Boolean)LHConfig.SERVER.enableStructureSpecificDatapack.get()) {
         return null;
      }

      if (this.structureDefaultTraits.isEmpty()) {
         return null;
      }

      StructureManager manager = level.structureManager();
      Map<Structure, LongSet> map = manager.getAllStructuresAt(pos);
      EntityConfig.Config def = null;

      for (Entry<Structure, LongSet> ent : map.entrySet()) {
         Structure structure = ent.getKey();
         ResourceLocation key = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure);
         ArrayList<EntityConfig.Config> list = this.structureDefaultTraits.get(key);
         if (list != null) {
            MutableBoolean ans = new MutableBoolean(false);
            manager.fillStartsForStructure(structure, ent.getValue(), ex -> {
               if (ans.isFalse() && manager.structureHasPieceAt(pos, ex)) {
                  ans.setTrue();
               }
            });
            if (!ans.isFalse()) {
               for (EntityConfig.Config e : list) {
                  if (e.entities.contains(type.builtInRegistryHolder())) {
                     return e;
                  }

                  if (e.entities.size() == 0) {
                     def = e;
                  }
               }
            }
         }
      }

      return def;
   }

   @Nullable
   public EntityConfig.Config get(ResourceLocation level, EntityType<?> type) {
      if (!(Boolean)LHConfig.SERVER.enableEntitySpecificDatapack.get()) {
         return null;
      }

      ArrayList<EntityConfig.Config> list = this.levelDefaultTraits.get(level);
      if (list == null) {
         return null;
      }

      EntityConfig.Config def = null;

      for (EntityConfig.Config e : list) {
         if (e.entities.contains(type.builtInRegistryHolder())) {
            return e;
         }

         if (e.entities.size() == 0) {
            def = e;
         }
      }

      return def;
   }

   public WorldDifficultyConfig putDim(ResourceKey<Level> key, int min, int base, double var, double scale) {
      this.levelMap.put(key.location(), new WorldDifficultyConfig.DifficultyConfig(min, base, var, scale, 1.0, 1.0, 0.0));
      return this;
   }

   @SafeVarargs
   public final WorldDifficultyConfig putBiome(int min, int base, double var, double scale, ResourceKey<Biome>... keys) {
      for (ResourceKey<Biome> key : keys) {
         this.biomeMap.put(key.location(), new WorldDifficultyConfig.DifficultyConfig(min, base, var, scale, 1.0, 1.0, 0.0));
      }

      return this;
   }

   public WorldDifficultyConfig putLevelDef(ResourceKey<Level> id, EntityConfig.Config config) {
      this.levelDefaultTraits.computeIfAbsent(id.location(), l -> new ArrayList<>()).add(config);
      return this;
   }

   public WorldDifficultyConfig putStructureDef(ResourceKey<Structure> id, EntityConfig.Config config) {
      this.structureDefaultTraits.computeIfAbsent(id.location(), l -> new ArrayList<>()).add(config);
      return this;
   }

   public record DifficultyConfig(int min, int base, double variation, double scale, double apply_chance, double trait_chance, double suppression) {
   }
}
