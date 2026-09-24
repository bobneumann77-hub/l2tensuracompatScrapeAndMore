package io.github.manasmods.tensura.data.template.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.registry.data.TensuraCritereonPredicates;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record ExistencePointPredicate(double EP, Optional<Double> maxEP, Optional<Boolean> affectedByGamerule, Optional<List<MobSpawnType>> blackListSpawnType)
   implements EntitySubPredicate {
   public static final MapCodec<ExistencePointPredicate> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.DOUBLE.fieldOf("EP").forGetter(ExistencePointPredicate::EP),
            Codec.DOUBLE.optionalFieldOf("maxEP").forGetter(ExistencePointPredicate::maxEP),
            Codec.BOOL.optionalFieldOf("affectedByGamerule").forGetter(ExistencePointPredicate::affectedByGamerule),
            Codec.list(TensuraCritereonPredicates.SPAWN_TYPE_CODEC)
               .optionalFieldOf("blackListSpawnType")
               .forGetter(ExistencePointPredicate::blackListSpawnType)
         )
         .apply(instance, ExistencePointPredicate::new)
   );

   public MapCodec<ExistencePointPredicate> codec() {
      return (MapCodec<ExistencePointPredicate>)TensuraCritereonPredicates.EXISTENCE_POINT_PREDICATE.get();
   }

   public boolean matches(Entity entity, ServerLevel serverLevel, @Nullable Vec3 vec3) {
      if (entity instanceof LivingEntity living) {
         double EP = EnergyHelper.getMaxEP(living);
         if (this.affectedByGamerule.isPresent() && this.affectedByGamerule.get()) {
            EP *= EnergyHelper.getEPMultiplierByNamespace(entity);
         }

         if (!(EP < this.EP) && (!this.maxEP.isPresent() || !(EP > this.maxEP.get()))) {
            if (this.blackListSpawnType.isPresent()) {
               IExistence existence = TensuraStorages.getExistenceFrom(living);
               if (this.blackListSpawnType.get().contains(existence.getSpawnType())) {
                  return false;
               }
            }

            return living instanceof INameEvolution evolution ? evolution.shouldDropCrystal() : true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static ExistencePointPredicate getDefault(double EP) {
      return new ExistencePointPredicate(EP, Optional.empty(), Optional.of(true), Optional.of(List.of(MobSpawnType.MOB_SUMMONED, MobSpawnType.TRIGGERED)));
   }

   public static ExistencePointPredicate getDefault(double EP, double maxEP) {
      return new ExistencePointPredicate(EP, Optional.of(maxEP), Optional.of(true), Optional.of(List.of(MobSpawnType.MOB_SUMMONED, MobSpawnType.TRIGGERED)));
   }

   public static ExistencePointPredicate getSpawnBlackList(MobSpawnType... types) {
      return new ExistencePointPredicate(1.0, Optional.empty(), Optional.empty(), Optional.of(List.of(types)));
   }
}
