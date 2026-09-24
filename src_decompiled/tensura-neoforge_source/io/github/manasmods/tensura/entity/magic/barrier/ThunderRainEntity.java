package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.magic.lightning.LightningBolt;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThunderRainEntity extends BarrierEntity {
   @Nullable
   private Entity target;

   public ThunderRainEntity(EntityType<? extends ThunderRainEntity> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.noCulling = false;
   }

   public ThunderRainEntity(Level level, LivingEntity entity) {
      this((EntityType<? extends ThunderRainEntity>)MiscEntityTypes.THUNDER_RAIN.get(), level);
      this.setOwner(entity);
   }

   @Nullable
   public Entity getTarget() {
      return this.target;
   }

   public void setTarget(@Nullable Entity pTarget) {
      this.target = pTarget;
   }

   @Override
   public boolean canWalkThrough() {
      return true;
   }

   @Override
   public boolean blockBuilding() {
      return false;
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   public boolean shouldCreateParts() {
      return false;
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.LIGHTNING_ELEMENTAL;
   }

   @Override
   public boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return !super.canHitEntity(pTarget) ? false : !(this.getOwner() instanceof LivingEntity owner && owner.isAlliedTo(pTarget));
      }
   }

   @Override
   protected AABB getAffectedArea() {
      double height = this.getSize() * 2.0F + this.getHeight();
      return new AABB(
         this.getX() - this.getSize(),
         this.getY() - height,
         this.getZ() - this.getSize(),
         this.getX() + this.getSize(),
         this.getY(),
         this.getZ() + this.getSize()
      );
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return EntityDimensions.scalable(this.getSize() * 2.0F, 2.0F);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.tickCount % 20 == 0) {
         double height = this.getSize() + this.getHeight();
         this.level()
            .playSound(null, this.getX(), this.getY() - height, this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   @Override
   public void applyFollowOwner() {
      Entity owner = this.getOwner();
      if (owner != null) {
         this.setPos(owner.getX(), owner.getY() + this.getSize() + this.getHeight(), owner.getZ());
      }
   }

   @Override
   protected boolean dealDamage(Entity target, float damage, float costMultiplier) {
      LightningBolt bolt = new LightningBolt(target.level(), this.getOwner());
      bolt.setCause(this.getOwner() instanceof ServerPlayer serverPlayer ? serverPlayer : null);
      bolt.setSkill(this.getSkill());
      bolt.setApCost(this.getApCost() / 10.0);
      bolt.setMpCost(this.getMpCost() / 10.0);
      bolt.setMode(this.getMode());
      bolt.setElement(this.getElement());
      bolt.setElementalAttack(this.isElementalAttack());
      bolt.setTensuraDamage(this.getDamage());
      bolt.setSecondaryDamage(this.getSecondaryDamage());
      bolt.setAdditionalVisual(2);
      bolt.setRadius(this.getSize() / 10.0F);
      bolt.setPos(target.position());
      this.level().addFreshEntity(bolt);
      return true;
   }

   @Override
   public void spawnParticle() {
      double yPos = this.getY() + 1.0;
      float f = Mth.clamp(0.5F * this.getVisualSize(), 0.125F, 5.0F);

      for (int i = 0; i < f; i++) {
         if (f - i < 1.0F && this.random.nextFloat() > f - i) {
            return;
         }

         float distance = this.getVisualSize() * (1.0F - this.random.nextFloat() * this.random.nextFloat());
         float v = this.random.nextFloat() * 6.28F;
         Vec3 pos = new Vec3(distance * Mth.cos(v), 0.2, distance * Mth.sin(v));
         Vec3 motion = new Vec3((2.0 * Math.random() - 1.0) * 0.03, this.random.nextDouble() * 0.01, (2.0 * Math.random() - 1.0) * 0.03);
         this.level()
            .addParticle(
               TensuraParticleUtils.getBlackCloud(1.0F, 0.6F, 3.0F), this.getX() + pos.x, yPos + pos.y, this.getZ() + pos.z, motion.x, motion.y, motion.z
            );
      }

      for (int i = 0; i < f; i++) {
         if (f - i < 1.0F && this.random.nextFloat() > f - i) {
            return;
         }

         float distance = this.getVisualSize() * (1.0F - this.random.nextFloat() * this.random.nextFloat());
         float v = this.random.nextFloat() * 6.28F;
         Vec3 pos = new Vec3(distance * Mth.cos(v), 0.2, distance * Mth.sin(v));
         Vec3 motion = new Vec3((2.0 * Math.random() - 1.0) * 0.03, this.random.nextDouble() * 0.01, (2.0 * Math.random() - 1.0) * 0.03);
         this.level()
            .addParticle(
               (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(),
               this.getX() + pos.x,
               yPos + pos.y,
               this.getZ() + pos.z,
               motion.x,
               motion.y,
               motion.z
            );
      }
   }
}
