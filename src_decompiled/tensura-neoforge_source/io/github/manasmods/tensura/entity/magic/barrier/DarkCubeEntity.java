package io.github.manasmods.tensura.entity.magic.barrier;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.magic.spiritual.darkness.DarkCubeMagic;
import io.github.manasmods.tensura.ability.magic.spiritual.darkness.TrueDarknessMagic;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DarkCubeEntity extends BarrierEntity implements GeoEntity {
   public static final ResourceLocation[] DARK_CUBE = new ResourceLocation[]{
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/dark_cube_0.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/dark_cube_1.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/dark_cube_2.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/dark_cube_3.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/dark_cube_4.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/dark_cube_5.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/dark_cube_6.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/dark_cube_7.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/dark_cube_8.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/dark_cube_9.png")
   };
   private static final EntityDataAccessor<Boolean> TRUE = SynchedEntityData.defineId(DarkCubeEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public DarkCubeEntity(Level level, LivingEntity entity) {
      this((EntityType<? extends DarkCubeEntity>)MiscEntityTypes.DARK_CUBE.get(), level);
      this.setOwner(entity);
      this.setElementalAttack(true);
   }

   public DarkCubeEntity(EntityType<? extends DarkCubeEntity> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   public boolean shouldCreateParts() {
      return true;
   }

   @Override
   public boolean canWalkThrough() {
      return true;
   }

   @Override
   public boolean blockBuilding() {
      return false;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(TRUE, false);
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("TrueDarkness", this.isTrueDarkness());
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setTrueDarkness(pCompound.getBoolean("TrueDarkness"));
   }

   @Override
   public void setSize(float size) {
      this.getEntityData().set(SIZE, size);
   }

   public boolean isTrueDarkness() {
      return (Boolean)this.getEntityData().get(TRUE);
   }

   public void setTrueDarkness(boolean trueDarkness) {
      this.getEntityData().set(TRUE, trueDarkness);
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.DARKNESS_ELEMENTAL;
   }

   @Override
   public boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return !super.canHitEntity(pTarget) ? false : !(this.getOwner() instanceof LivingEntity owner && owner.isAlliedTo(pTarget));
      }
   }

   @Override
   protected void updateVisualSize() {
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return true;
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Override
   public void applyEffect(LivingEntity entity) {
      if (this.isTrueDarkness()) {
         TensuraDamageHelper.directSpiritualHurt(entity, null, this.getDamageSource(), this.getDamage());
         entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20, TrueDarknessMagic.CONFIG.darknessLevel, false, false, false));
         if (this.getAge() % (this.getTickEachHit() * 3) == 0) {
            int insanityLevel = 0;
            if (this.isEffectStack()) {
               MobEffectInstance insanity = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY));
               if (insanity != null) {
                  insanityLevel = insanity.getAmplifier() + 1;
               }
            }

            MobEffectInstance instance = new MobEffectInstance(
               TensuraMobEffects.getReference(TensuraMobEffects.INSANITY), TrueDarknessMagic.CONFIG.insanityDuration, insanityLevel, true, false, true
            );
            ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
            TensuraMobEffect.addEffect(entity, instance, this.getOwner(), skill, this.getMode());
         }
      } else {
         if (this.dealDamage(entity)) {
            entity.addEffect(
               new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), 20, DarkCubeMagic.CONFIG.cubeSpeed - 1, false, false, false
               )
            );
         }
      }
   }

   public static void spawnTrueCube(
      EntityType<? extends DarkCubeEntity> entityType,
      float damage,
      float radius,
      int life,
      boolean trueDarkness,
      Vec3 pos,
      LivingEntity owner,
      ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost,
      Pair<Double, Double> increaseCost,
      int heldTicks
   ) {
      CompoundTag tag = instance.getOrCreateTag();
      Level level = owner.level();
      if (tag.getInt("BarrierID") == 0 && !EnergyHelper.isOutOfEnergy(owner, (Double)cost.getFirst(), (Double)cost.getSecond())) {
         DarkCubeEntity cube = (DarkCubeEntity)entityType.create(level);
         if (cube == null) {
            return;
         }

         cube.setTrueDarkness(trueDarkness);
         cube.setEffectStack(true);
         cube.setTickEachHit(10);
         cube.setOwner(owner);
         cube.setDamage(damage);
         cube.setSize(radius);
         cube.setVisualSize(radius);
         cube.setLife(life);
         cube.setPos(pos);
         cube.setSkill(instance);
         cube.setMode(mode);
         cube.setApCost((Double)cost.getFirst());
         cube.setMpCost((Double)cost.getSecond());
         owner.level().addFreshEntity(cube);
         owner.swing(InteractionHand.MAIN_HAND, true);
         tag.putInt("BarrierID", cube.getId());
      } else if (owner.level().getEntity(tag.getInt("BarrierID")) instanceof DarkCubeEntity cube) {
         if (heldTicks % 20 != 0 || !EnergyHelper.isOutOfEnergy(owner, (Double)increaseCost.getFirst(), (Double)increaseCost.getSecond())) {
            cube.increaseLife(1);
         }
      } else {
         tag.putInt("BarrierID", 0);
      }

      instance.markDirty();
   }
}
