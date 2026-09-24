package io.github.manasmods.tensura.block.entity;

import io.github.manasmods.tensura.block.CharybdisCoreBlock;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.world.TensuraGameEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity.DataComponentInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.gameevent.GameEventListener.DeliveryMode;
import net.minecraft.world.level.gameevent.GameEventListener.Provider;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class CharybdisCoreBlockEntity extends BlockEntity implements Provider<CharybdisCoreBlockEntity.CoreListener> {
   private double EP = 0.0;
   private final CharybdisCoreBlockEntity.CoreListener coreListener;

   public CharybdisCoreBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType)TensuraBlockEntities.CHARYBDIS_CORE.get(), pPos, pBlockState);
      this.coreListener = new CharybdisCoreBlockEntity.CoreListener(this, pBlockState, pPos);
   }

   protected void saveAdditional(CompoundTag pTag, net.minecraft.core.HolderLookup.Provider provider) {
      super.saveAdditional(pTag, provider);
      pTag.putDouble("EP", this.getEP());
   }

   protected void loadAdditional(CompoundTag compoundTag, net.minecraft.core.HolderLookup.Provider provider) {
      super.loadAdditional(compoundTag, provider);
      this.setEP(compoundTag.getDouble("EP"));
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
      return this.saveCustomOnly(provider);
   }

   public CharybdisCoreBlockEntity.CoreListener getListener() {
      return this.coreListener;
   }

   protected void collectImplicitComponents(Builder builder) {
      super.collectImplicitComponents(builder);
      builder.set((DataComponentType)TensuraDataComponents.EP_DURABILITY.get(), this.getEP());
   }

   protected void applyImplicitComponents(DataComponentInput dataComponentInput) {
      super.applyImplicitComponents(dataComponentInput);
      this.setEP((Double)dataComponentInput.getOrDefault((DataComponentType)TensuraDataComponents.EP_DURABILITY.get(), 0.0));
   }

   public void removeComponentsFromTag(CompoundTag compoundTag) {
      super.removeComponentsFromTag(compoundTag);
      compoundTag.remove("EP");
   }

   @Generated
   public double getEP() {
      return this.EP;
   }

   @Generated
   public void setEP(double EP) {
      this.EP = EP;
   }

   public static class CoreListener implements GameEventListener {
      private final BlockState blockState;
      private final CharybdisCoreBlockEntity coreEntity;
      private final BlockPos position;

      public CoreListener(CharybdisCoreBlockEntity coreEntity, BlockState blockState, BlockPos pos) {
         this.blockState = blockState;
         this.coreEntity = coreEntity;
         this.position = pos;
      }

      @NotNull
      public PositionSource getListenerSource() {
         return new BlockPositionSource(this.position);
      }

      public int getListenerRadius() {
         return 16;
      }

      public @NotNull DeliveryMode getDeliveryMode() {
         return DeliveryMode.BY_DISTANCE;
      }

      public boolean handleGameEvent(ServerLevel serverLevel, Holder<GameEvent> holder, Context context, Vec3 vec3) {
         if (!holder.equals(GameEvent.ENTITY_DIE)) {
            return false;
         }

         if (holder.equals(TensuraGameEvents.AFTER_CHEAT_DEATH)) {
            return false;
         }

         if (this.coreEntity.getBlockState().getValue(CharybdisCoreBlock.MODE) != SculkSensorPhase.INACTIVE) {
            return false;
         }

         if (context.sourceEntity() instanceof LivingEntity entity) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            if (existence.isSkippingEPDrop()) {
               return false;
            }

            double EP = EnergyHelper.getEPGain(entity);
            if (EP <= 0.0) {
               return true;
            }

            double maxEP = ObjectSelectionHelper.CONFIG.CharybdisCore.charybdisCoreActiveEP;
            this.coreEntity.setEP(Math.min(this.coreEntity.getEP() + EP, maxEP));
            existence.setSkippingEPDrop(true);
            entity.skipDropExperience();
            this.coreEntity.setChanged();
            TensuraParticleHelper.addServerParticlesAroundPos(serverLevel.getRandom(), serverLevel, this.position.getCenter(), ParticleTypes.SCULK_SOUL, 1.0);
            TensuraParticleHelper.addServerParticlesAroundPos(
               serverLevel.getRandom(), serverLevel, this.position.getCenter(), (ParticleOptions)TensuraParticleTypes.SOUL.get(), 1.0
            );
            if (this.coreEntity.EP >= maxEP) {
               serverLevel.setBlockAndUpdate(this.position, (BlockState)this.blockState.setValue(CharybdisCoreBlock.MODE, SculkSensorPhase.ACTIVE));
               serverLevel.playSound(null, this.position, SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 2.0F, 0.6F + serverLevel.getRandom().nextFloat() * 0.4F);
            } else {
               serverLevel.sendBlockUpdated(this.position, this.blockState, this.blockState, 3);
               serverLevel.playSound(
                  null, this.position, SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.BLOCKS, 2.0F, 0.6F + serverLevel.getRandom().nextFloat() * 0.4F
               );
            }

            return true;
         } else {
            return false;
         }
      }
   }
}
