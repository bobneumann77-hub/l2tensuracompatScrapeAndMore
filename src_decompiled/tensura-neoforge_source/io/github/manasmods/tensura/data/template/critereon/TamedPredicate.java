package io.github.manasmods.tensura.data.template.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.registry.data.TensuraCritereonPredicates;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record TamedPredicate(boolean tamed) implements EntitySubPredicate {
   public static final MapCodec<TamedPredicate> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(Codec.BOOL.fieldOf("tamed").forGetter(TamedPredicate::tamed)).apply(instance, TamedPredicate::new)
   );

   public MapCodec<TamedPredicate> codec() {
      return (MapCodec<TamedPredicate>)TensuraCritereonPredicates.TAMED_PREDICATE.get();
   }

   public boolean matches(Entity entity, ServerLevel serverLevel, @Nullable Vec3 vec3) {
      if (this.tamed()) {
         return entity instanceof ISubordinate subordinate ? subordinate.isTame() : false;
      } else {
         return entity instanceof ISubordinate subordinate ? !subordinate.isTame() : false;
      }
   }
}
