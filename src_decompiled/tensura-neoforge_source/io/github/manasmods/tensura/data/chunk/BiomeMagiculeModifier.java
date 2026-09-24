package io.github.manasmods.tensura.data.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record BiomeMagiculeModifier(
   ResourceLocation biomeId, int priority, List<DataPackMagiculeModifier> modifiers, List<DataPackMagiculeModifier> regenModifiers
) implements MagiculeModifier {
   public static final Codec<BiomeMagiculeModifier> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("biome").forGetter(BiomeMagiculeModifier::biomeId),
            Codec.intRange(0, 255).optionalFieldOf("priority", -1).forGetter(BiomeMagiculeModifier::priority),
            DataPackMagiculeModifier.CODEC.listOf().optionalFieldOf("modifiers", new ArrayList()).forGetter(BiomeMagiculeModifier::modifiers),
            DataPackMagiculeModifier.CODEC.listOf().optionalFieldOf("regen_modifiers", new ArrayList()).forGetter(BiomeMagiculeModifier::regenModifiers)
         )
         .apply(instance, BiomeMagiculeModifier::new)
   );

   @Override
   public double getMagicule(double oldMagicule) {
      for (DataPackMagiculeModifier modifier : this.modifiers) {
         switch (modifier.mode()) {
            case ADD:
               oldMagicule += modifier.value();
               break;
            case MULTIPLY:
               oldMagicule *= modifier.value();
         }
      }

      return oldMagicule;
   }

   @Override
   public double getRegenerationRate(double oldRegenerationRate) {
      for (DataPackMagiculeModifier modifier : this.regenModifiers) {
         switch (modifier.mode()) {
            case ADD:
               oldRegenerationRate += modifier.value();
               break;
            case MULTIPLY:
               oldRegenerationRate *= modifier.value();
         }
      }

      return oldRegenerationRate;
   }

   @Override
   public int getPriority() {
      return this.priority;
   }
}
