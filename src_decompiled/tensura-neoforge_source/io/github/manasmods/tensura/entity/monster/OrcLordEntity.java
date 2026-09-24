package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.ConditionlessAction;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierPart;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IGiantMob;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.RaceHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.InteractWithDoor;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.schedule.SmartBrainSchedule;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;

public class OrcLordEntity extends OrcEntity implements IGiantMob {
   private boolean dancing;
   @Nullable
   private BlockPos jukebox;
   public int stopMovingTick = 0;
   protected int selfRegen;
   protected boolean shouldSummonOrcs = false;
   protected int laughTicks;
   public static final RawAnimation CRUSH = RawAnimation.begin().then("animation.orc_lord.crush", LoopType.PLAY_ONCE);
   public static final RawAnimation EAT_ITEM = RawAnimation.begin().then("animation.orc_lord.eat_item", LoopType.PLAY_ONCE);
   public static final RawAnimation EAT = RawAnimation.begin().then("animation.orc_lord.eat_mob", LoopType.PLAY_ONCE);

   public OrcLordEntity(EntityType<? extends OrcLordEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.moveControl = new OrcLordEntity.OrcLordMoveControl();
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ATTACK_DAMAGE, 30.0)
         .add(Attributes.MAX_HEALTH, 200.0)
         .add(Attributes.MOVEMENT_SPEED, 0.15F)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 3.0)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 3.0)
         .add(TensuraAttributes.AURA_GAIN, 10.0)
         .add(TensuraAttributes.MAGICULE_GAIN, 10.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 2.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 2.0)
         .add(TensuraAttributes.ABILITY_LEARNING_GAIN, 3.0)
         .add(TensuraAttributes.ABILITY_MASTERY_GAIN, 3.0);
   }

   protected boolean shouldAttack(LivingEntity entity) {
      if (entity == this || RaceUtils.isNonLiving(entity)) {
         return false;
      }

      if (this.is(entity.getVehicle())) {
         return true;
      }

      if (this.isAlliedTo(entity)) {
         return false;
      }

      if (entity.hasInfiniteMaterials() || entity.isSpectator()) {
         return false;
      }

      if (this.getOwner() == null) {
         if (entity instanceof Player) {
            return true;
         } else if (entity instanceof AbstractVillager || entity instanceof AbstractIllager) {
            return true;
         } else {
            return entity instanceof Animal ? !(entity instanceof OrcEntity) : entity instanceof IronGolem;
         }
      } else if (entity.isAlliedTo(this.getOwner())) {
         return false;
      } else {
         return entity instanceof Mob mob
            ? mob.getTarget() == this.getOwner()
            : this.getOwner().getLastHurtMob() == entity || this.getOwner().getLastHurtByMob() == entity;
      }
   }

   public boolean shouldStopTarget(Mob subordinate, LivingEntity target) {
      return subordinate.is(target.getVehicle()) ? false : super.shouldStopTarget(subordinate, target);
   }

   @Override
   public boolean shouldSwim() {
      return false;
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return false;
   }

   @Override
   public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      return null;
   }

   @Override
   protected EntityDimensions getSittingDimension(EntityDimensions original) {
      return original;
   }

   @Override
   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.SWEET_BERRY_BUSH) || source.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(source);
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else {
         return entity instanceof OrcEntity orc ? orc.isTame() == this.isTame() : false;
      }
   }

   public boolean canAttack(LivingEntity pTarget) {
      return this.isAlliedTo(pTarget) ? false : super.canAttack(pTarget);
   }

   @NotNull
   @Override
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @Override
   public List<EquipmentSlot> getAvailableSlots() {
      return List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
   }

   @Override
   public int getChestSlots() {
      return 27;
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      boolean hurt = super.hurt(pSource, pAmount);
      if (hurt && !pSource.isCreativePlayer() && pSource.getEntity() instanceof LivingEntity damageSource) {
         if (!damageSource.isAlive() || this.isAlliedTo(damageSource)) {
            return true;
         }

         List<OrcEntity> list = this.level()
            .getEntitiesOfClass(OrcEntity.class, this.getBoundingBox().inflate(32.0), entity -> !entity.isTame() && !(entity instanceof OrcLordEntity));
         if (!list.isEmpty()) {
            list.forEach(orc -> orc.setTarget(damageSource));
         }
      }

      return hurt;
   }

   @Override
   public boolean doHurtTarget(Entity pEntity) {
      if (super.doHurtTarget(pEntity) && pEntity instanceof LivingEntity living) {
         if (this.getStarved() != null && living.getLastHurtByMobTimestamp() == living.tickCount) {
            MobEffectInstance instance = new MobEffectInstance(
               TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), 200, this.getClass() == OrcLordEntity.class ? 2 : 3
            );
            TensuraMobEffect.addEffect(living, instance, this, (ManasSkill)UniqueSkills.STARVED.get());
         }

         this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   protected ManasSkillInstance getStarved() {
      Optional<ManasSkillInstance> skill = SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)UniqueSkills.STARVED.get());
      if (skill.isEmpty()) {
         return null;
      } else {
         return !skill.get().canInteractSkill(this) ? null : skill.get();
      }
   }

   public void setRecordPlayingNearby(BlockPos pPos, boolean dancing) {
      if (!this.getNavigation().isInProgress()) {
         this.jukebox = pPos;
         this.dancing = dancing;
      }
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      if (!this.isTame()) {
         if (this.isColliding(this, false) || this.tickCount % 20 == 0 && this.getTarget() != null) {
            if (this.tickCount % 20 == 0) {
               List<BarrierPart> list = this.level().getEntitiesOfClass(BarrierPart.class, this.getBoundingBox().inflate(1.0));
               if (!list.isEmpty()) {
                  for (BarrierPart barrier : list) {
                     this.doHurtTarget(barrier);
                  }
               }
            }

            this.breakBlocks(this, 2.0F, false, 1, this.inventory, true);
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.stopMovingTick > 0) {
         this.stopMovingTick--;
         if (this.stopMovingTick == 0) {
            this.ejectPassengers();
         }
      }

      if (--this.selfRegen <= 0 && this.getHealth() < this.getMaxHealth() && this.isAlive()) {
         this.healAndEat();
      }

      if (this.laughTicks-- > 0) {
         if (this.laughTicks == 40) {
            this.laughTicks = 0;
            this.triggerAnim("miscController2", "laugh");
            BrainUtils.clearMemories(this, new MemoryModuleType[]{MemoryModuleType.WALK_TARGET});
            this.playSound((SoundEvent)TensuraSoundEvents.ORC_LAUGH.get(), 2.0F, 1.0F);
         } else if (this.laughTicks < 40) {
            this.playSound((SoundEvent)TensuraSoundEvents.ORC_LAUGH.get(), 2.0F, 1.0F);
            this.getNavigation().stop();
         }
      }
   }

   @Override
   public void aiStep() {
      if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 5.0) || !this.level().getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
         this.dancing = false;
         this.jukebox = null;
      }

      super.aiStep();
   }

   @Override
   protected void evolvingTick() {
      if (this.getEvolving() > 0) {
         this.setEvolving(this.getEvolving() - 1);
         this.playSound((SoundEvent)TensuraSoundEvents.ORC_LAUGH.get(), 1.0F, 1.0F);
         TensuraParticleHelper.addServerAuraParticles(this, TensuraParticleUtils.getChaosEaterAura(1.0F, 8.0F, -0.3F), 10, 0.01);
         if (this.getEvolving() == 0) {
            this.evolveToDisaster();
         }
      }
   }

   protected void healAndEat() {
      this.heal(10.0F);
      this.selfRegen = 20;
      this.eatOrcs();
   }

   protected void eatOrcs() {
      if (!this.level().isClientSide()) {
         if (this.getFirstPassenger() == null) {
            if (this.getHealth() < this.getMaxHealth() / 4.0F && this.random.nextFloat() <= 0.2 || this.getHealth() < this.getMaxHealth() / 8.0F) {
               for (OrcEntity sacrifice : this.level().getEntitiesOfClass(OrcEntity.class, this.getBoundingBox().inflate(3.0))) {
                  if (sacrifice != this) {
                     this.stopMovingTick = 45;
                     this.triggerAnim("miscController2", "eat");
                     sacrifice.startRiding(this, true);
                     BrainUtils.clearMemory(this, MemoryModuleType.ATE_RECENTLY);
                     return;
                  }
               }
            }
         }
      }
   }

   public void areaAttack(float damageMultiplier, float upVector) {
      TensuraParticleHelper.spawnServerGroundSlamParticle(this, 5, 2.5F);
      TensuraParticleHelper.spawnServerParticles(
         this.level(), TensuraParticleUtils.getColorlessWave(0.9F, damageMultiplier * 2.0F), this.getX(), this.getY() + 0.2F, this.getZ()
      );
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
      EffectStorage.setCameraShake(this, 10.0, 0.01F, 10);
      if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         SkillHelper.launchBlock(
            this,
            this.position(),
            (int)(damageMultiplier * 2.0F),
            1,
            0.4F,
            0.3F,
            blockState -> this.getRandom().nextInt(3) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE),
            blockPos -> !blockPos.equals(this.getOnPos().below())
         );
      }

      AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 2.0);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(LivingEntity.class, aabb, entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && entity != this);
      if (!list.isEmpty()) {
         float damage = (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier);

         for (LivingEntity target : list) {
            if (target instanceof OrcEntity orc && !orc.isTame() && this.getTarget() != orc) {
               LivingEntity orcTarget = orc.getTarget();
               if (orcTarget == null || orcTarget != this && !orcTarget.isAlliedTo(this)) {
                  continue;
               }
            }

            target.hurt(this.damageSources().mobAttack(this), damage);
            target.getDeltaMovement().add(0.0, upVector, 0.0);
         }
      }
   }

   @Override
   public void push(Entity pEntity) {
      if (!(pEntity instanceof OrcEntity)) {
         super.push(pEntity);
      }
   }

   protected void summonOrcRandomPos(int amount, int minRadius, int maxRadius) {
      ServerLevel serverLevel = (ServerLevel)this.level();
      int i = Mth.floor(this.getX());
      int j = Mth.floor(this.getY());
      int k = Mth.floor(this.getZ());

      for (int orcs = 0; orcs < amount; orcs++) {
         OrcEntity orc = new OrcEntity((EntityType<? extends OrcEntity>)MonsterEntityTypes.ORC.get(), serverLevel);

         for (int l = 0; l < 50; l++) {
            int i1 = i + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int j1 = j + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int k1 = k + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            orc.setPos(i1, j1, k1);
            if (serverLevel.isUnobstructed(orc) && serverLevel.noCollision(orc)) {
               orc.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(orc.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
               orc.setTarget(this.getTarget());
               this.makeKnight(orc);
               IExistence existence = TensuraStorages.getExistenceFrom(orc);
               existence.setSummoner(this.getUUID());
               existence.markDirty();
               serverLevel.addFreshEntityWithPassengers(orc);
               break;
            }
         }
      }
   }

   protected void makeKnight(OrcEntity orc) {
      ItemStack helmet = new ItemStack(Items.IRON_HELMET);
      ItemStack chestplate = new ItemStack(Items.IRON_CHESTPLATE);
      ItemStack leggings = new ItemStack(Items.IRON_LEGGINGS);
      ItemStack boots = new ItemStack(Items.IRON_BOOTS);
      ItemStack weapon = new ItemStack((ItemLike)(this.random.nextBoolean() ? (ItemLike)TensuraToolItems.IRON_SPEAR.get() : Items.IRON_AXE));
      if (this.random.nextFloat() <= 0.3) {
         if (this.random.nextBoolean()) {
            weapon = new ItemStack((ItemLike)TensuraToolItems.LONG_BOW.get());
         } else {
            weapon = new ItemStack(Items.CROSSBOW);
         }
      }

      if (this.random.nextFloat() <= 0.05) {
         helmet = new ItemStack(Items.NETHERITE_HELMET);
         chestplate = new ItemStack(Items.NETHERITE_CHESTPLATE);
         leggings = new ItemStack(Items.NETHERITE_LEGGINGS);
         boots = new ItemStack(Items.NETHERITE_BOOTS);
         weapon = new ItemStack(Items.NETHERITE_AXE);
      }

      orc.addFakeItem(EquipmentSlot.HEAD, helmet);
      orc.addFakeItem(EquipmentSlot.CHEST, chestplate);
      orc.addFakeItem(EquipmentSlot.LEGS, leggings);
      orc.addFakeItem(EquipmentSlot.FEET, boots);
      orc.addFakeItem(EquipmentSlot.MAINHAND, weapon);
      if (this.random.nextFloat() <= 0.1) {
         orc.addFakeItem(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
      }
   }

   @Override
   protected boolean canEvolveToLord() {
      return false;
   }

   @NotNull
   protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
      Vec3 vec3 = new Vec3(0.0, -this.getBbHeight() / 2.0F, this.getBbWidth() + entity.getBbWidth() / 2.0F);
      return super.getPassengerAttachmentPoint(entity, entityDimensions, f).add(vec3.yRot(-this.getYRot() * 0.0174F));
   }

   @Override
   public InteractionResult handleEating(Player player, InteractionHand hand, ItemStack stack) {
      return this.stopMovingTick > 0 && !this.isAngry() ? InteractionResult.PASS : super.handleEating(player, hand, stack);
   }

   public void ate() {
      super.ate();
      this.triggerAnim("miscController2", "eat_item");
      this.stopMovingTick = 35;
   }

   protected boolean shouldLaugh() {
      return true;
   }

   @Override
   public boolean killedEntity(ServerLevel pLevel, LivingEntity target) {
      boolean wasKilled = super.killedEntity(pLevel, target);
      if (wasKilled && this.isAlive()) {
         if (this.shouldLaugh() && (this.getTarget() == null || !this.getTarget().isAlive())) {
            if (target.getLastHurtByMob() == this) {
               this.laughTicks = 60;
               this.stopMovingTick = 60;
            } else {
               this.laughTicks = 40;
               this.stopMovingTick = 40;
               this.getNavigation().stop();
               this.triggerAnim("miscController2", "laugh");
            }
         }

         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.COMPOSTER);
         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.COMPOSTER, 1.0);
         float heal = 5.0F;
         AttributeInstance HP = this.getAttribute(Attributes.MAX_HEALTH);
         if (HP != null) {
            HP.setBaseValue(HP.getBaseValue() + heal);
         }

         this.heal(30.0F);
         this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0, true, true, true));
         List<OrcEntity> list = this.level().getEntitiesOfClass(OrcEntity.class, this.getBoundingBox().inflate(16.0));
         if (!list.isEmpty()) {
            for (OrcEntity sub : list) {
               if (sub != this && !sub.isTame()) {
                  sub.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN), 400, 0, false, false));
                  sub.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0, false, false));
               }
            }
         }

         if (this.getClass() == OrcLordEntity.class && EnergyHelper.getMaxEP(this) >= 200000.0) {
            this.getNavigation().stop();
            this.setInvulnerable(true);
            this.setEvolving(40);
         }
      }

      return wasKilled;
   }

   protected void evolveToDisaster() {
      Level level = this.level();
      CompoundTag tag = this.saveWithoutId(new CompoundTag());
      this.discard();
      OrcDisasterEntity orc = new OrcDisasterEntity((EntityType<? extends OrcDisasterEntity>)MonsterEntityTypes.ORC_DISASTER.get(), level);
      orc.load(tag);
      if (level instanceof ServerLevel serverLevel) {
         orc.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(orc.blockPosition()), MobSpawnType.CONVERSION, null);
      }

      RaceHelper.applyBaseAttribute(OrcDisasterEntity.setAttributes().build(), orc);
      orc.setHealth(orc.getMaxHealth());
      orc.setEvolving(0);
      orc.setInvulnerable(false);
      level.addFreshEntity(orc);
      orc.refreshDimensions();
      level.playSound(null, orc.blockPosition(), (SoundEvent)TensuraSoundEvents.ORC_TRANSFORM.get(), TensuraSkill.ABILITY_SOUND, 10.0F, 1.0F);
      level.addFreshEntity(orc);
      TensuraParticleHelper.addServerAuraParticles(this, TensuraParticleUtils.getChaosEaterAura(1.0F, 8.0F, -0.3F), 10, 0.01);
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   @Override
   protected boolean canRandomizeSpawnData(MobSpawnType pSpawnType) {
      return pSpawnType == MobSpawnType.CONVERSION ? false : super.canRandomizeSpawnData(pSpawnType);
   }

   @NotNull
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         this.populateDefaultEquipmentSlots(this.random, pDifficulty);
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      ItemStack stack = new ItemStack((ItemLike)TensuraToolItems.MEAT_CRUSHER.get());
      if (pRandom.nextFloat() < 0.05) {
         stack = new ItemStack((ItemLike)TensuraToolItems.BLADE_TIGER_SCYTHE.get());
      }

      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
      this.updateContainerEquipment();
   }

   @Override
   public List<ExtendedSensor<OrcEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   @Override
   public BrainActivityGroup<OrcEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new LookAtTarget(),
            new MoveToWalkTarget()
               .cooldownFor(entity -> 0)
               .startCondition(entity -> !entity.isOrderedToSit() && !entity.isSleeping() && entity.stopMovingTick <= 0)
               .stopIf(entity -> entity.isOrderedToSit() || entity.isSleeping() || entity.stopMovingTick > 0),
            new FloatToSurfaceOfFluid()
         }
      );
   }

   @Override
   public BrainActivityGroup<OrcEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     TensuraBehaviourHelper.getPreyTargeting(this, this::shouldAttack),
                     TensuraBehaviourHelper.getMoveToWanderPos(),
                     new SubordinateFollowOwner(),
                     new SetPlayerLookTarget(),
                     new SetRandomLookTarget()
                  }
               )
               .startCondition(entity -> entity.stopMovingTick <= 0),
            new InteractWithDoor(),
            new HumanoidConsumeItem().startCondition(entity -> entity.shouldHeal() && entity.isTame()).stopIf(entity -> !entity.shouldHeal()),
            new ConditionlessAction(25).actionInterval(entity -> 100).attack(entity -> {
               if (entity.getFirstPassenger() instanceof LivingEntity living) {
                  living.stopRiding();
                  float heal = entity.getHealth();
                  entity.doHurtTarget(living, 2.0F);
                  if (living.isAlive()) {
                     entity.heal(50.0F);
                  } else {
                     entity.heal(heal);
                  }

                  entity.level().playSound(null, entity, (SoundEvent)TensuraSoundEvents.EATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               } else {
                  entity.heal(20.0F);
                  entity.level().playSound(null, entity, (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0));
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.END_ROD, 2.0);
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.COMPOSTER, 2.0);
               }
            }).whenStarting(entity -> {
               entity.stopMovingTick = 25;
               if (entity.isVehicle()) {
                  entity.triggerAnim("miscController", "eat");
               } else {
                  entity.playSound((SoundEvent)TensuraSoundEvents.ORC_LAUGH.get(), 1.0F, 1.0F);
                  entity.triggerAnim("miscController2", "recover");
               }
            }).startCondition(entity -> entity.isVehicle() || entity.stopMovingTick <= 0 && entity.shouldHeal()),
            new OneRandomBehaviour(
                  new ExtendedBehaviour[]{
                     new SetRandomWalkTarget().startCondition(entity -> entity.stopMovingTick <= 0),
                     new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
                  }
               )
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   @Override
   public BrainActivityGroup<OrcEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F).startCondition(entity -> entity.stopMovingTick <= 0),
            new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     new ConditionlessAction(0).actionInterval(entity -> 0).attack(Entity::ejectPassengers).startCondition(Entity::isVehicle),
                     new CustomRangeAttack(20)
                        .maxAttackRadius(40.0F)
                        .attackInterval(entity -> 0)
                        .performAttack((entity, target) -> entity.summonOrcRandomPos(4, 0, 7))
                        .whenStopping(entity -> entity.shouldSummonOrcs = false)
                        .whenStarting(entity -> {
                           entity.stopMovingTick = 40;
                           entity.playSound((SoundEvent)TensuraSoundEvents.ORC_LAUGH.get(), 2.0F, 1.0F);
                           entity.triggerAnim("miscController", "yell");
                        })
                        .startCondition(entity -> entity.shouldSummonOrcs),
                     new CustomHeldAttack()
                        .maxAttackRadius(entity -> entity.getFirstPassenger() != null ? 64.0F : 4.0F)
                        .minAttackRadius(0.0F)
                        .attackInterval(entity -> 40)
                        .onTick((entity, target, tick) -> {
                           if (tick >= 10 && tick < 15 && entity.getFirstPassenger() == null) {
                              target.startRiding(entity, true);
                           } else if (tick == 20) {
                              for (Entity passenger : entity.getPassengers()) {
                                 entity.doHurtTarget(passenger, 2.0F);
                              }

                              entity.level().playSound(null, entity, (SoundEvent)TensuraSoundEvents.EATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                           } else if (tick == 30) {
                              for (Entity passenger : entity.getPassengers()) {
                                 entity.doHurtTarget(passenger, 2.5F);
                              }

                              entity.level().playSound(null, entity, (SoundEvent)TensuraSoundEvents.EATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                           } else if (tick == 40) {
                              float heal = entity.getHealth();

                              for (Entity passenger : entity.getPassengers()) {
                                 entity.doHurtTarget(passenger, 3.0F);
                                 entity.stopRiding();
                              }

                              if (target.isAlive()) {
                                 entity.heal(50.0F);
                              } else {
                                 entity.heal(heal * 2.0F);
                              }

                              entity.level().playSound(null, entity, (SoundEvent)TensuraSoundEvents.EATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                           }

                           return tick < 45;
                        })
                        .whenStarting(entity -> {
                           entity.triggerAnim("miscController2", "eat");
                           entity.stopMovingTick = 45;
                        })
                        .startCondition(
                           entity -> entity.getRandom().nextFloat() < 0.2 && entity.getHealth() < entity.getMaxHealth() / 2.0F && !entity.isPassenger()
                        ),
                     new CustomHeldAttack().minAttackRadius(0.0F).maxAttackRadius(5.0F).attackInterval(entity -> 40).onTick((entity, target, tick) -> {
                        if (tick >= 5 && tick < 10 && entity.getFirstPassenger() == null) {
                           target.startRiding(entity, true);
                        } else if (tick >= 22) {
                           for (Entity passenger : entity.getPassengers()) {
                              entity.doHurtTarget(passenger, 2.0F);
                              entity.stopRiding();
                           }

                           entity.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 1.0F, 1.0F);
                        }

                        return tick < 25;
                     }).whenStarting(entity -> {
                        entity.triggerAnim("miscController2", "crush");
                        entity.stopMovingTick = 25;
                     }).startCondition(entity -> entity.getRandom().nextFloat() < 0.2 && !entity.isPassenger()),
                     new CustomRangeAttack(10)
                        .maxAttackRadius(6.0F)
                        .attackInterval(entity -> 40)
                        .performAttack((entity, target) -> entity.areaAttack(2.0F, 0.75F))
                        .whenStarting(entity -> {
                           entity.triggerAnim("miscController", "slam");
                           entity.stopMovingTick = 25;
                        })
                        .startCondition(entity -> entity.getRandom().nextFloat() < 0.5 && !entity.getMainHandItem().isEmpty()),
                     new CustomRangeAttack(1)
                        .maxAttackRadius(40.0F)
                        .minAttackRadius(5.0F)
                        .performAttack(
                           (entity, target) -> {
                              int i = entity.level()
                                 .getNearbyEntities(
                                    OrcEntity.class,
                                    TargetingConditions.forNonCombat().range(32.0).ignoreLineOfSight().ignoreInvisibilityTesting(),
                                    entity,
                                    entity.getBoundingBox().inflate(32.0)
                                 )
                                 .size();
                              entity.shouldSummonOrcs = entity.random.nextInt(15) + 1 > i;
                           }
                        )
                        .startCondition(entity -> !entity.isTame() && entity.getRandom().nextFloat() <= 0.2),
                     new AnimatableMeleeAttack(8).attackInterval(entity -> 0).whenStarting(entity -> {
                        entity.triggerAnim("miscController", entity.getMainHandItem().isEmpty() ? "punch" : "swing");
                        entity.stopMovingTick = 15;
                     })
                  }
               )
               .startCondition(entity -> entity.stopMovingTick <= 0)
         }
      );
   }

   @Override
   public Map<Activity, BrainActivityGroup<? extends OrcEntity>> getAdditionalTasks() {
      return new Object2ObjectOpenHashMap(0);
   }

   @Override
   public SmartBrainSchedule getSchedule() {
      return null;
   }

   @Override
   protected PlayState loopController(AnimationState<OrcEntity> state) {
      String name;
      if (this.isDancing()) {
         name = "animation.orc_lord.skibidi_dop";
      } else if (state.isMoving()) {
         if (!this.isAngry() && !this.isSprinting()) {
            name = "animation.orc_lord.walk";
         } else {
            name = "animation.orc_lord.run";
         }
      } else {
         name = "animation.orc_lord.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   @Override
   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("punch", RawAnimation.begin().then("animation.orc_lord.punch", LoopType.PLAY_ONCE))
               .triggerableAnim("swing", RawAnimation.begin().then("animation.orc_lord.cleaver_swing", LoopType.PLAY_ONCE))
               .triggerableAnim("slam", RawAnimation.begin().then("animation.orc_lord.cleaver_slam", LoopType.PLAY_ONCE))
               .triggerableAnim("yell", RawAnimation.begin().then("animation.orc_lord.yell", LoopType.PLAY_ONCE)),
            new AnimationController(this, "miscController2", 3, event -> PlayState.STOP)
               .triggerableAnim("crush", RawAnimation.begin().then("animation.orc_lord.crush", LoopType.PLAY_ONCE))
               .triggerableAnim("eat_item", RawAnimation.begin().then("animation.orc_lord.eat_item", LoopType.PLAY_ONCE))
               .triggerableAnim("eat", RawAnimation.begin().then("animation.orc_lord.eat_mob", LoopType.PLAY_ONCE))
               .triggerableAnim("recover", RawAnimation.begin().then("animation.orc_lord.recover", LoopType.PLAY_ONCE))
               .triggerableAnim("laugh", RawAnimation.begin().then("animation.orc_lord.laugh", LoopType.PLAY_ONCE))
         }
      );
   }

   @Generated
   public boolean isDancing() {
      return this.dancing;
   }

   public class OrcLordMoveControl extends MoveControl {
      public OrcLordMoveControl() {
         super(OrcLordEntity.this);
      }

      public void tick() {
         if (OrcLordEntity.this.laughTicks <= 0) {
            super.tick();
         }
      }
   }
}
