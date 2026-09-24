package io.github.manasmods.tensura.storage.boss.exit;

import com.google.gson.JsonObject;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import io.github.manasmods.tensura.util.EnergyHelper;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class WarpPointForceExit implements IForceExitHandler {
   private final WarpPoint warpPoint;
   private final WarpPoint.TransmissionType type;

   public WarpPointForceExit(WarpPoint warpPoint, WarpPoint.TransmissionType type) {
      this.warpPoint = warpPoint;
      this.type = type;
   }

   public WarpPointForceExit(BlockPos position, ResourceKey<Level> dimension, WarpPoint.TransmissionType type) {
      this.warpPoint = new WarpPoint("Force Exit", position.getX(), position.getY(), position.getZ(), dimension);
      this.type = type;
   }

   @Override
   public CompoundTag toNBT(Provider registries) {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "WarpPoint");
      nbt.putInt("TransmissionType", this.type.ordinal());
      nbt.put("WarpPoint", this.warpPoint.serialize(registries));
      return nbt;
   }

   public static WarpPointForceExit fromNBT(CompoundTag tag, Provider registries) {
      WarpPoint.TransmissionType type = tag.contains("TransmissionType")
         ? WarpPoint.TransmissionType.values()[tag.getInt("TransmissionType")]
         : WarpPoint.TransmissionType.FORCE_EXIT;
      return new WarpPointForceExit(WarpPoint.fromNBT(tag.getCompound("WarpPoint"), registries), type);
   }

   @Override
   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("type", "WarpPoint");
      json.addProperty("TransmissionType", this.type.ordinal());
      json.add("WarpPoint", this.warpPoint.toJson());
      return json;
   }

   public static WarpPointForceExit fromJson(JsonObject json) {
      WarpPoint.TransmissionType type = json.has("TransmissionType")
         ? WarpPoint.TransmissionType.values()[json.get("TransmissionType").getAsInt()]
         : WarpPoint.TransmissionType.FORCE_EXIT;
      return new WarpPointForceExit(WarpPoint.fromJson(json.getAsJsonObject("WarpPoint")), type);
   }

   @Override
   public MutableComponent getDataMessage() {
      String center = "[" + this.getWarpPoint().getX() + ", " + this.getWarpPoint().getY() + ", " + this.getWarpPoint().getZ() + "]";
      return Component.translatable(
         "tensura.boss_fight.get.force_exit.warp_point", new Object[]{center, this.getWarpPoint().getDimension().location().toString()}
      );
   }

   @Override
   public boolean apply(Entity entity, double costPerBlock) {
      if (costPerBlock > 0.0
         && entity instanceof LivingEntity teleporter
         && EnergyHelper.isOutOfEnergy(
            teleporter, 0.0, costPerBlock * Math.sqrt(entity.distanceToSqr(this.getWarpPoint().getX(), this.getWarpPoint().getY(), this.getWarpPoint().getZ()))
         )) {
         return false;
      } else {
         this.getWarpPoint().warp(entity, null, this.type, false, true);
         return true;
      }
   }

   @Generated
   public WarpPoint getWarpPoint() {
      return this.warpPoint;
   }

   @Generated
   public WarpPoint.TransmissionType getType() {
      return this.type;
   }
}
