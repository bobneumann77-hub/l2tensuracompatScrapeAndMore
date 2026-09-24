package io.github.manasmods.tensura.world.subclass;

import io.github.manasmods.tensura.entity.template.TensuraPartEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.phys.AABB;

public interface IMultipartLevel {
   default Collection<TensuraPartEntity> getParts() {
      return this.tensura$getParts().values();
   }

   Int2ObjectMap<TensuraPartEntity> tensura$getParts();

   Long2ObjectMap<List<TensuraPartEntity>> tensura$getPartsByChunk();

   Int2ObjectMap<long[]> tensura$getPartChunkKeys();

   void tensura$registerPart(TensuraPartEntity var1);

   void tensura$unregisterPart(TensuraPartEntity var1);

   void tensura$rebucketPart(TensuraPartEntity var1);

   Collection<TensuraPartEntity> tensura$getPartsIn(AABB var1);
}
