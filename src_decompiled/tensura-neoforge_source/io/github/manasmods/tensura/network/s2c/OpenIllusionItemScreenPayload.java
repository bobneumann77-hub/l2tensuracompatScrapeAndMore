package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record OpenIllusionItemScreenPayload(int entityId, ResourceLocation skill) implements CustomPacketPayload {
   public static final Type<OpenIllusionItemScreenPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "open_illusion_item_screen_menu"));
   public static final StreamCodec<FriendlyByteBuf, OpenIllusionItemScreenPayload> STREAM_CODEC = CustomPacketPayload.codec(
      OpenIllusionItemScreenPayload::encode, OpenIllusionItemScreenPayload::new
   );

   public OpenIllusionItemScreenPayload(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readResourceLocation());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeResourceLocation(this.skill);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         context.queue(() -> ClientAccess.handleOpenIllusionItemScreenPayload(this));
      }
   }

   public Type<OpenIllusionItemScreenPayload> type() {
      return TYPE;
   }
}
