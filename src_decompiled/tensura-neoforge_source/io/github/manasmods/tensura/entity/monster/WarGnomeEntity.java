package io.github.manasmods.tensura.entity.monster;

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
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.magic.field.GravityField;
import io.github.manasmods.tensura.entity.magic.spike.DripstoneSpikeEntity;
import io.github.manasmods.tensura.entity.magic.spike.MagicSpikeEntity;
import io.github.manasmods.tensura.entity.projectile.magic.GravitySphereProjectile;
import io.github.manasmods.tensura.entity.template.GreaterSpiritEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
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

public class WarGnomeEntity extends GreaterSpiritEntity implements GeoEntity, SmartBrainOwner<WarGnomeEntity> {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean shouldSummonGnomes = false;
   private boolean training = false;

   public WarGnomeEntity(EntityType<? extends WarGnomeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossBarColor.YELLOW, BossBarOverlay.NOTCHED_20).setPlayBossMusic(true);
   }

   @Override
   public void switchNavigator(Mob entity, boolean onLand) {
      this.moveControl = new TensuraTamableEntity.SleepMoveControl();
      this.navigation = new GroundPathNavigation(this, this.level());
      this.wasFlying = false;
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 20.0)
         .add(Attributes.ATTACK_DAMAGE, 40.0)
         .add(Attributes.MAX_HEALTH, 400.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
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
      return source.is(DamageTypes.ON_FIRE)
         || source.is(DamageTypes.IN_FIRE)
         || source.is(DamageTypes.FALLING_STALACTITE)
         || source.is(DamageTypes.FALL)
         || super.isInvulnerableTo(source);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (!(pSource.getEntity() instanceof BeastGnomeEntity) && !(pSource.getEntity() instanceof WarGnomeEntity)) {
         boolean hurt = super.hurt(pSource, pAmount);
         if (hurt && pSource.getEntity() instanceof LivingEntity damageSource && !pSource.isCreativePlayer()) {
            if (!damageSource.isAlive() || this.isAlliedTo(damageSource)) {
               return true;
            }

            List<BeastGnomeEntity> list = this.level()
               .getEntitiesOfClass(BeastGnomeEntity.class, this.getBoundingBox().inflate(32.0), entity -> !entity.isTame());
            if (!list.isEmpty()) {
               list.forEach(gnome -> gnome.setTarget(damageSource));
            }
         }

         return hurt;
      } else {
         return false;
      }
   }

   @Override
   protected float getDamageReductionMultiplier(DamageSource source) {
      return TensuraDamageHelper.isGravityDamage(source) ? 0.05F : super.getDamageReductionMultiplier(source);
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof BeastGnomeEntity gnome) {
         return gnome.isTame() == this.isTame();
      } else {
         return entity instanceof WarGnomeEntity gnome ? gnome.isTame() == this.isTame() : false;
      }
   }

   @Override
   public EntityDimensions getSleepingDimensions(Pose pPose) {
      return this.getType().getDimensions().scale(1.0F, 0.25F);
   }

   public boolean isBlocking() {
      return this.random.nextFloat() <= 0.3 || this.getOffhandItem().getUseDuration(this) - this.useItemRemaining >= 5;
   }

   protected void blockUsingShield(LivingEntity target) {
      int randomBlock = this.getRandom().nextInt(3);
      if (randomBlock == 0) {
         this.triggerAnim("miscController", "block_away");
         SkillHelper.knockBack(this, target, 3.0F);
      } else if (randomBlock == 1) {
         this.triggerAnim("miscController", "block_up");
         Vec3 vec3 = new Vec3(target.getX() - this.getX(), target.getY() - this.getY() + 1.0, target.getZ() - this.getZ()).scale(1.0 / target.distanceTo(this));
         target.setDeltaMovement(vec3.normalize().scale(1.0));
         target.hasImpulse = true;
         target.hurtMarked = true;
      } else {
         this.triggerAnim("miscController", "block");
      }

      this.playSound(SoundEvents.SHIELD_BLOCK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
   }

   public boolean isDamageSourceBlocked(DamageSource source) {
      if (source.is(DamageTypeTags.BYPASSES_SHIELD)) {
         return false;
      }

      if (this.getRandom().nextFloat() >= 0.25) {
         return false;
      }

      if (source.is(DamageTypeTags.IS_PROJECTILE) && source.getDirectEntity() instanceof Projectile target) {
         if (this.getRandom().nextBoolean()) {
            boolean up = this.getRandom().nextBoolean();
            this.triggerAnim("miscController", up ? "block_up" : "block_away");
            Vec3 vec3 = new Vec3(target.getX() - this.getX(), target.getY() - this.getY() + (up ? 2.0 : 0.5), target.getZ() - this.getZ())
               .scale(1.0 / target.distanceTo(this));
            target.setDeltaMovement(vec3.normalize().scale(2.0));
            target.hasImpulse = true;
            target.hurtMarked = true;
         } else {
            this.triggerAnim("miscController", "block");
         }

         this.playSound(SoundEvents.SHIELD_BLOCK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
         return true;
      } else {
         return TensuraDamageHelper.isPhysicalAttack(source);
      }
   }

   protected void hurtCurrentlyUsedShield(float f) {
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide() && this.isAlive() && this.tickCount % 200 == 0 && this.onGround() && !this.isSleeping()) {
         this.training = !this.training && this.getRandom().nextFloat() <= 0.25F;
      }
   }

   @Override
   public void handleFlying(Mob entity) {
   }

   @Override
   protected void breakBlocks() {
      if (!this.isTame()) {
         this.breakBlocks(this, 2.0F, false, 1, null, false);
      }
   }

   private void gravitySphere() {
      int ballId = this.getMagicID();
      if (ballId == 0) {
         GravitySphereProjectile sphere = new GravitySphereProjectile(this.level(), this);
         sphere.setDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0));
         sphere.setSize(0.5F);
         sphere.setMpCost(500.0);
         sphere.setArmorHurt(30);
         sphere.setSkill((ManasSkillInstance)SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)ExtraSkills.EARTH_MANIPULATION.get()).orElse(null));
         sphere.setPos(this.getEyePosition().add(0.0, 4.0, 0.0));
         sphere.setOwnerOffset(new Vec3(0.0, 3.0, 0.0));
         sphere.setLookDistance(30.0F);
         sphere.setDelayTick(25);
         sphere.setDelaySizeChange(0.1F);
         sphere.setNoGravity(true);
         sphere.setMobEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.BURDEN), 100, 0, true, false, true));
         sphere.setEffectRange(3.0F);
         this.level().addFreshEntity(sphere);
         this.setMagicID(sphere.getId());
      } else if (!(this.level().getEntity(ballId) instanceof GravitySphereProjectile ball)) {
         this.setMagicID(0);
         this.gravitySphere();
      }

      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 10.0F, 0.95F + this.random.nextFloat() * 0.1F);
   }

   public void stomp() {
      this.setMagicID(0);
      TensuraParticleHelper.spawnGroundSlamParticle(this, 5, 2.5F);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getColorlessWave(0.9F, 5.0F), this.getX(), this.getY() + 0.2F, this.getZ());
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
      double damageMultiplier = 1.5;
      float damage = (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier);
      ManasSkillInstance instance = SkillUtils.getSkillOrNull(this, (ManasSkill)SpiritualMagics.EARTH_SPIKES.get());

      for (int i = 0; i < 10; i++) {
         float angle = (float) (Math.PI / 180.0) * this.yBodyRot + i;
         double extraX = 4.0F * Mth.sin((float)(Math.PI + angle));
         double extraZ = 4.0F * Mth.cos(angle);
         Vec3 groundPos = new Vec3(Mth.floor(this.getX() + extraX), this.getY(), Mth.floor(this.getZ() + extraZ));
         MagicSpikeEntity spike = new DripstoneSpikeEntity(this.level(), this);
         spike.setPos(groundPos);
         spike.setDamage(damage);
         spike.setLife(60);
         spike.setYaw(-30.0F);
         spike.setPitch(ObjectSelectionHelper.getYRotFromVector(spike.position().subtract(this.position()).normalize()));
         spike.setHeight(3.0F);
         spike.setSkill(this, instance, (TensuraSkill)SpiritualMagics.EARTH_SPIKES.get(), 0);
         this.level().addFreshEntity(spike);
         spike.triggerAnim("controller", "start");
         EffectStorage.setCameraShake(spike, 3.0, 0.005F, 5);
         this.level()
            .playSound(null, spike.getX(), spike.getY(), spike.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 10.0);
      List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb, this::shouldAttack);
      if (!list.isEmpty()) {
         DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.EARTH_ELEMENTAL, this);

         for (LivingEntity target : list) {
            if (target.distanceTo(this) > 5.0F) {
               target.hurt(source, damage / 1.5F);
            } else {
               target.hurt(source, damage);
            }

            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1, false, false, false));
            target.setDeltaMovement(0.0, 0.1, 0.0);
            SkillHelper.knockBack(this, target, 2.0F);
         }
      }
   }

   private void summonBeastGnome(int minRadius, int maxRadius) {
      if (this.level() instanceof ServerLevel serverLevel) {
         int var13 = Mth.floor(this.getX());
         int j = Mth.floor(this.getY());
         int k = Mth.floor(this.getZ());
         BeastGnomeEntity gnome = new BeastGnomeEntity((EntityType<? extends BeastGnomeEntity>)MonsterEntityTypes.BEAST_GNOME.get(), serverLevel);

         for (int l = 0; l < 50; l++) {
            int i1 = var13 + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int j1 = j + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int k1 = k + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            gnome.setPos(i1, j1, k1);
            if (serverLevel.isUnobstructed(gnome) && serverLevel.noCollision(gnome)) {
               gnome.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(gnome.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
               gnome.setTarget(this.getTarget());
               IExistence existence = TensuraStorages.getExistenceFrom(gnome);
               existence.setSummonedSecond(300);
               existence.setSummoner(this.getUUID());
               existence.markDirty();
               serverLevel.addFreshEntityWithPassengers(gnome);
               TensuraParticleHelper.addServerParticlesAroundSelf(
                  gnome, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD_BRICKS.defaultBlockState()), 2.0
               );
               TensuraParticleHelper.addServerParticlesAroundSelf(
                  gnome, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DRIPSTONE_BLOCK.defaultBlockState()), 2.0
               );
               break;
            }
         }
      }
   }

   @Override
   public Element getElemental() {
      return Element.EARTH;
   }

   @Override
   public Item getElementalCore() {
      return (Item)TensuraMaterialItems.ELEMENT_CORE_EARTH.get();
   }

   @Override
   public void die(DamageSource source) {
      super.die(source);
      if (!this.isAlive()) {
         this.triggerAnim("heldController", "death");
      }
   }

   protected void tickDeath() {
      if (++this.deathTime >= 29) {
         this.remove(RemovalReason.KILLED);
         this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 1.0F);
         this.spawnDeathParticles();
      }
   }

   @Override
   protected void spawnDeathParticles() {
      TensuraParticleHelper.addServerParticlesAroundSelf(this, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD_BRICKS.defaultBlockState()));
      TensuraParticleHelper.addServerParticlesAroundSelf(this, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DRIPSTONE_BLOCK.defaultBlockState()));
      TensuraParticleHelper.addServerParticlesAroundSelf(this, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD_BRICKS.defaultBlockState()), 2.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DRIPSTONE_BLOCK.defaultBlockState()), 2.0);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.GOLEM_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.GOLEM_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.GOLEM_DEATH.get();
   }

   protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockIn) {
      this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
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

   public List<ExtendedSensor<WarGnomeEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<WarGnomeEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<WarGnomeEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldTarget).whenStarting(entity -> entity.setMagicID(0)),
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

   public BrainActivityGroup<WarGnomeEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomHeldAttack()
                     .maxAttackRadius(40.0F)
                     .attackInterval(entity -> 0)
                     .onTick(
                        (entity, target, tick) -> {
                           switch (tick) {
                              case 7:
                                 entity.summonBeastGnome(3, 7);
                                 break;
                              case 14:
                                 entity.summonBeastGnome(5, 10);
                                 break;
                              case 21:
                                 entity.summonBeastGnome(7, 15);
                                 break;
                              case 25:
                                 entity.shouldSummonGnomes = false;
                           }

                           entity.level()
                              .playSound(
                                 null,
                                 entity,
                                 (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 10.0F,
                                 0.95F + entity.getRandom().nextFloat() * 0.1F
                              );
                           return tick < 40;
                        }
                     )
                     .whenStopping(entity -> entity.shouldSummonGnomes = false)
                     .startCondition(entity -> entity.getMagicID() == 0 && entity.shouldSummonGnomes)
                     .whenStarting(entity -> entity.triggerAnim("heldController", "summon")),
                  new CustomHeldAttack().minAttackRadius(0.0F).maxAttackRadius(40.0F).attackInterval(entity -> 60).onTick((entity, target, tick) -> {
                     if (tick >= 20 && tick <= 45) {
                        if (tick == 20) {
                           entity.setMagicID(0);
                        }

                        entity.gravitySphere();
                     }

                     return tick < 50;
                  }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.2 && entity.getMagicID() == 0).whenStarting(entity -> {
                     entity.setMagicID(1);
                     entity.triggerAnim("heldController", "sphere");
                  }).whenStopping(entity -> entity.setMagicID(0)),
                  new CustomRangeAttack(12)
                     .maxAttackRadius(7.0F)
                     .performAttack((entity, target) -> entity.stomp())
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.3 && entity.getMagicID() == 0)
                     .whenStarting(entity -> {
                        entity.triggerAnim("miscController", "stomp");
                        entity.setMagicID(1);
                     })
                     .whenStopping(entity -> entity.setMagicID(0)),
                  new CustomRangeAttack(15)
                     .maxAttackRadius(7.0F)
                     .performAttack(
                        (entity, target) -> {
                           entity.setMagicID(0);
                           GravityField sphere = new GravityField(entity.level(), entity);
                           sphere.setLife(1200);
                           sphere.setSize(10.0F);
                           sphere.setPos(entity.position().add(0.0, -10.0, 0.0));
                           entity.level().addFreshEntity(sphere);
                           entity.level()
                              .playSound(
                                 null,
                                 entity,
                                 (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 8.0F,
                                 0.95F + this.random.nextFloat() * 0.1F
                              );
                        }
                     )
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.1 && entity.getMagicID() == 0)
                     .whenStarting(entity -> {
                        entity.triggerAnim("miscController", "summon");
                        entity.setMagicID(1);
                     })
                     .whenStopping(entity -> entity.setMagicID(0)),
                  new CustomRangeAttack(7)
                     .maxAttackRadius(5.0F)
                     .performAttack(
                        (entity, target) -> {
                           entity.doHurtTarget(target);
                           Vec3 vec3 = new Vec3(target.getX() - this.getX(), target.getY() - this.getY() + 1.0, target.getZ() - this.getZ())
                              .scale(1.0 / target.distanceTo(entity));
                           target.setDeltaMovement(vec3.normalize().scale(2.0));
                           target.hasImpulse = true;
                           target.hurtMarked = true;
                           entity.setMagicID(0);
                        }
                     )
                     .startCondition(entity -> entity.getMagicID() == 0 && entity.getRandom().nextFloat() <= 0.2)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "shield_bash")),
                  new CustomRangeAttack(1).maxAttackRadius(40.0F).performAttack((entity, target) -> {
                     List<BeastGnomeEntity> serpents = entity.level().getEntitiesOfClass(BeastGnomeEntity.class, entity.getBoundingBox().inflate(20.0));
                     if (!serpents.isEmpty()) {
                        for (BeastGnomeEntity gnome : serpents) {
                           gnome.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), 200, 1, false, false, false));
                        }
                     } else {
                        entity.shouldSummonGnomes = entity.getRandom().nextFloat() <= 0.1;
                     }
                  }).startCondition(entity -> entity.getMagicID() == 0 && !entity.isTame()),
                  new AnimatableMeleeAttack(7)
                     .attackInterval(entity -> 5)
                     .whenStarting(entity -> entity.triggerAnim("miscController", entity.getRandom().nextBoolean() ? "swing" : "swing2"))
                     .startCondition(entity -> entity.getMagicID() == 0)
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<WarGnomeEntity> state) {
      String name;
      if (this.isNoAi()) {
         name = "animation.war_gnome.stay";
      } else if (this.isSleeping()) {
         name = "animation.war_gnome.sleep";
      } else if (this.isInSittingPose()) {
         if (this.training) {
            name = "animation.war_gnome.train";
         } else {
            name = "animation.war_gnome.stay";
         }
      } else if (state.isMoving()) {
         if (this.onGround() && this.isAngry()) {
            name = "animation.war_gnome.run";
         } else {
            name = "animation.war_gnome.walk";
         }
      } else {
         name = "animation.war_gnome.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("block", RawAnimation.begin().then("animation.war_gnome.block", LoopType.PLAY_ONCE))
               .triggerableAnim("block_up", RawAnimation.begin().then("animation.war_gnome.block_fling_up", LoopType.PLAY_ONCE))
               .triggerableAnim("block_away", RawAnimation.begin().then("animation.war_gnome.block_fling_away", LoopType.PLAY_ONCE))
               .triggerableAnim("swing", RawAnimation.begin().then("animation.war_gnome.swing", LoopType.PLAY_ONCE))
               .triggerableAnim("swing2", RawAnimation.begin().then("animation.war_gnome.swing2", LoopType.PLAY_ONCE))
               .triggerableAnim("shield_bash", RawAnimation.begin().then("animation.war_gnome.shield_bash", LoopType.PLAY_ONCE))
               .triggerableAnim("stomp", RawAnimation.begin().then("animation.war_gnome.stomp", LoopType.PLAY_ONCE)),
            new AnimationController(this, "heldController", 3, event -> PlayState.STOP)
               .triggerableAnim("sphere", RawAnimation.begin().then("animation.war_gnome.sphere", LoopType.PLAY_ONCE))
               .triggerableAnim("summon", RawAnimation.begin().then("animation.war_gnome.summon", LoopType.PLAY_ONCE))
               .triggerableAnim("death", RawAnimation.begin().then("animation.war_gnome.death", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
