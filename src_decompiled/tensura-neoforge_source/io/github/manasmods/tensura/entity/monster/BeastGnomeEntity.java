package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.TensuraRideableEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IElementalSpirit;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class BeastGnomeEntity extends TensuraRideableEntity implements GeoEntity, SmartBrainOwner<BeastGnomeEntity>, IElementalSpirit, ITensuraMount {
   private static final EntityDataAccessor<Boolean> SHRUNK = SynchedEntityData.defineId(BeastGnomeEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final ResourceLocation SHRINK = ResourceLocation.fromNamespaceAndPath("tensura", "shrunk_gnome");
   private final List<Holder<Attribute>> attributeList = List.of(
      Attributes.SCALE,
      Attributes.MAX_HEALTH,
      Attributes.JUMP_STRENGTH,
      Attributes.ATTACK_DAMAGE,
      Attributes.KNOCKBACK_RESISTANCE,
      Attributes.ARMOR,
      Attributes.ARMOR_TOUGHNESS
   );
   private int mountAbilityTick = 0;
   public static final RawAnimation EAT = RawAnimation.begin().then("animation.beast_gnome.eat", LoopType.PLAY_ONCE);
   public static final RawAnimation SLAM = RawAnimation.begin().then("animation.beast_gnome.slam", LoopType.PLAY_ONCE);
   public static final RawAnimation YELL = RawAnimation.begin().then("animation.beast_gnome.yell_slam", LoopType.PLAY_ONCE);

   public BeastGnomeEntity(EntityType<? extends BeastGnomeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 28.0)
         .add(Attributes.MAX_HEALTH, 80.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.JUMP_STRENGTH, 2.0)
         .add(Attributes.STEP_HEIGHT, 1.5)
         .add(Attributes.SAFE_FALL_DISTANCE, 16.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 10.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 2.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHRUNK, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("Shrunk", this.isShrunk());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(SHRUNK, compound.getBoolean("Shrunk"));
   }

   public boolean isShrunk() {
      return (Boolean)this.entityData.get(SHRUNK);
   }

   public void setShrunk(boolean shrunk, boolean pResetHealth) {
      this.entityData.set(SHRUNK, shrunk);
      if (shrunk) {
         AttributeModifier modifier = new AttributeModifier(SHRINK, -0.9, Operation.ADD_MULTIPLIED_TOTAL);

         for (Holder<Attribute> attribute : this.attributeList) {
            AttributeInstance attributeInstance = this.getAttribute(attribute);
            if (attributeInstance != null && !attributeInstance.hasModifier(SHRINK)) {
               attributeInstance.addOrReplacePermanentModifier(modifier);
            }
         }

         if (pResetHealth) {
            this.setHealth(this.getHealth() * 0.1F);
         }
      } else {
         for (Holder<Attribute> attribute : this.attributeList) {
            AttributeInstance attributeInstance = this.getAttribute(attribute);
            if (attributeInstance != null) {
               attributeInstance.removeModifier(SHRINK);
            }
         }

         if (pResetHealth) {
            this.setHealth(Math.min(this.getHealth() * 10.0F, this.getMaxHealth()));
         }
      }
   }

   @Override
   public boolean canSleep() {
      return !this.isNoAi();
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return false;
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.CACTUS) || source.is(DamageTypes.SWEET_BERRY_BUSH) || source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof BeastGnomeEntity gnome) {
         return gnome.isTame() == this.isTame();
      } else {
         return entity instanceof WarGnomeEntity gnome ? gnome.isTame() == this.isTame() : false;
      }
   }

   public boolean canAttack(LivingEntity pTarget) {
      return this.isAlliedTo(pTarget) ? false : super.canAttack(pTarget);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (!this.isTame()) {
         if (pSource.getEntity() instanceof BeastGnomeEntity gnome && !gnome.isTame()) {
            return false;
         }

         if (pSource.getEntity() instanceof WarGnomeEntity gnome && !gnome.isTame()) {
            return false;
         }
      }

      return super.hurt(pSource, pAmount);
   }

   protected void actuallyHurt(DamageSource source, float damage) {
      damage *= this.getPhysicalAttackInput(source);
      super.actuallyHurt(source, damage);
   }

   @Override
   public boolean canBeNamed(Player player) {
      return !this.isTamedByNonPlayer();
   }

   @Override
   public void tick() {
      super.tick();
      boolean ownerSneak = this.getVehicle() instanceof Player player && player.isSecondaryUseActive();
      if (this.isPassenger()
         && this.getVehicle() instanceof LivingEntity living
         && (living.isInWaterOrBubble() || living.isInLava() || living.isFallFlying() || ownerSneak)) {
         this.stopRiding();
         this.setPos(Vec3.atCenterOf(living.blockPosition()));
      }

      if (this.mountAbilityTick > 0) {
         this.getNavigation().stop();
         if (this.mountAbilityTick-- == 25) {
            this.gravityAttack();
         }
      }
   }

   @Override
   public void mountAbility(Player rider) {
      if (this.mountAbilityTick <= 0) {
         this.triggerAnim("slamController", "yell");
         this.mountAbilityTick = 60;
      }
   }

   public void areaAttack() {
      TensuraParticleHelper.spawnGroundSlamParticle(this, 5, 2.5F);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getColorlessWave(0.9F, 3.0F), this.getX(), this.getY() + 0.2F, this.getZ());
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
      EffectStorage.setCameraShake(this, 7.0, 0.02F, 15);
      if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         SkillHelper.launchBlock(
            this,
            this.position(),
            3,
            1,
            0.4F,
            0.3F,
            blockState -> this.getRandom().nextInt(3) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_MANIPULATING),
            blockPos -> !blockPos.equals(this.getOnPos().below())
         );
      }

      AABB aabb = this.getBoundingBox().inflate(4.0);
      List<LivingEntity> livingEntityList = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            aabb,
            entity -> !entity.isAlliedTo(this)
               && entity != this.getOwner()
               && !entity.equals(this)
               && (!(entity instanceof BeastGnomeEntity) || entity == this.getTarget())
         );
      if (!livingEntityList.isEmpty()) {
         double damageMultiplier = this.hasEarthManipulation() ? 1.5 : 0.75;
         DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.EARTH_ELEMENTAL, this)
            .tensura$setAbilityInstance(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.EARTH_MANIPULATION.get()))
            .tensura$setMagiculeCost(20.0)
            .tensura$setElement(Element.EARTH)
            .tensura$setMagicType(Magic.MagicType.SPIRITUAL);

         for (LivingEntity target : livingEntityList) {
            target.hurt(damageSource, (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier));
            target.getDeltaMovement().add(0.0, 0.5 * damageMultiplier, 0.0);
         }
      }
   }

   public void gravityAttack() {
      TensuraParticleHelper.spawnGroundSlamParticle(this, 10, 6.0F);
      TensuraParticleHelper.spawnServerParticles(this.level(), TensuraParticleUtils.getColorlessWave(0.9F, 5.0F), this.getX(), this.getY() + 0.2F, this.getZ());
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
      EffectStorage.setCameraShake(this, 10.0, 0.1F, 15);
      if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         SkillHelper.launchBlock(
            this,
            this.position(),
            8,
            1,
            0.5F,
            0.4F,
            blockState -> this.getRandom().nextInt(3) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE),
            blockPos -> !blockPos.equals(this.getOnPos().below())
         );
      }

      AABB aabb = this.getBoundingBox().inflate(10.0);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            aabb,
            entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && !entity.equals(this) && !(entity instanceof BeastGnomeEntity)
         );
      if (!list.isEmpty()) {
         double damageMultiplier = this.hasEarthManipulation() ? 0.5 : 0.25;
         DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.GRAVITY_ELEMENTAL, this)
            .tensura$setAbilityInstance(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.EARTH_MANIPULATION.get()))
            .tensura$setMagiculeCost(100.0)
            .tensura$setElement(Element.EARTH)
            .tensura$setMagicType(Magic.MagicType.SPIRITUAL);

         for (LivingEntity target : list) {
            if (target.hurt(damageSource, (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier)) && target.onGround()) {
               TensuraParticleHelper.addParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.DARK_PURPLE_LIGHTNING_SPARK.get());
               if (target.getRandom().nextBoolean() && this.hasEarthManipulation()) {
                  target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200));
               }

               SkillHelper.knockBack(this, target, 2.0F);
            }
         }
      }
   }

   public boolean hasEarthManipulation() {
      return SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)ExtraSkills.EARTH_MANIPULATION.get()).isPresent();
   }

   @Override
   public boolean isRideable(Player rider) {
      return !rider.isSecondaryUseActive() && !this.isShrunk() ? this.canAddPassenger(rider) : false;
   }

   @Override
   public boolean isSaddleRequired() {
      return false;
   }

   @Override
   public Element getElemental() {
      return Element.EARTH;
   }

   @Override
   public SpiritualMagic.SpiritLevel getSpiritLevel() {
      return SpiritualMagic.SpiritLevel.MEDIUM;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(TensuraItemTags.SPIRIT_FOOD);
   }

   @Override
   public boolean isTamingFood(ItemStack pStack) {
      return pStack.is((Item)TensuraMaterialItems.EARTH_ELEMENTAL_SHARD.get());
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (!this.isTame() || !this.isOwnedBy(player)) {
         return InteractionResult.PASS;
      }

      if (this.isShrunk() && player.getFirstPassenger() == null && !player.isSecondaryUseActive() && stack.isEmpty()) {
         this.startRiding(player, true);
         this.getNavigation().stop();
         this.setTarget(null);
         this.refreshDimensions();
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      }

      if (this.convertElementalCore(this, player, hand, (Item)TensuraMaterialItems.ELEMENT_CORE_EARTH.get())) {
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      }

      InteractionResult golemInteraction = this.getGolemInteraction(player, hand, this);
      if (golemInteraction.consumesAction()) {
         return golemInteraction;
      }

      if (!stack.isEmpty() && !player.isSecondaryUseActive()) {
         this.setShrunk(!this.isShrunk(), true);
         if (this.isShrunk()) {
            this.getNavigation().stop();
            this.setTarget(null);
         }

         TensuraParticleHelper.addParticlesAroundSelf(this, ParticleTypes.SQUID_INK);
         TensuraParticleHelper.addParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.DARK_PURPLE_LIGHTNING_SPARK.get());
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         InteractionResult riding = this.getRidingInteraction(player, hand);
         if (riding != InteractionResult.PASS) {
            return riding;
         }

         this.cycleCommands(this, player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      }
   }

   @Override
   public void applyFoodHeal(ItemStack stack, Player player, InteractionHand hand) {
      this.heal(this.isShrunk() ? 0.5F : 5.0F);
      this.ate();
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController", "eat");
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.beastGnome, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canSpawnSpecialVariant(pReason)
         && pLevel.getBiome(this.blockPosition()).is(TensuraBiomeTags.ANCIENT_FOREST)
         && TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.greaterSpiritChance, pLevel.getRandom())) {
         WarGnomeEntity spirit = new WarGnomeEntity((EntityType<? extends WarGnomeEntity>)MonsterEntityTypes.WAR_GNOME.get(), this.level());
         spirit.setPos(this.getX(), this.getY(), this.getZ());
         spirit.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
         this.level().addFreshEntity(spirit);
         this.setRemoved(RemovalReason.DISCARDED);
         return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
      } else {
         return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
      }
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.GNOME_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.GNOME_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.GNOME_DEATH.get();
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

   public List<ExtendedSensor<BeastGnomeEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<BeastGnomeEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new FloatToSurfaceOfFluid(), new LookAtTarget(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<BeastGnomeEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false).startCondition(entity -> !entity.isShrunk()),
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

   public BrainActivityGroup<BeastGnomeEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F).startCondition(entity -> !entity.isShrunk()),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(32)
                     .maxAttackRadius(5.0F)
                     .attackInterval(entity -> 100)
                     .performAttack((entity, target) -> entity.gravityAttack())
                     .whenStarting(entity -> entity.triggerAnim("slamController", "yell"))
                     .startCondition(gnome -> !gnome.isShrunk() && gnome.getRandom().nextInt(10) == 1),
                  new CustomRangeAttack(20)
                     .maxAttackRadius(5.0F)
                     .attackInterval(entity -> 60)
                     .performAttack((entity, target) -> entity.areaAttack())
                     .whenStarting(entity -> entity.triggerAnim("slamController", "slam"))
                     .startCondition(gnome -> !gnome.isShrunk() && gnome.getRandom().nextInt(10) == 1),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(5.0F)
                     .attackInterval(entity -> 30)
                     .performAttack((entity, target) -> {
                        entity.doHurtTarget(target);
                        SkillHelper.knockBack(entity, target, 3.0F);
                     })
                     .whenStarting(entity -> entity.triggerAnim("miscController", "slap"))
                     .startCondition(gnome -> !gnome.isShrunk() && gnome.getRandom().nextInt(7) == 1),
                  new AnimatableMeleeAttack(10).attackInterval(entity -> 5).whenStarting(entity -> {
                     LivingEntity target = entity.getTarget();
                     if (target != null && target.getEyeHeight() <= entity.getEyeHeight() * 0.25F) {
                        entity.triggerAnim("miscController", "eat");
                     } else {
                        entity.triggerAnim("miscController", "bite");
                     }
                  }).startCondition(entity -> !entity.isShrunk())
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<BeastGnomeEntity> state) {
      String name;
      if (!this.isAlive()) {
         name = "animation.beast_gnome.death";
      } else if (state.isCurrentAnimation(SLAM) || state.isCurrentAnimation(YELL)) {
         name = "animation.beast_gnome.idle";
      } else if (this.isSleeping()) {
         name = "animation.beast_gnome.sleep";
      } else if (this.isInSittingPose()) {
         if (this.getHealth() < this.getMaxHealth() / 4.0F) {
            name = "animation.beast_gnome.sit_hurt";
         } else {
            name = "animation.beast_gnome.sit";
         }
      } else if (state.isMoving()) {
         if (this.isInLiquid() || !this.isAngry() && !this.isSprinting()) {
            name = "animation.beast_gnome.walk";
         } else {
            name = "animation.beast_gnome.run";
         }
      } else {
         name = "animation.beast_gnome.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("eat", EAT)
               .triggerableAnim("slap", RawAnimation.begin().then("animation.beast_gnome.slap", LoopType.PLAY_ONCE))
               .triggerableAnim("bite", RawAnimation.begin().then("animation.beast_gnome.bite", LoopType.PLAY_ONCE)),
            new AnimationController(this, "slamController", 0, event -> PlayState.STOP).triggerableAnim("slam", SLAM).triggerableAnim("yell", YELL)
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
