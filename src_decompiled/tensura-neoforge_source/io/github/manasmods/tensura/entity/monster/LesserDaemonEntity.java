package io.github.manasmods.tensura.entity.monster;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.OrbitAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.FireBoltProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FlameSphereProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.StoneShotProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WaterBladeProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WindBladeProjectile;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IDaemon;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
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

public class LesserDaemonEntity extends PlayerLikeEntity implements GeoEntity, SmartBrainOwner<LesserDaemonEntity>, IDaemon, IFlying, INameEvolution {
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(LesserDaemonEntity.class, EntityDataSerializers.BOOLEAN);
   protected int flyingTick;
   protected boolean wasFlying;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public LesserDaemonEntity(EntityType<? extends LesserDaemonEntity> type, Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.initFlying(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 5.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.2F)
         .add(Attributes.FLYING_SPEED, 2.0)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 10.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 2.0);
   }

   @Override
   public void switchMoveControl(MoveControl control) {
      this.moveControl = control;
   }

   @Override
   public void switchNavigation(PathNavigation navigation) {
      this.navigation = navigation;
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(compound.getBoolean("Flying"));
   }

   public boolean isFlying() {
      return (Boolean)this.entityData.get(FLYING);
   }

   @Override
   public void setFlying(boolean flying) {
      this.entityData.set(FLYING, flying);
   }

   @Override
   public boolean wasFlying() {
      return this.wasFlying;
   }

   @Override
   public boolean shouldStopFlying(Mob entity) {
      return IFlying.super.shouldStopFlying(entity) || this.isOrderedToSit() || this.isInLove();
   }

   @Override
   public EntityDimensions getSleepingDimensions(Pose pPose) {
      return this.getType().getDimensions().scale(this.getAgeScale());
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof LesserDaemonEntity daemon) {
         return daemon.isTame() == this.isTame();
      } else if (entity instanceof GreaterDaemonEntity daemon) {
         return daemon.isTame() == this.isTame();
      } else {
         return entity instanceof ArchDaemonEntity daemon ? daemon.isTame() == this.isTame() : false;
      }
   }

   public boolean canAttack(LivingEntity pTarget) {
      return this.isAlliedTo(pTarget) ? false : super.canAttack(pTarget);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || super.isInvulnerableTo(source);
   }

   protected void actuallyHurt(DamageSource source, float damage) {
      damage *= this.getPhysicalAttackInput(source);
      if (TensuraDamageHelper.isTensuraMagic(source)) {
         damage *= 0.1F;
      }

      super.actuallyHurt(source, damage);
   }

   @Override
   public boolean canBeNamed(Player player) {
      return !this.isTamedByNonPlayer();
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
   }

   public void shootFireBolt(@NotNull LivingEntity target, float v) {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.FIRE.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            this.lookAt(Anchor.EYES, target.getEyePosition());
            FireBoltProjectile bolt = new FireBoltProjectile(this.level(), this);
            bolt.setSkill(instance);
            bolt.setSize(1.5F);
            float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
            double xOffset = Mth.sin((float)(Math.PI + angle));
            double zOffset = Mth.cos(angle);
            bolt.moveTo(this.getX() + xOffset, this.getEyeY(), this.getZ() + zOffset, this.getYRot(), this.getXRot());
            bolt.setNoGravity(true);
            bolt.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            bolt.setSecondaryDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0));
            bolt.setBurnTicks(100);
            bolt.shootToward(target, v, 0.0F);
            this.level().addFreshEntity(bolt);
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void shootWaterBlade(@NotNull LivingEntity target, float v) {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.WATER_CUTTER.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            this.lookAt(Anchor.EYES, target.getEyePosition());
            WaterBladeProjectile blade = new WaterBladeProjectile(this.level(), this);
            blade.setSkill(instance);
            blade.setSize(1.0F);
            float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
            double xOffset = Mth.sin((float)(Math.PI + angle));
            double zOffset = Mth.cos(angle);
            blade.moveTo(this.getX() + xOffset, this.getEyeY(), this.getZ() + zOffset, this.getYRot(), this.getXRot());
            blade.setNoGravity(true);
            blade.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            blade.setSecondaryDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0));
            blade.setBurnTicks(-1);
            blade.shootToward(target, v, 0.0F);
            this.level().addFreshEntity(blade);
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void shootWindCutter(@NotNull LivingEntity target, float v) {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.WIND_CUTTER.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            this.lookAt(Anchor.EYES, target.getEyePosition());
            WindBladeProjectile sphere = new WindBladeProjectile(this.level(), this);
            sphere.setSkill(instance);
            sphere.setSize(1.0F);
            float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
            double xOffset = Mth.sin((float)(Math.PI + angle));
            double zOffset = Mth.cos(angle);
            sphere.moveTo(this.getX() + xOffset, this.getEyeY(), this.getZ() + zOffset, this.getYRot(), this.getXRot());
            sphere.setNoGravity(true);
            sphere.setKnockForce(3.0F);
            sphere.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            sphere.setSecondaryDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0F);
            sphere.setBurnTicks(-1);
            sphere.shootToward(target, v, 0.0F);
            this.level().addFreshEntity(sphere);
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void burstStoneShot(Predicate<LivingEntity> predicate) {
      if (this.canCastMagics(this)) {
         ManasSkillInstance stoneShot = this.getMagic(this, (Magic)AspectualMagics.STONE_SHOT.get());
         if (stoneShot == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            this.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.EARTH_LOCK), 240, 0, false, false, false));
            double size = this.getAttributeValue(Attributes.SCALE) * 4.0;
            TensuraParticleHelper.spawnServerParticles(
               this.level(),
               TensuraParticleUtils.getEarthAura(1.0F, (float)size, -0.3F),
               this.getX(),
               this.getEyeY(),
               this.getZ(),
               55,
               0.08,
               0.08,
               0.08,
               0.2,
               true
            );
            TensuraParticleHelper.spawnServerParticles(
               this.level(),
               new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD_BRICKS.defaultBlockState()),
               this.getX(),
               this.getEyeY(),
               this.getZ(),
               55,
               0.08,
               0.08,
               0.08,
               0.2,
               true
            );
            TensuraParticleHelper.addServerParticlesAroundSelf(this, TensuraParticleUtils.getEarthAura(1.0F, (float)size, -0.3F), 2.0);
            AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 10.0);
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb, predicate);
            if (!list.isEmpty()) {
               DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.MAGIC_GENERIC, this);

               for (LivingEntity target : list) {
                  target.hurt(source, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0F);
                  target.setDeltaMovement(0.0, 0.1, 0.0);
                  SkillHelper.knockBack(this, target, 2.0F);
               }

               double angleStep = Math.PI / 6;

               for (int i = 0; i < 12; i++) {
                  double angle = i * angleStep;
                  double offsetX = Math.cos(angle) * 3.0;
                  double offsetZ = Math.sin(angle) * 3.0;
                  double spawnX = this.getX() + offsetX;
                  double spawnY = this.getY() + this.getBbHeight() / 2.0F;
                  double spawnZ = this.getZ() + offsetZ;
                  Vec3 direction = new Vec3(offsetX, 0.0, offsetZ).normalize();
                  StoneShotProjectile projectile = new StoneShotProjectile(this.level(), this);
                  projectile.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                  projectile.setSecondaryDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0F);
                  projectile.setSkill(stoneShot);
                  projectile.setNoGravity(true);
                  projectile.setPiercingEntity(true);
                  projectile.setPos(spawnX, spawnY, spawnZ);
                  projectile.shoot(direction.x, direction.y, direction.z, 1.0F, 0.0F);
                  this.level().addFreshEntity(projectile);
               }
            }
         }
      }
   }

   protected void flameOrb() {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.FIRE_BALL.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            int orbID = instance.getOrCreateTag().getInt("orbID");
            if (orbID == 0) {
               FlameSphereProjectile orb = new FlameSphereProjectile(this.level(), this);
               orb.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
               orb.setSecondaryDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 6.0));
               orb.setBurnTicks(100);
               orb.setMpCost(1000.0);
               orb.setSkill(instance);
               orb.setExplosionRadius(4.0F);
               orb.setPos(this.getEyePosition().add(0.0, this.getBbHeight() * 1.5, 0.0));
               orb.setOwnerOffset(new Vec3(0.0, this.getBbHeight() * 1.5, 0.0));
               orb.setLookDistance(30.0F);
               orb.setDelayTick(30);
               orb.setDelaySizeChange(0.05F);
               orb.setNoGravity(true);
               this.level().addFreshEntity(orb);
               instance.getOrCreateTag().putInt("orbID", orb.getId());
            } else {
               Entity entity = this.level().getEntity(orbID);
               if (!(entity instanceof FlameSphereProjectile)) {
                  instance.getOrCreateTag().putInt("orbID", 0);
                  this.flameOrb();
               }
            }

            this.level()
               .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 10.0F, 0.95F + this.random.nextFloat() * 0.1F);
         }
      }
   }

   @Override
   public List<EquipmentSlot> getAvailableSlots() {
      return List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
   }

   @Override
   public int getChestSlots() {
      return 18;
   }

   @Override
   public int getMenuRenderSize() {
      return 15;
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      return null;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.SPIRIT_FOOD);
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (this.isTame() && this.isOwnedBy(player)) {
         InteractionResult golemInteraction = this.getGolemInteraction(player, hand, this);
         if (golemInteraction.consumesAction()) {
            return golemInteraction;
         }

         InteractionResult interaction = this.getInventoryInteraction(player, hand);
         if (interaction.consumesAction()) {
            return interaction;
         }

         this.cycleCommands(this, player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   public void applyFoodHeal(ItemStack stack, Player player, InteractionHand hand) {
      this.heal(5.0F);
      this.ate();
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.lesserDaemon, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.GOAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return SoundEvents.GOAT_SCREAMING_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.GOAT_SCREAMING_DEATH;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<LesserDaemonEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<LesserDaemonEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<LesserDaemonEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, target -> this.shouldAttack(this, target)),
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

   public BrainActivityGroup<LesserDaemonEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new OrbitAttack()
               .lookAtTargetWhileOrbiting(true)
               .speedMod((entity, target) -> 2.0F)
               .orbitRadius((entity, target) -> 20.0)
               .orbitMinRadius((entity, target) -> 10.0)
               .orbitHeight((entity, target) -> 10.0)
               .orbitAttackInterval(entity -> !entity.canCastMagics(entity) ? 10 : 200)
               .shouldDoMeleeAttack((entity, target) -> entity.distanceTo(target) <= 5.0F)
               .onStartOrbitAttack(
                  (entity, target) -> entity.level()
                     .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GOAT_PREPARE_RAM, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F)
               )
               .performOrbitAttack((entity, target) -> {
                  entity.doHurtTarget(target);
                  entity.triggerAnim("attackController", entity.getRandom().nextBoolean() ? "attack_right" : "attack_left");
               })
               .onTick(entity -> {
                  entity.setFlying(true);
                  return true;
               }),
            new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     new CustomRangeAttack(10)
                        .maxAttackRadius(12.0F)
                        .attackInterval(entity -> 20)
                        .performAttack((entity, target) -> {
                           entity.burstStoneShot(living -> entity.shouldAttack(entity, living));
                           entity.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 0.95F + entity.getRandom().nextFloat() * 0.1F);
                        })
                        .whenStarting(
                           entity -> {
                              entity.triggerAnim("miscController", "burst");
                              ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.STONE_SHOT.get());
                              if (instance != null) {
                                 MagicCircle.castMagicCircle(
                                    4.0F,
                                    25,
                                    MagicCircleVariant.EARTH,
                                    true,
                                    entity,
                                    new CompoundTag(),
                                    0.0F,
                                    new Vec3(0.0, entity.getBbHeight() / 2.0F, 0.0),
                                    instance,
                                    0,
                                    Pair.of(0.0, 1000.0)
                                 );
                              }
                           }
                        )
                        .startCondition(
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.STONE_SHOT.get()) != null && entity.getRandom().nextFloat() < 0.1F
                        ),
                     new CustomRangeAttack(15)
                        .maxAttackRadius(40.0F)
                        .attackInterval(entity -> 20)
                        .performAttack((entity, target) -> entity.shootFireBolt(target, 1.5F))
                        .whenStarting(entity -> {
                           entity.triggerAnim("miscController", "magic_shoot");
                           ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.FIRE.get());
                           if (instance != null) {
                              MagicCircle.castMagicCircle(
                                 2.0F, 25, MagicCircleVariant.FLAME, entity, new CompoundTag(), 3.0F, Vec3.ZERO, instance, 0, Pair.of(0.0, 100.0)
                              );
                           }
                        })
                        .startCondition(entity -> entity.getMagic(entity, (Magic)AspectualMagics.FIRE.get()) != null && entity.getRandom().nextFloat() < 0.3F),
                     new CustomRangeAttack(15)
                        .maxAttackRadius(40.0F)
                        .attackInterval(entity -> 20)
                        .performAttack((entity, target) -> entity.shootWaterBlade(target, 2.5F))
                        .whenStarting(entity -> {
                           entity.triggerAnim("attackController", entity.getRandom().nextBoolean() ? "swing_right" : "swing_left");
                           ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.WATER_CUTTER.get());
                           if (instance != null) {
                              MagicCircle.castMagicCircle(
                                 2.0F, 25, MagicCircleVariant.WATER, entity, new CompoundTag(), 3.0F, Vec3.ZERO, instance, 0, Pair.of(0.0, 100.0)
                              );
                           }
                        })
                        .startCondition(
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.WATER_CUTTER.get()) != null && entity.getRandom().nextFloat() < 0.3F
                        ),
                     new CustomRangeAttack(15)
                        .maxAttackRadius(40.0F)
                        .attackInterval(entity -> 20)
                        .performAttack((entity, target) -> entity.shootWindCutter(target, 1.5F))
                        .whenStarting(entity -> {
                           entity.triggerAnim("attackController", entity.getRandom().nextBoolean() ? "swing_right" : "swing_left");
                           ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.WIND_CUTTER.get());
                           if (instance != null) {
                              MagicCircle.castMagicCircle(
                                 2.0F, 25, MagicCircleVariant.WIND, entity, new CompoundTag(), 3.0F, Vec3.ZERO, instance, 0, Pair.of(0.0, 100.0)
                              );
                           }
                        })
                        .startCondition(
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.WIND_CUTTER.get()) != null && entity.getRandom().nextFloat() < 0.3F
                        ),
                     new CustomHeldAttack()
                        .minAttackRadius(0.0F)
                        .maxAttackRadius(40.0F)
                        .attackInterval(entity -> 40)
                        .onTick((entity, target, tick) -> {
                           if (tick >= 15 && tick <= 45) {
                              entity.flameOrb();
                           }

                           return tick < 60;
                        })
                        .whenStarting(
                           entity -> {
                              entity.triggerAnim("miscController", "magic_big");
                              ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.FIRE_BALL.get());
                              if (instance != null) {
                                 MagicCircle.castMagicCircle(
                                    3.0F,
                                    45,
                                    MagicCircleVariant.FLAME,
                                    true,
                                    entity,
                                    new CompoundTag(),
                                    0.0F,
                                    new Vec3(0.0, -1.0, 0.0),
                                    instance,
                                    0,
                                    Pair.of(0.0, 1000.0)
                                 );
                              }
                           }
                        )
                        .startCondition(
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.FIRE_BALL.get()) != null && entity.getRandom().nextFloat() < 0.2F
                        )
                  }
               )
               .startCondition(entity -> entity.canCastMagics(entity))
         }
      );
   }

   protected PlayState loopController(AnimationState<LesserDaemonEntity> state) {
      String name;
      if (this.isNoAi()) {
         name = "animation.lesser_daemon.concealed";
      } else if (!this.isAlive()) {
         name = "animation.lesser_daemon.burst_quick";
      } else if (this.isInSittingPose()) {
         name = "animation.lesser_daemon.stay";
      } else if (state.isMoving()) {
         if (this.onGround()) {
            name = "animation.lesser_daemon.walk";
         } else {
            name = "animation.lesser_daemon.fly";
         }
      } else if (this.onGround()) {
         name = "animation.lesser_daemon.idle";
      } else {
         name = "animation.lesser_daemon.idle_fly";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController)
               .triggerableAnim("conceal", RawAnimation.begin().then("animation.lesser_daemon.conceal", LoopType.PLAY_ONCE))
               .triggerableAnim("conceal_off", RawAnimation.begin().then("animation.lesser_daemon.conceal_off", LoopType.PLAY_ONCE)),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("magic_shoot", RawAnimation.begin().then("animation.lesser_daemon.magic_shoot", LoopType.PLAY_ONCE))
               .triggerableAnim("magic_big", RawAnimation.begin().then("animation.lesser_daemon.magic_big", LoopType.PLAY_ONCE))
               .triggerableAnim("burst", RawAnimation.begin().then("animation.lesser_daemon.burst", LoopType.PLAY_ONCE)),
            new AnimationController(this, "attackController", 3, event -> PlayState.STOP)
               .setAnimationSpeed(2.0)
               .triggerableAnim("attack_left", RawAnimation.begin().then("animation.lesser_daemon.attack_left", LoopType.PLAY_ONCE))
               .triggerableAnim("attack_right", RawAnimation.begin().then("animation.lesser_daemon.attack_right", LoopType.PLAY_ONCE))
               .triggerableAnim("swing_left", RawAnimation.begin().then("animation.lesser_daemon.swing_left", LoopType.PLAY_ONCE))
               .triggerableAnim("swing_right", RawAnimation.begin().then("animation.lesser_daemon.swing_right", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   @Override
   public void setFlyingTick(int flyingTick) {
      this.flyingTick = flyingTick;
   }

   @Generated
   @Override
   public int getFlyingTick() {
      return this.flyingTick;
   }

   @Generated
   @Override
   public void setWasFlying(boolean wasFlying) {
      this.wasFlying = wasFlying;
   }
}
