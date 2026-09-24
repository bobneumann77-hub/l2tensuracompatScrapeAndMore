package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record OpenSpatialMovementMenuPayload(int entityId, ResourceLocation skill, List<String> dimensionLocations) implements CustomPacketPayload {
   public static final Type<OpenSpatialMovementMenuPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "open_spatial_movement_menu"));
   public static final StreamCodec<FriendlyByteBuf, OpenSpatialMovementMenuPayload> STREAM_CODEC = CustomPacketPayload.codec(
      OpenSpatialMovementMenuPayload::encode, OpenSpatialMovementMenuPayload::new
   );

   public OpenSpatialMovementMenuPayload(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readResourceLocation(), buf.readList(FriendlyByteBuf::readUtf));
   }

   public OpenSpatialMovementMenuPayload(int entityId, ResourceLocation skill) {
      this(entityId, skill, new ArrayList<>());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeResourceLocation(this.skill);
      buf.writeVarInt(this.dimensionLocations.size());
      this.dimensionLocations.forEach(buf::writeUtf);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         context.queue(() -> ClientAccess.handleOpenSpatialMovementMenuPayload(this));
      }
   }

   @NotNull
   public Type<OpenSpatialMovementMenuPayload> type() {
      return TYPE;
   }
}
