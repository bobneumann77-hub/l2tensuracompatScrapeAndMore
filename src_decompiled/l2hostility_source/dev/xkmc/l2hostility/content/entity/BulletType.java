package dev.xkmc.l2hostility.content.entity;

import dev.xkmc.l2damagetracker.contents.attack.DamageData.Attack;
import dev.xkmc.l2library.content.explosion.BaseExplosion;
import dev.xkmc.l2library.content.explosion.BaseExplosionContext;
import dev.xkmc.l2library.content.explosion.ExplosionHandler;
import dev.xkmc.l2library.content.explosion.ParticleExplosionContext;
import dev.xkmc.l2library.content.explosion.VanillaExplosionContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public enum BulletType {
   PLAIN(4.0F, true),
   EXPLODE(4.0F, true);

   private final float damage;
   private final boolean limit;

   BulletType(float damage, boolean limit) {
      this.damage = damage;
      this.limit = limit;
   }

   public float getDamage(int level) {
      return this.damage * level;
   }

   public void onHit(HostilityBullet bullet, HitResult result, int level) {
      if (this == EXPLODE) {
         Vec3 pos = result.getLocation();
         ExplosionHandler.explode(
            new BaseExplosion(
               new BaseExplosionContext(bullet.level(), pos.x, pos.y, pos.z, 1 + level),
               new VanillaExplosionContext(bullet, null, null, false, BlockInteraction.KEEP),
               bullet::isTarget,
               ParticleExplosionContext.of(1 + level)
            )
         );
      }
   }

   public boolean onAttackedByOthers(int level, LivingEntity entity, Attack event) {
      if (event.getSource().getDirectEntity() instanceof ShulkerBullet) {
         return true;
      } else {
         return this == EXPLODE ? event.getSource().is(DamageTypeTags.IS_EXPLOSION) : false;
      }
   }

   public boolean limit() {
      return this.limit;
   }
}
