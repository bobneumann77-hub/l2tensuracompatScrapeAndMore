package io.github.manasmods.tensura.data.template.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.data.TensuraCritereonPredicates;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record AdditionalRacialPredicate(Optional<Boolean> spiritual, Optional<Boolean> holyAffected, Optional<Boolean> bloodless, Optional<Boolean> undead)
   implements EntitySubPredicate {
   public static final AdditionalRacialPredicate ANY = new AdditionalRacialPredicate(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
   public static final MapCodec<AdditionalRacialPredicate> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.BOOL.optionalFieldOf("spiritual").forGetter(AdditionalRacialPredicate::spiritual),
            Codec.BOOL.optionalFieldOf("holyAffected").forGetter(AdditionalRacialPredicate::holyAffected),
            Codec.BOOL.optionalFieldOf("bloodless").forGetter(AdditionalRacialPredicate::bloodless),
            Codec.BOOL.optionalFieldOf("undead").forGetter(AdditionalRacialPredicate::undead)
         )
         .apply(instance, AdditionalRacialPredicate::new)
   );

   public MapCodec<AdditionalRacialPredicate> codec() {
      return (MapCodec<AdditionalRacialPredicate>)TensuraCritereonPredicates.ADDITIONAL_RACIAL_PREDICATE.get();
   }

   public boolean matches(Entity entity, ServerLevel serverLevel, @Nullable Vec3 vec3) {
      if (this.spiritual.isPresent() && entity instanceof LivingEntity living && this.spiritual.get() != RaceUtils.isSpiritual(living)) {
         return false;
      } else if (this.holyAffected.isPresent() && this.holyAffected.get() != RaceUtils.isAffectedByHolyExposure(entity)) {
         return false;
      } else {
         return this.bloodless.isPresent() && this.bloodless.get() != RaceUtils.isBloodless(entity)
            ? false
            : this.undead.isEmpty() || this.undead.get() == RaceUtils.isUndead(entity);
      }
   }
}
