package io.github.manasmods.tensura.network.c2s;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.tensura.race.RaceHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.ExistenceStorage;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record RequestEvolutionPacket(RequestEvolutionPacket.Action action, ResourceLocation resourceLocation, boolean booleanValue)
   implements CustomPacketPayload {
   public static final Type<RequestEvolutionPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "request_evolution"));
   public static final StreamCodec<FriendlyByteBuf, RequestEvolutionPacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestEvolutionPacket::encode, RequestEvolutionPacket::new
   );

   public RequestEvolutionPacket(FriendlyByteBuf buf) {
      this((RequestEvolutionPacket.Action)buf.readEnum(RequestEvolutionPacket.Action.class), buf.readResourceLocation(), buf.readBoolean());
   }

   public static RequestEvolutionPacket getEvolutionPacket(ResourceLocation race) {
      return new RequestEvolutionPacket(RequestEvolutionPacket.Action.EVOLVE, race, false);
   }

   public static RequestEvolutionPacket getSetTrackedEvolutionPacket(ResourceLocation race) {
      return new RequestEvolutionPacket(RequestEvolutionPacket.Action.SET_TRACKED_RACE, race, false);
   }

   public static RequestEvolutionPacket getAwakeningPacket(boolean hero) {
      return new RequestEvolutionPacket(RequestEvolutionPacket.Action.AWAKEN, ResourceLocation.withDefaultNamespace("empty"), hero);
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(this.action);
      buf.writeResourceLocation(this.resourceLocation);
      buf.writeBoolean(this.booleanValue);
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(() -> {
            Player sender = context.getPlayer();
            if (sender != null) {
               ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(sender);
               Races raceData = RaceAPI.getRaceFrom(sender);
               ManasRaceInstance race = (ManasRaceInstance)raceData.getRace().orElse(null);
               if (race != null) {
                  switch (this.action) {
                     case SET_TRACKED_RACE:
                        ManasRace desiredRace = (ManasRace)RaceAPI.getRaceRegistry().get(this.resourceLocation);
                        if (desiredRace == null) {
                           return;
                        }

                        if (!race.getNextEvolutions(sender).contains(desiredRace)) {
                           return;
                        }

                        playerData.setTrackedEvolution(desiredRace);
                        break;
                     case EVOLVE:
                        ManasRace desiredRace = (ManasRace)RaceAPI.getRaceRegistry().get(this.resourceLocation);
                        if (desiredRace == null) {
                           return;
                        }

                        this.handleEvolution(sender, desiredRace, race);
                        break;
                     case AWAKEN:
                        this.handleAwakening(sender, this.booleanValue);
                  }

                  raceData.markDirty();
                  playerData.markDirty();
               }
            }
         });
      }
   }

   private void handleEvolution(Player player, ManasRace targetRace, ManasRaceInstance race) {
      if (race.getNextEvolutions(player).contains(targetRace)) {
         if (!(race.getEvolutionProgress(player, targetRace) < 100.0F)) {
            RaceHelper.evolveRace(player, targetRace, true);
            player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.MASTER, 1.0F, 1.0F);
         }
      }
   }

   private void handleAwakening(Player player, boolean hero) {
      if (RaceUtils.canAwaken(player, hero)) {
         if (hero) {
            RaceHelper.awakening(player, true);
         } else {
            ExistenceStorage.enterHarvestFestival(TensuraStorages.getExistenceFrom(player), player);
         }
      }
   }

   private enum Action {
      SET_TRACKED_RACE,
      EVOLVE,
      AWAKEN;
   }
}
