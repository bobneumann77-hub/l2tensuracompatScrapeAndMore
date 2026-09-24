package io.github.manasmods.tensura.network.c2s;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.utils.Env;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.menu.NamingMenu;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record RequestNamingKeyPacket() implements CustomPacketPayload {
   public static final Type<RequestNamingKeyPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "request_naming_key"));
   public static final StreamCodec<FriendlyByteBuf, RequestNamingKeyPacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestNamingKeyPacket::encode, RequestNamingKeyPacket::new
   );
   private static final Map<UUID, Integer> LAST_NAMING_KEY_TICK = Collections.synchronizedMap(new WeakHashMap<>());

   public RequestNamingKeyPacket(FriendlyByteBuf buf) {
      this();
   }

   public void encode(FriendlyByteBuf buf) {
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(
            () -> {
               ServerPlayer player = (ServerPlayer)context.getPlayer();
               if (player != null) {
                  int now = player.tickCount;
                  UUID uuid = player.getUUID();
                  Integer last = LAST_NAMING_KEY_TICK.get(uuid);
                  if (last == null || now < last || now - last >= 4) {
                     LAST_NAMING_KEY_TICK.put(uuid, now);
                     IExistence existence = TensuraStorages.getExistenceFrom(player);
                     if (player.isSecondaryUseActive()) {
                        boolean isNameable = existence.isNameable();
                        existence.setNameable(!isNameable);
                        Component component = Component.translatable(isNameable ? "tensura.message.disabled" : "tensura.message.enabled");
                        player.displayClientMessage(
                           Component.translatable("tensura.naming.nameable_status", new Object[]{component})
                              .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                           false
                        );
                        existence.markDirty();
                     } else {
                        LivingEntity sub = ObjectSelectionHelper.getTargetingEntity(player, 6.0, false);
                        if (sub != null && canName(player, sub)) {
                           MenuRegistry.openExtendedMenu(
                              player,
                              new SimpleMenuProvider((i, inventory, pPlayer) -> new NamingMenu(i, inventory, pPlayer, sub.getId()), Component.empty()),
                              buf -> buf.writeInt(sub.getId())
                           );
                        }
                     }
                  }
               }
            }
         );
      }
   }

   @NotNull
   public Type<RequestNamingKeyPacket> type() {
      return TYPE;
   }

   public static boolean canName(Player player, LivingEntity sub) {
      if (isNotNameable(sub, player)) {
         player.displayClientMessage(Component.translatable("tensura.naming.cannot_name").withStyle(ChatFormatting.RED), false);
         return false;
      }

      if (SubordinateHelper.isSubordinate(sub, player)) {
         player.displayClientMessage(Component.translatable("tensura.naming.name_owner").withStyle(ChatFormatting.RED), false);
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(sub);
      if (existence.getName() != null) {
         player.displayClientMessage(Component.translatable("tensura.naming.already_named").withStyle(ChatFormatting.RED), false);
         return false;
      }

      UUID owner = existence.getPermanentOwner();
      if (owner != null && !Objects.equals(owner, player.getUUID())) {
         player.displayClientMessage(Component.translatable("tensura.naming.had_owner").withStyle(ChatFormatting.RED), false);
         return false;
      }

      if (isNotSubmitting(sub, player)) {
         player.displayClientMessage(Component.translatable("tensura.naming.not_submit").withStyle(ChatFormatting.RED), false);
         return false;
      }

      if (player.isCreative()
         || !sub.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY))
            && !sub.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))) {
         if (!player.isCreative() && EnergyHelper.getMaxEP(sub) >= EnergyHelper.getBaseMaxEP(player)) {
            player.displayClientMessage(Component.translatable("tensura.naming.lack_EP").withStyle(ChatFormatting.RED), false);
            return false;
         } else {
            return true;
         }
      } else {
         player.displayClientMessage(Component.translatable("tensura.naming.insane").withStyle(ChatFormatting.RED), false);
         return false;
      }
   }

   private static boolean isNotNameable(LivingEntity sub, Player player) {
      if (!sub.isAlive()) {
         return true;
      } else {
         IExistence existence = TensuraStorages.getExistenceFrom(sub);
         if (sub instanceof Player) {
            return !existence.isNameable() ? true : !sub.level().getGameRules().getBoolean(TensuraGameRules.PLAYER_NAME);
         } else if (!sub.getType().is(TensuraEntityTags.NAMEABLE)) {
            return true;
         } else {
            return existence.getSummoner() != null && existence.getSummonedSecond() > 0
               ? true
               : sub instanceof INameEvolution ranking && !ranking.canBeNamed(player);
         }
      }
   }

   private static boolean isNotSubmitting(LivingEntity sub, Player player) {
      if (player.isCreative()) {
         return false;
      } else if (SubordinateHelper.isSubordinate(player, sub)) {
         return false;
      } else {
         MobEffectInstance instance = sub.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FEAR));
         if (instance != null && Objects.equals(instance.tensura$getSource(), player.getUUID())) {
            return false;
         } else {
            return sub.getHealth() <= sub.getMaxHealth() * NamingMenu.CONFIG.lowHPToName / 100.0
               ? false
               : EnergyHelper.getMaxEP(sub) > EnergyHelper.getMaxEP(player) * NamingMenu.CONFIG.maximumEPToName / 100.0;
         }
      }
   }
}
