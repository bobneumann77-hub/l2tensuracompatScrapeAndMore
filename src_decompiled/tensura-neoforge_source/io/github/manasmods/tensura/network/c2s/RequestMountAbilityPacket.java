package io.github.manasmods.tensura.network.c2s;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public record RequestMountAbilityPacket(double alternative) implements CustomPacketPayload {
   public static final Type<RequestMountAbilityPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "request_mount_ability"));
   public static final StreamCodec<FriendlyByteBuf, RequestMountAbilityPacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestMountAbilityPacket::encode, RequestMountAbilityPacket::new
   );

   public RequestMountAbilityPacket(FriendlyByteBuf buf) {
      this(buf.readDouble());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeDouble(this.alternative);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(
            () -> {
               ServerPlayer player = (ServerPlayer)context.getPlayer();
               if (player != null) {
                  if (player.getVehicle() instanceof ITensuraMount mount
                     && mount instanceof LivingEntity entity
                     && Objects.equals(SubordinateHelper.getSubordinateOwnerUUID(entity), player.getUUID())) {
                     mount.mountScrollAbility(player, this.alternative);
                  }
               }
            }
         );
      }
   }

   @NotNull
   public Type<RequestMountAbilityPacket> type() {
      return TYPE;
   }
}
