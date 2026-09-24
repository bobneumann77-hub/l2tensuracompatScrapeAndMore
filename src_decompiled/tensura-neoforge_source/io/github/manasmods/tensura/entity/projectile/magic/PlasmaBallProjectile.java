package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PlasmaBallProjectile extends FireBoltProjectile {
   public PlasmaBallProjectile(EntityType<? extends PlasmaBallProjectile> entityType, Level level) {
      super(entityType, level);
      this.setSize(1.5F);
   }

   public PlasmaBallProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends FireBoltProjectile>)ProjectileEntityTypes.PLASMA_BALL.get(), levelIn);
      this.setOwner(shooter);
      this.setSize(1.5F);
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/plasma_fire.png");
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         List<LivingEntity> livingEntityList = this.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               this.getBoundingBox().inflate(4.0),
               entity -> (this.getOwner() == null || !entity.isAlliedTo(this.getOwner()) && !entity.is(this.getOwner()))
                  && !entity.fireImmune()
                  && !entity.hasEffect(MobEffects.FIRE_RESISTANCE)
            );
         if (!livingEntityList.isEmpty()) {
            for (LivingEntity pLivingEntity : livingEntityList) {
               pLivingEntity.setRemainingFireTicks(this.getBurnTicks());
               if (this.getOwner() instanceof Player player) {
                  pLivingEntity.setLastHurtByPlayer(player);
               }
            }
         }
      }
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      TensuraParticleHelper.spawnServerParticles(this.level(), (ParticleOptions)TensuraParticleTypes.PLASMA_FIRE.get(), x, y, z, 30, 1.5, 0.1, 1.5, 0.2, false);
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.DRAGON_BREATH, x, y, z, 30, 1.5, 0.1, 1.5, 0.2, false);
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
            .addParticle((ParticleOptions)TensuraParticleTypes.PLASMA_FIRE.get(), d0 + pos.x, d1 + 0.5 + pos.y, d2 + pos.z, motion.x, motion.y, motion.z);
         this.level().addParticle(ParticleTypes.SMOKE, d0 + pos.x, d1 + 0.5 + pos.y, d2 + pos.z, motion.x, motion.y, motion.z);
      }
   }
}
