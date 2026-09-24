package io.github.manasmods.tensura.entity.human;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.unique.ThrowerSkill;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
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

public class MarkLaurenEntity extends OtherworlderEntity implements SmartBrainOwner<MarkLaurenEntity> {
   private SmartBrainSchedule schedule;

   public MarkLaurenEntity(EntityType<? extends MarkLaurenEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 15.0)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.MAX_HEALTH, 500.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0);
   }

   @Override
   public ResourceLocation getTextureLocation() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/otherworlder/mark_lauren.png");
   }

   @Override
   public List<ManasSkill> getUniqueSkills() {
      return List.of((ManasSkill)UniqueSkills.THROWER.get());
   }

   public MarkLaurenEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      MarkLaurenEntity entity = (MarkLaurenEntity)((EntityType)HumanEntityTypes.MARK_LAUREN.get()).create(pLevel);
      if (entity == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         entity.setOwnerUUID(uuid);
         entity.setTame(true, true);
      }

      return entity;
   }

   @Override
   public boolean doHurtTarget(Entity target) {
      boolean flag = super.doHurtTarget(target);
      if (flag && this.getThrower() != null) {
         TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.CLOUD, 1.0);
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         Vec3 vec3 = new Vec3(target.getX() - this.getX(), target.getY() - this.getY(), target.getZ() - this.getZ());
         target.setDeltaMovement(vec3.normalize().scale(3.0).add(0.0, 0.5, 0.0));
         target.hasImpulse = true;
         target.hurtMarked = true;
      }

      return flag;
   }

   private void throwItem(LivingEntity target) {
      ItemStack thrownStack = this.getThrownItem();
      if (thrownStack != null) {
         Projectile projectile = ThrowerSkill.getProjectile(this.level(), this, thrownStack, this.getThrower());
         if (projectile instanceof AbstractArrow arrow) {
            if (Boolean.TRUE.equals(thrownStack.get((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get()))) {
               arrow.pickup = Pickup.CREATIVE_ONLY;
            } else {
               arrow.pickup = Pickup.ALLOWED;
            }
         } else if (projectile instanceof ThrowableItemProjectile itemProjectile) {
            itemProjectile.setItem(thrownStack);
         }

         double d1 = target.getX() - this.getX();
         double d2 = target.getY() - projectile.getY();
         double d3 = target.getZ() - this.getZ();
         double f = Math.sqrt(d1 * d1 + d3 * d3) * 0.2F;
         projectile.shoot(d1, d2 + f, d3, 2.0F, 0.0F);
         this.level().addFreshEntity(projectile);
         this.swing(InteractionHand.OFF_HAND, true);
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARROW_SHOOT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   @Nullable
   private ItemStack getThrownItem() {
      ItemStack offhandItem = this.getOffhandItem();
      if (!offhandItem.isEmpty()) {
         ItemStack thrownItem = offhandItem.copy();
         offhandItem.shrink(1);
         return thrownItem;
      } else {
         BlockState state = this.level().getBlockState(this.getOnPos());
         ItemStack stack = new ItemStack(
            (ItemLike)(!state.is(TensuraBlockTags.SKILL_UNOBTAINABLE) && !state.is(TensuraBlockTags.SKILL_UNBREAKABLE) ? state.getBlock() : Items.AIR)
         );
         stack.set((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), true);
         this.setItemSlot(EquipmentSlot.OFFHAND, stack);
         TensuraParticleHelper.addServerParticlesAroundPos(
            this.getRandom(), this.level(), this.position().add(0.0, 0.5, 0.0), new BlockParticleOption(ParticleTypes.BLOCK, state), 1.0
         );
         this.level()
            .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         return null;
      }
   }

   @Nullable
   private ManasSkillInstance getThrower() {
      Optional<ManasSkillInstance> skill = SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)UniqueSkills.THROWER.get());
      if (skill.isEmpty()) {
         return null;
      } else {
         return !skill.get().canInteractSkill(this) ? null : skill.get();
      }
   }

   @Override
   protected float getEquipmentDropChance(EquipmentSlot pSlot) {
      if (this.isTame()) {
         return 0.0F;
      } else {
         float chance = super.getEquipmentDropChance(pSlot);
         if (pSlot.equals(EquipmentSlot.MAINHAND)) {
            return Math.max(0.05F, chance);
         } else {
            return pSlot.equals(EquipmentSlot.OFFHAND)
                  && Boolean.TRUE.equals(this.getItemBySlot(pSlot).get((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get()))
               ? 0.0F
               : chance;
         }
      }
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         this.populateDefaultEquipmentSlots(this.random, pDifficulty);
      }

      TensuraBehaviourHelper.setHome(this, pLevel.getLevel(), this.blockPosition());
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      super.populateDefaultEquipmentSlots(pRandom, pDifficulty);
      ItemStack stack = new ItemStack((ItemLike)TensuraToolItems.MITHRIL_AXE.get());
      stack.set(DataComponents.ITEM_NAME, Component.literal("Minos Bardiche").withStyle(ChatFormatting.GOLD));
      this.setItemSlot(EquipmentSlot.MAINHAND, stack);
      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
      this.updateContainerEquipment();
   }

   @Nullable
   @Override
   public Item getEquipmentForArmor(EquipmentSlot pSlot, int pChance) {
      return switch (pSlot) {
         case HEAD -> pChance == 3 ? Items.IRON_HELMET : null;
         case CHEST -> pChance == 3 ? Items.IRON_CHESTPLATE : (pChance == 1 ? Items.CHAINMAIL_CHESTPLATE : null);
         case LEGS -> pChance == 3 ? Items.IRON_LEGGINGS : (pChance == 1 ? Items.CHAINMAIL_LEGGINGS : null);
         case FEET -> pChance == 3 ? Items.IRON_BOOTS : (pChance == 1 ? Items.CHAINMAIL_BOOTS : null);
         default -> null;
      };
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<MarkLaurenEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new SleepSensor()});
   }

   public BrainActivityGroup<MarkLaurenEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new WakeUp().stopIf(entity -> {
         if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
            entity.stopSleeping();
            return true;
         } else {
            return false;
         }
      }), new LookAtTarget(), new FloatToSurfaceOfFluid(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<MarkLaurenEntity> getIdleTasks() {
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

   public BrainActivityGroup<MarkLaurenEntity> getFightTasks() {
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
                  new CustomRangeAttack(0)
                     .attackInterval(entity -> 20)
                     .minAttackRadius(5.0F)
                     .maxAttackRadius(32.0F)
                     .performAttack(MarkLaurenEntity::throwItem)
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.25 && entity.getThrower() != null),
                  new AnimatableMeleeAttack(1)
                     .attackInterval(entity -> 10)
                     .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
                     .startCondition(entity -> !entity.usingRangedWeapon())
               }
            )
         }
      );
   }

   public Map<Activity, BrainActivityGroup<? extends MarkLaurenEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends MarkLaurenEntity>>)Util.make(
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
