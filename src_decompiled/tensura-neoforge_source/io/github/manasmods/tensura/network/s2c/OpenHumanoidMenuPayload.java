package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record OpenHumanoidMenuPayload(int containerId, int size, int page, int entityId, int chestSlots) implements CustomPacketPayload {
   public static final Type<OpenHumanoidMenuPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "open_humanoid_menu"));
   public static final StreamCodec<FriendlyByteBuf, OpenHumanoidMenuPayload> STREAM_CODEC = CustomPacketPayload.codec(
      OpenHumanoidMenuPayload::encode, OpenHumanoidMenuPayload::new
   );

   public OpenHumanoidMenuPayload(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.containerId);
      buf.writeInt(this.size);
      buf.writeInt(this.page);
      buf.writeInt(this.entityId);
      buf.writeInt(this.chestSlots);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         ClientAccess.handleOpenHumanoidInventoryMenuPayload(this);
      }
   }

   public Type<OpenHumanoidMenuPayload> type() {
      return TYPE;
   }
}
