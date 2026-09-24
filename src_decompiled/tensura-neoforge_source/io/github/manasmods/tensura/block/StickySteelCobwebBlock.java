package io.github.manasmods.tensura.block;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.Vec3;

public class StickySteelCobwebBlock extends StickyCobwebBlock {
   public StickySteelCobwebBlock(Properties pProperties) {
      super(pProperties);
   }

   @Override
   public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
      if (!pEntity.getType().is(TensuraEntityTags.WEB_WALKABLE_MOBS)) {
         double xChange = Math.abs(pEntity.getX() - pEntity.xOld);
         double yChange = Math.abs(pEntity.getY() - pEntity.yOld);
         double zChange = Math.abs(pEntity.getZ() - pEntity.zOld);
         if (xChange >= 0.001F || yChange >= 0.001F || zChange >= 0.001F) {
            DamageSource source = TensuraDamageTypes.getDamageSource(pLevel, TensuraDamageTypes.STEEL_THREAD);
            pEntity.hurt(source, 2.0F);
         }

         pEntity.makeStuckInBlock(pState, new Vec3(0.2, 0.04F, 0.2));
      }
   }
}
