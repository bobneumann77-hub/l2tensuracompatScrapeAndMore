package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.TensuraExplosionDamageCalculator;
import java.awt.Color;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AuraBulletProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private static final EntityDataAccessor<Integer> COLOR_ID = SynchedEntityData.defineId(AuraBulletProjectile.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public AuraBulletProjectile(EntityType<? extends AuraBulletProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public AuraBulletProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends Projectile>)ProjectileEntityTypes.AURA_BULLET.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.AURA_BULLET;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(COLOR_ID, 10278389);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("Color", this.getColor());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.entityData.set(COLOR_ID, pCompound.getInt("Color"));
   }

   public int getColor() {
      return (Integer)this.entityData.get(COLOR_ID);
   }

   public void setColor(int color) {
      this.entityData.set(COLOR_ID, color);
   }

   public static int getColorBySize(float size, float startSize, float endSize, int startColor, int endColor) {
      float t = (size - startSize) / (endSize - startSize);
      t = Math.max(0.0F, Math.min(1.0F, t));
      float[] hsvStart = Color.RGBtoHSB(startColor >> 16 & 0xFF, startColor >> 8 & 0xFF, startColor & 0xFF, null);
      float[] hsvEnd = Color.RGBtoHSB(endColor >> 16 & 0xFF, endColor >> 8 & 0xFF, endColor & 0xFF, null);
      float h1 = hsvStart[0];
      float h2 = hsvEnd[0];
      float dh = h2 - h1;
      if (Math.abs(dh) > 0.5F) {
         if (dh > 0.0F) {
            h1++;
         } else {
            h2++;
         }
      }

      float h = (h1 + (h2 - h1) * t) % 1.0F;
      float s = hsvStart[1] + (hsvEnd[1] - hsvStart[1]) * t;
      float v = hsvStart[2] + (hsvEnd[2] - hsvStart[2]) * t;
      return Color.HSBtoRGB(h, s, v) & 16777215;
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_sphere/aura_bullet.png");
   }

   @Override
   protected void shakeScreenOnExplosion() {
      if (this.getExplosionRadius() >= 2.0F) {
         EffectStorage.setCameraShake(this, this.getExplosionRadius() / 2.0F, this.getExplosionRadius() / 300.0F, 15);
      }
   }

   @Override
   protected void explode(double x, double y, double z, boolean fire) {
      TensuraExplosionDamageCalculator calculator = new TensuraExplosionDamageCalculator(false, true, Optional.of(1.0F), Optional.of(this.getKnockForce()));
      this.level()
         .explode(
            this.getOwner(),
            this.getDamageSource(),
            calculator,
            x,
            y,
            z,
            this.getExplosionRadius(),
            false,
            this.getExplosionInteraction(),
            ParticleTypes.EXPLOSION,
            ParticleTypes.EXPLOSION_EMITTER,
            SoundEvents.GENERIC_EXPLODE
         );
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.FLASH, x, y, z, 5, 0.1, 0.1, 0.1, 0.25, true);
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.SMOKE, x, y, z, 5, 0.1, 0.1, 0.1, 0.25, true);
   }

   @Override
   public void flyingParticles() {
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of(SoundEvents.DRAGON_FIREBALL_EXPLODE);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_sphere.loop")))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
