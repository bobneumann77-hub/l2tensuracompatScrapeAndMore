package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.SpittingRangedMonsterAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.FindNearestBlock;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.LayEggs;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetWalkTargetToSpecificBlock;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.navigator.ClimbingNavigator;
import io.github.manasmods.tensura.entity.projectile.MonsterSpitProjectile;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IClimbing;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.entity.template.subclass.SpittingRangedMonster;
import io.github.manasmods.tensura.handler.AttributeHandler;
import io.github.manasmods.tensura.race.RaceHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.Color;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;
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

public class HellCaterpillarEntity extends TensuraTamableEntity implements GeoEntity, SmartBrainOwner<HellCaterpillarEntity>, IClimbing, SpittingRangedMonster {
   private static final EntityDataAccessor<Boolean> GEHENNA = SynchedEntityData.defineId(HellCaterpillarEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> GRASS_EATEN = SynchedEntityData.defineId(HellCaterpillarEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> COCOONING = SynchedEntityData.defineId(HellCaterpillarEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> COCOONED = SynchedEntityData.defineId(HellCaterpillarEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> COCOON_TICKS = SynchedEntityData.defineId(HellCaterpillarEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(HellCaterpillarEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Direction> FACE_ATTACHED = SynchedEntityData.defineId(HellCaterpillarEntity.class, EntityDataSerializers.DIRECTION);
   private static final Direction[] DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public float attachChangeProgress = 0.0F;
   public float prevAttachChangeProgress = 0.0F;
   public Direction prevAttachDir = Direction.DOWN;

   public HellCaterpillarEntity(EntityType<? extends HellCaterpillarEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @NotNull
   @Override
   protected PathNavigation createNavigation(Level level) {
      return new ClimbingNavigator(this, level);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 8.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.MOVEMENT_SPEED, 0.15F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.2)
         .add(Attributes.JUMP_STRENGTH, 1.25)
         .add(Attributes.STEP_HEIGHT, 3.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(GEHENNA, Boolean.FALSE);
      builder.define(GRASS_EATEN, 0);
      builder.define(COCOONING, Boolean.FALSE);
      builder.define(COCOONED, Boolean.FALSE);
      builder.define(COCOON_TICKS, 0);
      builder.define(CLIMBING, Boolean.FALSE);
      builder.define(FACE_ATTACHED, Direction.DOWN);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Gehenna", this.isGehenna());
      compound.putInt("GrassEaten", this.getGrassEaten());
      compound.putBoolean("Cocooning", this.isCocooning());
      compound.putBoolean("Cocooned", this.isCocooned());
      compound.putInt("CocoonTicks", this.getCocoonTicks());
      compound.putBoolean("Climbing", this.isClimbing());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setGehenna(compound.getBoolean("Gehenna"));
      this.setGrassEaten(compound.getInt("GrassEaten"));
      this.setCocooning(compound.getBoolean("Cocooning"));
      this.setCocooned(compound.getBoolean("Cocooned"));
      this.setCocoonTick(compound.getInt("CocoonTicks"));
      this.setClimbing(compound.getBoolean("Climbing"));
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
      super.onSyncedDataUpdated(accessor);
      if (FACE_ATTACHED.equals(accessor)) {
         this.prevAttachChangeProgress = 0.0F;
         this.attachChangeProgress = 0.0F;
      } else if (COCOONED.equals(accessor)) {
         this.refreshDimensions();
      }
   }

   @Override
   public boolean isClimbing() {
      return (Boolean)this.entityData.get(CLIMBING);
   }

   @Override
   public void setClimbing(boolean climbing) {
      this.entityData.set(CLIMBING, climbing);
   }

   public boolean onClimbable() {
      return this.isClimbing();
   }

   public boolean isGehenna() {
      return (Boolean)this.entityData.get(GEHENNA);
   }

   public void setGehenna(boolean gehenna) {
      this.entityData.set(GEHENNA, gehenna);
   }

   public int getGrassEaten() {
      return (Integer)this.entityData.get(GRASS_EATEN);
   }

   public void setGrassEaten(int grassEaten) {
      this.entityData.set(GRASS_EATEN, grassEaten);
   }

   public boolean isCocooning() {
      return (Boolean)this.entityData.get(COCOONING);
   }

   public void setCocooning(boolean cocooned) {
      this.entityData.set(COCOONING, cocooned);
   }

   public boolean isCocooned() {
      return (Boolean)this.entityData.get(COCOONED);
   }

   public void setCocooned(boolean cocooned) {
      this.entityData.set(COCOONED, cocooned);
   }

   public int getCocoonTicks() {
      return (Integer)this.entityData.get(COCOON_TICKS);
   }

   public void setCocoonTick(int cocoonTick) {
      this.entityData.set(COCOON_TICKS, cocoonTick);
   }

   public Direction getAttachmentFacing() {
      return (Direction)this.entityData.get(FACE_ATTACHED);
   }

   public boolean canFallInLove() {
      return false;
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return false;
   }

   public float getAgeScale() {
      return this.isBaby() ? 0.3F : 1.0F;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.CACTUS) || source.is(DamageTypes.SWEET_BERRY_BUSH) || super.isInvulnerableTo(source);
   }

   @Override
   public float getClimbSpeedMultiplier() {
      return 0.5F;
   }

   public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
      if (!pState.is(TensuraBlockTags.WEB_BLOCKS)) {
         super.makeStuckInBlock(pState, pMotionMultiplier);
      }
   }

   protected float getBlockSpeedFactor() {
      BlockState blockstate = this.level().getBlockState(this.blockPosition());
      return blockstate.is(TensuraBlockTags.WEB_BLOCKS) ? 1.0F : super.getBlockSpeedFactor();
   }

   protected float getBlockJumpFactor() {
      BlockState blockstate = this.level().getBlockState(this.blockPosition());
      return blockstate.is(TensuraBlockTags.WEB_BLOCKS) ? 1.0F : super.getBlockJumpFactor();
   }

   protected float getJumpPower() {
      return 0.0F;
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   @NotNull
   @Override
   public EntityDimensions getDefaultDimensions(Pose pPose) {
      EntityDimensions dimensions = super.getDefaultDimensions(pPose);
      return this.isCocooned() ? EntityDimensions.fixed(dimensions.width(), dimensions.height() * 3.0F) : dimensions;
   }

   private boolean canCocoon() {
      return !this.isBaby() && this.getGrassEaten() > 50;
   }

   private void createCocoon() {
      this.setPos(this.position().add(0.0, this.getBbHeight() * -1.0F, 0.0));
      this.setCocooned(true);
      AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
      if (speed != null) {
         speed.setBaseValue(0.0);
      }

      BrainUtils.clearMemories(this, new MemoryModuleType[]{MemoryModuleType.MEETING_POINT});
   }

   private boolean isSafeToCocoon() {
      BlockPos blockpos = this.blockPosition();
      BlockPos pos = this.getPosWithBlock(blockpos, this.level());
      if (pos == null) {
         return false;
      } else {
         return !this.level().getBlockState(blockpos.above()).is(BlockTags.LEAVES) && !this.level().getBlockState(blockpos.above()).is(BlockTags.LOGS)
            ? this.level().getBlockState(blockpos).is(BlockTags.LOGS)
            : true;
      }
   }

   @Nullable
   private BlockPos getPosWithBlock(BlockPos pPos, BlockGetter pLevel) {
      if (pLevel.getBlockState(pPos).is(BlockTags.LEAVES)) {
         return pPos;
      }

      BlockPos[] pos = new BlockPos[]{pPos.above(), pPos.west(), pPos.east(), pPos.north(), pPos.south(), pPos.above().above()};

      for (BlockPos blockpos : pos) {
         if (pLevel.getBlockState(blockpos).is(BlockTags.LEAVES)) {
            return blockpos;
         }
      }

      return null;
   }

   private boolean isValidCocoonPlace(LevelReader pLevel, BlockPos pPos) {
      ChunkAccess chunkaccess = pLevel.getChunk(
         SectionPos.blockToSectionCoord(pPos.getX()), SectionPos.blockToSectionCoord(pPos.getZ()), ChunkStatus.FULL, false
      );
      if (chunkaccess == null) {
         return false;
      }

      boolean isLeaves = chunkaccess.getBlockState(pPos).is(BlockTags.LEAVES) || chunkaccess.getBlockState(pPos).is(BlockTags.LOGS);
      if (!isLeaves) {
         return false;
      }

      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (Direction direction : Plane.HORIZONTAL) {
         mutableBlockPos.setWithOffset(pPos, direction);
         if (chunkaccess.getBlockState(mutableBlockPos).is(BlockTags.LEAVES)
            && chunkaccess.getBlockState(mutableBlockPos.below()).isAir()
            && chunkaccess.getBlockState(mutableBlockPos.below(2)).isAir()) {
            return chunkaccess.getBlockState(pPos.below()).is(BlockTags.LOGS)
               && chunkaccess.getBlockState(pPos.below(2)).is(BlockTags.LOGS)
               && chunkaccess.getBlockState(pPos.below(3)).is(BlockTags.LOGS);
         }
      }

      return false;
   }

   @Override
   public void tick() {
      super.tick();
      this.handleClimbing(this);
      if (this.isCocooned() && this.isAlive()) {
         this.setCocoonTick(this.getCocoonTicks() + 1);
         if (this.getCocoonTicks() >= 2400) {
            Level level = this.level();
            CompoundTag tag = this.saveWithoutId(new CompoundTag());
            this.discard();
            HellMothEntity moth = new HellMothEntity((EntityType<? extends HellMothEntity>)MonsterEntityTypes.HELL_MOTH.get(), level);
            moth.load(tag);
            if (level instanceof ServerLevel serverLevel) {
               moth.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(moth.blockPosition()), MobSpawnType.CONVERSION, null);
            }

            if (TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.gehennaCocoonChance, this.level().getRandom())) {
               moth.turnGehenna();
            }

            RaceHelper.applyBaseAttribute(HellMothEntity.setAttributes().build(), moth);
            moth.setHealth(moth.getMaxHealth());
            AttributeHandler.updateEntityExistence(moth, level, TensuraStorages.getExistenceFrom(moth));
            moth.setAge(-24000);
            moth.setFlying(true);
            level.levelEvent(2009, moth.blockPosition(), new Color(255, 255, 255).getRGB());
            level.playSound(null, moth.blockPosition(), SoundEvents.WOOL_FALL, SoundSource.PLAYERS, 1.0F, 1.0F);
            level.addFreshEntity(moth);
            moth.triggerAnim("loopController", "hatch");
         }
      }
   }

   @Override
   public void handleClimbing(LivingEntity entity) {
      this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.yBodyRot, this.getMaxHeadYRot());
      this.prevAttachChangeProgress = this.attachChangeProgress;
      if (this.prevAttachDir != this.getAttachmentFacing()) {
         if (this.attachChangeProgress < 5.0F) {
            this.attachChangeProgress++;
         } else if (this.attachChangeProgress >= 5.0F) {
            this.prevAttachDir = this.getAttachmentFacing();
         }
      } else {
         this.attachChangeProgress = 5.0F;
      }

      Vec3 vector3d = this.getDeltaMovement();
      if (!this.level().isClientSide()) {
         this.setClimbing(this.collidingWall(this) || this.verticalCollision && !this.onGround());
         if (this.onGround() || this.isInWaterOrBubble() || this.isInLava()) {
            this.entityData.set(FACE_ATTACHED, Direction.DOWN);
         } else if (this.verticalCollision) {
            this.entityData.set(FACE_ATTACHED, Direction.UP);
         } else {
            Direction closestDirection = Direction.DOWN;
            double closestDistance = 100.0;

            for (Direction dir : DIRECTIONS) {
               BlockPos antPos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
               BlockPos offsetPos = antPos.relative(dir);
               Vec3 offset = Vec3.atCenterOf(offsetPos);
               if (closestDistance > this.position().distanceTo(offset) && this.level().loadedAndEntityCanStandOnFace(offsetPos, this, dir.getOpposite())) {
                  closestDistance = this.position().distanceTo(offset);
                  closestDirection = dir;
               }
            }

            this.entityData.set(FACE_ATTACHED, closestDirection);
         }
      }

      if (this.getAttachmentFacing() == Direction.UP) {
         this.setNoGravity(true);
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.1, 0.0));
      } else {
         this.setNoGravity(false);
         if (this.getAttachmentFacing() != Direction.DOWN && vector3d.y < 0.0) {
            if (!this.horizontalCollision) {
               Vec3 vec = Vec3.atLowerCornerOf(this.getAttachmentFacing().getNormal());
               this.setDeltaMovement(this.getDeltaMovement().add(vec.normalize().multiply(0.1F, 0.1F, 0.1F)));
            }

            if (!this.onGround()) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.5, 1.0));
               if (this.onClimbable()) {
                  this.setDeltaMovement(vector3d.multiply(1.0, 0.5, 1.0));
               }
            }
         }
      }
   }

   @Override
   public void spitParticle(MonsterSpitProjectile projectile) {
      this.particleSpawning(projectile, ParticleTypes.SPIT, 5);
   }

   @Override
   public void performRangedAttack(@NotNull LivingEntity target, float pDistanceFactor) {
      MonsterSpitProjectile spit = new MonsterSpitProjectile(this.level(), this);
      spit.moveTo(this.getX(), this.getY() + 1.0, this.getZ(), this.getYRot(), this.getXRot());
      double d0 = target.getY();
      double d1 = target.getX() - this.getX();
      double d2 = d0 - spit.getY();
      double d3 = target.getZ() - this.getZ();
      double f = Math.sqrt(d1 * d1 + d3 * d3) * 0.2F;
      spit.shoot(d1, d2 + f, d3, 1.2F, 0.0F);
      this.level().addFreshEntity(spit);
   }

   @Override
   public void spitHit(LivingEntity pTarget) {
      if (!(pTarget instanceof HellMothEntity)) {
         if (!(pTarget instanceof HellCaterpillarEntity)) {
            if (!this.isTame()) {
               this.clearTarget();
            }

            if (this.doHurtTarget(pTarget)) {
               if (!(pTarget.getBbHeight() <= 3.0F) && !(pTarget.getBbWidth() <= 3.0F)) {
                  pTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 0, true, false, true), this);
               } else {
                  pTarget.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.WEBBED), 300, 0, true, false, true), this);
                  pTarget.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.SILENCE), 300, 0, true, false, true), this);
               }
            }
         }
      }
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.CATERPILLAR_FOOD);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return this.isFood(pStack);
   }

   @Override
   public InteractionResult handleEating(Player player, InteractionHand hand, ItemStack stack) {
      if (this.isFood(stack) && this.getHealth() >= this.getMaxHealth() && !this.canCocoon() && !this.isBaby()) {
         this.usePlayerItem(player, hand, stack);
         this.ate();
         this.setGrassEaten(this.getGrassEaten() + 1);
         this.level().playSound(null, this, SoundEvents.GRASS_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return super.handleEating(player, hand, stack);
      }
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      HellCaterpillarEntity entity = (HellCaterpillarEntity)((EntityType)MonsterEntityTypes.HELL_CATERPILLAR.get()).create(pLevel);
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

   @NotNull
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType type, @Nullable SpawnGroupData spawnGroupData
   ) {
      if (this.canSpawnSpecialVariant(type)
         && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.gehennaCaterpillarChance, serverLevelAccessor.getRandom())) {
         this.setGehenna(true);
      }

      return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, type, spawnGroupData);
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.hellCaterpillar, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.INSECT_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return (SoundEvent)TensuraSoundEvents.INSECT_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.INSECT_DEATH.get();
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<HellCaterpillarEntity>> getSensors() {
      return ObjectArrayList.of(
         new ExtendedSensor[]{
            new NearbyLivingEntitySensor(), new HurtBySensor(), new NearbyBlocksSensor().setRadius(16.0).setScanRate(entity -> entity.canCocoon() ? 20 : 100)
         }
      );
   }

   public BrainActivityGroup<HellCaterpillarEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new FloatToSurfaceOfFluid(),
            new LookAtTarget().startCondition(entity -> !entity.isCocooned()),
            new MoveToWalkTarget()
               .cooldownFor(entity -> 0)
               .startCondition(entity -> !entity.isOrderedToSit() && !entity.isCocooned())
               .stopIf(ISubordinate::isOrderedToSit)
         }
      );
   }

   public BrainActivityGroup<HellCaterpillarEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new LayEggs(40)
                     .shouldLay((entity, position) -> position.pos().closerToCenterThan(entity.position(), 2.0) && entity.isSafeToCocoon())
                     .layEggs((entity, position) -> entity.createCocoon())
                     .laySound(SoundEvents.WOOL_FALL)
                     .whenStarting(entity -> {
                        entity.triggerAnim("miscController", "cocoon");
                        entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.WEBBED), 40, 0, false, false, false));
                     })
                     .startCondition(HellCaterpillarEntity::canCocoon),
                  new FindNearestBlock()
                     .predicate((entity, pair) -> entity.isValidCocoonPlace(entity.level(), (BlockPos)pair.getFirst()))
                     .action(
                        (mob, pair) -> BrainUtils.setForgettableMemory(
                           this, MemoryModuleType.MEETING_POINT, new GlobalPos(this.level().dimension(), (BlockPos)pair.getFirst()), 200
                        )
                     )
                     .startCondition(entity -> entity.canCocoon() && !BrainUtils.hasMemory(entity, MemoryModuleType.MEETING_POINT)),
                  new SetWalkTargetToSpecificBlock()
                     .speedMod((entity, pos) -> 1.2F)
                     .setTargetPos(entity -> ((GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.MEETING_POINT)).pos())
                     .startCondition(entity -> entity.canCocoon() && BrainUtils.hasMemory(entity, MemoryModuleType.MEETING_POINT)),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false),
                  new SubordinateFollowOwner().startCondition(entity -> !entity.isOrderedToSit() && !entity.isWandering() && !entity.isCocooned()),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<HellCaterpillarEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 1.2F),
            new SpittingRangedMonsterAttack(5).attackRadius(15.0F).performAttack((entity, target) -> {
               entity.performRangedAttack(target, 1.0F);
               entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LLAMA_SPIT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }).whenStarting(entity -> entity.triggerAnim("miscController", "silk"))
         }
      );
   }

   protected PlayState loopController(AnimationState<HellCaterpillarEntity> state) {
      String name;
      if (this.isCocooned()) {
         name = "animation.hell_caterpillar.cocoon";
      } else if (this.isInSittingPose()) {
         name = "animation.hell_caterpillar.stay";
      } else if (state.isMoving()) {
         name = "animation.hell_caterpillar.walk";
      } else {
         name = "animation.hell_caterpillar.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("eat", RawAnimation.begin().then("animation.hell_caterpillar.eat", LoopType.PLAY_ONCE))
               .triggerableAnim("silk", RawAnimation.begin().then("animation.hell_caterpillar.silk", LoopType.PLAY_ONCE))
               .triggerableAnim("leap", RawAnimation.begin().then("animation.hell_caterpillar.leap", LoopType.PLAY_ONCE))
               .triggerableAnim("cocoon", RawAnimation.begin().then("animation.hell_caterpillar.make_cocoon", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
