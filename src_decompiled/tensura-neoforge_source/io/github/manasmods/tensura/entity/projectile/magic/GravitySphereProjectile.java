package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.aspectual.gravity.BurdenMagic;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GravitySphereProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public GravitySphereProjectile(EntityType<? extends GravitySphereProjectile> entityType, Level level) {
      super(entityType, level);
      this.setPiercingEntity(true);
      this.setElementalAttack(true);
      this.setElement(Element.EARTH);
      this.setArmorHurt(20);
   }

   public GravitySphereProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends GravitySphereProjectile>)ProjectileEntityTypes.GRAVITY_SPHERE.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.GRAVITY_ELEMENTAL;
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
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_sphere/gravity_sphere.png");
   }

   @Override
   public void setPosAndShoot(LivingEntity entity) {
      this.setPos(entity.position().add(0.0, entity.getEyeHeight(), 0.0));
      this.shootFromRot(entity.getLookAngle());
   }

   @Override
   protected void onHitBlock(@NotNull BlockHitResult result) {
      if (this.getEffectRange() > 0.0F) {
         EffectStorage.setCameraShake(this, this.getEffectRange(), 0.02F, 10);
         if (this.shouldGrief()) {
            SkillHelper.launchBlock(
               this,
               this.getOwner(),
               result.getLocation(),
               (int)this.getEffectRange(),
               (int)this.getEffectRange() - 2,
               0.5F,
               0.2F,
               blockState -> this.random.nextInt(3) != 1 ? false : blockState.is(TensuraBlockTags.SKILL_BREAK_EASY),
               blockPos -> true,
               this.getSkill()
            );
         }
      }

      super.onHitBlock(result);
   }

   @Override
   protected boolean applyMobEffects(LivingEntity entity) {
      if (super.applyMobEffects(entity)) {
         TensuraDamageHelper.markHurt(entity, this.getOwner());
      }

      ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
      MobEffectInstance burden = new MobEffectInstance(
         TensuraMobEffects.getReference(TensuraMobEffects.BURDEN), BurdenMagic.CONFIG.burdenDuration, BurdenMagic.CONFIG.burdenLevel - 1, true, false, true
      );
      TensuraMobEffect.addEffect(entity, burden, this.getOwner(), skill, this.getMode());
      return true;
   }

   @Override
   public void applyEffectAround(double inflateRadius) {
      List<LivingEntity> livingEntityList = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            this.getBoundingBox().inflate(inflateRadius),
            entityData -> this.getOwner() == null || !entityData.isAlliedTo(this.getOwner()) && !entityData.is(this.getOwner())
         );
      if (!livingEntityList.isEmpty()) {
         for (LivingEntity target : livingEntityList) {
            this.dealDamage(target, this.damage / 10.0F, 0.1F);
            if (target.getBbHeight() <= 3.0F
               && target.getBbWidth() <= 3.0F
               && !target.getType().is(TensuraEntityTags.FULL_GRAVITY_CONTROL)
               && !SkillUtils.isSkillToggled(target, (ManasSkill)ExtraSkills.GRAVITY_DOMINATION.get())) {
               Vec3 vec3 = new Vec3(this.getX() - target.getX(), this.getY() - target.getY(), this.getZ() - target.getZ());
               target.setDeltaMovement(target.getDeltaMovement().add(vec3.scale(0.05F)));
            }
         }
      }
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return this.getSize() >= 1.0F
         ? Optional.of((SoundEvent)SoundEvents.GENERIC_EXPLODE.value())
         : Optional.of((SoundEvent)TensuraSoundEvents.CAST_EARTH.get());
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      if (this.getSize() >= 1.0F) {
         TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.FLASH, x, y, z, 3, 0.12, 0.12, 0.12, 0.15, false);
      }

      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.DARK_PURPLE_LIGHTNING_SPARK.get(), x, y, z, 10, 0.5, 0.5, 0.5, 0.1, false
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
            .addParticle((ParticleOptions)TensuraParticleTypes.DARK_PURPLE_LIGHTNING_SPARK.get(), this.getX() + x, this.getY() + y, this.getZ() + z, dx, dy, dz);
      }
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
