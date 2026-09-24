package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.tensura.world.TensuraGameRules;
import lombok.Generated;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Key;

public record SendBooleanGameruleUpdatePayload(SendBooleanGameruleUpdatePayload.GameruleKey key, boolean value) implements CustomPacketPayload {
   public static final Type<SendBooleanGameruleUpdatePayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "send_gamerule_update_boolean"));
   public static final StreamCodec<FriendlyByteBuf, SendBooleanGameruleUpdatePayload> STREAM_CODEC = CustomPacketPayload.codec(
      SendBooleanGameruleUpdatePayload::encode, SendBooleanGameruleUpdatePayload::new
   );

   public SendBooleanGameruleUpdatePayload(FriendlyByteBuf buf) {
      this((SendBooleanGameruleUpdatePayload.GameruleKey)buf.readEnum(SendBooleanGameruleUpdatePayload.GameruleKey.class), buf.readBoolean());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(this.key);
      buf.writeBoolean(this.value);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         context.queue(() -> ClientAccess.updateGameruleClientSide(this));
      }
   }

   public Type<SendBooleanGameruleUpdatePayload> type() {
      return TYPE;
   }

   public enum GameruleKey {
      PLAYER_MANUAL_DODGING(TensuraGameRules.PLAYER_MANUAL_DODGING),
      TENSURA_NAME(TensuraGameRules.TENSURA_DISPLAY_NAME),
      DISABLE_NULLIFICATION(TensuraGameRules.DISABLE_NULLIFICATION);

      private final Key<BooleanValue> key;

      GameruleKey(Key<BooleanValue> key) {
         this.key = key;
      }

      @Generated
      public Key<BooleanValue> getKey() {
         return this.key;
      }
   }
}
