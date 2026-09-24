package io.github.manasmods.tensura.network.c2s;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.tensura.menu.ResearcherEnchantingMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record RequestResearcherEnchantingPacket(int id, int level, boolean enchant) implements CustomPacketPayload {
   public static final Type<RequestResearcherEnchantingPacket> TYPE = new Type(
      ResourceLocation.fromNamespaceAndPath("tensura", "request_researcher_enchanting")
   );
   public static final StreamCodec<FriendlyByteBuf, RequestResearcherEnchantingPacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestResearcherEnchantingPacket::encode, RequestResearcherEnchantingPacket::new
   );

   public RequestResearcherEnchantingPacket(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readInt(), buf.readBoolean());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.id());
      buf.writeInt(this.level());
      buf.writeBoolean(this.enchant());
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(() -> {
            Player sender = context.getPlayer();
            if (sender != null && sender.containerMenu instanceof ResearcherEnchantingMenu menu) {
               if (this.enchant()) {
                  menu.handleFinalizeEnchantment();
               } else {
                  menu.editSelection(this.id(), this.level());
               }
            }
         });
      }
   }

   @NotNull
   public Type<RequestResearcherEnchantingPacket> type() {
      return TYPE;
   }
}
