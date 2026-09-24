package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record OpenDegenerateMenuPayload(OpenDegenerateMenuPayload.MenuType storageType, int containerId, int entityId, ResourceLocation skill)
   implements CustomPacketPayload {
   public static final Type<OpenDegenerateMenuPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "open_degenerate_menu"));
   public static final StreamCodec<FriendlyByteBuf, OpenDegenerateMenuPayload> STREAM_CODEC = CustomPacketPayload.codec(
      OpenDegenerateMenuPayload::encode, OpenDegenerateMenuPayload::new
   );

   public OpenDegenerateMenuPayload(FriendlyByteBuf buf) {
      this((OpenDegenerateMenuPayload.MenuType)buf.readEnum(OpenDegenerateMenuPayload.MenuType.class), buf.readInt(), buf.readInt(), buf.readResourceLocation());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(this.storageType);
      buf.writeInt(this.containerId);
      buf.writeInt(this.entityId);
      buf.writeResourceLocation(this.skill);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         switch (this.storageType()) {
            case CRAFTING:
               ClientAccess.handleOpenUncraftingMenuPayload(this);
               break;
            case ENCHANTING:
               ClientAccess.handleOpenSynthesisSeparationMenuPayload(this);
         }
      }
   }

   public Type<OpenDegenerateMenuPayload> type() {
      return TYPE;
   }

   public enum MenuType {
      CRAFTING,
      ENCHANTING;
   }
}
