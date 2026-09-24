package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.entity.template.TensuraPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.IMultipart;
import io.github.manasmods.tensura.world.subclass.IMultipartLevel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class MixinLevel implements IMultipartLevel {
   @Unique
   private final Int2ObjectMap<TensuraPartEntity> tensura$parts = new Int2ObjectOpenHashMap();
   @Unique
   private final Long2ObjectMap<List<TensuraPartEntity>> tensura$partsByChunk = new Long2ObjectOpenHashMap();
   @Unique
   private final Int2ObjectMap<long[]> tensura$partChunkKeys = new Int2ObjectOpenHashMap();

   @Override
   public Int2ObjectMap<TensuraPartEntity> tensura$getParts() {
      return this.tensura$parts;
   }

   @Override
   public Long2ObjectMap<List<TensuraPartEntity>> tensura$getPartsByChunk() {
      return this.tensura$partsByChunk;
   }

   @Override
   public Int2ObjectMap<long[]> tensura$getPartChunkKeys() {
      return this.tensura$partChunkKeys;
   }

   @Override
   public void tensura$registerPart(TensuraPartEntity part) {
      if (part != null) {
         this.tensura$parts.put(part.getId(), part);
         this.tensura$rebucketPart(part);
      }
   }

   @Override
   public void tensura$unregisterPart(TensuraPartEntity part) {
      if (part != null) {
         int id = part.getId();
         this.tensura$parts.remove(id);
         long[] keys = (long[])this.tensura$partChunkKeys.remove(id);
         if (keys != null) {
            for (long key : keys) {
               List<TensuraPartEntity> bucket = (List<TensuraPartEntity>)this.tensura$partsByChunk.get(key);
               if (bucket != null) {
                  bucket.removeIf(e -> e.getId() == id);
                  if (bucket.isEmpty()) {
                     this.tensura$partsByChunk.remove(key);
                  }
               }
            }
         }
      }
   }

   @Override
   public void tensura$rebucketPart(TensuraPartEntity part) {
      if (part != null) {
         int id = part.getId();
         long[] newKeys = tensura$chunkKeysCovered(part.getBoundingBox());
         long[] oldKeys = (long[])this.tensura$partChunkKeys.get(id);
         if (!Arrays.equals(oldKeys, newKeys)) {
            if (oldKeys != null) {
               for (long key : oldKeys) {
                  List<TensuraPartEntity> bucket = (List<TensuraPartEntity>)this.tensura$partsByChunk.get(key);
                  if (bucket != null) {
                     bucket.removeIf(e -> e.getId() == id);
                     if (bucket.isEmpty()) {
                        this.tensura$partsByChunk.remove(key);
                     }
                  }
               }
            }

            for (long key : newKeys) {
               ((List)this.tensura$partsByChunk.computeIfAbsent(key, k -> new ArrayList())).add(part);
            }

            this.tensura$partChunkKeys.put(id, newKeys);
         }
      }
   }

   @Override
   public Collection<TensuraPartEntity> tensura$getPartsIn(AABB aabb) {
      if (this.tensura$parts.isEmpty()) {
         return Collections.emptyList();
      }

      int minX = Mth.floor(aabb.minX) >> 4;
      int maxX = Mth.floor(aabb.maxX) >> 4;
      int minZ = Mth.floor(aabb.minZ) >> 4;
      int maxZ = Mth.floor(aabb.maxZ) >> 4;
      IntOpenHashSet seen = new IntOpenHashSet();
      ArrayList<TensuraPartEntity> out = new ArrayList<>();

      for (int cx = minX; cx <= maxX; cx++) {
         for (int cz = minZ; cz <= maxZ; cz++) {
            List<TensuraPartEntity> bucket = (List<TensuraPartEntity>)this.tensura$partsByChunk.get(ChunkPos.asLong(cx, cz));
            if (bucket != null) {
               for (TensuraPartEntity part : bucket) {
                  if (seen.add(part.getId()) && part.isAlive() && part.getBoundingBox().intersects(aabb)) {
                     out.add(part);
                  }
               }
            }
         }
      }

      return out;
   }

   @Unique
   private static long[] tensura$chunkKeysCovered(AABB box) {
      int x0 = Mth.floor(box.minX) >> 4;
      int x1 = Mth.floor(box.maxX) >> 4;
      int z0 = Mth.floor(box.minZ) >> 4;
      int z1 = Mth.floor(box.maxZ) >> 4;
      long[] keys = new long[(x1 - x0 + 1) * (z1 - z0 + 1)];
      int i = 0;

      for (int cx = x0; cx <= x1; cx++) {
         for (int cz = z0; cz <= z1; cz++) {
            keys[i++] = ChunkPos.asLong(cx, cz);
         }
      }

      return keys;
   }

   @Inject(
      method = "guardEntityTick(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/Entity;)V",
      at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", shift = Shift.AFTER)
   )
   private void tensura$rebucketAfterTick(Consumer<Entity> consumer, Entity entity, CallbackInfo ci) {
      if (entity instanceof IMultipart parent) {
         for (TensuraPartEntity part : parent.getParts()) {
            if (part != null) {
               this.tensura$rebucketPart(part);
            }
         }
      }
   }

   @ModifyReturnValue(
      method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
      at = @At("RETURN")
   )
   private List<Entity> tensura$getEntityParts(List<Entity> original, Entity entity, AABB aabb, Predicate<? super Entity> predicate) {
      if (this.tensura$parts.isEmpty()) {
         return original;
      }

      for (TensuraPartEntity part : this.tensura$getPartsIn(aabb)) {
         if (part != entity && predicate.test(part)) {
            original.add(part);
         }
      }

      return original;
   }

   @ModifyReturnValue(
      method = "getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;",
      at = @At("RETURN")
   )
   private <T extends Entity> List<T> tensura$getEntityPartsTyped(List<T> original, EntityTypeTest<Entity, T> test, AABB aabb, Predicate<? super T> predicate) {
      if (this.tensura$parts.isEmpty()) {
         return original;
      }

      for (TensuraPartEntity part : this.tensura$getPartsIn(aabb)) {
         T type = (T)test.tryCast(part);
         if (type != null && predicate.test(type)) {
            original.add(type);
         }
      }

      return original;
   }
}
