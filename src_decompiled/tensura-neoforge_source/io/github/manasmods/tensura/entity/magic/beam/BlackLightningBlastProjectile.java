package io.github.manasmods.tensura.entity.magic.beam;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.magic.lightning.BlackLightningBolt;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BlackLightningBlastProjectile extends BeamProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public BlackLightningBlastProjectile(EntityType<? extends BlackLightningBlastProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
   }

   public BlackLightningBlastProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends BlackLightningBlastProjectile>)MiscEntityTypes.BLACK_LIGHTNING_BLAST.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceLocation[] getTextureLocation() {
      return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/black_lightning.png")};
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.BLACK_LIGHTNING;
   }

   @Override
   protected boolean shouldStopFollowOwner() {
      return this.getLife() - this.getAge() < 1;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         Vec3 startPos = this.position();
         Vec3 collisionPos = new Vec3(this.getCollideX(), this.getCollideY(), this.getCollideZ());
         Vec3 offSetToTarget = collisionPos.subtract(startPos);

         for (int i = 1; i < Mth.floor(offSetToTarget.length()) - 1; i++) {
            this.rayParticles(startPos.add(offSetToTarget.normalize().scale(i)), i);
         }

         this.hitParticles(this.getCollideX(), this.getCollideY(), this.getCollideZ());
      }
   }

   @Override
   protected boolean dealDamage(Entity target) {
      if (this.damage <= 0.0F || target instanceof ItemEntity) {
         return false;
      }

      if (super.dealDamage(target)) {
         TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.BLACK_LIGHTNING_EFFECT.get());
         if (target instanceof LivingEntity living) {
            BlackLightningBolt dummyBolt = new BlackLightningBolt(this.level(), this.getOwner());
            dummyBolt.setTensuraDamage(0.0F);
            living.thunderHit((ServerLevel)this.level(), dummyBolt);
            living.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 100, 1, true, false, true), this.getOwner());
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      if (this.tickCount % 2 == 0) {
         if (this.level().getBlockState(new BlockPos((int)x, (int)y, (int)z)).isSolid()) {
            TensuraParticleHelper.spawnParticlesLikeServer(
               this.level(), (ParticleOptions)TensuraParticleTypes.BLACK_LIGHTNING_EFFECT.get(), x, y, z, (int)this.getSize() * 10, 0.5, 0.5, 0.5, 1.0, false
            );
         }
      }
   }

   @Override
   public void rayParticles(Vec3 pos, int i) {
      if (this.tickCount % this.random.nextInt(5, 12) == 0) {
         TensuraParticleHelper.addParticlesAroundPos(
            this.random, this.level(), pos, (ParticleOptions)TensuraParticleTypes.BLACK_LIGHTNING_SPARK.get(), 1.0F + this.getVisualSize(), 1
         );
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.black_lightning.start")))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
