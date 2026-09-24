package io.github.manasmods.tensura.entity.magic.field;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.unique.LustSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DeathBlessingField extends AreaField implements GeoEntity {
   protected static final EntityDataAccessor<Integer> CASTED_TIME = SynchedEntityData.defineId(DeathBlessingField.class, EntityDataSerializers.INT);
   protected double ep = 0.0;
   private final List<Entity> blessedList = new ArrayList<>();
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public DeathBlessingField(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noPhysics = true;
   }

   public DeathBlessingField(Level level, Entity entity) {
      this((EntityType<? extends Projectile>)MiscEntityTypes.DEATH_BLESSING.get(), level);
      this.setOwner(entity);
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return !super.canHitEntity(pTarget) ? false : this.getOwner() == null || !pTarget.isAlliedTo(this.getOwner());
      }
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CASTED_TIME, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("CastedTime", this.getCastedTime());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setCastedTime(compound.getInt("CastedTime"));
   }

   public int getCastedTime() {
      return (Integer)this.entityData.get(CASTED_TIME);
   }

   public void setCastedTime(int time) {
      this.entityData.set(CASTED_TIME, time);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.DEATH_BLESS;
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return pDistance < 4096.0 || super.shouldRenderAtSqrDistance(pDistance);
   }

   @Override
   protected boolean shouldRemove() {
      return this.getAge() > this.getActualLife();
   }

   public boolean isCastedTooEarly() {
      return this.getCastedTime() < LustSkill.CONFIG.blessTime;
   }

   public int getActualLife() {
      return this.isCastedTooEarly() ? this.getLife() + 10 : this.getLife() + this.getCastedTime();
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getAge() <= LustSkill.CONFIG.blessTime / 2) {
         if (this.getLife() + this.getCastedTime() - this.getAge() >= LustSkill.CONFIG.blessTime - 10) {
            if (this.getOwner() instanceof LivingEntity owner) {
               this.setPos(getBlessingPos(owner, LustSkill.CONFIG.blessRange));
               this.setRot(owner.getYHeadRot(), 0.0F);
            }
         }
      }
   }

   public static Vec3 getBlessingPos(LivingEntity owner, double range) {
      Entity target = ObjectSelectionHelper.getTargetingEntity(owner, range, 1.0, false, false);
      Vec3 pos;
      if (target != null) {
         pos = target.position();
      } else {
         BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(owner.level(), owner, Fluid.NONE, range);
         pos = result.getLocation();
      }

      return pos;
   }

   @Override
   protected void updateVisualSize() {
      if (this.getActualLife() - this.getAge() < 10) {
         this.setVisualSize(Math.max(this.getVisualSize() - 0.1F, 0.0F));
      } else {
         this.setVisualSize(1.0F);
      }
   }

   @Override
   public void applyEffect(LivingEntity target, boolean instant) {
      UniqueSkillConfig.Lust CONFIG = LustSkill.CONFIG;
      if (this.getLife() + this.getCastedTime() - this.getAge() >= LustSkill.CONFIG.blessTime - 10) {
         if (this.getAge() < CONFIG.blessTime + 70) {
            target.addEffect(
               new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), 15, CONFIG.blessInterference - 1, false, false, false
               ),
               this.getOwner()
            );
            if (!this.isCastedTooEarly()) {
               if (this.getAge() >= CONFIG.blessTime + 10) {
                  if (!this.blessedList.contains(target)) {
                     this.blessedList.add(target);
                     double targetEP = TensuraStorages.getExistenceFrom(target).getEP();
                     double difference = targetEP / this.getEp();
                     DamageSource source = this.getDamageSource();
                     if (difference <= CONFIG.blessEP) {
                        if (target.hurt(source, target.getMaxHealth() * 10.0F)) {
                           if (!target.isAlive() && target.getType().is(TensuraEntityTags.NO_EP_PLUNDER)) {
                              return;
                           }

                           double epGain = targetEP * CONFIG.blessFullRestore;
                           if (this.getOwner() instanceof LivingEntity entity) {
                              EnergyHelper.gainMagicule(entity, epGain * CONFIG.blessMPHeal, EnergyHelper.GainType.NORMAL);
                              EnergyHelper.gainAura(entity, epGain * (1.0F - CONFIG.blessMPHeal), EnergyHelper.GainType.NORMAL);
                           }
                        }
                     } else if (difference <= CONFIG.blessHalfEP) {
                        if (target.hurt(source, target.getMaxHealth() * CONFIG.blessHalfDamage)) {
                           if (!target.isAlive() && target.getType().is(TensuraEntityTags.NO_EP_PLUNDER)) {
                              return;
                           }

                           double epGain = targetEP * CONFIG.blessHalfRestore;
                           if (this.getOwner() instanceof LivingEntity entity) {
                              EnergyHelper.drainEnergy(
                                 target, entity, epGain * CONFIG.blessMPHeal, false, EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NORMAL
                              );
                              EnergyHelper.drainEnergy(
                                 target, entity, epGain * (1.0F - CONFIG.blessMPHeal), false, EnergyHelper.DrainType.AURA, EnergyHelper.GainType.NORMAL
                              );
                           }
                        }
                     } else if (difference <= CONFIG.blessMinimalEP && target.hurt(source, target.getMaxHealth() * CONFIG.blessMinimalDamage)) {
                        if (!target.isAlive() && target.getType().is(TensuraEntityTags.NO_EP_PLUNDER)) {
                           return;
                        }

                        double epGain = targetEP * CONFIG.blessMinimalRestore;
                        if (this.getOwner() instanceof LivingEntity entity) {
                           EnergyHelper.drainEnergy(
                              target, entity, epGain * CONFIG.blessMPHeal, false, EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NORMAL
                           );
                           EnergyHelper.drainEnergy(
                              target, entity, epGain * (1.0F - CONFIG.blessMPHeal), false, EnergyHelper.DrainType.AURA, EnergyHelper.GainType.NORMAL
                           );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.death_blessing.start")))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Nullable
   public static DeathBlessingField getBlessing(
      double radius, int blessTime, double blessRange, LivingEntity owner, @Nullable ManasSkillInstance instance, TensuraSkill skill, int mode
   ) {
      return getBlessing(
         (EntityType<? extends DeathBlessingField>)MiscEntityTypes.DEATH_BLESSING.get(),
         "BlessingId",
         radius,
         blessTime,
         getBlessingPos(owner, blessRange),
         owner,
         instance,
         skill,
         mode
      );
   }

   @Nullable
   public static DeathBlessingField getBlessing(
      EntityType<? extends DeathBlessingField> type,
      String id,
      double radius,
      int blessTime,
      Vec3 pos,
      LivingEntity owner,
      @Nullable ManasSkillInstance instance,
      TensuraSkill skill,
      int mode
   ) {
      if (instance == null) {
         return null;
      }

      CompoundTag tag = instance.getOrCreateTag();
      Level level = owner.level();
      if (tag.getInt(id) == 0 && !EnergyHelper.isOutOfEnergy(owner, instance, mode)) {
         DeathBlessingField bless = (DeathBlessingField)type.create(level);
         if (bless == null) {
            return null;
         }

         bless.setOwner(owner);
         bless.setEp(EnergyHelper.getMaxEP(owner));
         bless.setTickEachHit(10);
         bless.setSkill(owner, instance, skill, mode);
         bless.setSize((float)radius);
         bless.setLife(blessTime);
         bless.setPos(pos);
         owner.level().addFreshEntity(bless);
         tag.putInt(id, bless.getId());
         instance.markDirty();
         return bless;
      } else if (owner.level().getEntity(tag.getInt(id)) instanceof DeathBlessingField bless) {
         bless.setCastedTime(bless.getCastedTime() + 1);
         return bless;
      } else {
         tag.putInt(id, 0);
         instance.markDirty();
         return null;
      }
   }

   @Generated
   public double getEp() {
      return this.ep;
   }

   @Generated
   public void setEp(double ep) {
      this.ep = ep;
   }
}
