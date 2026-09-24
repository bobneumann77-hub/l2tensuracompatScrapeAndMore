package io.github.manasmods.tensura.entity.projectile;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitEvent;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SevererBladeProjectile extends Projectile {
   private static final EntityDataAccessor<Integer> DELAY_TICK = SynchedEntityData.defineId(SevererBladeProjectile.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> LOOK_DISTANCE = SynchedEntityData.defineId(SevererBladeProjectile.class, EntityDataSerializers.FLOAT);
   private int piercingEntity = 0;
   private Vec3 delayVec = Vec3.ZERO;
   private Vec3 ownerOffset = Vec3.ZERO;
   protected int age;
   private float baseDamage = 3.0F;
   protected int mode = 0;
   protected ManasSkillInstance skill = null;
   private ItemStack sourceItem = new ItemStack((ItemLike)TensuraToolItems.SPATIAL_BLADE.get());

   public SevererBladeProjectile(EntityType<? extends SevererBladeProjectile> type, Level level) {
      super(type, level);
   }

   public SevererBladeProjectile(Level worldIn, LivingEntity shooter, boolean right, ItemStack pStack) {
      this((EntityType<? extends SevererBladeProjectile>)ProjectileEntityTypes.SEVERER_BLADE.get(), worldIn);
      this.setOwner(shooter);
      this.sourceItem = pStack.copy();
      float rot = shooter.yHeadRot + (right ? 60 : -60);
      this.setPos(
         shooter.getX() - shooter.getBbWidth() * 0.5 * Mth.sin(rot * (float) (Math.PI / 180.0)),
         shooter.getEyeY() - 0.2F,
         shooter.getZ() + shooter.getBbWidth() * 0.5 * Mth.cos(rot * (float) (Math.PI / 180.0))
      );
   }

   public SevererBladeProjectile(Level worldIn, LivingEntity shooter, ItemStack blade) {
      this((EntityType<? extends SevererBladeProjectile>)ProjectileEntityTypes.SEVERER_BLADE.get(), worldIn);
      this.setOwner(shooter);
      this.sourceItem = blade.copy();
   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      Vec3 vector3d = new Vec3(x, y, z)
         .normalize()
         .add(
            this.random.nextGaussian() * 0.0075F * inaccuracy,
            this.random.nextGaussian() * 0.0075F * inaccuracy,
            this.random.nextGaussian() * 0.0075F * inaccuracy
         )
         .scale(velocity);
      this.setDeltaMovement(vector3d);
      float f = Mth.sqrt((float)(vector3d.x * vector3d.x + vector3d.z * vector3d.z));
      this.setYRot((float)(Mth.atan2(vector3d.x, vector3d.z) * 180.0F / (float)Math.PI));
      this.setXRot((float)(Mth.atan2(vector3d.y, f) * 180.0F / (float)Math.PI));
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   private void updateShootVector() {
      Entity entity = this.getOwner();
      if (this.getLookDistance() != 0.0F) {
         if (entity instanceof LivingEntity owner) {
            Entity target = ObjectSelectionHelper.getTargetingEntity(owner, this.getLookDistance(), false, true);
            Vec3 pos;
            if (target != null) {
               pos = target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0);
            } else {
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), owner, Fluid.NONE, this.getLookDistance());
               pos = result.getLocation();
            }

            this.setDelayVec(pos.subtract(this.position()).normalize().scale(2.0));
         }
      }
   }

   public void tick() {
      super.tick();
      if (this.getDelayTick() > 0) {
         this.setDelayTick(this.getDelayTick() - 1);
         this.updateShootVector();
         Entity owner = this.getOwner();
         if (this.getDelayTick() == 0) {
            this.setDeltaMovement(this.getDelayVec());
            this.hurtMarked = true;
         } else if (this.ownerOffset != Vec3.ZERO && owner != null) {
            this.setPos(
               owner.getEyePosition()
                  .add(owner.getLookAngle().normalize().scale(1.0))
                  .add(this.ownerOffset.xRot(-owner.getXRot() * (float) (Math.PI / 180.0)).yRot(-owner.getYRot() * (float) (Math.PI / 180.0)))
            );
         }

         this.setRotation(this.getDelayVec(), true);
      } else {
         this.setRotation(this.getDeltaMovement(), false);
      }

      HitResult rayTraceResult = ProjectileUtil.getHitResultOnMoveVector(this, x$0 -> this.canHitEntity(x$0));
      Changeable<ProjectileHitResult> resultChangeable = Changeable.of(ProjectileHitResult.DEFAULT);
      Changeable<ProjectileDeflection> deflectionChangeable = Changeable.of(ProjectileDeflection.NONE);
      ((ProjectileHitEvent)EntityEvents.PROJECTILE_HIT.invoker()).hit(rayTraceResult, this, deflectionChangeable, resultChangeable);
      if (resultChangeable.get() != ProjectileHitResult.PASS) {
         this.onHit(rayTraceResult);
      }

      if (this.isInLava()) {
         this.remove(RemovalReason.DISCARDED);
      } else {
         this.setDeltaMovement(this.getDeltaMovement().scale(0.99F));
         this.setPos(this.position().add(this.getDeltaMovement()));
         if (this.getDelayTick() <= 0) {
            this.updateRotation();
            if (!this.isNoGravity()) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.02F, 0.0));
            }
         }
      }

      this.checkInsideBlocks();
      if (this.age++ > 600) {
         this.discard();
      }
   }

   public void setRotation(Vec3 vec3, boolean constantUpdate) {
      if (constantUpdate || this.xRotO == 0.0F && this.yRotO == 0.0F) {
         double d0 = vec3.horizontalDistance();
         this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI));
         this.setXRot((float)(Mth.atan2(vec3.y, d0) * 180.0F / (float)Math.PI));
         this.yRotO = this.getYRot();
         this.xRotO = this.getXRot();
      }
   }

   public void updateShootRotation() {
      this.updateShootVector();
      this.setRotation(this.getDelayVec(), true);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DELAY_TICK, 0);
      builder.define(LOOK_DISTANCE, 0.0F);
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("DelayTick", this.getDelayTick());
      compound.putFloat("LookDistance", this.getLookDistance());
      ItemStack stack = this.sourceItem;
      if (stack != null && !stack.isEmpty()) {
         compound.put("sourceItem", stack.saveOptional(this.registryAccess()));
      } else {
         compound.remove("sourceItem");
      }

      compound.putInt("Mode", this.getMode());
      if (this.skill != null) {
         compound.put("skill", this.skill.toNBT());
      }
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setDelayTick(compound.getInt("DelayTick"));
      this.setLookDistance(compound.getFloat("LookDistance"));
      if (compound.contains("sourceItem", 10)) {
         ItemStack parsed = ItemStack.parse(this.registryAccess(), compound.getCompound("sourceItem")).orElse(Items.DIRT.getDefaultInstance());
         if (!parsed.isEmpty()) {
            this.sourceItem = parsed;
         }
      }

      this.setMode(compound.getInt("Mode"));
      if (compound.contains("skill") && compound.get("skill") instanceof CompoundTag tag) {
         this.skill = ManasSkillInstance.fromNBT(tag);
      }
   }

   public int getDelayTick() {
      return (Integer)this.entityData.get(DELAY_TICK);
   }

   public void setDelayTick(int i) {
      this.entityData.set(DELAY_TICK, i);
   }

   public float getLookDistance() {
      return (Float)this.entityData.get(LOOK_DISTANCE);
   }

   public void setLookDistance(float i) {
      this.entityData.set(LOOK_DISTANCE, i);
   }

   private int getSeveranceLevel() {
      return TensuraEnchantmentHelper.getEnchantmentLevel(this.level(), TensuraEnchantments.SEVERANCE, this.sourceItem);
   }

   protected void channeling(SevererBladeProjectile entity) {
      if (!entity.level().isClientSide()) {
         if (entity.level().isThundering()) {
            if (TensuraEnchantmentHelper.getEnchantmentLevel(this.level(), Enchantments.CHANNELING, this.sourceItem) >= 1) {
               BlockPos blockpos = entity.blockPosition();
               Entity owner = entity.getOwner();
               if (entity.level().canSeeSky(blockpos)) {
                  LightningBolt lightningbolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(entity.level());
                  if (lightningbolt != null) {
                     lightningbolt.setPos(Vec3.atBottomCenterOf(blockpos));
                     lightningbolt.setCause(owner instanceof ServerPlayer player ? player : null);
                     entity.level().addFreshEntity(lightningbolt);
                  }
               }
            }
         }
      }
   }

   protected void onHitEntity(EntityHitResult pResult) {
      Entity entity = pResult.getEntity();
      Entity ownerEntity = this.getOwner();
      if (entity != ownerEntity) {
         if (this.level() instanceof ServerLevel level) {
            float baseDamage = this.baseDamage;
            DamageSource damagesource = TensuraDamageTypes.getIndirectEntityDamageSource(this.level(), TensuraDamageTypes.SEVERER_BLADE, this.getOwner(), this)
               .tensura$setAbilityInstance(this.getSkill())
               .tensura$setAbilityMode(this.getMode());
            if (this.getSeveranceLevel() > 0) {
               damagesource = damagesource.tensura$setElement(Element.SPACE);
               baseDamage += 7.0F;
            }

            if (entity instanceof LivingEntity livingEntity) {
               float damage = baseDamage + EnchantmentHelper.modifyDamage(level, this.sourceItem, livingEntity, damagesource, baseDamage);
               if (entity.hurt(damagesource, damage)) {
                  if (entity.getType() == EntityType.ENDERMAN) {
                     return;
                  }

                  EnchantmentHelper.doPostAttackEffectsWithItemSource(level, livingEntity, damagesource, this.sourceItem);
                  if (ownerEntity instanceof LivingEntity livingOwner) {
                     TensuraEnchantmentHelper.doAdditionalAfterDamage(level, livingEntity, livingOwner, damagesource, this.sourceItem, damage);
                  }

                  if (this.getSeveranceLevel() >= 5) {
                     entity.invulnerableTime = 0;
                  }
               }

               if (entity.getType() != EntityType.ENDERMAN && ownerEntity instanceof LivingEntity livingOwner) {
                  TensuraEnchantmentHelper.doAdditionalAfterAttack(level, livingEntity, livingOwner, damagesource, this.sourceItem, damage);
               }
            }

            this.piercingEntity++;
            if (this.piercingEntity == 1) {
               this.channeling(this);
            }

            if (this.piercingEntity >= 5 + TensuraEnchantmentHelper.getEnchantmentLevel(level, Enchantments.PIERCING, this.sourceItem)) {
               this.discard();
               this.playSound(SoundEvents.ITEM_BREAK, 0.5F, 0.75F);
            }
         }
      }
   }

   protected void onHitBlock(BlockHitResult pResult) {
      if (!this.level().isClientSide()) {
         this.channeling(this);
         this.remove(RemovalReason.DISCARDED);
         this.playSound(SoundEvents.ITEM_BREAK, 0.5F, 0.75F);
      } else {
         this.level().addParticle(ParticleTypes.ENCHANTED_HIT, this.getX(), this.getY(), this.getZ(), 0.0, 0.05, 0.0);
      }
   }

   public void shootToward(Entity pEntity, float pVelocity, float pInaccuracy) {
      Vec3 towardEntity = new Vec3(pEntity.getX() - this.getX(), pEntity.getEyeY() - this.getY(), pEntity.getZ() - this.getZ()).scale(0.1F);
      this.shoot(towardEntity.x(), towardEntity.y(), towardEntity.z(), pVelocity, pInaccuracy);
   }

   @Generated
   public void setDelayVec(Vec3 delayVec) {
      this.delayVec = delayVec;
   }

   @Generated
   public Vec3 getDelayVec() {
      return this.delayVec;
   }

   @Generated
   public void setOwnerOffset(Vec3 ownerOffset) {
      this.ownerOffset = ownerOffset;
   }

   @Generated
   public Vec3 getOwnerOffset() {
      return this.ownerOffset;
   }

   @Generated
   public void setBaseDamage(float baseDamage) {
      this.baseDamage = baseDamage;
   }

   @Generated
   public int getMode() {
      return this.mode;
   }

   @Generated
   public void setMode(int mode) {
      this.mode = mode;
   }

   @Generated
   public ManasSkillInstance getSkill() {
      return this.skill;
   }

   @Generated
   public void setSkill(ManasSkillInstance skill) {
      this.skill = skill;
   }
}
