package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.ConditionlessAction;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.projectile.magic.ChaosEaterProjectile;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.variant.OrcVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.InteractWithDoor;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;

public class OrcDisasterEntity extends OrcLordEntity {
   private final ServerBossEvent bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossBarColor.RED, BossBarOverlay.NOTCHED_20)
      .setPlayBossMusic(true)
      .setDarkenScreen(true);
   public static final RawAnimation CRUSH = RawAnimation.begin().then("animation.orc_disaster.crush", LoopType.PLAY_ONCE);
   public static final RawAnimation CRY = RawAnimation.begin().then("animation.orc_disaster.cry", LoopType.PLAY_ONCE);
   public static final RawAnimation EAT = RawAnimation.begin().then("animation.orc_disaster.eat", LoopType.PLAY_ONCE);

   public OrcDisasterEntity(EntityType<? extends OrcDisasterEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 20.0)
         .add(Attributes.ATTACK_DAMAGE, 60.0)
         .add(Attributes.MAX_HEALTH, 700.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 4.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 4.0)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 5.0)
         .add(TensuraAttributes.AURA_GAIN, 10.0)
         .add(TensuraAttributes.MAGICULE_GAIN, 10.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 3.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 3.0)
         .add(TensuraAttributes.ABILITY_LEARNING_GAIN, 5.0)
         .add(TensuraAttributes.ABILITY_MASTERY_GAIN, 5.0);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }
   }

   public boolean canBeAffected(MobEffectInstance instance) {
      return instance.getEffect() == TensuraMobEffects.CORROSION.get() ? false : super.canBeAffected(instance);
   }

   @Override
   public boolean isInvulnerableTo(DamageSource source) {
      if (super.isInvulnerableTo(source)) {
         return true;
      } else if (TensuraDamageHelper.isCorrosion(source)) {
         return true;
      } else {
         return TensuraDamageHelper.isPoison(source) ? true : source.is(DamageTypes.FALL) || source.is(DamageTypes.CRAMMING) || super.isInvulnerableTo(source);
      }
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return this.isInvulnerableTo(pSource)
         ? false
         : super.hurt(pSource, pSource.tensura$getMagicType() == Magic.MagicType.ASPECTUAL ? pAmount * 0.5F : pAmount);
   }

   public void setCustomName(@Nullable Component pName) {
      super.setCustomName(pName);
      this.bossEvent.setName(this.getDisplayName());
   }

   public void startSeenByPlayer(ServerPlayer pPlayer) {
      super.startSeenByPlayer(pPlayer);
      if (!this.isTame()) {
         this.bossEvent.addPlayer(pPlayer);
      }
   }

   public void stopSeenByPlayer(ServerPlayer pPlayer) {
      super.stopSeenByPlayer(pPlayer);
      this.bossEvent.removePlayer(pPlayer);
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      if (!this.isTame()) {
         this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
      }
   }

   @Override
   protected void applyTamingSideEffects() {
      super.applyTamingSideEffects();
      this.bossEvent.removeAllPlayers();
   }

   @Override
   public int getChestSlots() {
      return 36;
   }

   @Override
   protected boolean shouldLaugh() {
      return false;
   }

   @Override
   protected void healAndEat() {
      this.heal(20.0F);
      this.selfRegen = 20;
      this.eatOrcs();
   }

   @Override
   public void areaAttack(float damageMultiplier, float upVector) {
      TensuraParticleHelper.spawnServerGroundSlamParticle(this, 5, 3.5F);
      super.areaAttack(damageMultiplier, upVector);
   }

   private void spawnChaosEater(LivingEntity entity, int amount, float distance) {
      int rot = 360 / amount;

      for (int i = 0; i < amount; i++) {
         Vec3 offset = new Vec3(0.0, distance, 0.0)
            .zRot((rot * i - rot / 2.0F) * (float) (Math.PI / 180.0))
            .xRot(-entity.getXRot() * (float) (Math.PI / 180.0))
            .yRot(-entity.getYRot() * (float) (Math.PI / 180.0));
         Vec3 offPos = entity.getEyePosition().add(offset);
         ChaosEaterProjectile chaosEater = new ChaosEaterProjectile(entity.level(), entity);
         chaosEater.setPos(offPos);
         chaosEater.setUpStartPos(amount, i, distance);
         LivingEntity target = this.getTarget();
         List<LivingEntity> list = this.getTargetList();
         if (i != 0 && !list.isEmpty()) {
            target = list.get(this.getRandom().nextInt(list.size()));
         }

         chaosEater.setTarget(target);
         chaosEater.shootFromRot(entity.getLookAngle());
         chaosEater.setLife(300);
         chaosEater.setDamage((float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE));
         chaosEater.setMobEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), 200, 3, true, false, true));
         chaosEater.setEffectRange(1.5F);
         chaosEater.setMpCost(200.0);
         chaosEater.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)UniqueSkills.STARVED.get()));
         entity.level().addFreshEntity(chaosEater);
      }
   }

   private List<LivingEntity> getTargetList() {
      AABB box = this.getBoundingBox().inflate(Math.min(50.0, this.getAttributeValue(Attributes.FOLLOW_RANGE)));
      return this.level().getEntitiesOfClass(LivingEntity.class, box, this::shouldAttack);
   }

   @Override
   protected void makeKnight(OrcEntity orc) {
      ItemStack helmet = new ItemStack(Items.NETHERITE_HELMET);
      ItemStack chestplate = new ItemStack(Items.NETHERITE_CHESTPLATE);
      ItemStack leggings = new ItemStack(Items.NETHERITE_LEGGINGS);
      ItemStack boots = new ItemStack(Items.NETHERITE_BOOTS);
      ItemStack weapon = new ItemStack((ItemLike)(this.random.nextBoolean() ? (ItemLike)TensuraToolItems.NETHERITE_SPEAR.get() : Items.NETHERITE_AXE));
      if (this.random.nextFloat() <= 0.3) {
         if (this.random.nextBoolean()) {
            weapon = new ItemStack((ItemLike)TensuraToolItems.LONG_BOW.get());
         } else {
            weapon = new ItemStack(Items.CROSSBOW);
         }
      }

      orc.addFakeItem(EquipmentSlot.HEAD, helmet);
      orc.addFakeItem(EquipmentSlot.CHEST, chestplate);
      orc.addFakeItem(EquipmentSlot.LEGS, leggings);
      orc.addFakeItem(EquipmentSlot.FEET, boots);
      orc.addFakeItem(EquipmentSlot.MAINHAND, weapon);
      if (this.random.nextFloat() <= 0.1) {
         orc.addFakeItem(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
      }
   }

   protected void tickDeath() {
      if (++this.deathTime >= 40) {
         this.remove(RemovalReason.KILLED);
         this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 1.0F);
         TensuraParticleHelper.addServerAuraParticles(this, TensuraParticleUtils.getChaosEaterAura(1.0F, 8.0F, -0.3F), 10, 0.01);
      }
   }

   @Override
   public void die(DamageSource source) {
      super.die(source);
      if (!this.isAlive() && !this.getVariant().equals(OrcVariant.ROYAL_LORD)) {
         if (this.getPose() == Pose.DYING) {
            this.triggerAnim("miscController2", "death");
         }
      }
   }

   @Override
   public BrainActivityGroup<OrcEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     TensuraBehaviourHelper.getPreyTargeting(this, this::shouldAttack),
                     TensuraBehaviourHelper.getMoveToWanderPos(),
                     new SubordinateFollowOwner(),
                     new SetPlayerLookTarget(),
                     new SetRandomLookTarget()
                  }
               )
               .startCondition(entity -> entity.stopMovingTick <= 0),
            new InteractWithDoor(),
            new HumanoidConsumeItem().startCondition(entity -> entity.shouldHeal() && entity.isTame()).stopIf(entity -> !entity.shouldHeal()),
            new ConditionlessAction(30).actionInterval(entity -> 100).attack(entity -> {
               if (entity.getFirstPassenger() instanceof LivingEntity living) {
                  living.stopRiding();
                  float heal = entity.getHealth();
                  entity.doHurtTarget(living, 3.5F);
                  if (living.isAlive()) {
                     entity.heal(75.0F);
                  } else {
                     entity.heal(heal * 3.0F);
                  }

                  entity.level().playSound(null, entity, (SoundEvent)TensuraSoundEvents.EATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               } else {
                  entity.heal(50.0F);
                  entity.level().playSound(null, entity, (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0));
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.END_ROD, 2.0);
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.COMPOSTER, 2.0);
               }
            }).whenStarting(entity -> {
               if (this.isVehicle()) {
                  entity.triggerAnim("miscController", "eat");
               } else {
                  entity.triggerAnim("miscController2", "recover");
               }

               entity.stopMovingTick = 55;
            }).startCondition(entity -> entity.isVehicle() || entity.stopMovingTick <= 0 && entity.shouldHeal()),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{
                     new SetRandomWalkTarget().startCondition(entity -> entity.stopMovingTick <= 0),
                     new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
                  }
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   @Override
   public BrainActivityGroup<OrcEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 1.5F).startCondition(entity -> entity.stopMovingTick <= 0),
            new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     new ConditionlessAction(0).actionInterval(entity -> 0).attack(Entity::ejectPassengers).startCondition(Entity::isVehicle),
                     new CustomRangeAttack(30)
                        .maxAttackRadius(40.0F)
                        .attackInterval(entity -> 0)
                        .performAttack((entity, target) -> entity.summonOrcRandomPos(4, 0, 7))
                        .whenStopping(entity -> entity.shouldSummonOrcs = false)
                        .whenStarting(entity -> {
                           entity.stopMovingTick = 55;
                           entity.triggerAnim("miscController2", "recover");
                        })
                        .startCondition(entity -> entity.shouldSummonOrcs),
                     new CustomRangeAttack(25).minAttackRadius(6.0F).maxAttackRadius(32.0F).attackInterval(entity -> 0).performAttack((entity, target) -> {
                        int amount = entity.getHealth() <= entity.getMaxHealth() / 2.0F ? 6 : 4;
                        entity.spawnChaosEater(entity, amount, 1.2F);
                        TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getChaosEaterAura(1.0F, 8.0F, -0.3F), 10, 0.01);
                        entity.playSound(SoundEvents.WARDEN_ROAR, 10.0F, 0.95F + this.random.nextFloat() * 0.1F);
                     }).whenStarting(entity -> {
                        entity.stopMovingTick = 65;
                        entity.triggerAnim("miscController", "yell");
                     }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.05F && entity.getStarved() != null),
                     new CustomHeldAttack()
                        .maxAttackRadius(entity -> entity.getFirstPassenger() != null ? 64.0F : 4.0F)
                        .minAttackRadius(0.0F)
                        .attackInterval(entity -> 40)
                        .onTick((entity, target, tick) -> {
                           if (tick >= 15 && entity.getFirstPassenger() == null) {
                              target.startRiding(entity, true);
                           } else if (tick == 25) {
                              for (Entity passenger : entity.getPassengers()) {
                                 entity.doHurtTarget(passenger, 2.5F);
                              }

                              entity.level().playSound(null, entity, (SoundEvent)TensuraSoundEvents.EATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                           } else if (tick == 35) {
                              for (Entity passenger : entity.getPassengers()) {
                                 entity.doHurtTarget(passenger, 3.0F);
                              }

                              entity.level().playSound(null, entity, (SoundEvent)TensuraSoundEvents.EATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                           } else if (tick == 45) {
                              float heal = entity.getHealth();

                              for (Entity passenger : entity.getPassengers()) {
                                 entity.doHurtTarget(passenger, 3.5F);
                                 entity.stopRiding();
                              }

                              if (target.isAlive()) {
                                 entity.heal(75.0F);
                              } else {
                                 entity.heal(heal * 3.0F);
                              }

                              entity.level().playSound(null, entity, (SoundEvent)TensuraSoundEvents.EATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                           }

                           return tick < 50;
                        })
                        .whenStarting(entity -> {
                           entity.triggerAnim("miscController2", "eat");
                           entity.stopMovingTick = 50;
                        })
                        .startCondition(
                           entity -> entity.getRandom().nextFloat() < 0.2 && entity.getHealth() < entity.getMaxHealth() / 2.0F && !entity.isPassenger()
                        ),
                     new CustomHeldAttack().minAttackRadius(0.0F).maxAttackRadius(5.0F).attackInterval(entity -> 40).onTick((entity, target, tick) -> {
                        if (tick >= 10 && entity.getFirstPassenger() == null) {
                           target.startRiding(entity, true);
                        } else if (tick >= 50) {
                           for (Entity passenger : entity.getPassengers()) {
                              entity.doHurtTarget(passenger, 3.0F);
                              entity.stopRiding();
                           }

                           entity.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 1.0F);
                        }

                        return tick < 65;
                     }).whenStarting(entity -> {
                        entity.triggerAnim("miscController2", "crush");
                        entity.stopMovingTick = 25;
                     }).startCondition(entity -> entity.getRandom().nextFloat() < 0.2 && !entity.isPassenger()),
                     new CustomRangeAttack(10)
                        .maxAttackRadius(8.0F)
                        .attackInterval(entity -> 40)
                        .performAttack((entity, target) -> entity.areaAttack(2.0F, 1.25F))
                        .whenStarting(entity -> {
                           entity.triggerAnim("miscController", "slam");
                           entity.stopMovingTick = 25;
                        })
                        .startCondition(entity -> entity.getRandom().nextFloat() < 0.5 && !entity.getMainHandItem().isEmpty()),
                     new CustomRangeAttack(1)
                        .maxAttackRadius(40.0F)
                        .minAttackRadius(5.0F)
                        .performAttack(
                           (entity, target) -> {
                              int i = entity.level()
                                 .getNearbyEntities(
                                    OrcEntity.class,
                                    TargetingConditions.forNonCombat().range(32.0).ignoreLineOfSight().ignoreInvisibilityTesting(),
                                    entity,
                                    entity.getBoundingBox().inflate(32.0)
                                 )
                                 .size();
                              entity.shouldSummonOrcs = entity.random.nextInt(15) + 1 > i;
                           }
                        )
                        .startCondition(entity -> !entity.isTame() && entity.getRandom().nextFloat() <= 0.2),
                     new AnimatableMeleeAttack(10).attackInterval(entity -> 0).whenStarting(entity -> {
                        entity.triggerAnim("miscController", entity.getMainHandItem().isEmpty() ? "punch" : "swing");
                        entity.stopMovingTick = 20;
                     })
                  }
               )
               .startCondition(entity -> entity.stopMovingTick <= 0)
         }
      );
   }

   @Override
   protected PlayState loopController(AnimationState<OrcEntity> state) {
      String name;
      if (!this.isAlive() && this.getVariant().equals(OrcVariant.ROYAL_LORD)) {
         name = "animation.orc_disaster.cry_aura";
      } else if (state.isMoving()) {
         if (this.isSprinting()) {
            name = "animation.orc_disaster.run";
         } else {
            name = "animation.orc_disaster.walk";
         }
      } else {
         name = "animation.orc_disaster.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   @Override
   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("punch", RawAnimation.begin().then("animation.orc_disaster.punch", LoopType.PLAY_ONCE))
               .triggerableAnim("swing", RawAnimation.begin().then("animation.orc_disaster.cleaver_swing", LoopType.PLAY_ONCE))
               .triggerableAnim("slam", RawAnimation.begin().then("animation.orc_disaster.slam", LoopType.PLAY_ONCE))
               .triggerableAnim("yell", RawAnimation.begin().then("animation.orc_disaster.yell", LoopType.PLAY_ONCE)),
            new AnimationController(this, "miscController2", 0, event -> PlayState.STOP)
               .triggerableAnim("crush", RawAnimation.begin().then("animation.orc_disaster.crush", LoopType.PLAY_ONCE))
               .triggerableAnim("eat", RawAnimation.begin().then("animation.orc_disaster.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("recover", RawAnimation.begin().then("animation.orc_disaster.recover", LoopType.PLAY_ONCE))
               .triggerableAnim("death", RawAnimation.begin().then("animation.orc_disaster.cry", LoopType.PLAY_ONCE))
         }
      );
   }
}
