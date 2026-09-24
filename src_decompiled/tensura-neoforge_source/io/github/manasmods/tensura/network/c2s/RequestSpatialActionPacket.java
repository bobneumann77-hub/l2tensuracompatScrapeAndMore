package io.github.manasmods.tensura.network.c2s;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.subclass.ISpatialMovement;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.misc.WarpPortalEntity;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public record RequestSpatialActionPacket(
   ResourceLocation skill, RequestSpatialActionPacket.Action action, int selectedWarp, double posX, double posY, double posZ, String name, String dimension
) implements CustomPacketPayload {
   public static final Type<RequestSpatialActionPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "request_spatial_action"));
   public static final StreamCodec<FriendlyByteBuf, RequestSpatialActionPacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestSpatialActionPacket::encode, RequestSpatialActionPacket::new
   );

   public RequestSpatialActionPacket(FriendlyByteBuf buf) {
      this(
         buf.readResourceLocation(),
         (RequestSpatialActionPacket.Action)buf.readEnum(RequestSpatialActionPacket.Action.class),
         buf.readInt(),
         buf.readDouble(),
         buf.readDouble(),
         buf.readDouble(),
         buf.readUtf(48),
         buf.readUtf(64)
      );
   }

   public static RequestSpatialActionPacket getWarpPacket(ManasSkill skill, double x, double y, double z, String dimension, boolean warpPad) {
      return new RequestSpatialActionPacket(skill.getRegistryName(), RequestSpatialActionPacket.Action.WARP, warpPad ? -2 : -1, x, y, z, "", dimension);
   }

   public static RequestSpatialActionPacket getPortalPacket(ManasSkill skill, double x, double y, double z, String dimension, boolean warpPad) {
      return new RequestSpatialActionPacket(skill.getRegistryName(), RequestSpatialActionPacket.Action.PORTAL, warpPad ? -2 : -1, x, y, z, "", dimension);
   }

   public static RequestSpatialActionPacket getDeletePacket(ManasSkill skill, int selectedWarp) {
      return new RequestSpatialActionPacket(skill.getRegistryName(), RequestSpatialActionPacket.Action.DELETE, selectedWarp, 0.0, 0.0, 0.0, "", "");
   }

   public static RequestSpatialActionPacket getDeletePadPacket(ManasSkill skill, int selectedWarp) {
      return new RequestSpatialActionPacket(skill.getRegistryName(), RequestSpatialActionPacket.Action.DELETE_PAD, selectedWarp, 0.0, 0.0, 0.0, "", "");
   }

   public static RequestSpatialActionPacket getRenamePacket(ManasSkill skill, int selectedWarp, String name) {
      return new RequestSpatialActionPacket(skill.getRegistryName(), RequestSpatialActionPacket.Action.RENAME, selectedWarp, 0.0, 0.0, 0.0, name, "");
   }

   public static RequestSpatialActionPacket getRenamePadPacket(ManasSkill skill, int selectedWarp, String name) {
      return new RequestSpatialActionPacket(skill.getRegistryName(), RequestSpatialActionPacket.Action.RENAME_PAD, selectedWarp, 0.0, 0.0, 0.0, name, "");
   }

   public static RequestSpatialActionPacket getSavePacket(ManasSkill skill, String name, double x, double y, double z, String dimension) {
      return new RequestSpatialActionPacket(skill.getRegistryName(), RequestSpatialActionPacket.Action.SAVE, -1, x, y, z, name, dimension);
   }

   public static RequestSpatialActionPacket getUpdatePacket(ManasSkill skill, int selectedWarp, double x, double y, double z, String dimension) {
      return new RequestSpatialActionPacket(skill.getRegistryName(), RequestSpatialActionPacket.Action.UPDATE, selectedWarp, x, y, z, "", dimension);
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeResourceLocation(this.skill);
      buf.writeEnum(this.action);
      buf.writeInt(this.selectedWarp);
      buf.writeDouble(this.posX);
      buf.writeDouble(this.posY);
      buf.writeDouble(this.posZ);
      buf.writeUtf(this.name);
      buf.writeUtf(this.dimension);
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(
            () -> {
               ServerPlayer player = (ServerPlayer)context.getPlayer();
               if (player != null) {
                  ManasSkill manasSkill = (ManasSkill)SkillAPI.getSkillRegistry().get(this.skill);
                  if (manasSkill instanceof ISpatialMovement spatial) {
                     ManasSkillInstance instance = (ManasSkillInstance)SkillAPI.getSkillsFrom(player).getSkill(this.skill).orElse(null);
                     if (instance != null) {
                        ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
                        switch (this.action) {
                           case WARP:
                              if (!spatial.canWarp(instance, player)) {
                                 return;
                              }

                              if (instance.onCoolDown(spatial.getSpatialMovementModeIndex())
                                 && !instance.canIgnoreCoolDown(player, spatial.getSpatialMovementModeIndex())) {
                                 return;
                              }

                              handleWarp(player, instance, spatial, this.posX, this.posY, this.posZ, getLevelKey(this.dimension), this.selectedWarp == -2);
                              break;
                           case PORTAL:
                              if (!spatial.canPortal(instance, player)) {
                                 return;
                              }

                              if (instance.onCoolDown(spatial.getSpatialMovementModeIndex())
                                 && !instance.canIgnoreCoolDown(player, spatial.getSpatialMovementModeIndex())) {
                                 return;
                              }

                              handlePortal(player, instance, spatial, this.posX, this.posY, this.posZ, getLevelKey(this.dimension), this.selectedWarp == -2);
                              break;
                           case SAVE:
                              if (data.getWarpPoints().size() >= data.getMaxWarpPoints()) {
                                 return;
                              }

                              if (spatial.getBlacklistDimensions().contains(this.dimension)) {
                                 player.displayClientMessage(
                                    Component.translatable("tensura.skill.teleport.warp_point.wrong_dimension").withStyle(ChatFormatting.RED), false
                                 );
                                 return;
                              }

                              data.addWarpPoint(this.name, this.posX, this.posY, this.posZ, getLevelKey(this.dimension));
                              break;
                           case DELETE:
                              data.removeWarpPoint(this.selectedWarp);
                              break;
                           case DELETE_PAD:
                              data.removeWarpPad(this.selectedWarp);
                              break;
                           case RENAME:
                              if (this.selectedWarp < 0 || this.selectedWarp >= data.getWarpPoints().size()) {
                                 return;
                              }

                              data.getWarpPoints().get(this.selectedWarp).setName(this.name);
                              break;
                           case RENAME_PAD:
                              if (this.selectedWarp < 0 || this.selectedWarp >= data.getWarpPads().size()) {
                                 return;
                              }

                              data.getWarpPads().get(this.selectedWarp).setName(this.name);
                              break;
                           case UPDATE:
                              if (this.selectedWarp < 0 || this.selectedWarp >= data.getWarpPoints().size()) {
                                 return;
                              }

                              if (spatial.getBlacklistDimensions().contains(this.dimension)) {
                                 player.displayClientMessage(
                                    Component.translatable("tensura.skill.teleport.warp_point.wrong_dimension").withStyle(ChatFormatting.RED), false
                                 );
                                 return;
                              }

                              WarpPoint warp = data.getWarpPoints().get(this.selectedWarp);
                              warp.setX(this.posX);
                              warp.setY(this.posY);
                              warp.setZ(this.posZ);
                              warp.setDimension(getLevelKey(this.dimension));
                        }

                        data.markDirty();
                     }
                  }
               }
            }
         );
      }
   }

   public static void handleWarp(
      ServerPlayer player,
      ManasSkillInstance instance,
      ISpatialMovement spatial,
      double posX,
      double posY,
      double posZ,
      ResourceKey<Level> targetLevel,
      boolean warpPad
   ) {
      if (spatial.getBlacklistDimensions().contains(targetLevel.registry().toString())) {
         player.displayClientMessage(Component.translatable("tensura.skill.teleport.warp_point.wrong_dimension").withStyle(ChatFormatting.RED), false);
      } else {
         boolean dimensionTravel = !player.level().dimension().equals(targetLevel);
         if (dimensionTravel && !spatial.canDimensionTravel(instance, player)) {
            player.displayClientMessage(Component.translatable("tensura.skill.teleport.warp_point.wrong_dimension").withStyle(ChatFormatting.RED), false);
         } else {
            if (warpPad) {
               MinecraftServer server = player.getServer();
               if (server != null) {
                  ServerLevel level = server.getLevel(targetLevel);
                  if (level == null) {
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.teleport.warp_point.wrong_dimension").withStyle(ChatFormatting.RED), false
                     );
                     return;
                  }

                  BlockPos pad = new BlockPos((int)posX, (int)posY, (int)posZ);
                  if (!level.getWorldBorder().isWithinBounds(pad)) {
                     player.sendSystemMessage(Component.translatable("tensura.skill.teleport.out_border").withStyle(ChatFormatting.RED));
                     return;
                  }

                  level.getChunk((int)posX >> 4, (int)posZ >> 4, ChunkStatus.FULL, true);
                  BlockState state = level.getBlockState(pad);
                  if (!state.is(TensuraBlockTags.WARP_PADS)) {
                     player.displayClientMessage(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED), false);
                     return;
                  }

                  posY += 0.5;
               }
            }

            double cost = spatial.getWarpCost(Math.sqrt(player.distanceToSqr(posX, posY, posZ)));
            if (dimensionTravel) {
               cost += spatial.getDimensionTravelCost(player.level().dimension(), targetLevel);
            }

            if (!EnergyHelper.isOutOfEnergy(player, 0.0, cost)) {
               int chargeTick = spatial.getWarpChargeTick(instance, player);
               if (chargeTick <= 0) {
                  ISpatialMovement.warp(player, null, posX, posY, posZ, targetLevel);
                  instance.addMasteryPoint(player);
               } else {
                  player.portalProcess = null;
                  MobEffectInstance warping = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.WARPING), chargeTick, 0, false, false, false
                  );
                  warping.tensura$getOrCreateTag().put("WarpPoint", new WarpPoint(posX, posY, posZ, targetLevel).serialize(player.registryAccess()));
                  TensuraMobEffect.addEffect(player, warping, player, instance.getSkill(), spatial.getSpatialMovementModeIndex());
               }

               instance.setCoolDown(spatial.getWarpCooldown(instance, player), spatial.getSpatialMovementModeIndex());
               player.closeContainer();
            }
         }
      }
   }

   public static void handlePortal(
      ServerPlayer player,
      ManasSkillInstance instance,
      ISpatialMovement spatial,
      double posX,
      double posY,
      double posZ,
      ResourceKey<Level> targetLevel,
      boolean warpPad
   ) {
      if (spatial.getBlacklistDimensions().contains(targetLevel.registry().toString())) {
         player.displayClientMessage(Component.translatable("tensura.skill.teleport.warp_point.wrong_dimension").withStyle(ChatFormatting.RED), false);
      } else {
         boolean dimensionTravel = !player.level().dimension().equals(targetLevel);
         if (dimensionTravel && !spatial.canDimensionTravel(instance, player)) {
            player.displayClientMessage(Component.translatable("tensura.skill.teleport.warp_point.wrong_dimension").withStyle(ChatFormatting.RED), false);
         } else {
            if (warpPad) {
               MinecraftServer server = player.getServer();
               if (server != null) {
                  ServerLevel level = server.getLevel(targetLevel);
                  if (level == null) {
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.teleport.warp_point.wrong_dimension").withStyle(ChatFormatting.RED), false
                     );
                     return;
                  }

                  BlockPos pad = new BlockPos((int)posX, (int)posY, (int)posZ);
                  if (!level.getWorldBorder().isWithinBounds(pad)) {
                     player.sendSystemMessage(Component.translatable("tensura.skill.teleport.out_border").withStyle(ChatFormatting.RED));
                     return;
                  }

                  level.getChunk((int)posX >> 4, (int)posZ >> 4, ChunkStatus.FULL, true);
                  BlockState state = level.getBlockState(pad);
                  if (!state.is(TensuraBlockTags.WARP_PADS)) {
                     player.displayClientMessage(Component.translatable("tensura.warp_pad.not_found").withStyle(ChatFormatting.RED), false);
                     return;
                  }

                  posY += 0.5;
               }
            }

            double cost = spatial.getPortalCost(Math.sqrt(player.distanceToSqr(posX, posY, posZ)));
            if (dimensionTravel) {
               cost += spatial.getDimensionTravelCost(player.level().dimension(), targetLevel);
            }

            if (!EnergyHelper.isOutOfEnergy(player, 0.0, cost)) {
               instance.addMasteryPoint(player);
               instance.setCoolDown(spatial.getWarpCooldown(instance, player), spatial.getSpatialMovementModeIndex());
               Level level = player.level();
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, player, Fluid.NONE, 3.0);
               level.playSound(
                  null, player.getX(), player.getY(), player.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), SoundSource.PLAYERS, 1.0F, 1.0F
               );
               WarpPortalEntity portal = new WarpPortalEntity(level, player);
               portal.setSkill(instance);
               portal.setMode(spatial.getSpatialMovementModeIndex());
               portal.setMpCost(cost);
               Direction direction = Direction.orderedByNearest(player)[0].getOpposite();
               if (direction == Direction.UP) {
                  direction = Direction.DOWN;
               }

               portal.setFacingDirection(direction);
               portal.setPos(result.getLocation().x(), result.getLocation().y(), result.getLocation().z());
               portal.setDestination(new WarpPoint(posX, posY, posZ, targetLevel));
               portal.setChargeTick(spatial.getWarpChargeTick(instance, player));
               level.addFreshEntity(portal);
               portal.setBoundingBox(portal.makeBoundingBox());
               portal.triggerAnim("controller", "start");
               player.closeContainer();
            }
         }
      }
   }

   private static ResourceKey<Level> getLevelKey(String dimension) {
      return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimension));
   }

   public enum Action {
      WARP,
      PORTAL,
      SAVE,
      DELETE,
      DELETE_PAD,
      RENAME,
      RENAME_PAD,
      UPDATE;
   }
}
