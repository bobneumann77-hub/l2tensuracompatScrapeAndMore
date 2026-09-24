package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record HurtLivingPartPayload(int part, int parent) implements CustomPacketPayload {
   public static final Type<HurtLivingPartPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "hurt_living_part"));
   public static final StreamCodec<FriendlyByteBuf, HurtLivingPartPayload> STREAM_CODEC = CustomPacketPayload.codec(
      HurtLivingPartPayload::encode, HurtLivingPartPayload::new
   );

   public HurtLivingPartPayload(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readInt());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.part);
      buf.writeInt(this.parent);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         context.queue(() -> ClientAccess.updatePartHurtAnimation(this));
      }
   }

   public Type<HurtLivingPartPayload> type() {
      return TYPE;
   }
}
