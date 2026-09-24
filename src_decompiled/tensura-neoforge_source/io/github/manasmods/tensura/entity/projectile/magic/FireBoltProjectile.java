package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.monster.IfritEntity;
import io.github.manasmods.tensura.entity.monster.SalamanderEntity;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FireBoltProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private int impactParticleCount = 1;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public FireBoltProjectile(EntityType<? extends FireBoltProjectile> entityType, Level level) {
      super(entityType, level);
      this.setSize(0.5F);
      this.setElement(Element.FLAME);
      this.setElementalAttack(true);
   }

   public FireBoltProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends FireBoltProjectile>)ProjectileEntityTypes.FIRE_BOLT.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.FIRE_ELEMENTAL;
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("trailLoops", this.getImpactParticleCount());
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setImpactParticleCount(compound.getInt("trailLoops"));
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      return (pTarget instanceof IfritEntity || pTarget instanceof SalamanderEntity) && this.getOwner() != null && this.getOwner().isAlliedTo(pTarget)
         ? false
         : super.canHitEntity(pTarget);
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/fire.png");
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), x, y, z, 5 * this.getImpactParticleCount(), 0.1, 0.1, 0.1, 0.25, true
      );
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.SMOKE, x, y, z, 5 * this.getImpactParticleCount(), 0.1, 0.1, 0.1, 0.25, true);
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.getDeltaMovement();
      double d0 = this.getX() - vec3.x;
      double d1 = this.getY() - vec3.y;
      double d2 = this.getZ() - vec3.z;

      for (int i = 0; i < 4; i++) {
         Vec3 motion = this.getRandomVec3().scale(0.1F).subtract(this.getDeltaMovement().scale(0.1F));
         Vec3 pos = this.getRandomVec3().scale(0.2F);
         this.level()
            .addParticle(
               (ParticleOptions)(this.level().random.nextDouble() < 0.3 ? ParticleTypes.SMOKE : (ParticleOptions)TensuraParticleTypes.RED_FIRE.get()),
               d0 + pos.x,
               d1 + 0.5 + pos.y,
               d2 + pos.z,
               motion.x,
               motion.y,
               motion.z
            );
      }
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of(SoundEvents.DRAGON_FIREBALL_EXPLODE);
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   public int getImpactParticleCount() {
      return this.impactParticleCount;
   }

   @Generated
   public void setImpactParticleCount(int impactParticleCount) {
      this.impactParticleCount = impactParticleCount;
   }
}
