package io.github.manasmods.tensura.entity.human;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.unique.CookSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.TeleportToEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.magic.misc.HazyBlossomEntity;
import io.github.manasmods.tensura.entity.magic.spike.DripstoneSpikeEntity;
import io.github.manasmods.tensura.entity.magic.spike.MagicSpikeEntity;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IArenaBoss;
import io.github.manasmods.tensura.entity.template.subclass.ITeleportation;
import io.github.manasmods.tensura.item.misc.BattlewillManualItem;
import io.github.manasmods.tensura.item.weapon.TensuraSwordItem;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.particle.option.SimpleAuraParticleOptions;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.battlewill.MeleeArts;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.boss.template.BossFightInstance;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ItemHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatType;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
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

public class GazelDwargoEntity extends PlayerLikeEntity implements SmartBrainOwner<GazelDwargoEntity>, GeoEntity, ITeleportation, IArenaBoss {
   private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(GazelDwargoEntity.class, EntityDataSerializers.INT);
   public int eightPetalsCooldown = 0;
   public int stopMovingTick = 0;
   private String bossFightId = null;
   private boolean negative = false;
   private final ServerBossEvent bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossBarColor.WHITE, BossBarOverlay.NOTCHED_20)
      .setPlayBossMusic(true);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public GazelDwargoEntity(EntityType<? extends GazelDwargoEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 60.0)
         .add(Attributes.ATTACK_DAMAGE, 80.0)
         .add(Attributes.MAX_HEALTH, 3000.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.FLYING_SPEED, 0.7F)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.5)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(TensuraAttributes.DODGE_NEGATE_CHANCE, 100.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 10.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 20.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 5.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 5.0);
   }

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
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Phase", this.getPhase());
      if (this.getBossFightId() != null) {
         compound.putString("BossFightId", this.getBossFightId());
      }

      compound.putBoolean("NegativeReputation", this.negative);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (this.hasCustomName()) {
         this.bossEvent.setName(this.getDisplayName());
      }

      this.setPhase(compound.getInt("Phase"));
      if (compound.contains("BossFightId")) {
         this.setBossFightId(compound.getString("BossFightId"));
      }

      this.negative = compound.getBoolean("NegativeReputation");
   }

   public int getPhase() {
      return (Integer)this.entityData.get(PHASE);
   }

   public void setPhase(int phase) {
      this.entityData.set(PHASE, phase);
   }

   @Override
   public boolean wantsToPickUp(ItemStack pStack) {
      return false;
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
      return false;
   }

   public boolean canBeAffected(MobEffectInstance instance) {
      return instance.getEffect().equals(TensuraMobEffects.getReference(TensuraMobEffects.INFECTION)) ? this.getPhase() >= 2 : super.canBeAffected(instance);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (this.getPhase() != 0) {
         return this.shouldDodge(pSource) ? false : super.hurt(pSource, pAmount);
      }

      if (pSource.getEntity() instanceof LivingEntity attacker && !this.level().isClientSide()) {
         if (attacker instanceof Player player) {
            MutableComponent name = Component.literal("<").append(this.getName()).append(">").withStyle(ChatFormatting.GOLD);
            double reputation = TensuraStorages.getPlayerDataFrom(player).getReputation((EntityType<?>)HumanEntityTypes.DWARF.get());
            if (reputation >= 0.0) {
               player.sendSystemMessage(
                  name.append(" ").append(Component.translatable("tensura.message.dwarf.king.greet.positive_start").withStyle(ChatFormatting.GREEN))
               );
            } else {
               player.sendSystemMessage(
                  name.append(" ").append(Component.translatable("tensura.message.dwarf.king.greet.negative_start").withStyle(ChatFormatting.RED))
               );
            }
         }

         this.setPhase(1);
         this.setTarget(attacker);
         this.stopMovingTick = 60;
         this.invulnerableTime = 80;
         AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 6.0);

         for (LivingEntity target : this.level()
            .getEntitiesOfClass(LivingEntity.class, aabb, entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && entity != this)) {
            target.setDeltaMovement(0.0, 0.2, 0.0);
            Vec3 vec3 = target.position().subtract(target.getViewVector(1.0F)).subtract(new Vec3(this.getX(), target.getY(), this.getZ()));
            SkillHelper.knockBack(target, vec3.normalize(), 3.0, 1.0, 0.2F);
            target.hurtMarked = true;
         }
      }

      return false;
   }

   protected void actuallyHurt(DamageSource source, float damage) {
      float multiplier = 1.0F;
      if (source.tensura$getMagicType() == Magic.MagicType.ASPECTUAL || source.tensura$getMagicType() == Magic.MagicType.SPIRITUAL) {
         multiplier *= 0.5F;
      }

      if (this.getPhase() == 2) {
         multiplier *= RaceUtils.getPhysicalAttackInputMultiplier(source);
      }

      super.actuallyHurt(source, damage * multiplier);
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

         if (entity.getRandom().nextFloat() >= 0.15 * this.getPhase()) {
            return false;
         }

         if (this.getTarget() != null) {
            this.teleportTowards(this, this.getTarget(), 5.0);
         }

         this.invulnerableTime = 60;
         return true;
      } else {
         return false;
      }
   }

   public boolean isPushedByFluid() {
      return false;
   }

   @Override
   public List<EquipmentSlot> getAvailableSlots() {
      return List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
   }

   @Override
   public int getChestSlots() {
      return 27;
   }

   @Override
   public boolean shouldBroadcastTeleport() {
      return false;
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

      if (this.getPhase() == 2) {
         this.bossEvent.setColor(BossBarColor.RED);
         this.bossEvent.setOverlay(BossBarOverlay.NOTCHED_12);
      }
   }

   private boolean shouldStopMoving() {
      return this.stopMovingTick > 0 ? true : this.getPhase() == 0;
   }

   protected boolean canRide(Entity entity) {
      return false;
   }

   @Override
   public DamageSource getBaseDamageSource() {
      DamageSource source = super.getBaseDamageSource().tensura$setResistanceBypassLevel(1.0F);
      if (this.getPhase() == 2) {
         source = source.tensura$setBarrierBypassLevel(1.0F);
      }

      return source;
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         this.phaseHandler();
         if (this.eightPetalsCooldown > 0) {
            this.eightPetalsCooldown--;
         }

         if (this.stopMovingTick > 0) {
            this.stopMovingTick--;
         }

         if (this.tickCount % 20 == 0) {
            if (!this.isAlive()) {
               return;
            }

            if (this.getY() < this.level().getMinBuildHeight() - 100) {
               this.discard();
               return;
            }

            if (this.tickCount % 200 == 0) {
               this.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.HAKI_COAT), 240, 1, false, false, false));
            }

            IExistence existence = TensuraStorages.getExistenceFrom(this);
            if (this.getHealth() >= this.getMaxHealth() && existence.getSpiritualHealth() >= this.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH)) {
               return;
            }

            if (this.tickCount % 100 == 0) {
               this.heal(100.0F);
               existence.setSpiritualHealth(existence.getSpiritualHealth() + 200.0);
               existence.markDirty();
               TensuraStorages.resetEffect(this);
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
            } else if (this.getPhase() != 2) {
               this.heal(10.0F);
               existence.setSpiritualHealth(existence.getSpiritualHealth() + 50.0);
            } else {
               this.heal(30.0F);
               existence.setSpiritualHealth(existence.getSpiritualHealth() + 100.0);
            }
         }
      }
   }

   protected void phaseHandler() {
      switch (this.getPhase()) {
         case 0:
            if (this.negative && this.tickCount % 60 == 0) {
               double range = this.getAttributeValue(Attributes.FOLLOW_RANGE);
               double rangeSqr = range * range;

               for (Player player : this.level().players()) {
                  if (!player.isSpectator() && !player.isCreative() && !(player.distanceToSqr(this) > rangeSqr)) {
                     MutableComponent name = Component.literal("<").append(this.getName()).append(">").withStyle(ChatFormatting.GOLD);
                     double reputation = TensuraStorages.getPlayerDataFrom(player).getReputation((EntityType<?>)HumanEntityTypes.DWARF.get());
                     if (reputation <= DwarfEntity.REPUTATION_CONFIG.minReputation) {
                        player.sendSystemMessage(
                           name.append(" ").append(Component.translatable("tensura.message.dwarf.king.greet.negative_start").withStyle(ChatFormatting.RED))
                        );
                        player.playNotifySound(SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.HOSTILE, 1.0F, 1.0F);
                        if (this.getPhase() == 0) {
                           this.setPhase(1);
                           this.setTarget(player);
                           this.stopMovingTick = 60;
                           this.invulnerableTime = 80;
                        }
                     }
                  }
               }
            }
            break;
         case 1:
            if (!this.isAlive()) {
               return;
            }

            if (this.getHealth() > this.getMaxHealth() * 0.5 || this.getHealth() <= 500.0F) {
               return;
            }

            this.enterLastPhase();
            break;
         case 2:
            if (this.tickCount % 10 != 0) {
               return;
            }

            TensuraParticleHelper.addServerParticlesAroundSelf(this, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD_BRICKS.defaultBlockState()));
            TensuraParticleHelper.addServerParticlesAroundSelf(this, TensuraParticleUtils.getBogEffect());
            double size = this.getAttributeValue(Attributes.SCALE) * 4.0;
            TensuraParticleHelper.addServerAuraParticles(this, new SimpleAuraParticleOptions(0.66F, 0.47F, 0.32F, 1.0F, (float)size, -0.3F), 5, 0.01);
      }
   }

   private void enterLastPhase() {
      this.invulnerableTime = 100;
      CookSkill.removeCookedHP(this);
      AttributeInstance shpRegen = this.getAttribute(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION);
      if (shpRegen != null) {
         shpRegen.setBaseValue(shpRegen.getBaseValue() + 30.0);
      }

      AttributeInstance HP = this.getAttribute(Attributes.MAX_HEALTH);
      if (HP != null) {
         HP.setBaseValue(HP.getBaseValue() + 1897.0);
         this.setHealth((float)HP.getValue());
      }

      EnergyHelper.gainAura(this, EnergyHelper.getMaxAura(this), EnergyHelper.GainType.MAX);
      EnergyHelper.gainMagicule(this, EnergyHelper.getMaxMagicule(this), EnergyHelper.GainType.MAX);
      IExistence existence = TensuraStorages.getExistenceFrom(this);
      existence.setAura(EnergyHelper.getMaxAura(this));
      existence.setMagicule(EnergyHelper.getMaxMagicule(this));
      existence.setSpiritualHealth(this.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH));
      existence.markDirty();
      AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
      if (armor != null) {
         armor.setBaseValue(armor.getBaseValue() + 20.0);
      }

      TensuraStorages.resetEffect(this);
      this.dead = false;
      this.deathTime = 0;
      this.unsetRemoved();
      this.eightPetalsCooldown = 0;
      this.setPhase(2);
      AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 6.0);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(LivingEntity.class, aabb, entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && entity != this);
      DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.EARTH_ELEMENTAL, this)
         .tensura$setBarrierBypassLevel(1.0F)
         .tensura$setResistanceBypassLevel(1.0F);

      for (LivingEntity target : list) {
         target.hurt(source, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
         Vec3 vec3 = target.position().subtract(target.getViewVector(1.0F)).subtract(new Vec3(this.getX(), target.getY(), this.getZ()));
         SkillHelper.knockBack(target, vec3.normalize(), 3.0, 1.0, 0.2F);
         target.hurtMarked = true;
      }

      TensuraParticleHelper.addServerParticlesAroundSelf(this, TensuraParticleUtils.getMudEffect(), 1.0);
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

   public void slashCombo() {
      Level level = this.level();
      level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
      this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 5.0F, 0.95F + this.random.nextFloat() * 0.1F);
      int radius = 4;
      BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, this, Fluid.NONE, radius + 2);
      BlockPos ahead = result.getBlockPos();
      Vec3 aheadVec = new Vec3(ahead.getX(), ahead.getY(), ahead.getZ());
      TensuraParticleHelper.addServerParticlesAroundPos(this.random, level, aheadVec, ParticleTypes.SWEEP_ATTACK, 4.0);
      TensuraParticleHelper.addServerParticlesAroundPos(this.random, level, aheadVec, ParticleTypes.SWEEP_ATTACK, 5.0);
      AABB aabb = new AABB(ahead).inflate(radius);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(
            LivingEntity.class, aabb.minmax(aabb), living -> !living.is(this) && living.isAlive() && !living.isAlliedTo(this) && !living.hasInfiniteMaterials()
         );
      if (!list.isEmpty()) {
         float damage = (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5);
         DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), DamageTypes.MOB_ATTACK, this).tensura$setResistanceBypassLevel(1.0F);
         if (this.getPhase() == 2) {
            source = source.tensura$setBarrierBypassLevel(1.0F);
         }

         for (LivingEntity target : list) {
            target.hurt(source, damage);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SWEEP_ATTACK, 2.0);
         }
      }
   }

   public void areaAttack(float damageMultiplier, double range, float upVector, ResourceKey<DamageType> type) {
      float radius = damageMultiplier * 2.0F;
      TensuraParticleHelper.spawnServerGroundSlamParticle(this, 5, radius / 2.0F);
      TensuraParticleHelper.spawnServerParticles(
         this.level(), TensuraParticleUtils.getColorlessWave(0.9F, radius), this.getX(), this.getY() + 0.2F, this.getZ()
      );
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
      EffectStorage.setCameraShake(this, 10.0, 0.01F, 10);
      if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         SkillHelper.launchBlock(
            this,
            this.position(),
            (int)radius,
            1,
            0.4F,
            0.3F,
            blockState -> this.getRandom().nextInt(3) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE),
            blockPos -> !blockPos.equals(this.getOnPos().below())
         );
      }

      AABB aabb = this.getBoundingBox().inflate(range);
      List<LivingEntity> list = this.level()
         .getEntitiesOfClass(LivingEntity.class, aabb, entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && entity != this);
      if (!list.isEmpty()) {
         float damage = (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier);
         DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), type, this).tensura$setResistanceBypassLevel(1.0F);
         if (this.getPhase() == 2) {
            source = source.tensura$setBarrierBypassLevel(1.0F);
         }

         for (LivingEntity target : list) {
            target.hurt(source, damage);
            target.getDeltaMovement().add(0.0, upVector, 0.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SWEEP_ATTACK, 2.0);
         }
      }
   }

   public void spawnEarthSpikes(float damageMultiplier, double range, int number) {
      float damage = (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier);
      ManasSkillInstance instance = SkillUtils.getSkillOrNull(this, (ManasSkill)SpiritualMagics.EARTH_SPIKES.get());

      for (int i = 0; i < number; i++) {
         float angle = (float) (Math.PI / 180.0) * this.yBodyRot + i;
         double extraX = range * Mth.sin((float)(Math.PI + angle));
         double extraZ = range * Mth.cos(angle);
         Vec3 groundPos = new Vec3(Mth.floor(this.getX() + extraX), this.getY(), Mth.floor(this.getZ() + extraZ));
         MagicSpikeEntity spike = new DripstoneSpikeEntity(this.level(), this);
         spike.setPos(groundPos);
         spike.setDamage(damage);
         spike.setLife(60);
         spike.setYaw(-30.0F);
         spike.setPitch(ObjectSelectionHelper.getYRotFromVector(spike.position().subtract(this.position()).normalize()));
         spike.setHeight(3.0F);
         spike.setSkill(this, instance, (TensuraSkill)SpiritualMagics.EARTH_SPIKES.get(), 0);
         this.level().addFreshEntity(spike);
         spike.triggerAnim("controller", "start");
         EffectStorage.setCameraShake(spike, 3.0, 0.005F, 5);
         this.level()
            .playSound(null, spike.getX(), spike.getY(), spike.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   protected void blockUsingShield(LivingEntity target) {
      if (this.stopMovingTick == 0) {
         this.triggerAnim("miscController", "parry");
      }

      this.level()
         .playSound(
            null, this.getX(), this.getY(), this.getZ(), SoundEvents.ANVIL_HIT, this.getSoundSource(), 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F
         );
   }

   @Override
   public boolean isDamageSourceBlocked(DamageSource source) {
      if (!source.is(DamageTypeTags.BYPASSES_SHIELD) && (this.stopMovingTick >= 10 || this.stopMovingTick <= 0)) {
         if (this.getRandom().nextFloat() >= 0.25 * this.getPhase()) {
            return false;
         } else if (source.is(DamageTypeTags.IS_PROJECTILE) && source.getDirectEntity() instanceof Projectile) {
            this.triggerAnim("miscController", "parry");
            this.playSound(SoundEvents.ANVIL_HIT, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
            return true;
         } else {
            return TensuraDamageHelper.isPhysicalAttack(source);
         }
      } else {
         return false;
      }
   }

   @Override
   protected void hurtCurrentlyUsedShield(float f) {
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (!this.isTame() && !this.isOwnedBy(player)) {
         if (!player.level().isClientSide() && this.getPhase() == 0) {
            this.setPhase(1);
            this.setTarget(player);
            this.stopMovingTick = 60;
            this.invulnerableTime = 80;
            if (TensuraStorages.getPlayerDataFrom(player).getReputation((EntityType<?>)HumanEntityTypes.DWARF.get())
               >= DwarfEntity.REPUTATION_CONFIG.maxReputation) {
               MutableComponent name = Component.literal("<").append(this.getName()).append(">").withStyle(ChatFormatting.GOLD);
               player.sendSystemMessage(
                  name.append(" ").append(Component.translatable("tensura.message.dwarf.king.greet.positive_start").withStyle(ChatFormatting.GREEN))
               );
               player.playNotifySound(SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }

            AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 6.0);

            for (LivingEntity target : this.level()
               .getEntitiesOfClass(LivingEntity.class, aabb, entity -> !entity.isAlliedTo(this) && entity != this.getOwner() && entity != this)) {
               Vec3 vec3 = target.position().subtract(target.getViewVector(1.0F)).subtract(new Vec3(this.getX(), target.getY(), this.getZ()));
               SkillHelper.knockBack(target, vec3.normalize(), 5.0, 1.0, 0.5);
               target.hurtMarked = true;
            }
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return super.handleCommanding(player, hand, stack);
      }
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   @Override
   public void onSpawned(ServerLevel level, String id, BossFightInstance instance) {
      this.setBossFightId(id);

      for (UUID uuid : instance.getJoinedPlayers()) {
         ServerPlayer player = (ServerPlayer)level.getPlayerByUUID(uuid);
         if (player != null) {
            if (player.getStats().getValue(((StatType)TensuraStats.BOSS_KILLED.get()).get((EntityType)HumanEntityTypes.GAZEL_DWARGO.get())) > 0) {
               IExistence existence = TensuraStorages.getExistenceFrom(this);
               existence.setSpawnType(MobSpawnType.MOB_SUMMONED);
               existence.markDirty();
            }

            MutableComponent name = Component.literal("<").append(this.getName()).append(">").withStyle(ChatFormatting.GOLD);
            double reputation = TensuraStorages.getPlayerDataFrom(player).getReputation((EntityType<?>)HumanEntityTypes.DWARF.get());
            if (reputation <= DwarfEntity.REPUTATION_CONFIG.minReputation) {
               player.sendSystemMessage(
                  name.append(" ").append(Component.translatable("tensura.message.dwarf.king.greet.negative").withStyle(ChatFormatting.RED))
               );
               player.playNotifySound(SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.HOSTILE, 1.0F, 1.0F);
               this.negative = true;
            } else if (reputation >= DwarfEntity.REPUTATION_CONFIG.maxReputation) {
               player.sendSystemMessage(
                  name.append(" ")
                     .append(
                        Component.translatable("tensura.message.dwarf.king.greet.positive", new Object[]{player.getName()}).withStyle(ChatFormatting.GREEN)
                     )
               );
               player.playNotifySound(SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
         }
      }
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
      ItemStack stack = new ItemStack((ItemLike)TensuraToolItems.RUHK.get());
      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
      this.updateContainerEquipment();
   }

   protected void tickDeath() {
      if (++this.deathTime >= 40) {
         this.remove(RemovalReason.KILLED);
         this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 1.0F);
      }
   }

   public void die(DamageSource source) {
      if (!source.is(DamageTypes.FELL_OUT_OF_WORLD) && !source.is(DamageTypes.GENERIC_KILL) && this.getPhase() < 2) {
         this.enterLastPhase();
      } else {
         boolean gazelKilled = false;
         if (source.getEntity() instanceof ServerPlayer player) {
            gazelKilled = player.getStats().getValue(((StatType)TensuraStats.BOSS_KILLED.get()).get((EntityType)HumanEntityTypes.GAZEL_DWARGO.get())) > 0;
         }

         super.die(source);
         if (!this.isAlive()) {
            if (this.getPose() == Pose.DYING) {
               this.triggerAnim("dashController", "defeat");
            }

            if (!this.level().isClientSide()) {
               if (source.getEntity() instanceof ServerPlayer player) {
                  if (!gazelKilled) {
                     ItemHelper.dropItem(
                        this, this.getRandom(), BattlewillManualItem.createForBattlewill((ManasSkill)MeleeArts.FIVE_PETALS_THRUST.get()), 0, 0.1F
                     );
                     ItemHelper.dropItem(this, this.getRandom(), ((TensuraSwordItem)TensuraToolItems.RUHK.get()).getDefaultInstance(), 0, 0.1F);
                  }

                  double reputation = TensuraStorages.getPlayerDataFrom(player).getReputation((EntityType<?>)HumanEntityTypes.DWARF.get());
                  MutableComponent name = Component.literal("<").append(this.getName()).append(">").withStyle(ChatFormatting.GOLD);
                  if (reputation <= DwarfEntity.REPUTATION_CONFIG.minReputation) {
                     player.sendSystemMessage(
                        name.append(" ").append(Component.translatable("tensura.message.dwarf.king.defeat.negative").withStyle(ChatFormatting.RED))
                     );
                  } else if (reputation >= DwarfEntity.REPUTATION_CONFIG.maxReputation) {
                     if (gazelKilled) {
                        player.sendSystemMessage(
                           name.append(" ")
                              .append(
                                 Component.translatable("tensura.message.dwarf.king.defeat.positive.retry", new Object[]{player.getName()})
                                    .withStyle(ChatFormatting.GREEN)
                              )
                        );
                     } else {
                        player.sendSystemMessage(
                           name.append(" ")
                              .append(
                                 Component.translatable("tensura.message.dwarf.king.defeat.positive", new Object[]{player.getName()})
                                    .withStyle(ChatFormatting.GREEN)
                              )
                        );
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   protected float getEquipmentDropChance(EquipmentSlot pSlot) {
      if (this.isTame()) {
         return 0.0F;
      } else {
         return pSlot.equals(EquipmentSlot.MAINHAND) ? 0.0F : super.getEquipmentDropChance(pSlot);
      }
   }

   @Nullable
   @Override
   public SoundEvent getTeleportSound() {
      return (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   public List<ExtendedSensor<GazelDwargoEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<GazelDwargoEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new LookAtTarget(),
            new MoveToWalkTarget()
               .cooldownFor(entity -> 0)
               .startCondition(entity -> !entity.isOrderedToSit() && !entity.isSleeping() && !entity.shouldStopMoving())
               .stopIf(entity -> entity.isOrderedToSit() || entity.isSleeping() || entity.shouldStopMoving())
         }
      );
   }

   public BrainActivityGroup<GazelDwargoEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     TensuraBehaviourHelper.getPreyTargeting(this, this::shouldTarget),
                     new SubordinateFollowOwner(),
                     TensuraBehaviourHelper.getMoveToWanderPos(),
                     new SetPlayerLookTarget(),
                     new SetRandomLookTarget()
                  }
               )
               .startCondition(entity -> !entity.shouldStopMoving()),
            new InteractWithDoor(),
            new HumanoidConsumeItem().startCondition(PlayerLikeEntity::shouldHeal).stopIf(entity -> !entity.shouldHeal()),
            new OneRandomBehaviour(
               new ExtendedBehaviour[]{
                  new FirstApplicableBehaviour(
                     new ExtendedBehaviour[]{
                        new SetRandomWalkTarget().startCondition(entity -> !entity.shouldStopMoving()),
                        new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
                     }
                  ),
                  new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
               }
            )
         }
      );
   }

   public BrainActivityGroup<GazelDwargoEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new TeleportToEntity()
               .following(Mob::getTarget)
               .teleportRadius((entity, target) -> 5)
               .teleportToTargetAfter((entity, target) -> entity.tickCount % 200 == 0 ? 20.0 : 30.0)
               .onSuccessTeleport((entity, target) -> TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SWEEP_ATTACK, 1.0))
               .startCondition(entity -> !entity.shouldStopMoving()),
            new StrafeTarget()
               .stopStrafingWhen(entity -> !entity.usingRangedWeapon())
               .startCondition(entity -> entity.usingRangedWeapon() && !entity.shouldStopMoving()),
            new SetWalkTargetToAttackTarget()
               .speedMod((owner, target) -> 2.0F)
               .startCondition(entity -> !entity.usingRangedWeapon() && !entity.shouldStopMoving()),
            new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     new CustomHeldAttack()
                        .minAttackRadius(0.0F)
                        .maxAttackRadius(30.0F)
                        .attackInterval(entity -> 40)
                        .onTick(
                           (entity, target, tick) -> {
                              if (tick >= 15 && tick < 100) {
                                 if (tick == 35) {
                                    ManasSkillInstance instance = SkillUtils.getSkillOrNull(this, (ManasSkill)MeleeArts.EIGHT_PETALS_SLASH.get());
                                    HazyBlossomEntity blossom = new HazyBlossomEntity(entity.level(), entity);
                                    blossom.setVisualSize(entity.getBbHeight() / 1.8F);
                                    blossom.setSkill(entity, instance, (TensuraSkill)MeleeArts.EIGHT_PETALS_SLASH.get(), 0);
                                    blossom.setDamage(500.0F);
                                    blossom.setPetals(8);
                                    blossom.setInvisible(true);
                                    blossom.setPos(entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0));
                                    blossom.setLife(1200);
                                    entity.level().addFreshEntity(blossom);
                                    blossom.triggerAnim("loopController", "start");
                                    blossom.setYRot(entity.getYRot() % 360.0F);
                                    blossom.setXRot(entity.getXRot() % 360.0F);
                                    entity.level()
                                       .playSound(
                                          null,
                                          entity.getX(),
                                          entity.getY(),
                                          entity.getZ(),
                                          (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                                          TensuraSkill.ABILITY_SOUND,
                                          1.0F,
                                          1.0F
                                       );
                                 }

                                 double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
                                 TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getPinkAura(0.75F, (float)size, -0.3F), 3, 0.01);
                                 if (tick % 2 == 0) {
                                    entity.level()
                                       .playSound(
                                          null,
                                          entity.getX(),
                                          entity.getY(),
                                          entity.getZ(),
                                          (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                                          TensuraSkill.ABILITY_SOUND,
                                          0.25F,
                                          2.0F
                                       );
                                 }
                              } else if (tick == 105) {
                                 entity.teleportTowards(entity, target, 2.0);
                              } else if (tick == 107) {
                                 if (entity.distanceTo(target) < 10.0F) {
                                    target.invulnerableTime = 0;
                                    entity.doHurtTarget(target, 4.0F);
                                 }

                                 entity.eightPetalsCooldown = 400;
                                 entity.level()
                                    .playSound(
                                       null,
                                       entity.getX(),
                                       entity.getY(),
                                       entity.getZ(),
                                       SoundEvents.PLAYER_ATTACK_SWEEP,
                                       TensuraSkill.ABILITY_SOUND,
                                       5.0F,
                                       1.0F
                                    );
                              }

                              return tick < 125;
                           }
                        )
                        .whenStarting(entity -> {
                           entity.stopMovingTick = 120;
                           entity.triggerAnim("dashController", "dash_charge");
                        })
                        .startCondition(entity -> entity.getPhase() >= 2 && entity.getRandom().nextFloat() <= 0.1),
                     new CustomHeldAttack()
                        .minAttackRadius(0.0F)
                        .maxAttackRadius(30.0F)
                        .attackInterval(entity -> 40)
                        .onTick(
                           (entity, target, tick) -> {
                              if (tick == 25) {
                                 entity.teleportTowards(entity, target, 2.0);
                              } else if (tick == 26) {
                                 if (entity.distanceTo(target) < 10.0F) {
                                    entity.doHurtTarget(target, 2.0F);
                                    target.invulnerableTime = 0;
                                 }

                                 entity.level()
                                    .playSound(
                                       null,
                                       entity.getX(),
                                       entity.getY(),
                                       entity.getZ(),
                                       SoundEvents.PLAYER_ATTACK_SWEEP,
                                       TensuraSkill.ABILITY_SOUND,
                                       5.0F,
                                       1.0F
                                    );
                              } else if (tick == 40) {
                                 if (entity.distanceTo(target) < 10.0F) {
                                    entity.doHurtTarget(target, 4.0F);
                                 }

                                 entity.level()
                                    .playSound(
                                       null,
                                       entity.getX(),
                                       entity.getY(),
                                       entity.getZ(),
                                       TensuraSoundEvents.EARTHSHATTER_KICK,
                                       TensuraSkill.ABILITY_SOUND,
                                       5.0F,
                                       1.0F
                                    );
                              }

                              return tick < 55;
                           }
                        )
                        .whenStarting(entity -> {
                           entity.stopMovingTick = 50;
                           entity.triggerAnim("dashController", "slash_heaven");
                        })
                        .startCondition(entity -> entity.getRandom().nextFloat() <= 0.1),
                     new CustomHeldAttack()
                        .minAttackRadius(0.0F)
                        .maxAttackRadius(15.0F)
                        .attackInterval(entity -> 40)
                        .onTick(
                           (entity, target, tick) -> {
                              if (tick == 30) {
                                 entity.teleportTowards(entity, target, 2.0);
                              } else if (tick == 33) {
                                 if (entity.distanceTo(target) < 10.0F) {
                                    entity.doHurtTarget(target, 2.0F);
                                 }

                                 entity.level()
                                    .playSound(
                                       null,
                                       entity.getX(),
                                       entity.getY(),
                                       entity.getZ(),
                                       SoundEvents.PLAYER_ATTACK_SWEEP,
                                       TensuraSkill.ABILITY_SOUND,
                                       5.0F,
                                       1.0F
                                    );
                              }

                              return tick < 50 && entity.stopMovingTick != 0;
                           }
                        )
                        .whenStarting(entity -> {
                           entity.stopMovingTick = 45;
                           entity.triggerAnim("dashController", "dash");
                        })
                        .startCondition(entity -> entity.getRandom().nextFloat() <= 0.1),
                     new CustomHeldAttack().minAttackRadius(0.0F).maxAttackRadius(14.0F).attackInterval(entity -> 40).onTick((entity, target, tick) -> {
                        if (tick >= 10 && tick <= 25) {
                           if (tick == 10) {
                              entity.areaAttack(1.5F, 10.0, 1.0F, TensuraDamageTypes.EARTH_ELEMENTAL);
                              target.invulnerableTime = 0;
                              entity.spawnEarthSpikes(2.0F, 3.0, 10);
                           } else if (tick == 15) {
                              entity.spawnEarthSpikes(2.0F, 5.0, 15);
                           } else if (tick == 20) {
                              entity.spawnEarthSpikes(2.0F, 7.5, 20);
                           } else if (tick == 25) {
                              entity.spawnEarthSpikes(2.0F, 10.0, 25);
                           }
                        }

                        return tick < 40;
                     }).whenStarting(entity -> {
                        entity.stopMovingTick = 35;
                        entity.triggerAnim("miscController", "stomp");
                     }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.1),
                     new CustomRangeAttack(10).maxAttackRadius(12.0F).attackInterval(entity -> 40).performAttack((entity, target) -> {
                        entity.areaAttack(1.5F, 12.0, 2.0F, TensuraDamageTypes.EARTH_ELEMENTAL);
                        entity.spawnEarthSpikes(2.0F, 5.0, 15);
                     }).whenStarting(entity -> {
                        entity.stopMovingTick = 35;
                        entity.triggerAnim("miscController", "struck_sword");
                     }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.2),
                     new CustomRangeAttack(15)
                        .maxAttackRadius(6.0F)
                        .attackInterval(entity -> 20)
                        .performAttack(
                           (entity, target) -> entity.areaAttack(
                              2.0F, entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE), 1.25F, DamageTypes.MOB_ATTACK
                           )
                        )
                        .whenStarting(entity -> {
                           entity.stopMovingTick = 25;
                           entity.triggerAnim("miscController", "slash_jump");
                        })
                        .startCondition(entity -> entity.getRandom().nextFloat() <= 0.2),
                     new CustomHeldAttack().minAttackRadius(0.0F).maxAttackRadius(10.0F).attackInterval(entity -> 60).onTick((entity, target, tick) -> {
                        if (tick >= 45 && tick <= 55) {
                           entity.slashCombo();
                        }

                        return tick < 80;
                     }).whenStarting(entity -> {
                        entity.stopMovingTick = 45;
                        entity.triggerAnim("miscController", "slash_combo");
                     }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.1),
                     new AnimatableMeleeAttack(10).attackInterval(entity -> 10).whenStarting(entity -> {
                        entity.stopMovingTick = 15;
                        entity.triggerAnim("miscController", entity.getRandom().nextBoolean() ? "slash" : "swing");
                     })
                  }
               )
               .startCondition(entity -> !entity.shouldStopMoving())
         }
      );
   }

   protected PlayState loopController(AnimationState<GazelDwargoEntity> state) {
      String name;
      if (!this.isAlive()) {
         name = "animation.gazel.idle";
      } else if (this.getPhase() == 0) {
         name = "animation.gazel.sit";
      } else if (this.isInSittingPose()) {
         name = "animation.gazel.stand_aura_2";
      } else if (state.isMoving()) {
         if (this.isAngry()) {
            if (this.isSprinting()) {
               if (this.getPhase() == 1) {
                  name = "animation.gazel.run_ready";
               } else {
                  name = "animation.gazel.run_ready_3";
               }
            } else {
               name = "animation.gazel.run_ready_2";
            }
         } else if (this.isSprinting()) {
            name = "animation.gazel.run";
         } else {
            name = "animation.gazel.walk";
         }
      } else if (this.isAngry()) {
         if (this.getPhase() == 1) {
            name = "animation.gazel.idle_ready";
         } else {
            name = "animation.gazel.idle_ready_2";
         }
      } else {
         name = "animation.gazel.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("slash", RawAnimation.begin().then("animation.gazel.slash", LoopType.PLAY_ONCE))
               .triggerableAnim("swing", RawAnimation.begin().then("animation.gazel.slash_2", LoopType.PLAY_ONCE))
               .triggerableAnim("slash_combo", RawAnimation.begin().then("animation.gazel.slash_combo", LoopType.PLAY_ONCE))
               .triggerableAnim("slash_jump", RawAnimation.begin().then("animation.gazel.slash_run", LoopType.PLAY_ONCE))
               .triggerableAnim("struck_sword", RawAnimation.begin().then("animation.gazel.struck_sword", LoopType.PLAY_ONCE))
               .triggerableAnim("parry", RawAnimation.begin().then("animation.gazel.parry", LoopType.PLAY_ONCE))
               .triggerableAnim("stomp", RawAnimation.begin().then("animation.gazel.stomp", LoopType.PLAY_ONCE)),
            new AnimationController(this, "dashController", 3, event -> PlayState.STOP)
               .triggerableAnim("dash", RawAnimation.begin().then("animation.gazel.dash", LoopType.PLAY_ONCE))
               .triggerableAnim("dash_charge", RawAnimation.begin().then("animation.gazel.dash_charge", LoopType.PLAY_ONCE))
               .triggerableAnim("slash_heaven", RawAnimation.begin().then("animation.gazel.slash_heaven", LoopType.PLAY_ONCE))
               .triggerableAnim("defeat", RawAnimation.begin().then("animation.gazel.defeat", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   @Override
   public String getBossFightId() {
      return this.bossFightId;
   }

   @Generated
   @Override
   public void setBossFightId(String bossFightId) {
      this.bossFightId = bossFightId;
   }
}
