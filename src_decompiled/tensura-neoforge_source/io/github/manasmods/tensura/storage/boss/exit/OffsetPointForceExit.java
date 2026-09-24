package io.github.manasmods.tensura.storage.boss.exit;

import com.google.gson.JsonObject;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import io.github.manasmods.tensura.util.EnergyHelper;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class OffsetPointForceExit implements IForceExitHandler {
   private double xOffset;
   private double yOffset;
   private double zOffset;
   private final WarpPoint.TransmissionType type;

   public OffsetPointForceExit(double xOffset, double yOffset, double zOffset, WarpPoint.TransmissionType type) {
      this.xOffset = xOffset;
      this.yOffset = yOffset;
      this.zOffset = zOffset;
      this.type = type;
   }

   @Override
   public CompoundTag toNBT(Provider registries) {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "OffsetPoint");
      nbt.putDouble("xOffset", this.xOffset);
      nbt.putDouble("yOffset", this.yOffset);
      nbt.putDouble("zOffset", this.zOffset);
      nbt.putInt("TransmissionType", this.type.ordinal());
      return nbt;
   }

   public static OffsetPointForceExit fromNBT(CompoundTag tag) {
      WarpPoint.TransmissionType type = tag.contains("TransmissionType")
         ? WarpPoint.TransmissionType.values()[tag.getInt("TransmissionType")]
         : WarpPoint.TransmissionType.FORCE_EXIT;
      return new OffsetPointForceExit(tag.getDouble("xOffset"), tag.getDouble("yOffset"), tag.getDouble("zOffset"), type);
   }

   @Override
   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("type", "OffsetPoint");
      json.addProperty("xOffset", this.xOffset);
      json.addProperty("yOffset", this.yOffset);
      json.addProperty("zOffset", this.zOffset);
      json.addProperty("TransmissionType", this.type.ordinal());
      return json;
   }

   public static OffsetPointForceExit fromJson(JsonObject json) {
      WarpPoint.TransmissionType type = json.has("TransmissionType")
         ? WarpPoint.TransmissionType.values()[json.get("TransmissionType").getAsInt()]
         : WarpPoint.TransmissionType.FORCE_EXIT;
      return new OffsetPointForceExit(json.get("xOffset").getAsDouble(), json.get("yOffset").getAsDouble(), json.get("zOffset").getAsDouble(), type);
   }

   @Override
   public MutableComponent getDataMessage() {
      String center = "[" + this.getXOffset() + ", " + this.getYOffset() + ", " + this.getZOffset() + "]";
      return Component.translatable("tensura.boss_fight.get.force_exit.offset", new Object[]{center});
   }

   @Override
   public boolean apply(Entity entity, double costPerBlock) {
      Vec3 offset = entity.position().add(this.xOffset, this.yOffset, this.zOffset);
      if (costPerBlock > 0.0
         && entity instanceof LivingEntity teleporter
         && EnergyHelper.isOutOfEnergy(teleporter, 0.0, costPerBlock * Math.sqrt(entity.distanceToSqr(offset)))) {
         return false;
      } else {
         Level level = entity.level();
         Changeable<Vec3> position = Changeable.of(offset);
         if (!((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
            .transmission(entity, null, position, this.type)
            .isFalse()) {
            double x = ((Vec3)position.get()).x();
            double y = ((Vec3)position.get()).y();
            double z = ((Vec3)position.get()).z();
            if (level.getWorldBorder().isWithinBounds(new BlockPos((int)x, (int)y, (int)z))) {
               entity.unRide();
               entity.resetFallDistance();
               entity.teleportTo(x, y, z);
            }
         }

         return true;
      }
   }

   @Generated
   public double getXOffset() {
      return this.xOffset;
   }

   @Generated
   public double getYOffset() {
      return this.yOffset;
   }

   @Generated
   public double getZOffset() {
      return this.zOffset;
   }

   @Generated
   public WarpPoint.TransmissionType getType() {
      return this.type;
   }
}
