package io.github.manasmods.tensura.storage.boss.exit;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;

public interface IForceExitHandler {
   CompoundTag toNBT(Provider var1);

   boolean apply(Entity var1, double var2);

   MutableComponent getDataMessage();

   static IForceExitHandler fromNBT(CompoundTag nbt, Provider registries) {
      String type = nbt.getString("type");

      return switch (type) {
         case "WarpPoint" -> WarpPointForceExit.fromNBT(nbt, registries);
         case "OffsetPoint" -> OffsetPointForceExit.fromNBT(nbt);
         case "RandomRange" -> RandomRangeForceExit.fromNBT(nbt, registries);
         default -> new SpawnPointForceExit();
      };
   }

   JsonObject toJson();

   static IForceExitHandler fromJson(JsonObject json) {
      String type = json.get("type").getAsString();

      return switch (type) {
         case "WarpPoint" -> WarpPointForceExit.fromJson(json);
         case "OffsetPoint" -> OffsetPointForceExit.fromJson(json);
         case "RandomRange" -> RandomRangeForceExit.fromJson(json);
         default -> new SpawnPointForceExit();
      };
   }
}
