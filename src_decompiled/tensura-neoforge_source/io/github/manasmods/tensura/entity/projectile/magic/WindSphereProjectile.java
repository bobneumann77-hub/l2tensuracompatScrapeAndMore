package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WindSphereProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new SimpleExplosionDamageCalculator(
      true, false, Optional.of(1.5F), BuiltInRegistries.BLOCK.getTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
   );
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public WindSphereProjectile(EntityType<? extends WindSphereProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.setElement(Element.WIND);
   }

   public WindSphereProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends WindSphereProjectile>)ProjectileEntityTypes.WIND_SPHERE.get(), levelIn);
      this.setOwner(shooter);
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
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_sphere/wind_sphere.png");
   }

   @Override
   protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
      this.level()
         .explode(
            this,
            null,
            EXPLOSION_DAMAGE_CALCULATOR,
            this.getX(),
            this.getY(),
            this.getZ(),
            5.0F,
            false,
            ExplosionInteraction.TRIGGER,
            ParticleTypes.GUST_EMITTER_SMALL,
            ParticleTypes.GUST_EMITTER_LARGE,
            SoundEvents.WIND_CHARGE_BURST
         );
      super.onHitBlock(blockHitResult);
   }

   @Override
   protected void onHitEntity(@NotNull EntityHitResult result, ProjectileHitResult customResult) {
      this.level()
         .explode(
            this,
            null,
            EXPLOSION_DAMAGE_CALCULATOR,
            this.getX(),
            this.getY(),
            this.getZ(),
            5.0F,
            false,
            ExplosionInteraction.TRIGGER,
            ParticleTypes.GUST_EMITTER_SMALL,
            ParticleTypes.GUST_EMITTER_LARGE,
            SoundEvents.WIND_CHARGE_BURST
         );
      super.onHitEntity(result, customResult);
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)SoundEvents.WIND_CHARGE_BURST.value());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.GUST, 3.0);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getGust(), x, y, z, 20, 0.1, 0.1, 0.1, 0.15, true);
   }

   @Override
   public void flyingParticles() {
      double dx = this.level().random.nextDouble() * 0.05 - 0.05;
      double dy = this.level().random.nextDouble() * 0.05 - 0.05;
      double dz = this.level().random.nextDouble() * 0.05 - 0.05;
      double x = (this.level().random.nextDouble() - 0.5) * 4.0;
      double y = (this.level().random.nextDouble() - 0.5) * 4.0;
      double z = (this.level().random.nextDouble() - 0.5) * 4.0;
      this.level().addParticle(TensuraParticleUtils.getGust(), this.getX() + x, this.getY() + y, this.getZ() + z, dx, dy, dz);
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
