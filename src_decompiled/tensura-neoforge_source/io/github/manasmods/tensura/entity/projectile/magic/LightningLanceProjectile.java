package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class LightningLanceProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public LightningLanceProjectile(EntityType<? extends LightningLanceProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElement(Element.WIND);
      this.setElementalAttack(true);
   }

   public LightningLanceProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends LightningLanceProjectile>)ProjectileEntityTypes.LIGHTNING_LANCE.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.LIGHTNING_ELEMENTAL;
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
      return this.getVehicle() != null ? null : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/lance/lightning_lance.png");
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getEffectRange() == -2.0F) {
         if (!(this.getVehicle() instanceof LivingEntity target && target.isAlive())) {
            this.discard();
         } else if (this.getAge() == 140) {
            this.setDamage(this.getDamage() / 2.0F);
            this.dealDamage(target);
            this.playSound((SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get());
            TensuraParticleHelper.spawnServerParticles(
               this.level(),
               (ParticleOptions)TensuraParticleTypes.LIGHTNING_SPARK.get(),
               target.getX(),
               target.getEyeY(),
               target.getZ(),
               10,
               0.5,
               0.5,
               0.5,
               0.1,
               false
            );
            this.discard();
         } else {
            TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.LIGHTNING_SPARK.get());
         }
      }
   }

   @Override
   protected boolean hitEntity(Entity entity, ProjectileHitResult result) {
      if (this.getEffectRange() == -2.0F) {
         return false;
      }

      boolean hit = super.hitEntity(entity, result);
      if (entity.isAlive() && this.getEffectRange() == -1.0F) {
         this.startRiding(entity, true);
         this.setEffectRange(-2.0F);
         this.setAge(100);
      }

      return hit;
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.LIGHTNING_SPARK.get(), x, y, z, 10, 0.5, 0.5, 0.5, 0.1, false
      );
   }

   @Override
   public void flyingParticles() {
      if (this.getDelayTick() <= 0 && !this.isPassenger()) {
         this.level().addParticle((ParticleOptions)TensuraParticleTypes.LIGHTNING_SPARK.get(), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
