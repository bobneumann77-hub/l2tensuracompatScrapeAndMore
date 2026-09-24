package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record DisplayTotemStackEffectPayload(CompoundTag item) implements CustomPacketPayload {
   public static final Type<DisplayTotemStackEffectPayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "display_totem_stack_effect"));
   public static final StreamCodec<FriendlyByteBuf, DisplayTotemStackEffectPayload> STREAM_CODEC = CustomPacketPayload.codec(
      DisplayTotemStackEffectPayload::encode, DisplayTotemStackEffectPayload::new
   );

   public DisplayTotemStackEffectPayload(FriendlyByteBuf buf) {
      this((CompoundTag)buf.readNbt(NbtAccounter.create(8192L)));
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeNbt(this.item);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         context.queue(() -> ClientAccess.handle(this));
      }
   }

   public Type<DisplayTotemStackEffectPayload> type() {
      return TYPE;
   }
}
