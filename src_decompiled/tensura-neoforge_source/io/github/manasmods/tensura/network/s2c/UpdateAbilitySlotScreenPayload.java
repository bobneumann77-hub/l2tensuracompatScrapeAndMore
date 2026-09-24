package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record UpdateAbilitySlotScreenPayload(int entityId, int slot, ResourceLocation ability) implements CustomPacketPayload {
   public static final Type<UpdateAbilitySlotScreenPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "update_ability_slot_screen"));
   public static final StreamCodec<FriendlyByteBuf, UpdateAbilitySlotScreenPayload> STREAM_CODEC = CustomPacketPayload.codec(
      UpdateAbilitySlotScreenPayload::encode, UpdateAbilitySlotScreenPayload::new
   );

   public UpdateAbilitySlotScreenPayload(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readInt(), buf.readResourceLocation());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeInt(this.slot);
      buf.writeResourceLocation(this.ability);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         context.queue(() -> ClientAccess.handleAbilitySlotUpdate(this));
      }
   }

   public Type<UpdateAbilitySlotScreenPayload> type() {
      return TYPE;
   }
}
