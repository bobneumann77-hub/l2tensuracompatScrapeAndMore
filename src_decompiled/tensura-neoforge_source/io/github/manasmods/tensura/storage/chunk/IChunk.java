package io.github.manasmods.tensura.storage.chunk;

import io.github.manasmods.tensura.data.chunk.BlockMagiculeModifier;
import java.util.List;
import net.minecraft.core.BlockPos;

public interface IChunk {
   double getMagicule();

   void setMagicule(double var1);

   boolean consumeMagicule(double var1);

   double getBaseMaxMagicule();

   double getMaxMagicule();

   void setMaxMagicule(double var1);

   double getRegenerationRate();

   void setRegenerationRate(double var1);

   List<BlockMagiculeModifier> getBlockModifiers();

   void addBlockModifier(BlockMagiculeModifier var1);

   void addBlockModifier(BlockPos var1, double var2, double var4);

   void removeBlockModifier(BlockMagiculeModifier var1);

   void removeBlockModifier(BlockPos var1);

   void clearBlockModifiers();

   void reinitialize();

   void markDirty();
}
