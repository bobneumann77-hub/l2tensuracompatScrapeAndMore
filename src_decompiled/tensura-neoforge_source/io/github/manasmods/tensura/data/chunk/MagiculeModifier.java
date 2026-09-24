package io.github.manasmods.tensura.data.chunk;

import org.jetbrains.annotations.NotNull;

public interface MagiculeModifier extends Comparable<MagiculeModifier> {
   int getPriority();

   default double getMagicule(double oldMagicule) {
      return oldMagicule;
   }

   default double getRegenerationRate(double oldRegenerationRate) {
      return oldRegenerationRate;
   }

   default int compareTo(@NotNull MagiculeModifier o) {
      return Integer.compare(this.getPriority(), o.getPriority());
   }
}
