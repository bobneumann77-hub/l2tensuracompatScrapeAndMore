package io.github.manasmods.tensura.data.template.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.entity.monster.HellCaterpillarEntity;
import io.github.manasmods.tensura.entity.monster.HellMothEntity;
import io.github.manasmods.tensura.registry.data.TensuraCritereonPredicates;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record HellCaterpillarPredicate(Optional<Boolean> gehenna, Optional<Boolean> cocoon) implements EntitySubPredicate {
   public static final HellCaterpillarPredicate ANY = new HellCaterpillarPredicate(Optional.empty(), Optional.empty());
   public static final MapCodec<HellCaterpillarPredicate> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.BOOL.optionalFieldOf("gehenna").forGetter(HellCaterpillarPredicate::gehenna),
            Codec.BOOL.optionalFieldOf("cocoon").forGetter(HellCaterpillarPredicate::cocoon)
         )
         .apply(instance, HellCaterpillarPredicate::new)
   );

   public MapCodec<HellCaterpillarPredicate> codec() {
      return (MapCodec<HellCaterpillarPredicate>)TensuraCritereonPredicates.HELL_CATERPILLAR_PREDICATE.get();
   }

   public boolean matches(Entity entity, ServerLevel serverLevel, @Nullable Vec3 vec3) {
      if (entity instanceof HellCaterpillarEntity caterpillar) {
         return this.gehenna.isPresent() && caterpillar.isGehenna() != this.gehenna.get()
            ? false
            : this.cocoon.isEmpty() || caterpillar.isCocooned() == this.cocoon.get();
      } else if (entity instanceof HellMothEntity moth) {
         return this.gehenna.isPresent() && moth.isGehenna() != this.gehenna.get() ? false : this.cocoon.isEmpty() || moth.hasEgg() == this.cocoon.get();
      } else {
         return true;
      }
   }
}
