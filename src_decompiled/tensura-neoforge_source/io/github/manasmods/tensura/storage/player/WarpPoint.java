package io.github.manasmods.tensura.storage.player;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.SpawnPointHelper;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarpPoint {
   private String name;
   private double x;
   private double y;
   private double z;
   private ResourceKey<Level> dimension;

   public WarpPoint(double x, double y, double z) {
      this.name = "Warp Point";
      this.x = x;
      this.y = y;
      this.z = z;
      this.dimension = Level.OVERWORLD;
   }

   public WarpPoint(String name, double x, double y, double z) {
      this.name = name;
      this.x = x;
      this.y = y;
      this.z = z;
      this.dimension = Level.OVERWORLD;
   }

   public WarpPoint(double x, double y, double z, ResourceKey<Level> dimension) {
      this.name = "Warp Point";
      this.x = x;
      this.y = y;
      this.z = z;
      this.dimension = dimension;
   }

   public WarpPoint(String name, double x, double y, double z, ResourceKey<Level> dimension) {
      this.name = name;
      this.x = x;
      this.y = y;
      this.z = z;
      this.dimension = dimension;
   }

   public final CompoundTag serialize(Provider registries) {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("name", this.name);
      nbt.putDouble("x", this.x);
      nbt.putDouble("y", this.y);
      nbt.putDouble("z", this.z);
      nbt.put("dimension", writeLevelTag(this.dimension, registries));
      return nbt;
   }

   public static WarpPoint fromNBT(CompoundTag tag, Provider registries) {
      return new WarpPoint(tag.getString("name"), tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"), getLevel(tag.get("dimension"), registries));
   }

   public static Tag writeLevelTag(ResourceKey<Level> key, Provider registries) {
      DataResult<Tag> result = Level.RESOURCE_KEY_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), key);
      return (Tag)result.result().orElse(new CompoundTag());
   }

   public static ResourceKey<Level> getLevel(Tag compound, Provider registries) {
      DataResult<ResourceKey<Level>> result = Level.RESOURCE_KEY_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), compound);
      return result.result().isPresent() ? (ResourceKey)result.result().get() : Level.OVERWORLD;
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("name", this.name);
      json.addProperty("x", this.x);
      json.addProperty("y", this.y);
      json.addProperty("z", this.z);
      json.addProperty("dimension", this.dimension.location().toString());
      return json;
   }

   public static WarpPoint fromJson(JsonObject json) {
      String name = json.get("name").getAsString();
      double x = json.get("x").getAsDouble();
      double y = json.get("y").getAsDouble();
      double z = json.get("z").getAsDouble();
      ResourceLocation dimLocation = ResourceLocation.parse(json.get("dimension").getAsString());
      ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, dimLocation);
      return new WarpPoint(name, x, y, z, dimension);
   }

   public void warp(Entity target) {
      this.warp(target, target, false, false);
   }

   public void warp(Entity target, @Nullable Entity warper) {
      this.warp(target, warper, false, false);
   }

   public void warp(Entity target, @Nullable Entity warper, boolean message, boolean allowDimensionWarp) {
      this.warp(target, warper, WarpPoint.TransmissionType.ABILITY, message, allowDimensionWarp);
   }

   public void warp(Entity target, @Nullable Entity warper, WarpPoint.TransmissionType type, boolean message, boolean allowDimensionWarp) {
      Level level = target.level();
      boolean sameDim = level.dimension() == this.getDimension();
      if (!sameDim) {
         if (!allowDimensionWarp) {
            target.sendSystemMessage(Component.translatable("tensura.skill.teleport.warp_point.wrong_dimension").withStyle(ChatFormatting.RED));
            return;
         }

         if (((TensuraEntityEvents.DimensionTravelEvent)TensuraEntityEvents.DIMENSION_TRAVEL_EVENT.invoker())
            .travel(target, warper, this.getDimension())
            .isFalse()) {
            return;
         }
      }

      Changeable<Vec3> position = Changeable.of(new Vec3(this.x, this.y, this.z));
      if (!((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
         .transmission(target, warper, position, type)
         .isFalse()) {
         Vec3 pos = (Vec3)position.get();
         double x = pos.x();
         double y = pos.y();
         double z = pos.z();
         if (!level.getWorldBorder().isWithinBounds(new BlockPos((int)x, (int)y, (int)z))) {
            target.sendSystemMessage(Component.translatable("tensura.skill.teleport.out_border").withStyle(ChatFormatting.RED));
         } else {
            target.unRide();
            target.resetFallDistance();
            if (sameDim) {
               target.teleportTo(x, y, z);
            } else {
               SpawnPointHelper.teleportToAcrossDimensions(target, this.getDimension(), x, y, z, target.getYRot(), target.getXRot());
            }

            if (message) {
               target.sendSystemMessage(
                  Component.translatable("tensura.skill.teleport.warp_point.success", new Object[]{this.name})
                     .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
               );
            }
         }
      }
   }

   public boolean isSame(WarpPoint other) {
      return this.x == other.x && this.y == other.y && this.z == other.z && this.dimension.equals(other.dimension);
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public double getX() {
      return this.x;
   }

   @Generated
   public double getY() {
      return this.y;
   }

   @Generated
   public double getZ() {
      return this.z;
   }

   @Generated
   public ResourceKey<Level> getDimension() {
      return this.dimension;
   }

   @Generated
   public void setName(String name) {
      this.name = name;
   }

   @Generated
   public void setX(double x) {
      this.x = x;
   }

   @Generated
   public void setY(double y) {
      this.y = y;
   }

   @Generated
   public void setZ(double z) {
      this.z = z;
   }

   @Generated
   public void setDimension(ResourceKey<Level> dimension) {
      this.dimension = dimension;
   }

   public enum TransmissionType implements StringRepresentable {
      ABILITY("ability"),
      INSTANT_MOVE("instant_move"),
      ENDER("ender"),
      PEARL("pearl"),
      CHORUS("chorus"),
      FORCE_EXIT("force_exit"),
      COMMANDS("commands");

      public static final Codec<WarpPoint.TransmissionType> CODEC = StringRepresentable.fromEnum(WarpPoint.TransmissionType::values);
      private final String namespace;

      @NotNull
      public String getSerializedName() {
         return this.namespace;
      }

      @Generated
      TransmissionType(final String namespace) {
         this.namespace = namespace;
      }
   }
}
