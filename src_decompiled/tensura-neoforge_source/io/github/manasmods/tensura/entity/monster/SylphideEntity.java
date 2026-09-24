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
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.OrbitAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.beam.ElectroBlastProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.LightningSphereProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WindBladeProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WindSphereProjectile;
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
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SylphideEntity extends GreaterSpiritEntity implements GeoEntity, SmartBrainOwner<SylphideEntity> {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean shouldSummonSerpents = false;
   private boolean training = false;

   public SylphideEntity(EntityType<? extends SylphideEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossBarColor.GREEN, BossBarOverlay.NOTCHED_20).setPlayBossMusic(true);
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

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || super.isInvulnerableTo(source);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (!(pSource.getEntity() instanceof FeatheredSerpentEntity) && !(pSource.getEntity() instanceof SylphideEntity)) {
         boolean hurt = super.hurt(pSource, pAmount);
         if (hurt && pSource.getEntity() instanceof LivingEntity damageSource && !pSource.isCreativePlayer()) {
            if (!damageSource.isAlive() || this.isAlliedTo(damageSource)) {
               return true;
            }

            List<FeatheredSerpentEntity> list = this.level()
               .getEntitiesOfClass(FeatheredSerpentEntity.class, this.getBoundingBox().inflate(32.0), entity -> !entity.isTame());
            if (!list.isEmpty()) {
               list.forEach(serpent -> serpent.setTarget(damageSource));
            }
         }

         return hurt;
      } else {
         return false;
      }
   }

   @Override
   protected float getDamageReductionMultiplier(DamageSource source) {
      return TensuraDamageHelper.isLightningDamage(source) ? 0.05F : super.getDamageReductionMultiplier(source);
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof FeatheredSerpentEntity serpent) {
         return serpent.isTame() == this.isTame();
      } else {
         return entity instanceof SylphideEntity sylphide ? sylphide.isTame() == this.isTame() : false;
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide() && this.isAlive()) {
         if (this.tickCount % 10 == 0) {
            TensuraParticleHelper.addParticlesAroundSelf(this, TensuraParticleUtils.getGust(), 2.0);
         }

         if (this.tickCount % 200 == 0 && this.onGround() && !this.isSleeping()) {
            this.training = !this.training && this.getRandom().nextFloat() <= 0.25F;
         }
      }
   }

   public void shootWindSphere(LivingEntity target) {
      WindSphereProjectile sphere = new WindSphereProjectile(this.level(), this);
      sphere.setSize(0.75F);
      sphere.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.WIND_MANIPULATION.get()));
      float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
      double xOffset = Mth.sin((float)(Math.PI + angle));
      double zOffset = Mth.cos(angle);
      sphere.moveTo(this.getX() + xOffset, this.getEyeY() - 0.2, this.getZ() + zOffset, this.getYRot(), this.getXRot());
      sphere.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
      sphere.setSpeed(1.5F);
      sphere.setNoGravity(true);
      sphere.shootToward(target, 1.0F, 0.0F);
      this.level().addFreshEntity(sphere);
      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 7.0F, 0.95F + this.random.nextFloat() * 0.1F);
      this.setMagicID(0);
   }

   public void shootWindBlade(LivingEntity target) {
      WindBladeProjectile blade = new WindBladeProjectile(this.level(), this);
      blade.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)SpiritualMagics.WIND_BLADE.get()));
      float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
      double xOffset = Mth.sin((float)(Math.PI + angle));
      double zOffset = Mth.cos(angle);
      blade.moveTo(this.getX() + xOffset, this.getEyeY() - 0.2, this.getZ() + zOffset, this.getYRot(), this.getXRot());
      blade.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F);
      blade.setSpeed(2.0F);
      blade.setNoGravity(true);
      blade.shootToward(target, 1.5F, 0.0F);
      this.level().addFreshEntity(blade);
      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 7.0F, 0.95F + this.random.nextFloat() * 0.1F);
      this.setMagicID(0);
   }

   private void lightningBall() {
      int ballId = this.getMagicID();
      if (ballId == 0) {
         LightningSphereProjectile sphere = new LightningSphereProjectile(this.level(), this);
         sphere.setSize(0.1F);
         sphere.setDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0));
         sphere.setMpCost(500.0);
         sphere.setSkill((ManasSkillInstance)SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)ExtraSkills.WIND_MANIPULATION.get()).orElse(null));
         sphere.setExplosionRadius(4.0F);
         sphere.setPos(this.getEyePosition().add(0.0, 4.0, 0.0));
         sphere.setOwnerOffset(new Vec3(0.0, 3.0, 0.0));
         sphere.setLookDistance(30.0F);
         sphere.setDelayTick(30);
         sphere.setDelaySizeChange(0.1F);
         sphere.setNoGravity(true);
         sphere.setMobEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 200, 1, true, false, true));
         sphere.setEffectRange(3.0F);
         this.level().addFreshEntity(sphere);
         this.setMagicID(sphere.getId());
      } else if (!(this.level().getEntity(ballId) instanceof LightningSphereProjectile ball)) {
         this.setMagicID(0);
         this.lightningBall();
      }

      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 0.95F + this.random.nextFloat() * 0.1F);
   }

   private void electroBlast(LivingEntity target) {
      this.getLookControl().setLookAt(target.getX(), target.getY() + target.getBbHeight() / 2.0F, target.getZ());
      ElectroBlastProjectile beam = new ElectroBlastProjectile(this.level(), this);
      beam.setFollowingOwner(true);
      beam.setSize(1.0F);
      beam.setLife(25);
      float damage = (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0);
      beam.setDamage(damage);
      beam.setExplosionRadius(2.0F);
      beam.setRange(20.0F);
      beam.setPos(this.getEyePosition());
      beam.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)SpiritualMagics.ELECTRO_BLAST.get()));
      beam.setMpCost(2000.0);
      this.level().addFreshEntity(beam);
      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 0.95F + this.random.nextFloat() * 0.1F);
      this.setMagicID(0);
   }

   public void aerialBlade() {
      Level level = this.level();
      level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 0.95F + this.random.nextFloat() * 0.1F);
      int radius = 4;
      BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, this, Fluid.NONE, radius + 2);
      BlockPos ahead = result.getBlockPos();
      Vec3 aheadVec = new Vec3(ahead.getX(), ahead.getY(), ahead.getZ());
      TensuraParticleHelper.addServerParticlesAroundPos(this.random, level, aheadVec, ParticleTypes.GUST, 3.0);
      TensuraParticleHelper.addServerParticlesAroundPos(this.random, level, aheadVec, ParticleTypes.SMALL_GUST, 4.0);
      TensuraParticleHelper.addServerParticlesAroundPos(this.random, level, aheadVec, ParticleTypes.GUST_EMITTER_SMALL, 5.0);
      TensuraParticleHelper.addServerParticlesAroundPos(this.random, level, aheadVec, ParticleTypes.SWEEP_ATTACK, 4.0);
      TensuraParticleHelper.addServerParticlesAroundPos(this.random, level, aheadVec, ParticleTypes.SWEEP_ATTACK, 5.0);
      AABB box = this.getBoundingBox().inflate(radius);

      for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, living -> !living.is(this) && living.isAlive() && !living.isAlliedTo(this))) {
         target.setDeltaMovement(aheadVec.subtract(target.position()).normalize().scale(2.0));
         target.hurtMarked = true;
      }

      AABB aabb = new AABB(ahead).inflate(radius);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(LivingEntity.class, aabb.minmax(aabb), living -> !living.is(this) && living.isAlive() && !living.isAlliedTo(this));
      if (!list.isEmpty()) {
         float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
         DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.WIND_ELEMENTAL, this);

         for (LivingEntity target : list) {
            if (!(target instanceof Player player && player.getAbilities().invulnerable)) {
               target.hurt(source, damage);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, TensuraParticleUtils.getGust(), 3.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.GUST, 3.0);
            }
         }
      }
   }

   public void combust(Predicate<LivingEntity> predicate) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.CLOUD, this.getX(), this.getEyeY(), this.getZ(), 10, 0.08, 0.08, 0.08, 0.2, true);
      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.LIGHTNING_EFFECT.get(), this.getX(), this.getEyeY(), this.getZ(), 10, 0.08, 0.08, 0.08, 0.2, true
      );
      TensuraParticleHelper.addServerParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.LIGHTNING_SPARK.get(), 2.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, TensuraParticleUtils.getGust(), 3.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, TensuraParticleUtils.getGust(3.0F), 4.0);
      AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 10.0);
      List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb, predicate);
      if (!list.isEmpty()) {
         float damage = (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5);
         DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.WIND_ELEMENTAL, this);

         for (LivingEntity target : list) {
            target.hurt(source, damage);
            target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 200, 2, true, false, true));
            target.setDeltaMovement(0.0, 0.1, 0.0);
            SkillHelper.knockBack(this, target, 2.0F);
         }
      }
   }

   private void summonFeatheredSerpent(int minRadius, int maxRadius) {
      if (this.level() instanceof ServerLevel serverLevel) {
         int var13 = Mth.floor(this.getX());
         int j = Mth.floor(this.getY());
         int k = Mth.floor(this.getZ());
         FeatheredSerpentEntity serpent = new FeatheredSerpentEntity(
            (EntityType<? extends FeatheredSerpentEntity>)MonsterEntityTypes.FEATHERED_SERPENT.get(), serverLevel
         );

         for (int l = 0; l < 50; l++) {
            int i1 = var13 + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int j1 = j + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int k1 = k + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            serpent.setPos(i1, j1, k1);
            if (serverLevel.isUnobstructed(serpent) && serverLevel.noCollision(serpent)) {
               serpent.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(serpent.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
               serpent.setTarget(this.getTarget());
               IExistence existence = TensuraStorages.getExistenceFrom(serpent);
               existence.setSummonedSecond(300);
               existence.setSummoner(this.getUUID());
               existence.markDirty();
               serverLevel.addFreshEntityWithPassengers(serpent);
               TensuraParticleHelper.addServerParticlesAroundSelf(serpent, (ParticleOptions)TensuraParticleTypes.LIGHTNING_SPARK.get(), 3.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(serpent, ParticleTypes.GUST_EMITTER_LARGE, 2.0);
               break;
            }
         }
      }
   }

   @Override
   public Element getElemental() {
      return Element.WIND;
   }

   @Override
   public Item getElementalCore() {
      return (Item)TensuraMaterialItems.ELEMENT_CORE_WIND.get();
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
      return (SoundEvent)TensuraSoundEvents.SPIRIT_WIND_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_WIND_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_WIND_DEATH.get();
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

   public List<ExtendedSensor<SylphideEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<SylphideEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<SylphideEntity> getIdleTasks() {
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

   public BrainActivityGroup<SylphideEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new OrbitAttack()
               .requireInSight(false)
               .lookAtTargetWhileOrbiting(true)
               .orbitAttackInterval(entity -> 120 + entity.getRandom().nextInt(80))
               .orbitRadius((entity, target) -> 8.0)
               .orbitHeight((entity, target) -> 5.0)
               .canDoOrbitalAttack((entity, target) -> {
                  if (entity.isTame()) {
                     return true;
                  }

                  List<FeatheredSerpentEntity> serpents = entity.level()
                     .getEntitiesOfClass(FeatheredSerpentEntity.class, entity.getBoundingBox().inflate(20.0));
                  if (!serpents.isEmpty()) {
                     for (FeatheredSerpentEntity serpent : serpents) {
                        serpent.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), 200, 1, false, false, false));
                     }
                  } else {
                     entity.shouldSummonSerpents = entity.getRandom().nextFloat() <= 0.25;
                  }

                  return false;
               })
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
                                 entity.summonFeatheredSerpent(3, 7);
                                 break;
                              case 20:
                                 entity.summonFeatheredSerpent(5, 10);
                                 break;
                              case 30:
                                 entity.summonFeatheredSerpent(7, 15);
                                 break;
                              case 35:
                                 entity.shouldSummonSerpents = false;
                           }

                           entity.level()
                              .playSound(
                                 null,
                                 entity,
                                 (SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 10.0F,
                                 0.95F + entity.getRandom().nextFloat() * 0.1F
                              );
                           return tick < 40;
                        }
                     )
                     .whenStopping(entity -> entity.shouldSummonSerpents = false)
                     .startCondition(entity -> entity.getMagicID() == 0 && entity.shouldSummonSerpents)
                     .whenStarting(entity -> entity.triggerAnim("heldController", "summon")),
                  new CustomRangeAttack(15)
                     .maxAttackRadius(7.0F)
                     .performAttack((entity, target) -> {
                        entity.combust(this::shouldAttack);
                        entity.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 0.95F + entity.getRandom().nextFloat() * 0.1F);
                     })
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.2 && entity.getMagicID() == 0)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "burst")),
                  new CustomHeldAttack()
                     .minAttackRadius(0.0F)
                     .maxAttackRadius(12.0F)
                     .attackInterval(entity -> 60)
                     .onTick(
                        (entity, target, tick) -> {
                           if (tick == 1) {
                              MagicCircle.castMagicCircle(
                                 5.0F,
                                 70,
                                 MagicCircleVariant.WIND,
                                 entity,
                                 0.0F,
                                 Vec3.ZERO,
                                 (ManasSkill)SpiritualMagics.AERIAL_BLADE.get(),
                                 0,
                                 Pair.of(0.0, 15000.0)
                              );
                              entity.setMagicID(1);
                           } else if (tick >= 30 && tick % 10 == 0) {
                              entity.aerialBlade();
                           }

                           return tick < 70;
                        }
                     )
                     .whenStopping(entity -> entity.setMagicID(0))
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.2 && entity.getMagicID() == 0)
                     .whenStarting(entity -> entity.triggerAnim("heldController", "aerial_blade")),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(20.0F)
                     .attackInterval(entity -> 60)
                     .performAttack(SylphideEntity::electroBlast)
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.3 && entity.getMagicID() == 0)
                     .whenStarting(
                        entity -> {
                           Vec3 circleVec3 = new Vec3(0.0, entity.getBbHeight() * 3.0F / 4.0F - entity.getEyeHeight(), 0.0);
                           MagicCircle.castMagicCircle(
                              1.0F,
                              40,
                              MagicCircleVariant.WIND,
                              entity,
                              0.5F,
                              circleVec3,
                              (ManasSkill)SpiritualMagics.ELECTRO_BLAST.get(),
                              0,
                              Pair.of(0.0, 5000.0)
                           );
                           entity.triggerAnim("miscController", "electro_blast");
                           entity.setMagicID(1);
                        }
                     )
                     .whenStopping(entity -> entity.setMagicID(0)),
                  new CustomHeldAttack().minAttackRadius(0.0F).maxAttackRadius(40.0F).attackInterval(entity -> 60).onTick((entity, target, tick) -> {
                     if (tick >= 20 && tick <= 50) {
                        if (tick == 20) {
                           entity.setMagicID(0);
                        }

                        entity.lightningBall();
                     }

                     return tick < 60;
                  }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.3 && entity.getMagicID() == 0).whenStarting(entity -> {
                     this.setMagicID(1);
                     entity.triggerAnim("heldController", "lightning_sphere");
                  }).whenStopping(entity -> entity.setMagicID(0)),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(40.0F)
                     .performAttack(SylphideEntity::shootWindSphere)
                     .startCondition(entity -> entity.getMagicID() == 0 && entity.getRandom().nextFloat() <= 0.5)
                     .whenStarting(entity -> {
                        entity.setMagicID(1);
                        entity.triggerAnim("miscController", entity.getRandom().nextBoolean() ? "wind_sphere_right" : "wind_sphere_left");
                     })
                     .whenStopping(entity -> entity.setMagicID(0)),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(40.0F)
                     .performAttack(SylphideEntity::shootWindBlade)
                     .startCondition(entity -> entity.getMagicID() == 0)
                     .whenStarting(entity -> {
                        entity.setMagicID(1);
                        entity.triggerAnim("miscController", entity.getRandom().nextBoolean() ? "wind_blade_right" : "wind_blade_left");
                     })
                     .whenStopping(entity -> entity.setMagicID(0))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<SylphideEntity> state) {
      String name;
      if (this.isNoAi() || this.isDeadOrDying()) {
         name = "animation.sylphide.idle";
      } else if (this.isSleeping()) {
         name = "animation.sylphide.relax";
      } else if (this.isInSittingPose()) {
         if (this.training) {
            name = "animation.sylphide.idle_train";
         } else {
            name = "animation.sylphide.stay_by";
         }
      } else if (state.isMoving()) {
         if (!this.isAngry() && !this.isSprinting()) {
            name = "animation.sylphide.fly";
         } else {
            name = "animation.sylphide.fly_fast";
         }
      } else {
         name = "animation.sylphide.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("wind_sphere_right", RawAnimation.begin().then("animation.sylphide.wind_sphere_right", LoopType.PLAY_ONCE))
               .triggerableAnim("wind_sphere_left", RawAnimation.begin().then("animation.sylphide.wind_sphere_left", LoopType.PLAY_ONCE))
               .triggerableAnim("wind_blade_right", RawAnimation.begin().then("animation.sylphide.wind_sphere_right", LoopType.PLAY_ONCE))
               .triggerableAnim("wind_blade_left", RawAnimation.begin().then("animation.sylphide.wind_sphere_left", LoopType.PLAY_ONCE))
               .triggerableAnim("electro_blast", RawAnimation.begin().then("animation.sylphide.electro_blast", LoopType.PLAY_ONCE))
               .triggerableAnim("burst", RawAnimation.begin().then("animation.sylphide.aerial_blade_burst", LoopType.PLAY_ONCE)),
            new AnimationController(this, "heldController", 3, event -> PlayState.STOP)
               .triggerableAnim("lightning_sphere", RawAnimation.begin().then("animation.sylphide.lightning_sphere", LoopType.PLAY_ONCE))
               .triggerableAnim("aerial_blade", RawAnimation.begin().then("animation.sylphide.aerial_blade", LoopType.PLAY_ONCE))
               .triggerableAnim("summon", RawAnimation.begin().then("animation.sylphide.summon", LoopType.PLAY_ONCE))
               .triggerableAnim("death", RawAnimation.begin().then("animation.sylphide.death", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
