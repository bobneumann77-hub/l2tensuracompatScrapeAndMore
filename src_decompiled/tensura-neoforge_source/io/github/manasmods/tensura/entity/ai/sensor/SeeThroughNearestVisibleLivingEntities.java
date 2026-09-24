package io.github.manasmods.tensura.entity.ai.sensor;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.tslat.smartbrainlib.util.SensoryUtils;
import org.jetbrains.annotations.NotNull;

public class SeeThroughNearestVisibleLivingEntities extends NearestVisibleLivingEntities {
   protected final List<LivingEntity> nearbyEntities;
   protected final Predicate<LivingEntity> lineOfSightTest;

   public SeeThroughNearestVisibleLivingEntities(LivingEntity entity, List<LivingEntity> list) {
      super(entity, list);
      this.nearbyEntities = list;
      this.lineOfSightTest = new Predicate<LivingEntity>() {
         final Object2BooleanOpenHashMap<LivingEntity> cache = new Object2BooleanOpenHashMap(list.size());

         public boolean test(LivingEntity target) {
            return this.cache.computeIfAbsent(target, target1 -> SensoryUtils.isEntityAttackableIgnoringLineOfSight(entity, target1));
         }
      };
   }

   @NotNull
   public Optional<LivingEntity> findClosest(Predicate<LivingEntity> predicate) {
      for (LivingEntity target : this.nearbyEntities) {
         if (predicate.test(target) && this.lineOfSightTest.test(target)) {
            return Optional.of(target);
         }
      }

      return Optional.empty();
   }

   @NotNull
   public Iterable<LivingEntity> findAll(Predicate<LivingEntity> predicate) {
      return Iterables.filter(this.nearbyEntities, livingEntity -> predicate.test(livingEntity) && this.lineOfSightTest.test(livingEntity));
   }

   @NotNull
   public Stream<LivingEntity> find(Predicate<LivingEntity> predicate) {
      return this.nearbyEntities.stream().filter(livingEntity -> predicate.test(livingEntity) && this.lineOfSightTest.test(livingEntity));
   }

   public boolean contains(LivingEntity livingEntity) {
      return this.nearbyEntities.contains(livingEntity) && this.lineOfSightTest.test(livingEntity);
   }

   public boolean contains(Predicate<LivingEntity> predicate) {
      for (LivingEntity target : this.nearbyEntities) {
         if (predicate.test(target) && this.lineOfSightTest.test(target)) {
            return true;
         }
      }

      return false;
   }
}
