package io.github.manasmods.tensura.entity.projectile;

import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import java.awt.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class ThrownHolyWater extends ThrowableItemProjectile {
   public ThrownHolyWater(EntityType<? extends ThrownHolyWater> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public ThrownHolyWater(Level pLevel, LivingEntity pShooter) {
      super((EntityType)ProjectileEntityTypes.HOLY_WATER.get(), pShooter, pLevel);
   }

   public ThrownHolyWater(Level pLevel, double pX, double pY, double pZ) {
      super((EntityType)ProjectileEntityTypes.HOLY_WATER.get(), pX, pY, pZ, pLevel);
   }

   protected Item getDefaultItem() {
      return Items.AIR;
   }

   protected double getDefaultGravity() {
      return 0.05F;
   }

   protected void onHitBlock(BlockHitResult pResult) {
      BlockState blockstate = this.level().getBlockState(pResult.getBlockPos());
      blockstate.onProjectileHit(this.level(), blockstate, pResult, this);
      if (!this.level().isClientSide()) {
         this.placeFire(this);
      }
   }

   protected void onHitEntity(EntityHitResult pResult) {
      this.placeFire(pResult.getEntity());
      if (!this.level().isClientSide()) {
         if (pResult.getEntity() instanceof Cow cow) {
            this.convertCow(cow);
         }
      }
   }

   protected void convertCow(Cow cow) {
   }

   protected void onHit(HitResult pResult) {
      Type hitResultType = pResult.getType();
      if (hitResultType == Type.ENTITY) {
         this.onHitEntity((EntityHitResult)pResult);
         this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), Context.of(this, null));
      } else if (hitResultType == Type.BLOCK) {
         BlockHitResult blockhitresult = (BlockHitResult)pResult;
         this.onHitBlock(blockhitresult);
         BlockPos blockpos = blockhitresult.getBlockPos();
         this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, Context.of(this, this.level().getBlockState(blockpos)));
      }

      if (!this.level().isClientSide) {
         this.applyHolyWater();
         this.level().levelEvent(2007, this.blockPosition(), new Color(0, 255, 195).getRGB());
         this.discard();
      }
   }

   private void applyHolyWater() {
   }

   protected void placeFire(Entity entity) {
   }
}
