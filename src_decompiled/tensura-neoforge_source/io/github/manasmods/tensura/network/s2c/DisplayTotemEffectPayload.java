package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record DisplayTotemEffectPayload(ResourceLocation item) implements CustomPacketPayload {
   public static final Type<DisplayTotemEffectPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "display_totem_effect"));
   public static final StreamCodec<FriendlyByteBuf, DisplayTotemEffectPayload> STREAM_CODEC = CustomPacketPayload.codec(
      DisplayTotemEffectPayload::encode, DisplayTotemEffectPayload::new
   );

   public DisplayTotemEffectPayload(FriendlyByteBuf buf) {
      this(buf.readResourceLocation());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeResourceLocation(this.item);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         context.queue(() -> ClientAccess.handle(this));
      }
   }

   public Type<DisplayTotemEffectPayload> type() {
      return TYPE;
   }
}
