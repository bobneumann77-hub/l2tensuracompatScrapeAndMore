package io.github.manasmods.tensura.ability.subclass;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.SpawnPointHelper;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.network.s2c.OpenSpatialMovementMenuPayload;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface ISpatialMovement {
   default boolean canWarp(ManasSkillInstance instance, LivingEntity entity) {
      return true;
   }

   default boolean canPortal(ManasSkillInstance instance, LivingEntity entity) {
      return true;
   }

   default boolean canDimensionTravel(ManasSkillInstance instance, LivingEntity entity) {
      return false;
   }

   default double getWarpCost(double distance) {
      return 10.0;
   }

   default double getPortalCost(double distance) {
      return 50.0;
   }

   default double getDimensionTravelCost(ResourceKey<Level> from, ResourceKey<Level> to) {
      return 10000.0;
   }

   default List<String> getBlacklistDimensions() {
      return List.of("tensura:labyrinth", "tensura:boss_area");
   }

   default int getSpatialMovementModeIndex() {
      return 0;
   }

   default int getWarpChargeTick(ManasSkillInstance instance, LivingEntity entity) {
      return 0;
   }

   default int getWarpCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return 0;
   }

   default void openSpatialMovementMenu(ServerPlayer player, ManasSkill skill) {
      if (player.level().dimension() == TensuraDimensions.LABYRINTH) {
         player.playNotifySound(SoundEvents.PLAYER_ATTACK_SWEEP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
      } else {
         List<String> dimensionLocations = new ArrayList<>();
         player.server
            .registryAccess()
            .registry(Registries.DIMENSION)
            .ifPresent(
               registry -> dimensionLocations.addAll(
                  registry.keySet().stream().map(ResourceLocation::toString).filter(string -> !this.getBlacklistDimensions().contains(string)).toList()
               )
            );
         NetworkManager.sendToPlayer(player, new OpenSpatialMovementMenuPayload(player.getId(), skill.getRegistryName(), dimensionLocations));
         player.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F);
      }
   }

   static void warp(LivingEntity living, double x, double y, double z) {
      warp(living, living, x, y, z, living.level().dimension());
   }

   static void warp(LivingEntity target, @Nullable Entity warper, double x, double y, double z, ResourceKey<Level> dimension) {
      Level level = target.level();
      if (level.dimension() != dimension) {
         if (!((TensuraEntityEvents.DimensionTravelEvent)TensuraEntityEvents.DIMENSION_TRAVEL_EVENT.invoker()).travel(target, warper, dimension).isFalse()) {
            Changeable<Vec3> position = Changeable.of(new Vec3(x, y, z));
            if (!((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
               .transmission(target, warper, position, WarpPoint.TransmissionType.ABILITY)
               .isFalse()) {
               x = ((Vec3)position.get()).x();
               y = ((Vec3)position.get()).y();
               z = ((Vec3)position.get()).z();
               target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.PORTAL, 1.0);
               if (level.getWorldBorder().isWithinBounds(new BlockPos((int)x, (int)y, (int)z))) {
                  target.unRide();
                  target.resetFallDistance();
                  SpawnPointHelper.teleportToAcrossDimensions(target, dimension, x, y, z, target.getYRot(), target.getXRot());
               } else {
                  target.sendSystemMessage(Component.translatable("tensura.skill.teleport.out_border").withStyle(ChatFormatting.RED));
               }

               TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.PORTAL, 1.0);
               target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }
         }
      } else {
         Changeable<Vec3> position = Changeable.of(new Vec3(x, y, z));
         if (!((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
            .transmission(target, warper, position, WarpPoint.TransmissionType.ABILITY)
            .isFalse()) {
            x = ((Vec3)position.get()).x();
            y = ((Vec3)position.get()).y();
            z = ((Vec3)position.get()).z();
            target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.PORTAL, 1.0);
            if (target.level().getWorldBorder().isWithinBounds(new BlockPos((int)x, (int)y, (int)z))) {
               target.unRide();
               target.resetFallDistance();
               target.teleportTo(x, y, z);
            } else if (warper != null) {
               warper.sendSystemMessage(Component.translatable("tensura.skill.teleport.out_border").withStyle(ChatFormatting.RED));
            }

            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.PORTAL, 1.0);
            target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }
}
