package io.github.manasmods.tensura.entity.human;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.unique.BerserkerSkill;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.schedule.Activity;
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

public class ShogoTaguchiEntity extends OtherworlderEntity implements SmartBrainOwner<ShogoTaguchiEntity> {
   private SmartBrainSchedule schedule;

   public ShogoTaguchiEntity(EntityType<? extends ShogoTaguchiEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.ATTACK_KNOCKBACK, 5.0)
         .add(Attributes.MAX_HEALTH, 200.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 7.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 3.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 3.0);
   }

   @Override
   public ResourceLocation getTextureLocation() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/otherworlder/shogo_taguchi.png");
   }

   @Override
   public List<ManasSkill> getUniqueSkills() {
      return List.of((ManasSkill)UniqueSkills.BERSERKER.get(), (ManasSkill)UniqueSkills.SURVIVOR.get());
   }

   public ShogoTaguchiEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      ShogoTaguchiEntity entity = (ShogoTaguchiEntity)((EntityType)HumanEntityTypes.SHOGO_TAGUCHI.get()).create(pLevel);
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

   private void activateBerserker() {
      if (this.getBerserker() != null) {
         double EP = EnergyHelper.getMaxEP(this) * 10.0;
         AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
         if (armor != null) {
            if (armor.hasModifier(BerserkerSkill.BERSERKER)) {
               return;
            }

            AttributeModifier armorModifier = new AttributeModifier(BerserkerSkill.BERSERKER, BerserkerSkill.getArmor(EP), Operation.ADD_VALUE);
            armor.addOrReplacePermanentModifier(armorModifier);
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.TRANSFORM_BERSERKER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            TensuraParticleHelper.addServerParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), 3.0);
            TensuraParticleHelper.spawnServerParticles(
               this.level(),
               (ParticleOptions)TensuraParticleTypes.PURPLE_LIGHTNING_SPARK.get(),
               this.getX(),
               this.getY() + this.getBbHeight() / 2.0F,
               this.getZ(),
               25,
               0.08,
               0.08,
               0.08,
               0.2,
               true
            );
            TensuraParticleHelper.spawnServerParticles(
               this.level(),
               TensuraParticleUtils.getGoldWave(0.9F, this.getBbWidth() * 6.0F, 0.1F, true),
               this.getX(),
               this.getY() + this.getBbHeight() * 0.66F,
               this.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               this.level(),
               TensuraParticleUtils.getPurpleWave(0.75F, this.getBbWidth() * 7.0F, 0.1F, true),
               this.getX(),
               this.getY() + this.getBbHeight() * 0.33F,
               this.getZ()
            );
         }

         AttributeInstance damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
         if (damage != null) {
            damage.addOrReplacePermanentModifier(new AttributeModifier(BerserkerSkill.BERSERKER, BerserkerSkill.getAttack(EP), Operation.ADD_VALUE));
         }

         AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
         if (speed != null) {
            speed.addOrReplacePermanentModifier(new AttributeModifier(BerserkerSkill.BERSERKER, BerserkerSkill.getSpeed(EP) / 100.0, Operation.ADD_VALUE));
         }
      }
   }

   private void deactivateBerserker() {
      AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
      if (armor != null && armor.removeModifier(BerserkerSkill.BERSERKER)) {
         this.level()
            .playSound(
               null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }

      AttributeInstance damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
      if (damage != null) {
         damage.removeModifier(BerserkerSkill.BERSERKER);
      }

      AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
      if (speed != null) {
         speed.removeModifier(BerserkerSkill.BERSERKER);
      }
   }

   @Nullable
   private ManasSkillInstance getBerserker() {
      Optional<ManasSkillInstance> skill = SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)UniqueSkills.BERSERKER.get());
      if (skill.isEmpty()) {
         return null;
      } else {
         return !skill.get().canInteractSkill(this) ? null : skill.get();
      }
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      TensuraBehaviourHelper.setHome(this, pLevel.getLevel(), this.blockPosition());
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<ShogoTaguchiEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new SleepSensor()});
   }

   public BrainActivityGroup<ShogoTaguchiEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new WakeUp().stopIf(entity -> {
         if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
            entity.stopSleeping();
            return true;
         } else {
            return false;
         }
      }), new LookAtTarget(), new FloatToSurfaceOfFluid(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<ShogoTaguchiEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldTarget).whenStarting(ShogoTaguchiEntity::activateBerserker),
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

   public BrainActivityGroup<ShogoTaguchiEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().onInvalidate((entity, target) -> {
               entity.forgetCurrentTargetAndRefreshUniversalAnger();
               entity.deactivateBerserker();
            }).invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new AnimatableMeleeAttack(1)
               .attackInterval(entity -> 10)
               .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
               .startCondition(entity -> !entity.usingRangedWeapon())
         }
      );
   }

   public Map<Activity, BrainActivityGroup<? extends ShogoTaguchiEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends ShogoTaguchiEntity>>)Util.make(
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
