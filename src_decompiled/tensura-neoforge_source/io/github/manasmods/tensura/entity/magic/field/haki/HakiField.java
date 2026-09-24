package io.github.manasmods.tensura.entity.magic.field.haki;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.field.AreaField;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HakiField extends AreaField implements GeoEntity {
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(HakiField.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> EFFECT_SIZE_MULTIPLIER = SynchedEntityData.defineId(HakiField.class, EntityDataSerializers.FLOAT);
   protected double ep = 0.0;
   protected double epDifferenceMultiplier = 0.0;
   protected int fearDuration = 0;
   protected boolean subordinate = false;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public HakiField(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noPhysics = true;
   }

   public HakiField(Level level, Entity entity) {
      this((EntityType<? extends Projectile>)MiscEntityTypes.HAKI_FIELD.get(), level);
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
      builder.define(VARIANT, 0);
      builder.define(EFFECT_SIZE_MULTIPLIER, 1.0F);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
      compound.putFloat("EffectSizeMultiplier", this.getEffectSizeMultiplier());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(VARIANT, compound.getInt("Variant"));
      this.setEffectSizeMultiplier(compound.getFloat("EffectSizeMultiplier"));
   }

   public HakiField.HakiVariant getVariant() {
      return HakiField.HakiVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(HakiField.HakiVariant variant) {
      this.entityData.set(VARIANT, variant.getId() & 0xFF);
   }

   public float getEffectSizeMultiplier() {
      return (Float)this.entityData.get(EFFECT_SIZE_MULTIPLIER);
   }

   public void setEffectSizeMultiplier(float size) {
      this.entityData.set(EFFECT_SIZE_MULTIPLIER, size);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.FEAR;
   }

   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return this.getType().getDimensions();
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return pDistance < 4096.0 || super.shouldRenderAtSqrDistance(pDistance);
   }

   @Override
   public void tick() {
      super.tick();
      Entity owner = this.getOwner();
      if (owner != null) {
         this.setPos(owner.position().add(0.0, owner.getBbHeight() / 2.0F, 0.0));
         if (!owner.isAlive() || owner.level() != this.level()) {
            this.remove();
         }
      }
   }

   @Override
   protected void updateVisualSize() {
      if (this.getLife() - this.getAge() < 10) {
         this.setVisualSize(Math.max(this.getVisualSize() - this.getSize() / 10.0F, 0.0F));
      } else if (this.getVisualSize() != this.getSize()) {
         this.setVisualSize(Math.min(this.getVisualSize() + 1.0F, this.getSize()));
      }
   }

   @Override
   protected void hitTarget(boolean instant) {
      if (!this.level().isClientSide()) {
         for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(this.getSize()))) {
            if (this.canHitEntity(target)) {
               this.applyEffect(target, instant);
            }
         }
      }
   }

   @Override
   public void applyEffect(LivingEntity target, boolean instant) {
      double targetEP = EnergyHelper.getMaxEP(target);
      double difference = this.getEp() / targetEP;
      if (!(difference <= 2.0)) {
         int fearLevel = (int)(this.getEpDifferenceMultiplier() * (difference - 2.0));
         fearLevel = Math.min(fearLevel, TensuraMobEffect.CONFIG.maxFear);
         MobEffectInstance fear = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.FEAR), this.getFearDuration(), fearLevel, true, false, true
         );
         UUID owner = this.getOwner() == null
            ? null
            : (this.isSubordinate() && this.getOwner() instanceof LivingEntity sub ? SubordinateHelper.getSubordinateOwnerUUID(sub) : this.getOwner().getUUID());
         if (TensuraMobEffect.addEffect(target, fear, owner, this.getSkill().getSkill(), this.getMode())) {
            HakiSkill.hakiPush(target, this.getOwner(), this.getSkill(), fearLevel);
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.haki.loop")))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Nullable
   public static HakiField getHaki(
      HakiField.HakiVariant variant,
      double radius,
      double epDifferenceMultiplier,
      int fearDuration,
      Vec3 pos,
      LivingEntity owner,
      @Nullable ManasSkillInstance instance,
      TensuraSkill skill,
      int mode
   ) {
      return getHaki(
         (EntityType<? extends HakiField>)MiscEntityTypes.HAKI_FIELD.get(),
         variant,
         radius,
         epDifferenceMultiplier,
         fearDuration,
         pos,
         owner,
         instance,
         skill,
         mode
      );
   }

   @Nullable
   public static HakiField getHaki(
      EntityType<? extends HakiField> type,
      HakiField.HakiVariant variant,
      double radius,
      double epDifferenceMultiplier,
      int fearDuration,
      Vec3 pos,
      LivingEntity owner,
      @Nullable ManasSkillInstance instance,
      TensuraSkill skill,
      int mode
   ) {
      return getHaki(type, "HakiID", variant, radius, epDifferenceMultiplier, fearDuration, owner.getBbHeight() / 1.8F, pos, owner, instance, skill, mode);
   }

   @Nullable
   public static HakiField getHaki(
      EntityType<? extends HakiField> type,
      String id,
      HakiField.HakiVariant variant,
      double radius,
      double epDifferenceMultiplier,
      int fearDuration,
      float effectSize,
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
         HakiField haki = (HakiField)type.create(level);
         if (haki == null) {
            return null;
         }

         haki.setVariant(variant);
         haki.setOwner(owner);
         haki.setEpDifferenceMultiplier(epDifferenceMultiplier);
         haki.setTickEachHit(5);
         haki.setSkill(owner, instance, skill, mode);
         haki.setFearDuration(fearDuration);
         haki.setEffectSizeMultiplier(effectSize);
         haki.setSize((float)radius);
         haki.setLife(16);
         haki.setPos(pos);
         owner.level().addFreshEntity(haki);
         tag.putInt(id, haki.getId());
         instance.markDirty();
         return haki;
      } else if (owner.level().getEntity(tag.getInt(id)) instanceof HakiField haki) {
         haki.increaseLife(1);
         return haki;
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

   @Generated
   public double getEpDifferenceMultiplier() {
      return this.epDifferenceMultiplier;
   }

   @Generated
   public void setEpDifferenceMultiplier(double epDifferenceMultiplier) {
      this.epDifferenceMultiplier = epDifferenceMultiplier;
   }

   @Generated
   public int getFearDuration() {
      return this.fearDuration;
   }

   @Generated
   public void setFearDuration(int fearDuration) {
      this.fearDuration = fearDuration;
   }

   @Generated
   public boolean isSubordinate() {
      return this.subordinate;
   }

   @Generated
   public void setSubordinate(boolean subordinate) {
      this.subordinate = subordinate;
   }

   public enum HakiVariant {
      DEFAULT(0, "default"),
      DEMON_LORD(1, "demon_lord"),
      MORTAL_FEAR(2, "mortal_fear"),
      HERO(3, "hero"),
      SACRED(4, "sacred");

      private static final HakiField.HakiVariant[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(HakiField.HakiVariant::getId))
         .toArray(HakiField.HakiVariant[]::new);
      private final int id;
      private final String name;

      HakiVariant(int id, String name) {
         this.id = id;
         this.name = name;
      }

      public static HakiField.HakiVariant byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      @Generated
      public int getId() {
         return this.id;
      }

      @Generated
      public String getName() {
         return this.name;
      }
   }
}
