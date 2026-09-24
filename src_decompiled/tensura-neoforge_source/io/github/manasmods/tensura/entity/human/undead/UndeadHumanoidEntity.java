package io.github.manasmods.tensura.entity.human.undead;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.InteractWithDoor;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import org.jetbrains.annotations.NotNull;

public class UndeadHumanoidEntity extends PlayerLikeEntity implements SmartBrainOwner<UndeadHumanoidEntity> {
   private static final EntityDataAccessor<Boolean> BURN_IN_SUNLIGHT = SynchedEntityData.defineId(UndeadHumanoidEntity.class, EntityDataSerializers.BOOLEAN);

   public UndeadHumanoidEntity(EntityType<? extends UndeadHumanoidEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.STEP_HEIGHT, 0.5)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(BURN_IN_SUNLIGHT, Boolean.FALSE);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("BurnInSunlight", (Boolean)this.entityData.get(BURN_IN_SUNLIGHT));
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(BURN_IN_SUNLIGHT, compound.getBoolean("BurnInSunlight"));
   }

   public boolean shouldBurnInSunlight() {
      return (Boolean)this.entityData.get(BURN_IN_SUNLIGHT);
   }

   public void setBurnInSunlight(boolean burn) {
      this.entityData.set(BURN_IN_SUNLIGHT, burn);
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return false;
   }

   @Override
   public boolean shouldSwim() {
      return false;
   }

   public boolean isSwimming() {
      return false;
   }

   @Override
   public void aiStep() {
      if (this.isAlive()) {
         this.applyBurn();
      }

      super.aiStep();
   }

   protected void applyBurn() {
      if (this.shouldBurnInSunlight() && this.isSunBurnTick()) {
         ItemStack stack = this.getItemBySlot(EquipmentSlot.HEAD);
         if (!stack.isEmpty()) {
            if (stack.isDamageableItem()) {
               Item item = stack.getItem();
               stack.setDamageValue(stack.getDamageValue() + this.random.nextInt(2));
               if (stack.getDamageValue() >= stack.getMaxDamage()) {
                  this.onEquippedItemBroken(item, EquipmentSlot.HEAD);
                  this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
               }
            }
         } else {
            this.igniteForSeconds(8.0F);
         }
      }
   }

   @Override
   public boolean doHurtTarget(Entity entity) {
      boolean hurt = super.doHurtTarget(entity);
      if (hurt) {
         float f = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
         if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
            entity.igniteForSeconds(2 * (int)f);
         }
      }

      return hurt;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.SLIME_FOOD);
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<UndeadHumanoidEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<UndeadHumanoidEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<UndeadHumanoidEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, target -> target.getType().equals(EntityType.PLAYER)),
                  new SubordinateFollowOwner(),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new InteractWithDoor(),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<UndeadHumanoidEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new StrafeTarget().stopStrafingWhen(entity -> !entity.usingRangedWeapon()).startCondition(PlayerLikeEntity::usingRangedWeapon),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F).startCondition(entity -> !entity.usingRangedWeapon()),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  PlayerLikeEntity.getSpearAttack(20).attackInterval(entity -> 5).attackRadius(25.0F),
                  PlayerLikeEntity.getCrossbowAttack().attackInterval(entity -> 10).attackRadius(30.0F),
                  PlayerLikeEntity.getBowAttack().attackInterval(entity -> 10).attackRadius(25.0F),
                  new AnimatableMeleeAttack(1)
                     .attackInterval(entity -> 5)
                     .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
                     .startCondition(entity -> !entity.usingRangedWeapon())
               }
            )
         }
      );
   }
}
