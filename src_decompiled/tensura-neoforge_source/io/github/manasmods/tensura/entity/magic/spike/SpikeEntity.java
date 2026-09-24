package io.github.manasmods.tensura.entity.magic.spike;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.unique.AntiSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpikeEntity extends TensuraProjectile {
   private static final EntityDataAccessor<Integer> EXTENDING_TICK = SynchedEntityData.defineId(SpikeEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(SpikeEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(SpikeEntity.class, EntityDataSerializers.FLOAT);
   protected float contactDamage = 0.0F;
   protected float contactSecondaryDamage = 0.0F;
   protected float contactRange = 0.1F;
   protected int contactInterval = 20;
   protected float contactPush = 0.0F;
   protected boolean pushEntityUp = false;
   protected float aoeDamage = 0.0F;
   protected float aoeRange = 5.0F;
   protected int aoeInterval = 20;
   protected float breakRange = 5.0F;
   @Nullable
   private MobEffectInstance breakEffect = null;

   public SpikeEntity(EntityType<? extends SpikeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public SpikeEntity(EntityType<? extends SpikeEntity> pEntityType, Level pLevel, LivingEntity pOwner) {
      this(pEntityType, pLevel);
      this.setOwner(pOwner);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(HEALTH, 20.0F);
      builder.define(HEIGHT, 3.0F);
      builder.define(EXTENDING_TICK, 20);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setHealth(pCompound.getFloat("Health"));
      this.setHeight(pCompound.getFloat("BlockHeight"));
      this.setExtendingTick(pCompound.getInt("ExtendingTick"));
      this.setContactDamage(pCompound.getFloat("ContactDamage"));
      this.setContactSecondaryDamage(pCompound.getFloat("ContactSecondaryDamage"));
      this.setContactRange(pCompound.getFloat("ContactRange"));
      this.setContactInterval(pCompound.getInt("ContactInterval"));
      this.setContactPush(pCompound.getFloat("ContactPush"));
      this.setPushEntityUp(pCompound.getBoolean("PushEntityUp"));
      this.setAoeDamage(pCompound.getFloat("AoeDamage"));
      this.setAoeRange(pCompound.getFloat("AoeRange"));
      this.setAoeInterval(pCompound.getInt("AoeInterval"));
      this.setBreakRange(pCompound.getFloat("BreakRange"));
      if (pCompound.contains("BreakEffect")) {
         this.setBreakEffect(MobEffectInstance.load(pCompound.getCompound("BreakEffect")));
      }
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("Health", this.getHealth());
      pCompound.putFloat("BlockHeight", this.getHeight());
      pCompound.putInt("ExtendingTick", this.getExtendingTick());
      pCompound.putFloat("ContactDamage", this.getContactDamage());
      pCompound.putFloat("ContactSecondaryDamage", this.getContactSecondaryDamage());
      pCompound.putFloat("ContactRange", this.getContactRange());
      pCompound.putInt("ContactInterval", this.getContactInterval());
      pCompound.putFloat("ContactPush", this.getContactPush());
      pCompound.putBoolean("PushEntityUp", this.isPushEntityUp());
      pCompound.putFloat("AoeDamage", this.getAoeDamage());
      pCompound.putFloat("AoeRange", this.getAoeRange());
      pCompound.putInt("AoeInterval", this.getAoeInterval());
      pCompound.putFloat("BreakRange", this.getBreakRange());
      if (this.getBreakEffect() != null) {
         pCompound.put("BreakEffect", this.getBreakEffect().save());
      }
   }

   public void setHealth(float pDamageTaken) {
      this.entityData.set(HEALTH, pDamageTaken);
   }

   public float getHealth() {
      return (Float)this.entityData.get(HEALTH);
   }

   public void setHeight(float height) {
      this.entityData.set(HEIGHT, height);
   }

   public float getHeight() {
      return (Float)this.entityData.get(HEIGHT);
   }

   public void setExtendingTick(int tick) {
      this.entityData.set(EXTENDING_TICK, tick);
   }

   public int getExtendingTick() {
      return (Integer)this.entityData.get(EXTENDING_TICK);
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else if (!super.canHitEntity(pTarget)) {
         return false;
      } else {
         return pTarget instanceof SpikeEntity ? false : !(this.getOwner() instanceof LivingEntity owner && owner.isAlliedTo(pTarget));
      }
   }

   public ResourceKey<DamageType> getContactDamageType() {
      return TensuraDamageTypes.MAGIC_GENERIC;
   }

   public ResourceKey<DamageType> getSecondaryContactDamageType() {
      return this.getContactDamageType();
   }

   public boolean shouldTakeDamage(DamageSource pSource) {
      return true;
   }

   public void copyBlockHealth(Block block, float multiplier) {
      this.setHealth(block.defaultDestroyTime() * multiplier);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (!this.shouldTakeDamage(pSource)) {
         this.markHurt();
         return false;
      }

      if (!this.level().isClientSide() && !this.isRemoved()) {
         if (pSource.getDirectEntity() instanceof LivingEntity attacker) {
            if (((AntiSkill)UniqueSkills.ANTI_SKILL.get()).isInSlot(attacker)) {
               pAmount = this.getHealth();
            } else if (attacker.getMainHandItem().getItem() instanceof PickaxeItem) {
               pAmount *= 2.0F;
            }
         }

         this.setHealth(this.getHealth() - pAmount);
         this.gameEvent(GameEvent.ENTITY_DAMAGE, pSource.getEntity());
         if (this.getHealth() <= 0.0F) {
            this.onBreak();
         } else {
            this.markHurt();
         }
      }

      return true;
   }

   public void onBreak() {
      super.remove();
   }

   @Override
   public void remove() {
      super.remove();
      MobEffectInstance effect = this.getBreakEffect();
      if (effect != null) {
         for (LivingEntity target : this.level()
            .getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(this.getContactRange()), this::canHitEntity)) {
            ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
            TensuraMobEffect.addEffect(target, effect, this.getOwner(), skill, this.getMode());
         }
      }
   }

   public boolean shouldPushUp() {
      return this.isPushEntityUp();
   }

   public boolean canCollideWith(@NotNull Entity entity) {
      Entity owner = this.getOwner();
      if (owner != null) {
         if (owner.isShiftKeyDown()) {
            return false;
         }

         if (entity.isAlliedTo(owner)) {
            return false;
         }
      }

      return (entity.canBeCollidedWith() || entity.isPushable()) && !this.isPassengerOfSameVehicle(entity);
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pose) {
      EntityDimensions dimensions = this.getType().getDimensions();
      int extendingTick = Math.min(this.getAge(), this.getExtendingTick());
      return dimensions.scale(this.getSize(), this.getSize() * this.getHeight() * extendingTick / this.getExtendingTick());
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (HEIGHT.equals(pKey) || AGE.equals(pKey)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   protected void reapplyPosition() {
      this.setPos(this.getX(), Math.round(this.getY()), this.getZ());
   }

   @Override
   public void tick() {
      super.tick();
      int age = this.getAge();
      Level level = this.level();
      if (age < this.getExtendingTick()) {
         if (age == this.getExtendingTick() / 2) {
            this.doExtendingEffect();
         }
      } else {
         float contactDamage = this.getContactDamage();
         int contactInterval = this.getContactInterval();
         if (contactInterval == 0 || contactDamage > 0.0F && age % contactInterval == 0) {
            AABB box = this.getBoundingBox().inflate(this.getContactRange());

            for (Entity target : level.getEntities(this, box, this::canHitEntity)) {
               this.hitEntityOnContact(target);
            }
         }

         float aoeDamage = this.getAoeDamage();
         int aoeInterval = this.getAoeInterval();
         if (aoeDamage > 0.0F && (aoeInterval == 0 || age % aoeInterval == 0)) {
            this.doRangedDamage(level, this.getAoeRange(), aoeDamage);
         }
      }
   }

   private void doRangedDamage(Level level, double range, float damage) {
      AABB box = this.getBoundingBox().inflate(range);

      for (Entity target : level.getEntities(this, box, this::canHitEntity)) {
         if (this.getBurnTicks() > 0) {
            target.setRemainingFireTicks(this.getBurnTicks());
         }

         this.dealContactDamage(target, damage);
      }
   }

   public void doExtendingEffect() {
      Level level = this.level();
      AABB box = this.getBoundingBox().inflate(0.0, 0.5, 0.0);

      for (Entity target : level.getEntities(this, box, this::canHitEntity)) {
         if (this.shouldPushUp()) {
            target.move(MoverType.SHULKER, new Vec3(0.0, 1.0, 0.0));
         }

         if (target instanceof LivingEntity living) {
            this.applyEffect(living);
         }
      }
   }

   public void applyEffect(LivingEntity target) {
      this.hitEntity(target, ProjectileHitResult.DEFAULT);
   }

   protected boolean hitEntityOnContact(Entity target) {
      boolean success = false;
      if (this.getBurnTicks() > 0) {
         target.setRemainingFireTicks(this.getBurnTicks());
         success = true;
      }

      if (target instanceof LivingEntity entity) {
         success = this.applyMobEffects(entity) || success;
      }

      success = this.dealContactDamage(target, this.getContactDamage()) || success;
      return !(this.getContactPush() > 0.0F) ? success : this.pushOnContact(target) || success;
   }

   protected boolean dealContactDamage(Entity target, float damage) {
      if (target.invulnerableTime >= 20) {
         return false;
      } else {
         Magic.MagicType magicType = this.isElementalAttack() ? Magic.MagicType.SPIRITUAL : null;
         DamageSource source = this.getDamageSource(1.0F).tensura$setMagicType(magicType);
         if (this.getSecondaryDamage() > 0.0F) {
            Magic.MagicType secondMagicType = this.isElementalAttack() ? Magic.MagicType.ASPECTUAL : null;
            DamageSource secondSource = this.getDamageSource(this.getSecondaryContactDamageType(), 1.0F).tensura$setMagicType(secondMagicType);
            return TensuraDamageHelper.hurtDouble(target, source, damage, secondSource, this.getSecondaryDamage());
         } else {
            return damage > 0.0F && target.hurt(source, damage);
         }
      }
   }

   protected boolean pushOnContact(Entity target) {
      Vec3 vec3 = target.position().subtract(target.getViewVector(1.0F)).subtract(new Vec3(this.getX(), target.getY(), this.getZ()));
      SkillHelper.knockBack(
         target, this.getOwner(), this.getSkill(), vec3.normalize(), this.getContactPush(), this.getKnockResistNegate(), this.getContactPush() / 10.0
      );
      return true;
   }

   @Generated
   public float getContactDamage() {
      return this.contactDamage;
   }

   @Generated
   public void setContactDamage(float contactDamage) {
      this.contactDamage = contactDamage;
   }

   @Generated
   public float getContactSecondaryDamage() {
      return this.contactSecondaryDamage;
   }

   @Generated
   public void setContactSecondaryDamage(float contactSecondaryDamage) {
      this.contactSecondaryDamage = contactSecondaryDamage;
   }

   @Generated
   public float getContactRange() {
      return this.contactRange;
   }

   @Generated
   public void setContactRange(float contactRange) {
      this.contactRange = contactRange;
   }

   @Generated
   public int getContactInterval() {
      return this.contactInterval;
   }

   @Generated
   public void setContactInterval(int contactInterval) {
      this.contactInterval = contactInterval;
   }

   @Generated
   public float getContactPush() {
      return this.contactPush;
   }

   @Generated
   public void setContactPush(float contactPush) {
      this.contactPush = contactPush;
   }

   @Generated
   public boolean isPushEntityUp() {
      return this.pushEntityUp;
   }

   @Generated
   public void setPushEntityUp(boolean pushEntityUp) {
      this.pushEntityUp = pushEntityUp;
   }

   @Generated
   public float getAoeDamage() {
      return this.aoeDamage;
   }

   @Generated
   public void setAoeDamage(float aoeDamage) {
      this.aoeDamage = aoeDamage;
   }

   @Generated
   public float getAoeRange() {
      return this.aoeRange;
   }

   @Generated
   public void setAoeRange(float aoeRange) {
      this.aoeRange = aoeRange;
   }

   @Generated
   public int getAoeInterval() {
      return this.aoeInterval;
   }

   @Generated
   public void setAoeInterval(int aoeInterval) {
      this.aoeInterval = aoeInterval;
   }

   @Generated
   public float getBreakRange() {
      return this.breakRange;
   }

   @Generated
   public void setBreakRange(float breakRange) {
      this.breakRange = breakRange;
   }

   @Nullable
   @Generated
   public MobEffectInstance getBreakEffect() {
      return this.breakEffect;
   }

   @Generated
   public void setBreakEffect(@Nullable MobEffectInstance breakEffect) {
      this.breakEffect = breakEffect;
   }
}
