package dev.xkmc.l2hostility.content.entity;

import dev.xkmc.l2hostility.init.registrate.LHEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.Nullable;

public class HostilityBullet extends ShulkerBullet implements IEntityWithComplexSpawn {
   @Nullable
   protected BulletType type;
   private int lv;

   public HostilityBullet(EntityType<HostilityBullet> type, Level level) {
      super(type, level);
   }

   public HostilityBullet(Level level, LivingEntity owner, Entity target, Axis direction, BulletType type, int lv) {
      this((EntityType<HostilityBullet>)LHEntities.BULLET.get(), level);
      this.setOwner(owner);
      BlockPos blockpos = owner.blockPosition();
      double d0 = blockpos.getX() + 0.5;
      double d1 = blockpos.getY() + 0.5;
      double d2 = blockpos.getZ() + 0.5;
      this.moveTo(d0, d1, d2, this.getYRot(), this.getXRot());
      this.finalTarget = target;
      this.currentMoveDirection = Direction.UP;
      this.selectNextMoveDirection(direction);
      this.type = type;
      this.lv = lv;
   }

   protected void onHitEntity(EntityHitResult result) {
      if (this.type != null) {
         Entity target = result.getEntity();
         Entity owner = this.getOwner();
         LivingEntity leowner = owner instanceof LivingEntity ? (LivingEntity)owner : null;
         float damage = this.type.getDamage(this.lv);
         if (damage > 0.0F) {
            target.hurt(this.damageSources().mobProjectile(this, leowner), damage);
         }

         this.type.onHit(this, result, this.lv);
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
      if (this.type != null) {
         this.type.onHit(this, result, this.lv);
      }
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      if (this.type != null) {
         tag.putString("BulletType", this.type.name());
      }

      tag.putInt("BulletLevel", this.lv);
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("BulletType")) {
         this.type = BulletType.valueOf(tag.getString("BulletType"));
      }

      this.lv = tag.getInt("BulletLevel");
   }

   public boolean isTarget(Entity e) {
      if (e instanceof Player) {
         return true;
      }

      if (e == this.finalTarget) {
         return true;
      }

      if (e instanceof Mob target) {
         Entity owner = this.getOwner();
         if (owner != null) {
            if (target.getTarget() == owner) {
               return true;
            }

            if (owner instanceof Mob mob) {
               return mob.getTarget() == e;
            }
         }
      }

      return false;
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         Vec3 vel = this.getDeltaMovement();
         SimpleParticleType particle = this.type == BulletType.EXPLODE ? ParticleTypes.FLAME : ParticleTypes.END_ROD;
         this.level().addParticle(particle, this.getX() - vel.x, this.getY() - vel.y + 0.15, this.getZ() - vel.z, 0.0, 0.0, 0.0);
      }
   }

   public void writeSpawnData(RegistryFriendlyByteBuf buf) {
      Entity owner = this.getOwner();
      buf.writeInt(owner == null ? -1 : owner.getId());
      buf.writeInt(this.type == null ? -1 : this.type.ordinal());
   }

   public void readSpawnData(RegistryFriendlyByteBuf buf) {
      int owner = buf.readInt();
      Entity e = this.level().getEntity(owner);
      if (e != null) {
         this.setOwner(e);
      }

      int val = buf.readInt();
      if (val >= 0) {
         this.type = BulletType.values()[val];
      }
   }
}
