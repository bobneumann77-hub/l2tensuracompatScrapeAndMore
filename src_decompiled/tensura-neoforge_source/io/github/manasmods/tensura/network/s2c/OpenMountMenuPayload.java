package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record OpenMountMenuPayload(int containerId, int size, int page, int entityId, boolean saddle, boolean armor, boolean weapon, int chestSlots)
   implements CustomPacketPayload {
   public static final Type<OpenMountMenuPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "open_mount_menu"));
   public static final StreamCodec<FriendlyByteBuf, OpenMountMenuPayload> STREAM_CODEC = CustomPacketPayload.codec(
      OpenMountMenuPayload::encode, OpenMountMenuPayload::new
   );

   public OpenMountMenuPayload(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readInt());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.containerId);
      buf.writeInt(this.size);
      buf.writeInt(this.page);
      buf.writeInt(this.entityId);
      buf.writeBoolean(this.saddle);
      buf.writeBoolean(this.armor);
      buf.writeBoolean(this.weapon);
      buf.writeInt(this.chestSlots);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         ClientAccess.handleOpenMountMenuPayload(this);
      }
   }

   public Type<OpenMountMenuPayload> type() {
      return TYPE;
   }
}
