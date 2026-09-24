package io.github.manasmods.tensura.data.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record LevelMagiculeModifier(
   ResourceLocation worldId, int priority, List<DataPackMagiculeModifier> modifiers, List<DataPackMagiculeModifier> regenModifiers
) implements MagiculeModifier {
   public static final Codec<LevelMagiculeModifier> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("world").forGetter(LevelMagiculeModifier::worldId),
            Codec.intRange(0, 255).optionalFieldOf("priority", 0).forGetter(LevelMagiculeModifier::priority),
            DataPackMagiculeModifier.CODEC.listOf().optionalFieldOf("modifiers", new ArrayList()).forGetter(LevelMagiculeModifier::modifiers),
            DataPackMagiculeModifier.CODEC.listOf().optionalFieldOf("regen_modifiers", new ArrayList()).forGetter(LevelMagiculeModifier::regenModifiers)
         )
         .apply(instance, LevelMagiculeModifier::new)
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
