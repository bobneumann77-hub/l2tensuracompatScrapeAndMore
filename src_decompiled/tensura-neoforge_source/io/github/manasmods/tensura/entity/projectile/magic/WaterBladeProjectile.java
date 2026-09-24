package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WaterBladeProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public WaterBladeProjectile(EntityType<? extends WaterBladeProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElement(Element.WATER);
      this.setElementalAttack(true);
   }

   public WaterBladeProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends WaterBladeProjectile>)ProjectileEntityTypes.WATER_BLADE.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.WATER_BLADE;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/slash/water_blade.png");
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
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
