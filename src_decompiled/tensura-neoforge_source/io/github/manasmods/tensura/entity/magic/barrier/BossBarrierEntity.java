package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.storage.boss.template.BossFightInstance;
import lombok.Generated;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BossBarrierEntity extends BarrierEntity {
   private BossFightInstance instance = null;

   public BossBarrierEntity(Level level) {
      this((EntityType<? extends BossBarrierEntity>)MiscEntityTypes.BOSS_BARRIER.get(), level);
   }

   public BossBarrierEntity(EntityType<? extends BossBarrierEntity> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return false;
   }

   @Override
   public boolean blockBuilding() {
      return false;
   }

   @Override
   public boolean canWalkThrough(Entity entity) {
      return entity.isSpectator() || entity instanceof Player player && player.isSpectator();
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return this.getType().getDimensions();
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   protected boolean shouldRemove() {
      if (this.instance == null) {
         return true;
      } else if (!this.instance.isStarted() && !this.instance.isOnHold()) {
         return true;
      } else if (this.isInWaterOrBubble() && this.shouldDiscardInWater()) {
         return true;
      } else {
         return this.isInLava() && this.shouldDiscardInLava() ? true : this.getLife() != -1 && this.getAge() > this.getLife();
      }
   }

   @Generated
   public void setInstance(BossFightInstance instance) {
      this.instance = instance;
   }
}
