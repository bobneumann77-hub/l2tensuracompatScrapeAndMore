package io.github.manasmods.tensura.entity.human;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.magic.spiritual.earth.EarthSpikesMagic;
import io.github.manasmods.tensura.ability.magic.summon.ISummoning;
import io.github.manasmods.tensura.ability.skill.unique.ChosenOneSkill;
import io.github.manasmods.tensura.ability.skill.unique.CookSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.MoveOrFlyToWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.TeleportToEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomSwimAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.BlizzardEntity;
import io.github.manasmods.tensura.entity.magic.barrier.DisintegrationEntity;
import io.github.manasmods.tensura.entity.magic.barrier.HolyFieldEntity;
import io.github.manasmods.tensura.entity.magic.beam.SpatialRayProjectile;
import io.github.manasmods.tensura.entity.magic.field.Hellfire;
import io.github.manasmods.tensura.entity.template.GreaterSpiritEntity;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IElementalSpirit;
import io.github.manasmods.tensura.entity.template.subclass.IFlyingAmphibian;
import io.github.manasmods.tensura.entity.template.subclass.ITeleportation;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.item.weapon.TensuraSwordItem;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleType;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.magic.SummoningMagics;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ItemHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.InteractWithDoor;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HinataSakaguchiEntity extends OtherworlderEntity implements SmartBrainOwner<HinataSakaguchiEntity>, ITeleportation, IFlyingAmphibian {
   private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(HinataSakaguchiEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(HinataSakaguchiEntity.class, EntityDataSerializers.BOOLEAN);
   private boolean landNavigating = false;
   private int swimmingTick = 0;
   protected int flyingTick;
   protected boolean wasFlying;
   public int disintegrationCooldown = 0;
   public int earthJailCooldown = 0;
   private UUID disintegrationUUID = null;
   public UUID holyFieldUUID = null;
   private final List<UUID> spiritIDList = new ArrayList<>();
   private static final Double TELEPORT_RADIUS_FAR = 10.0;
   private static final Double TELEPORT_RADIUS_MID = 20.0;
   private static final Double TELEPORT_RADIUS_CLOSE = 32.0;
   private final ServerBossEvent bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossBarColor.WHITE, BossBarOverlay.NOTCHED_20)
      .setPlayBossMusic(true);

   public HinataSakaguchiEntity(EntityType<? extends HinataSakaguchiEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.initFlying(this);
      this.setPathfindingMalus(PathType.TRAPDOOR, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1.0F);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 50.0)
         .add(Attributes.ATTACK_DAMAGE, 60.0)
         .add(Attributes.MAX_HEALTH, 3000.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.FLYING_SPEED, 0.7F)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.8F)
         .add(Attributes.STEP_HEIGHT, 2.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.5)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 7.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 20.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 5.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 5.0);
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
   public boolean shouldTarget(LivingEntity target) {
      if (target == this) {
         return false;
      } else if (!target.isAlive()) {
         return false;
      } else if (target.getType().is(TensuraEntityTags.HINATA_NEUTRAL)) {
         return false;
      } else if (target instanceof Mob mob && mob.getTarget() == this) {
         return true;
      } else if (EnergyHelper.getMaxEP(target) < 10000.0) {
         return false;
      } else {
         Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(target).getRace();
         if (race.isPresent()) {
            return TensuraStorages.getExistenceFrom(target).getAlignment().equals(Alignment.MAJIN) ? true : !race.get().is(TensuraRaceTags.HUMAN_LIKE);
         } else {
            return target.getType().is(TensuraEntityTags.HERO_BOSS);
         }
      }
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(PHASE, 0);
      builder.define(FLYING, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Phase", this.getPhase());
      if (this.disintegrationUUID != null) {
         compound.putUUID("Disintegration", this.disintegrationUUID);
      }

      compound.putBoolean("Flying", this.isFlying());
      ListTag listTag = new ListTag();

      for (int i = 0; i < this.spiritIDList.size(); i++) {
         CompoundTag tag = new CompoundTag();
         tag.putUUID("UUID" + i, this.spiritIDList.get(i));
         listTag.add(tag);
      }

      compound.put("SpiritList", listTag);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }

      this.setPhase(compound.getInt("Phase"));
      if (compound.contains("Disintegration")) {
         this.disintegrationUUID = compound.getUUID("Disintegration");
      }

      this.setFlying(compound.getBoolean("Flying"));
      ListTag listTag = (ListTag)compound.get("SpiritList");
      if (listTag != null) {
         this.spiritIDList.clear();

         for (int i = 0; i < listTag.size(); i++) {
            CompoundTag spirit = (CompoundTag)listTag.get(i);
            this.spiritIDList.add(spirit.getUUID("UUID" + i));
         }
      }
   }

   public int getPhase() {
      return (Integer)this.entityData.get(PHASE);
   }

   public void setPhase(int phase) {
      this.entityData.set(PHASE, phase);
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
      return IFlyingAmphibian.super.shouldStopFlying(entity) || this.isOrderedToSit() || this.isInLove();
   }

   @Override
   public boolean canFly() {
      return this.getPhase() == 4;
   }

   @Override
   public ResourceLocation getTextureLocation() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/otherworlder/hinata_sakaguchi.png");
   }

   @Override
   public List<ManasSkill> getUniqueSkills() {
      return List.of((ManasSkill)UniqueSkills.USURPER.get(), (ManasSkill)UniqueSkills.MATHEMATICIAN.get());
   }

   public boolean isOnFire() {
      return false;
   }

   @Override
   public boolean wantsToPickUp(ItemStack pStack) {
      return false;
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   public boolean canBeAffected(MobEffectInstance instance) {
      if (instance.getEffect().is(TensuraTags.MobEffects.IGNORE_ANTI_MAGICULE)) {
         return true;
      } else if (instance.getEffect().is(TensuraTags.MobEffects.SKILL_BUFF)) {
         return true;
      } else {
         return instance.getEffect().equals(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION))
            ? this.getPhase() >= 4
            : !((MobEffect)instance.getEffect().value()).isBeneficial();
      }
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (pSource.getEntity() instanceof IElementalSpirit) {
         return false;
      }

      if (this.canActivateFaultField(pSource)) {
         return false;
      }

      if (this.shouldDodge(pSource)) {
         return false;
      }

      boolean hurt = super.hurt(pSource, pAmount);
      if (hurt) {
         if (!this.level().isClientSide() && this.disintegrationUUID != null) {
            Entity entity = ((ServerLevel)this.level()).getEntity(this.disintegrationUUID);
            if (entity instanceof DisintegrationEntity disintegration && disintegration.getAge() < 215) {
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION_EMITTER, 3.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION, 3.0);
               disintegration.remove();
               this.disintegrationCooldown = 1000;
               this.disintegrationUUID = null;
               this.level()
                  .playSound(
                     null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F
                  );
            }
         }

         if (pSource.getEntity() instanceof LivingEntity damageSource) {
            if (!damageSource.isAlive() || this.isAlliedTo(damageSource)) {
               return true;
            }

            if (damageSource instanceof Player player && (player.isCreative() || player.isSpectator())) {
               return true;
            }

            List<GreaterSpiritEntity> list = this.level()
               .getEntitiesOfClass(GreaterSpiritEntity.class, this.getBoundingBox().inflate(32.0), entity -> !entity.isTame());
            if (!list.isEmpty()) {
               list.forEach(spirit -> spirit.setTarget(damageSource));
            }
         }
      }

      return hurt;
   }

   protected void actuallyHurt(DamageSource source, float damage) {
      float multiplier = 1.0F;
      if (source.tensura$getMagicType() == Magic.MagicType.ASPECTUAL || source.tensura$getMagicType() == Magic.MagicType.SPIRITUAL) {
         multiplier *= 0.5F;
      }

      if (this.getPhase() == 4) {
         multiplier *= RaceUtils.getPhysicalAttackInputMultiplier(source);
      }

      super.actuallyHurt(source, damage * multiplier);
   }

   @Override
   public DamageSource getBaseDamageSource() {
      DamageSource source = super.getBaseDamageSource().tensura$setResistanceBypassLevel(1.0F);
      if (this.getPhase() == 4) {
         source = source.tensura$setBarrierBypassLevel(1.0F);
      }

      return source;
   }

   private boolean shouldDodge(DamageSource source) {
      if (this.level().isClientSide()) {
         return false;
      }

      if (this.getAttributeValue(Attributes.MOVEMENT_SPEED) <= 0.0) {
         return false;
      }

      if (this.shouldStopMoving()) {
         return false;
      }

      if (source.getDirectEntity() instanceof LivingEntity entity) {
         if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
         }

         if (source.isCreativePlayer()) {
            return false;
         }

         double negate = entity.getAttributeValue(TensuraAttributes.DODGE_NEGATE_CHANCE);
         if (entity.getRandom().nextFloat() * 100.0F < negate) {
            return false;
         }

         if (entity.getRandom().nextFloat() >= 0.05 * this.getPhase()) {
            return false;
         }

         this.level()
            .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(), TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
         if (this.getTarget() != null) {
            this.teleportTowards(this, this.getTarget(), 5.0);
         }

         this.invulnerableTime = 60;
         return true;
      } else {
         return false;
      }
   }

   private boolean canActivateFaultField(DamageSource source) {
      if (this.getPhase() != 1) {
         return false;
      } else if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return false;
      } else {
         return source.is(TensuraTags.DamageTypes.BYPASS_DIMENSION_FAULT) ? true : !TensuraDamageHelper.isSeveranceDamage(source, this, true);
      }
   }

   @Override
   public boolean shouldCountMotionBlock() {
      return this.getPhase() != 4 ? true : this.getTarget() != null && this.getTarget().onGround();
   }

   public boolean isPushedByFluid() {
      return false;
   }

   @Override
   public boolean prefersOnLand() {
      return true;
   }

   @Override
   public boolean canOpenDoor() {
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

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
      if (!this.isTame()) {
         this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
      }

      if (this.getPhase() == 4) {
         this.bossEvent.setColor(BossBarColor.YELLOW);
         this.bossEvent.setOverlay(BossBarOverlay.NOTCHED_12);
      }
   }

   private boolean shouldStopMoving() {
      if (this.level().isClientSide() || this.disintegrationUUID == null) {
         return this.getPhase() == 1 && this.getTarget() != null;
      } else {
         return this.getTarget() == null
            ? false
            : ((ServerLevel)this.level()).getEntity(this.disintegrationUUID) instanceof DisintegrationEntity disintegration && disintegration.getAge() < 215;
      }
   }

   protected boolean canRide(Entity entity) {
      return false;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         this.phaseHandler();
         this.handleFlying(this);
         if (this.disintegrationCooldown > 0) {
            this.disintegrationCooldown--;
         }

         if (this.earthJailCooldown > 0) {
            this.earthJailCooldown--;
         }

         if (this.tickCount % 20 == 0) {
            if (!this.isAlive()) {
               return;
            }

            if (this.getY() < this.level().getMinBuildHeight() - 100) {
               this.discard();
               return;
            }

            if (this.shouldStopMoving()) {
               return;
            }

            IExistence existence = TensuraStorages.getExistenceFrom(this);
            if (this.getHealth() >= this.getMaxHealth() && existence.getSpiritualHealth() >= this.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH)) {
               return;
            }

            if (this.tickCount % 200 == 0) {
               this.heal(100.0F);
               existence.setSpiritualHealth(existence.getSpiritualHealth() + 200.0);
               existence.markDirty();
               TensuraStorages.resetEffect(this);
               this.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false, false));
               TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.COMPOSTER, 1.0);
               this.level()
                  .playSound(
                     null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               TensuraParticleHelper.spawnServerParticles(
                  this.level(),
                  TensuraParticleUtils.getColorlessWave(0.9F, this.getBbWidth() * 3.0F, -0.5F, true),
                  this.getX(),
                  this.getY() + this.getBbHeight() * 0.33,
                  this.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  this.level(),
                  TensuraParticleUtils.getColorlessWave(0.9F, this.getBbWidth() * 3.0F, -0.5F, true),
                  this.getX(),
                  this.getY() + this.getBbHeight() * 0.66,
                  this.getZ()
               );
            } else if (this.getPhase() != 4) {
               this.heal(2.0F);
            } else {
               this.heal(10.0F);
               existence.setSpiritualHealth(existence.getSpiritualHealth() + 50.0);
            }
         }
      }
   }

   protected void phaseHandler() {
      if (this.tickCount % 60 == 0) {
         if (this.holyFieldUUID == null) {
            HolyFieldEntity holyField = new HolyFieldEntity(this.level(), this);
            holyField.setSize(25.0F);
            holyField.setLife(-1);
            holyField.setFollowOwner(true);
            holyField.setPos(this.position().add(0.0, -12.5, 0.0));
            this.level().addFreshEntity(holyField);
            this.holyFieldUUID = holyField.getUUID();
         } else {
            Entity entity = ((ServerLevel)this.level()).getEntity(this.holyFieldUUID);
            if (entity == null || !entity.getType().equals(MiscEntityTypes.HOLY_FIELD.get())) {
               HolyFieldEntity holyField = new HolyFieldEntity(this.level(), this);
               holyField.setSize(25.0F);
               holyField.setLife(-1);
               holyField.setFollowOwner(true);
               holyField.setPos(this.position().add(0.0, -12.5, 0.0));
               this.level().addFreshEntity(holyField);
               this.holyFieldUUID = holyField.getUUID();
            }
         }

         switch (this.getPhase()) {
            case 0:
               if (this.getTarget() != null) {
                  this.setPhase(this.isTame() ? 2 : 1);
                  if (this.isAlive()) {
                     this.heal(this.getMaxHealth());
                  }
               }
               break;
            case 1:
               if (this.getHealth() <= this.getMaxHealth() * 0.75 || this.getHealth() <= 750.0F) {
                  this.setPhase(3);
                  return;
               }

               if (this.spiritIDList.isEmpty()) {
                  return;
               }

               for (UUID id : List.copyOf(this.spiritIDList)) {
                  Entity entity = ((ServerLevel)this.level()).getEntity(id);
                  if (entity == null || !entity.isAlive()) {
                     this.spiritIDList.remove(id);
                  }
               }

               if (this.spiritIDList.isEmpty()) {
                  this.setPhase(2);
               }
               break;
            case 2:
               if (this.getHealth() > this.getMaxHealth() * 0.75 || this.getHealth() <= 750.0F) {
                  return;
               }

               this.setPhase(3);
               break;
            case 3:
               if (!this.isAlive()) {
                  return;
               }

               if (this.getHealth() > this.getMaxHealth() * 0.5 || this.getHealth() <= 500.0F) {
                  return;
               }

               this.enterLastPhase();
         }
      }
   }

   private void enterLastPhase() {
      this.invulnerableTime = 100;
      CookSkill.removeCookedHP(this);
      AttributeInstance shpRegen = this.getAttribute(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION);
      if (shpRegen != null) {
         shpRegen.setBaseValue(shpRegen.getBaseValue() + 30.0);
      }

      this.setHealth((float)this.getAttributeValue(Attributes.MAX_HEALTH));
      IExistence existence = TensuraStorages.getExistenceFrom(this);
      if (existence.getMagicule() <= 0.0) {
         existence.setMagicule(50.0);
      }

      double SHP = this.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
      if (existence.getSpiritualHealth() < SHP * ChosenOneSkill.CONFIG.shpMultiplier) {
         existence.setSpiritualHealth(SHP * ChosenOneSkill.CONFIG.shpMultiplier);
      }

      existence.markDirty();
      AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
      if (armor != null) {
         armor.setBaseValue(armor.getBaseValue() + 20.0);
      }

      TensuraStorages.resetEffect(this);
      this.dead = false;
      this.deathTime = 0;
      this.unsetRemoved();
      this.disintegrationCooldown = 200;
      this.setPhase(4);
      this.switchNavigator(this, this.isInWaterOrBubble(), false);
      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), ((TensuraSwordItem)TensuraToolItems.MOONLIGHT.get()).getDefaultInstance());
      this.inventory.setItem(this.getSlotId(EquipmentSlot.CHEST), ((Item)TensuraArmorItems.HOLY_ARMAMENTS_CHESTPLATE.get()).getDefaultInstance());
      this.inventory.setItem(this.getSlotId(EquipmentSlot.LEGS), ((Item)TensuraArmorItems.HOLY_ARMAMENTS_LEGGINGS.get()).getDefaultInstance());
      this.inventory.setItem(this.getSlotId(EquipmentSlot.FEET), ((Item)TensuraArmorItems.HOLY_ARMAMENTS_BOOTS.get()).getDefaultInstance());
      this.updateContainerEquipment();
      AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 6.0);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(LivingEntity.class, aabb, entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && entity != this);
      DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.HOLY_DAMAGE, this);

      for (LivingEntity target : list) {
         target.hurt(source, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
         target.setDeltaMovement(0.0, 0.2, 0.0);
         SkillHelper.knockBack(this, target, 3.0F);
      }

      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.END_ROD, 1.0);
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      TensuraParticleHelper.spawnServerParticles(
         this.level(),
         TensuraParticleUtils.getGoldWave(0.9F, this.getBbWidth() * 3.0F, -0.5F, true),
         this.getX(),
         this.getY() + this.getBbHeight() * 0.33,
         this.getZ()
      );
      TensuraParticleHelper.spawnServerParticles(
         this.level(),
         TensuraParticleUtils.getGoldWave(0.9F, this.getBbWidth() * 3.0F, -0.5F, true),
         this.getX(),
         this.getY() + this.getBbHeight() * 0.66,
         this.getZ()
      );
   }

   private <T extends GreaterSpiritEntity> void summonSpirit(
      EntityType<T> type, Vec3 pos, MagicCircleVariant variant, ParticleOptions particleOptions, int order, int summonTick
   ) {
      if (summonTick == 0) {
         MagicCircle.castMagicCircle(3.0F, 40, pos, variant, this, (ManasSkill)SummoningMagics.SUMMON_GREATER_ELEMENTAL.get(), 0, Pair.of(0.0, 2000.0));
         GreaterSpiritEntity spirit = (GreaterSpiritEntity)type.create(this.level());
         if (spirit != null) {
            spirit.setNoAi(true);
            spirit.setInvulnerable(true);
            spirit.noPhysics = true;
            spirit.setPos(pos.add(0.0, -1.5 * spirit.getBbHeight(), 0.0));
            spirit.finalizeSpawn(
               (ServerLevelAccessor)this.level(), this.level().getCurrentDifficultyAt(ObjectSelectionHelper.getBlockPos(pos)), MobSpawnType.MOB_SUMMONED, null
            );
            spirit.skipDropExperience();
            IExistence existence = TensuraStorages.getExistenceFrom(spirit);
            existence.setSummonedSecond(1200);
            existence.setSummoner(this.getUUID());
            existence.markDirty();
            AttributeInstance attack = spirit.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attack != null) {
               attack.setBaseValue(attack.getBaseValue() / 2.0);
            }

            AttributeInstance health = spirit.getAttribute(Attributes.MAX_HEALTH);
            if (health != null) {
               health.setBaseValue(health.getBaseValue() / 2.0);
            }

            spirit.setHealth(spirit.getMaxHealth());
            this.level().addFreshEntity(spirit);
            this.spiritIDList.add(spirit.getUUID());
         }
      }

      if (!this.spiritIDList.isEmpty()) {
         Entity summonUUID = ((ServerLevel)this.level()).getEntity(this.spiritIDList.get(Math.min(order, this.spiritIDList.size() - 1)));
         if (summonUUID instanceof Mob mob) {
            summonUUID.setPos(summonUUID.position().add(0.0, mob.getBbHeight() * 1.5 / 40.0, 0.0));
            TensuraParticleHelper.addServerParticlesAroundSelf(mob, particleOptions, 3.0);
            if (summonTick >= 39) {
               summonUUID.noPhysics = false;
               mob.setNoAi(false);
               mob.setInvulnerable(false);
               mob.playSound(SoundEvents.EVOKER_CAST_SPELL, 3.0F, 1.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.FLASH, 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.FLASH, 3.0);
            }
         }
      }
   }

   private void disintegration(LivingEntity target) {
      this.lookAt(Anchor.EYES, target.getEyePosition());
      if (this.disintegrationUUID == null || ((ServerLevel)this.level()).getEntity(this.disintegrationUUID) == null) {
         DisintegrationEntity disintegration = new DisintegrationEntity(this.level(), this);
         disintegration.setSize(3.0F);
         disintegration.setHeight(50.0F);
         disintegration.setLife(310);
         disintegration.setDamage(1000.0F);
         Vec3 pos = ObjectSelectionHelper.getNearestGround(target.position(), this.level(), 10.0, target);
         disintegration.setPos(pos);
         this.level().addFreshEntity(disintegration);
         this.disintegrationUUID = disintegration.getUUID();
         target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), 40, 9, false, false, false));
         target.moveTo(disintegration.position());
         target.setDeltaMovement(Vec3.ZERO);
         target.hurtMarked = true;
         if (this.getY() < pos.y - 1.0) {
            this.level().playSound(null, this.xo, this.yo, this.zo, (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            this.moveTo(ObjectSelectionHelper.getNearestGround(this.position(), this.level(), 10.0, target));
            this.playSound((SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(), 1.0F, 1.0F);
         }

         this.getNavigation().stop();
      }

      this.disintegrationCooldown = 1000;
   }

   private void meltSlash(LivingEntity target) {
      if (target.distanceTo(this) <= 20.0F) {
         this.disintegrationCooldown = 1000;
         Vec3 source = this.position().add(0.0, this.getBbHeight() / 2.0F, 0.0);
         Vec3 targetPos = target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0);
         Vec3 subtract = targetPos.subtract(source);
         Vec3 vec3 = targetPos.add(subtract.normalize().scale(20.0 - subtract.length()));
         if (target.onGround()) {
            vec3 = ObjectSelectionHelper.getNearestGround(vec3, this.level(), 10.0, this);
         }

         Vec3 offSetToTarget = vec3.subtract(source);

         for (int particleIndex = 1; particleIndex < Mth.floor(offSetToTarget.length()); particleIndex++) {
            Vec3 particlePos = source.add(offSetToTarget.normalize().scale(particleIndex));
            TensuraParticleHelper.addServerParticlesAroundPos(
               this.getRandom(), this.level(), particlePos, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get(), 1.0
            );
            TensuraParticleHelper.addServerParticlesAroundPos(this.getRandom(), this.level(), particlePos, TensuraParticleUtils.getYellowGust(), 2.0);
            double attackRange = this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 2.0;
            List<LivingEntity> list = this.level()
               .getEntitiesOfClass(
                  LivingEntity.class,
                  new AABB(ObjectSelectionHelper.getBlockPos(particlePos)).inflate(attackRange),
                  entityx -> !entityx.is(this) && !entityx.isAlliedTo(this)
               );
            if (!list.isEmpty()) {
               float amount = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 5.0F;
               DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.DISINTEGRATION, this)
                  .tensura$setBarrierBypassLevel(3.0F)
                  .tensura$setResistanceBypassLevel(2.0F)
                  .tensura$setElement(Element.HOLY);

               for (LivingEntity entity : list) {
                  if (!(entity instanceof Player player && (player.isCreative() || player.isSpectator()))) {
                     entity.hurt(damageSource, amount);
                     TensuraDamageHelper.directSpiritualHurt(entity, this, damageSource, amount);
                     MobEffectInstance effect = new MobEffectInstance(
                        TensuraMobEffects.getReference(TensuraMobEffects.DISINTEGRATING), 100, 0, false, false, false
                     );
                     TensuraMobEffect.addEffect(entity, effect, this, null);
                  }
               }
            }
         }

         this.unRide();
         this.moveTo(vec3);
         this.hasImpulse = true;
         this.swing(InteractionHand.MAIN_HAND, true);
         ItemHelper.breakItem(this.getMainHandItem(), this, EquipmentSlot.MAINHAND);
         this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), ((TensuraSwordItem)TensuraToolItems.DEAD_END_RAINBOW.get()).getDefaultInstance());
         this.updateContainerEquipment();
         this.level()
            .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   public void hellFire(LivingEntity target) {
      Vec3 pos = target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0);
      Hellfire sphere = new Hellfire(this.level(), this);
      sphere.setDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 8.0));
      sphere.setMpCost(10000.0);
      sphere.setSkill((ManasSkillInstance)SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)SpiritualMagics.HELLFIRE.get()).orElse(null));
      sphere.setLife(60);
      sphere.setSize(2.5F);
      sphere.setPos(pos.x, pos.y - sphere.getSize(), pos.z);
      this.level().addFreshEntity(sphere);
      this.playSound((SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), 10.0F, 0.95F + this.getRandom().nextFloat() * 0.1F);
      ((ServerLevel)this.level())
         .sendParticles(
            (TensuraParticleType)TensuraParticleTypes.RED_FIRE.get(),
            this.getX(),
            this.getY() + this.getBbHeight() / 2.0,
            this.getZ(),
            10,
            0.08,
            0.08,
            0.08,
            0.15
         );
   }

   private void blizzard() {
      BlizzardEntity blizzard = new BlizzardEntity(this.level(), this);
      blizzard.setLife(200);
      blizzard.setSize(15.0F);
      blizzard.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
      blizzard.setFollowOwner(true);
      blizzard.setMpCost(1000.0);
      blizzard.setSkill((ManasSkillInstance)SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)SpiritualMagics.BLIZZARD.get()).orElse(null));
      blizzard.setPos(this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ());
      this.level().addFreshEntity(blizzard);
      this.swing(InteractionHand.OFF_HAND, true);
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_ICE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }

   private void earthJail(LivingEntity target) {
      this.swing(InteractionHand.MAIN_HAND, true);
      this.earthJailCooldown = 600;
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      TensuraParticleHelper.addServerParticlesAroundSelf(target, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD_BRICKS.defaultBlockState()));
      TensuraParticleHelper.addServerParticlesAroundSelf(target, TensuraParticleUtils.getBogBubble());
      float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
      DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.EARTH_ELEMENTAL, this);
      if (target.hurt(damageSource, damage * 2.0F)) {
         if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.EARTH_ATTACK_NULLIFICATION.get())) {
            return;
         }

         int slowness = 5;
         if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.EARTH_ATTACK_RESISTANCE.get())) {
            slowness = 2;
         }

         target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), 400, slowness, true, false, true));
         target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 400, 1, true, false, true));
         target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 400, 2, true, false, true));
      }

      if (target.onGround()) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)SpiritualMagics.EARTH_SPIKES.get());
         if (optional.isEmpty()) {
            return;
         }

         EarthSpikesMagic.spawnSpike(target.position(), this, optional.get(), damage, (TensuraSkill)SpiritualMagics.EARTH_SPIKES.get(), 0);
         EarthSpikesMagic.spawnSpike(target.position().add(0.0, 0.0, 1.0), this, optional.get(), damage, (TensuraSkill)SpiritualMagics.EARTH_SPIKES.get(), 0);
         EarthSpikesMagic.spawnSpike(target.position().add(0.0, 0.0, -1.0), this, optional.get(), damage, (TensuraSkill)SpiritualMagics.EARTH_SPIKES.get(), 0);
         EarthSpikesMagic.spawnSpike(target.position().add(1.0, 0.0, 0.0), this, optional.get(), damage, (TensuraSkill)SpiritualMagics.EARTH_SPIKES.get(), 0);
         EarthSpikesMagic.spawnSpike(target.position().add(-1.0, 0.0, 0.0), this, optional.get(), damage, (TensuraSkill)SpiritualMagics.EARTH_SPIKES.get(), 0);
      }
   }

   public void aerialBlade() {
      Level level = this.level();
      level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
      this.playSound((SoundEvent)TensuraSoundEvents.CAST_WIND.get(), 5.0F, 0.95F + this.random.nextFloat() * 0.1F);
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

      for (LivingEntity target : level.getEntitiesOfClass(
         LivingEntity.class, box, living -> !living.is(this) && living.isAlive() && !living.isAlliedTo(this) && !living.hasInfiniteMaterials()
      )) {
         target.setDeltaMovement(aheadVec.subtract(target.position()).normalize().scale(2.0));
         target.hurtMarked = true;
      }

      AABB aabb = new AABB(ahead).inflate(radius);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(
            LivingEntity.class, aabb.minmax(aabb), living -> !living.is(this) && living.isAlive() && !living.isAlliedTo(this) && !living.hasInfiniteMaterials()
         );
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

   private void spatialRay() {
      SpatialRayProjectile ray = new SpatialRayProjectile(this.level(), this);
      ray.setFollowingOwner(true);
      ray.setSize(1.0F);
      ray.setLife(25);
      float damage = (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0);
      ray.setDamage(damage);
      ray.setRange(30.0F);
      ray.setPos(this.getEyePosition());
      ray.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)SpiritualMagics.SWIPE.get()));
      ray.setMpCost(1000.0);
      this.level().addFreshEntity(ray);
      this.playSound((SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), 5.0F, 0.5F);
   }

   @Override
   public boolean randomTeleport(LivingEntity entity, double pX, double pY, double pZ, boolean pBroadcastTeleport) {
      boolean randomTeleport = ITeleportation.super.randomTeleport(entity, pX, pY, pZ, pBroadcastTeleport);
      if (randomTeleport && !this.onGround()) {
         this.setFlying(true);
         this.switchNavigator(this, this.isInWaterOrBubble(), false);
      }

      return randomTeleport;
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         this.populateDefaultEquipmentSlots(this.random, pDifficulty);
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      ItemStack stack = new ItemStack((ItemLike)TensuraToolItems.DEAD_END_RAINBOW.get());
      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
      this.updateContainerEquipment();
   }

   @Override
   protected float getEquipmentDropChance(EquipmentSlot pSlot) {
      if (this.isTame()) {
         return 0.0F;
      }

      float chance = super.getEquipmentDropChance(pSlot);
      return pSlot.equals(EquipmentSlot.MAINHAND) ? Math.max(1.0F, chance) : chance;
   }

   @Override
   public void die(DamageSource source) {
      if (!source.is(DamageTypes.FELL_OUT_OF_WORLD) && !source.is(DamageTypes.GENERIC_KILL) && this.getPhase() < 4) {
         this.enterLastPhase();
      } else {
         super.die(source);
         if (!this.spiritIDList.isEmpty() && !this.level().isClientSide()) {
            for (int i = 0; i <= 4; i++) {
               if (((ServerLevel)this.level()).getEntity(this.spiritIDList.get(Math.min(i, this.spiritIDList.size() - 1))) instanceof Mob mob
                  && mob.isAlive()
                  && mob.isNoAi()) {
                  ISummoning.removeSummon(mob, TensuraStorages.getExistenceFrom(mob));
               }
            }
         }
      }
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   public List<ExtendedSensor<HinataSakaguchiEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new SleepSensor()});
   }

   public BrainActivityGroup<HinataSakaguchiEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new LookAtTarget(),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new MoveOrFlyToWalkTarget()
                     .startCondition(entity -> !entity.isOrderedToSit() && !entity.isSleeping() && !entity.shouldStopMoving())
                     .stopIf(entity -> entity.isOrderedToSit() || entity.isSleeping() || entity.shouldStopMoving()),
                  new MoveToWalkTarget()
                     .cooldownFor(entity -> 0)
                     .startCondition(entity -> !entity.isOrderedToSit() && !entity.isSleeping() && !entity.shouldStopMoving())
                     .stopIf(entity -> entity.isOrderedToSit() || entity.isSleeping() || entity.shouldStopMoving())
               }
            )
         }
      );
   }

   public BrainActivityGroup<HinataSakaguchiEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldTarget), new SubordinateFollowOwner().canTeleportOffGroundWhen(entity -> {
                     if (entity.canFly()) {
                        entity.setFlying(true);
                        return true;
                     } else {
                        return false;
                     }
                  }), TensuraBehaviourHelper.getMoveToWanderPos(), new SetPlayerLookTarget(), new SetRandomLookTarget()
               }
            ),
            new InteractWithDoor(),
            new HumanoidConsumeItem().startCondition(entity -> entity.shouldHeal()).stopIf(entity -> !entity.shouldHeal()),
            new OneRandomBehaviour(
               new ExtendedBehaviour[]{
                  new FirstApplicableBehaviour(
                     new ExtendedBehaviour[]{
                        new SetRandomFlyAndWalkTarget()
                           .verticalWeight(entity -> 1)
                           .startCondition(entity -> entity.canFly() && !entity.isOrderedToSit() && !entity.isSleeping() && !entity.shouldStopMoving()),
                        new SetRandomSwimAndWalkTarget()
                           .speedModifier((entity, pos) -> entity.isInWaterOrBubble() ? 1.5F : 1.0F)
                           .cooldownFor(entity -> 0)
                           .startCondition(entity -> !entity.isOrderedToSit() && !entity.isSleeping() && !entity.shouldStopMoving())
                     }
                  ),
                  new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
               }
            )
         }
      );
   }

   public BrainActivityGroup<HinataSakaguchiEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new TeleportToEntity()
               .following(Mob::getTarget)
               .canTeleportOffGroundWhen(HinataSakaguchiEntity::canFly)
               .teleportRadius((entity, target) -> 10)
               .teleportToTargetAfter((entity, target) -> {
                  if (entity.tickCount % 200 == 0) {
                     return TELEPORT_RADIUS_FAR;
                  } else {
                     return entity.tickCount % 20 == 0 ? TELEPORT_RADIUS_MID : TELEPORT_RADIUS_CLOSE;
                  }
               })
               .onSuccessTeleport((entity, target) -> {
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.END_ROD, 1.0);
                  entity.level()
                     .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               }),
            new StrafeTarget()
               .stopStrafingWhen(entity -> !entity.usingRangedWeapon())
               .startCondition(entity -> entity.usingRangedWeapon() && !entity.shouldStopMoving()),
            new SetWalkTargetToAttackTarget()
               .speedMod((owner, target) -> 2.0F)
               .startCondition(entity -> !entity.usingRangedWeapon() && !entity.shouldStopMoving()),
            new CustomHeldAttack()
               .minAttackRadius(0.0F)
               .maxAttackRadius(40.0F)
               .attackInterval(entity -> 0)
               .onTick(
                  (entity, target, tick) -> {
                     entity.summonSpirit(
                        (EntityType)MonsterEntityTypes.IFRIT.get(),
                        this.position().add(5.0, 0.0, 0.0),
                        MagicCircleVariant.FLAME,
                        (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(),
                        0,
                        tick
                     );
                     entity.summonSpirit(
                        (EntityType)MonsterEntityTypes.UNDINE.get(),
                        this.position().add(-5.0, 0.0, 0.0),
                        MagicCircleVariant.WATER,
                        TensuraParticleUtils.getWaterBubble(),
                        1,
                        tick
                     );
                     entity.summonSpirit(
                        (EntityType)MonsterEntityTypes.SYLPHIDE.get(),
                        this.position().add(0.0, 0.0, 5.0),
                        MagicCircleVariant.WIND,
                        TensuraParticleUtils.getGust(),
                        2,
                        tick
                     );
                     entity.summonSpirit(
                        (EntityType)MonsterEntityTypes.WAR_GNOME.get(),
                        this.position().add(0.0, 0.0, -5.0),
                        MagicCircleVariant.EARTH,
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD_BRICKS.defaultBlockState()),
                        3,
                        tick
                     );
                     entity.summonSpirit(
                        (EntityType)MonsterEntityTypes.AKASH.get(),
                        this.position().add(0.0, 5.0, 0.0),
                        MagicCircleVariant.SPACE,
                        ParticleTypes.REVERSE_PORTAL,
                        4,
                        tick
                     );
                     entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), 10.0F, 0.95F + entity.getRandom().nextFloat() * 0.1F);
                     return tick < 40;
                  }
               )
               .startCondition(entity -> entity.getPhase() == 1 && entity.spiritIDList.isEmpty()),
            new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     new CustomHeldAttack()
                        .minAttackRadius(0.0F)
                        .maxAttackRadius(32.0F)
                        .attackInterval(entity -> 60)
                        .onTick(
                           (entity, target, tick) -> {
                              double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
                              TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getGoldAura(1.0F, (float)size, -0.3F), 3, 0.03);
                              if (tick >= 80 && target.isAlive()) {
                                 IExistence existence = TensuraStorages.getExistenceFrom(target);
                                 if (existence.getSummoner() == null) {
                                    return false;
                                 }

                                 if (existence.getSummonedSecond() <= 0) {
                                    return false;
                                 }

                                 if (!((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker()).tame(target, entity, true).isFalse()
                                    )
                                  {
                                    LivingEntity owner = SubordinateHelper.getSubordinateOwner(target);
                                    MobEffectInstance mindControl = new MobEffectInstance(
                                       TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL), 6000, 0, false, false, false
                                    );
                                    TensuraMobEffect.addEffect(target, mindControl, entity, (ManasSkill)UniqueSkills.USURPER.get(), 2);
                                    if (!target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL))) {
                                       return false;
                                    }

                                    existence.setSummoner(entity.getUUID());
                                    existence.setTemporaryOwner(null);
                                    if (target instanceof Mob mob) {
                                       SubordinateHelper.removeTarget(mob);
                                    }

                                    existence.markDirty();
                                    entity.swing(InteractionHand.MAIN_HAND, true);
                                    TensuraDamageHelper.markHurt(target, entity);
                                    if (owner != null) {
                                       owner.sendSystemMessage(
                                          Component.translatable("tensura.summon.stolen", new Object[]{target.getName(), entity.getName()})
                                             .withStyle(ChatFormatting.RED)
                                       );
                                    }

                                    entity.level()
                                       .playSound(
                                          null,
                                          entity.getX(),
                                          entity.getY(),
                                          entity.getZ(),
                                          (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(),
                                          TensuraSkill.ABILITY_SOUND,
                                          1.0F,
                                          1.0F
                                       );
                                 }

                                 return false;
                              } else {
                                 return target.isAlive();
                              }
                           }
                        )
                        .startCondition(entity -> {
                           LivingEntity target = entity.getTarget();
                           if (target == null) {
                              return false;
                           }

                           IExistence existence = TensuraStorages.getExistenceFrom(target);
                           return existence.getSummoner() == null ? false : existence.getSummonedSecond() > 0;
                        }),
                     new FirstApplicableBehaviour(
                           new ExtendedBehaviour[]{
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
                                       } else if (tick >= 30 && tick % 10 == 0) {
                                          entity.aerialBlade();
                                       }

                                       return tick < 70;
                                    }
                                 )
                                 .startCondition(entity -> entity.getRandom().nextFloat() <= 0.2),
                              new CustomRangeAttack(25)
                                 .maxAttackRadius(18.0F)
                                 .attackInterval(entity -> 60)
                                 .performAttack(HinataSakaguchiEntity::hellFire)
                                 .whenStarting(
                                    entity -> {
                                       Vec3 circleVec3 = new Vec3(0.0, entity.getBbHeight() * 3.0F / 4.0F - entity.getEyeHeight(), 0.0);
                                       MagicCircle.castMagicCircle(
                                          1.0F,
                                          25,
                                          MagicCircleVariant.FLAME,
                                          entity,
                                          1.0F,
                                          circleVec3,
                                          (ManasSkill)SpiritualMagics.HELLFIRE.get(),
                                          0,
                                          Pair.of(0.0, 10000.0)
                                       );
                                    }
                                 )
                                 .startCondition(entity -> entity.getRandom().nextFloat() <= 0.05),
                              new CustomRangeAttack(25)
                                 .maxAttackRadius(18.0F)
                                 .attackInterval(entity -> 60)
                                 .performAttack((entity, target) -> entity.blizzard())
                                 .whenStarting(
                                    entity -> MagicCircle.castMagicCircle(
                                       5.0F,
                                       25,
                                       MagicCircleVariant.WATER,
                                       entity,
                                       0.0F,
                                       Vec3.ZERO,
                                       (ManasSkill)SpiritualMagics.BLIZZARD.get(),
                                       0,
                                       Pair.of(0.0, 1000.0)
                                    )
                                 )
                                 .startCondition(entity -> entity.getRandom().nextFloat() <= 0.1),
                              new CustomRangeAttack(25)
                                 .minAttackRadius(20.0F)
                                 .maxAttackRadius(48.0F)
                                 .attackInterval(entity -> 60)
                                 .performAttack(HinataSakaguchiEntity::earthJail)
                                 .whenStarting(
                                    entity -> {
                                       Vec3 circleVec3 = new Vec3(0.0, entity.getBbHeight() * 3.0F / 4.0F - entity.getEyeHeight(), 0.0);
                                       MagicCircle.castMagicCircle(
                                          1.0F,
                                          25,
                                          MagicCircleVariant.EARTH,
                                          entity,
                                          1.0F,
                                          circleVec3,
                                          (ManasSkill)SpiritualMagics.EARTH_JAIL.get(),
                                          0,
                                          Pair.of(0.0, 1000.0)
                                       );
                                    }
                                 )
                                 .startCondition(entity -> entity.earthJailCooldown <= 0 && entity.getRandom().nextFloat() <= 0.3),
                              new CustomRangeAttack(10)
                                 .maxAttackRadius(30.0F)
                                 .attackInterval(entity -> 60)
                                 .performAttack((entity, target) -> entity.spatialRay())
                                 .startCondition(entity -> entity.getRandom().nextFloat() <= 0.1)
                                 .whenStarting(
                                    entity -> {
                                       Vec3 circleVec3 = new Vec3(0.0, entity.getBbHeight() * 3.0F / 4.0F - entity.getEyeHeight(), 0.0);
                                       MagicCircle.castMagicCircle(
                                          0.75F,
                                          30,
                                          MagicCircleVariant.SPACE,
                                          entity,
                                          0.5F,
                                          circleVec3,
                                          (ManasSkill)SpiritualMagics.SWIPE.get(),
                                          0,
                                          Pair.of(0.0, 1000.0)
                                       );
                                    }
                                 ),
                              new FirstApplicableBehaviour(
                                    new ExtendedBehaviour[]{
                                       new CustomRangeAttack(40)
                                          .minAttackRadius(5.0F)
                                          .maxAttackRadius(48.0F)
                                          .attackInterval(entity -> 60)
                                          .performAttack(HinataSakaguchiEntity::meltSlash)
                                          .whenStarting(entity -> {
                                             Vec3 circleVec3 = new Vec3(0.0, entity.getBbHeight() * 3.0F / 4.0F - entity.getEyeHeight(), 0.0);
                                             MagicCircle.castMagicCircle(
                                                1.0F, 25, MagicCircleVariant.LIGHT, entity, 1.0F, circleVec3, (ManasSkill)null, 0, Pair.of(0.0, 1000.0)
                                             );
                                          })
                                          .startCondition(
                                             entity -> {
                                                if (!entity.getMainHandItem().is((Item)TensuraToolItems.MOONLIGHT.get())) {
                                                   return false;
                                                } else {
                                                   return entity.getRandom().nextFloat() <= 0.5
                                                      ? true
                                                      : entity.getHealth() <= entity.getMaxHealth() / 2.0F && entity.getRandom().nextFloat() <= 0.5;
                                                }
                                             }
                                          ),
                                       new CustomHeldAttack()
                                          .minAttackRadius(0.0F)
                                          .maxAttackRadius(30.0F)
                                          .attackInterval(entity -> 60)
                                          .onTick(
                                             (entity, target, tick) -> {
                                                if (entity.disintegrationUUID == null) {
                                                   if (entity.disintegrationCooldown <= 0) {
                                                      entity.disintegration(target);
                                                   }
                                                } else if (!(
                                                   ((ServerLevel)entity.level()).getEntity(entity.disintegrationUUID) instanceof DisintegrationEntity disintegration
                                                      && target.isAlive()
                                                )) {
                                                   entity.disintegrationUUID = null;
                                                } else if (disintegration.getAffectedEntities().isEmpty()) {
                                                   disintegration.discard();
                                                   entity.disintegrationUUID = null;
                                                } else if (this.distanceTo(disintegration) <= 10.0F) {
                                                   entity.teleportTowards(entity, disintegration, 15.0);
                                                }

                                                double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
                                                TensuraParticleHelper.addServerAuraParticles(
                                                   entity, TensuraParticleUtils.getGoldAura(1.0F, (float)size, -0.3F), 3, 0.03
                                                );
                                                return tick < 250 && entity.disintegrationUUID != null;
                                             }
                                          )
                                          .startCondition(entity -> {
                                             if (entity.getRandom().nextFloat() >= 0.8) {
                                                return false;
                                             } else {
                                                return entity.getPhase() == 4 ? true : entity.getHealth() <= entity.getMaxHealth() / 2.0F;
                                             }
                                          })
                                    }
                                 )
                                 .startCondition(entity -> entity.disintegrationCooldown <= 0)
                           }
                        )
                        .startCondition(entity -> entity.getPhase() >= 3),
                     PlayerLikeEntity.getSpearAttack(20).attackInterval(entity -> 5).attackRadius(25.0F),
                     PlayerLikeEntity.getCrossbowAttack().attackInterval(entity -> 10).attackRadius(30.0F),
                     PlayerLikeEntity.getBowAttack().attackInterval(entity -> 10).attackRadius(25.0F),
                     new AnimatableMeleeAttack(1)
                        .attackInterval(entity -> 15)
                        .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
                        .startCondition(entity -> !entity.usingRangedWeapon())
                  }
               )
               .startCondition(entity -> !entity.shouldStopMoving())
         }
      );
   }

   @Generated
   @Override
   public boolean isLandNavigating() {
      return this.landNavigating;
   }

   @Generated
   @Override
   public void setLandNavigating(boolean landNavigating) {
      this.landNavigating = landNavigating;
   }

   @Generated
   @Override
   public int getSwimmingTick() {
      return this.swimmingTick;
   }

   @Generated
   @Override
   public void setSwimmingTick(int swimmingTick) {
      this.swimmingTick = swimmingTick;
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
