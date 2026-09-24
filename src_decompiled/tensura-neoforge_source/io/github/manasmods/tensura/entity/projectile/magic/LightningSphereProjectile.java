package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.lightning.LightningBolt;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
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

public class LightningSphereProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public LightningSphereProjectile(EntityType<? extends LightningSphereProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElement(Element.WIND);
      this.setPiercingEntity(true);
   }

   public LightningSphereProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends LightningSphereProjectile>)ProjectileEntityTypes.LIGHTNING_SPHERE.get(), levelIn);
      this.setOwner(shooter);
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
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_sphere/lightning_sphere.png");
   }

   protected void onHit(@NotNull HitResult hitresult) {
      super.onHit(hitresult);
      Entity entity = this.getOwner();
      LightningBolt bolt = new LightningBolt(this.level(), entity);
      bolt.setCause(entity instanceof ServerPlayer serverPlayer ? serverPlayer : null);
      bolt.setMpCost(this.getMpCost());
      bolt.setTensuraDamage(this.getDamage());
      bolt.setSkill(this.getSkill());
      float radius = this.getSize();
      bolt.setAdditionalVisual((int)radius);
      bolt.setRadius(radius);
      bolt.setPos(this.position());
      this.level().addFreshEntity(bolt);
   }

   @Override
   public void applyEffectAround(double inflateRadius) {
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            this.getBoundingBox().inflate(inflateRadius),
            entityData -> this.getOwner() == null || !entityData.isAlliedTo(this.getOwner()) && !entityData.is(this.getOwner())
         );
      if (!list.isEmpty()) {
         ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;

         for (LivingEntity target : list) {
            if (this.getMobEffect() != null) {
               TensuraMobEffect.addEffect(target, new MobEffectInstance(this.getMobEffect()), this.getOwner(), skill, this.getMode());
            }

            this.dealDamage(target, this.damage / 10.0F, 0.1F);
         }
      }
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
      if (this.random.nextFloat() <= 0.8) {
         double dx = this.level().random.nextDouble() * 0.05 - 0.05;
         double dy = this.level().random.nextDouble() * 0.05 - 0.05;
         double dz = this.level().random.nextDouble() * 0.05 - 0.05;
         double x = (this.level().random.nextDouble() - 0.5) * 4.0;
         double y = (this.level().random.nextDouble() - 0.5) * 4.0;
         double z = (this.level().random.nextDouble() - 0.5) * 4.0;
         this.level().addParticle((ParticleOptions)TensuraParticleTypes.LIGHTNING_SPARK.get(), this.getX() + x, this.getY() + y, this.getZ() + z, dx, dy, dz);
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
}
