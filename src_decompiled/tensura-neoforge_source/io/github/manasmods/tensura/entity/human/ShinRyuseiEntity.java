package io.github.manasmods.tensura.entity.human;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.projectile.KunaiProjectile;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ITeleportation;
import io.github.manasmods.tensura.item.weapon.ranged.KunaiItem;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
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

public class ShinRyuseiEntity extends OtherworlderEntity implements SmartBrainOwner<ShinRyuseiEntity>, ITeleportation {
   private SmartBrainSchedule schedule;

   public ShinRyuseiEntity(EntityType<? extends ShinRyuseiEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.ATTACK_KNOCKBACK, 1.0)
         .add(Attributes.MAX_HEALTH, 400.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 2.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 5.0);
   }

   @Override
   public ResourceLocation getTextureLocation() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/otherworlder/shin_ryusei.png");
   }

   @Override
   public List<ManasSkill> getUniqueSkills() {
      return List.of((ManasSkill)UniqueSkills.OBSERVER.get());
   }

   public ShinRyuseiEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      ShinRyuseiEntity entity = (ShinRyuseiEntity)((EntityType)HumanEntityTypes.SHIN_RYUSEI.get()).create(pLevel);
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

   public boolean hurt(DamageSource source, float pAmount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      }

      this.dodge(source);
      return super.hurt(source, pAmount);
   }

   @Override
   public void onProjectileImpact(
      EntityHitResult hitResult, Projectile projectile, Changeable<ProjectileDeflection> deflection, Changeable<ProjectileHitResult> result
   ) {
      if (!this.level().isClientSide()) {
         if (this.getObserver() != null) {
            if (!(this.getAttributeValue(Attributes.MOVEMENT_SPEED) <= 0.0)) {
               if (projectile.getOwner() instanceof LivingEntity shooter) {
                  double negate = shooter.getAttributeValue(TensuraAttributes.DODGE_NEGATE_CHANCE);
                  if (shooter.getRandom().nextFloat() * 100.0F < negate) {
                     return;
                  }
               }

               this.setPos(this.position().add(this.getRandom().nextFloat() * 2.0F - 1.0F, 0.0, this.getRandom().nextFloat() * 2.0F - 1.0F));
               result.set(ProjectileHitResult.PASS);
            }
         }
      }
   }

   private void dodge(DamageSource source) {
      if (this.getObserver() != null) {
         if (!this.level().isClientSide()) {
            if (!(this.getAttributeValue(Attributes.MOVEMENT_SPEED) <= 0.0)) {
               if (source.getDirectEntity() instanceof LivingEntity attacker) {
                  if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                     return;
                  }

                  if (!TensuraDamageHelper.isPhysicalAttack(source)) {
                     return;
                  }

                  if (source.isCreativePlayer()) {
                     return;
                  }

                  double negate = attacker.getAttributeValue(TensuraAttributes.DODGE_NEGATE_CHANCE);
                  if (attacker.getRandom().nextFloat() * 100.0F < negate) {
                     return;
                  }

                  this.setPos(this.position().add(this.getRandom().nextFloat() - 0.5, 0.0, this.getRandom().nextFloat() - 0.5));
               }
            }
         }
      }
   }

   private void throwKunai(LivingEntity target) {
      ItemStack stack = this.getOffhandItem().isEmpty() ? ((KunaiItem)TensuraToolItems.KUNAI.get()).getDefaultInstance() : this.getOffhandItem();
      KunaiProjectile kunai = new KunaiProjectile(this.level(), this, stack, true);
      kunai.setBaseDamage(14.0);
      kunai.pickup = Pickup.CREATIVE_ONLY;
      kunai.setMultishot(true);
      double d1 = target.getX() - this.getX();
      double d2 = target.getY() - kunai.getY();
      double d3 = target.getZ() - this.getZ();
      double f = Math.sqrt(d1 * d1 + d3 * d3) * 0.2F;
      kunai.shoot(d1, d2 + f, d3, 3.0F, 0.0F);
      this.level().addFreshEntity(kunai);
      this.swing(InteractionHand.OFF_HAND, true);
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARROW_SHOOT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }

   @Override
   public void teleportTowards(LivingEntity entity, Entity pTarget, double distance) {
      ITeleportation.super.teleportTowards(entity, pTarget, distance);
      if (pTarget instanceof LivingEntity target) {
         this.throwKunai(target);
      }
   }

   @Override
   public void teleport(LivingEntity entity, double pX, double pY, double pZ) {
      Vec3 oldPosition = new Vec3(this.xo, this.yo + this.getBbHeight() / 2.0F, this.zo);
      MutableBlockPos pos = new MutableBlockPos(pX, pY, pZ);

      while (pos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(pos).blocksMotion()) {
         pos.move(Direction.DOWN);
      }

      BlockState state = this.level().getBlockState(pos);
      if (state.blocksMotion() && state.getFluidState().isEmpty() && this.randomTeleport(pX, pY, pZ, false)) {
         TensuraParticleHelper.addServerParticlesAroundPos(this.getRandom(), this.level(), oldPosition, ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, 1.0);
         TensuraParticleHelper.addServerParticlesAroundPos(this.getRandom(), this.level(), oldPosition, ParticleTypes.EXPLOSION, 1.0);
         TensuraParticleHelper.addServerParticlesAroundPos(this.getRandom(), this.level(), oldPosition, ParticleTypes.CAMPFIRE_COSY_SMOKE, 2.0);
         if (!this.isSilent()) {
            this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.GENERIC_EXPLODE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   @Nullable
   private ManasSkillInstance getObserver() {
      Optional<ManasSkillInstance> skill = SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)UniqueSkills.OBSERVER.get());
      if (skill.isEmpty()) {
         return null;
      } else {
         return !skill.get().canInteractSkill(this) ? null : skill.get();
      }
   }

   @Nullable
   @Override
   public SoundEvent getTeleportSound() {
      return (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get();
   }

   @Override
   public boolean shouldBroadcastTeleport() {
      return false;
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
      ItemStack stack = new ItemStack((ItemLike)TensuraToolItems.KUNAI.get());
      this.setItemSlot(EquipmentSlot.MAINHAND, stack.copy());
      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack.copy());
      this.setItemSlot(EquipmentSlot.OFFHAND, stack.copy());
      this.inventory.setItem(this.getSlotId(EquipmentSlot.OFFHAND), stack.copy());
      this.updateContainerEquipment();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<ShinRyuseiEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new SleepSensor()});
   }

   public BrainActivityGroup<ShinRyuseiEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new WakeUp().stopIf(entity -> {
         if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
            entity.stopSleeping();
            return true;
         } else {
            return false;
         }
      }), new LookAtTarget(), new FloatToSurfaceOfFluid(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<ShinRyuseiEntity> getIdleTasks() {
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

   public BrainActivityGroup<ShinRyuseiEntity> getFightTasks() {
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
                     .maxAttackRadius(25.0F)
                     .performAttack(ShinRyuseiEntity::throwKunai),
                  new AnimatableMeleeAttack(1)
                     .attackInterval(entity -> 10)
                     .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
                     .startCondition(entity -> !entity.usingRangedWeapon())
               }
            )
         }
      );
   }

   public Map<Activity, BrainActivityGroup<? extends ShinRyuseiEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends ShinRyuseiEntity>>)Util.make(
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
