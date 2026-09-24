package io.github.manasmods.tensura.entity.human;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.InteractWithDoor;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.schedule.SmartBrainSchedule;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FalmuthKnightEntity extends PlayerLikeEntity implements SmartBrainOwner<FalmuthKnightEntity> {
   private static final ResourceLocation REINFORCEMENT_CALLER_CHARGE_ID = ResourceLocation.fromNamespaceAndPath("tensura", "reinforcement_caller_charge");
   private static final AttributeModifier REINFORCEMENT_CALLER_CHARGE = new AttributeModifier(
      ResourceLocation.fromNamespaceAndPath("tensura", "reinforcement_callee_charge"), -0.05, Operation.ADD_VALUE
   );
   private SmartBrainSchedule schedule;

   public FalmuthKnightEntity(EntityType<? extends FalmuthKnightEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 20.0)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.ATTACK_KNOCKBACK, 1.0)
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.1F);
   }

   public boolean shouldTarget(LivingEntity target) {
      if (EnergyHelper.getMaxEP(target) < 5000.0) {
         return false;
      }

      Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(target).getRace();
      if (race.isPresent()) {
         if (race.get().is(TensuraRaceTags.SPIRITUAL)) {
            return false;
         } else {
            return TensuraStorages.getExistenceFrom(target).getAlignment().equals(Alignment.MAJIN) ? true : !race.get().is(TensuraRaceTags.HUMAN_LIKE);
         }
      } else {
         return target.getType().is(TensuraEntityTags.OTHERWORLDER_PREY)
            ? true
            : TensuraStorages.getExistenceFrom(target).getAlignment().equals(Alignment.MAJIN);
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      TensuraBehaviourHelper.saveGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      TensuraBehaviourHelper.readGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
   }

   public void push(Entity pEntity) {
      if (!(pEntity instanceof FalmuthKnightEntity)) {
         super.push(pEntity);
      }
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return false;
   }

   public void die(DamageSource damageSource) {
      TensuraBehaviourHelper.releaseHome(this);
      super.die(damageSource);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         this.populateDefaultEquipmentSlots(this.getRandom(), pDifficulty);
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      ItemStack weapon = new ItemStack(pRandom.nextBoolean() ? (ItemLike)TensuraToolItems.IRON_SPEAR.get() : (ItemLike)TensuraToolItems.IRON_LONG_SWORD.get());
      if (pRandom.nextInt(10) == 1) {
         weapon.enchant(TensuraEnchantmentHelper.getEnchantment(this.level(), Enchantments.SHARPNESS), 2);
      }

      this.setItemSlot(EquipmentSlot.MAINHAND, weapon);
      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), weapon);
      this.updateContainerEquipment();
      if (!(pRandom.nextFloat() >= 0.8F)) {
         ItemStack stack = new ItemStack(Items.SHIELD);
         this.setItemSlot(EquipmentSlot.OFFHAND, stack);
         this.inventory.setItem(this.getSlotId(EquipmentSlot.OFFHAND), stack);
         this.updateContainerEquipment();
      }
   }

   public boolean hurt(DamageSource damageSource, float f) {
      if (!super.hurt(damageSource, f)) {
         return false;
      } else if (!(this.level() instanceof ServerLevel serverLevel)) {
         return false;
      } else {
         if (!this.isTame()) {
            LivingEntity target = this.getTarget();
            if (target == null && damageSource.getEntity() instanceof LivingEntity attacker) {
               target = attacker;
            }

            if (target != null
               && this.level().getDifficulty() == Difficulty.HARD
               && this.random.nextFloat() < this.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
               && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
               int i = Mth.floor(this.getX());
               int j = Mth.floor(this.getY());
               int k = Mth.floor(this.getZ());
               FalmuthKnightEntity knight = new FalmuthKnightEntity(
                  (EntityType<? extends FalmuthKnightEntity>)HumanEntityTypes.FALMUTH_KNIGHT.get(), this.level()
               );

               for (int l = 0; l < 50; l++) {
                  int m = i + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                  int n = j + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                  int o = k + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                  BlockPos blockPos = new BlockPos(m, n, o);
                  EntityType<?> entityType = knight.getType();
                  if (SpawnPlacements.isSpawnPositionOk(entityType, this.level(), blockPos)
                     && SpawnPlacements.checkSpawnRules(entityType, serverLevel, MobSpawnType.REINFORCEMENT, blockPos, this.level().random)) {
                     knight.setPos(m, n, o);
                     if (!this.level().hasNearbyAlivePlayer(m, n, o, 7.0)
                        && this.level().isUnobstructed(knight)
                        && this.level().noCollision(knight)
                        && !this.level().containsAnyLiquid(knight.getBoundingBox())) {
                        knight.setTarget(target);
                        knight.finalizeSpawn(
                           serverLevel, this.level().getCurrentDifficultyAt(knight.blockPosition()), MobSpawnType.REINFORCEMENT, (SpawnGroupData)null
                        );
                        serverLevel.addFreshEntityWithPassengers(knight);
                        AttributeInstance attributeInstance = this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
                        if (attributeInstance == null) {
                           return false;
                        }

                        AttributeModifier attributeModifier = attributeInstance.getModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                        double d = attributeModifier != null ? attributeModifier.amount() : 0.0;
                        attributeInstance.removeModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                        attributeInstance.addOrReplacePermanentModifier(new AttributeModifier(REINFORCEMENT_CALLER_CHARGE_ID, d - 0.05, Operation.ADD_VALUE));
                        attributeInstance.addOrReplacePermanentModifier(REINFORCEMENT_CALLER_CHARGE);
                        break;
                     }
                  }
               }
            }
         }

         return true;
      }
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<FalmuthKnightEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new SleepSensor()});
   }

   public BrainActivityGroup<FalmuthKnightEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new WakeUp().stopIf(entity -> {
         if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
            entity.stopSleeping();
            return true;
         } else {
            return false;
         }
      }), new LookAtTarget(), new FloatToSurfaceOfFluid(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<FalmuthKnightEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldTarget),
                  new SubordinateFollowOwner(),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new InteractWithDoor(),
            new HumanoidConsumeItem().startCondition(entity -> entity.shouldHeal()).stopIf(entity -> !entity.shouldHeal()),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<FalmuthKnightEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new AnimatableMeleeAttack(1)
               .attackInterval(entity -> 10)
               .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
               .startCondition(entity -> !entity.usingRangedWeapon())
         }
      );
   }

   public Map<Activity, BrainActivityGroup<? extends FalmuthKnightEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends FalmuthKnightEntity>>)Util.make(
         new Object2ObjectOpenHashMap(), map -> map.put(Activity.REST, TensuraBehaviourHelper.getHumanoidSleepActivityGroup(this))
      );
   }

   public SmartBrainSchedule getSchedule() {
      if (this.schedule == null) {
         this.schedule = new SmartBrainSchedule().activityAt(10, Activity.IDLE).activityAt(13000, Activity.REST);
      }

      return this.schedule;
   }

   public void startSleeping(BlockPos blockPos) {
      super.startSleeping(blockPos);
      BrainUtils.setMemory(this, MemoryModuleType.LAST_SLEPT, this.level().getGameTime());
      BrainUtils.clearMemory(this, MemoryModuleType.WALK_TARGET);
      BrainUtils.clearMemory(this, MemoryModuleType.LOOK_TARGET);
      BrainUtils.clearMemory(this, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
   }

   public void stopSleeping() {
      super.stopSleeping();
      BrainUtils.setMemory(this, MemoryModuleType.LAST_WOKEN, this.level().getGameTime());
   }
}
