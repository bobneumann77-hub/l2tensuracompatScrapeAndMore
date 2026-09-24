package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.block.CharybdisCoreBlock;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.OrbitMovement;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.controller.FlightMoveController;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierPart;
import io.github.manasmods.tensura.entity.magic.misc.TensuraFallingBlock;
import io.github.manasmods.tensura.entity.projectile.magic.TempestScaleEntity;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.IGiantMob;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
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

public class CharybdisEntity extends TensuraMountEntity implements SmartBrainOwner<CharybdisEntity>, GeoEntity, IFlying, ITensuraMount, IGiantMob {
   private static final EntityDataAccessor<BlockPos> BEAM_TARGET = SynchedEntityData.defineId(CharybdisEntity.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<Integer> MEGALODON_SUMMON = SynchedEntityData.defineId(CharybdisEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(CharybdisEntity.class, EntityDataSerializers.BOOLEAN);
   protected int flyingTick;
   protected boolean wasFlying;
   private int stayLowTick = 0;
   public static final float MAX_SIZE = TensuraBehaviourHelper.CONFIG.MobSpecific.charybdisSize;
   private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(), BossBarColor.BLUE, BossBarOverlay.NOTCHED_20);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public CharybdisEntity(EntityType<? extends CharybdisEntity> type, Level worldIn) {
      super(type, worldIn);
      this.initFlying(this);
      this.noCulling = true;
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 3000.0)
         .add(Attributes.ATTACK_DAMAGE, 100.0)
         .add(Attributes.ARMOR, 20.0)
         .add(Attributes.FOLLOW_RANGE, 256.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.FLYING_SPEED, 0.7F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.STEP_HEIGHT, 5.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 2.0)
         .add(Attributes.SCALE, MAX_SIZE)
         .add(TensuraAttributes.PRESENCE_SENSE, 3.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 40.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 7.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 7.0);
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
   public void switchNavigator(Mob entity, boolean onLand) {
      this.switchMoveControl(new FlightMoveController(entity, true, this.canIgnoreCollisionFlight()));
      this.switchNavigation(this.getFlyingPathNavigation(entity));
      this.setWasFlying(true);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
      builder.define(MEGALODON_SUMMON, 3);
      builder.define(BEAM_TARGET, BlockPos.ZERO);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putInt("MegalodonSummon", this.getMegalodonSummon());
      compound.putInt("BX", this.getBeamTarget().getX());
      compound.putInt("BY", this.getBeamTarget().getY());
      compound.putInt("BZ", this.getBeamTarget().getZ());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(compound.getBoolean("Flying"));
      this.setMegalodonSummon(compound.getInt("MegalodonSummon"));
      if (compound.contains("BX")) {
         this.setBeamTarget(new BlockPos(compound.getInt("BX"), compound.getInt("BY"), compound.getInt("BZ")));
      }

      if (this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }
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
   public boolean shouldContinueFlying() {
      return true;
   }

   @Override
   public boolean shouldStopFlying(Mob entity) {
      return false;
   }

   @Override
   public boolean canIgnoreCollisionFlight() {
      return true;
   }

   public int getMegalodonSummon() {
      return (Integer)this.entityData.get(MEGALODON_SUMMON);
   }

   public void setMegalodonSummon(int summon) {
      this.entityData.set(MEGALODON_SUMMON, summon);
   }

   public BlockPos getBeamTarget() {
      return (BlockPos)this.entityData.get(BEAM_TARGET);
   }

   public void setBeamTarget(BlockPos pos) {
      this.entityData.set(BEAM_TARGET, pos);
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 1;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || source.type().effects().equals(DamageEffects.POKING) || super.isInvulnerableTo(source);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return this.isInvulnerableTo(pSource) ? false : super.hurt(pSource, pAmount * 0.5F);
   }

   public boolean isOnFire() {
      return TensuraStorages.getEffectFrom(this).isOnBlackFlame();
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof MegalodonEntity megalodon) {
         return megalodon.isTame() == this.isTame();
      } else {
         return entity instanceof CharybdisEntity charybdis ? charybdis.isTame() == this.isTame() : false;
      }
   }

   public boolean canAttack(LivingEntity pTarget) {
      return this.isAlliedTo(pTarget) ? false : super.canAttack(pTarget);
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return true;
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
   protected void applyTamingSideEffects() {
      super.applyTamingSideEffects();
      this.bossEvent.removeAllPlayers();
   }

   @Override
   public boolean breakableBlocks(LivingEntity entity, BlockPos pos, BlockState state) {
      return !state.is(TensuraBlockTags.BOSS_IMMUNE);
   }

   @Override
   public boolean dropBlockLoot(LivingEntity entity, BlockState state) {
      return false;
   }

   public Vec3 getBeamStartOffset() {
      float radius = 14.0F * MAX_SIZE;
      float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
      double extraX = radius * Mth.sin((float)(Math.PI + angle));
      double extraZ = radius * Mth.cos(angle);
      return new Vec3(extraX, this.getBbHeight() / 2.0, extraZ);
   }

   @Override
   public void tick() {
      super.tick();
      this.handleFlying(this);
      AttributeInstance size = this.getAttribute(Attributes.SCALE);
      if (size != null) {
         if (!(size.getBaseValue() >= MAX_SIZE)) {
            double addSize = (MAX_SIZE - 0.1) / 100.0;
            size.setBaseValue(Math.min(size.getBaseValue() + addSize, MAX_SIZE));
            EffectStorage.setCameraShake(this, 20.0, 0.02F, 10);
            this.playSound(SoundEvents.WARDEN_AGITATED, 10.0F, 1.0F);
         }
      }
   }

   @Override
   public void handleFlying(Mob entity) {
      if (!this.level().isClientSide()) {
         entity.setNoGravity(true);
         if (entity.tickCount % 200 == 0 && entity.getRandom().nextBoolean()) {
            this.stayLowTick = 140;
         }

         if (this.stayLowTick > 0) {
            this.stayLowTick--;
         }

         LivingEntity controller = this.getControllingPassenger();
         SimpleContainer container = this.isChested() ? this.inventory : null;
         if (!this.isTame()) {
            this.breakBlocks(this, 1.0F, true, 0, container, true);
         } else if (controller != null && this.isOwnedBy(controller)) {
            this.breakBlocks(this, 1.0F, false, 0, container);
         }

         if (controller != null) {
            if (controller.getXRot() <= 20.0F) {
               this.digBlocks(this, 1.0F, 1, 1.0F, false, container);
            } else {
               this.digBlocks(this, 1.0F, 0, 1.0F, controller.getXRot() >= 40.0F, container);
            }
         }
      }
   }

   @Override
   public boolean doHurtTarget(Entity pEntity) {
      if (super.doHurtTarget(pEntity) && pEntity instanceof LivingEntity living) {
         if (EnergyHelper.getMaxEP(living) > EnergyHelper.getMaxEP(this) * 3.5) {
            return true;
         }

         MobEffectInstance instance = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE), 200, 1, true, true, true);
         TensuraMobEffect.addEffect(living, instance, this, null);
         return true;
      } else {
         return false;
      }
   }

   private void wickedLightRay(int tick) {
      if (tick < 20) {
         this.level().playSound(null, this, SoundEvents.BEACON_AMBIENT, TensuraSkill.ABILITY_SOUND, 10.0F, 0.95F + this.getRandom().nextFloat() * 0.1F);
      } else if (tick == 20) {
         if (this.getTarget() != null) {
            this.setBeamTarget(this.getTarget().blockPosition().below());
         } else {
            double d0 = this.getRandom().nextDouble() * Math.PI * 2.0;
            BlockPos newTarget = new BlockPos(
               (int)(this.blockPosition().getX() - Math.sin(d0) * 25.0),
               this.getBeamTarget().getY() - 50,
               (int)(this.blockPosition().getZ() - Math.cos(d0) * 25.0)
            );
            this.setBeamTarget(newTarget);
         }
      } else if (tick >= 40) {
         this.setBeamTarget(BlockPos.ZERO);
      } else {
         this.setBeamTarget(this.getBeamTarget().below());
         this.level().playSound(null, this, SoundEvents.BEACON_ACTIVATE, TensuraSkill.ABILITY_SOUND, 10.0F, 0.95F + this.getRandom().nextFloat() * 0.1F);
         this.rayDamage();
      }
   }

   private void rayDamage() {
      Vec3 source = this.getEyePosition().add(this.getBeamStartOffset());
      Vec3 targetPos = Vec3.atCenterOf(this.getBeamTarget());
      Vec3 offSetToTarget = targetPos.subtract(source);
      Vec3 normalizes = offSetToTarget.normalize();
      double beamRadius = 4.0;
      ArrayList<LivingEntity> exploded = new ArrayList<>();

      for (int i = 0; i < Mth.floor(offSetToTarget.length()) + 1; i += 2) {
         Vec3 particlePos = source.add(normalizes.scale(i));
         AABB aabb = new AABB(
            particlePos.x + beamRadius,
            particlePos.y + beamRadius,
            particlePos.z + beamRadius,
            particlePos.x - beamRadius,
            particlePos.y - beamRadius,
            particlePos.z - beamRadius
         );
         boolean explosion = false;
         List<LivingEntity> list = this.level()
            .getEntitiesOfClass(LivingEntity.class, aabb, entity -> entity != this.getOwner() && entity != this && !this.isAlliedTo(entity) && entity.isAlive());
         DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.WICKED_LIGHT_RAY, this);

         for (LivingEntity living : list) {
            float multiplier = RaceUtils.isAffectedByHolyExposure(living) ? 2.0F : 1.5F;
            if (living.hurt(damageSource, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier)
               && EnergyHelper.getMaxEP(living) <= EnergyHelper.getMaxEP(this) * 3.5) {
               MobEffectInstance instance = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE), 200, 1, true, true, true
               );
               TensuraMobEffect.addEffect(living, instance, this, null);
            }

            if (!exploded.contains(living)) {
               exploded.add(living);
               this.level().explode(this, living.getX(), living.getY(0.0625), living.getZ(), 5.0F, ExplosionInteraction.MOB);
               EffectStorage.setCameraShake(living, 5.0, 0.03F, 15);
               explosion = true;
            }
         }

         if (!explosion) {
            HitResult hitresult = this.level().clip(new ClipContext(particlePos, particlePos.add(normalizes), Block.COLLIDER, Fluid.NONE, this));
            if (hitresult.getType().equals(Type.BLOCK)) {
               this.level()
                  .explode(this, hitresult.getLocation().x(), hitresult.getLocation().y(), hitresult.getLocation().z(), 3.0F, ExplosionInteraction.MOB);
            }
         }
      }
   }

   private void summonMegalodons(int minRadius, int maxRadius) {
      if (this.level() instanceof ServerLevel serverLevel) {
         int var12 = Mth.floor(this.getX());
         int j = Mth.floor(this.getY() + this.getBbHeight() / 2.0F);
         int k = Mth.floor(this.getZ());
         MegalodonEntity megalodon = new MegalodonEntity((EntityType<? extends MegalodonEntity>)MonsterEntityTypes.MEGALODON.get(), serverLevel);

         for (int l = 0; l < 50; l++) {
            int i1 = var12 + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int j1 = j + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int k1 = k + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            megalodon.setPos(i1, j1, k1);
            if (serverLevel.isUnobstructed(megalodon) && serverLevel.noCollision(megalodon)) {
               megalodon.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(megalodon.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
               megalodon.setTarget(this.getTarget());
               megalodon.setFlying(true);
               megalodon.setNoGravity(true);
               megalodon.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), 12000, 1, false, false, false));
               serverLevel.addFreshEntityWithPassengers(megalodon);
               TensuraParticleHelper.addServerParticlesAroundSelf(megalodon, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get(), 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(megalodon, ParticleTypes.EXPLOSION_EMITTER, 2.0);
               break;
            }
         }
      }
   }

   private void summonTempestScales(int minRadius, int maxRadius) {
      if (this.level() instanceof ServerLevel serverLevel) {
         int var18 = Mth.floor(this.getX());
         int j = Mth.floor(this.getY() + this.getBbHeight() / 2.0F);
         int k = Mth.floor(this.getZ());
         TempestScaleEntity scale = new TempestScaleEntity(serverLevel, this);

         for (int l = 0; l < 50; l++) {
            int i1 = var18 + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int j1 = j + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int k1 = k + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            scale.setPos(i1, j1, k1);
            if (serverLevel.isUnobstructed(scale) && serverLevel.noCollision(scale)) {
               double d0 = this.getX() - scale.getX();
               double d1 = this.getY() + this.getBbHeight() / 2.0F - scale.getY();
               double d2 = this.getZ() - scale.getZ();
               scale.setDeltaMovement(new Vec3(d0, d1, d2).normalize().scale(2.0));
               scale.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2.0F);
               scale.setIgnoreInvulnerabilityOnHit(true);
               if (this.getRandom().nextInt(5) == 1) {
                  scale.setKnockForce(1.0F);
               }

               if (this.getRandom().nextInt(10) == 1) {
                  scale.setTarget(this.getTarget());
               }

               serverLevel.addFreshEntityWithPassengers(scale);
               break;
            }
         }
      }
   }

   @Override
   public boolean isFood(ItemStack stack) {
      return stack.is(TensuraItemTags.SPIRIT_FOOD) || stack.has(DataComponents.FOOD);
   }

   public CharybdisEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      return null;
   }

   @Override
   public int getChestSlots() {
      return 30;
   }

   public boolean isNoGravity() {
      return super.isNoGravity() ? true : this.getControllingPassenger() != null && !this.onGround();
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, -0.25, 3.0F * f).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   @Override
   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
      if (this.isNoGravity()) {
         if (controller.jumping) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.1, 0.0));
         } else if (TensuraKeybinds.DODGE.isDown()) {
            this.descending(this, controller);
         }
      }
   }

   @Override
   public void mountAbility(Player rider) {
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   @Override
   protected boolean removeWhenNoAction() {
      return false;
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      this.setFlying(true);
      this.setMegalodonSummon(pLevel.getRandom().nextBoolean() ? 3 : 4);
      this.getAttribute(Attributes.SCALE).setBaseValue(0.1);
      if (!this.isTame()) {
         OrbitMovement.setDefaultMeetingPoint(this, this.blockPosition());
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   protected void tickDeath() {
      if (++this.deathTime >= 40) {
         this.remove(RemovalReason.KILLED);
         TensuraFallingBlock block = TensuraFallingBlock.fall(
            this.level(),
            this.blockPosition(),
            (BlockState)((net.minecraft.world.level.block.Block)TensuraBlocks.CHARYBDIS_CORE.get())
               .defaultBlockState()
               .setValue(CharybdisCoreBlock.MODE, SculkSensorPhase.COOLDOWN)
         );
         block.setIndestructible(true);
         this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 1.0F);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.CLOUD);
         TensuraParticleHelper.addServerAuraParticles(this, TensuraParticleUtils.getBlackAura(0.5F, 10.0F, -0.3F), 20, 0.01);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.CLOUD, 2.0);
      }
   }

   public void die(DamageSource source) {
      super.die(source);
      if (!this.isAlive()) {
         if (this.getPose() == Pose.DYING) {
            if (!this.onGround() && !this.isInLiquid()) {
               this.triggerAnim("rollController", "death_fly");
            } else {
               this.triggerAnim("rollController", "death_swim");
            }
         }
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENDER_DRAGON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENDER_DRAGON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDER_DRAGON_DEATH;
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
      if (this.tickCount % 20 == 0) {
         List<BarrierPart> list = this.level().getEntitiesOfClass(BarrierPart.class, this.getBoundingBox().inflate(1.0));
         if (!list.isEmpty()) {
            for (BarrierPart barrier : list) {
               this.doHurtTarget(barrier);
            }
         }
      }

      if (!this.isTame()) {
         this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
      }
   }

   public List<ExtendedSensor<CharybdisEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<CharybdisEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<CharybdisEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldAttack),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  new SubordinateFollowOwner().canTeleportOffGroundWhen(entity -> {
                     entity.setFlying(true);
                     return true;
                  }),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OrbitMovement().requireNotHavingTarget(entity -> false).orbitHeight(entity -> entity.stayLowTick > 0 ? 7.0 : 30.0),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{
                     new SetRandomFlyAndWalkTarget().startCondition(TamableAnimal::isTame), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
                  }
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<CharybdisEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf((entity, target) -> {
               boolean stop = entity.shouldStopTarget(entity, target);
               if (!stop) {
                  OrbitMovement.setDefaultMeetingPoint(entity, target.blockPosition());
               }

               return stop;
            }),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomHeldAttack()
                     .maxAttackRadius(128.0F)
                     .attackInterval(entity -> (1 + entity.random.nextInt(4)) * 10)
                     .onTick(
                        (entity, target, tick) -> {
                           switch (tick) {
                              case 10:
                                 entity.summonMegalodons(10, 15);
                                 break;
                              case 17:
                                 entity.summonMegalodons(15, 20);
                                 break;
                              case 21:
                                 entity.summonMegalodons(20, 25);
                           }

                           entity.level()
                              .playSound(
                                 null,
                                 entity,
                                 (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 10.0F,
                                 0.95F + entity.getRandom().nextFloat() * 0.1F
                              );
                           return tick < 25;
                        }
                     )
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.1 && entity.canSummonMegalodons() && entity.getBeamTarget() == BlockPos.ZERO)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "roar")),
                  new CustomHeldAttack()
                     .maxAttackRadius(128.0F)
                     .attackInterval(entity -> (1 + entity.random.nextInt(4)) * 10)
                     .onTick(
                        (entity, target, tick) -> {
                           if (tick >= 10) {
                              entity.summonTempestScales(10, 15);
                              entity.summonTempestScales(5, 10);
                           }

                           entity.level()
                              .playSound(
                                 null,
                                 entity,
                                 (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 10.0F,
                                 0.95F + entity.getRandom().nextFloat() * 0.1F
                              );
                           return tick < 30;
                        }
                     )
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.1 && entity.getBeamTarget() == BlockPos.ZERO)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "scale")),
                  new CustomHeldAttack()
                     .minAttackRadius(0.0F)
                     .maxAttackRadius(128.0F)
                     .requireInSight(false)
                     .attackInterval(entity -> (1 + entity.random.nextInt(4)) * 10)
                     .onTick((entity, target, tick) -> {
                        entity.wickedLightRay(tick);
                        return tick < 45;
                     })
                     .whenStarting(
                        entity -> {
                           entity.triggerAnim("rollController", "beam");
                           entity.playSound(
                              SoundEvents.PLAYER_ATTACK_SWEEP,
                              entity.getSoundVolume(),
                              (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + 1.0F
                           );
                        }
                     )
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.05)
                     .whenStopping(entity -> entity.setBeamTarget(BlockPos.ZERO)),
                  new CustomHeldAttack()
                     .minAttackRadius(0.0F)
                     .maxAttackRadius(12.0F)
                     .requireInSight(false)
                     .attackInterval(entity -> (1 + entity.random.nextInt(2)) * 10)
                     .onTick(
                        (entity, target, tick) -> {
                           if (tick == 5) {
                              SkillHelper.riptidePush(entity, 5.0F);
                              entity.markHurt();
                              TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 8.0);
                              TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 5.0);
                              entity.playSound((SoundEvent)SoundEvents.TRIDENT_RIPTIDE_3.value(), 10.0F, 0.95F + this.random.nextFloat() * 0.1F);
                           } else if (tick > 5) {
                              entity.hasImpulse = true;
                              AABB aabb = entity.getBoundingBox().inflate(16.0);
                              List<LivingEntity> list = entity.level()
                                 .getEntitiesOfClass(
                                    LivingEntity.class, aabb, livingx -> !entity.isAlliedTo(livingx) && livingx != entity.getOwner() && livingx != entity
                                 );
                              if (!list.isEmpty()) {
                                 float damage = (float)(entity.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0);
                                 DamageSource source = entity.damageSources().mobAttack(entity);
                                 DamageSource magicSource = TensuraDamageTypes.getEntityDamageSource(entity.level(), TensuraDamageTypes.MAGIC_GENERIC, entity);

                                 for (LivingEntity living : list) {
                                    TensuraDamageHelper.hurtSplit(living, source, 0.5F, magicSource, damage);
                                    living.setDeltaMovement(living.getDeltaMovement().add(entity.getDeltaMovement()));
                                    living.hurtMarked = true;
                                 }
                              }
                           }

                           return target != null && target.isAlive() && tick < 20;
                        }
                     )
                     .whenStarting(
                        entity -> {
                           entity.triggerAnim("rollController", "roll");
                           entity.playSound(
                              SoundEvents.PLAYER_ATTACK_SWEEP,
                              entity.getSoundVolume(),
                              (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F + 1.0F
                           );
                        }
                     )
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.5 && entity.getBeamTarget() == BlockPos.ZERO),
                  new AnimatableMeleeAttack(5)
                     .attackInterval(entity -> 0)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "bite"))
                     .startCondition(entity -> entity.getBeamTarget() == BlockPos.ZERO)
               }
            )
         }
      );
   }

   public boolean shouldAttack(LivingEntity entity) {
      if (entity == this) {
         return false;
      } else if (RaceUtils.isNonLiving(entity)) {
         return false;
      } else if (entity.hasInfiniteMaterials()) {
         return false;
      } else if (this.isAlliedTo(entity)) {
         return false;
      } else if (this.getOwner() == null) {
         return EnergyHelper.getMaxEP(entity) > 100.0;
      } else if (entity.isAlliedTo(this.getOwner())) {
         return false;
      } else {
         return entity instanceof Mob mob
            ? mob.getTarget() == this.getOwner()
            : this.getOwner().getLastHurtMob() == entity || this.getOwner().getLastHurtByMob() == entity;
      }
   }

   private boolean canSummonMegalodons() {
      if (this.isTame()) {
         return false;
      }

      if (this.getRandom().nextInt(10) != 1) {
         return false;
      }

      int summon = this.getMegalodonSummon();
      if (summon <= 0) {
         return false;
      }

      this.setMegalodonSummon(summon - 1);
      return true;
   }

   protected PlayState loopController(AnimationState<CharybdisEntity> state) {
      String name;
      if (this.isInWaterRainOrBubble()) {
         if (state.isMoving()) {
            if (!this.isAngry() && !this.isSprinting()) {
               name = "animation.charybdis.swim_slow";
            } else {
               name = "animation.charybdis.swim_fast";
            }
         } else {
            name = "animation.charybdis.swim_idle";
         }
      } else if (state.isMoving()) {
         name = "animation.charybdis.fly";
      } else {
         name = "animation.charybdis.fly_idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 3, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.charybdis.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("roar", RawAnimation.begin().then("animation.charybdis.roar", LoopType.PLAY_ONCE))
               .triggerableAnim("scale", RawAnimation.begin().then("animation.charybdis.tempest_scale", LoopType.PLAY_ONCE)),
            new AnimationController(this, "rollController", 3, event -> PlayState.STOP)
               .triggerableAnim("roll", RawAnimation.begin().then("animation.charybdis.roll", LoopType.PLAY_ONCE))
               .triggerableAnim("beam", RawAnimation.begin().then("animation.charybdis.beam", LoopType.PLAY_ONCE))
               .triggerableAnim("death_swim", RawAnimation.begin().then("animation.charybdis.swim_death", LoopType.PLAY_ONCE))
               .triggerableAnim("death_fly", RawAnimation.begin().then("animation.charybdis.fly_death", LoopType.PLAY_ONCE))
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
