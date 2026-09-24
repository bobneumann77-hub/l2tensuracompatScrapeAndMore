package io.github.manasmods.tensura.entity.magic.misc;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class NonPlayerFishingHook extends Projectile {
   private final RandomSource syncronizedRandom = RandomSource.create();
   private boolean biting;
   private static final EntityDataAccessor<Integer> OUT_OF_WATER_TIME = SynchedEntityData.defineId(NonPlayerFishingHook.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HOOKED_ENTITY = SynchedEntityData.defineId(NonPlayerFishingHook.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> BITING = SynchedEntityData.defineId(NonPlayerFishingHook.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> CAN_HOOK = SynchedEntityData.defineId(NonPlayerFishingHook.class, EntityDataSerializers.BOOLEAN);
   private int life;
   private int nibble;
   private int timeUntilLured;
   private int timeUntilHooked;
   private float fishAngle;
   @Nullable
   private Entity hookedIn;
   private NonPlayerFishingHook.FishHookState currentState = NonPlayerFishingHook.FishHookState.FLYING;
   private final int luck;
   private final int lureSpeed;

   public NonPlayerFishingHook(EntityType<? extends NonPlayerFishingHook> entityType, Level level, int i, int j) {
      super(entityType, level);
      this.noCulling = true;
      this.luck = Math.max(0, i);
      this.lureSpeed = Math.max(0, j);
   }

   public NonPlayerFishingHook(EntityType<? extends NonPlayerFishingHook> entityType, Level level) {
      this(entityType, level, 0, 0);
   }

   public NonPlayerFishingHook(LivingEntity entity, ServerLevel level, ItemStack stack) {
      this(
         (EntityType<? extends NonPlayerFishingHook>)MiscEntityTypes.FISHING_HOOK.get(),
         level,
         EnchantmentHelper.getFishingLuckBonus(level, stack, entity),
         (int)(EnchantmentHelper.getFishingTimeReduction(level, stack, entity) * 20.0F)
      );
      this.setOwner(entity);
      float xRot = entity.getXRot();
      float yRot = entity.getYRot();
      float h = Mth.cos(-yRot * (float) (Math.PI / 180.0) - (float) Math.PI);
      float k = Mth.sin(-yRot * (float) (Math.PI / 180.0) - (float) Math.PI);
      float l = -Mth.cos(-xRot * (float) (Math.PI / 180.0));
      float m = Mth.sin(-xRot * (float) (Math.PI / 180.0));
      double x = entity.getX() - k * 0.3;
      double y = entity.getEyeY();
      double z = entity.getZ() - h * 0.3;
      this.moveTo(x, y, z, yRot, xRot);
      Vec3 vec3 = new Vec3(-k, Mth.clamp(-(m / l), -5.0F, 5.0F), -h);
      double length = vec3.length();
      vec3 = vec3.multiply(
         0.6 / length + this.random.triangle(0.5, 0.0103365),
         0.6 / length + this.random.triangle(0.5, 0.0103365),
         0.6 / length + this.random.triangle(0.5, 0.0103365)
      );
      this.setDeltaMovement(vec3);
      this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI));
      this.setXRot((float)(Mth.atan2(vec3.y, vec3.horizontalDistance()) * 180.0F / (float)Math.PI));
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   public NonPlayerFishingHook(LivingEntity entity, ServerLevel level, BlockPos targetWater, int luck, int lure) {
      this((EntityType<? extends NonPlayerFishingHook>)MiscEntityTypes.FISHING_HOOK.get(), level, luck, lure);
      this.setOwner(entity);
      float xRot = entity.getXRot();
      float yRot = entity.getYRot();
      double x = entity.getX() - Mth.sin(-yRot * (float) (Math.PI / 180.0) - (float) Math.PI) * 0.3;
      double y = entity.getEyeY();
      double z = entity.getZ() - Mth.cos(-yRot * (float) (Math.PI / 180.0) - (float) Math.PI) * 0.3;
      this.moveTo(x, y, z, yRot, xRot);
      Vec3 vec3 = new Vec3(targetWater.getX() - x, targetWater.getY() + 0.5 - y, targetWater.getZ() - z).normalize();
      double length = vec3.length();
      vec3 = vec3.multiply(
         0.2 / length + this.random.triangle(0.5, 0.0103365),
         0.2 / length + this.random.triangle(0.5, 0.0103365),
         0.2 / length + this.random.triangle(0.5, 0.0103365)
      );
      this.setDeltaMovement(vec3);
      this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI));
      this.setXRot((float)(Mth.atan2(vec3.y, vec3.horizontalDistance()) * 180.0F / (float)Math.PI));
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(OUT_OF_WATER_TIME, 0);
      builder.define(HOOKED_ENTITY, 0);
      builder.define(BITING, false);
      builder.define(CAN_HOOK, false);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
      if (HOOKED_ENTITY.equals(entityDataAccessor)) {
         int i = (Integer)this.getEntityData().get(HOOKED_ENTITY);
         this.hookedIn = i > 0 ? this.level().getEntity(i - 1) : null;
      }

      if (BITING.equals(entityDataAccessor)) {
         this.biting = (Boolean)this.getEntityData().get(BITING);
         if (this.biting) {
            this.setDeltaMovement(this.getDeltaMovement().x, -0.4F * Mth.nextFloat(this.syncronizedRandom, 0.6F, 1.0F), this.getDeltaMovement().z);
         }
      }

      super.onSyncedDataUpdated(entityDataAccessor);
   }

   private void setHookedEntity(@Nullable Entity entity) {
      if (entity == null || (Boolean)this.getEntityData().get(CAN_HOOK)) {
         if (entity != this.getOwner()) {
            this.hookedIn = entity;
            this.getEntityData().set(HOOKED_ENTITY, entity == null ? 0 : entity.getId() + 1);
         }
      }
   }

   public int getOutOfWaterTime() {
      return (Integer)this.getEntityData().get(OUT_OF_WATER_TIME);
   }

   public boolean isBiting() {
      return (Boolean)this.getEntityData().get(BITING);
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      return d < 4096.0;
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.NONE;
   }

   public boolean canUsePortal(boolean bl) {
      return false;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      Entity entity = this.getOwner();
      return new ClientboundAddEntityPacket(this, serverEntity, entity == null ? this.getId() : entity.getId());
   }

   public void lerpTo(double d, double e, double f, float g, float h, int i) {
   }

   public void tick() {
      this.syncronizedRandom.setSeed(this.getUUID().getLeastSignificantBits() ^ this.level().getGameTime());
      super.tick();
      if (this.getOwner() instanceof LivingEntity entity && (this.level().isClientSide() || !this.shouldStopFishing(entity))) {
         if (this.onGround()) {
            this.life++;
            if (this.life >= 1200) {
               this.discard();
               return;
            }
         } else {
            this.life = 0;
         }

         float fluidHeight = 0.0F;
         BlockPos blockPos = this.blockPosition();
         FluidState fluidState = this.level().getFluidState(blockPos);
         if (fluidState.is(FluidTags.WATER)) {
            fluidHeight = fluidState.getHeight(this.level(), blockPos);
         }

         boolean inWater = fluidHeight > 0.0F;
         if (this.currentState == NonPlayerFishingHook.FishHookState.FLYING) {
            if (this.hookedIn != null) {
               this.setDeltaMovement(Vec3.ZERO);
               this.currentState = NonPlayerFishingHook.FishHookState.HOOKED_IN_ENTITY;
               return;
            }

            if (inWater) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.3, 0.2, 0.3));
               this.currentState = NonPlayerFishingHook.FishHookState.BOBBING;
               return;
            }

            this.checkCollision();
         } else {
            if (this.currentState == NonPlayerFishingHook.FishHookState.HOOKED_IN_ENTITY) {
               if (this.hookedIn != null) {
                  if (!this.hookedIn.isRemoved() && this.hookedIn.level().dimension() == this.level().dimension()) {
                     this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8), this.hookedIn.getZ());
                  } else {
                     this.setHookedEntity(null);
                     this.currentState = NonPlayerFishingHook.FishHookState.FLYING;
                  }
               }

               return;
            }

            if (this.currentState == NonPlayerFishingHook.FishHookState.BOBBING) {
               Vec3 vec3 = this.getDeltaMovement();
               double d = this.getY() + vec3.y - blockPos.getY() - fluidHeight;
               if (Math.abs(d) < 0.01) {
                  d += Math.signum(d) * 0.1;
               }

               this.setDeltaMovement(vec3.x * 0.9, vec3.y - d * this.random.nextFloat() * 0.2, vec3.z * 0.9);
               int time = this.getOutOfWaterTime();
               if (inWater) {
                  this.getEntityData().set(OUT_OF_WATER_TIME, Math.max(0, time - 1));
                  if (this.biting) {
                     this.setDeltaMovement(
                        this.getDeltaMovement().add(0.0, -0.1 * this.syncronizedRandom.nextFloat() * this.syncronizedRandom.nextFloat(), 0.0)
                     );
                  }

                  if (!this.level().isClientSide) {
                     this.catchingFish(blockPos);
                  }
               } else {
                  this.getEntityData().set(OUT_OF_WATER_TIME, Math.min(10, time + 1));
               }
            }
         }

         if (!fluidState.is(FluidTags.WATER)) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03, 0.0));
         }

         this.move(MoverType.SELF, this.getDeltaMovement());
         this.updateRotation();
         if (this.currentState == NonPlayerFishingHook.FishHookState.FLYING && (this.onGround() || this.horizontalCollision)) {
            this.setDeltaMovement(Vec3.ZERO);
         }

         this.setDeltaMovement(this.getDeltaMovement().scale(0.92));
         this.reapplyPosition();
      }
   }

   private boolean shouldStopFishing(LivingEntity caster) {
      if (!caster.isRemoved() && caster.isAlive()) {
         if (!caster.getMainHandItem().is(TensuraItemTags.FISHING_RODS) && !caster.getOffhandItem().is(TensuraItemTags.FISHING_RODS)) {
            return this.remove();
         }

         double distance = this.distanceTo(caster);
         return distance > 5.0 ? this.remove() : false;
      } else {
         return this.remove();
      }
   }

   private boolean remove() {
      this.discard();
      return true;
   }

   private void checkCollision() {
      HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      this.hitTargetOrDeflectSelf(hitResult);
   }

   protected boolean canHitEntity(Entity entity) {
      return super.canHitEntity(entity) || entity.isAlive() && entity instanceof ItemEntity;
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      if (!this.level().isClientSide) {
         this.setHookedEntity(entityHitResult.getEntity());
      }
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.setDeltaMovement(this.getDeltaMovement().normalize().scale(blockHitResult.distanceTo(this)));
   }

   private void catchingFish(BlockPos blockPos) {
      ServerLevel serverLevel = (ServerLevel)this.level();
      int i = 1;
      BlockPos above = blockPos.above();
      if (this.random.nextFloat() < 0.25F && this.level().isRainingAt(above)) {
         i++;
      }

      if (this.random.nextFloat() < 0.5F && !this.level().canSeeSky(above)) {
         i--;
      }

      if (this.nibble > 0) {
         this.nibble--;
         if (this.nibble <= 0) {
            this.timeUntilLured = 0;
            this.timeUntilHooked = 0;
            this.getEntityData().set(BITING, false);
         }
      } else if (this.timeUntilHooked > 0) {
         this.timeUntilHooked -= i;
         if (this.timeUntilHooked > 0) {
            this.fishAngle = this.fishAngle + (float)this.random.triangle(0.0, 9.188);
            float f = this.fishAngle * (float) (Math.PI / 180.0);
            float g = Mth.sin(f);
            float h = Mth.cos(f);
            double d = this.getX() + g * this.timeUntilHooked * 0.1F;
            double e = Mth.floor(this.getY()) + 1.0F;
            double j = this.getZ() + h * this.timeUntilHooked * 0.1F;
            BlockState blockState = serverLevel.getBlockState(BlockPos.containing(d, e - 1.0, j));
            if (blockState.is(Blocks.WATER)) {
               if (this.random.nextFloat() < 0.15F) {
                  serverLevel.sendParticles(ParticleTypes.BUBBLE, d, e - 0.1F, j, 1, g, 0.1, h, 0.0);
               }

               float k = g * 0.04F;
               float l = h * 0.04F;
               serverLevel.sendParticles(ParticleTypes.FISHING, d, e, j, 0, l, 0.01, -k, 1.0);
               serverLevel.sendParticles(ParticleTypes.FISHING, d, e, j, 0, -l, 0.01, k, 1.0);
            }
         } else {
            this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            double m = this.getY() + 0.5;
            serverLevel.sendParticles(
               ParticleTypes.BUBBLE, this.getX(), m, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), this.getBbWidth(), 0.0, this.getBbWidth(), 0.2F
            );
            serverLevel.sendParticles(
               ParticleTypes.FISHING, this.getX(), m, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), this.getBbWidth(), 0.0, this.getBbWidth(), 0.2F
            );
            this.nibble = Mth.nextInt(this.random, 20, 40);
            this.getEntityData().set(BITING, true);
         }
      } else if (this.timeUntilLured > 0) {
         this.timeUntilLured -= i;
         float f = 0.15F;
         if (this.timeUntilLured < 20) {
            f += (20 - this.timeUntilLured) * 0.05F;
         } else if (this.timeUntilLured < 40) {
            f += (40 - this.timeUntilLured) * 0.02F;
         } else if (this.timeUntilLured < 60) {
            f += (60 - this.timeUntilLured) * 0.01F;
         }

         if (this.random.nextFloat() < f) {
            float g = Mth.nextFloat(this.random, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
            float h = Mth.nextFloat(this.random, 25.0F, 60.0F);
            double d = this.getX() + Mth.sin(g) * h * 0.1;
            double e = Mth.floor(this.getY()) + 1.0F;
            double j = this.getZ() + Mth.cos(g) * h * 0.1;
            BlockState blockState = serverLevel.getBlockState(BlockPos.containing(d, e - 1.0, j));
            if (blockState.is(Blocks.WATER)) {
               serverLevel.sendParticles(ParticleTypes.SPLASH, d, e, j, 2 + this.random.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
            }
         }

         if (this.timeUntilLured <= 0) {
            this.fishAngle = Mth.nextFloat(this.random, 0.0F, 360.0F);
            this.timeUntilHooked = Mth.nextInt(this.random, 20, 80);
         }
      } else {
         this.timeUntilLured = Mth.nextInt(this.random, 100, 600);
         this.timeUntilLured = this.timeUntilLured - this.lureSpeed;
      }
   }

   public int retrieve(ItemStack itemStack) {
      if (!this.level().isClientSide && this.getOwner() instanceof LivingEntity owner && !this.shouldStopFishing(owner)) {
         int i = 0;
         if (this.hookedIn != null) {
            this.pullEntity(this.hookedIn);
            this.level().broadcastEntityEvent(this, (byte)31);
            i = this.hookedIn instanceof ItemEntity ? 3 : 5;
         } else if (this.nibble > 0) {
            LootParams lootParams = new net.minecraft.world.level.storage.loot.LootParams.Builder((ServerLevel)this.level())
               .withParameter(LootContextParams.ORIGIN, this.position())
               .withParameter(LootContextParams.TOOL, itemStack)
               .withParameter(LootContextParams.THIS_ENTITY, this)
               .withLuck((float)(this.luck + owner.getAttributeValue(Attributes.LUCK)))
               .create(LootContextParamSets.FISHING);
            LootTable lootTable = this.level().getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);

            for (ItemStack itemStack2 : lootTable.getRandomItems(lootParams)) {
               ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemStack2);
               double d = owner.getX() - this.getX();
               double e = owner.getY() - this.getY();
               double f = owner.getZ() - this.getZ();
               itemEntity.setDeltaMovement(d * 0.1, e * 0.1 + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08, f * 0.1);
               this.level().addFreshEntity(itemEntity);
            }

            i = 1;
         }

         if (this.onGround()) {
            i = 2;
         }

         this.discard();
         return i;
      } else {
         return 0;
      }
   }

   public void handleEntityEvent(byte b) {
      if (b == 31 && this.level().isClientSide && this.hookedIn instanceof Player player && player.isLocalPlayer()) {
         this.pullEntity(this.hookedIn);
      }

      super.handleEntityEvent(b);
   }

   protected void pullEntity(Entity entity) {
      Entity entity2 = this.getOwner();
      if (entity2 != null) {
         Vec3 vec3 = new Vec3(entity2.getX() - this.getX(), entity2.getY() - this.getY(), entity2.getZ() - this.getZ()).scale(0.1);
         entity.setDeltaMovement(entity.getDeltaMovement().add(vec3));
      }
   }

   @Generated
   public int getNibble() {
      return this.nibble;
   }

   enum FishHookState {
      FLYING,
      HOOKED_IN_ENTITY,
      BOBBING;
   }
}
