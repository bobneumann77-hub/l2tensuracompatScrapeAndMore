package io.github.manasmods.tensura.entity.magic.misc;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.entity.magic.field.MagicExplosion;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.TensuraExplosionDamageCalculator;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.MovementEmission;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MagicLandmineEntity extends Entity {
   private static final EntityDataAccessor<Integer> DATA_EXPLOSION_RADIUS = SynchedEntityData.defineId(MagicLandmineEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<CompoundTag> INSTANCE_TAG = SynchedEntityData.defineId(
      MagicLandmineEntity.class, EntityDataSerializers.COMPOUND_TAG
   );
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private Entity cachedOwner;
   protected float damage = 0.0F;
   protected float secondaryDamage = 0.0F;
   protected int triggerTimer = -1;
   protected double apCost = 0.0;
   protected double mpCost = 0.0;
   protected int mode = 0;
   protected boolean elementalAttack = false;
   protected boolean limitedGriefing = false;
   protected MagicLandmineEntity.ExplosionType explosionType = MagicLandmineEntity.ExplosionType.VANILLA;

   public MagicLandmineEntity(EntityType<? extends MagicLandmineEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noPhysics = true;
   }

   public MagicLandmineEntity(Level pLevel, double pX, double pY, double pZ, @Nullable LivingEntity pOwner) {
      this((EntityType<? extends MagicLandmineEntity>)MiscEntityTypes.LANDMINE.get(), pLevel);
      this.setPos(pX, pY, pZ);
      this.setOwner(pOwner);
      this.noPhysics = true;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_EXPLOSION_RADIUS, 2);
      builder.define(INSTANCE_TAG, new CompoundTag());
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putFloat("Damage", this.getDamage());
      compound.putFloat("SecondaryDamage", this.getSecondaryDamage());
      compound.putInt("TriggerTimer", this.getTriggerTimer());
      compound.putShort("Radius", (short)this.getRadius());
      if (this.ownerUUID != null) {
         compound.putUUID("Owner", this.ownerUUID);
      }

      compound.putBoolean("ElementalAttack", this.isElementalAttack());
      compound.putBoolean("LimitedGriefing", this.isLimitedGriefing());
      compound.putString("ExplosionType", this.explosionType.getSerializedName());
      compound.putInt("Mode", this.getMode());
      compound.putDouble("APCost", this.getApCost());
      compound.putDouble("MPCost", this.getMpCost());
      if (this.getSkill() != null) {
         compound.put("skill", (Tag)this.entityData.get(INSTANCE_TAG));
      } else if (compound.contains("skill")) {
         compound.remove("skill");
      }
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.setDamage(compound.getFloat("Damage"));
      this.setSecondaryDamage(compound.getFloat("SecondaryDamage"));
      this.setTriggerTimer(compound.getInt("TriggerTimer"));
      this.setRadius(compound.getShort("Radius"));
      if (compound.hasUUID("Owner")) {
         this.ownerUUID = compound.getUUID("Owner");
      }

      this.setElementalAttack(compound.getBoolean("ElementalAttack"));
      this.setLimitedGriefing(compound.getBoolean("LimitedGriefing"));
      this.setExplosionType(MagicLandmineEntity.ExplosionType.valueOf(compound.getString("ExplosionType").toUpperCase()));
      this.setMode(compound.getInt("Mode"));
      this.setApCost(compound.getDouble("APCost"));
      this.setMpCost(compound.getDouble("MPCost"));
      if (compound.contains("skill") && compound.get("skill") instanceof CompoundTag tag) {
         this.entityData.set(INSTANCE_TAG, tag);
      }
   }

   public void setRadius(int pLife) {
      this.entityData.set(DATA_EXPLOSION_RADIUS, pLife);
   }

   public int getRadius() {
      return (Integer)this.entityData.get(DATA_EXPLOSION_RADIUS);
   }

   public void setOwner(@Nullable Entity pOwner) {
      if (pOwner != null) {
         this.ownerUUID = pOwner.getUUID();
         this.cachedOwner = pOwner;
      }
   }

   @Nullable
   public Entity getOwner() {
      if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
         return this.cachedOwner;
      } else if (this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
         this.cachedOwner = serverLevel.getEntity(this.ownerUUID);
         return this.cachedOwner;
      } else {
         return null;
      }
   }

   protected boolean ownedBy(Entity pEntity) {
      return pEntity.getUUID().equals(this.ownerUUID);
   }

   public ManasSkillInstance getSkill() {
      return ((CompoundTag)this.entityData.get(INSTANCE_TAG)).isEmpty() ? null : ManasSkillInstance.fromNBT((CompoundTag)this.entityData.get(INSTANCE_TAG));
   }

   public void setSkill(@Nullable ManasSkillInstance instance) {
      this.entityData.set(INSTANCE_TAG, instance == null ? new CompoundTag() : instance.toNBT());
   }

   public void setSkill(LivingEntity entity, ManasSkillInstance instance, TensuraSkill skill, int mode) {
      this.setSkill(entity, instance, skill, mode, 1.0F);
   }

   public void setSkill(LivingEntity entity, ManasSkillInstance instance, TensuraSkill skill, int mode, float costMultiplier) {
      this.setSkill(instance);
      this.setApCost(skill.getAuraCost(entity, instance, mode) * costMultiplier);
      this.setMpCost(skill.getMagiculeCost(entity, instance, mode) * costMultiplier);
   }

   protected MovementEmission getMovementEmission() {
      return MovementEmission.NONE;
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   public boolean isPushable() {
      return true;
   }

   public void tick() {
      if (!this.level().isClientSide()) {
         if (this.getTriggerTimer() > 0 && --this.triggerTimer == 0) {
            this.trigger();
         }

         if (this.tickCount % 5 == 0) {
            if (this.getOwner() instanceof ServerPlayer player) {
               double var8 = this.random.nextGaussian() * 0.02;
               double d1 = this.random.nextGaussian() * 0.02;
               double d2 = this.random.nextGaussian() * 0.02;
               TensuraParticleHelper.spawnParticlesToOnePLayer(
                  player,
                  DustParticleOptions.REDSTONE,
                  this.getRandomX(0.5),
                  this.getY(1.5 * this.random.nextDouble() - 0.5),
                  this.getRandomZ(0.5),
                  0,
                  var8,
                  d1,
                  d2,
                  1.0,
                  false
               );
            }
         }
      }
   }

   public void push(Entity pEntity) {
      if (this.targetNotAlly(pEntity)) {
         this.trigger();
      }
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      Entity pEntity = pSource.getEntity();
      if (pEntity != null) {
         if (this.targetNotAlly(pEntity)) {
            this.trigger();
         } else if (this.getOwner() instanceof Player player && pSource.getDirectEntity() == player) {
            this.discard();
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }

      return false;
   }

   public InteractionResult interact(Player player, InteractionHand pHand) {
      if (this.targetNotAlly(player)) {
         this.trigger();
      } else if (this.getOwner() == player) {
         this.discard();
         player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      return InteractionResult.SUCCESS;
   }

   public boolean targetNotAlly(Entity entity) {
      if (this.getOwner() == null) {
         return true;
      } else {
         return entity.isAlliedTo(this.getOwner()) ? false : entity != this.getOwner();
      }
   }

   public void trigger() {
      this.trigger(this.getX(), this.getY(), this.getZ());
   }

   public void trigger(double x, double y, double z) {
      switch (this.getExplosionType()) {
         case VANILLA:
            EffectStorage.setCameraShake(this, this.getRadius(), this.getRadius() / 500.0F, this.getRadius() / 3);
            break;
         case MAGIC_VISUAL: {
            MagicExplosion explosion = new MagicExplosion(this.level(), this.getOwner());
            explosion.setVisualOnly(true);
            explosion.setTickEachHit(1);
            explosion.setLife(50);
            explosion.setPos(x, y, z);
            explosion.setSize(this.getRadius());
            this.level().addFreshEntity(explosion);
            break;
         }
         case MAGIC_FULL: {
            MagicExplosion explosion = new MagicExplosion(this.level(), this.getOwner());
            explosion.setPos(x, y, z);
            explosion.setTickEachHit(1);
            explosion.setLife(50);
            explosion.setDamage(this.getDamage());
            explosion.setSecondaryDamage(this.getSecondaryDamage());
            explosion.setElementalAttack(this.isElementalAttack());
            explosion.setSize(this.getRadius());
            explosion.setSkill(this.getSkill());
            explosion.setMode(this.getMode());
            explosion.setApCost(this.getApCost());
            explosion.setMpCost(this.getMpCost());
            explosion.setLimitedGriefing(this.isLimitedGriefing());
            this.level().addFreshEntity(explosion);
            this.discard();
            return;
         }
      }

      if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
         .grief(this.getSkill(), this.level(), this.getOwner(), x, y, z)
         .isFalse()) {
         this.explode(x, y, z, false);
         ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker()).grief(this.getSkill(), this.level(), this.getOwner(), x, y, z);
      }

      this.discard();
   }

   protected void explode(double x, double y, double z, boolean fire) {
      TensuraExplosionDamageCalculator calculator = new TensuraExplosionDamageCalculator(this.shouldGrief(), true, Optional.of(1.0F));
      this.level()
         .explode(
            this.getOwner(),
            this.getExplosionDamageSource(),
            calculator,
            x,
            y,
            z,
            this.getRadius(),
            fire,
            this.getExplosionInteraction(),
            ParticleTypes.EXPLOSION,
            ParticleTypes.EXPLOSION_EMITTER,
            SoundEvents.GENERIC_EXPLODE
         );
   }

   public DamageSource getExplosionDamageSource() {
      return Explosion.getDefaultDamageSource(this.level(), this.getOwner())
         .tensura$setAbilityInstance(this.getSkill())
         .tensura$setAbilityMode(this.getMode())
         .tensura$setMagiculeCost(this.getMpCost())
         .tensura$setAuraCost(this.getApCost());
   }

   protected ExplosionInteraction getExplosionInteraction() {
      return this.getOwner() instanceof Player ? ExplosionInteraction.BLOCK : ExplosionInteraction.MOB;
   }

   protected boolean shouldGrief() {
      return this.getOwner() != null && !this.getOwner().getType().equals(EntityType.PLAYER)
         ? this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
         : TensuraGameRules.canSkillGrief(this.level());
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      Entity entity = this.getOwner();
      return new ClientboundAddEntityPacket(this, serverEntity, entity == null ? 0 : entity.getId());
   }

   public void recreateFromPacket(ClientboundAddEntityPacket clientboundAddEntityPacket) {
      super.recreateFromPacket(clientboundAddEntityPacket);
      Entity entity = this.level().getEntity(clientboundAddEntityPacket.getData());
      if (entity != null) {
         this.setOwner(entity);
      }
   }

   @Generated
   public float getDamage() {
      return this.damage;
   }

   @Generated
   public void setDamage(float damage) {
      this.damage = damage;
   }

   @Generated
   public float getSecondaryDamage() {
      return this.secondaryDamage;
   }

   @Generated
   public void setSecondaryDamage(float secondaryDamage) {
      this.secondaryDamage = secondaryDamage;
   }

   @Generated
   public int getTriggerTimer() {
      return this.triggerTimer;
   }

   @Generated
   public void setTriggerTimer(int triggerTimer) {
      this.triggerTimer = triggerTimer;
   }

   @Generated
   public double getApCost() {
      return this.apCost;
   }

   @Generated
   public void setApCost(double apCost) {
      this.apCost = apCost;
   }

   @Generated
   public double getMpCost() {
      return this.mpCost;
   }

   @Generated
   public void setMpCost(double mpCost) {
      this.mpCost = mpCost;
   }

   @Generated
   public int getMode() {
      return this.mode;
   }

   @Generated
   public void setMode(int mode) {
      this.mode = mode;
   }

   @Generated
   public boolean isElementalAttack() {
      return this.elementalAttack;
   }

   @Generated
   public void setElementalAttack(boolean elementalAttack) {
      this.elementalAttack = elementalAttack;
   }

   @Generated
   public boolean isLimitedGriefing() {
      return this.limitedGriefing;
   }

   @Generated
   public void setLimitedGriefing(boolean limitedGriefing) {
      this.limitedGriefing = limitedGriefing;
   }

   @Generated
   public MagicLandmineEntity.ExplosionType getExplosionType() {
      return this.explosionType;
   }

   @Generated
   public void setExplosionType(MagicLandmineEntity.ExplosionType explosionType) {
      this.explosionType = explosionType;
   }

   public enum ExplosionType implements StringRepresentable {
      VANILLA("vanilla"),
      MAGIC_VISUAL("magic_visual"),
      MAGIC_FULL("magic_full");

      private final String name;

      @NotNull
      public String getSerializedName() {
         return this.name;
      }

      @Generated
      ExplosionType(final String name) {
         this.name = name;
      }

      @Generated
      public String getName() {
         return this.name;
      }
   }
}
