package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SolarGrenadeProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public SolarGrenadeProjectile(EntityType<? extends SolarGrenadeProjectile> entityType, Level level) {
      super(entityType, level);
      this.setSize(0.4F);
      this.setElement(Element.LIGHT);
      this.setElementalAttack(true);
   }

   public SolarGrenadeProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends SolarGrenadeProjectile>)ProjectileEntityTypes.SOLAR_GRENADE.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.LIGHT_ELEMENTAL;
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
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_sphere/light_sphere.png");
   }

   @Override
   public void setPosAndShoot(LivingEntity entity) {
      this.setPos(entity.position().add(0.0, entity.getEyeHeight(), 0.0));
      this.shootFromRot(entity.getLookAngle());
   }

   @Override
   public void onExplosion(double x, double y, double z) {
      TensuraParticleHelper.addServerParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get());
      TensuraParticleHelper.addServerParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get(), 2.0);
      List<LivingEntity> livingEntityList = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            this.getBoundingBox().inflate(this.getEffectRange()),
            entityData -> this.getOwner() == null || !entityData.isAlliedTo(this.getOwner()) && !entityData.is(this.getOwner())
         );
      if (!livingEntityList.isEmpty()) {
         for (LivingEntity target : livingEntityList) {
            if (this.dealDamage(target, 30.0F, 1.0F)) {
               if (this.getMobEffect() != null) {
                  target.addEffect(new MobEffectInstance(this.getMobEffect()));
               }

               target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 1, false, false, false));
            }
         }
      }
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)SoundEvents.GENERIC_EXPLODE.value());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.FLASH, x, y, z, 1, 0.12, 0.12, 0.12, 0.15, false);
      TensuraParticleHelper.spawnServerParticles(this.level(), (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get(), x, y, z, 10, 0.5, 0.5, 0.5, 0.1, false);
   }

   @Override
   public void flyingParticles() {
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_sphere.circling")))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
