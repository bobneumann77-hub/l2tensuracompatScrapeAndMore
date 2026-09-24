package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WindTornadoProjectile extends TensuraFlyingProjectile implements GeoEntity {
   protected static final EntityDataAccessor<Integer> BURST_DELAY = SynchedEntityData.defineId(WindTornadoProjectile.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Boolean> BURSTING = SynchedEntityData.defineId(WindTornadoProjectile.class, EntityDataSerializers.BOOLEAN);
   protected int onExplodeBlades = 0;
   protected float bladeDamage = 0.0F;
   protected float pullForce = 0.2F;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public WindTornadoProjectile(EntityType<? extends WindTornadoProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.setElement(Element.WIND);
   }

   public WindTornadoProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends WindTornadoProjectile>)ProjectileEntityTypes.WIND_TORNADO.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(BURST_DELAY, 40);
      builder.define(BURSTING, false);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Bursting", this.isBursting());
      compound.putInt("BurstDelay", this.getBurstDelay());
      compound.putInt("Blades", this.getOnExplodeBlades());
      compound.putFloat("BladeDamage", this.getBladeDamage());
      compound.putFloat("PullForce", this.getPullForce());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setBursting(compound.getBoolean("Bursting"));
      this.setBurstDelay(compound.getInt("BurstDelay"));
      this.setOnExplodeBlades(compound.getInt("Blades"));
      this.setBladeDamage(compound.getFloat("BladeDamage"));
      this.setPullForce(compound.getFloat("PullForce"));
   }

   public int getBurstDelay() {
      return (Integer)this.entityData.get(BURST_DELAY);
   }

   public void setBurstDelay(int delay) {
      this.entityData.set(BURST_DELAY, delay);
   }

   public boolean isBursting() {
      return (Boolean)this.entityData.get(BURSTING);
   }

   public void setBursting(boolean bursting) {
      this.entityData.set(BURSTING, bursting);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.WIND_ELEMENTAL;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/wind_tornado.png");
   }

   @Override
   public void tick() {
      super.tick();
      if (this.isBursting()) {
         this.setBurstDelay(this.getBurstDelay() - 1);
         if (this.getBurstDelay() == 13) {
            if (this.getHitRadius() > 0.0F) {
               for (Entity target : this.level()
                  .getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(this.getHitRadius()), x$0 -> this.canHitEntity(x$0))) {
                  if (this.isIgnoreInvulnerabilityOnHit() && (this.getDamage() > 0.0F || this.getSecondaryDamage() > 0.0F)) {
                     target.invulnerableTime = 0;
                  }

                  this.hitEntity(target, ProjectileHitResult.DEFAULT);
               }
            }

            this.shootBlades(this.getOnExplodeBlades());
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WIND_CHARGE_BURST, TensuraSkill.ABILITY_SOUND, 3.0F, 1.0F);
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 3.0F, 1.0F);
            this.onExplosion(this.getX(), this.getY(), this.getZ());
         } else if (this.getBurstDelay() > 13) {
            this.applyPull(this.getPullForce());
         }
      }
   }

   @Override
   protected void applyBlockHitPre(BlockHitResult pResult) {
   }

   @Override
   protected void applyBlockHitPost(BlockHitResult pResult) {
      if (!this.isPiercingBlock()) {
         Vec3 vec3 = pResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
         this.setDeltaMovement(vec3);
         this.setBursting(true);
         this.setAge(this.getLife() - this.getBurstDelay());
         Vec3 vec32 = vec3.normalize().scale(0.05F);
         this.setPosRaw(this.getX() - vec32.x, this.getY() - vec32.y, this.getZ() - vec32.z);
      }
   }

   @Override
   protected void applyHitEntity(Entity entity, EntityHitResult result, ProjectileHitResult customResult) {
      if (!this.isPiercingEntity()) {
         Vec3 vec3 = this.position().subtract(this.getX(), this.getY(), this.getZ());
         this.setDeltaMovement(vec3);
         this.setBursting(true);
         this.setAge(this.getLife() - this.getBurstDelay());
         Vec3 vec32 = vec3.normalize().scale(0.05F);
         this.setPosRaw(this.getX() - vec32.x, this.getY() - vec32.y, this.getZ() - vec32.z);
      }
   }

   private void applyPull(float pullMultiplier) {
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            this.getBoundingBox().inflate(this.getHitRadius() * 2.0F),
            targetx -> (this.getOwner() == null || !targetx.isAlliedTo(this.getOwner()) && !targetx.is(this.getOwner()))
               && !targetx.getType().is(TensuraEntityTags.FULL_GRAVITY_CONTROL)
               && !targetx.getType().is(TensuraEntityTags.NO_FORCED_MOVE)
         );
      if (!list.isEmpty()) {
         for (LivingEntity target : list) {
            if (!SkillUtils.isSkillToggled(target, (ManasSkill)ExtraSkills.GRAVITY_DOMINATION.get())) {
               Vec3 vec3 = new Vec3(this.getX() - target.getX(), this.getY() - target.getY(), this.getZ() - target.getZ()).normalize();
               Changeable<Vec3> changeable = Changeable.of(target.getDeltaMovement().add(vec3.scale(pullMultiplier)));
               if (!((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker())
                  .move(target, this.getOwner(), this.getSkill(), changeable)
                  .isFalse()) {
                  target.setDeltaMovement(vec3);
               }
            }
         }
      }
   }

   private void shootBlades(int count) {
      if (count >= 0) {
         double radius = this.getSize();
         double angleStep = (Math.PI * 2) / count;

         for (int i = 0; i < count; i++) {
            double angle = i * angleStep;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;
            double spawnX = this.getX() + offsetX;
            double spawnY = this.getY() + this.getBbHeight() / 2.0F;
            double spawnZ = this.getZ() + offsetZ;
            Vec3 direction = new Vec3(offsetX, 0.0, offsetZ).normalize();
            WindBladeProjectile blade = new WindBladeProjectile(this.level());
            blade.setOwner(this.getOwner());
            if (!this.isElementalAttack()) {
               blade.setDamage(this.getBladeDamage());
            } else {
               blade.setSecondaryDamage(this.getBladeDamage());
            }

            blade.setSkill(this);
            blade.setNoGravity(true);
            blade.setElementalAttack(this.isElementalAttack());
            blade.setPiercingEntity(true);
            blade.setPos(spawnX, spawnY, spawnZ);
            blade.shoot(direction.x, direction.y, direction.z, 1.0F, 0.0F);
            this.level().addFreshEntity(blade);
         }
      }
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)SoundEvents.WIND_CHARGE_BURST.value());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
   }

   @Override
   public void flyingParticles() {
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(
            this,
            "controller",
            0,
            event -> {
               if (this.getAge() < 6) {
                  return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.magic_tornado.start"));
               } else {
                  return this.getLife() - this.getAge() < 31
                     ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.magic_tornado.stop"))
                     : event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_tornado.loop"));
               }
            }
         )
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   public int getOnExplodeBlades() {
      return this.onExplodeBlades;
   }

   @Generated
   public void setOnExplodeBlades(int onExplodeBlades) {
      this.onExplodeBlades = onExplodeBlades;
   }

   @Generated
   public float getBladeDamage() {
      return this.bladeDamage;
   }

   @Generated
   public void setBladeDamage(float bladeDamage) {
      this.bladeDamage = bladeDamage;
   }

   @Generated
   public float getPullForce() {
      return this.pullForce;
   }

   @Generated
   public void setPullForce(float pullForce) {
      this.pullForce = pullForce;
   }
}
