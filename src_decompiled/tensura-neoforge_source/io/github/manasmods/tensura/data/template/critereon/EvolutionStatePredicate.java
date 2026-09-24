package io.github.manasmods.tensura.data.template.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.registry.data.TensuraCritereonPredicates;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record EvolutionStatePredicate(int evolution, Optional<Integer> maxEvolution) implements EntitySubPredicate {
   public static final MapCodec<EvolutionStatePredicate> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.INT.fieldOf("evolution").forGetter(EvolutionStatePredicate::evolution),
            Codec.INT.optionalFieldOf("maxEvolution").forGetter(EvolutionStatePredicate::maxEvolution)
         )
         .apply(instance, EvolutionStatePredicate::new)
   );

   public MapCodec<EvolutionStatePredicate> codec() {
      return (MapCodec<EvolutionStatePredicate>)TensuraCritereonPredicates.EVOLUTION_STATE_PREDICATE.get();
   }

   public boolean matches(Entity entity, ServerLevel serverLevel, @Nullable Vec3 vec3) {
      if (entity instanceof INameEvolution nameEvolution) {
         int state = nameEvolution.getCurrentEvolutionState();
         return state < this.evolution ? false : this.maxEvolution.isEmpty() || state <= this.maxEvolution.get();
      } else {
         return false;
      }
   }

   public static EvolutionStatePredicate getDefault(int evolution) {
      return new EvolutionStatePredicate(evolution, Optional.empty());
   }
}
