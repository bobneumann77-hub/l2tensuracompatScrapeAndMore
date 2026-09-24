package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record OpenResearcherEnchantingMenuPayload(int containerId, int entityId, ResourceLocation skill) implements CustomPacketPayload {
   public static final Type<OpenResearcherEnchantingMenuPayload> TYPE = new Type(
      ResourceLocation.fromNamespaceAndPath("tensura", "open_researcher_enchanting_menu")
   );
   public static final StreamCodec<FriendlyByteBuf, OpenResearcherEnchantingMenuPayload> STREAM_CODEC = CustomPacketPayload.codec(
      OpenResearcherEnchantingMenuPayload::encode, OpenResearcherEnchantingMenuPayload::new
   );

   public OpenResearcherEnchantingMenuPayload(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readInt(), buf.readResourceLocation());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.containerId);
      buf.writeInt(this.entityId);
      buf.writeResourceLocation(this.skill);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         ClientAccess.handleOpenResearcherEnchantingMenuPayload(this);
      }
   }

   public Type<OpenResearcherEnchantingMenuPayload> type() {
      return TYPE;
   }
}
