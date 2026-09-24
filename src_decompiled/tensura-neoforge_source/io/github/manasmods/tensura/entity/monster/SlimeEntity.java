package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.advancement.ItemUsedOnEntityTrigger;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.existence.EntityExistenceData;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.LeapToTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IGiantMob;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.entity.variant.SlimeColor;
import io.github.manasmods.tensura.entity.variant.SlimeType;
import io.github.manasmods.tensura.race.slime.SlimeRace;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.navigation.SmoothAmphibiousPathNavigation;
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

public class SlimeEntity
   extends TensuraMountEntity
   implements GeoEntity,
   SmartBrainOwner<SlimeEntity>,
   Bucketable,
   IGiantMob,
   VariantHolder<SlimeType>,
   INameEvolution {
   public static final int MAX_SIZE = 30;
   public static final int DEFAULT_SIZE = 3;
   public static final int MASSIVE_SIZE = 18;
   protected static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(SlimeEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(SlimeEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Integer> SLIME_TYPE = SynchedEntityData.defineId(SlimeEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(SlimeEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> SUPPER_MASSIVE = SynchedEntityData.defineId(SlimeEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> CHILLED = SynchedEntityData.defineId(SlimeEntity.class, EntityDataSerializers.BOOLEAN);
   public boolean wasOnGround;
   public static final int MAX_PRODUCE = 3;
   public int slimeballTime = this.random.nextInt(6000) + 6000;
   public int slimeProduceTime = this.random.nextInt(12000) + 12000;
   public int producedTimes = 0;
   public int selfRegen = 20;
   public int trappingCoolDown = 120;
   public boolean canTrap = false;
   protected boolean trappingTarget = false;
   protected final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public SlimeEntity(EntityType<? extends SlimeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @NotNull
   @Override
   protected PathNavigation createNavigation(Level level) {
      return new SmoothAmphibiousPathNavigation(this, level);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.MAX_HEALTH, 5.0)
         .add(Attributes.ATTACK_DAMAGE, 0.5)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.05F)
         .add(Attributes.JUMP_STRENGTH, 0.6F)
         .add(Attributes.STEP_HEIGHT, 2.0)
         .add(Attributes.SAFE_FALL_DISTANCE, 20.0)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 4.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SIZE, 1.0F);
      builder.define(DATA_ID_TYPE_VARIANT, 0);
      builder.define(SLIME_TYPE, 0);
      builder.define(FROM_BUCKET, false);
      builder.define(SUPPER_MASSIVE, Boolean.FALSE);
      builder.define(CHILLED, Boolean.FALSE);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("Size", this.getSize());
      pCompound.putBoolean("wasOnGround", this.wasOnGround);
      pCompound.putInt("Variant", this.getTypeColor());
      pCompound.putInt("Type", this.getTypeVariant());
      pCompound.putInt("RegenTime", this.selfRegen);
      pCompound.putInt("SlimeballTime", this.slimeballTime);
      pCompound.putInt("SlimeProduceTime", this.slimeProduceTime);
      pCompound.putInt("ProduceTimes", this.producedTimes);
      pCompound.putBoolean("FromBucket", this.fromBucket());
      pCompound.putBoolean("Chilled", this.isChilled());
      pCompound.putBoolean("SuperMassive", this.isMassive());
      pCompound.putInt("TrappingCooldown", this.trappingCoolDown);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.entityData.set(SIZE, pCompound.getFloat("Size"));
      this.wasOnGround = pCompound.getBoolean("wasOnGround");
      this.entityData.set(DATA_ID_TYPE_VARIANT, pCompound.getInt("Variant"));
      this.entityData.set(SLIME_TYPE, pCompound.getInt("Type"));
      if (pCompound.contains("RegenTime")) {
         this.selfRegen = pCompound.getInt("RegenTime");
      }

      if (pCompound.contains("SlimeballTime")) {
         this.slimeballTime = pCompound.getInt("SlimeballTime");
      }

      if (pCompound.contains("SlimeProduceTime")) {
         this.slimeProduceTime = pCompound.getInt("SlimeProduceTime");
      }

      if (pCompound.contains("ProduceTimes")) {
         this.producedTimes = pCompound.getInt("ProduceTimes");
      }

      this.setFromBucket(pCompound.getBoolean("FromBucket"));
      this.entityData.set(SUPPER_MASSIVE, pCompound.getBoolean("SuperMassive"));
      if (pCompound.contains("TrappingCooldown")) {
         this.trappingCoolDown = pCompound.getInt("TrappingCooldown");
      }

      this.setChilled(pCompound.getBoolean("Chilled"));
   }

   public boolean isMassive() {
      return (Boolean)this.entityData.get(SUPPER_MASSIVE);
   }

   public void setMassive(boolean massive) {
      this.entityData.set(SUPPER_MASSIVE, massive);
   }

   private void applyMassiveAbilities() {
      this.addBaseValue(Attributes.STEP_HEIGHT, 1.0);
      this.addBaseValue(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, 4.0);
      this.addBaseValue(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER, 4.0);
      Registry<EntityExistenceData> registry = this.level().registryAccess().registryOrThrow(TensuraCustomData.ENTITY_EXISTENCE);
      registry.stream()
         .filter(entityExistenceData -> entityExistenceData.entity().equals(((EntityType)MonsterEntityTypes.SUPERMASSIVE_SLIME.get()).arch$registryName()))
         .findFirst()
         .ifPresent(
            data -> {
               IExistence existence = TensuraStorages.getExistenceFrom(this);
               AttributeInstance shp = this.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
               if (shp != null) {
                  if (shp.getBaseValue() < data.spiritualHP()) {
                     shp.setBaseValue(data.spiritualHP());
                  }

                  existence.setSpiritualHealth(data.spiritualHP());
               }

               AttributeInstance aura = this.getAttribute(TensuraAttributes.MAX_AURA);
               if (aura != null) {
                  double maxAura = data.maxAura() <= data.minAura() ? data.minAura() : this.getRandom().nextInt(data.minAura(), data.maxAura());
                  if (aura.getBaseValue() < maxAura) {
                     aura.setBaseValue(maxAura);
                  }

                  existence.setAura(maxAura);
               }

               AttributeInstance magicule = this.getAttribute(TensuraAttributes.MAX_MAGICULE);
               if (magicule != null) {
                  double maxMagicule = data.maxMagicule() <= data.minMagicule()
                     ? data.minMagicule()
                     : this.getRandom().nextInt(data.minMagicule(), data.maxMagicule());
                  if (magicule.getBaseValue() < maxMagicule) {
                     magicule.setBaseValue(maxMagicule);
                  }

                  existence.setMagicule(maxMagicule);
               }

               if (data.abilities().isPresent()) {
                  for (ResourceLocation ability : data.abilities().get()) {
                     ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(ability);
                     if (skill != null) {
                        ManasSkillInstance instance = new TensuraSkillInstance(skill);
                        instance.getOrCreateTag().putBoolean("NoMagiculeCost", true);
                        SkillHelper.learnSkill(this, instance);
                        if (instance.canBeToggled(this)) {
                           instance.setToggled(true);
                           instance.onToggleOn(this);
                        }
                     }
                  }
               }

               if (data.abilitiesRandom().isPresent()) {
                  for (Entry<ResourceLocation, Double> entry : data.abilitiesRandom().get().entrySet()) {
                     if (!(this.getRandom().nextFloat() > entry.getValue())) {
                        ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(entry.getKey());
                        if (skill != null) {
                           ManasSkillInstance instance = new TensuraSkillInstance(skill);
                           instance.getOrCreateTag().putBoolean("NoMagiculeCost", true);
                           SkillHelper.learnSkill(this, instance);
                           if (instance.canBeToggled(this)) {
                              instance.setToggled(true);
                              instance.onToggleOn(this);
                           }
                        }
                     }
                  }
               }

               existence.setSkippingEPDrop(false);
               existence.markDirty();
            }
         );
   }

   public boolean isChilled() {
      return (Boolean)this.entityData.get(CHILLED);
   }

   public void setChilled(boolean chilled) {
      this.entityData.set(CHILLED, chilled);
   }

   public SlimeColor getColor() {
      return SlimeColor.byId(this.getTypeColor() & 0xFF);
   }

   private int getTypeColor() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public void setColor(SlimeColor variant) {
      if (this.isDyeable()) {
         this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 0xFF);
      }
   }

   public SlimeType getVariant() {
      return SlimeType.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(SLIME_TYPE);
   }

   public void setVariant(SlimeType type) {
      this.entityData.set(SLIME_TYPE, type.getId() & 0xFF);
   }

   public float getSize() {
      return (Float)this.entityData.get(SIZE);
   }

   public void setSize(float pSize, boolean pResetHealth) {
      this.setSize(pSize, true, pResetHealth, true);
   }

   public void setSize(float pSize, boolean resetStat, boolean pResetHealth, boolean resetEP) {
      double oldSize = this.getSize();
      this.entityData.set(SIZE, pSize);
      this.reapplyPosition();
      this.refreshDimensions();
      if (resetStat) {
         double difference = pSize - oldSize;
         this.addBaseValue(Attributes.MAX_HEALTH, 2.0 * difference);
         this.addBaseValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH, 4.0 * difference);
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(Math.min(0.08F + 0.03F * pSize, 0.5F));
         if (pSize <= 4.0F) {
            this.addBaseValue(Attributes.ATTACK_DAMAGE, 0.05F * difference);
            this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(0.5);
         } else {
            this.addBaseValue(Attributes.ATTACK_DAMAGE, 0.2F * difference);
            this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(Math.min(0.1 * pSize, 1.05F));
         }

         if (pResetHealth) {
            this.setHealth(this.getMaxHealth());
         }

         if (resetEP) {
            double currentEP = EnergyHelper.getBaseMaxEP(this);
            if (!(currentEP <= 0.0)) {
               double EP = pSize < 4.0F && oldSize < 4.0 ? 250.0 : 1000.0;
               EnergyHelper.increaseMaxEP(this, EP * difference);
            }
         }
      }
   }

   private void addBaseValue(Holder<Attribute> attribute, double amount) {
      AttributeInstance instance = this.getAttribute(attribute);
      if (instance != null) {
         instance.setBaseValue(instance.getBaseValue() + amount);
         if (attribute == Attributes.ATTACK_DAMAGE && instance.getBaseValue() < 0.5) {
            instance.setBaseValue(0.5);
         }
      }
   }

   @NotNull
   @Override
   public EntityDimensions getDefaultDimensions(Pose pPose) {
      return super.getDefaultDimensions(pPose).scale(this.getSize());
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (SIZE.equals(pKey)) {
         this.refreshDimensions();
         this.setYRot(this.yHeadRot);
         this.yBodyRot = this.yHeadRot;
         if (this.isInWater() && this.random.nextInt(20) == 0) {
            this.doWaterSplashEffect();
         }
      }

      super.onSyncedDataUpdated(pKey);
   }

   @Override
   public int getMenuRenderSize() {
      return (int)(Math.clamp(10.0F * this.getSize(), 20.0F, 150.0F) / this.getSize());
   }

   @Override
   protected float getRiddenJump(float f) {
      return this.getJumpPower(f) * 2.0F;
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 point = super.getPassengerAttachmentPoint(entity, entityDimensions, f);
      return entity == this.getControllingPassenger() ? point.add(0.0, 0.25, 0.0) : point.add(0.0, -2.5, 0.0);
   }

   @Override
   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   public int getMaxHeadXRot() {
      return 0;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.type().effects().equals(DamageEffects.POKING) || super.isInvulnerableTo(source);
   }

   public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return false;
   }

   public boolean hurt(DamageSource damageSource, float amount) {
      if (super.hurt(damageSource, amount * SlimeRace.getSlimePhysicalInputMultiplier(damageSource, this))) {
         this.triggerAnim("miscController", "damage");
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.onGround() && !this.wasOnGround && this.getJumpPower(1.0F) >= 0.3) {
         this.triggerAnim("miscController", "land");
         this.playSound(this.getSquishSound(), 0.1F * this.getSize(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
      }

      this.wasOnGround = this.onGround();
      if (!this.level().isClientSide && this.isAlive()) {
         if (--this.selfRegen <= 0 && this.getHealth() < this.getMaxHealth()) {
            this.selfRegen();
         }

         this.massiveSlimeTick();
         switch (this.getVariant()) {
            case PRODUCE:
               this.produceSlimeTick();
               break;
            case SUMMONED:
               this.summonedSlimeTick();
         }

         LivingEntity controller = this.getControllingPassenger();
         if (!this.isTame() && this.isAngry() || controller != null && this.isOwnedBy(controller)) {
            SimpleContainer container = this.isChested() ? this.inventory : null;
            if (this.getSize() >= 15.0) {
               this.breakBlocks(this, 1.0F, false, container);
            }
         }
      }
   }

   protected void selfRegen() {
      if (this.isMassive()) {
         this.heal(10.0F);
      } else {
         this.heal(2.0F);
      }

      this.selfRegen = 20;
   }

   private void massiveSlimeTick() {
      if (this.isMassive()) {
         if (this.trappingTarget && (this.getTarget() == null || this.getTarget() != null && this.distanceToSqr(this.getTarget()) > 4.0)) {
            this.trappingTarget = false;
         }

         if (--this.trappingCoolDown <= 0) {
            if (!this.canTrap) {
               boolean hasThrownAwayTarget = false;

               for (Entity passenger : this.getPassengers()) {
                  if (!passenger.equals(this.getControllingPassenger()) && this.isVehicle() && !(passenger instanceof Player)) {
                     passenger.stopRiding();
                     this.trappingCoolDown = 120;
                     hasThrownAwayTarget = true;
                     Vec3 throwVec = this.getViewVector(10.0F);
                     if (this.getOwner() != null && this.getControllingPassenger() == this.getOwner()) {
                        throwVec = this.getOwner().getViewVector(10.0F);
                     }

                     passenger.setDeltaMovement(passenger.getDeltaMovement().add(throwVec.x(), 1.0 + throwVec.y(), throwVec.z()));
                     this.triggerAnim("miscController", "land");
                  }
               }

               if (!hasThrownAwayTarget) {
                  this.canTrap = true;
               }
            }
         }
      }
   }

   private void summonedSlimeTick() {
      if (--this.slimeProduceTime <= 0) {
         if (this.getOwner() instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.message.slime.despawn").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withBold(true)), false
            );
         }

         this.playSound(SoundEvents.SHULKER_BULLET_HURT, 0.8F, 0.8F);
         this.setRemoved(RemovalReason.DISCARDED);
      }
   }

   private void produceSlimeTick() {
      if (this.isMassive()) {
         this.massiveSlimeTick();
      } else {
         if (--this.slimeballTime <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(Items.SLIME_BALL);
            this.slimeballTime = this.random.nextInt(6000) + 6000;
            this.triggerAnim("miscController", "damage");
         }

         if (--this.slimeProduceTime <= 0 && this.producedTimes < 3 && this.isTame()) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (this.getSize() < 3.0F) {
               this.setSize(this.getSize() + 1.0F, true);
               this.triggerAnim("miscController", "land");
            } else {
               SlimeEntity slime = new SlimeEntity((EntityType<? extends SlimeEntity>)MonsterEntityTypes.SLIME.get(), this.level());
               slime.setPos(this.getX(), this.getY(), this.getZ());
               slime.setColor(this.getColor());
               slime.setVariant(SlimeType.byId(this.random.nextInt(2)));
               if (this.isTame() && this.getOwner() instanceof Player owner) {
                  slime.tame(owner);
                  slime.setOrderedToSit(this.isOrderedToSit());
                  slime.setWandering(this.isWandering());
                  slime.setWanderPos(this.getWanderPos());
               }

               this.triggerAnim("miscController", "land");
               this.level().addFreshEntity(slime);
               this.producedTimes++;
            }

            this.slimeProduceTime = this.random.nextInt(12000) + 12000;
         }
      }
   }

   public boolean isSameOwner(TamableAnimal entity) {
      return entity.isTame() && this.isTame() && Objects.equals(this.getOwner(), entity.getOwner());
   }

   private boolean isSameSizeMergeSlime(SlimeEntity slime) {
      if (!slime.getVariant().equals(SlimeType.MERGE)) {
         return false;
      } else if (!this.getVariant().equals(SlimeType.MERGE)) {
         return false;
      } else {
         return slime.getSize() != this.getSize() ? false : this.getSize() <= 30.0F;
      }
   }

   public void playerTouch(Player pEntity) {
      if (!this.isOwnedBy(pEntity)) {
         if (this.isEffectiveAi()) {
            this.damageCollidedEntity(pEntity);
            if (this.getControllingPassenger() instanceof Player && !pEntity.equals(this.getControllingPassenger())) {
               pEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 30, 1, false, false, false), this);
               pEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30, 1, false, false, false), this);
               pEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30, 0, false, false, false), this);
            }

            if (!pEntity.hasInfiniteMaterials() && this.isMassive() && this.distanceTo(pEntity) < 3.5) {
               pEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2, false, false, false), this);
               if (pEntity.equals(this.getTarget())) {
                  this.trappingTarget = true;
               }
            }
         }
      }
   }

   public void push(Entity pEntity) {
      super.push(pEntity);
      if (this.isEffectiveAi()) {
         if (!pEntity.getType().is(TensuraEntityTags.SLIMES) && pEntity.getDeltaMovement().y() < 0.0) {
            pEntity.resetFallDistance();
            pEntity.setDeltaMovement(pEntity.getDeltaMovement().multiply(1.0, 0.5, 1.0));
         }

         if (!this.canSlimeMerge(pEntity)) {
            if (!pEntity.getType().is(TensuraEntityTags.SLIMES) || this.getTarget() == pEntity) {
               if (!this.isAlliedTo(pEntity)) {
                  if (!(pEntity instanceof LivingEntity living && this.isOwnedBy(living))) {
                     this.damageCollidedEntity(pEntity);
                     this.supermassiveTrapping(pEntity);
                  }
               }
            }
         }
      }
   }

   protected boolean canSlimeMerge(Entity pEntity) {
      if (pEntity instanceof SlimeEntity slime && this.isSameSizeMergeSlime(slime) && (this.isSameOwner(slime) || !this.isTame() && !slime.isTame())) {
         if (EnergyHelper.getMaxEP(this) >= EnergyHelper.getMaxEP(slime)) {
            this.mergeSlime(this, slime);
         } else {
            this.mergeSlime(slime, this);
         }

         return true;
      } else {
         return false;
      }
   }

   private void mergeSlime(SlimeEntity remain, SlimeEntity remove) {
      remove.setRemoved(RemovalReason.DISCARDED);
      this.triggerAnim("miscController", "land");
      float oldSize = remain.getSize();
      remain.setSize(oldSize + 1.0F, true);
      if (oldSize < 18.0F && remain.getSize() >= 18.0F) {
         remain.setMassive(true);
         remain.applyMassiveAbilities();
      }

      if (remain.getOwner() instanceof Player player) {
         player.displayClientMessage(
            Component.translatable("tensura.message.slime.merge").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withBold(true)), false
         );
         if (remain.isMassive() && oldSize < 18.0F) {
            player.displayClientMessage(
               Component.translatable("tensura.message.slime.massive").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withBold(true)), false
            );
         }
      }
   }

   protected void supermassiveTrapping(Entity entity) {
      if (entity.equals(this.getTarget())) {
         if (!this.trappingTarget) {
            this.trappingTarget = true;
            if (!(this.distanceTo(entity) > 5.0F)) {
               if (this.canTrap) {
                  if (this.trappingCoolDown <= 0) {
                     entity.startRiding(this, true);
                     this.canTrap = false;
                     this.trappingCoolDown = 120;
                  }
               }
            }
         }
      }
   }

   protected void damageCollidedEntity(Entity entity) {
      float damage = entity == this.getTarget() ? (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) : (this.getSize() <= 1.0F ? 0.0F : 0.5F);
      this.dealDamage(entity, damage, entity == this.getTarget());
   }

   protected boolean dealDamage(Entity entity, float damage, boolean aggro) {
      if (damage < 0.0F) {
         return false;
      }

      if (!this.isAlive()) {
         return false;
      }

      DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.CORROSION, aggro ? this : null);
      if (entity.getVehicle() == this) {
         damage *= 2.0F;
      }

      if (this.distanceTo(entity) < this.getBbWidth() / 2.0F + 1.0F && entity.hurt(damageSource, damage)) {
         this.playSound(this.getAttackSound(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         return true;
      } else {
         return false;
      }
   }

   public boolean isDyeable() {
      return true;
   }

   @Override
   public boolean isTamingFood(ItemStack stack) {
      return stack.is(TensuraItemTags.SLIME_TAMING_FOOD);
   }

   @Override
   public boolean isFood(ItemStack stack) {
      return stack.is(TensuraItemTags.SLIME_FOOD);
   }

   public boolean canConsumeCore() {
      return true;
   }

   private float getHealAmount(Item item) {
      float healAmount = 2.0F;
      if (item.equals(TensuraMobDropItems.SLIME_CHUNK.get())) {
         healAmount = 3.0F;
      }

      if (item.equals(TensuraMaterialItems.MAGIC_ORE.get())) {
         healAmount = 20.0F;
      }

      return healAmount;
   }

   @Override
   public void applyFoodHeal(ItemStack stack, Player player, InteractionHand hand) {
      this.heal(this.getHealAmount(stack.getItem()));
      this.ate();
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (this.isTame() && this.isOwnedBy(player)) {
         if (this.getVariant() != SlimeType.SUMMONED) {
            if (this.getSize() <= 4.0F && stack.is(Items.BUCKET)) {
               this.playSound(this.getPickupSound(), 1.0F, 1.0F);
               ItemStack bucket = this.getBucketItemStack();
               this.saveToBucketTag(bucket);
               if (player instanceof ServerPlayer serverPlayer) {
                  CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, bucket);
               }

               ItemStack filledBucket = ItemUtils.createFilledResult(stack, player, bucket, false);
               player.setItemInHand(hand, filledBucket);
               this.discard();
               return InteractionResult.sidedSuccess(this.level().isClientSide());
            }

            if (this.canConsumeCore() && this.getSize() < 30.0F && stack.is((Item)TensuraMobDropItems.SLIME_CORE.get())) {
               this.setSize(this.getSize() + 1.0F, true);
               this.triggerAnim("miscController", "land");
               if (!player.hasInfiniteMaterials()) {
                  stack.shrink(1);
               }

               this.playSound(SoundEvents.PLAYER_BURP, 1.0F, 0.8F);
               if (this.getSize() >= 18.0F) {
                  if (!this.isMassive()) {
                     player.displayClientMessage(
                        Component.translatable("tensura.message.slime.massive").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withBold(true)), false
                     );
                     this.setMassive(true);
                     this.applyMassiveAbilities();
                  }
               } else if (this.getSize() >= 30.0F) {
                  if (this.getSize() == 18.0F) {
                     this.addBaseValue(Attributes.STEP_HEIGHT, 1.0);
                  }

                  player.displayClientMessage(
                     Component.translatable("tensura.message.slime.maxsize").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withBold(true)), false
                  );
               }

               if (player instanceof ServerPlayer serverPlayer) {
                  ((ItemUsedOnEntityTrigger)TensuraCriteriaTriggers.ITEM_USED_ON_ENTITY.get()).trigger(serverPlayer, stack, this);
               }

               return InteractionResult.sidedSuccess(this.level().isClientSide());
            }
         }

         if (this.isDyeable() && stack.getItem() instanceof DyeItem dyeItem && !SlimeColor.DYE_BY_VARIANT.get(this.getColor()).equals(dyeItem.getDyeColor())) {
            SlimeColor.setVariantFromColor(this, dyeItem.getDyeColor());
            this.triggerAnim("miscController", "damage");
            if (!player.hasInfiniteMaterials()) {
               stack.shrink(1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide());
         } else {
            InteractionResult riding = this.getRidingInteraction(player, hand);
            if (riding.consumesAction()) {
               return riding;
            }

            this.cycleCommands(this, player);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "damage");
   }

   @Override
   protected void equipChest(Player player, ItemStack itemStack) {
      super.equipChest(player, itemStack);
      this.triggerAnim("miscController", "land");
   }

   @Override
   public boolean isSaddleable() {
      return super.isSaddleable() && this.isMassive();
   }

   @Override
   public boolean hasArmorSlot() {
      return true;
   }

   @Override
   public boolean isMountArmor(ItemStack stack) {
      return stack.getItem() instanceof ArmorItem armor && armor.getEquipmentSlot().equals(EquipmentSlot.HEAD);
   }

   @Override
   public EquipmentSlot getMountArmorSlot() {
      return EquipmentSlot.HEAD;
   }

   @Override
   public int getChestSlots() {
      return 45;
   }

   @Override
   public void handleStartJump(int pJumpPower) {
      if (this.canExecuteRidersJump()) {
         this.playSound(this.getJumpSound(), 0.4F, 1.0F);
         this.triggerAnim("miscController", "jump");
      } else {
         this.playerJumpPendingScale = 0.0F;
      }
   }

   @Override
   public void handleStopJump() {
      this.playSound(this.getSquishSound(), 0.4F, 1.0F);
   }

   @Override
   public boolean canOpenMountInventory(Player owner) {
      return this.isOwnedBy(owner);
   }

   @NonNull
   public ItemStack getBucketItemStack() {
      return new ItemStack((ItemLike)TensuraMaterialItems.SLIME_IN_A_BUCKET.get());
   }

   public boolean fromBucket() {
      return (Boolean)this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean pBoolean) {
      this.entityData.set(FROM_BUCKET, pBoolean);
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket() || this.isTame();
   }

   public void saveToBucketTag(@NonNull ItemStack bucket) {
      if (bucket == null) {
         throw new NullPointerException("bucket is marked non-null but is null");
      }

      bucket.set(DataComponents.CUSTOM_NAME, this.getCustomName());
      bucket.set(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(this.saveWithoutId(new CompoundTag())));
      CompoundTag compound = new CompoundTag();
      compound.putBoolean("Metal", this.getClass() == MetalSlimeEntity.class);
      compound.putBoolean("Supermassive", this.getClass() == SupermassiveSlimeEntity.class);
      bucket.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
   }

   public void loadFromBucketTag(@NonNull CompoundTag pCompound) {
      if (pCompound == null) {
         throw new NullPointerException("pCompound is marked non-null but is null");
      }

      this.load(pCompound);
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return this.getSize() == 1.0F ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.getSize() == 1.0F ? SoundEvents.SLIME_DEATH_SMALL : SoundEvents.SLIME_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.getSize() == 1.0F ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.SLIME_JUMP;
   }

   protected SoundEvent getAttackSound() {
      return SoundEvents.SLIME_ATTACK;
   }

   @NonNull
   public SoundEvent getPickupSound() {
      return SoundEvents.SLIME_JUMP_SMALL;
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.slime, pLevel, pSpawnReason) && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (!pReason.equals(MobSpawnType.STRUCTURE)) {
         int variant = this.random.nextInt(6);
         this.setColor(SlimeColor.byId(variant));
         if (pLevel.getBiome(this.getOnPos()).is(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
            this.setChilled(Boolean.TRUE);
         }

         if (this.canRandomizeSpawnData(pReason)) {
            if (this.getClass() == SlimeEntity.class) {
               if (this.canSpawnSpecialVariant(pReason)) {
                  if (TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.metalSlimeChance, pLevel.getRandom())) {
                     MetalSlimeEntity slime = new MetalSlimeEntity((EntityType<? extends MetalSlimeEntity>)MonsterEntityTypes.METAL_SLIME.get(), this.level());
                     slime.setPos(this.getX(), this.getY(), this.getZ());
                     slime.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
                     this.level().addFreshEntity(slime);
                     this.setRemoved(RemovalReason.DISCARDED);
                     return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
                  }

                  if (this.level().getBiome(this.blockPosition()).is(TensuraBiomeTags.EntitySpawn.SUPERMASSIVE_SLIME)
                     && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.supermassiveSlimeChance, pLevel.getRandom())) {
                     SupermassiveSlimeEntity slime = new SupermassiveSlimeEntity(
                        (EntityType<? extends SupermassiveSlimeEntity>)MonsterEntityTypes.SUPERMASSIVE_SLIME.get(), this.level()
                     );
                     slime.setPos(this.getX(), this.getY(), this.getZ());
                     slime.setColor(this.getColor());
                     slime.setChilled(this.isChilled());
                     slime.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
                     this.level().addFreshEntity(slime);
                     this.setRemoved(RemovalReason.DISCARDED);
                     return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
                  }

                  int type = this.random.nextInt(2);
                  this.setVariant(SlimeType.byId(type));
               }

               int i = this.random.nextInt(4);
               if (i < 2 && this.random.nextFloat() < 0.5F * pDifficulty.getSpecialMultiplier()) {
                  i++;
               }

               int size;
               for (size = 1 << i; size > 1; size--) {
                  AABB box = this.getBoundingBox().inflate(size * this.getBbWidth(), 0.0, size * this.getBbWidth());
                  if (this.level().noCollision(this, box)) {
                     break;
                  }
               }

               this.setSize(Math.max(size, 1.0F), true);
            } else if (this.getClass() == MetalSlimeEntity.class) {
               this.setSize(4.0F, false, false, false);
               this.setVariant(SlimeType.MERGE);
            }
         }
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   @Override
   public boolean shouldDropCrystal() {
      return !this.isMassive();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<SlimeEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<SlimeEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<SlimeEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, target -> target.getType().equals(EntityType.SLIME)),
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

   public BrainActivityGroup<SlimeEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget(),
            new LeapToTarget(0)
               .minRange((entity, target) -> 0.0F)
               .leapRange((entity, target) -> 32.0F)
               .moveSpeedContribution((slime, entity) -> 0.2F)
               .verticalJumpStrength((entity, target) -> entity.getJumpPower())
               .attackInterval(entity -> 10),
            new AnimatableMeleeAttack(0).attackInterval(entity -> 1).whenStarting(entity -> entity.triggerAnim("miscController", "damage"))
         }
      );
   }

   protected PlayState loopController(AnimationState<SlimeEntity> state) {
      String name;
      if (state.isMoving()) {
         if (this.isInLiquid()) {
            name = "animation.slime.swim";
         } else {
            name = "animation.slime.walk";
         }
      } else {
         name = "animation.slime.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 3, this::loopController),
            new AnimationController(this, "miscController", 3, event -> {
                  this.swinging = false;
                  return PlayState.STOP;
               })
               .triggerableAnim("damage", RawAnimation.begin().then("animation.slime.damage", LoopType.PLAY_ONCE))
               .triggerableAnim("jump", RawAnimation.begin().then("animation.slime.jump", LoopType.PLAY_ONCE))
               .triggerableAnim("land", RawAnimation.begin().then("animation.slime.land", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
