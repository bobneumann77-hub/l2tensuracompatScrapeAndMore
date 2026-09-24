package io.github.manasmods.tensura.data.template.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.entity.monster.SlimeEntity;
import io.github.manasmods.tensura.registry.data.TensuraCritereonPredicates;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record SlimePredicate(Optional<Boolean> chilled, Optional<Boolean> massive) implements EntitySubPredicate {
   public static final SlimePredicate ANY = new SlimePredicate(Optional.empty(), Optional.empty());
   public static final MapCodec<SlimePredicate> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.BOOL.optionalFieldOf("chilled").forGetter(SlimePredicate::chilled), Codec.BOOL.optionalFieldOf("massive").forGetter(SlimePredicate::massive)
         )
         .apply(instance, SlimePredicate::new)
   );

   public MapCodec<SlimePredicate> codec() {
      return (MapCodec<SlimePredicate>)TensuraCritereonPredicates.SLIME_PREDICATE.get();
   }

   public boolean matches(Entity entity, ServerLevel serverLevel, @Nullable Vec3 vec3) {
      if (entity instanceof SlimeEntity slime) {
         return this.chilled.isPresent() && slime.isChilled() != this.chilled.get() ? false : this.massive.isEmpty() || slime.isMassive() == this.massive.get();
      } else {
         return true;
      }
   }
}
