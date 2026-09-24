package io.github.manasmods.tensura.entity.human;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.intrinsic.CharmSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FireBoltProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.IceLanceProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.StoneShotProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WindBladeProjectile;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class KiraraMizutaniEntity extends OtherworlderEntity implements SmartBrainOwner<KiraraMizutaniEntity> {
   private final List<Element> elements = List.of(Element.EARTH, Element.FLAME, Element.WATER, Element.WIND);
   private Element chosenElement = Element.FLAME;
   private SmartBrainSchedule schedule;

   public KiraraMizutaniEntity(EntityType<? extends KiraraMizutaniEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.MAX_HEALTH, 150.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 1.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 5.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 3.0);
   }

   @Override
   public ResourceLocation getTextureLocation() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/otherworlder/kirara_mizutani.png");
   }

   @Override
   public List<ManasSkill> getUniqueSkills() {
      return List.of((ManasSkill)UniqueSkills.BEWILDER.get());
   }

   @Override
   public boolean usingRangedWeapon() {
      return !this.getMainHandItem().is(ItemTags.SHARP_WEAPON_ENCHANTABLE);
   }

   public KiraraMizutaniEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      KiraraMizutaniEntity entity = (KiraraMizutaniEntity)((EntityType)HumanEntityTypes.KIRARA_MIZUTANI.get()).create(pLevel);
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

   private void shootMagicProjectile() {
      if (this.getTarget() != null) {
         this.lookAt(Anchor.EYES, this.getTarget().getEyePosition());
      }
      TensuraFlyingProjectile projectile = switch (this.chosenElement) {
         case EARTH -> new StoneShotProjectile(this.level(), this);
         case WATER -> new IceLanceProjectile(this.level(), this);
         case WIND -> new WindBladeProjectile(this.level(), this);
         default -> new FireBoltProjectile(this.level(), this);
      };
      projectile.setSpeed(1.5F);
      projectile.setDamage(20.0F);
      if (this.chosenElement == Element.FLAME) {
         projectile.setBurnTicks(10);
      }

      projectile.setNoGravity(true);
      projectile.setPosAndShoot(this);
      this.level().addFreshEntity(projectile);
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }

   private void mindControlSurrounding() {
      LivingEntity livingTarget = this.getTarget();
      if (livingTarget != null) {
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BAT_HURT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         List<Mob> list = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(20.0), mob -> !mob.is(this) && mob.isAlive());
         if (!list.isEmpty()) {
            for (Mob target : list) {
               if (CharmSkill.canMindControl(target, this.level(), true, true)
                  && !target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))
                  && !RaceUtils.isSpiritual(target)
                  && !SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
                  target.setAggressive(true);
                  target.setTarget(livingTarget);
                  if (target instanceof NeutralMob neutralMob) {
                     neutralMob.setTarget(livingTarget);
                  }

                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.DAMAGE_INDICATOR);
               }
            }
         }
      }
   }

   private void instantKill() {
      LivingEntity target = this.getTarget();
      if (target != null) {
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BAT_HURT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         if (CharmSkill.canMindControl(target, this.level(), true, true)) {
            if (!target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))) {
               if (!RaceUtils.isSpiritual(target)) {
                  if (!SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
                     if (!(EnergyHelper.getMaxEP(this) < EnergyHelper.getMaxEP(target))) {
                        float damage = target.getHealth();
                        if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
                           damage /= 2.0F;
                        }

                        if (target.hurt(TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.SUICIDE, this), damage)) {
                           TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ENCHANTED_HIT);
                           TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   private ManasSkillInstance getBewilder() {
      Optional<ManasSkillInstance> skill = SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)UniqueSkills.BEWILDER.get());
      if (skill.isEmpty()) {
         return null;
      } else {
         return !skill.get().canInteractSkill(this) ? null : skill.get();
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
      ItemStack stack = new ItemStack(Items.STICK);
      stack.set(DataComponents.ITEM_NAME, Component.literal("Staff").withStyle(ChatFormatting.GOLD));
      this.setItemSlot(EquipmentSlot.MAINHAND, stack);
      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
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

   public List<ExtendedSensor<KiraraMizutaniEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new SleepSensor()});
   }

   public BrainActivityGroup<KiraraMizutaniEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new WakeUp().stopIf(entity -> {
         if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
            entity.stopSleeping();
            return true;
         } else {
            return false;
         }
      }), new LookAtTarget(), new FloatToSurfaceOfFluid(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<KiraraMizutaniEntity> getIdleTasks() {
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

   public BrainActivityGroup<KiraraMizutaniEntity> getFightTasks() {
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
                     .requireInSight(false)
                     .minAttackRadius(0.0F)
                     .maxAttackRadius(20.0F)
                     .attackInterval(entity -> 20)
                     .performAttack((entity, target) -> entity.mindControlSurrounding())
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.25 && entity.getBewilder() != null),
                  new CustomRangeAttack(0)
                     .requireInSight(false)
                     .minAttackRadius(0.0F)
                     .maxAttackRadius(20.0F)
                     .attackInterval(entity -> 20)
                     .performAttack((entity, target) -> entity.instantKill())
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.125 && entity.getBewilder() != null),
                  new CustomRangeAttack(10)
                     .requireInSight(false)
                     .minAttackRadius(5.0F)
                     .maxAttackRadius(32.0F)
                     .attackInterval(entity -> 20)
                     .performAttack((entity, target) -> entity.shootMagicProjectile())
                     .whenStarting(entity -> {
                        Element element = entity.elements.get(entity.getRandom().nextInt(entity.elements.size()));
                        MagicCircle.castMagicCircle(1.0F, 15, element.getMagicCircleVariant(), entity, 1.0F, Vec3.ZERO, (ManasSkill)null, 0, Pair.of(0.0, 0.0));
                        entity.chosenElement = element;
                     })
                     .startCondition(entity -> !entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_MAGIC))),
                  new AnimatableMeleeAttack(1)
                     .attackInterval(entity -> 5)
                     .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
                     .startCondition(entity -> !entity.usingRangedWeapon())
               }
            )
         }
      );
   }

   public Map<Activity, BrainActivityGroup<? extends KiraraMizutaniEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends KiraraMizutaniEntity>>)Util.make(
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
