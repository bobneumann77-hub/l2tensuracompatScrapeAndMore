package dev.xkmc.l2hostility.content.logic;

import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2hostility.content.config.TraitExclusion;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;

public class TraitGenerator {
   private final LivingEntity entity;
   private final int mobLevel;
   private final int maxTrait;
   private final MobDifficultyCollector ins;
   private final HashMap<MobTrait, Integer> traits;
   private final RandomSource rand;
   private final RegistryAccess access;
   private final TraitGenerator.TraitPool pool;
   private final boolean free;
   private int level;

   public static void generateTraits(MobTraitCap cap, LivingEntity le, int lv, HashMap<MobTrait, Integer> traits, MobDifficultyCollector ins) {
      new TraitGenerator(cap, le, lv, traits, ins).generate();
   }

   private TraitGenerator(MobTraitCap cap, LivingEntity entity, int mobLevel, HashMap<MobTrait, Integer> traits, MobDifficultyCollector ins) {
      this.entity = entity;
      this.mobLevel = mobLevel;
      this.ins = ins;
      this.traits = traits;
      this.access = entity.level().registryAccess();
      this.rand = entity.getRandom();
      this.level = mobLevel;
      this.free = ins.trait_cost < 0.01;
      EntityConfig.Config config = cap.getConfigCache(entity);
      int max = (Integer)LHConfig.SERVER.maxTraitCount.get();
      if (config != null && config.maxTraitCount > 0) {
         max = config.maxTraitCount;
      }

      this.maxTrait = this.free ? -1 : (int)(max / ins.trait_cost);
      ArrayList<MobTrait> list = new ArrayList<>(
         LHTraits.TRAITS
            .get()
            .stream()
            .filter(e -> (config == null || !config.blacklist().contains(e)) && e.allow(entity, mobLevel, ins.getMaxTraitLevel()))
            .toList()
      );
      if (config != null) {
         for (EntityConfig.TraitBase base : config.traits()) {
            if ((base.condition() == null || base.condition().match(entity, mobLevel, ins)) && this.genBase(base) && base.cap()) {
               list.remove(base.trait());
            }
         }

         if (config.presetTraitsOnly) {
            list.clear();
            traits.clear();
         }
      }

      this.pool = new TraitGenerator.TraitPool(list, traits);
   }

   private int getRank(MobTrait e) {
      return this.traits.getOrDefault(e, 0);
   }

   private void setRank(MobTrait e, int rank) {
      if (rank == 0) {
         this.traits.remove(e);
      } else {
         if (this.pool != null && this.getRank(e) == 0) {
            this.pool.update(e);
         }

         this.traits.put(e, rank);
      }
   }

   private boolean genBase(EntityConfig.TraitBase base) {
      MobTrait e = base.trait();
      if (e == null) {
         return false;
      }

      int maxTrait = TraitManager.getMaxLevel() + 1;
      if (!e.allow(this.entity, this.mobLevel, maxTrait)) {
         return false;
      }

      int max = e.getMaxLevel(this.access);
      int cost = e.getCost(this.access, this.ins.trait_cost);
      int old = Math.min(e.getMaxLevel(this.access), Math.max(this.getRank(e), base.free()));
      int expected = Math.min(max, Math.max(old, base.min()));
      int rank = Math.min(expected, old + this.level / cost);
      this.setRank(e, Math.max(old, rank));
      if (rank > old) {
         this.level -= (rank - old) * cost;
      }

      return rank > 0;
   }

   private void generate() {
      while (this.level > 0 && !this.pool.isEmpty() && (this.maxTrait <= 0 || this.traits.size() < this.maxTrait)) {
         MobTrait e = this.pool.pop();
         int cost = e.getCost(this.access, this.ins.trait_cost);
         if (cost <= this.level) {
            int max = Math.min(this.ins.getMaxTraitLevel(), e.getMaxLevel(this.access));
            int old = Math.min(e.getMaxLevel(this.access), this.getRank(e));
            int rank = this.free ? max : Math.min(max, old + this.rand.nextInt(this.level / cost) + 1);
            if (rank > old) {
               this.setRank(e, rank);
               this.level -= (rank - old) * cost;
               if (this.ins.isFullChance() || !(this.rand.nextDouble() < this.ins.suppression())) {
                  continue;
               }
               break;
            }
         }
      }

      for (Entry<MobTrait, Integer> e : this.traits.entrySet()) {
         e.getKey().initialize(this.entity, e.getValue());
      }
   }

   private class TraitEntry {
      private final MobTrait trait;
      private final int baseWeight;
      private double chance;
      private int weight;

      private TraitEntry(MobTrait trait) {
         this.trait = trait;
         this.chance = 1.0;
         this.baseWeight = trait.getConfig(TraitGenerator.this.access).weight();
         this.weight = this.baseWeight;
      }

      public int weight() {
         return this.weight;
      }

      public int updateExclusion(double value) {
         int old = this.weight;
         this.chance *= 1.0 - value;
         this.weight = (int)(this.baseWeight * this.chance);
         return old - this.weight;
      }
   }

   private class TraitPool {
      private final LinkedList<TraitGenerator.TraitEntry> list = new LinkedList<>();
      private final LinkedHashMap<MobTrait, TraitGenerator.TraitEntry> map = new LinkedHashMap<>();
      private final Set<MobTrait> existing = new LinkedHashSet<>();
      private int weights = 0;

      public TraitPool(List<MobTrait> available, HashMap<MobTrait, Integer> existing) {
         for (MobTrait trait : available) {
            TraitGenerator.TraitEntry ent = TraitGenerator.this.new TraitEntry(trait);
            this.list.add(ent);
            this.map.put(trait, ent);
         }

         for (TraitGenerator.TraitEntry e : this.list) {
            this.weights = this.weights + e.weight();
         }

         for (MobTrait trait : existing.keySet()) {
            this.update(trait);
         }

         this.purge();
      }

      private MobTrait pop() {
         int val = TraitGenerator.this.rand.nextInt(this.weights);
         TraitGenerator.TraitEntry e = this.list.getFirst();
         Iterator<TraitGenerator.TraitEntry> itr = this.list.iterator();

         while (itr.hasNext()) {
            TraitGenerator.TraitEntry x = itr.next();
            val -= x.weight();
            if (val <= 0) {
               e = x;
               itr.remove();
               this.map.remove(e.trait);
               this.weights = this.weights - e.weight();
               return e.trait;
            }
         }

         this.list.remove(e);
         this.map.remove(e.trait);
         this.weights = this.weights - e.weight();
         return e.trait;
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      public void update(MobTrait trait) {
         if (!this.existing.contains(trait)) {
            this.existing.add(trait);
            TraitExclusion data = trait.getExclusion(TraitGenerator.this.access);

            for (Entry<Holder<MobTrait>, Double> pair : data.excluded().entrySet()) {
               TraitGenerator.TraitEntry entry = this.map.get(pair.getKey().value());
               if (entry != null) {
                  this.weights = this.weights - entry.updateExclusion(pair.getValue());
               }
            }

            for (TraitGenerator.TraitEntry e : this.list) {
               TraitExclusion exc = e.trait.getExclusion(TraitGenerator.this.access);
               Double val = exc.excluded().getOrDefault(trait.holder(), 0.0);
               if (val != 0.0) {
                  this.weights = this.weights - e.updateExclusion(val);
               }
            }

            this.purge();
         }
      }

      private void purge() {
         Iterator<TraitGenerator.TraitEntry> itr = this.list.iterator();

         while (itr.hasNext()) {
            TraitGenerator.TraitEntry x = itr.next();
            if (x.weight() == 0) {
               itr.remove();
               this.map.remove(x.trait);
            }
         }
      }
   }
}
