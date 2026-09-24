package io.github.manasmods.tensura.data.slotting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record SlottingProjectile(
   ResourceLocation entity, float speed, float damage, float knockback, float explosion, int burn, boolean noGravity, Optional<SlottingStatusEffect> effect
) {
   public static final Codec<SlottingProjectile> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("entity").forGetter(SlottingProjectile::entity),
            Codec.FLOAT.optionalFieldOf("speed", 0.0F).forGetter(SlottingProjectile::speed),
            Codec.FLOAT.optionalFieldOf("damage", 0.0F).forGetter(SlottingProjectile::damage),
            Codec.FLOAT.optionalFieldOf("knockbackForce", 0.0F).forGetter(SlottingProjectile::knockback),
            Codec.FLOAT.optionalFieldOf("explosionRadius", 0.0F).forGetter(SlottingProjectile::explosion),
            Codec.INT.optionalFieldOf("burnTicks", 0).forGetter(SlottingProjectile::burn),
            Codec.BOOL.optionalFieldOf("noGravity", false).forGetter(SlottingProjectile::noGravity),
            SlottingStatusEffect.CODEC.optionalFieldOf("effect").forGetter(SlottingProjectile::effect)
         )
         .apply(instance, SlottingProjectile::new)
   );

   public SlottingProjectile(
      ResourceLocation entity, float speed, float damage, float knockback, float explosion, int burn, boolean noGravity, SlottingStatusEffect effect
   ) {
      this(entity, speed, damage, knockback, explosion, burn, noGravity, Optional.of(effect));
   }

   public SlottingProjectile(ResourceLocation entity, float speed, float damage, float knockback, float explosion, int burn, boolean noGravity) {
      this(entity, speed, damage, knockback, explosion, burn, noGravity, Optional.empty());
   }
}
