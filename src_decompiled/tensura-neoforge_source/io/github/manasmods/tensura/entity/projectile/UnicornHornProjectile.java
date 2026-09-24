package io.github.manasmods.tensura.entity.projectile;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnicornHornProjectile extends AbstractArrow {
   private int life;

   public UnicornHornProjectile(EntityType<? extends UnicornHornProjectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public UnicornHornProjectile(Level level, double d, double e, double f, ItemStack itemStack, @Nullable ItemStack itemStack2) {
      super((EntityType)ProjectileEntityTypes.UNICORN_HORN.get(), d, e, f, level, itemStack, itemStack2);
      this.setBaseDamage(this.getBaseDamage() + 2.75);
   }

   public UnicornHornProjectile(Level level, LivingEntity livingEntity, ItemStack itemStack, @Nullable ItemStack itemStack2) {
      super((EntityType)ProjectileEntityTypes.UNICORN_HORN.get(), livingEntity, level, itemStack, itemStack2);
      this.setBaseDamage(this.getBaseDamage() + 2.75);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("Life", this.life);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.life = pCompound.getInt("Life");
   }

   @NotNull
   protected ItemStack getDefaultPickupItem() {
      return new ItemStack((ItemLike)TensuraMobDropItems.UNICORN_HORN.get());
   }

   protected float getWaterInertia() {
      return 0.55F;
   }

   public void tick() {
      super.tick();
      if (this.life == 0 && !this.isSilent()) {
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, TensuraSkill.ABILITY_SOUND, 3.0F, 1.0F);
      }

      this.life++;
   }

   protected void onHitEntity(EntityHitResult pResult) {
      if (!this.level().isClientSide()) {
         this.explode(pResult.getEntity() instanceof LivingEntity living ? living : null);
      }

      super.onHitEntity(pResult);
   }

   protected void onHitBlock(BlockHitResult pResult) {
      BlockPos blockpos = new BlockPos(pResult.getBlockPos());
      this.level().getBlockState(blockpos).entityInside(this.level(), blockpos, this);
      if (!this.level().isClientSide()) {
         this.explode(null);
      }

      super.onHitBlock(pResult);
   }

   private void explode(@Nullable LivingEntity living) {
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, TensuraSkill.ABILITY_SOUND, 3.0F, 1.0F);
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_TWINKLE, TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
      this.level().broadcastEntityEvent(this, (byte)17);
      this.gameEvent(GameEvent.EXPLODE, this.getOwner());
      this.dealExplosionDamage(living);
      this.discard();
   }

   private void dealExplosionDamage(@Nullable LivingEntity living) {
      Vec3 vec3 = this.position();

      for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0))) {
         if (this.distanceToSqr(target) <= 25.0 && target != living) {
            boolean flag = false;

            for (int i = 0; i < 2; i++) {
               Vec3 vec31 = new Vec3(target.getX(), target.getY(0.5 * i), target.getZ());
               HitResult hitresult = this.level().clip(new ClipContext(vec3, vec31, Block.COLLIDER, Fluid.NONE, this));
               if (hitresult.getType() == Type.MISS) {
                  flag = true;
                  break;
               }
            }

            if (flag) {
               float amount = 15.0F * (float)Math.sqrt((5.0 - this.distanceTo(target)) / 5.0);
               DamageSource source = TensuraDamageTypes.getIndirectEntityDamageSource(this.level(), TensuraDamageTypes.UNICORN_HORN, this.getOwner(), this);
               target.hurt(source, amount);
            }
         }
      }
   }

   public void handleEntityEvent(byte pId) {
      if (pId == 17 && this.level().isClientSide) {
         for (int i = 0; i < this.random.nextInt(3) + 2; i++) {
            this.level()
               .addParticle(
                  ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05
               );
         }
      }

      super.handleEntityEvent(pId);
   }
}
