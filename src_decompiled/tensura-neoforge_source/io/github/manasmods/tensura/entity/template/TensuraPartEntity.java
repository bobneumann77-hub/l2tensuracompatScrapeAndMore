package io.github.manasmods.tensura.entity.template;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class TensuraPartEntity extends Entity {
   public final Entity parent;

   public TensuraPartEntity(Entity owner) {
      super(owner.getType(), owner.level());
      this.parent = owner;
   }

   public Entity getParent() {
      return this.parent;
   }

   public boolean is(Entity entity) {
      return this == entity || this.getParent() == entity;
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(CompoundTag nbt) {
   }

   protected void addAdditionalSaveData(CompoundTag nbt) {
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entityTrackerEntry) {
      throw new UnsupportedOperationException();
   }

   public boolean shouldBeSaved() {
      return false;
   }

   public boolean isPickable() {
      return true;
   }

   public boolean dampensVibrations() {
      return true;
   }

   public boolean canSpawnSprintParticle() {
      return false;
   }

   public EntityDimensions getDimensions(Pose pose) {
      return EntityDimensions.scalable(0.1F, 0.1F);
   }

   public boolean isAlive() {
      return this.getParent().isAlive() && super.isAlive();
   }

   @Nullable
   public ItemStack getPickResult() {
      return this.getParent().getPickResult();
   }

   public boolean isAlliedTo(Entity entity) {
      return this.getParent().isAlliedTo(entity);
   }

   public boolean isCurrentlyGlowing() {
      return this.getParent().isCurrentlyGlowing();
   }

   public boolean isInvisible() {
      return this.getParent().isInvisible();
   }

   public boolean isInvisibleTo(Player player) {
      return this.getParent().isInvisibleTo(player);
   }

   public boolean isOnPortalCooldown() {
      return this.getParent().isOnPortalCooldown();
   }

   public boolean onGround() {
      return this.getParent().onGround();
   }

   public boolean isNoGravity() {
      return this.getParent().isNoGravity();
   }

   public boolean hasPose(Pose pose) {
      return this.getParent().hasPose(pose);
   }

   public boolean fireImmune() {
      return this.getParent().fireImmune();
   }

   public boolean isAttackable() {
      return this.getParent().isAttackable();
   }

   public boolean skipAttackInteraction(Entity entity) {
      return this.getParent().skipAttackInteraction(entity);
   }

   public boolean isInvulnerable() {
      return this.getParent().isInvulnerable() || super.isInvulnerable();
   }

   public boolean isInvulnerableTo(DamageSource damageSource) {
      return this.getParent().isInvulnerableTo(damageSource);
   }

   public boolean hurt(DamageSource source, float amount) {
      return !this.isInvulnerableTo(source) && this.getParent().hurt(source, amount);
   }

   public InteractionResult interact(Player player, InteractionHand hand) {
      return this.getParent().interact(player, hand);
   }

   public boolean startRiding(Entity vehicle, boolean force) {
      return this.getParent().startRiding(vehicle, force);
   }

   public boolean killedEntity(ServerLevel level, LivingEntity entity) {
      return this.getParent().killedEntity(level, entity);
   }

   public void setRelativePos(double x, double y, double z, double centerX, double centerY, double centerZ, double pitch, double yaw) {
      this.xOld = this.getX();
      this.yOld = this.getY();
      this.zOld = this.getZ();
      double cosYaw = Math.cos(-yaw * 0.017453292);
      double sinYaw = Math.sin(-yaw * 0.017453292);
      double cosPitch = Math.cos(pitch * 0.017453292);
      double sinPitch = Math.sin(pitch * 0.017453292);
      this.setPos(
         this.getParent().getX() + centerX + z * sinYaw * cosPitch + x * cosYaw + y * sinYaw * sinPitch,
         this.getParent().getY() + centerY + z * -sinPitch + y * cosPitch,
         this.getParent().getZ() + centerZ + z * cosYaw * cosPitch + x * -sinYaw + y * cosYaw * sinPitch
      );
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
   }

   public void setRelativePos(double x, double y, double z, double centerX, double centerY, double centerZ) {
      this.setRelativePos(x, y, z, centerX, centerY, centerZ, this.getParent().getXRot(), this.getParent().getYRot());
   }

   public void setRelativePos(double x, double y, double z, double pitch, double yaw) {
      this.setRelativePos(x, y, z, 0.0, 0.0, 0.0, pitch, yaw);
   }

   public void setRelativePos(double x, double y, double z) {
      this.setRelativePos(x, y, z, 0.0, 0.0, 0.0, this.getParent().getXRot(), this.getParent().getYRot());
   }
}
