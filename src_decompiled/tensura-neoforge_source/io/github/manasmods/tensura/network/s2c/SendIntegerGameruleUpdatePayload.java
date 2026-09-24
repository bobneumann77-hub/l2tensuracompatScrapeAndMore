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
import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Key;

public record SendIntegerGameruleUpdatePayload(SendIntegerGameruleUpdatePayload.GameruleKey key, int value) implements CustomPacketPayload {
   public static final Type<SendIntegerGameruleUpdatePayload> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "send_gamerule_update_integer"));
   public static final StreamCodec<FriendlyByteBuf, SendIntegerGameruleUpdatePayload> STREAM_CODEC = CustomPacketPayload.codec(
      SendIntegerGameruleUpdatePayload::encode, SendIntegerGameruleUpdatePayload::new
   );

   public SendIntegerGameruleUpdatePayload(FriendlyByteBuf buf) {
      this((SendIntegerGameruleUpdatePayload.GameruleKey)buf.readEnum(SendIntegerGameruleUpdatePayload.GameruleKey.class), buf.readInt());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(this.key);
      buf.writeInt(this.value);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         context.queue(() -> ClientAccess.updateGameruleClientSide(this));
      }
   }

   public Type<SendIntegerGameruleUpdatePayload> type() {
      return TYPE;
   }

   public enum GameruleKey {
      AWAKEN_SOUL(TensuraGameRules.DEMON_LORD_AWAKEN),
      RESET_PER_SKILL_LOCK(TensuraGameRules.RESET_PER_SKILL_LOCK),
      RESET_INCOMPLETE_PENALTY(TensuraGameRules.RESET_INCOMPLETE_PENALTY);

      private final Key<IntegerValue> key;

      GameruleKey(Key<IntegerValue> key) {
         this.key = key;
      }

      @Generated
      public Key<IntegerValue> getKey() {
         return this.key;
      }
   }
}
