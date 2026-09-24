package io.github.manasmods.tensura.entity.monster;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.OrbitAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.AcidRainEntity;
import io.github.manasmods.tensura.entity.projectile.magic.PoisonBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WaterBladeProjectile;
import io.github.manasmods.tensura.entity.template.GreaterSpiritEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class UndineEntity extends GreaterSpiritEntity implements GeoEntity, SmartBrainOwner<UndineEntity> {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean shouldSummonFrogs = false;
   private boolean training = false;

   public UndineEntity(EntityType<? extends UndineEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossBarColor.BLUE, BossBarOverlay.NOTCHED_20).setPlayBossMusic(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.ATTACK_DAMAGE, 40.0)
         .add(Attributes.MAX_HEALTH, 400.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.STEP_HEIGHT, 2.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 3.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 20.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 3.0);
   }

   public boolean isOnFire() {
      return TensuraStorages.getEffectFrom(this).isOnBlackFlame();
   }

   public boolean shouldDistinguishProjectile(@Nullable Entity entity) {
      return entity == null ? false : entity.getType().is(TensuraEntityTags.CAN_DISTINGUISH);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || super.isInvulnerableTo(source);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (pSource.getEntity() instanceof AquaFrogEntity || pSource.getEntity() instanceof UndineEntity) {
         return false;
      }

      if (this.shouldDistinguishProjectile(pSource.getDirectEntity())) {
         this.playSound(SoundEvents.LAVA_EXTINGUISH, 10.0F, 0.8F);
         pSource.getDirectEntity().remove(RemovalReason.KILLED);
         return false;
      }

      boolean hurt = super.hurt(pSource, pAmount);
      if (hurt && pSource.getEntity() instanceof LivingEntity damageSource && !pSource.isCreativePlayer()) {
         if (!damageSource.isAlive() || this.isAlliedTo(damageSource)) {
            return true;
         }

         List<AquaFrogEntity> list = this.level().getEntitiesOfClass(AquaFrogEntity.class, this.getBoundingBox().inflate(32.0), entity -> !entity.isTame());
         if (!list.isEmpty()) {
            list.forEach(frog -> frog.setTarget(damageSource));
         }
      }

      return hurt;
   }

   @Override
   protected float getDamageReductionMultiplier(DamageSource source) {
      return TensuraDamageHelper.isCold(source) ? 0.05F : super.getDamageReductionMultiplier(source);
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof AquaFrogEntity frog) {
         return frog.isTame() == this.isTame();
      } else {
         return entity instanceof UndineEntity undine ? undine.isTame() == this.isTame() : false;
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide() && this.isAlive()) {
         if (this.tickCount % 10 == 0) {
            TensuraParticleHelper.addParticlesAroundSelf(this, TensuraParticleUtils.getWaterEffect(), 2.0);
         }

         if (this.tickCount % 200 == 0 && this.onGround() && !this.isSleeping()) {
            this.training = !this.training && this.getRandom().nextFloat() <= 0.25F;
         }
      }
   }

   public void shootWaterBlade(LivingEntity target) {
      WaterBladeProjectile blade = new WaterBladeProjectile(this.level(), this);
      blade.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)SpiritualMagics.WATER_CUTTER.get()));
      float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
      double xOffset = Mth.sin((float)(Math.PI + angle));
      double zOffset = Mth.cos(angle);
      blade.moveTo(this.getX() + xOffset, this.getEyeY() - 0.2, this.getZ() + zOffset, this.getYRot(), this.getXRot());
      blade.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
      blade.setSpeed(2.0F);
      blade.shootToward(target, 1.0F, 0.0F);
      blade.setNoGravity(true);
      this.level().addFreshEntity(blade);
      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 10.0F, 0.95F + this.random.nextFloat() * 0.1F);
      this.setMagicID(0);
   }

   private void poisonBall() {
      int ballId = this.getMagicID();
      if (ballId == 0) {
         PoisonBallProjectile ball = new PoisonBallProjectile(this.level(), this);
         ball.setDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0));
         ball.setMpCost(500.0);
         ball.setSkill((ManasSkillInstance)SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)ExtraSkills.WATER_MANIPULATION.get()).orElse(null));
         ball.setPos(this.getEyePosition().add(0.0, 4.0, 0.0));
         ball.setOwnerOffset(new Vec3(0.0, 3.0, 0.0));
         ball.setLookDistance(30.0F);
         ball.setDelayTick(15);
         ball.setDelaySizeChange(0.3F);
         ball.setNoGravity(true);
         ball.setMobEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 200, 1, true, false, true));
         ball.setEffectRange(3.0F);
         this.level().addFreshEntity(ball);
         this.setMagicID(ball.getId());
      } else if (!(this.level().getEntity(ballId) instanceof PoisonBallProjectile ball)) {
         this.setMagicID(0);
         this.poisonBall();
      }

      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 10.0F, 0.95F + this.random.nextFloat() * 0.1F);
   }

   public void tailSlam() {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), TensuraParticleUtils.getWaterEffect(), this.position().x(), this.position().y(), this.position().z(), 55, 0.08, 0.08, 0.08, 0.15, true
      );
      TensuraParticleHelper.spawnServerParticles(
         this.level(),
         (ParticleOptions)TensuraParticleTypes.SNOWFLAKE.get(),
         this.position().x(),
         this.position().y(),
         this.position().z(),
         55,
         0.08,
         0.08,
         0.08,
         0.15,
         true
      );
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_SPLASH.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GLASS_BREAK, TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
      this.setMagicID(0);
      AABB aabb = this.getBoundingBox().inflate(5.0);
      List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb, this::shouldAttack);
      if (!list.isEmpty()) {
         double damageMultiplier = 1.5;
         DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.WATER_ELEMENTAL, this);

         for (LivingEntity target : list) {
            target.hurt(source, (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier));
            target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CHILL), 200, 1, true, false, true));
            SkillHelper.knockBack(this, target, 1.0F);
         }
      }
   }

   private void acidRain(LivingEntity target) {
      AcidRainEntity rain = new AcidRainEntity(this.level(), this);
      rain.setPos(target.position().add(0.0, 10.0, 0.0));
      rain.setTarget(target);
      rain.setFollowOwner(true);
      rain.setDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5));
      rain.setMpCost(1000.0);
      rain.setSkill((ManasSkillInstance)SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)SpiritualMagics.ACID_RAIN.get()).orElse(null));
      if (this.getTarget() != null) {
         rain.setTarget(this.getTarget());
      }

      rain.setLife(200);
      rain.setSize(5.0F);
      this.level().addFreshEntity(rain);
      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 0.95F + this.random.nextFloat() * 0.1F);
      this.setMagicID(0);
   }

   public void combust(Predicate<LivingEntity> predicate) {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), TensuraParticleUtils.getWaterEffect(), this.getX(), this.getEyeY(), this.getZ(), 55, 0.08, 0.08, 0.08, 0.2, true
      );
      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.SNOWFLAKE.get(), this.getX(), this.getEyeY(), this.getZ(), 55, 0.08, 0.08, 0.08, 0.2, true
      );
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.BUBBLE, 2.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.BUBBLE, 3.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.BUBBLE, 4.0);
      AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 10.0);
      List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb, predicate);
      if (!list.isEmpty()) {
         DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.WATER_ELEMENTAL, this);

         for (LivingEntity target : list) {
            target.hurt(source, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0F);
            target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CHILL), 200, 2, true, false, true));
            target.setDeltaMovement(0.0, 0.1, 0.0);
            SkillHelper.knockBack(this, target, 2.0F);
         }
      }
   }

   private void summonAquaFrog(int minRadius, int maxRadius) {
      if (this.level() instanceof ServerLevel serverLevel) {
         int var13 = Mth.floor(this.getX());
         int j = Mth.floor(this.getY());
         int k = Mth.floor(this.getZ());
         AquaFrogEntity frog = new AquaFrogEntity((EntityType<? extends AquaFrogEntity>)MonsterEntityTypes.AQUA_FROG.get(), serverLevel);

         for (int l = 0; l < 50; l++) {
            int i1 = var13 + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int j1 = j + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int k1 = k + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            frog.setPos(i1, j1, k1);
            if (serverLevel.isUnobstructed(frog) && serverLevel.noCollision(frog)) {
               frog.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(frog.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
               frog.setTarget(this.getTarget());
               IExistence existence = TensuraStorages.getExistenceFrom(frog);
               existence.setSummonedSecond(300);
               existence.setSummoner(this.getUUID());
               existence.markDirty();
               serverLevel.addFreshEntityWithPassengers(frog);
               TensuraParticleHelper.addServerParticlesAroundSelf(frog, TensuraParticleUtils.getWaterEffect(), 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(frog, ParticleTypes.BUBBLE_COLUMN_UP, 2.0);
               break;
            }
         }
      }
   }

   @Override
   public Element getElemental() {
      return Element.WATER;
   }

   @Override
   public Item getElementalCore() {
      return (Item)TensuraMaterialItems.ELEMENT_CORE_WATER.get();
   }

   @Override
   public void die(DamageSource source) {
      super.die(source);
      if (!this.isAlive()) {
         this.triggerAnim("heldController", "death");
      }
   }

   protected void tickDeath() {
      if (++this.deathTime >= 36) {
         this.remove(RemovalReason.KILLED);
         this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 1.0F);
         this.spawnDeathParticles();
      }
   }

   @Override
   protected void spawnDeathParticles() {
      TensuraParticleHelper.addServerParticlesAroundSelf(this, TensuraParticleUtils.getWaterEffect());
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.CLOUD);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, TensuraParticleUtils.getWaterEffect(), 2.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.CLOUD, 2.0);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_WATER_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_WATER_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_WATER_DEATH.get();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<UndineEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<UndineEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<UndineEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldTarget).whenStarting(entity -> entity.setMagicID(0)),
                  new SubordinateFollowOwner().canTeleportOffGroundWhen(entity -> {
                     entity.setFlying(true);
                     return true;
                  }),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomFlyAndWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<UndineEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new OrbitAttack()
               .requireInSight(false)
               .lookAtTargetWhileOrbiting(true)
               .attackSpeedMod((entity, target) -> 3.0F)
               .orbitAttackInterval(entity -> 160 + entity.getRandom().nextInt(80))
               .orbitRadius((entity, target) -> 8.0)
               .orbitHeight((entity, target) -> 6.0)
               .canDoOrbitalAttack((entity, target) -> {
                  if (entity.isTame()) {
                     return true;
                  }

                  List<AquaFrogEntity> frogs = entity.level().getEntitiesOfClass(AquaFrogEntity.class, entity.getBoundingBox().inflate(20.0));
                  if (!frogs.isEmpty()) {
                     for (AquaFrogEntity frog : frogs) {
                        frog.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), 200, 1, false, false, false));
                     }
                  } else {
                     entity.shouldSummonFrogs = entity.getRandom().nextFloat() <= 0.25;
                  }

                  return !entity.shouldSummonFrogs;
               })
               .onStartOrbitAttack((entity, target) -> {
                  entity.triggerAnim("heldController", "tail_slam");
                  entity.setMagicID(1);
               })
               .shouldDoMeleeAttack((entity, target) -> entity.distanceTo(target) <= 5.0F)
               .performOrbitAttack((entity, target) -> entity.tailSlam())
               .onTick(entity -> {
                  entity.setFlying(true);
                  return true;
               }),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomHeldAttack()
                     .maxAttackRadius(40.0F)
                     .attackInterval(entity -> 0)
                     .onTick(
                        (entity, target, tick) -> {
                           switch (tick) {
                              case 10:
                                 entity.summonAquaFrog(3, 7);
                                 break;
                              case 20:
                                 entity.summonAquaFrog(5, 10);
                                 break;
                              case 30:
                                 entity.summonAquaFrog(7, 15);
                                 break;
                              case 35:
                                 entity.shouldSummonFrogs = false;
                           }

                           entity.level()
                              .playSound(
                                 null,
                                 entity,
                                 (SoundEvent)TensuraSoundEvents.CAST_WATER.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 10.0F,
                                 0.95F + entity.getRandom().nextFloat() * 0.1F
                              );
                           return tick < 40;
                        }
                     )
                     .whenStopping(entity -> entity.shouldSummonFrogs = false)
                     .startCondition(entity -> entity.getMagicID() == 0 && entity.shouldSummonFrogs)
                     .whenStarting(entity -> entity.triggerAnim("heldController", "summon")),
                  new CustomRangeAttack(15)
                     .maxAttackRadius(7.0F)
                     .performAttack((entity, target) -> {
                        entity.combust(this::shouldAttack);
                        entity.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 0.95F + entity.getRandom().nextFloat() * 0.1F);
                     })
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.2 && entity.getMagicID() == 0)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "burst")),
                  new CustomRangeAttack(25)
                     .maxAttackRadius(20.0F)
                     .attackInterval(entity -> 60)
                     .performAttack(UndineEntity::acidRain)
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.3 && entity.getMagicID() == 0)
                     .whenStarting(
                        entity -> {
                           MagicCircle.castMagicCircle(
                              5.0F,
                              40,
                              BrainUtils.getTargetOfEntity(entity).position().add(0.0, 10.0, 0.0),
                              MagicCircleVariant.WATER,
                              entity,
                              (ManasSkill)SpiritualMagics.ACID_RAIN.get(),
                              0,
                              Pair.of(0.0, 1000.0)
                           );
                           entity.triggerAnim("miscController", "acid_rain");
                           entity.setMagicID(1);
                        }
                     )
                     .whenStopping(entity -> entity.setMagicID(0)),
                  new CustomHeldAttack().minAttackRadius(0.0F).maxAttackRadius(40.0F).attackInterval(entity -> 60).onTick((entity, target, tick) -> {
                     if (tick >= 10 && tick <= 25) {
                        if (tick == 10) {
                           entity.setMagicID(0);
                        }

                        entity.poisonBall();
                     }

                     return tick < 40;
                  }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.3 && entity.getMagicID() == 0).whenStarting(entity -> {
                     entity.setMagicID(1);
                     entity.triggerAnim("heldController", "water_ball_massive");
                  }).whenStopping(entity -> entity.setMagicID(0)),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(40.0F)
                     .performAttack(UndineEntity::shootWaterBlade)
                     .startCondition(entity -> entity.getMagicID() == 0)
                     .whenStarting(entity -> {
                        entity.setMagicID(1);
                        entity.triggerAnim("miscController", entity.getRandom().nextBoolean() ? "water_ball_right" : "water_ball_left");
                     })
                     .whenStopping(entity -> entity.setMagicID(0))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<UndineEntity> state) {
      String name;
      if (this.isNoAi() || this.isDeadOrDying()) {
         name = "animation.undine.idle";
      } else if (this.isSleeping()) {
         name = "animation.undine.relax";
      } else if (this.isInSittingPose() && this.training) {
         name = "animation.undine.idle_train";
      } else if (state.isMoving()) {
         if (this.onGround()) {
            name = "animation.undine.walk";
         } else if (!this.isAngry() && !this.isSprinting()) {
            name = "animation.undine.swim_swift";
         } else {
            name = "animation.undine.swim_slow";
         }
      } else if (this.onGround()) {
         name = "animation.undine.idle";
      } else {
         name = "animation.undine.idle_swim";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("water_ball_right", RawAnimation.begin().then("animation.undine.water_ball_right", LoopType.PLAY_ONCE))
               .triggerableAnim("water_ball_left", RawAnimation.begin().then("animation.undine.water_ball_left", LoopType.PLAY_ONCE))
               .triggerableAnim("acid_rain", RawAnimation.begin().then("animation.undine.acid_rain", LoopType.PLAY_ONCE))
               .triggerableAnim("burst", RawAnimation.begin().then("animation.undine.burst", LoopType.PLAY_ONCE)),
            new AnimationController(this, "heldController", 3, event -> PlayState.STOP)
               .triggerableAnim("water_ball_massive", RawAnimation.begin().then("animation.undine.water_ball_massive", LoopType.PLAY_ONCE))
               .triggerableAnim("tail_slam", RawAnimation.begin().then("animation.undine.tail_slam", LoopType.PLAY_ONCE))
               .triggerableAnim("summon", RawAnimation.begin().then("animation.undine.summon", LoopType.PLAY_ONCE))
               .triggerableAnim("death", RawAnimation.begin().then("animation.undine.death", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
