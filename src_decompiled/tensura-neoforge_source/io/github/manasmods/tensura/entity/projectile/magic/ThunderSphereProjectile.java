package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.magic.lightning.LightningBolt;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ThunderSphereProjectile extends TensuraFlyingProjectile implements GeoEntity {
   protected float strikeRadius = 0.0F;
   protected int strikeInterval = 10;
   private final List<Integer> struckEntityIds = new ArrayList<>();
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ThunderSphereProjectile(EntityType<? extends ThunderSphereProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElement(Element.WIND);
      this.setPiercingEntity(true);
   }

   public ThunderSphereProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends ThunderSphereProjectile>)ProjectileEntityTypes.THUNDER_SPHERE.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putFloat("StrikeRadius", this.getStrikeRadius());
      compound.putInt("StrikeInterval", this.getStrikeInterval());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setStrikeRadius(compound.getFloat("StrikeRadius"));
      this.setStrikeInterval(compound.getInt("StrikeInterval"));
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.LIGHTNING_ELEMENTAL;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_sphere/thunder_sphere.png");
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      return this.struckEntityIds.contains(pTarget.getId()) ? false : super.canHitEntity(pTarget);
   }

   @Override
   public void tick() {
      super.tick();
      if (!(this.getStrikeRadius() <= 0.0F) && !this.noPhysics && this.getDelayTick() <= 0) {
         if (this.getAge() <= 0 || this.getAge() % this.getStrikeInterval() == 0) {
            List<LivingEntity> list = this.level()
               .getEntitiesOfClass(
                  LivingEntity.class,
                  this.getBoundingBox().inflate(this.getStrikeRadius()),
                  targetx -> this.canHitEntity(targetx)
                     && targetx.isAlive()
                     && !this.struckEntityIds.contains(targetx.getId())
                     && (this.getOwner() == null || !targetx.isAlliedTo(this.getOwner()) && !targetx.is(this.getOwner()))
               );
            if (!list.isEmpty()) {
               for (LivingEntity target : list) {
                  LightningBolt bolt = new LightningBolt(this.level(), this.getOwner());
                  bolt.setCause(this.getOwner() instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                  bolt.setSkill(this.getSkill());
                  bolt.setMpCost(this.getMpCost() / 10.0);
                  bolt.setApCost(this.getApCost() / 10.0);
                  bolt.setTensuraDamage(this.getDamage());
                  bolt.setSecondaryDamage(this.getSecondaryDamage());
                  bolt.setAdditionalVisual(2);
                  bolt.setRadius(this.getStrikeRadius() / 2.0F);
                  bolt.setPos(target.position());
                  this.level().addFreshEntity(bolt);
                  this.struckEntityIds.add(target.getId());
               }
            }
         }
      }
   }

   protected void onHit(@NotNull HitResult hitresult) {
      super.onHit(hitresult);
      Entity entity = this.getOwner();
      LightningBolt bolt = new LightningBolt(this.level(), entity);
      bolt.setCause(entity instanceof ServerPlayer serverPlayer ? serverPlayer : null);
      bolt.setSkill(this.getSkill());
      bolt.setMpCost(this.getMpCost() / 10.0);
      bolt.setApCost(this.getApCost() / 10.0);
      bolt.setTensuraDamage(this.getDamage());
      bolt.setSecondaryDamage(this.getSecondaryDamage());
      bolt.setAdditionalVisual(2);
      bolt.setRadius(this.getStrikeRadius() / 2.0F);
      bolt.setPos(this.position());
      this.level().addFreshEntity(bolt);
   }

   @Override
   public Optional<SoundEvent> delayShootSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get());
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), x, y, z, 10, 0.5, 0.5, 0.5, 0.1, false
      );
   }

   @Override
   public void flyingParticles() {
      if (this.random.nextFloat() <= 0.8) {
         double dx = this.level().random.nextDouble() * 0.05 - 0.05;
         double dy = this.level().random.nextDouble() * 0.05 - 0.05;
         double dz = this.level().random.nextDouble() * 0.05 - 0.05;
         double x = (this.level().random.nextDouble() - 0.5) * 4.0;
         double y = (this.level().random.nextDouble() - 0.5) * 4.0;
         double z = (this.level().random.nextDouble() - 0.5) * 4.0;
         this.level()
            .addParticle((ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), this.getX() + x, this.getY() + y, this.getZ() + z, dx, dy, dz);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_sphere.loop")))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   public float getStrikeRadius() {
      return this.strikeRadius;
   }

   @Generated
   public void setStrikeRadius(float strikeRadius) {
      this.strikeRadius = strikeRadius;
   }

   @Generated
   public int getStrikeInterval() {
      return this.strikeInterval;
   }

   @Generated
   public void setStrikeInterval(int strikeInterval) {
      this.strikeInterval = strikeInterval;
   }
}
