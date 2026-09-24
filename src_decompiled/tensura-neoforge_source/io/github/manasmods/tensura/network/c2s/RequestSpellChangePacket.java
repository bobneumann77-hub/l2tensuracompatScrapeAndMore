package io.github.manasmods.tensura.network.c2s;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.item.weapon.spell.SimpleSpellCastItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record RequestSpellChangePacket(double delta, boolean modeChange, InteractionHand hand) implements CustomPacketPayload {
   public static final Type<RequestSpellChangePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "request_spell_change"));
   public static final StreamCodec<FriendlyByteBuf, RequestSpellChangePacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestSpellChangePacket::encode, RequestSpellChangePacket::new
   );

   public RequestSpellChangePacket(FriendlyByteBuf buf) {
      this(buf.readDouble(), buf.readBoolean(), (InteractionHand)buf.readEnum(InteractionHand.class));
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeDouble(this.delta);
      buf.writeBoolean(this.modeChange);
      buf.writeEnum(this.hand);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(() -> {
            ServerPlayer player = (ServerPlayer)context.getPlayer();
            if (player != null) {
               ItemStack stack = player.getItemInHand(this.hand);
               if (stack.is(TensuraItemTags.SPELL_CAST_WEAPONS) && stack.getItem() instanceof SimpleSpellCastItem castItem) {
                  if (this.modeChange) {
                     castItem.changeMode(player, stack);
                  } else {
                     castItem.changeMagic(player, stack, this.delta);
                  }
               }
            }
         });
      }
   }

   @NotNull
   public Type<RequestSpellChangePacket> type() {
      return TYPE;
   }
}
