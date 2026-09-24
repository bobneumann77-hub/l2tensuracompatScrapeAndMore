package io.github.manasmods.tensura.network.c2s;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.config.ability.AbilityConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import lombok.Generated;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record RequestDodgePacket(RequestDodgePacket.DodgeDirection direction) implements CustomPacketPayload {
   public static final Type<RequestDodgePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "request_dodge"));
   public static final StreamCodec<FriendlyByteBuf, RequestDodgePacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestDodgePacket::encode, RequestDodgePacket::new
   );
   private static final Map<UUID, Integer> LAST_DODGE_TICK = Collections.synchronizedMap(new WeakHashMap<>());

   public RequestDodgePacket(FriendlyByteBuf buf) {
      this((RequestDodgePacket.DodgeDirection)buf.readEnum(RequestDodgePacket.DodgeDirection.class));
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(this.direction);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(
            () -> {
               ServerPlayer player = (ServerPlayer)context.getPlayer();
               if (player != null) {
                  int now = player.tickCount;
                  UUID uuid = player.getUUID();
                  Integer last = LAST_DODGE_TICK.get(uuid);
                  if (last == null || now < last || now - last >= 2) {
                     LAST_DODGE_TICK.put(uuid, now);
                     if (canDodge(player)) {
                        AbilityConfig.Dodge dodgeConfig = TensuraSkill.BASE_CONFIG.Dodge;
                        double power = player.getAttributeValue(TensuraAttributes.DODGE_STRENGTH);
                        Vec3 look = player.getLookAngle()
                           .multiply(1.0, 0.0, 1.0)
                           .normalize()
                           .scale(power + power <= 0.0 ? dodgeConfig.weakDodgeStrength : dodgeConfig.strongDodgeStrength);
                        double jump = player.getAttributeValue(Attributes.JUMP_STRENGTH) * dodgeConfig.verticalDodgeMultiplier;
                        Vec3 forwards = new Vec3(look.x, jump, look.z);
                        Vec3 backwards = new Vec3(-look.x, jump, -look.z);
                        Vec3 left = new Vec3(look.z, jump, -look.x);
                        Vec3 right = new Vec3(-look.z, jump, look.x);
                        Vec3 forwardsLeft = forwards.add(left).scale(0.5);
                        Vec3 forwardsRight = forwards.add(right).scale(0.5);
                        Vec3 backwardsLeft = backwards.add(left).scale(0.5);
                        Vec3 backwardsRight = backwards.add(right).scale(0.5);
                        switch (this.direction) {
                           case FORWARD:
                              player.push(forwards.x, forwards.y, forwards.z);
                              break;
                           case BACKWARD:
                              player.push(backwards.x, backwards.y, backwards.z);
                              break;
                           case LEFT:
                              player.push(left.x, left.y, left.z);
                              break;
                           case RIGHT:
                              player.push(right.x, right.y, right.z);
                              break;
                           case FORWARD_LEFT:
                              player.push(forwardsLeft.x, forwardsLeft.y, forwardsLeft.z);
                              break;
                           case FORWARD_RIGHT:
                              player.push(forwardsRight.x, forwardsRight.y, forwardsRight.z);
                              break;
                           case BACKWARD_LEFT:
                              player.push(backwardsLeft.x, backwardsLeft.y, backwardsLeft.z);
                              break;
                           case BACKWARD_RIGHT:
                              player.push(backwardsRight.x, backwardsRight.y, backwardsRight.z);
                        }

                        player.hurtMarked = true;
                        player.resetAttackStrengthTicker();
                        player.level()
                           .playSound(
                              null,
                              player.getX(),
                              player.getY(),
                              player.getZ(),
                              (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(),
                              SoundSource.PLAYERS,
                              0.5F,
                              1.0F
                           );
                        ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
                        data.setDodgeCooldown(dodgeConfig.dodgeCooldown);
                        data.setDodgeInvulnerability((int)player.getAttributeValue(TensuraAttributes.DODGE_INVULNERABILITY));
                        data.markDirty();
                     }
                  }
               }
            }
         );
      }
   }

   public static boolean canDodge(Player player) {
      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
      if (data != null && data.getDodgeCooldown() > 0) {
         return false;
      } else if (!player.level().getGameRules().getBoolean(TensuraGameRules.PLAYER_MANUAL_DODGING)) {
         return false;
      } else {
         return !player.onGround() && !player.getAbilities().flying
            ? false
            : !player.isPassenger()
               && !player.isCrouching()
               && !player.isSwimming()
               && !player.isUsingItem()
               && player.getAttributeValue(Attributes.MOVEMENT_SPEED) > 0.0;
      }
   }

   @NotNull
   public Type<RequestDodgePacket> type() {
      return TYPE;
   }

   public enum DodgeDirection {
      FORWARD,
      BACKWARD,
      LEFT,
      RIGHT,
      FORWARD_LEFT,
      FORWARD_RIGHT,
      BACKWARD_LEFT,
      BACKWARD_RIGHT;
   }
}
