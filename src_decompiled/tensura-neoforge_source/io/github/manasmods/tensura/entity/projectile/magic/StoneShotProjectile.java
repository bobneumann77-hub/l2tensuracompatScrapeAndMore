package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.Optional;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class StoneShotProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public StoneShotProjectile(EntityType<? extends StoneShotProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElement(Element.EARTH);
      this.setElementalAttack(true);
      this.setSize(0.5F);
   }

   public StoneShotProjectile(Level levelIn, Entity shooter) {
      this((EntityType<? extends StoneShotProjectile>)ProjectileEntityTypes.STONE_SHOT.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.EARTH_ELEMENTAL;
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
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/lance/dripstone_shot.png");
   }

   @Override
   public void setPosAndShoot(LivingEntity entity) {
      this.setPos(entity.position().add(0.0, entity.getEyeHeight(), 0.0));
      this.shootFromRot(entity.getLookAngle());
   }

   @Override
   public Optional<SoundEvent> delayShootSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_EARTH.get());
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)TensuraSoundEvents.CAST_EARTH.get());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DRIPSTONE_BLOCK.defaultBlockState()), x, y, z, 10, 0.08, 0.08, 0.08, 0.1, false
      );
   }

   @Override
   public void flyingParticles() {
      if (this.getDelayTick() <= 0) {
         Vec3 vec3 = this.position().subtract(this.getDeltaMovement().scale(2.0));
         this.level().addParticle(ParticleTypes.CRIT, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
