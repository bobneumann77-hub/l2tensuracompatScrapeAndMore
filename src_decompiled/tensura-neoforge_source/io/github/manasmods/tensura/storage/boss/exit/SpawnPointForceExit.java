package io.github.manasmods.tensura.storage.boss.exit;

import com.google.gson.JsonObject;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

public class SpawnPointForceExit implements IForceExitHandler {
   @Override
   public CompoundTag toNBT(Provider registries) {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("type", "spawnPoint");
      return nbt;
   }

   @Override
   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("type", "SpawnPoint");
      return json;
   }

   @Override
   public MutableComponent getDataMessage() {
      return Component.translatable("tensura.boss_fight.get.force_exit.spawn_point");
   }

   @Override
   public boolean apply(Entity entity, double costPerBlock) {
      DimensionTransition transition = this.getPortalDestination((ServerLevel)entity.level(), entity);
      if (costPerBlock > 0.0
         && entity instanceof LivingEntity teleporter
         && EnergyHelper.isOutOfEnergy(teleporter, 0.0, costPerBlock * Math.sqrt(entity.distanceToSqr(transition.pos())))) {
         return false;
      } else {
         entity.changeDimension(transition);
         return true;
      }
   }

   public DimensionTransition getPortalDestination(ServerLevel serverLevel, Entity entity) {
      if (entity instanceof ServerPlayer player) {
         ServerLevel destination = serverLevel.getServer().getLevel(player.getRespawnDimension());
         return destination == null ? null : player.findRespawnPositionAndUseSpawnBlock(false, DimensionTransition.DO_NOTHING);
      } else {
         Vec3 vec3 = entity.adjustSpawnLocation(serverLevel, serverLevel.getSharedSpawnPos()).getBottomCenter();
         return new DimensionTransition(
            serverLevel,
            vec3,
            Vec3.ZERO,
            entity.getYRot(),
            entity.getXRot(),
            DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)
         );
      }
   }
}
