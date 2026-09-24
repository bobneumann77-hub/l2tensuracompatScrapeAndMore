package io.github.manasmods.tensura.entity;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.entity.ProjectileConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TensuraProjectile extends Projectile {
   public static final ProjectileConfig CONFIG = (ProjectileConfig)ConfigRegistry.getConfig(ProjectileConfig.class);
   protected static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(TensuraProjectile.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(TensuraProjectile.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(TensuraProjectile.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> VISUAL_SIZE = SynchedEntityData.defineId(TensuraProjectile.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<CompoundTag> INSTANCE_TAG = SynchedEntityData.defineId(TensuraProjectile.class, EntityDataSerializers.COMPOUND_TAG);
   protected float damage = 0.0F;
   protected float secondaryDamage = 0.0F;
   protected float knockForce = 0.0F;
   protected float knockResistNegate = 0.0F;
   protected int burnTicks = 0;
   protected boolean visible = true;
   @Nullable
   private MobEffectInstance mobEffect = null;
   protected float effectRange = 0.0F;
   protected boolean effectStack = false;
   protected int mode = 0;
   protected double apCost = 0.0;
   protected double mpCost = 0.0;
   protected boolean elementalAttack = false;
   protected Element element = null;
   protected MagicCircle magicCircle = null;

   public TensuraProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(AGE, 0);
      builder.define(LIFE, CONFIG.magicDespawnTick);
      builder.define(SIZE, 1.0F);
      builder.define(VISUAL_SIZE, 1.0F);
      builder.define(INSTANCE_TAG, new CompoundTag());
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Age", this.getAge());
      compound.putInt("Life", this.getLife());
      compound.putFloat("Size", this.getSize());
      compound.putFloat("VisualSize", this.getVisualSize());
      compound.putFloat("Damage", this.getDamage());
      compound.putFloat("SecondaryDamage", this.getSecondaryDamage());
      compound.putFloat("KnockBack", this.getKnockForce());
      compound.putFloat("KnockResistNegate", this.getKnockResistNegate());
      compound.putInt("BurnTicks", this.getBurnTicks());
      compound.putFloat("EffectRange", this.getEffectRange());
      if (this.getMobEffect() != null) {
         compound.put("StatusEffect", this.getMobEffect().save());
      }

      compound.putBoolean("EffectStack", this.isEffectStack());
      compound.putInt("Mode", this.getMode());
      compound.putDouble("APCost", this.getApCost());
      compound.putDouble("MPCost", this.getMpCost());
      compound.putBoolean("ElementalAttack", this.isElementalAttack());
      compound.putBoolean("Visible", this.isVisible());
      if (this.getSkill() != null) {
         compound.put("skill", (Tag)this.entityData.get(INSTANCE_TAG));
      } else if (compound.contains("skill")) {
         compound.remove("skill");
      }

      if (this.element != null) {
         compound.putInt("element", this.element.getId());
      } else if (compound.contains("element")) {
         compound.remove("element");
      }
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setLife(compound.getInt("Life"));
      this.setAge(compound.getInt("Age"));
      this.setVisualSize(compound.getFloat("VisualSize"));
      this.entityData.set(SIZE, compound.getFloat("Size"));
      this.setDamage(compound.getFloat("Damage"));
      this.setSecondaryDamage(compound.getFloat("SecondaryDamage"));
      this.setKnockForce(compound.getFloat("KnockBack"));
      this.setKnockResistNegate(compound.getFloat("KnockResistNegate"));
      this.setBurnTicks(compound.getInt("BurnTicks"));
      this.setEffectRange(compound.getFloat("EffectRange"));
      if (compound.contains("StatusEffect")) {
         this.setMobEffect(MobEffectInstance.load(compound.getCompound("StatusEffect")));
      }

      this.setEffectStack(compound.getBoolean("EffectStack"));
      this.setMode(compound.getInt("Mode"));
      this.setApCost(compound.getDouble("APCost"));
      this.setMpCost(compound.getDouble("MPCost"));
      this.setElementalAttack(compound.getBoolean("ElementalAttack"));
      this.setVisible(compound.getBoolean("Visible"));
      if (compound.contains("skill") && compound.get("skill") instanceof CompoundTag tag) {
         this.entityData.set(INSTANCE_TAG, tag);
      }

      if (compound.contains("element")) {
         this.element = Element.byId(compound.getInt("element"));
      }
   }

   public int getAge() {
      return (Integer)this.entityData.get(AGE);
   }

   public void setAge(int life) {
      this.entityData.set(AGE, life);
   }

   public int getLife() {
      return (Integer)this.entityData.get(LIFE);
   }

   public void setLife(int life) {
      this.entityData.set(LIFE, life);
   }

   public void increaseLife(int life) {
      this.setLife(this.getLife() + life);
   }

   public void setRemoveIn(int ticks) {
      this.setAge(this.getLife() - ticks);
   }

   public float getSize() {
      return (Float)this.entityData.get(SIZE);
   }

   public void setSize(float size) {
      this.entityData.set(SIZE, size);
      this.setVisualSize(size);
   }

   public float getVisualSize() {
      return (Float)this.entityData.get(VISUAL_SIZE);
   }

   public void setVisualSize(float size) {
      this.entityData.set(VISUAL_SIZE, size);
   }

   public ManasSkillInstance getSkill() {
      return ((CompoundTag)this.entityData.get(INSTANCE_TAG)).isEmpty() ? null : ManasSkillInstance.fromNBT((CompoundTag)this.entityData.get(INSTANCE_TAG));
   }

   public void setSkill(@Nullable ManasSkillInstance instance) {
      this.entityData.set(INSTANCE_TAG, instance == null ? new CompoundTag() : instance.toNBT());
   }

   public void setSkill(LivingEntity entity, ManasSkillInstance instance, TensuraSkill skill, int mode) {
      this.setSkill(entity, instance, skill, mode, 1.0F);
   }

   public void setSkill(LivingEntity entity, ManasSkillInstance instance, TensuraSkill skill, int mode, float costMultiplier) {
      this.setSkill(instance);
      this.setMode(mode);
      this.setApCost(skill.getAuraCost(entity, instance, mode) * costMultiplier);
      this.setMpCost(skill.getMagiculeCost(entity, instance, mode) * costMultiplier);
      if (skill instanceof SpiritualMagic spiritualMagic) {
         this.setElement(spiritualMagic.getElemental());
      }
   }

   public void setSkill(TensuraProjectile projectile) {
      this.setSkill(projectile.getSkill());
      this.setMode(projectile.getMode());
      this.setApCost(projectile.getApCost());
      this.setMpCost(projectile.getMpCost());
      this.setElement(projectile.getElement());
   }

   public void setMagicCircle(Entity entity) {
      if (entity == null) {
         this.magicCircle = null;
      } else if (entity instanceof MagicCircle circle) {
         this.magicCircle = circle;
      } else {
         this.magicCircle = null;
      }
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (SIZE.equals(pKey)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.MAGIC_GENERIC;
   }

   public ResourceKey<DamageType> getSecondaryDamageType() {
      return this.getDamageType();
   }

   public boolean isOnFire() {
      return false;
   }

   public boolean isInvisible() {
      return !this.isVisible();
   }

   public boolean isInvisibleTo(Player pPlayer) {
      if (!this.isInvisible()) {
         return super.isInvisibleTo(pPlayer);
      } else if (pPlayer.isSpectator()) {
         return false;
      } else {
         return this.getOwner() == pPlayer ? false : pPlayer.getAttributeValue(TensuraAttributes.PRESENCE_SENSE) < 3.0;
      }
   }

   public EntityDimensions getDimensions(Pose pPose) {
      return super.getDimensions(pPose).scale(this.getSize());
   }

   public boolean shouldDiscardInLava() {
      return true;
   }

   public boolean shouldDiscardInWater() {
      return true;
   }

   public boolean isPushedByFluid() {
      return !this.shouldDiscardInWater() ? false : super.isPushedByFluid();
   }

   protected boolean updateInWaterStateAndDoFluidPushing() {
      return !this.shouldDiscardInWater() ? false : super.updateInWaterStateAndDoFluidPushing();
   }

   protected void updateFluidOnEyes() {
      if (this.shouldDiscardInWater()) {
         super.updateFluidOnEyes();
      }
   }

   protected boolean canHitEntity(Entity pTarget) {
      if (pTarget == this) {
         return false;
      } else {
         return pTarget.isSpectator() ? false : super.canHitEntity(pTarget) && !(pTarget instanceof Player player && player.isCreative());
      }
   }

   public void tick() {
      super.tick();
      this.setAge(this.getAge() + 1);
      if (this.shouldRemove()) {
         this.remove();
      }
   }

   protected boolean shouldRemove() {
      if (this.isInWaterOrBubble() && this.shouldDiscardInWater()) {
         return true;
      } else {
         return this.isInLava() && this.shouldDiscardInLava() ? true : this.getLife() != -1 && this.getAge() > this.getLife();
      }
   }

   public void remove() {
      this.discard();
   }

   protected boolean shouldGrief() {
      return this.getOwner() != null && !this.getOwner().getType().equals(EntityType.PLAYER)
         ? this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
         : TensuraGameRules.canSkillGrief(this.level());
   }

   protected boolean hitEntity(Entity entity, @Nullable ProjectileHitResult result) {
      if (result == ProjectileHitResult.PASS) {
         return false;
      }

      boolean success = false;
      if (this.getBurnTicks() > 0) {
         this.applyBurn(entity);
         success = true;
      }

      if (!(this.getDamage() > 0.0F) && !(this.getSecondaryDamage() > 0.0F)) {
         if (entity instanceof LivingEntity target) {
            success = this.applyMobEffects(target) || success;
         }
      } else if (result != ProjectileHitResult.HIT_NO_DAMAGE) {
         if (this.dealDamage(entity)) {
            if (entity instanceof LivingEntity target) {
               this.applyMobEffects(target);
            }

            success = true;
         }

         if (this.getKnockForce() > 0.0F) {
            this.knockBack(entity);
            success = true;
         }
      }

      return success;
   }

   protected void applyBurn(Entity entity) {
      entity.setRemainingFireTicks(Math.max(this.getBurnTicks(), 0));
   }

   protected boolean dealDamage(Entity target) {
      return this.dealDamage(target, this.getDamage(), 1.0F);
   }

   protected boolean dealDamage(Entity target, float damage, float costMultiplier) {
      Magic.MagicType magicType = this.isElementalAttack() ? Magic.MagicType.SPIRITUAL : null;
      DamageSource source = this.getDamageSource(costMultiplier).tensura$setMagicType(magicType);
      if (this.getSecondaryDamage() > 0.0F) {
         Magic.MagicType secondMagicType = this.isElementalAttack() ? Magic.MagicType.ASPECTUAL : null;
         DamageSource secondSource = this.getDamageSource(this.getSecondaryDamageType(), costMultiplier).tensura$setMagicType(secondMagicType);
         return TensuraDamageHelper.hurtDouble(target, source, damage, secondSource, this.getSecondaryDamage());
      } else {
         return damage > 0.0F && target.hurt(source, damage);
      }
   }

   public DamageSource getDamageSource() {
      return this.getDamageSource(1.0F);
   }

   public DamageSource getDamageSource(float costMultiplier) {
      return this.getDamageSource(this.getDamageType(), costMultiplier);
   }

   public DamageSource getDamageSource(ResourceKey<DamageType> type, float costMultiplier) {
      return TensuraDamageTypes.getIndirectEntityDamageSource(this.level(), type, this.getOwner(), this)
         .tensura$setAbilityInstance(this.getSkill())
         .tensura$setAbilityMode(this.getMode())
         .tensura$setElement(this.getElement())
         .tensura$setMagiculeCost(this.getMpCost() * costMultiplier)
         .tensura$setAuraCost(this.getApCost() * costMultiplier);
   }

   protected boolean applyMobEffects(LivingEntity entity) {
      if (this.getMobEffect() != null) {
         ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
         TensuraMobEffect.addEffect(entity, new MobEffectInstance(this.getMobEffect()), this.getOwner(), skill, this.getMode());
         return true;
      } else {
         return false;
      }
   }

   public void applyEffectAround(double inflateRadius) {
      if (this.getMobEffect() != null) {
         List<LivingEntity> list = this.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               this.getBoundingBox().inflate(inflateRadius),
               entityData -> this.getOwner() == null || !entityData.isAlliedTo(this.getOwner()) && !entityData.is(this.getOwner())
            );
         if (!list.isEmpty()) {
            for (LivingEntity target : list) {
               if (this.getOwner() instanceof LivingEntity entity) {
                  target.setLastHurtByMob(entity);
               }

               ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
               TensuraMobEffect.addEffect(target, new MobEffectInstance(this.getMobEffect()), this.getOwner(), skill, this.getMode());
            }
         }
      }
   }

   public void knockBack(Entity entity) {
      Vec3 vec3 = this.getDeltaMovement().add(this.getViewVector(1.0F).normalize());
      SkillHelper.knockBack(entity, this.getOwner(), this.getSkill(), vec3, this.getKnockForce(), this.getKnockResistNegate(), this.getKnockForce() / 3.0);
   }

   @Generated
   public float getDamage() {
      return this.damage;
   }

   @Generated
   public void setDamage(float damage) {
      this.damage = damage;
   }

   @Generated
   public float getSecondaryDamage() {
      return this.secondaryDamage;
   }

   @Generated
   public void setSecondaryDamage(float secondaryDamage) {
      this.secondaryDamage = secondaryDamage;
   }

   @Generated
   public float getKnockForce() {
      return this.knockForce;
   }

   @Generated
   public void setKnockForce(float knockForce) {
      this.knockForce = knockForce;
   }

   @Generated
   public float getKnockResistNegate() {
      return this.knockResistNegate;
   }

   @Generated
   public void setKnockResistNegate(float knockResistNegate) {
      this.knockResistNegate = knockResistNegate;
   }

   @Generated
   public int getBurnTicks() {
      return this.burnTicks;
   }

   @Generated
   public void setBurnTicks(int burnTicks) {
      this.burnTicks = burnTicks;
   }

   @Generated
   public boolean isVisible() {
      return this.visible;
   }

   @Generated
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Nullable
   @Generated
   public MobEffectInstance getMobEffect() {
      return this.mobEffect;
   }

   @Generated
   public void setMobEffect(@Nullable MobEffectInstance mobEffect) {
      this.mobEffect = mobEffect;
   }

   @Generated
   public float getEffectRange() {
      return this.effectRange;
   }

   @Generated
   public void setEffectRange(float effectRange) {
      this.effectRange = effectRange;
   }

   @Generated
   public boolean isEffectStack() {
      return this.effectStack;
   }

   @Generated
   public void setEffectStack(boolean effectStack) {
      this.effectStack = effectStack;
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
   public double getApCost() {
      return this.apCost;
   }

   @Generated
   public void setApCost(double apCost) {
      this.apCost = apCost;
   }

   @Generated
   public double getMpCost() {
      return this.mpCost;
   }

   @Generated
   public void setMpCost(double mpCost) {
      this.mpCost = mpCost;
   }

   @Generated
   public boolean isElementalAttack() {
      return this.elementalAttack;
   }

   @Generated
   public void setElementalAttack(boolean elementalAttack) {
      this.elementalAttack = elementalAttack;
   }

   @Generated
   public Element getElement() {
      return this.element;
   }

   @Generated
   public void setElement(Element element) {
      this.element = element;
   }

   @Generated
   public MagicCircle getMagicCircle() {
      return this.magicCircle;
   }
}
