package io.github.manasmods.tensura.entity.projectile;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitEvent;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.entity.template.subclass.SpittingRangedMonster;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class MonsterSpitProjectile extends Projectile {
   public MonsterSpitProjectile(EntityType<? extends MonsterSpitProjectile> type, Level level) {
      super(type, level);
   }

   public MonsterSpitProjectile(Level worldIn, LivingEntity monster) {
      this((EntityType<? extends MonsterSpitProjectile>)ProjectileEntityTypes.MONSTER_SPIT.get(), worldIn);
      this.setOwner(monster);
   }

   public boolean canBeHitByProjectile() {
      return false;
   }

   public void tick() {
      super.tick();
      if (this.getOwner() instanceof SpittingRangedMonster entity) {
         entity.spitParticle(this);
      }

      HitResult rayTraceResult = ProjectileUtil.getHitResultOnMoveVector(this, x$0 -> this.canHitEntity(x$0));
      Changeable<ProjectileHitResult> resultChangeable = Changeable.of(ProjectileHitResult.DEFAULT);
      Changeable<ProjectileDeflection> deflectionChangeable = Changeable.of(ProjectileDeflection.NONE);
      ((ProjectileHitEvent)EntityEvents.PROJECTILE_HIT.invoker()).hit(rayTraceResult, this, deflectionChangeable, resultChangeable);
      if (resultChangeable.get() != ProjectileHitResult.PASS) {
         this.onHit(rayTraceResult);
      }

      if (!this.isInLava() && !this.isInWaterOrBubble()) {
         this.setDeltaMovement(this.getDeltaMovement().scale(0.99F));
         this.setPos(this.position().add(this.getDeltaMovement()));
         ProjectileUtil.rotateTowardsMovement(this, 1.0F);
         if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.02F, 0.0));
         }
      } else {
         this.remove(RemovalReason.DISCARDED);
      }

      this.checkInsideBlocks();
   }

   protected void defineSynchedData(Builder builder) {
   }

   public RandomSource getRandom() {
      return this.random;
   }

   protected void onHit(HitResult result) {
      if (!this.level().isClientSide && this.getOwner() instanceof SpittingRangedMonster mob) {
         double x = this.xOld;
         double y = this.yOld;
         double z = this.zOld;
         mob.impactEffect(this, x, y, z);
      }

      super.onHit(result);
   }

   protected void onHitEntity(EntityHitResult pResult) {
      Entity entity = pResult.getEntity();
      if (entity != this.getOwner()) {
         if (this.getOwner() instanceof SpittingRangedMonster monster && entity instanceof LivingEntity livingEntity) {
            monster.spitHit(livingEntity);
         }

         this.discard();
      }
   }

   protected void onHitBlock(BlockHitResult pResult) {
      super.onHitBlock(pResult);
      this.discard();
   }
}
