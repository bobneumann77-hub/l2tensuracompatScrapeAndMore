package io.github.manasmods.tensura.data.template.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.registry.data.TensuraCritereonPredicates;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record VariantPredicate(List<String> variants, Optional<Boolean> reverse) implements EntitySubPredicate {
   public static final MapCodec<VariantPredicate> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.list(Codec.STRING).fieldOf("variants").forGetter(VariantPredicate::variants),
            Codec.BOOL.optionalFieldOf("reverse").forGetter(VariantPredicate::reverse)
         )
         .apply(instance, VariantPredicate::new)
   );

   public MapCodec<VariantPredicate> codec() {
      return (MapCodec<VariantPredicate>)TensuraCritereonPredicates.VARIANT_PREDICATE.get();
   }

   public boolean matches(Entity entity, ServerLevel serverLevel, @Nullable Vec3 vec3) {
      if (entity instanceof VariantHolder<?> holder) {
         if (this.reverse.isPresent() && this.reverse.get()) {
            for (String variant : this.variants) {
               if (holder.getVariant().toString().equalsIgnoreCase(variant)) {
                  return false;
               }
            }

            return true;
         }

         for (String variant : this.variants) {
            if (holder.getVariant().toString().equalsIgnoreCase(variant)) {
               return true;
            }
         }
      }

      return false;
   }

   public static VariantPredicate getDefault(String variant) {
      return new VariantPredicate(List.of(variant), Optional.empty());
   }
}
