package io.github.manasmods.tensura.storage.boss.exit;

import com.google.gson.JsonObject;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.SpawnPointHelper;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import io.github.manasmods.tensura.util.EnergyHelper;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;

public class RandomRangeForceExit implements IForceExitHandler {
   private double xzRange;
   private double yRange;
   private boolean onWorldSurface;
   private ResourceKey<Level> dimension = Level.OVERWORLD;
   private final WarpPoint.TransmissionType type;

   public RandomRangeForceExit(double xzRange, double yRange, boolean onWorldSurface, WarpPoint.TransmissionType type) {
      this.xzRange = xzRange;
      this.yRange = yRange;
      this.onWorldSurface = onWorldSurface;
      this.type = type;
   }

   public RandomRangeForceExit(double xzRange, double yRange, boolean onWorldSurface, ResourceKey<Level> dimension, WarpPoint.TransmissionType type) {
      this.xzRange = xzRange;
      this.yRange = yRange;
      this.onWorldSurface = onWorldSurface;
      this.dimension = dimension;
      this.type = type;
   }

   @Override
   public CompoundTag toNBT(Provider registries) {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "RandomRange");
      nbt.putDouble("xzRange", this.xzRange);
      nbt.putDouble("yRange", this.yRange);
      nbt.putBoolean("onWorldSurface", this.onWorldSurface);
      nbt.put("dimension", WarpPoint.writeLevelTag(this.dimension, registries));
      nbt.putInt("TransmissionType", this.type.ordinal());
      return nbt;
   }

   public static RandomRangeForceExit fromNBT(CompoundTag tag, Provider registries) {
      ResourceKey<Level> dimension;
      if (tag.contains("dimension")) {
         dimension = WarpPoint.getLevel(tag.get("dimension"), registries);
      } else {
         dimension = Level.OVERWORLD;
      }

      WarpPoint.TransmissionType type = tag.contains("TransmissionType")
         ? WarpPoint.TransmissionType.values()[tag.getInt("TransmissionType")]
         : WarpPoint.TransmissionType.FORCE_EXIT;
      return new RandomRangeForceExit(tag.getDouble("xzRange"), tag.getDouble("yRange"), tag.getBoolean("onWorldSurface"), dimension, type);
   }

   @Override
   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("type", "RandomRange");
      json.addProperty("xzRange", this.xzRange);
      json.addProperty("yRange", this.yRange);
      json.addProperty("onWorldSurface", this.onWorldSurface);
      json.addProperty("dimension", this.dimension.location().toString());
      json.addProperty("TransmissionType", this.type.ordinal());
      return json;
   }

   public static RandomRangeForceExit fromJson(JsonObject json) {
      ResourceKey<Level> dimension;
      if (json.has("dimension")) {
         ResourceLocation dimLocation = ResourceLocation.parse(json.get("dimension").getAsString());
         dimension = ResourceKey.create(Registries.DIMENSION, dimLocation);
      } else {
         dimension = Level.OVERWORLD;
      }

      WarpPoint.TransmissionType type = json.has("TransmissionType")
         ? WarpPoint.TransmissionType.values()[json.get("TransmissionType").getAsInt()]
         : WarpPoint.TransmissionType.FORCE_EXIT;
      return new RandomRangeForceExit(
         json.get("xzRange").getAsDouble(), json.get("yRange").getAsDouble(), json.get("onWorldSurface").getAsBoolean(), dimension, type
      );
   }

   @Override
   public MutableComponent getDataMessage() {
      return Component.translatable(
         "tensura.boss_fight.get.force_exit.random", new Object[]{this.getXzRange(), this.getYRange(), this.getDimension().location().toString()}
      );
   }

   @Override
   public boolean apply(Entity entity, double costPerBlock) {
      Level level = entity.level();
      if (level.dimension() != this.getDimension()) {
         if (level.getServer() == null) {
            return false;
         }

         if (((TensuraEntityEvents.DimensionTravelEvent)TensuraEntityEvents.DIMENSION_TRAVEL_EVENT.invoker())
            .travel(entity, entity, this.getDimension())
            .isFalse()) {
            return false;
         }

         ServerLevel destination = level.getServer().getLevel(level.dimension());
         if (destination == null) {
            return false;
         }

         double x = entity.getX() + (2.0 * entity.getRandom().nextDouble() - 1.0) * this.xzRange;
         double z = entity.getZ() + (2.0 * entity.getRandom().nextDouble() - 1.0) * this.xzRange;
         double y;
         if (this.onWorldSurface) {
            destination.getChunk((int)x >> 4, (int)z >> 4, ChunkStatus.FULL, true);
            y = destination.getHeight(Types.WORLD_SURFACE, (int)x, (int)z);
         } else {
            y = entity.getY() + (2.0 * entity.getRandom().nextDouble() - 1.0) * this.yRange;
         }

         Changeable<Vec3> position = Changeable.of(new Vec3(x, y, z));
         if (!((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
            .transmission(entity, entity, position, this.type)
            .isFalse()) {
            x = ((Vec3)position.get()).x();
            y = ((Vec3)position.get()).y();
            z = ((Vec3)position.get()).z();
            if (level.getWorldBorder().isWithinBounds(new BlockPos((int)x, (int)y, (int)z))) {
               entity.unRide();
               entity.resetFallDistance();
               SpawnPointHelper.teleportToAcrossDimensions(entity, this.getDimension(), x, y, z, entity.getYRot(), entity.getXRot());
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         double x = entity.getX() + (2.0 * entity.getRandom().nextDouble() - 1.0) * this.xzRange;
         double z = entity.getZ() + (2.0 * entity.getRandom().nextDouble() - 1.0) * this.xzRange;
         double y;
         if (this.onWorldSurface) {
            level.getChunk((int)x >> 4, (int)z >> 4, ChunkStatus.FULL, true);
            y = level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z);
         } else {
            y = entity.getY() + (2.0 * entity.getRandom().nextDouble() - 1.0) * this.yRange;
         }

         Vec3 target = new Vec3(x, y, z);
         if (costPerBlock > 0.0
            && entity instanceof LivingEntity teleporter
            && EnergyHelper.isOutOfEnergy(teleporter, 0.0, costPerBlock * Math.sqrt(entity.distanceToSqr(target)))) {
            return false;
         } else {
            Changeable<Vec3> position = Changeable.of(target);
            if (!((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
               .transmission(entity, null, position, this.type)
               .isFalse()) {
               x = ((Vec3)position.get()).x();
               y = ((Vec3)position.get()).y();
               z = ((Vec3)position.get()).z();
               if (level.getWorldBorder().isWithinBounds(new BlockPos((int)x, (int)y, (int)z))) {
                  entity.unRide();
                  entity.resetFallDistance();
                  entity.teleportTo(x, y, z);
                  return true;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   @Generated
   public double getXzRange() {
      return this.xzRange;
   }

   @Generated
   public double getYRange() {
      return this.yRange;
   }

   @Generated
   public boolean isOnWorldSurface() {
      return this.onWorldSurface;
   }

   @Generated
   public ResourceKey<Level> getDimension() {
      return this.dimension;
   }

   @Generated
   public WarpPoint.TransmissionType getType() {
      return this.type;
   }
}
