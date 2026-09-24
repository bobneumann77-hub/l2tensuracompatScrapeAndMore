package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class DimensionCutProjectile extends SpaceCutProjectile {
   public DimensionCutProjectile(EntityType<? extends DimensionCutProjectile> type, Level level) {
      super(type, level);
      this.setPiercingBlock(true);
      this.setElementalAttack(true);
      this.setElement(Element.SPACE);
      this.setSize(2.0F);
   }

   public DimensionCutProjectile(Level worldIn, LivingEntity shooter) {
      this((EntityType<? extends DimensionCutProjectile>)ProjectileEntityTypes.DIMENSION_CUT.get(), worldIn);
      this.setOwner(shooter);
   }
}
