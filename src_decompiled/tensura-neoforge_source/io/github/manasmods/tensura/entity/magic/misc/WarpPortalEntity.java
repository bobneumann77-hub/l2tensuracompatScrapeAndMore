package io.github.manasmods.tensura.entity.magic.misc;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Portal.Transition;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WarpPortalEntity extends TensuraProjectile implements GeoEntity, Portal {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(WarpPortalEntity.class);
   protected static final EntityDataAccessor<Direction> DIRECTION = SynchedEntityData.defineId(WarpPortalEntity.class, EntityDataSerializers.DIRECTION);
   private static final EntityDataAccessor<Optional<UUID>> DESTINATION_PORTAL = SynchedEntityData.defineId(
      WarpPortalEntity.class, EntityDataSerializers.OPTIONAL_UUID
   );
   private static final EntityDataAccessor<Integer> CHARGE_TICK = SynchedEntityData.defineId(WarpPortalEntity.class, EntityDataSerializers.INT);
   private static final Predicate<Entity> WARP_FILTER = entity -> !entity.noPhysics
      && !entity.isSpectator()
      && entity.canUsePortal(false)
      && !entity.getType().is(TensuraEntityTags.NO_FORCED_WARP);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private WarpPoint destination;
   private AABB cachedBoundingBox;
   private double cachedBboxX = Double.NaN;
   private double cachedBboxY = Double.NaN;
   private double cachedBboxZ = Double.NaN;
   private Direction cachedBboxDirection;
   private float cachedBboxSize = -1.0F;

   public WarpPortalEntity(EntityType<? extends WarpPortalEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setLife(400);
      this.setSize(3.0F);
   }

   public WarpPortalEntity(Level level, Entity entity) {
      this((EntityType<? extends WarpPortalEntity>)MiscEntityTypes.WARP_PORTAL.get(), level);
      this.setOwner(entity);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DIRECTION, Direction.DOWN);
      builder.define(DESTINATION_PORTAL, Optional.empty());
      builder.define(CHARGE_TICK, 40);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Direction", this.getFacingDirection().get3DDataValue());
      if (this.getDestinationPortalId() != null) {
         compound.putUUID("DestinationPortal", this.getDestinationPortalId());
      }

      if (this.destination != null) {
         compound.put("Destination", this.destination.serialize(this.registryAccess()));
      }

      compound.putInt("ChargeTick", this.getChargeTick());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFacingDirection(Direction.from3DDataValue(compound.getInt("Direction")));
      if (compound.hasUUID("DestinationPortal")) {
         this.setDestinationPortalId(compound.getUUID("DestinationPortal"));
      }

      if (compound.contains("Destination")) {
         this.destination = WarpPoint.fromNBT(compound.getCompound("Destination"), this.registryAccess());
      }

      this.setChargeTick(compound.getInt("ChargeTick"));
   }

   public Direction getFacingDirection() {
      return (Direction)this.entityData.get(DIRECTION);
   }

   public void setFacingDirection(Direction facing) {
      this.entityData.set(DIRECTION, facing);
   }

   public WarpPortalEntity getDestinationPortal(ServerLevel serverLevel) {
      UUID id = this.getDestinationPortalId();
      return id != null ? (WarpPortalEntity)serverLevel.getEntity(id) : null;
   }

   @Nullable
   public UUID getDestinationPortalId() {
      return (UUID)((Optional)this.entityData.get(DESTINATION_PORTAL)).orElse(null);
   }

   public void setDestinationPortalId(@Nullable UUID uniqueId) {
      this.entityData.set(DESTINATION_PORTAL, Optional.ofNullable(uniqueId));
   }

   public int getChargeTick() {
      return (Integer)this.entityData.get(CHARGE_TICK);
   }

   public void setChargeTick(int tick) {
      this.entityData.set(CHARGE_TICK, tick);
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      return false;
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return this.getType().getDimensions();
   }

   @NotNull
   public AABB makeBoundingBox() {
      Direction direction = this.getFacingDirection();
      float size = this.getSize() / 2.0F;
      if (this.cachedBoundingBox != null
         && direction == this.cachedBboxDirection
         && size == this.cachedBboxSize
         && this.cachedBboxX == this.getX()
         && this.cachedBboxY == this.getY()
         && this.cachedBboxZ == this.getZ()) {
         return this.cachedBoundingBox;
      }

      float xOff = 0.2F;
      float yOff = 0.2F;
      float zOff = 0.2F;
      switch (direction) {
         case NORTH:
         case SOUTH:
            xOff = size;
            yOff = size;
            break;
         case EAST:
         case WEST:
            zOff = size;
            yOff = size;
            break;
         case UP:
         case DOWN:
            xOff = size;
            zOff = size;
      }

      AABB result = new AABB(this.getX() - xOff, this.getY() - yOff, this.getZ() - zOff, this.getX() + xOff, this.getY() + yOff, this.getZ() + zOff);
      this.cachedBoundingBox = result;
      this.cachedBboxX = this.getX();
      this.cachedBboxY = this.getY();
      this.cachedBboxZ = this.getZ();
      this.cachedBboxDirection = direction;
      this.cachedBboxSize = size;
      return result;
   }

   public boolean canUsePortal(boolean bl) {
      return false;
   }

   @Nullable
   public DimensionTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos) {
      entity.resetFallDistance();
      WarpPoint point = this.getDestination();
      this.level().playSound(null, this.blockPosition(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      Vec3 delta = entity.getDeltaMovement();
      point.warp(entity, this.getOwner(), false, true);
      double deltaY = delta.y;
      if (this.getFacingDirection().getAxis() == Axis.Y) {
         deltaY *= 0.1F;
      }

      entity.setDeltaMovement(delta.x, deltaY, delta.z);
      entity.hurtMarked = true;
      this.level().playSound(null, point.getX(), point.getY(), point.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      entity.portalProcess = null;
      return null;
   }

   @NotNull
   public Transition getLocalTransition() {
      return Transition.CONFUSION;
   }

   public int getPortalTransitionTime(ServerLevel serverLevel, Entity entity) {
      return this.getChargeTick();
   }

   @Override
   public void remove() {
      this.closeDestinationPortal();
      this.discard();
   }

   public void createDestinationPortal() {
      WarpPoint point = this.getDestination();
      Level level = this.level();
      if (point.getDimension() != this.level().dimension()) {
         MinecraftServer server = this.level().getServer();
         if (server == null) {
            return;
         }

         ServerLevel serverLevel = server.getLevel(point.getDimension());
         if (serverLevel == null) {
            return;
         }

         level = serverLevel;
      }

      WarpPortalEntity portal = new WarpPortalEntity(level, this.getOwner());
      portal.setFacingDirection(this.getFacingDirection().getOpposite());
      portal.setLife(this.getLife());
      portal.setSize(this.getSize());
      portal.setChargeTick(this.getChargeTick());
      portal.setPos(point.getX(), point.getY() + portal.getSize() / 2.0F, point.getZ());
      portal.setDestination(new WarpPoint(point.getName(), this.getX(), this.getY() + this.getSize() / 2.0F, this.getZ(), this.level().dimension()));
      portal.setDestinationPortalId(this.getUUID());
      this.setDestinationPortalId(portal.getUUID());
      level.addFreshEntity(portal);
      portal.reapplyPosition();
      portal.triggerAnim("controller", "start");
   }

   public void closeDestinationPortal() {
      if (!this.level().isClientSide()) {
         WarpPoint point = this.getDestination();
         ServerLevel level = (ServerLevel)this.level();
         if (point.getDimension() != this.level().dimension()) {
            MinecraftServer server = this.level().getServer();
            if (server == null) {
               return;
            }

            ServerLevel serverLevel = server.getLevel(point.getDimension());
            if (serverLevel == null) {
               return;
            }

            level = serverLevel;
         }

         level.getChunk((int)point.getX() >> 4, (int)point.getZ() >> 4, ChunkStatus.FULL, true);
         WarpPortalEntity portal = this.getDestinationPortal(level);
         if (portal != null && portal.getLife() - portal.getAge() > 55) {
            portal.setRemoveIn(55);
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      Level level = this.level();
      if (!level.isClientSide() && this.getDestinationPortalId() == null) {
         this.createDestinationPortal();
      }

      RandomSource random = this.getRandom();
      if (random.nextInt(100) == 0) {
         level.playLocalSound(
            this.getX() + 0.5,
            this.getY() + 0.5,
            this.getZ() + 0.5,
            SoundEvents.PORTAL_AMBIENT,
            TensuraSkill.ABILITY_SOUND,
            0.5F,
            random.nextFloat() * 0.4F + 0.8F,
            false
         );
      }

      int age = this.getAge();
      if (age >= 10 && this.getLife() - age >= 10) {
         BlockPos pos = this.blockPosition();

         for (Entity entity : level.getEntities(this, this.getBoundingBox(), WARP_FILTER)) {
            setInsidePortal(entity, this, pos, 10);
         }
      }
   }

   public static void setInsidePortal(Entity entity, Portal portal, BlockPos blockPos, int cooldown) {
      if (cooldown > 0 && entity.isOnPortalCooldown()) {
         entity.setPortalCooldown(cooldown);
      } else if (entity.portalProcess != null && entity.portalProcess.isSamePortal(portal)) {
         entity.portalProcess.updateEntryPosition(blockPos.immutable());
         entity.portalProcess.setAsInsidePortalThisTick(true);
      } else {
         entity.portalProcess = new PortalProcessor(portal, blockPos.immutable());
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(
               this,
               "loopController",
               0,
               event -> this.getLife() - this.getAge() < 55
                  ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.warp_portal.end"))
                  : event.setAndContinue(RawAnimation.begin().thenLoop("animation.warp_portal.loop"))
            ),
            new AnimationController(this, "controller", 0, event -> PlayState.STOP)
               .triggerableAnim("start", RawAnimation.begin().then("animation.warp_portal.start", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   public WarpPoint getDestination() {
      return this.destination;
   }

   @Generated
   public void setDestination(WarpPoint destination) {
      this.destination = destination;
   }
}
