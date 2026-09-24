package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.tensura.entity.projectile.MonsterSpitProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface SpittingRangedMonster extends RangedAttackMob {
   void spitHit(LivingEntity var1);

   default void performRangedAttack(BlockPos target, double xzOffset, double yOffset) {
      this.performRangedAttack(target, xzOffset, yOffset, Vec3.ZERO);
   }

   default void performRangedAttack(BlockPos target, double xzOffset, double yOffset, Vec3 additionalOffset) {
      if (this instanceof LivingEntity living) {
         MonsterSpitProjectile var25 = new MonsterSpitProjectile(living.level(), living);
         float angle = (float) (Math.PI / 180.0) * living.yBodyRot;
         double xOffset = xzOffset * Mth.sin((float)(Math.PI + angle));
         double zOffset = xzOffset * Mth.cos(angle);
         Vec3 vec3 = new Vec3(living.getX() + xOffset, living.getY() + yOffset, living.getZ() + zOffset);
         var25.moveTo(vec3.add(additionalOffset), living.getYRot(), living.getXRot());
         double d0 = target.getY() + 0.5F;
         double d1 = target.getX() - vec3.x();
         double d2 = d0 - vec3.y();
         double d3 = target.getZ() - vec3.z();
         double f = Math.sqrt(d1 * d1 + d3 * d3) * 0.2F;
         var25.shoot(d1, d2 + f, d3, 1.2F, 0.0F);
         living.level().addFreshEntity(var25);
      }
   }

   default void performRangedAttack(@NotNull LivingEntity target, double xzOffset, double yOffset) {
      this.performRangedAttack(target, xzOffset, yOffset, Vec3.ZERO);
   }

   default void performRangedAttack(@NotNull LivingEntity target, double xzOffset, double yOffset, Vec3 additionalOffset) {
      if (this instanceof LivingEntity living) {
         MonsterSpitProjectile var25 = new MonsterSpitProjectile(living.level(), living);
         float angle = (float) (Math.PI / 180.0) * living.yBodyRot;
         double xOffset = xzOffset * Mth.sin((float)(Math.PI + angle));
         double zOffset = xzOffset * Mth.cos(angle);
         Vec3 vec3 = new Vec3(living.getX() + xOffset, living.getY() + yOffset, living.getZ() + zOffset).add(additionalOffset);
         var25.moveTo(vec3, living.getYRot(), living.getXRot());
         double d0 = target.getY() + target.getBbHeight() / 2.0F;
         double d1 = target.getX() - vec3.x();
         double d2 = d0 - vec3.y();
         double d3 = target.getZ() - vec3.z();
         double f = Math.sqrt(d1 * d1 + d3 * d3) * 0.2F;
         var25.shoot(d1, d2 + f, d3, 1.2F, 0.0F);
         living.level().addFreshEntity(var25);
      }
   }

   default void performRangedAttack(@NotNull LivingEntity target, float pDistanceFactor) {
      if (this instanceof LivingEntity living) {
         MonsterSpitProjectile spit = new MonsterSpitProjectile(living.level(), living);
         spit.moveTo(living.getX(), living.getY() + 1.0, living.getZ(), living.getYRot(), living.getXRot());
         double d0 = target.getY() + target.getBbHeight() / 2.0F;
         double d1 = target.getX() - living.getX();
         double d2 = d0 - spit.getY();
         double d3 = target.getZ() - living.getZ();
         double f = Math.sqrt(d1 * d1 + d3 * d3) * 0.2F;
         spit.shoot(d1, d2 + f, d3, 1.2F, 0.0F);
         living.level().addFreshEntity(spit);
      }
   }

   default void particleSpawning(MonsterSpitProjectile projectile, ParticleOptions particleOptions, int particleAmount) {
      float radius = projectile.getBbWidth();
      double x = projectile.getX() + (projectile.level().random.nextDouble() - 0.5) * radius;
      double y = projectile.getY() + (projectile.level().random.nextDouble() - 0.5) * radius;
      double z = projectile.getZ() + (projectile.level().random.nextDouble() - 0.5) * radius;

      for (int j = 0; j < particleAmount; j++) {
         double newX = x + projectile.getRandom().nextGaussian() / 4.0;
         double newY = y + projectile.getRandom().nextGaussian() / 4.0;
         double newZ = z + projectile.getRandom().nextGaussian() / 4.0;
         projectile.level().addParticle(particleOptions, newX, newY, newZ, 0.0, -0.1, 0.0);
      }
   }

   default void spitParticle(MonsterSpitProjectile projectile) {
   }

   default void impactEffect(MonsterSpitProjectile spit, double x, double y, double z) {
   }
}
