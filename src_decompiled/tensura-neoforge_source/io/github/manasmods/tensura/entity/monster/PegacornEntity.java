package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class PegacornEntity extends PegasusEntity {
   private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(PegacornEntity.class, EntityDataSerializers.BOOLEAN);

   public PegacornEntity(EntityType<? extends PegacornEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.FLYING_SPEED, 0.9F)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.JUMP_STRENGTH, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.01F)
         .add(Attributes.STEP_HEIGHT, 1.5)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 2.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(CHARGING, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Charging", this.isCharging());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setCharging(compound.getBoolean("Charging"));
   }

   public boolean isCharging() {
      return (Boolean)this.entityData.get(CHARGING);
   }

   public void setCharging(boolean charging) {
      this.entityData.set(CHARGING, charging);
   }

   @Override
   public boolean canMate(Animal animal) {
      return false;
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      return null;
   }

   @Override
   protected void handleMountAbility() {
      if (this.level().isClientSide()) {
         if (this.getRandom().nextFloat() >= 0.3F || this.isBaby()) {
            return;
         }

         float radius = this.getBbWidth();
         this.level()
            .addParticle(
               ParticleTypes.INSTANT_EFFECT,
               this.getX() + (this.level().random.nextDouble() - 0.5) * radius,
               this.getY() + (this.level().random.nextDouble() - 0.5) * radius * 0.75,
               this.getZ() + (this.level().random.nextDouble() - 0.5) * radius,
               0.0,
               0.0,
               0.0
            );
      } else {
         if (this.isCharging()) {
            AABB aabb = this.getBoundingBox().move(this.getViewVector(1.0F));

            for (LivingEntity living : this.level()
               .getEntitiesOfClass(LivingEntity.class, aabb, livingx -> !livingx.is(this) && livingx.getVehicle() != this && livingx.isAlive())) {
               this.doHurtTarget(living, 1.0F);
               this.playSound(SoundEvents.HORSE_ANGRY);
            }
         }

         if (this.mountAbilityCooldown > 0 && --this.mountAbilityCooldown <= 0) {
            this.setCharging(false);
         }
      }
   }

   @Override
   protected float getRiddenSpeed(Player player) {
      float speed = super.getRiddenSpeed(player);
      return this.isCharging() ? speed * 4.0F / 3.0F : speed;
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAbilityCooldown <= 0) {
         this.mountAbilityCooldown = 60;
         this.setCharging(true);
         this.playSound(SoundEvents.HORSE_ANGRY);
      }
   }

   @Override
   public BrainActivityGroup<PegasusEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 1.2F),
            new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     new CustomRangeAttack(10)
                        .maxAttackRadius(10.0F)
                        .minAttackRadius(2.0F)
                        .attackInterval(entity -> 40)
                        .performAttack((entity, target) -> entity.setSprinting(true))
                        .whenStarting(entity -> entity.setCharging(true)),
                     new AnimatableMeleeAttack(0).whenStarting(entity -> {
                        if (!entity.isCharging()) {
                           entity.triggerAnim("miscController", "stand");
                        }

                        entity.setSprinting(false);
                     }).whenStopping(entity -> entity.setCharging(false))
                  }
               )
               .startCondition(entity -> !entity.isVehicle())
         }
      );
   }

   @Override
   protected PlayState loopController(AnimationState<PegasusEntity> state) {
      String name;
      if (!this.isAlive()) {
         name = "animation.horse.death";
      } else if (state.isMoving()) {
         if (this.isCharging()) {
            name = "animation.unicorn.charge";
         } else if (this.isInLiquid() || !this.isSprinting() && !this.isAngry()) {
            name = "animation.horse.walk";
         } else {
            name = "animation.horse.run";
         }
      } else {
         name = "animation.horse.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }
}
