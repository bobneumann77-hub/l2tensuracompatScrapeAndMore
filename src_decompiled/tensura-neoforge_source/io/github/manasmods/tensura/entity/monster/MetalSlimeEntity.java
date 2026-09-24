package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.PanicAroundEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import lombok.NonNull;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;

public class MetalSlimeEntity extends SlimeEntity {
   public MetalSlimeEntity(EntityType<? extends MetalSlimeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.JUMP_STRENGTH, 1.2)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.STEP_HEIGHT, 2.0)
         .add(Attributes.SAFE_FALL_DISTANCE, 50.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.5);
   }

   public void setSize(int pSize, boolean pResetHealth) {
      this.setSize(pSize, true, pResetHealth, false);
   }

   @Override
   public boolean isInvulnerableTo(DamageSource source) {
      return this.ignoreDamageSource(source) || super.isInvulnerableTo(source);
   }

   public boolean ignoreDamageSource(DamageSource source) {
      if (source.tensura$getResistanceBypassLevel() >= 1.0F) {
         return false;
      } else {
         return source.getEntity() instanceof LivingEntity attacker && attacker.getAttributeValue(TensuraAttributes.RESISTANCE_DEGRADATION) >= 1.0
            ? false
            : TensuraDamageHelper.isNaturalEffects(source);
      }
   }

   @Override
   public boolean isDyeable() {
      return false;
   }

   @Override
   public boolean canConsumeCore() {
      return false;
   }

   @Override
   public boolean isTamingFood(ItemStack stack) {
      return stack.is(TensuraItemTags.METAL_SLIME_TAMING_FOOD);
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   protected float getWaterSlowDown() {
      return 0.9F;
   }

   public boolean shouldStopTarget(Mob subordinate, LivingEntity target) {
      return !this.isTame() ? false : super.shouldStopTarget(subordinate, target);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.isInWater()) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.1, 0.0));
      }
   }

   @Override
   protected void selfRegen() {
      if (this.isMassive()) {
         this.heal(20.0F);
      } else {
         this.heal(4.0F);
      }

      this.selfRegen = 20;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.METAL_SLIME_HURT.get();
   }

   @Override
   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.METAL_SLIME_DEATH.get();
   }

   @Override
   protected SoundEvent getSquishSound() {
      return (SoundEvent)TensuraSoundEvents.METAL_SLIME_SQUISH.get();
   }

   @Override
   protected SoundEvent getAttackSound() {
      return (SoundEvent)TensuraSoundEvents.METAL_SLIME_ATTACK.get();
   }

   @Override
   protected SoundEvent getJumpSound() {
      return (SoundEvent)TensuraSoundEvents.METAL_SLIME_SQUISH.get();
   }

   @NonNull
   @Override
   public SoundEvent getPickupSound() {
      return this.getSquishSound();
   }

   @Override
   public BrainActivityGroup<SlimeEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   @Override
   public BrainActivityGroup<SlimeEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new PanicAroundEntity()
                     .speedMod(entity -> 2.0F)
                     .noCloserThan(24.0F)
                     .setRadius(64.0)
                     .avoiding(entity -> entity instanceof Mob mob && mob.getTarget() == entity ? true : !entity.hasInfiniteMaterials())
                     .additionAvoidingFunction((entity, target, pos) -> {
                        if (entity.onGround() && this.getJumpPower() > 0.0F) {
                           Vec3 velocity = new Vec3(pos.x - entity.getX(), 0.0, pos.z - entity.getZ());
                           if (velocity.lengthSqr() > 1.0E-7) {
                              velocity = velocity.normalize()
                                 .add(entity.getDeltaMovement().scale(4.0))
                                 .scale(entity.getAttributeValue(Attributes.MOVEMENT_SPEED) / 0.2);
                           }

                           entity.setDeltaMovement(velocity.x, this.getJumpPower(), velocity.z);
                        }
                     })
                     .startCondition(entity -> !entity.isTame())
                     .runFor(entity -> 200),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, target -> false).startCondition(TamableAnimal::isTame),
                  new SubordinateFollowOwner(),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }
}
