package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WaterBallProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public WaterBallProjectile(EntityType<? extends WaterBallProjectile> entityType, Level level) {
      super(entityType, level);
      this.setSize(1.5F);
      this.setElement(Element.WATER);
   }

   public WaterBallProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends Projectile>)ProjectileEntityTypes.WATER_BALL.get(), levelIn);
      this.setOwner(shooter);
      this.setSize(1.5F);
      this.setElement(Element.WATER);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.WATER_ELEMENTAL;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/water.png");
   }

   @Override
   protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
      List<LivingEntity> livingEntityList = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0), LivingEntity::isAlive);
      if (!livingEntityList.isEmpty()) {
         for (LivingEntity pLivingEntity : livingEntityList) {
            pLivingEntity.clearFire();
         }
      }

      super.onHitBlock(blockHitResult);
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.GENERIC_SPLASH.get());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getWaterEffect(), x, y, z, 55, 0.08, 0.08, 0.08, 0.15, true);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getWaterBubble(), x, y, z, 25, 0.08, 0.08, 0.08, 0.15, false);
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));
      this.level().addParticle(TensuraParticleUtils.getWaterBubble(), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);

      for (int i = 0; i < 2; i++) {
         Vec3 random = this.getRandomVec3().scale(0.1F);
         this.level().addParticle(TensuraParticleUtils.getWaterEffect(), vec3.x, vec3.y, vec3.z, random.x, random.y, random.z);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
