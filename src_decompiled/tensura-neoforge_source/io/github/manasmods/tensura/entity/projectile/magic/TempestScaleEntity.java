package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.monster.CharybdisEntity;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TempestScaleEntity extends TensuraFlyingProjectile {
   @Nullable
   private LivingEntity target;

   public TempestScaleEntity(EntityType<? extends TempestScaleEntity> entityType, Level level) {
      super(entityType, level);
      this.setArmorHurt(10);
   }

   public TempestScaleEntity(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends TempestScaleEntity>)ProjectileEntityTypes.TEMPEST_SCALE.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.TEMPEST_SCALE;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   public void setTarget(@Nullable LivingEntity pTarget) {
      this.target = pTarget;
   }

   @Override
   protected boolean canHitEntity(Entity target) {
      return this.getOwner() == null || !this.getOwner().isAlliedTo(target) && !target.isAlliedTo(this.getOwner()) ? super.canHitEntity(target) : false;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.getOwner() instanceof CharybdisEntity charybdis) {
            if (this.getTarget() != null && this.getTarget().isAlive()) {
               this.homing(this.getTarget());
            } else {
               List<LivingEntity> list = this.getTargetList(charybdis);
               if (!list.isEmpty()) {
                  this.setTarget(list.get(this.random.nextInt(list.size())));
               }
            }
         }
      }
   }

   @Override
   protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
      if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.random.nextInt(10) == 1) {
         SkillHelper.launchBlock(
            this,
            this.position(),
            2,
            1,
            0.5F,
            0.2F,
            blockState -> this.random.nextInt(3) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE),
            blockPos -> true
         );
      }

      super.onHitBlock(blockHitResult);
   }

   @Override
   protected boolean dealDamage(Entity target, float damage, float costMultiplier) {
      boolean hurt = super.dealDamage(target, damage, costMultiplier);
      if (hurt && !target.isAlive() && this.getRandom().nextFloat() <= 0.1) {
         this.spawnAtLocation((ItemLike)TensuraMobDropItems.CHARYBDIS_SCALE.get());
      }

      return hurt;
   }

   private List<LivingEntity> getTargetList(CharybdisEntity charybdis) {
      AABB box = charybdis.getBoundingBox().move(0.0, -25.0, 0.0).inflate(128.0);
      return this.level().getEntitiesOfClass(LivingEntity.class, box, entity -> {
         if (entity == this.getOwner()) {
            return false;
         } else {
            return charybdis.isAlliedTo(entity) ? false : charybdis.shouldAttack(entity);
         }
      });
   }

   private void homing(Entity target) {
      double posX = this.getX();
      double posY = this.getY();
      double posZ = this.getZ();
      double motionX = this.getDeltaMovement().x;
      double motionY = this.getDeltaMovement().y;
      double motionZ = this.getDeltaMovement().z;
      Vec3 targetVector = new Vec3(target.getX() - posX, target.getY() + target.getBbHeight() / 2.0F - posY, target.getZ() - posZ);
      targetVector = targetVector.normalize();
      double weight = 0.8;
      motionX = (0.9 - weight) * motionX + (0.1 + weight) * targetVector.x;
      motionY = (0.9 - weight) * motionY + (0.1 + weight) * targetVector.y;
      motionZ = (0.9 - weight) * motionZ + (0.1 + weight) * targetVector.z;
      posX += motionX;
      posY += motionY;
      posZ += motionZ;
      this.setPos(posX, posY, posZ);
      this.setDeltaMovement(motionX, motionY, motionZ);
      if (target.distanceTo(this) < 3.0F && target != this.getOwner()) {
         this.onHitEntity(new EntityHitResult(target));
      }
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of(SoundEvents.TRIDENT_HIT);
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.ENCHANTED_HIT, x, y, z, 1, 0.12, 0.12, 0.12, 0.15, false);
   }

   @Override
   public void flyingParticles() {
   }

   @Nullable
   @Generated
   public LivingEntity getTarget() {
      return this.target;
   }
}
