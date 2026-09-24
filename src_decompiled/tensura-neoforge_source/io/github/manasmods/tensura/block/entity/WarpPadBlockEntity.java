package io.github.manasmods.tensura.block.entity;

import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.block.WarpPadBlock;
import io.github.manasmods.tensura.block.part.WarpPadPart;
import io.github.manasmods.tensura.entity.human.DwarfEntity;
import io.github.manasmods.tensura.entity.magic.misc.WarpPortalEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.boss.exit.IForceExitHandler;
import io.github.manasmods.tensura.storage.boss.template.BossFightInstance;
import io.github.manasmods.tensura.storage.boss.template.IBossFightHolder;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Portal.Transition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarpPadBlockEntity extends BlockEntity implements Portal {
   @Nullable
   private IForceExitHandler forceExitHandler = null;
   @Nullable
   private String bossFightId = null;
   private int warpTime = 20;
   private double magiculeCost = 20.0;
   public boolean needUpdate = false;
   private AABB cachedAabb;
   private BlockState lastAabbState;
   private Predicate<LivingEntity> cachedPredicate;

   public WarpPadBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType)TensuraBlockEntities.WARP_PAD.get(), pPos, pBlockState);
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      tag.putInt("warpTime", this.warpTime);
      tag.putDouble("magiculeCost", this.magiculeCost);
      if (this.getForceExitHandler() != null) {
         tag.put("forceExitHandler", this.getForceExitHandler().toNBT(provider));
      }

      if (this.getBossFightId() != null) {
         tag.putString("bossFightId", this.getBossFightId());
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.warpTime = tag.getInt("warpTime");
      this.magiculeCost = tag.getDouble("magiculeCost");
      if (tag.contains("forceExitHandler")) {
         this.setForceExitHandler(IForceExitHandler.fromNBT(tag.getCompound("forceExitHandler"), provider));
      }

      if (tag.contains("bossFightId")) {
         this.setBossFightId(tag.getString("bossFightId"));
      }
   }

   public void setForceExitHandler(IForceExitHandler forceExitHandler) {
      this.forceExitHandler = forceExitHandler;
      this.needUpdate = true;
   }

   public void setBossFightId(String bossFightId) {
      this.bossFightId = bossFightId;
      this.needUpdate = true;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = new CompoundTag();
      if (this.bossFightId != null) {
         tag.putString("bossFightId", this.bossFightId);
      }

      if (this.getForceExitHandler() != null) {
         tag.put("forceExitHandler", this.getForceExitHandler().toNBT(provider));
      }

      return tag;
   }

   @NotNull
   public Transition getLocalTransition() {
      return Transition.CONFUSION;
   }

   public int getPortalTransitionTime(ServerLevel serverLevel, Entity entity) {
      return this.getWarpTime();
   }

   @Nullable
   public DimensionTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos) {
      Vec3 pos = entity.position();
      if (this.getBossFightId() != null) {
         MinecraftServer server = serverLevel.getServer();
         IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(server.overworld());
         if (bossFightHolder == null) {
            return null;
         }

         BossFightInstance instance = bossFightHolder.getBossFight(this.getBossFightId());
         if (instance == null) {
            return null;
         }

         if (instance.getEntrance() != null) {
            serverLevel.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.PLAYER_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.0F);
            if (entity instanceof ServerPlayer player) {
               if (instance.joinBossFight(player, server.getLevel(instance.getDimension()), this.getBossFightId(), false)) {
                  entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.0F);
               }
            } else if (!instance.isOnHold()) {
               BossFightInstance.teleportToEntrance(entity, instance);
               entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }

            entity.portalProcess = null;
            return null;
         }

         if (entity instanceof ServerPlayer player) {
            instance.joinBossFight(player, server.getLevel(instance.getDimension()), this.getBossFightId(), false);
         }
      }

      if (this.getForceExitHandler() != null) {
         serverLevel.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.PLAYER_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.0F);
         if (!this.getForceExitHandler().apply(entity, entity.getType().equals(EntityType.PLAYER) ? this.getMagiculeCost() : 0.0)) {
            entity.portalProcess = null;
            return null;
         } else {
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.0F);
            entity.portalProcess = null;
            return null;
         }
      } else {
         return null;
      }
   }

   public static AABB getTeleportingBox(BlockPos pos, BlockState state) {
      WarpPadPart part = (WarpPadPart)state.getValue(WarpPadBlock.PART);
      BlockPos origin = pos.offset(-part.dx, 0, -part.dz);
      int size = part.size;
      double inset = size == 3 ? 0.5 : 0.0;
      return new AABB(
         origin.getX() + inset, origin.getY(), origin.getZ() + inset, origin.getX() + size - inset, origin.getY() + 2.0, origin.getZ() + size - inset
      );
   }

   public static Vec3 getWarpCenter(BlockPos pos, BlockState state) {
      return getTeleportingBox(pos, state).getBottomCenter();
   }

   public static void tick(Level level, BlockPos pos, BlockState state, WarpPadBlockEntity pad) {
      if (pad.getForceExitHandler() != null || pad.getBossFightId() != null) {
         if (!level.isClientSide() && pad.needUpdate) {
            pad.setChanged();
            level.sendBlockUpdated(pos, state, state, 2);
            pad.needUpdate = false;
         }

         if (pad.cachedAabb == null || state != pad.lastAabbState) {
            pad.cachedAabb = getTeleportingBox(pos, state);
            pad.lastAabbState = state;
         }

         AABB aabb = pad.cachedAabb;
         if (pad.cachedPredicate == null) {
            pad.cachedPredicate = getPadPredicate(pad);
         }

         List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb, pad.cachedPredicate);
         if (!entities.isEmpty()) {
            for (LivingEntity entity : entities) {
               WarpPortalEntity.setInsidePortal(entity, pad, pos, 10);
            }

            TensuraParticleHelper.spawnEnchantingTableParticle(level, aabb.getBottomCenter(), ParticleTypes.PORTAL, pad.getWarpTime() + 1);
         }
      }
   }

   private static Predicate<LivingEntity> getPadPredicate(WarpPadBlockEntity pad) {
      return entity -> {
         if (!entity.onGround() || entity.noPhysics) {
            return false;
         }

         if (!entity.canUsePortal(false)) {
            return false;
         }

         if (entity instanceof Player player) {
            if (!entity.isShiftKeyDown()) {
               return false;
            }

            if (Objects.equals(pad.getBossFightId(), "GazelDwargoArena")) {
               ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(entity);
               double rep = playerData.getReputation((EntityType<?>)HumanEntityTypes.DWARF.get());
               if (!(rep >= DwarfEntity.REPUTATION_CONFIG.maxReputation) && !(rep <= DwarfEntity.REPUTATION_CONFIG.minReputation)) {
                  player.displayClientMessage(
                     Component.translatable("tensura.message.dwarf.wrong_reputation", new Object[]{SkillUtils.ROUND_DOUBLE.format(rep)})
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
                     true
                  );
                  return false;
               }

               int gazelKill = 0;
               if (player.isLocalPlayer()) {
                  gazelKill = ((LocalPlayer)entity)
                     .getStats()
                     .getValue(((StatType)TensuraStats.BOSS_KILLED.get()).get((EntityType)HumanEntityTypes.GAZEL_DWARGO.get()));
               } else if (entity instanceof ServerPlayer serverPlayer) {
                  gazelKill = serverPlayer.getStats().getValue(((StatType)TensuraStats.BOSS_KILLED.get()).get((EntityType)HumanEntityTypes.GAZEL_DWARGO.get()));
               }

               if (gazelKill > 0 && rep <= DwarfEntity.REPUTATION_CONFIG.minReputation) {
                  player.displayClientMessage(Component.translatable("tensura.message.dwarf.king.defeat.negative.retry").withStyle(ChatFormatting.RED), true);
                  return false;
               } else {
                  return true;
               }
            } else {
               return true;
            }
         } else {
            return !Objects.equals(pad.getBossFightId(), "GazelDwargoArena");
         }
      };
   }

   @Nullable
   @Generated
   public IForceExitHandler getForceExitHandler() {
      return this.forceExitHandler;
   }

   @Nullable
   @Generated
   public String getBossFightId() {
      return this.bossFightId;
   }

   @Generated
   public int getWarpTime() {
      return this.warpTime;
   }

   @Generated
   public void setWarpTime(int warpTime) {
      this.warpTime = warpTime;
   }

   @Generated
   public double getMagiculeCost() {
      return this.magiculeCost;
   }

   @Generated
   public void setMagiculeCost(double magiculeCost) {
      this.magiculeCost = magiculeCost;
   }
}
