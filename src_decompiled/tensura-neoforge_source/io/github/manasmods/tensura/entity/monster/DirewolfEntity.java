package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.common.CoercionSkill;
import io.github.manasmods.tensura.ability.skill.extra.BlackLightningSkill;
import io.github.manasmods.tensura.ability.skill.extra.HakiSkill;
import io.github.manasmods.tensura.ability.skill.extra.UltraInstinctSkill;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.magic.lightning.BlackLightningBolt;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.entity.variant.DirewolfVariant;
import io.github.manasmods.tensura.network.c2s.RequestNamingMenuPacket;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.AgeableMob.AgeableMobGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.pathfinder.PathType;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.BreedWithPartner;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowTemptation;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.ItemTemptingSensor;
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

public class DirewolfEntity
   extends TensuraMountEntity
   implements GeoEntity,
   SmartBrainOwner<DirewolfEntity>,
   INameEvolution,
   ITensuraMount,
   VariantHolder<DirewolfVariant> {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> EVOLUTION_STATE = SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> LEADER_TICK = SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> LEADER = SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> ALPHA = SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> STAR = SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Optional<UUID>> LEADER_UUID = SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.OPTIONAL_UUID);
   private int mountAttackCooldown = 0;
   private final ResourceLocation CONTROLLED_SIZE = ResourceLocation.fromNamespaceAndPath("tensura", "controlled_size");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   public static final RawAnimation EAT = RawAnimation.begin().then("animation.direwolf.eat", LoopType.PLAY_ONCE);
   public static final RawAnimation HOWL = RawAnimation.begin().then("animation.direwolf.howl", LoopType.PLAY_ONCE);
   public static final RawAnimation SHADOW_MOTION = RawAnimation.begin().then("animation.direwolf.shadow_motion", LoopType.PLAY_ONCE);

   public DirewolfEntity(EntityType<? extends DirewolfEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 6.0)
         .add(Attributes.MAX_HEALTH, 35.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.ARMOR, 0.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.2F)
         .add(Attributes.JUMP_STRENGTH, 1.0)
         .add(Attributes.SAFE_FALL_DISTANCE, 8.0)
         .add(Attributes.STEP_HEIGHT, 2.0)
         .add(Attributes.SCALE, 1.0)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 3.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ID_TYPE_VARIANT, 0);
      builder.define(EVOLUTION_STATE, 0);
      builder.define(LEADER_TICK, 0);
      builder.define(LEADER, Boolean.FALSE);
      builder.define(ALPHA, Boolean.FALSE);
      builder.define(STAR, Boolean.FALSE);
      builder.define(LEADER_UUID, Optional.empty());
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      compound.putInt("EvoState", this.getCurrentEvolutionState());
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
      compound.putInt("LeaderTick", this.getLeaderTick());
      if (this.getLeaderUUID() != null) {
         compound.putUUID("Leader", this.getLeaderUUID());
      }

      compound.putBoolean("Leader", this.isLeader());
      compound.putBoolean("Alpha", this.isAlpha());
      compound.putBoolean("Star", this.isStar());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      this.setCurrentEvolutionState(compound.getInt("EvoState"));
      super.readAdditionalSaveData(compound);
      this.entityData.set(DATA_ID_TYPE_VARIANT, compound.getInt("Variant"));
      this.setLeaderTick(compound.getInt("LeaderTick"));
      if (compound.hasUUID("Leader")) {
         this.setLeaderUUID(compound.getUUID("Leader"));
      }

      this.setLeader(compound.getBoolean("Leader"));
      this.setAlpha(compound.getBoolean("Alpha"));
      this.setStar(compound.getBoolean("Star"));
   }

   public DirewolfVariant getVariant() {
      return DirewolfVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setVariant(DirewolfVariant variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
      if (variant.equals(DirewolfVariant.BLUE_FANG) || variant.equals(DirewolfVariant.MYSTIC_WATER_WOLF)) {
         this.gainSwimSpeed(this, 3.0);
      } else if (variant.equals(DirewolfVariant.RED_FANG) || variant.equals(DirewolfVariant.MYSTIC_FIRE_WOLF)) {
         this.gainLavaSpeed(this, 3.0);
      }
   }

   public int getLeaderTick() {
      return (Integer)this.entityData.get(LEADER_TICK);
   }

   public void setLeaderTick(int tick) {
      this.entityData.set(LEADER_TICK, tick);
   }

   public boolean isLeader() {
      return (Boolean)this.entityData.get(LEADER);
   }

   public void setLeader(boolean alpha) {
      this.entityData.set(LEADER, alpha);
   }

   public boolean isAlpha() {
      return (Boolean)this.entityData.get(ALPHA);
   }

   public void setAlpha(boolean alpha) {
      this.entityData.set(ALPHA, alpha);
   }

   public boolean isStar() {
      return (Boolean)this.entityData.get(STAR);
   }

   public void setStar(boolean star) {
      this.entityData.set(STAR, star);
   }

   @Nullable
   public UUID getLeaderUUID() {
      return (UUID)((Optional)this.entityData.get(LEADER_UUID)).orElse(null);
   }

   public void setLeaderUUID(@Nullable UUID pUuid) {
      this.entityData.set(LEADER_UUID, Optional.ofNullable(pUuid));
   }

   @Override
   public boolean isSaddleRequired() {
      return false;
   }

   @Override
   public boolean canSleep() {
      return true;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      switch (this.getVariant()) {
         case BROWN_FANG:
         case MYSTIC_EARTH_WOLF:
            if (source.is(DamageTypes.IN_WALL)) {
               return true;
            }
            break;
         case RED_FANG:
         case MYSTIC_FIRE_WOLF:
            if (source.is(DamageTypeTags.IS_FIRE) && source.tensura$getAbilityInstance() == null) {
               return true;
            }
            break;
         case BLUE_FANG:
         case MYSTIC_WATER_WOLF:
            if (source.is(DamageTypes.FREEZE)) {
               return true;
            }
            break;
         case GREEN_FANG:
         case MYSTIC_WIND_WOLF:
            if (source.is(DamageTypes.CACTUS) || source.is(DamageTypes.SWEET_BERRY_BUSH)) {
               return true;
            }
      }

      return super.isInvulnerableTo(source);
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      return super.isAlliedTo(entity) ? true : !this.isTame() && entity instanceof DirewolfEntity wolf && !wolf.isTame();
   }

   public void push(Entity pEntity) {
      if (!(pEntity instanceof DirewolfEntity wolf && (wolf.getLeaderUUID() == this.getUUID() || wolf.getUUID() == this.getLeaderUUID()))) {
         super.push(pEntity);
      }
   }

   @Override
   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return this.getVariant() == DirewolfVariant.TEMPEST_STAR_WOLF ? false : super.causeFallDamage(pFallDistance, pMultiplier, pSource);
   }

   @Override
   public void gainLavaSpeed(LivingEntity entity, double amount) {
      AttributeInstance lavaSpeed = entity.getAttribute(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER);
      if (lavaSpeed != null) {
         lavaSpeed.setBaseValue(lavaSpeed.getBaseValue() + amount);
      }

      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
   }

   public boolean canBreatheUnderwater() {
      return !this.getVariant().equals(DirewolfVariant.BLUE_FANG) && !this.getVariant().equals(DirewolfVariant.MYSTIC_WATER_WOLF)
         ? super.canBreatheUnderwater()
         : true;
   }

   @Override
   public boolean canBeNamed(Player player) {
      if (this.isAlpha()) {
         return Objects.equals(SubordinateHelper.getSubordinateOwnerUUID(this), player.getUUID())
            ? !this.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MIND_CONTROL))
            : false;
      } else {
         return true;
      }
   }

   @Override
   public int getMaxEvolutionState() {
      return 3;
   }

   @Override
   public int getCurrentEvolutionState() {
      return (Integer)this.entityData.get(EVOLUTION_STATE);
   }

   @Override
   public void setCurrentEvolutionState(int state) {
      this.entityData.set(EVOLUTION_STATE, state);
   }

   @Override
   public void onPreNamed(
      IExistence existence, Player owner, Changeable<Double> epGain, Changeable<Double> cost, RequestNamingMenuPacket.NamingType namingType, String name
   ) {
      if (owner != null && this.isLeader()) {
         int packNamed = 1;

         for (DirewolfEntity wolf : this.level()
            .getEntitiesOfClass(
               DirewolfEntity.class,
               this.getBoundingBox().inflate(32.0, 32.0, 32.0),
               entity -> entity.isAlive() && entity != this && entity.getLeaderUUID() == this.getUUID()
            )) {
            if (!wolf.isTame() || this.isTame()) {
               wolf.getNavigation().stop();
               wolf.clearTarget();
               if (!wolf.isTame()) {
                  wolf.tame(owner);
                  wolf.setOrderedToSit(true);
                  wolf.level().broadcastEntityEvent(wolf, (byte)7);
                  IExistence wolfExistence = TensuraStorages.getExistenceFrom(wolf);
                  if (wolfExistence.getName() == null) {
                     wolfExistence.setName(name);
                     wolfExistence.setPermanentOwner(owner.getUUID());
                     if (Objects.equals(wolfExistence.getTemporaryOwner(), owner.getUUID())) {
                        wolfExistence.setTemporaryOwner(null);
                     }

                     packNamed++;
                     owner.level()
                        .playSound(
                           null,
                           wolf.getX(),
                           wolf.getY(),
                           wolf.getZ(),
                           (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                     wolf.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false, false));
                     TensuraParticleHelper.addServerParticlesAroundSelf(wolf, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
                     TensuraParticleHelper.addServerParticlesAroundSelf(wolf, ParticleTypes.FLASH, 1.0);
                     RequestNamingMenuPacket.namingReward(wolf, wolfExistence, (Double)epGain.get(), namingType);
                     wolf.heal(wolf.getMaxHealth());
                     wolfExistence.markDirty();
                  }
               }
            }
         }

         cost.set((Double)cost.get() * packNamed);
      }
   }

   @Override
   public void evolve() {
      int current = this.getCurrentEvolutionState();
      AttributeInstance instance = this.getAttribute(Attributes.SCALE);
      if (instance != null) {
         instance.setBaseValue(instance.getBaseValue() + 0.5);
      }

      if (current < this.getMaxEvolutionState()) {
         this.setCurrentEvolutionState(current + 1);
         this.evolveBuff(12.0);
         if (this.getVariant() == DirewolfVariant.DIREWOLF) {
            this.setVariant(this.getEvolutionByBiomes());
         } else if (this.getVariant().getId() >= 14) {
            this.setVariant(DirewolfVariant.TEMPEST_STAR_WOLF);
            if (this.isStar()) {
               Skills storage = SkillAPI.getSkillsFrom(this);
               storage.learnSkill((ManasSkill)ExtraSkills.BLACK_LIGHTNING.get());
               ManasSkillInstance ultra = ((UltraInstinctSkill)ExtraSkills.ULTRA_INSTINCT.get()).createDefaultInstance();
               ultra.setToggled(true);
               SkillHelper.learnSkill(this, ultra);
            }
         } else if (this.getVariant().getId() % 2 != 0) {
            this.setVariant(DirewolfVariant.byId(this.getVariant().getId() + 1));
         }

         if (this.getVariant().getId() >= 14) {
            SkillAPI.getSkillsFrom(this).learnSkill((ManasSkill)ExtraSkills.SHADOW_MOTION.get());
         }
      }
   }

   private void evolveBuff(double damage) {
      this.gainMaxHealth(this, 10.0);
      this.gainAttackDamage(this, damage);
      this.gainMovementSpeed(this, 0.05);
      this.gainSwimSpeed(this, 1.0);
      this.gainJumpStrength(this, 0.2);
   }

   private DirewolfVariant getEvolutionByBiomes() {
      if (this.isStar()) {
         return DirewolfVariant.STAR_WOLF;
      } else {
         Holder<Biome> biomes = this.level().getBiome(this.getOnPos());
         if (biomes.is(BiomeTags.IS_NETHER)
            || biomes.is(BiomeTags.HAS_RUINED_PORTAL_DESERT)
            || biomes.is(BiomeTags.IS_BADLANDS)
            || biomes.is(BiomeTags.IS_SAVANNA)) {
            return DirewolfVariant.RED_FANG;
         } else if (biomes.is(BiomeTags.IS_OCEAN)
            || biomes.is(BiomeTags.IS_BEACH)
            || biomes.is(BiomeTags.IS_RIVER)
            || biomes.is(BiomeTags.HAS_RUINED_PORTAL_SWAMP)) {
            return DirewolfVariant.BLUE_FANG;
         } else if (biomes.is(BiomeTags.IS_FOREST) || biomes.is(BiomeTags.IS_JUNGLE)) {
            return DirewolfVariant.GREEN_FANG;
         } else if (!biomes.is(BiomeTags.IS_END) && !biomes.is(BiomeTags.IS_MOUNTAIN)) {
            return !biomes.is(BiomeTags.IS_HILL) && !(this.getY() <= 64.0) ? DirewolfVariant.BLACK_FANG : DirewolfVariant.BROWN_FANG;
         } else {
            return DirewolfVariant.PURPLE_FANG;
         }
      }
   }

   @Nullable
   private DamageSource getAdditionalDamageSource() {
      return switch (this.getVariant()) {
         case BROWN_FANG, MYSTIC_EARTH_WOLF -> TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.EARTH_ELEMENTAL, this);
         case RED_FANG, MYSTIC_FIRE_WOLF -> TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.FIRE_ELEMENTAL, this);
         case BLUE_FANG, MYSTIC_WATER_WOLF -> TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.WATER_ELEMENTAL, this);
         case GREEN_FANG, MYSTIC_WIND_WOLF -> TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.WIND_ELEMENTAL, this);
         case DIREWOLF -> null;
         case PURPLE_FANG, MYSTIC_SPACE_WOLF -> TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.SPACE_ELEMENTAL, this);
         default -> TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.MAGIC_GENERIC, this);
      };
   }

   @Override
   public boolean doHurtTarget(Entity pEntity) {
      float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
      float knockBack = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      DamageSource source = this.getAdditionalDamageSource();
      boolean flag;
      if (source == null) {
         flag = pEntity.hurt(this.damageSources().mobAttack(this), damage);
      } else {
         flag = TensuraDamageHelper.hurtSplit(pEntity, this.damageSources().mobAttack(this), 0.5F, source, damage);
      }

      if (flag) {
         if (knockBack > 0.0F && pEntity instanceof LivingEntity entity) {
            entity.knockback(knockBack * 0.5F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
         }

         this.setLastHurtMob(pEntity);
      }

      return flag;
   }

   @Nullable
   private ManasSkillInstance getSkill(ManasSkill skill) {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(this).getSkill(skill);
      if (optional.isEmpty()) {
         return null;
      } else {
         return !optional.get().canInteractSkill(this) ? null : optional.get();
      }
   }

   @Override
   public void tick() {
      super.tick();
      this.mountAttackCooldown--;
      if (this.isLeader()) {
         int newTick = this.getLeaderTick() - 1;
         this.setLeaderTick(newTick);
         if (newTick <= 0) {
            this.setLeader(false);
         }
      }
   }

   @Override
   protected void sleepHandler() {
      boolean sleeping = this.isSleeping();
      if (sleeping && this.shouldWakeUp()) {
         this.setSleeping(false);
         sleeping = false;
      }

      Level level = this.level();
      boolean isNight = level.isNight();
      if (isNight && level.getMoonPhase() == 0) {
         if (sleeping) {
            this.setSleeping(false);
            sleeping = false;
         }

         long dayTime = level.getDayTime();
         if (dayTime >= 17000L && dayTime <= 19000L && this.tickCount % 100 == 0 && this.getRandom().nextInt(200) == 1) {
            this.triggerAnim("miscController", "howl");
            this.playSound((SoundEvent)TensuraSoundEvents.DIREWOLF_HOWL.get(), 1.5F, 1.0F);
         }
      } else {
         if (this.getTarget() == null
            && isNight
            && !this.shouldFollowOwner()
            && !sleeping
            && !this.isInLiquid()
            && !this.isVehicle()
            && !this.isPassenger()
            && this.getRandom().nextInt(100) == 0) {
            if (this.getRandom().nextBoolean()) {
               this.setSleeping(true);
            } else {
               this.sleepingTime = 0;
               this.maxSleepTime = 100 + this.random.nextInt(550);
            }
         }
      }
   }

   private void coercion(Vec3 targetPos) {
      Level level = this.level();
      if (!level.isClientSide()) {
         Vec3 source = this.getEyePosition().add(this.getLookAngle().scale(2.0));
         Vec3 sourceToTarget = targetPos.subtract(source);
         Vec3 normalizes = sourceToTarget.normalize();
         level.playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.COERCION.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);

         for (int particleIndex = 1; particleIndex < Mth.floor(sourceToTarget.length()); particleIndex++) {
            Vec3 particlePos = source.add(normalizes.scale(particleIndex));
            TensuraParticleHelper.spawnServerParticles(level, TensuraParticleUtils.getColorlessSonic(0.85F, 2.5F), particlePos.x, particlePos.y, particlePos.z);
            AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos)).inflate(4.0);
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, targetx -> !targetx.is(this) && !this.isAlliedTo(targetx));
            if (!list.isEmpty()) {
               double ownerEP = EnergyHelper.getMaxEP(this);
               LivingEntity owner = (LivingEntity)(this.getControllingPassenger() != null ? this.getControllingPassenger() : this);
               ManasSkillInstance coercion = this.getSkill((ManasSkill)CommonSkills.COERCION.get());

               for (LivingEntity target : list) {
                  if (!this.hasPassenger(target)) {
                     DamageSource damagesource = ((CoercionSkill)CommonSkills.COERCION.get()).createSource(coercion, this, DamageTypes.SONIC_BOOM, 0);
                     target.hurt(damagesource, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.25F);
                     double targetEP = EnergyHelper.getMaxEP(target);
                     double difference = ownerEP / targetEP;
                     if (!(difference <= 2.0)) {
                        int fearLevel = (int)(CoercionSkill.CONFIG.epDifferenceMultiplier * (difference - 2.0));
                        fearLevel = Math.min(fearLevel, TensuraMobEffect.CONFIG.maxFear);
                        TensuraMobEffect.addEffect(
                           target,
                           TensuraMobEffects.getReference(TensuraMobEffects.FEAR),
                           CoercionSkill.CONFIG.fearDuration,
                           fearLevel,
                           false,
                           false,
                           true,
                           owner.getUUID(),
                           (ManasSkill)CommonSkills.COERCION.get(),
                           0
                        );
                        if (target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, CoercionSkill.CONFIG.fearDuration, fearLevel, true, false, true), this)
                           )
                         {
                           HakiSkill.hakiPush(target, this, coercion, fearLevel);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void blackLightning(Vec3 position) {
      ManasSkillInstance instance = this.getSkill((ManasSkill)ExtraSkills.BLACK_LIGHTNING.get());
      if (instance != null) {
         ExtraSkillConfig.BlackLightning CONFIG = BlackLightningSkill.CONFIG;
         BlackLightningBolt bolt = new BlackLightningBolt(this.level(), this);
         bolt.setSkill(instance);
         bolt.setMpCost(CONFIG.magiculeCost);
         bolt.setRadius(CONFIG.defaultRange);
         bolt.setTensuraDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 5.0F);
         bolt.setSecondaryDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
         bolt.setAdditionalVisual(5);
         bolt.setPos(position);
         this.level().addFreshEntity(bolt);
      }
   }

   @Override
   public void onShadowStorageSpawned() {
      this.triggerAnim("shadowController", "shadow_motion");
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.SQUID_INK);
   }

   protected SoundEvent getAmbientSound() {
      return !this.isAngry() && !this.isAlpha() ? (SoundEvent)TensuraSoundEvents.DIREWOLF_AMBIENT.get() : (SoundEvent)TensuraSoundEvents.DIREWOLF_AGGRO.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.DIREWOLF_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.DIREWOLF_DEATH.get();
   }

   @NotNull
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      if (!(pOtherParent instanceof DirewolfEntity direwolf)) {
         return null;
      } else {
         DirewolfEntity baby = (DirewolfEntity)((EntityType)MonsterEntityTypes.DIREWOLF.get()).create(pLevel);
         if (baby == null) {
            return null;
         }

         UUID uuid = this.getOwnerUUID();
         if (uuid != null) {
            baby.setOwnerUUID(uuid);
            baby.setTame(true, true);
         }

         if (this.getCurrentEvolutionState() >= 1 && direwolf.getCurrentEvolutionState() >= 1) {
            baby.evolve();
            baby.setCurrentEvolutionState(1);
            int tempestParent = this.getTypeVariant() >= 13 ? 1 : 0;
            if (direwolf.getTypeVariant() >= 13) {
               tempestParent++;
            }

            if (pLevel.getRandom().nextFloat() <= TensuraEntityTypes.CONFIG.SpecialVariant.tempestWolfPack * tempestParent / 100.0F) {
               baby.setVariant(DirewolfVariant.TEMPEST_WOLF);
               if (this.isStar() && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.starBirthmark, pLevel.getRandom())) {
                  baby.setStar(true);
               } else if (direwolf.isStar() && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.starBirthmark, pLevel.getRandom())) {
                  baby.setStar(true);
               }
            } else {
               int fatherVariant = this.getTypeVariant();
               int motherVariant = direwolf.getTypeVariant();
               if (fatherVariant == motherVariant) {
                  baby.setVariant(baby.getVariant());
               } else if (fatherVariant >= 13) {
                  baby.setVariant(direwolf.getVariant());
               } else if (motherVariant >= 13) {
                  baby.setVariant(this.getVariant());
               } else if (pLevel.getRandom().nextFloat() * 100.0F <= TensuraEntityTypes.CONFIG.SpecialVariant.elementalFangPack) {
                  baby.setVariant(this.getEvolutionByBiomes());
               } else {
                  baby.setVariant(DirewolfVariant.BLACK_FANG);
               }
            }

            if (baby.getTypeVariant() >= 14) {
               baby.setVariant(DirewolfVariant.TEMPEST_WOLF);
            }
         }

         return baby;
      }
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   @Override
   public boolean isHealingFood(ItemStack stack) {
      return this.isFood(stack) || stack.is(Items.BONE);
   }

   @Nullable
   private DirewolfVariant getVariantFromCore(ItemStack core) {
      if (this.getVariant() != DirewolfVariant.BLACK_FANG) {
         return null;
      } else if (core.is((Item)TensuraMaterialItems.ELEMENT_CORE_EARTH.get())) {
         return DirewolfVariant.BROWN_FANG;
      } else if (core.is((Item)TensuraMaterialItems.ELEMENT_CORE_FIRE.get())) {
         return DirewolfVariant.RED_FANG;
      } else if (core.is((Item)TensuraMaterialItems.ELEMENT_CORE_WIND.get())) {
         return DirewolfVariant.GREEN_FANG;
      } else if (core.is((Item)TensuraMaterialItems.ELEMENT_CORE_WATER.get())) {
         return DirewolfVariant.BLUE_FANG;
      } else {
         return core.is((Item)TensuraMaterialItems.ELEMENT_CORE_SPACE.get()) ? DirewolfVariant.PURPLE_FANG : null;
      }
   }

   @Override
   public int getRiderSeats() {
      AttributeInstance instance = this.getAttribute(Attributes.SCALE);
      double size = instance != null ? instance.getValue() : 1.0;
      return (int)(size + 0.5);
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (this.isTame() && this.isOwnedBy(player)) {
         DirewolfVariant coreVariant = this.getVariantFromCore(stack);
         if (coreVariant != null) {
            this.setVariant(coreVariant);
            this.level()
               .playSound(
                  null,
                  this,
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F
               );
            TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.ELECTRIC_SPARK);
            this.usePlayerItem(player, hand, stack);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
         }

         InteractionResult riding = this.getRidingInteraction(player, hand);
         if (riding != InteractionResult.PASS) {
            return riding;
         }

         this.cycleCommands(this, player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else if (this.isRideable(player) && this.getControllingPassenger() != null) {
         this.doPlayerRide(player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      AttributeInstance instance = this.getAttribute(Attributes.SCALE);
      double size = instance != null ? instance.getValue() : 1.0;
      int i = Math.max(this.getPassengers().indexOf(entity), 0);
      Vec3 vec3 = new Vec3(0.0, 0.2 * (size + 2.0) - 0.75, -0.5 * i * f).yRot(-this.getYRot() * 0.0174F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3);
   }

   @Override
   protected void removePassenger(Entity pPassenger) {
      super.removePassenger(pPassenger);
      if (this.isNoGravity() && this.getControllingPassenger() == null) {
         this.setNoGravity(false);
      }
   }

   @Override
   public void handleStartJump(int pJumpPower) {
      if (this.canExecuteRidersJump()) {
         this.playJumpSound();
      } else {
         this.playerJumpPendingScale = 0.0F;
         if (this.getVariant() == DirewolfVariant.TEMPEST_STAR_WOLF && !this.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE))) {
            this.setNoGravity(true);
         }
      }
   }

   @Override
   public boolean canActivateMountAbility(LivingEntity rider) {
      return !this.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_SKILL));
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAttackCooldown <= 0) {
         this.stopRiding();
         AttributeInstance instance = this.getAttribute(Attributes.SCALE);
         float size = 0.0F;
         if (instance != null) {
            AttributeModifier modifier = instance.getModifier(this.CONTROLLED_SIZE);
            size = modifier != null ? (float)modifier.amount() : 0.0F;
         }

         if (size >= 0.0F && this.getSkill((ManasSkill)ExtraSkills.BLACK_LIGHTNING.get()) != null) {
            Entity target = ObjectSelectionHelper.getTargetingEntity(rider, 50.0, true, false);
            Vec3 pos;
            if (target != null) {
               pos = target.position();
            } else {
               pos = ObjectSelectionHelper.getPlayerPOVHitResultFromPos(
                     this.level(), rider, Fluid.NONE, 50.0, rider.position().add(0.0, rider.getBbHeight(), 0.0)
                  )
                  .getLocation();
            }

            this.blackLightning(pos);
            this.triggerAnim("miscController", "howl");
            this.playSound((SoundEvent)TensuraSoundEvents.DIREWOLF_HOWL.get(), 3.0F, 1.0F);
            this.mountAttackCooldown = 80;
         } else {
            this.triggerAnim("miscController", "bark");
            this.coercion(this.getEyePosition().add(rider.getLookAngle().scale(15.0)));
            this.mountAttackCooldown = 20;
         }
      }
   }

   @Override
   public boolean hasScrollAbility(Player rider) {
      return this.getCurrentEvolutionState() <= 0 ? false : this.getControllingPassenger() != null;
   }

   @Override
   public void mountScrollAbility(Player rider, double scrollChange) {
      AttributeInstance instance = this.getAttribute(Attributes.SCALE);
      if (instance != null) {
         AttributeModifier modifier = instance.getModifier(this.CONTROLLED_SIZE);
         float size = modifier != null ? (float)modifier.amount() : 0.0F;
         float minSize = 0.7F - (float)instance.getBaseValue();
         float newSize = Mth.clamp(size + (float)scrollChange * 0.1F, minSize, 0.2F);
         instance.addOrReplacePermanentModifier(new AttributeModifier(this.CONTROLLED_SIZE, newSize, Operation.ADD_VALUE));
         if (this.getPassengers().size() > (int)(newSize + 0.5)) {
            for (Entity passenger : this.getPassengers()) {
               if (passenger != this.getControllingPassenger()) {
                  passenger.unRide();
                  break;
               }
            }
         }
      }
   }

   @Override
   protected void tickRidden(Player player, Vec3 vec3) {
      super.tickRidden(player, vec3);
      if (this.onGround()) {
         this.setNoGravity(false);
      }
   }

   @Override
   protected void applyExtraRidingMovement(Player controller, Vec3 vec3) {
      Vec3 riddenInput = this.getRiddenInput(controller, vec3);
      if (this.isNoGravity()) {
         if (controller.jumping) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.1, 0.0));
         } else if (TensuraKeybinds.DODGE.isDown()) {
            this.descending(this, controller);
         }
      } else if (this.isInLava() || this.isInWater() && this.getFluidHeight(FluidTags.WATER) > this.getFluidJumpThreshold() && riddenInput.z() > 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.03, 0.0));
      }
   }

   @Override
   public boolean hasSaddleSlot() {
      return false;
   }

   @Override
   public int getChestSlots() {
      return 25;
   }

   public void die(DamageSource cause) {
      super.die(cause);
      if (!this.level().isClientSide()) {
         if (!this.isAlive()) {
            if ((this.isAlpha() || this.isLeader()) && cause.getEntity() instanceof Player player) {
               for (DirewolfEntity wolf : this.level()
                  .getEntitiesOfClass(
                     DirewolfEntity.class,
                     this.getBoundingBox().inflate(32.0, 32.0, 32.0),
                     entity -> entity.isAlive() && entity != this && entity.getLeaderUUID() == this.getUUID()
                  )) {
                  if (!wolf.isTame() || this.isTame()) {
                     IExistence existence = TensuraStorages.getExistenceFrom(wolf);
                     wolf.invulnerableTime = 10;
                     if (wolf.getTarget() == player) {
                        wolf.clearTarget();
                     }

                     existence.addNeutralTarget(player.getUUID());
                     existence.markDirty();
                     if (this.isAlpha() && !wolf.isTame()) {
                        boolean feared = wolf.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FEAR)) || cause.is(TensuraDamageTypes.FEAR);
                        if (feared) {
                           wolf.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.FEAR));
                           wolf.tame(player);
                           existence.setPermanentOwner(player.getUUID());
                           if (Objects.equals(existence.getTemporaryOwner(), player.getUUID())) {
                              existence.setTemporaryOwner(null);
                           }

                           wolf.getNavigation().stop();
                           wolf.clearTarget();
                           wolf.setTarget(null);
                           wolf.setOrderedToSit(false);
                           wolf.level().broadcastEntityEvent(wolf, (byte)7);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public int getMaxSpawnClusterSize() {
      return 12;
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.direwolf, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      DirewolfEntity.DirewolfPackData packData;
      if (pSpawnData instanceof DirewolfEntity.DirewolfPackData data) {
         packData = data;
      } else {
         packData = new DirewolfEntity.DirewolfPackData();
      }

      if (packData.getVariant() == null) {
         if (pLevel.getRandom().nextFloat() * 100.0F <= TensuraEntityTypes.CONFIG.SpecialVariant.evolvedPack) {
            if (pLevel.getRandom().nextFloat() * 100.0F <= TensuraEntityTypes.CONFIG.SpecialVariant.tempestWolfPack) {
               packData.setVariant(DirewolfVariant.TEMPEST_WOLF);
            } else if (pLevel.getRandom().nextFloat() * 100.0F <= TensuraEntityTypes.CONFIG.SpecialVariant.elementalFangPack) {
               packData.setVariant(this.getEvolutionByBiomes());
            } else {
               packData.setVariant(DirewolfVariant.BLACK_FANG);
            }
         } else {
            packData.setVariant(DirewolfVariant.DIREWOLF);
         }
      }

      DirewolfVariant variant = packData.getVariant();
      if (variant.getId() > 0) {
         AttributeInstance scale = this.getAttribute(Attributes.SCALE);
         if (scale != null) {
            scale.setBaseValue(scale.getBaseValue() + 0.25);
         }

         this.setCurrentEvolutionState(this.getCurrentEvolutionState() + 1);
         this.evolveBuff(6.0);
         if (variant == DirewolfVariant.TEMPEST_WOLF
            && packData.getStar() == null
            && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.starBirthmark, pLevel.getRandom())) {
            this.setStar(true);
            packData.setStar(this);
            if (!this.isBaby()) {
               this.triggerAnim("miscController", "howl");
               this.playSound((SoundEvent)TensuraSoundEvents.DIREWOLF_HOWL.get(), 1.5F, 1.0F);
            }
         }
      }

      this.setVariant(packData.getVariant());
      if (packData.getAlpha() == null && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.alphaWolf, pLevel.getRandom())) {
         packData.setAlpha(this);
         this.setAlpha(true);
         AttributeInstance scale = this.getAttribute(Attributes.SCALE);
         if (scale != null) {
            scale.setBaseValue(scale.getBaseValue() + (this.getVariant() != DirewolfVariant.DIREWOLF ? 0.75F : 0.5F));
         }

         this.gainAttackDamage(this, 10.0);
         this.gainMovementSpeed(this, 0.1F);
         this.gainSwimSpeed(this, 1.0);
         this.gainMaxHealth(this, 30.0);
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, packData);
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<DirewolfEntity>> getSensors() {
      return ObjectArrayList.of(
         new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new ItemTemptingSensor().temptedWith(DirewolfEntity::isFood)}
      );
   }

   public BrainActivityGroup<DirewolfEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<DirewolfEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new BreedWithPartner(),
                  TensuraBehaviourHelper.getPreyTargeting(this),
                  new FollowTemptation()
                     .followIf(TamableAnimal::isOwnedBy)
                     .speedMod((entity, player) -> 1.1F)
                     .startCondition(entity -> !entity.isOrderedToSit())
                     .whenStarting(entity -> entity.triggerAnim("loopController", "head_tilt")),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
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

   public BrainActivityGroup<DirewolfEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(2)
                     .requireInSight(false)
                     .maxAttackRadius(15.0F)
                     .minAttackRadius(4.0F)
                     .attackInterval(entity -> 100)
                     .performAttack((entity, target) -> entity.coercion(entity.getEyePosition().add(entity.getLookAngle().scale(15.0))))
                     .whenStarting(entity -> entity.triggerAnim("miscController", "bark"))
                     .startCondition(direwolfEntity -> this.random.nextInt(10) == 1),
                  new CustomRangeAttack(5)
                     .requireInSight(false)
                     .maxAttackRadius(32.0F)
                     .minAttackRadius(7.0F)
                     .attackInterval(entity -> 200)
                     .performAttack((entity, target) -> entity.blackLightning(target.position()))
                     .whenStarting(entity -> {
                        entity.triggerAnim("miscController", "howl");
                        this.playSound((SoundEvent)TensuraSoundEvents.DIREWOLF_HOWL.get(), 3.0F, 1.0F);
                        this.mountAttackCooldown = 80;
                     })
                     .startCondition(entity -> this.random.nextInt(10) == 1 && entity.getSkill((ManasSkill)ExtraSkills.BLACK_LIGHTNING.get()) != null),
                  new CustomHeldAttack().requireInSight(false).minAttackRadius(10.0F).maxAttackRadius(32.0F).onTick((entity, target, tick) -> {
                     if (tick % 3 == 0) {
                        TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.SQUID_INK);
                     }

                     if (tick >= 5 && tick <= 20) {
                        this.getNavigation().stop();
                     } else if (tick == 30) {
                        if (target != null && target.distanceTo(this) < 5.0F) {
                           this.doHurtTarget(target);
                        }

                        return false;
                     }

                     return target.isAlive();
                  }).whenStarting(entity -> {
                     LivingEntity target = this.getTarget();
                     if (target != null) {
                        entity.teleportTo(target.getX(), target.getY(), target.getZ());
                     }

                     entity.onShadowStorageSpawned();
                  }).startCondition(entity -> {
                     if (entity.getTarget() == null) {
                        return false;
                     } else if (this.random.nextInt(10) != 1) {
                        return false;
                     } else {
                        return entity.getSkill((ManasSkill)ExtraSkills.SHADOW_MOTION.get()) == null
                           ? false
                           : entity.onGround() && entity.getTarget().onGround();
                     }
                  }),
                  new AnimatableMeleeAttack(0).attackInterval(entity -> 10).whenStarting(entity -> entity.triggerAnim("miscController", "bite"))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<DirewolfEntity> state) {
      String name;
      if (this.isSleeping()) {
         name = "animation.direwolf.sleep";
      } else if (this.isInSittingPose()) {
         name = "animation.direwolf.sit";
      } else if (this.isInLiquid() && this.level().getBlockState(this.blockPosition().below(1)).canBeReplaced()) {
         name = "animation.direwolf.swim";
      } else if (state.isMoving()) {
         if (this.isInLiquid() || !this.isAngry() && !this.isSprinting()) {
            name = "animation.direwolf.walk";
         } else {
            name = "animation.direwolf.run";
         }
      } else {
         name = "animation.direwolf.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController)
               .triggerableAnim("head_tilt", RawAnimation.begin().then("animation.direwolf.head_tilt", LoopType.PLAY_ONCE)),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("eat", EAT)
               .triggerableAnim("howl", HOWL)
               .triggerableAnim("bite", RawAnimation.begin().then("animation.direwolf.bite", LoopType.PLAY_ONCE))
               .triggerableAnim("bark", RawAnimation.begin().then("animation.direwolf.bark", LoopType.PLAY_ONCE)),
            new AnimationController(this, "shadowController", 0, event -> PlayState.STOP).triggerableAnim("shadow_motion", SHADOW_MOTION)
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static class DirewolfPackData extends AgeableMobGroupData {
      @Nullable
      private DirewolfEntity alpha;
      @Nullable
      private DirewolfEntity star;
      @Nullable
      private DirewolfVariant variant;

      public DirewolfPackData() {
         super(false);
      }

      @Generated
      public void setAlpha(@Nullable DirewolfEntity alpha) {
         this.alpha = alpha;
      }

      @Generated
      public void setStar(@Nullable DirewolfEntity star) {
         this.star = star;
      }

      @Generated
      public void setVariant(@Nullable DirewolfVariant variant) {
         this.variant = variant;
      }

      @Nullable
      @Generated
      public DirewolfEntity getAlpha() {
         return this.alpha;
      }

      @Nullable
      @Generated
      public DirewolfEntity getStar() {
         return this.star;
      }

      @Nullable
      @Generated
      public DirewolfVariant getVariant() {
         return this.variant;
      }
   }
}
