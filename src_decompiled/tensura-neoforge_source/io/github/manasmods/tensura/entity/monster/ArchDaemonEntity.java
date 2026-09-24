package io.github.manasmods.tensura.entity.monster;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.config.entity.DaemonConfig;
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
import io.github.manasmods.tensura.entity.magic.barrier.WaterJailEntity;
import io.github.manasmods.tensura.entity.magic.spike.MudSpikeEntity;
import io.github.manasmods.tensura.entity.projectile.magic.AcidBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FireBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.IceLanceProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.StoneShotProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.ThunderSphereProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.WindTornadoProjectile;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IDaemon;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.entity.variant.DaemonVariant;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ArchDaemonEntity
   extends PlayerLikeEntity
   implements GeoEntity,
   SmartBrainOwner<ArchDaemonEntity>,
   IDaemon,
   IFlying,
   INameEvolution,
   VariantHolder<DaemonVariant.Linage> {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(ArchDaemonEntity.class);
   private static final EntityDataAccessor<Integer> LINAGE = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> GENDER = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HORN = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SECOND_HORN = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> THIRD_HORN = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HORN_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HAIR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> FACIAL_HAIR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HAIR_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> EYE_PUPIL_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> EYE_OUTER_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> THIRD_EYE_PUPIL_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> THIRD_EYE_OUTER_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> EYE_BROW = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> EYE_LINER = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> EYE_LINER_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TEETH = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TOP = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TOP_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BOTTOM = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BOTTOM_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SHOE = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SHOE_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> WINGS = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COAT = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> COAT_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> NECK_ACCESSORY = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> NECK_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> ARMBAND_COLOR = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.INT);
   public static final DaemonConfig CONFIG = (DaemonConfig)ConfigRegistry.getConfig(DaemonConfig.class);
   protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(ArchDaemonEntity.class, EntityDataSerializers.BOOLEAN);
   protected int flyingTick;
   protected boolean wasFlying;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ArchDaemonEntity(EntityType<? extends ArchDaemonEntity> type, Level level) {
      super(type, level);
      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.initFlying(this);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 50.0)
         .add(Attributes.MAX_HEALTH, 150.0)
         .add(Attributes.ARMOR, 25.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 4.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.6F)
         .add(Attributes.FLYING_SPEED, 2.5)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 2.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 20.0)
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
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLYING, false);
      builder.define(LINAGE, 0);
      builder.define(GENDER, 0);
      builder.define(HORN, 0);
      builder.define(SECOND_HORN, -1);
      builder.define(THIRD_HORN, -1);
      builder.define(HORN_COLOR, -1);
      builder.define(HAIR, 0);
      builder.define(FACIAL_HAIR, -1);
      builder.define(HAIR_COLOR, 0);
      builder.define(EYE_PUPIL_COLOR, 0);
      builder.define(EYE_OUTER_COLOR, 0);
      builder.define(THIRD_EYE_PUPIL_COLOR, -1);
      builder.define(THIRD_EYE_OUTER_COLOR, -1);
      builder.define(EYE_BROW, 0);
      builder.define(EYE_LINER, 0);
      builder.define(EYE_LINER_COLOR, -1);
      builder.define(TEETH, -1);
      builder.define(TOP, 0);
      builder.define(TOP_COLOR, 0);
      builder.define(BOTTOM, 0);
      builder.define(BOTTOM_COLOR, 0);
      builder.define(SHOE, 0);
      builder.define(SHOE_COLOR, 0);
      builder.define(WINGS, 0);
      builder.define(COAT, 0);
      builder.define(COAT_COLOR, -1);
      builder.define(NECK_ACCESSORY, 0);
      builder.define(NECK_COLOR, -1);
      builder.define(ARMBAND_COLOR, -1);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Flying", this.isFlying());
      compound.putInt("Linage", (Integer)this.entityData.get(LINAGE));
      compound.putInt("Gender", (Integer)this.entityData.get(GENDER));
      compound.putInt("Horn", (Integer)this.entityData.get(HORN));
      compound.putInt("SecondHorn", (Integer)this.entityData.get(SECOND_HORN));
      compound.putInt("ThirdHorn", (Integer)this.entityData.get(THIRD_HORN));
      compound.putInt("HornColor", (Integer)this.entityData.get(HORN_COLOR));
      compound.putInt("Hair", (Integer)this.entityData.get(HAIR));
      compound.putInt("FacialHair", (Integer)this.entityData.get(FACIAL_HAIR));
      compound.putInt("HairColor", (Integer)this.entityData.get(HAIR_COLOR));
      compound.putInt("EyePupilColor", (Integer)this.entityData.get(EYE_PUPIL_COLOR));
      compound.putInt("EyeOuterColor", (Integer)this.entityData.get(EYE_OUTER_COLOR));
      compound.putInt("ThirdEyePupilColor", (Integer)this.entityData.get(THIRD_EYE_PUPIL_COLOR));
      compound.putInt("ThirdEyeOuterColor", (Integer)this.entityData.get(THIRD_EYE_OUTER_COLOR));
      compound.putInt("EyeBrow", (Integer)this.entityData.get(EYE_BROW));
      compound.putInt("EyeLiner", (Integer)this.entityData.get(EYE_LINER));
      compound.putInt("EyeLinerColor", (Integer)this.entityData.get(EYE_LINER_COLOR));
      compound.putInt("Teeth", (Integer)this.entityData.get(TEETH));
      compound.putInt("Top", (Integer)this.entityData.get(TOP));
      compound.putInt("TopColor", (Integer)this.entityData.get(TOP_COLOR));
      compound.putInt("Bottom", (Integer)this.entityData.get(BOTTOM));
      compound.putInt("BottomColor", (Integer)this.entityData.get(BOTTOM_COLOR));
      compound.putInt("Shoe", (Integer)this.entityData.get(SHOE));
      compound.putInt("ShoeColor", (Integer)this.entityData.get(SHOE_COLOR));
      compound.putInt("Wings", (Integer)this.entityData.get(WINGS));
      compound.putInt("Coat", (Integer)this.entityData.get(COAT));
      compound.putInt("CoatColor", (Integer)this.entityData.get(COAT_COLOR));
      compound.putInt("NeckAccessory", (Integer)this.entityData.get(NECK_ACCESSORY));
      compound.putInt("NeckColor", (Integer)this.entityData.get(NECK_COLOR));
      compound.putInt("ArmbandColor", (Integer)this.entityData.get(ARMBAND_COLOR));
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlying(compound.getBoolean("Flying"));
      this.entityData.set(LINAGE, compound.getInt("Linage"));
      this.entityData.set(GENDER, compound.getInt("Gender"));
      this.entityData.set(HORN, compound.getInt("Horn"));
      this.entityData.set(SECOND_HORN, compound.getInt("SecondHorn"));
      this.entityData.set(THIRD_HORN, compound.getInt("ThirdHorn"));
      this.entityData.set(HORN_COLOR, compound.getInt("HornColor"));
      this.entityData.set(HAIR, compound.getInt("Hair"));
      this.entityData.set(FACIAL_HAIR, compound.getInt("FacialHair"));
      this.entityData.set(HAIR_COLOR, compound.getInt("HairColor"));
      this.entityData.set(EYE_PUPIL_COLOR, compound.getInt("EyePupilColor"));
      this.entityData.set(EYE_OUTER_COLOR, compound.getInt("EyeOuterColor"));
      this.entityData.set(THIRD_EYE_PUPIL_COLOR, compound.getInt("ThirdEyePupilColor"));
      this.entityData.set(THIRD_EYE_OUTER_COLOR, compound.getInt("ThirdEyeOuterColor"));
      this.entityData.set(EYE_BROW, compound.getInt("EyeBrow"));
      this.entityData.set(EYE_LINER, compound.getInt("EyeLiner"));
      this.entityData.set(EYE_LINER_COLOR, compound.getInt("EyeLinerColor"));
      this.entityData.set(TEETH, compound.getInt("Teeth"));
      this.entityData.set(TOP, compound.getInt("Top"));
      this.entityData.set(TOP_COLOR, compound.getInt("TopColor"));
      this.entityData.set(BOTTOM, compound.getInt("Bottom"));
      this.entityData.set(BOTTOM_COLOR, compound.getInt("BottomColor"));
      this.entityData.set(SHOE, compound.getInt("Shoe"));
      this.entityData.set(SHOE_COLOR, compound.getInt("ShoeColor"));
      this.entityData.set(WINGS, compound.getInt("Wings"));
      this.entityData.set(COAT, compound.getInt("Coat"));
      this.entityData.set(COAT_COLOR, compound.getInt("CoatColor"));
      this.entityData.set(NECK_ACCESSORY, compound.getInt("NeckAccessory"));
      this.entityData.set(NECK_COLOR, compound.getInt("NeckColor"));
      this.entityData.set(ARMBAND_COLOR, compound.getInt("ArmbandColor"));
   }

   @NotNull
   public DaemonVariant.Linage getVariant() {
      return DaemonVariant.Linage.byId((Integer)this.entityData.get(LINAGE));
   }

   public void setVariant(DaemonVariant.Linage linage) {
      this.entityData.set(LINAGE, linage.getId() & 0xFF);
   }

   public void setVariant(int linage) {
      this.entityData.set(LINAGE, linage);
   }

   public DaemonVariant.Gender getGender() {
      return DaemonVariant.Gender.byId((Integer)this.entityData.get(GENDER));
   }

   public void setGender(int gender) {
      this.entityData.set(GENDER, gender);
   }

   public DaemonVariant.Horn getHorn() {
      return DaemonVariant.Horn.byId((Integer)this.entityData.get(HORN));
   }

   public void setHorn(int horn) {
      this.entityData.set(HORN, horn);
   }

   public DaemonVariant.Horn getSecondHorn() {
      return DaemonVariant.Horn.byId((Integer)this.entityData.get(SECOND_HORN));
   }

   public void setSecondHorn(int horn) {
      this.entityData.set(SECOND_HORN, horn);
   }

   public DaemonVariant.Horn getThirdHorn() {
      return DaemonVariant.Horn.byId((Integer)this.entityData.get(THIRD_HORN));
   }

   public void setThirdHorn(int horn) {
      this.entityData.set(THIRD_HORN, horn);
   }

   public boolean hasSecondHorn() {
      return (Integer)this.entityData.get(SECOND_HORN) != -1;
   }

   public boolean hasThirdHorn() {
      return (Integer)this.entityData.get(THIRD_HORN) != -1;
   }

   public int getHornColor() {
      return (Integer)this.entityData.get(HORN_COLOR);
   }

   public void setHornColor(int color) {
      this.entityData.set(HORN_COLOR, color);
   }

   public DaemonVariant.Hair getHair() {
      return DaemonVariant.Hair.byId((Integer)this.entityData.get(HAIR));
   }

   public void setHair(int hair) {
      this.entityData.set(HAIR, hair);
   }

   public int getHairColor() {
      return (Integer)this.entityData.get(HAIR_COLOR);
   }

   public void setHairColor(int color) {
      this.entityData.set(HAIR_COLOR, color);
   }

   public boolean hasFacialHair() {
      return (Integer)this.entityData.get(FACIAL_HAIR) != -1;
   }

   public DaemonVariant.FacialHair getFacialHair() {
      return DaemonVariant.FacialHair.byId((Integer)this.entityData.get(FACIAL_HAIR));
   }

   public void setFacialHair(int facialHair) {
      this.entityData.set(FACIAL_HAIR, facialHair);
   }

   public int getEyePupilColor() {
      return (Integer)this.entityData.get(EYE_PUPIL_COLOR);
   }

   public void setEyePupilColor(int color) {
      this.entityData.set(EYE_PUPIL_COLOR, color);
   }

   public int getEyeOuterColor() {
      return (Integer)this.entityData.get(EYE_OUTER_COLOR);
   }

   public void setEyeOuterColor(int color) {
      this.entityData.set(EYE_OUTER_COLOR, color);
   }

   public int getThirdEyePupilColor() {
      return (Integer)this.entityData.get(THIRD_EYE_PUPIL_COLOR);
   }

   public void setThirdEyePupilColor(int color) {
      this.entityData.set(THIRD_EYE_PUPIL_COLOR, color);
   }

   public int getThirdEyeOuterColor() {
      return (Integer)this.entityData.get(THIRD_EYE_OUTER_COLOR);
   }

   public void setThirdEyeOuterColor(int color) {
      this.entityData.set(THIRD_EYE_OUTER_COLOR, color);
   }

   public DaemonVariant.EyeBrow getEyeBrow() {
      return DaemonVariant.EyeBrow.byId((Integer)this.entityData.get(EYE_BROW));
   }

   public void setEyeBrow(int brow) {
      this.entityData.set(EYE_BROW, brow);
   }

   public DaemonVariant.EyeLiner getEyeLiner() {
      return DaemonVariant.EyeLiner.byId((Integer)this.entityData.get(EYE_LINER));
   }

   public void setEyeLiner(int liner) {
      this.entityData.set(EYE_LINER, liner);
   }

   public int getEyeLinerColor() {
      return (Integer)this.entityData.get(EYE_LINER_COLOR);
   }

   public void setEyeLinerColor(int color) {
      this.entityData.set(EYE_LINER_COLOR, color);
   }

   public boolean hasTeeth() {
      return (Integer)this.entityData.get(TEETH) != -1;
   }

   public DaemonVariant.Teeth getTeeth() {
      return DaemonVariant.Teeth.byId((Integer)this.entityData.get(TEETH));
   }

   public void setTeeth(int teeth) {
      this.entityData.set(TEETH, teeth);
   }

   public DaemonVariant.Top getTop() {
      return DaemonVariant.Top.byId((Integer)this.entityData.get(TOP));
   }

   public void setTop(int top) {
      this.entityData.set(TOP, top);
   }

   public int getTopColor() {
      return (Integer)this.entityData.get(TOP_COLOR);
   }

   public void setTopColor(int color) {
      this.entityData.set(TOP_COLOR, color);
   }

   public DaemonVariant.Bottom getBottom() {
      return DaemonVariant.Bottom.byId((Integer)this.entityData.get(BOTTOM));
   }

   public void setBottom(int bottom) {
      this.entityData.set(BOTTOM, bottom);
   }

   public int getBottomColor() {
      return (Integer)this.entityData.get(BOTTOM_COLOR);
   }

   public void setBottomColor(int color) {
      this.entityData.set(BOTTOM_COLOR, color);
   }

   public DaemonVariant.Shoe getShoe() {
      return DaemonVariant.Shoe.byId((Integer)this.entityData.get(SHOE));
   }

   public void setShoe(int shoe) {
      this.entityData.set(SHOE, shoe);
   }

   public int getShoeColor() {
      return (Integer)this.entityData.get(SHOE_COLOR);
   }

   public void setShoeColor(int color) {
      this.entityData.set(SHOE_COLOR, color);
   }

   public DaemonVariant.Wings getWings() {
      return DaemonVariant.Wings.byId((Integer)this.entityData.get(WINGS));
   }

   public void setWings(int wings) {
      this.entityData.set(WINGS, wings);
   }

   public DaemonVariant.Coat getCoat() {
      return DaemonVariant.Coat.byId((Integer)this.entityData.get(COAT));
   }

   public void setCoat(int coat) {
      this.entityData.set(COAT, coat);
   }

   public int getCoatColor() {
      return (Integer)this.entityData.get(COAT_COLOR);
   }

   public void setCoatColor(int color) {
      this.entityData.set(COAT_COLOR, color);
   }

   public DaemonVariant.NeckAccessory getNeckAccessory() {
      return DaemonVariant.NeckAccessory.byId((Integer)this.entityData.get(NECK_ACCESSORY));
   }

   public void setNeckAccessory(int accessory) {
      this.entityData.set(NECK_ACCESSORY, accessory);
   }

   public int getNeckColor() {
      return (Integer)this.entityData.get(NECK_COLOR);
   }

   public void setNeckColor(int color) {
      this.entityData.set(NECK_COLOR, color);
   }

   public int getArmbandColor() {
      return (Integer)this.entityData.get(ARMBAND_COLOR);
   }

   public void setArmbandColor(int color) {
      this.entityData.set(ARMBAND_COLOR, color);
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

   public void shootFireBall(@NotNull LivingEntity target, float v) {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.FIRE_BALL.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            this.lookAt(Anchor.EYES, target.getEyePosition());
            FireBallProjectile ball = new FireBallProjectile(this.level(), this);
            ball.setSkill(instance);
            ball.setSize(3.0F);
            float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
            double xOffset = Mth.sin((float)(Math.PI + angle));
            double zOffset = Mth.cos(angle);
            ball.moveTo(this.getX() + xOffset, this.getEyeY(), this.getZ() + zOffset, this.getYRot(), this.getXRot());
            ball.setNoGravity(true);
            ball.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            ball.setSecondaryDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 4.0));
            ball.setExplosionRadius(6.0F);
            ball.setHitRadius(6.0F);
            ball.setBurnTicks(100);
            ball.setImpactParticleCount(4);
            ball.shootToward(target, v, 0.0F);
            this.level().addFreshEntity(ball);
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void shootAcidShell(@NotNull LivingEntity target, float v) {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.ACID_SHELL.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            this.lookAt(Anchor.EYES, target.getEyePosition());
            float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
            double xOffset = Mth.sin((float)(Math.PI + angle));
            double zOffset = Mth.cos(angle);
            Vec3 vec3 = new Vec3(this.getX() + xOffset, this.getEyeY(), this.getZ() + zOffset);
            int arrowRot = 36;

            for (int i = 0; i < 10; i++) {
               Vec3 offset = new Vec3(0.0, 2.0, 0.0)
                  .zRot((arrowRot * i - arrowRot / 2.0F) * (float) (Math.PI / 180.0))
                  .xRot(-this.getXRot() * (float) (Math.PI / 180.0))
                  .yRot(-this.getYRot() * (float) (Math.PI / 180.0));
               AcidBallProjectile ball = new AcidBallProjectile(this.level(), this);
               ball.setSize(1.0F);
               ball.setNoGravity(true);
               ball.setPos(vec3.add(offset));
               ball.setArmorHurt(300);
               ball.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
               ball.setSecondaryDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
               ball.setHitRadius(3.0F);
               MobEffectInstance corrosion = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), 400, 1, true, false, true);
               ball.setMobEffect(corrosion);
               ball.setBurnTicks(-1);
               ball.setSkill(instance);
               ball.shootToward(target, offset.add(0.0, target.getEyeHeight(), 0.0), v, 0.0F);
               this.level().addFreshEntity(ball);
            }

            AcidBallProjectile ball = new AcidBallProjectile(this.level(), this);
            ball.setSize(1.0F);
            ball.setNoGravity(true);
            ball.setPos(vec3);
            ball.setArmorHurt(300);
            ball.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            ball.setSecondaryDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
            ball.setHitRadius(3.0F);
            MobEffectInstance corrosion = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), 400, 1, true, false, true);
            ball.setMobEffect(corrosion);
            ball.setBurnTicks(-1);
            ball.setSkill(instance);
            ball.shootToward(target, v, 0.0F);
            this.level().addFreshEntity(ball);
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void shootIceBreaker(@NotNull LivingEntity target, float v) {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.ICE_BREAKER.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            this.lookAt(Anchor.EYES, target.getEyePosition());
            IceLanceProjectile lance = new IceLanceProjectile(this.level(), this);
            lance.setSkill(instance);
            lance.setSize(4.0F);
            float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
            double xOffset = Mth.sin((float)(Math.PI + angle));
            double zOffset = Mth.cos(angle);
            lance.moveTo(this.getX() + xOffset, this.getEyeY(), this.getZ() + zOffset, this.getYRot(), this.getXRot());
            lance.setNoGravity(true);
            lance.setHitRadius(4.0F);
            lance.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            lance.setSecondaryDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
            lance.setIceBreaker(true);
            lance.setFrostBonusDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
            lance.setChillBonusDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            lance.shootToward(target, v, 0.0F);
            this.level().addFreshEntity(lance);
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_ICE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void shootTornadoBlade(@NotNull LivingEntity target, float v) {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.TORNADO_BLADE.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            this.lookAt(Anchor.EYES, target.getEyePosition());
            WindTornadoProjectile projectile = new WindTornadoProjectile(this.level(), this);
            projectile.setSkill(instance);
            projectile.setSize(1.0F);
            float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
            double xOffset = Mth.sin((float)(Math.PI + angle));
            double zOffset = Mth.cos(angle);
            projectile.moveTo(this.getX() + xOffset, this.getEyeY(), this.getZ() + zOffset, this.getYRot(), this.getXRot());
            projectile.setNoGravity(true);
            projectile.setBurstDelay(33);
            projectile.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            projectile.setSecondaryDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
            projectile.setHitRadius(5.0F);
            projectile.setPullForce(0.1F);
            projectile.setOnExplodeBlades(10);
            projectile.setBladeDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            projectile.shootToward(target, Vec3.ZERO, v, 0.0F);
            this.level().addFreshEntity(projectile);
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void spawnWaterJail(@NotNull LivingEntity target) {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.WATER_JAIL.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            this.lookAt(Anchor.EYES, target.getEyePosition());
            WaterJailEntity jail = new WaterJailEntity(this.level(), this);
            jail.setSkill(instance);
            jail.setLife(240);
            jail.setHealth(this.getMaxHealth() / 2.0F);
            jail.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            jail.setSecondaryDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
            jail.setTickEachHit(40);
            jail.setSize(3.0F);
            jail.setPos(target.getX(), target.getY() - 1.5, target.getZ());
            this.level().addFreshEntity(jail);
            jail.triggerAnim("controller", "start");
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }

   public void spawnMudSpears(@NotNull LivingEntity target) {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.MUD_SPEARS.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            this.lookAt(Anchor.EYES, target.getEyePosition());
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            Vec3 center = target.position();
            int amount = 9;

            for (int i = 0; i < amount; i++) {
               double angle = (Math.PI * 2) / amount * i;
               double dx = Math.cos(angle) * 3.0;
               double dz = Math.sin(angle) * 3.0;
               double x = center.x() + dx;
               double y = center.y();
               double z = center.z() + dz;
               MudSpikeEntity spike = new MudSpikeEntity(this.level(), this);
               spike.setSkill(instance);
               spike.setPos(x, y, z);
               spike.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
               spike.setSecondaryDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
               spike.setContactDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
               spike.setLife(300);
               spike.setYaw(-30.0F);
               spike.setPitch(ObjectSelectionHelper.getYRotFromVector(center.subtract(spike.position()).normalize()));
               spike.setSize(1.0F);
               this.level().addFreshEntity(spike);
               spike.triggerAnim("controller", "start");
               EffectStorage.setCameraShake(spike, 3.0, 0.01F, 10);
               this.level()
                  .playSound(
                     null, spike.getX(), spike.getY(), spike.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }
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
                  target.hurt(source, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
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
                  projectile.setSecondaryDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
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

   protected void thunderOrb() {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.THUNDER_ORB.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            int orbID = instance.getOrCreateTag().getInt("orbID");
            if (orbID == 0) {
               ThunderSphereProjectile orb = new ThunderSphereProjectile(this.level(), this);
               orb.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
               orb.setSecondaryDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0));
               orb.setStrikeRadius(10.0F);
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
               if (!(entity instanceof ThunderSphereProjectile)) {
                  instance.getOrCreateTag().putInt("orbID", 0);
                  this.thunderOrb();
               }
            }

            this.level()
               .playSound(
                  null, this, (SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get(), TensuraSkill.ABILITY_SOUND, 10.0F, 0.95F + this.random.nextFloat() * 0.1F
               );
         }
      }
   }

   public void activateBarrier() {
      if (this.canCastMagics(this)) {
         ManasSkillInstance instance = this.getMagic(this, (Magic)AspectualMagics.REINFORCED_BARRIER.get());
         if (instance == null) {
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
            MobEffectInstance magicBarrier = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_BARRIER), 200, 0, true, false, true);
            CompoundTag magicTag = magicBarrier.tensura$getOrCreateTag();
            magicTag.putFloat("DamageThreshold", this.getMaxHealth() / 5.0F);
            magicTag.putFloat("UnderReduction", this.getMaxHealth() / 5.0F);
            magicTag.putFloat("AboveReduction", this.getMaxHealth() / 10.0F);
            this.addEffect(magicBarrier, this);
            MobEffectInstance physicalBarrier = new MobEffectInstance(
               TensuraMobEffects.getReference(TensuraMobEffects.PHYSICAL_BARRIER), 200, 0, true, false, true
            );
            CompoundTag physicalTag = physicalBarrier.tensura$getOrCreateTag();
            physicalTag.putFloat("DamageThreshold", this.getMaxHealth() / 5.0F);
            physicalTag.putFloat("UnderReduction", this.getMaxHealth() / 5.0F);
            physicalTag.putFloat("AboveReduction", this.getMaxHealth() / 10.0F);
            this.addEffect(physicalBarrier, this);
            this.heal(this.getMaxHealth() * 0.3F);
            this.level()
               .playSound(
                  null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      }
   }

   @Override
   public int getChestSlots() {
      return 27;
   }

   @Override
   public int getMenuRenderSize() {
      return 25;
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
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.archDaemon, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         this.applyRandomVariant();
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   private void applyRandomVariant() {
      RandomSource source = this.getRandom();
      DaemonConfig.ArchDaemon config = CONFIG.ArchDaemon;
      AttributeInstance scale = this.getAttribute(Attributes.SCALE);
      if (scale != null) {
         scale.setBaseValue(0.75 + source.nextFloat() * 0.5F);
      }

      DaemonVariant.Linage linage = DaemonVariant.Linage.getRandom(this);
      int color = linage.getBaseColor();
      this.setVariant(linage.getId());
      DaemonVariant.Gender gender = source.nextBoolean() ? DaemonVariant.Gender.MALE : DaemonVariant.Gender.FEMALE;
      this.setGender(gender.getId());
      int horns = DaemonVariant.Horn.values().length;
      if (source.nextInt(horns) != 0) {
         List<Integer> hornColors = config.hornColors;
         int hornColor = source.nextBoolean() ? color : hornColors.get(source.nextInt(hornColors.size()));
         this.setHornColor(hornColor);
         DaemonVariant.Horn horn = DaemonVariant.Horn.getRandom(this);
         this.setHorn(horn.getId());
         if (source.nextFloat() <= 0.01) {
            DaemonVariant.Horn secondHorn = DaemonVariant.Horn.getRandom(this, horn);
            if (secondHorn != null) {
               this.setSecondHorn(secondHorn.getId());
               if (source.nextFloat() <= 0.01) {
                  DaemonVariant.Horn thirdHorn = DaemonVariant.Horn.getRandom(this, horn, secondHorn);
                  if (thirdHorn != null) {
                     this.setThirdHorn(thirdHorn.getId());
                  }
               }
            }
         }
      }

      List<Integer> hairColors = config.hairColors;
      this.setHair(DaemonVariant.Hair.getRandom(this));
      int hairColor = source.nextBoolean() ? color : hairColors.get(source.nextInt(hairColors.size()));
      this.setHairColor(hairColor);
      if (gender == DaemonVariant.Gender.MALE) {
         this.setFacialHair(DaemonVariant.FacialHair.getRandom(this));
         this.setEyeBrow(source.nextBoolean() ? DaemonVariant.EyeBrow.NORMAL.getId() : DaemonVariant.EyeBrow.FULL.getId());
      } else {
         this.setEyeBrow(source.nextBoolean() ? DaemonVariant.EyeBrow.FEMININE.getId() : DaemonVariant.EyeBrow.NORMAL.getId());
      }

      List<Integer> eyeColors = config.eyeColors;
      if (source.nextBoolean()) {
         this.setEyePupilColor(eyeColors.get(source.nextInt(eyeColors.size())));
         this.setEyeOuterColor(eyeColors.get(source.nextInt(eyeColors.size())));
      } else {
         this.setEyePupilColor(TensuraColors.getTonedARGB(color, 0.5F));
         this.setEyeOuterColor(color);
      }

      if (source.nextFloat() <= 0.2) {
         this.setThirdEyePupilColor(eyeColors.get(source.nextInt(eyeColors.size())));
         this.setThirdEyeOuterColor(eyeColors.get(source.nextInt(eyeColors.size())));
      }

      int liners = DaemonVariant.EyeLiner.values().length;
      if (source.nextInt(liners) != 0) {
         this.setEyeLiner(DaemonVariant.EyeLiner.getRandom(this));
         List<Integer> eyeLinerColors = config.eyeLinerColors;
         this.setEyeLinerColor(eyeLinerColors.get(source.nextInt(eyeLinerColors.size())));
      }

      int teeth = DaemonVariant.Teeth.values().length;
      if (source.nextInt(teeth) != 0) {
         this.setTeeth(DaemonVariant.Teeth.getRandom(this));
      }

      this.setWings(DaemonVariant.Wings.getRandom(this));
      List<Integer> topColors = config.topClothesColors;
      this.setTop(DaemonVariant.Top.getRandom(this));
      this.setTopColor(topColors.get(source.nextInt(topColors.size())));
      List<Integer> bottomColors = config.bottomClothesColors;
      this.setBottom(DaemonVariant.Bottom.getRandom(this));
      this.setBottomColor(bottomColors.get(source.nextInt(bottomColors.size())));
      List<Integer> shoeColors = config.bootsColors;
      this.setShoe(DaemonVariant.Shoe.getRandom(this));
      this.setShoeColor(shoeColors.get(source.nextInt(shoeColors.size())));
      if (source.nextBoolean()) {
         List<Integer> coatColors = config.coatColors;
         this.setCoat(DaemonVariant.Coat.getRandom(this));
         this.setCoatColor(coatColors.get(source.nextInt(coatColors.size())));
      }

      if (source.nextBoolean()) {
         List<Integer> neckColors = config.neckAccessoryColors;
         this.setNeckAccessory(DaemonVariant.NeckAccessory.getRandom(this));
         this.setNeckColor(neckColors.get(source.nextInt(neckColors.size())));
      }

      if (source.nextFloat() <= 0.2) {
         List<Integer> armbandColors = config.armbandColors;
         this.setArmbandColor(armbandColors.get(source.nextInt(armbandColors.size())));
      }
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.DAEMON_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.DAEMON_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.DAEMON_DEATH.get();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<ArchDaemonEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<ArchDaemonEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<ArchDaemonEntity> getIdleTasks() {
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

   public BrainActivityGroup<ArchDaemonEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new OrbitAttack()
               .lookAtTargetWhileOrbiting(true)
               .speedMod((entity, target) -> 2.0F)
               .orbitRadius((entity, target) -> 30.0)
               .orbitMinRadius((entity, target) -> 20.0)
               .orbitHeight((entity, target) -> 20.0)
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
                     new CustomRangeAttack(15)
                        .maxAttackRadius(32.0F)
                        .attackInterval(entity -> 10)
                        .performAttack((entity, target) -> entity.activateBarrier())
                        .whenStarting(
                           entity -> {
                              entity.triggerAnim("miscController", "burst");
                              ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.REINFORCED_BARRIER.get());
                              if (instance != null) {
                                 MagicCircle.castMagicCircle(
                                    4.0F,
                                    25,
                                    MagicCircleVariant.BARRIER,
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
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.REINFORCED_BARRIER.get()) != null
                              && entity.getHealth() < entity.getMaxHealth()
                              && entity.getRandom().nextFloat() < 0.2F
                        ),
                     new CustomRangeAttack(10)
                        .maxAttackRadius(10.0F)
                        .attackInterval(entity -> 20)
                        .performAttack(ArchDaemonEntity::spawnWaterJail)
                        .whenStarting(entity -> {
                           entity.triggerAnim("attackController", entity.getRandom().nextBoolean() ? "swing_right" : "swing_left");
                           ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.WATER_JAIL.get());
                           if (instance != null) {
                              MagicCircle.castMagicCircle(
                                 2.0F, 30, MagicCircleVariant.WATER, entity, new CompoundTag(), 3.0F, Vec3.ZERO, instance, 0, Pair.of(0.0, 100.0)
                              );
                           }
                        })
                        .startCondition(
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.WATER_JAIL.get()) != null && entity.getRandom().nextFloat() < 0.2F
                        ),
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
                        .performAttack((entity, target) -> entity.shootAcidShell(target, 2.0F))
                        .whenStarting(
                           entity -> {
                              entity.triggerAnim("miscController", "magic_shoot");
                              ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.ACID_SHELL.get());
                              if (instance != null) {
                                 MagicCircle.castMagicCircle(
                                    5.0F, 25, MagicCircleVariant.WATER, entity, new CompoundTag(), -2.0F, Vec3.ZERO, instance, 0, Pair.of(0.0, 100.0)
                                 );
                              }
                           }
                        )
                        .startCondition(
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.ACID_SHELL.get()) != null && entity.getRandom().nextFloat() < 0.3F
                        ),
                     new CustomRangeAttack(15)
                        .maxAttackRadius(40.0F)
                        .attackInterval(entity -> 20)
                        .performAttack((entity, target) -> entity.shootFireBall(target, 1.5F))
                        .whenStarting(entity -> {
                           entity.triggerAnim("miscController", "magic_strong");
                           ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.FIRE_BALL.get());
                           if (instance != null) {
                              MagicCircle.castMagicCircle(
                                 3.0F, 25, MagicCircleVariant.FLAME, entity, new CompoundTag(), 3.0F, Vec3.ZERO, instance, 0, Pair.of(0.0, 100.0)
                              );
                           }
                        })
                        .startCondition(
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.FIRE_BALL.get()) != null && entity.getRandom().nextFloat() < 0.3F
                        ),
                     new CustomRangeAttack(25)
                        .maxAttackRadius(40.0F)
                        .attackInterval(entity -> 20)
                        .performAttack((entity, target) -> entity.shootIceBreaker(target, 1.5F))
                        .whenStarting(entity -> {
                           entity.triggerAnim("miscController", "magic_force");
                           ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.ICE_BREAKER.get());
                           if (instance != null) {
                              MagicCircle.castMagicCircle(
                                 4.0F, 25, MagicCircleVariant.ICE, entity, new CompoundTag(), 4.0F, Vec3.ZERO, instance, 0, Pair.of(0.0, 100.0)
                              );
                           }
                        })
                        .startCondition(
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.ICE_BREAKER.get()) != null && entity.getRandom().nextFloat() < 0.3F
                        ),
                     new CustomRangeAttack(15)
                        .maxAttackRadius(40.0F)
                        .attackInterval(entity -> 20)
                        .performAttack((entity, target) -> entity.shootTornadoBlade(target, 1.0F))
                        .whenStarting(entity -> {
                           entity.triggerAnim("attackController", entity.getRandom().nextBoolean() ? "swing_right" : "swing_left");
                           ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.TORNADO_BLADE.get());
                           if (instance != null) {
                              MagicCircle.castMagicCircle(
                                 2.5F, 25, MagicCircleVariant.WIND, entity, new CompoundTag(), 3.0F, Vec3.ZERO, instance, 0, Pair.of(0.0, 100.0)
                              );
                           }
                        })
                        .startCondition(
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.TORNADO_BLADE.get()) != null && entity.getRandom().nextFloat() < 0.3F
                        ),
                     new CustomRangeAttack(15)
                        .maxAttackRadius(40.0F)
                        .attackInterval(entity -> 20)
                        .performAttack(ArchDaemonEntity::spawnMudSpears)
                        .whenStarting(entity -> {
                           entity.triggerAnim("attackController", entity.getRandom().nextBoolean() ? "swing_right" : "swing_left");
                           ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.MUD_SPEARS.get());
                           if (instance != null) {
                              MagicCircle.castMagicCircle(
                                 2.5F, 25, MagicCircleVariant.EARTH, entity, new CompoundTag(), 3.0F, Vec3.ZERO, instance, 0, Pair.of(0.0, 100.0)
                              );
                           }
                        })
                        .startCondition(
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.MUD_SPEARS.get()) != null
                              && entity.getRandom().nextFloat() < 0.3F
                              && entity.getTarget() != null
                              && entity.getTarget().onGround()
                        ),
                     new CustomHeldAttack()
                        .minAttackRadius(0.0F)
                        .maxAttackRadius(40.0F)
                        .attackInterval(entity -> 40)
                        .onTick((entity, target, tick) -> {
                           if (tick >= 15 && tick <= 45) {
                              entity.thunderOrb();
                           }

                           return tick < 60;
                        })
                        .whenStarting(
                           entity -> {
                              entity.triggerAnim("miscController", "magic_big");
                              ManasSkillInstance instance = entity.getMagic(entity, (Magic)AspectualMagics.THUNDER_ORB.get());
                              if (instance != null) {
                                 MagicCircle.castMagicCircle(
                                    4.0F,
                                    45,
                                    MagicCircleVariant.LIGHTNING,
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
                           entity -> entity.getMagic(entity, (Magic)AspectualMagics.THUNDER_ORB.get()) != null && entity.getRandom().nextFloat() < 0.2F
                        )
                  }
               )
               .startCondition(entity -> entity.canCastMagics(entity))
         }
      );
   }

   protected PlayState loopController(AnimationState<ArchDaemonEntity> state) {
      String name;
      if (this.isNoAi()) {
         name = "animation.arch_daemon.concealed";
      } else if (this.isSleeping()) {
         name = "animation.arch_daemon.sleep";
      } else if (!this.isAlive()) {
         name = "animation.arch_daemon.burst_quick";
      } else if (this.isInSittingPose()) {
         name = "animation.arch_daemon.stay";
      } else if (state.isMoving()) {
         if (this.onGround()) {
            name = "animation.arch_daemon.walk";
         } else {
            name = "animation.arch_daemon.fly";
         }
      } else if (this.onGround()) {
         name = "animation.arch_daemon.idle";
      } else {
         name = "animation.arch_daemon.idle_fly";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController)
               .triggerableAnim("conceal", RawAnimation.begin().then("animation.arch_daemon.conceal", LoopType.PLAY_ONCE))
               .triggerableAnim("conceal_off", RawAnimation.begin().then("animation.arch_daemon.conceal_off", LoopType.PLAY_ONCE)),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("magic_shoot", RawAnimation.begin().then("animation.arch_daemon.magic_shoot", LoopType.PLAY_ONCE))
               .triggerableAnim("magic_big", RawAnimation.begin().then("animation.arch_daemon.magic_big", LoopType.PLAY_ONCE))
               .triggerableAnim("magic_strong", RawAnimation.begin().then("animation.arch_daemon.magic_big", LoopType.PLAY_ONCE))
               .triggerableAnim("magic_force", RawAnimation.begin().then("animation.arch_daemon.magic_big", LoopType.PLAY_ONCE))
               .triggerableAnim("burst", RawAnimation.begin().then("animation.arch_daemon.burst", LoopType.PLAY_ONCE)),
            new AnimationController(this, "attackController", 3, event -> PlayState.STOP)
               .setAnimationSpeed(2.0)
               .triggerableAnim("attack_left", RawAnimation.begin().then("animation.arch_daemon.attack_left", LoopType.PLAY_ONCE))
               .triggerableAnim("attack_right", RawAnimation.begin().then("animation.arch_daemon.attack_right", LoopType.PLAY_ONCE))
               .triggerableAnim("swing_left", RawAnimation.begin().then("animation.arch_daemon.swing_left", LoopType.PLAY_ONCE))
               .triggerableAnim("swing_right", RawAnimation.begin().then("animation.arch_daemon.swing_right", LoopType.PLAY_ONCE))
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
