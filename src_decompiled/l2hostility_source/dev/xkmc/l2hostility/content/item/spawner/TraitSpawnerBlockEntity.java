package dev.xkmc.l2hostility.content.item.spawner;

import dev.xkmc.l2core.base.tile.BaseBlockEntity;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2modularblock.tile_api.TickableBlockEntity;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@SerialClass
public abstract class TraitSpawnerBlockEntity extends BaseBlockEntity implements TickableBlockEntity {
   @SerialField
   public final TraitSpawnerData data = new TraitSpawnerData();
   @Nullable
   protected CustomBossEvent event;

   public TraitSpawnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   public void tick() {
      if (this.level != null) {
         if (!this.level.isClientSide()) {
            if (this.getBlockState().getValue(TraitSpawnerBlock.STATE) == TraitSpawnerBlock.State.ACTIVATED) {
               this.data.init(this.level);
               TraitSpawnerBlock.State next = this.data.tick();
               if (next == TraitSpawnerBlock.State.FAILED) {
                  this.stop();
                  this.level
                     .setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue(TraitSpawnerBlock.STATE, TraitSpawnerBlock.State.FAILED));
                  return;
               }

               if (next == TraitSpawnerBlock.State.CLEAR) {
                  this.clearStage();
                  this.stop();
                  this.level
                     .setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue(TraitSpawnerBlock.STATE, TraitSpawnerBlock.State.CLEAR));
                  return;
               }

               if (this.event == null) {
                  this.event = this.createBossEvent();
               }

               int max = this.data.getMax();
               int alive = this.data.getAlive();
               this.event.setMax(max);
               this.event.setValue(alive);
               this.event.setName(LangData.BOSS_EVENT.get(max - alive, max).withStyle(ChatFormatting.GOLD));
               Set<ServerPlayer> set = new HashSet<>();

               for (ServerPlayer e : this.event.getPlayers()) {
                  if (e.distanceToSqr(Vec3.atCenterOf(this.getBlockPos())) > 1024.0) {
                     set.add(e);
                  }
               }

               for (ServerPlayer e : set) {
                  this.event.removePlayer(e);
               }
            }
         }
      }
   }

   public void activate() {
      if (this.level != null && !this.level.isClientSide()) {
         this.data.stop();
         if (this.event != null) {
            this.event.removeAllPlayers();
         }

         this.event = this.createBossEvent();
         this.generate(this.data);
         this.level
            .setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue(TraitSpawnerBlock.STATE, TraitSpawnerBlock.State.ACTIVATED));
      }
   }

   public void setRemoved() {
      if (this.event != null) {
         this.event.removeAllPlayers();
      }

      super.setRemoved();
   }

   public void stop() {
      if (this.event != null) {
         this.event.removeAllPlayers();
         this.event = null;
      }

      this.data.stop();
   }

   public void deactivate() {
      if (this.level != null && !this.level.isClientSide()) {
         this.stop();
         this.level.setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue(TraitSpawnerBlock.STATE, TraitSpawnerBlock.State.IDLE));
      }
   }

   protected abstract void generate(TraitSpawnerData var1);

   protected abstract void clearStage();

   protected abstract CustomBossEvent createBossEvent();

   public void track(Player player) {
      if (this.event != null && player instanceof ServerPlayer sp) {
         this.event.addPlayer(sp);
      }
   }
}
