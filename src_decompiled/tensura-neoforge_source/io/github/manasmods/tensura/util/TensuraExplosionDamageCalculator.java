package io.github.manasmods.tensura.util;

import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class TensuraExplosionDamageCalculator extends SimpleExplosionDamageCalculator {
   private final Optional<Float> customSeenPercentage;

   public TensuraExplosionDamageCalculator(
      boolean destroyBlocks,
      boolean hurtEntity,
      Optional<Float> customSeenPercentage,
      Optional<Float> knockBackMultiplier,
      Optional<HolderSet<Block>> immuneBlocks
   ) {
      super(destroyBlocks, hurtEntity, knockBackMultiplier, immuneBlocks);
      this.customSeenPercentage = customSeenPercentage;
   }

   public TensuraExplosionDamageCalculator(boolean destroyBlocks, boolean hurtEntity, Optional<Float> customSeenPercentage, Optional<Float> knockBackMultiplier) {
      this(destroyBlocks, hurtEntity, customSeenPercentage, knockBackMultiplier, Optional.empty());
   }

   public TensuraExplosionDamageCalculator(boolean destroyBlocks, boolean hurtEntity, Optional<Float> customSeenPercentage) {
      this(destroyBlocks, hurtEntity, customSeenPercentage, Optional.empty(), Optional.empty());
   }

   public TensuraExplosionDamageCalculator(boolean destroyBlocks, boolean hurtEntity) {
      this(destroyBlocks, hurtEntity, Optional.empty(), Optional.empty(), Optional.empty());
   }

   public TensuraExplosionDamageCalculator(boolean destroyBlocks) {
      this(destroyBlocks, true, Optional.empty(), Optional.empty(), Optional.empty());
   }

   public float getEntityDamageAmount(Explosion explosion, Entity entity) {
      float f = explosion.radius() * 2.0F;
      Vec3 vec3 = explosion.center();
      double d = Math.sqrt(entity.distanceToSqr(vec3)) / f;
      double e = (1.0 - d) * this.getSeenPercent(vec3, entity);
      return (float)((e * e + e) / 2.0 * 7.0 * f + 1.0);
   }

   private float getSeenPercent(Vec3 vec3, Entity entity) {
      return this.customSeenPercentage.orElseGet(() -> Explosion.getSeenPercent(vec3, entity));
   }
}
