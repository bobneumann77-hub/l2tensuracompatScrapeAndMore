package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.magic.field.HellFlare;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HellFlareProjectile extends TensuraFlyingProjectile implements GeoEntity {
   protected float areaRadius = 0.0F;
   protected int areaLife = 0;
   private static final EntityDataAccessor<Boolean> LIMITED = SynchedEntityData.defineId(HellFlareProjectile.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public HellFlareProjectile(EntityType<? extends HellFlareProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElement(Element.FLAME);
   }

   public HellFlareProjectile(Level levelIn, Entity shooter) {
      super((EntityType<? extends Projectile>)ProjectileEntityTypes.HELL_FLARE_PROJECTILE.get(), levelIn);
      this.setOwner(shooter);
      this.setElement(Element.FLAME);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.BLACK_FLAME;
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
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(LIMITED, false);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setLimited(compound.getBoolean("Limited"));
      this.areaLife = compound.getInt("AreaLife");
      this.areaRadius = compound.getFloat("AreaLife");
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Limited", this.isLimited());
      compound.putInt("AreaLife", this.areaLife);
      compound.putFloat("AreaLife", this.areaRadius);
   }

   public boolean isLimited() {
      return (Boolean)this.entityData.get(LIMITED);
   }

   public void setLimited(boolean limited) {
      this.entityData.set(LIMITED, limited);
   }

   @Override
   public void setPosAndShoot(LivingEntity entity) {
      this.setPos(entity.position().add(0.0, entity.getEyeHeight(), 0.0));
      this.shootFromRot(entity.getLookAngle());
   }

   @Override
   protected void onHitBlock(BlockHitResult pResult) {
      if (!this.level().isClientSide() && this.isAlive()) {
         this.summonHellFlare(pResult.getLocation());
         this.discard();
      }
   }

   @Override
   protected void onHitEntity(@NotNull EntityHitResult result, ProjectileHitResult customResult) {
      if (!this.level().isClientSide() && this.isAlive()) {
         this.summonHellFlare(result.getEntity().position());
         this.discard();
      }
   }

   private void summonHellFlare(Vec3 position) {
      HellFlare flare = new HellFlare(
         this.isLimited() ? (EntityType)MiscEntityTypes.HELL_FLARE_LIMITED.get() : (EntityType)MiscEntityTypes.HELL_FLARE.get(), this.level(), this.getOwner()
      );
      flare.setDamage(this.getDamage());
      flare.setSecondaryDamage(this.getSecondaryDamage());
      flare.setTickEachHit(10);
      flare.setSkill(this);
      flare.setLife(this.getAreaLife());
      flare.setSize(this.getAreaRadius());
      flare.setVisualSize(flare.getSize());
      flare.setPos(position.add(0.0, flare.getBbHeight() * -0.4F, 0.0));
      this.level().addFreshEntity(flare);
      this.level()
         .playSound(
            null,
            this.getX(),
            this.getY(),
            this.getZ(),
            SoundEvents.GENERIC_EXPLODE,
            TensuraSkill.ABILITY_SOUND,
            3.0F,
            0.9F + this.level().random.nextFloat() * 0.2F
         );
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_sphere/black_flame_sphere.png");
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of((SoundEvent)SoundEvents.GENERIC_EXPLODE.value());
   }

   @Override
   public void flyingParticles() {
      Vec3 vec3 = this.getDeltaMovement();
      double d0 = this.getX() - vec3.x;
      double d1 = this.getY() - vec3.y;
      double d2 = this.getZ() - vec3.z;

      for (int i = 0; i < 8; i++) {
         Vec3 motion = this.getRandomVec3().scale(0.1F).subtract(this.getDeltaMovement().scale(0.1F));
         Vec3 pos = this.getRandomVec3().scale(0.3F);
         this.level()
            .addParticle((ParticleOptions)TensuraParticleTypes.BLACK_FIRE.get(), d0 + pos.x, d1 + 0.5 + pos.y, d2 + pos.z, motion.x, motion.y, motion.z);
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

   @Generated
   public float getAreaRadius() {
      return this.areaRadius;
   }

   @Generated
   public void setAreaRadius(float areaRadius) {
      this.areaRadius = areaRadius;
   }

   @Generated
   public int getAreaLife() {
      return this.areaLife;
   }

   @Generated
   public void setAreaLife(int areaLife) {
      this.areaLife = areaLife;
   }
}
