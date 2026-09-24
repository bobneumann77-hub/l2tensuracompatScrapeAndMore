package io.github.manasmods.tensura.data.slotting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record SlottingCombination(
   int id,
   int earth,
   int fire,
   int space,
   int water,
   int wind,
   double earthMultiplier,
   double fireMultiplier,
   double spaceMultiplier,
   double waterMultiplier,
   double windMultiplier,
   Optional<SlottingProjectile> projectile,
   int durabilityCost,
   int projectileCost
) {
   public static final Codec<SlottingCombination> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.INT.fieldOf("combinationID").forGetter(SlottingCombination::id),
            Codec.INT.optionalFieldOf("earthCores", 0).forGetter(SlottingCombination::earth),
            Codec.INT.optionalFieldOf("fireCores", 0).forGetter(SlottingCombination::fire),
            Codec.INT.optionalFieldOf("spaceCores", 0).forGetter(SlottingCombination::space),
            Codec.INT.optionalFieldOf("waterCores", 0).forGetter(SlottingCombination::water),
            Codec.INT.optionalFieldOf("windCores", 0).forGetter(SlottingCombination::wind),
            Codec.DOUBLE.optionalFieldOf("earthMultiplier", 0.0).forGetter(SlottingCombination::earthMultiplier),
            Codec.DOUBLE.optionalFieldOf("fireMultiplier", 0.0).forGetter(SlottingCombination::fireMultiplier),
            Codec.DOUBLE.optionalFieldOf("spaceMultiplier", 0.0).forGetter(SlottingCombination::spaceMultiplier),
            Codec.DOUBLE.optionalFieldOf("waterMultiplier", 0.0).forGetter(SlottingCombination::waterMultiplier),
            Codec.DOUBLE.optionalFieldOf("windMultiplier", 0.0).forGetter(SlottingCombination::windMultiplier),
            SlottingProjectile.CODEC.optionalFieldOf("slottingProjectile").forGetter(SlottingCombination::projectile),
            Codec.INT.optionalFieldOf("durability", 5).forGetter(SlottingCombination::durabilityCost),
            Codec.INT.optionalFieldOf("projectileCost", 10).forGetter(SlottingCombination::projectileCost)
         )
         .apply(instance, SlottingCombination::new)
   );

   public int[] getCores() {
      return new int[]{this.earth, this.fire, this.space, this.water, this.wind};
   }

   public double[] getElementMultiplier() {
      return new double[]{this.earthMultiplier, this.fireMultiplier, this.spaceMultiplier, this.waterMultiplier, this.windMultiplier};
   }

   public static SlottingCombination of(
      int combinationID,
      int earthCore,
      int fireCore,
      int spaceCore,
      int waterCore,
      int windCore,
      double earthMultiplier,
      double fireMultiplier,
      double spaceMultiplier,
      double waterMultiplier,
      double windMultiplier,
      String entity,
      float speed,
      float damage,
      float knockForce,
      float explosionRadius,
      int burnTicks,
      boolean noGravity,
      String effect,
      int level,
      int ticks,
      float range,
      int durabilityCost,
      int projectileCost
   ) {
      SlottingProjectile projectile = new SlottingProjectile(
         ResourceLocation.parse(entity),
         speed,
         damage,
         knockForce,
         explosionRadius,
         burnTicks,
         noGravity,
         new SlottingStatusEffect(ResourceLocation.parse(effect), level, ticks, range)
      );
      return new SlottingCombination(
         combinationID,
         earthCore,
         fireCore,
         spaceCore,
         waterCore,
         windCore,
         earthMultiplier,
         fireMultiplier,
         spaceMultiplier,
         waterMultiplier,
         windMultiplier,
         Optional.of(projectile),
         durabilityCost,
         projectileCost
      );
   }

   public static SlottingCombination of(
      int combinationID,
      int earthCore,
      int fireCore,
      int spaceCore,
      int waterCore,
      int windCore,
      double earthMultiplier,
      double fireMultiplier,
      double spaceMultiplier,
      double waterMultiplier,
      double windMultiplier,
      String entity,
      float speed,
      float damage,
      float knockForce,
      float explosionRadius,
      int burnTicks,
      boolean noGravity,
      String effect,
      int level,
      int ticks,
      int durabilityCost,
      int projectileCost
   ) {
      SlottingProjectile projectile = new SlottingProjectile(
         ResourceLocation.parse(entity),
         speed,
         damage,
         knockForce,
         explosionRadius,
         burnTicks,
         noGravity,
         new SlottingStatusEffect(ResourceLocation.parse(effect), level, ticks, 0.0F)
      );
      return new SlottingCombination(
         combinationID,
         earthCore,
         fireCore,
         spaceCore,
         waterCore,
         windCore,
         earthMultiplier,
         fireMultiplier,
         spaceMultiplier,
         waterMultiplier,
         windMultiplier,
         Optional.of(projectile),
         durabilityCost,
         projectileCost
      );
   }

   public static SlottingCombination of(
      int combinationID,
      int earthCore,
      int fireCore,
      int spaceCore,
      int waterCore,
      int windCore,
      double earthMultiplier,
      double fireMultiplier,
      double spaceMultiplier,
      double waterMultiplier,
      double windMultiplier,
      String entity,
      float speed,
      float damage,
      float knockForce,
      float explosionRadius,
      int burnTicks,
      boolean noGravity,
      String effect,
      int level,
      int durabilityCost,
      int projectileCost
   ) {
      SlottingProjectile projectile = new SlottingProjectile(
         ResourceLocation.parse(entity),
         speed,
         damage,
         knockForce,
         explosionRadius,
         burnTicks,
         noGravity,
         new SlottingStatusEffect(ResourceLocation.parse(effect), level, 100, 0.0F)
      );
      return new SlottingCombination(
         combinationID,
         earthCore,
         fireCore,
         spaceCore,
         waterCore,
         windCore,
         earthMultiplier,
         fireMultiplier,
         spaceMultiplier,
         waterMultiplier,
         windMultiplier,
         Optional.of(projectile),
         durabilityCost,
         projectileCost
      );
   }

   public static SlottingCombination of(
      int combinationID,
      int earthCore,
      int fireCore,
      int spaceCore,
      int waterCore,
      int windCore,
      double earthMultiplier,
      double fireMultiplier,
      double spaceMultiplier,
      double waterMultiplier,
      double windMultiplier,
      String entity,
      float speed,
      float damage,
      float knockForce,
      float explosionRadius,
      int burnTicks,
      boolean noGravity,
      String effect,
      int durabilityCost,
      int projectileCost
   ) {
      SlottingProjectile projectile = new SlottingProjectile(
         ResourceLocation.parse(entity),
         speed,
         damage,
         knockForce,
         explosionRadius,
         burnTicks,
         noGravity,
         new SlottingStatusEffect(ResourceLocation.parse(effect), 0, 100, 0.0F)
      );
      return new SlottingCombination(
         combinationID,
         earthCore,
         fireCore,
         spaceCore,
         waterCore,
         windCore,
         earthMultiplier,
         fireMultiplier,
         spaceMultiplier,
         waterMultiplier,
         windMultiplier,
         Optional.of(projectile),
         durabilityCost,
         projectileCost
      );
   }

   public static SlottingCombination of(
      int combinationID,
      int earthCore,
      int fireCore,
      int spaceCore,
      int waterCore,
      int windCore,
      double earthMultiplier,
      double fireMultiplier,
      double spaceMultiplier,
      double waterMultiplier,
      double windMultiplier,
      String entity,
      float speed,
      float damage,
      float knockForce,
      float explosionRadius,
      int burnTicks,
      boolean noGravity,
      int durabilityCost,
      int projectileCost
   ) {
      SlottingProjectile projectile = new SlottingProjectile(ResourceLocation.parse(entity), speed, damage, knockForce, explosionRadius, burnTicks, noGravity);
      return new SlottingCombination(
         combinationID,
         earthCore,
         fireCore,
         spaceCore,
         waterCore,
         windCore,
         earthMultiplier,
         fireMultiplier,
         spaceMultiplier,
         waterMultiplier,
         windMultiplier,
         Optional.of(projectile),
         durabilityCost,
         projectileCost
      );
   }

   public static SlottingCombination of(
      int combinationID,
      int earthCore,
      int fireCore,
      int spaceCore,
      int waterCore,
      int windCore,
      double earthMultiplier,
      double fireMultiplier,
      double spaceMultiplier,
      double waterMultiplier,
      double windMultiplier
   ) {
      return new SlottingCombination(
         combinationID,
         earthCore,
         fireCore,
         spaceCore,
         waterCore,
         windCore,
         earthMultiplier,
         fireMultiplier,
         spaceMultiplier,
         waterMultiplier,
         windMultiplier,
         Optional.empty(),
         10,
         50
      );
   }
}
