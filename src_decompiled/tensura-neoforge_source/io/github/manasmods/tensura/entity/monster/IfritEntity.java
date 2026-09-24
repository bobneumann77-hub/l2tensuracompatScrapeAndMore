package io.github.manasmods.tensura.entity.monster;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.OrbitAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.FlareCircleEntity;
import io.github.manasmods.tensura.entity.magic.field.Hellfire;
import io.github.manasmods.tensura.entity.projectile.magic.FireBoltProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FlameSphereProjectile;
import io.github.manasmods.tensura.entity.template.GreaterSpiritEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleType;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class IfritEntity extends GreaterSpiritEntity implements GeoEntity, SmartBrainOwner<IfritEntity> {
   protected static final EntityDataAccessor<BlockPos> FLARE_POS = SynchedEntityData.defineId(IfritEntity.class, EntityDataSerializers.BLOCK_POS);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean shouldSummonClones = false;
   private boolean shouldSummonSalamanders = false;
   private boolean training = false;

   public IfritEntity(EntityType<? extends IfritEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossBarColor.RED, BossBarOverlay.NOTCHED_20).setPlayBossMusic(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.ATTACK_DAMAGE, 40.0)
         .add(Attributes.MAX_HEALTH, 400.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.FLYING_SPEED, 0.7F)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.STEP_HEIGHT, 2.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.5)
         .add(TensuraAttributes.PRESENCE_SENSE, 3.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 20.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 3.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FLARE_POS, BlockPos.ZERO);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("FlareX", this.getFlarePos().getX());
      compound.putInt("FlareY", this.getFlarePos().getY());
      compound.putInt("FlareZ", this.getFlarePos().getZ());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setFlarePos(compound.getInt("FlareX"), compound.getInt("FlareY"), compound.getInt("FlareZ"));
   }

   public BlockPos getFlarePos() {
      return (BlockPos)this.entityData.get(FLARE_POS);
   }

   public void setFlarePos(int x, int y, int z) {
      this.entityData.set(FLARE_POS, new BlockPos(x, y, z));
   }

   public boolean isOnFire() {
      return TensuraStorages.getEffectFrom(this).isOnBlackFlame();
   }

   public boolean shouldEvaporateProjectile(@Nullable Entity entity) {
      return entity == null ? false : entity.getType().is(TensuraEntityTags.CAN_EVAPORATE);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || super.isInvulnerableTo(source);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (pSource.getEntity() instanceof SalamanderEntity || pSource.getEntity() instanceof IfritEntity) {
         return false;
      }

      if (this.shouldEvaporateProjectile(pSource.getDirectEntity())) {
         this.playSound(SoundEvents.LAVA_EXTINGUISH, 10.0F, 0.8F);
         pSource.getDirectEntity().remove(RemovalReason.KILLED);
         return false;
      }

      boolean hurt = super.hurt(pSource, pAmount);
      if (hurt && pSource.getEntity() instanceof LivingEntity damageSource && !pSource.isCreativePlayer()) {
         if (!damageSource.isAlive() || this.isAlliedTo(damageSource)) {
            return true;
         }

         List<SalamanderEntity> list = this.level().getEntitiesOfClass(SalamanderEntity.class, this.getBoundingBox().inflate(32.0), entity -> !entity.isTame());
         if (!list.isEmpty()) {
            list.forEach(salamander -> salamander.setTarget(damageSource));
         }
      }

      return hurt;
   }

   @Override
   protected float getDamageReductionMultiplier(DamageSource source) {
      return TensuraDamageHelper.isHeat(source) ? 0.05F : super.getDamageReductionMultiplier(source);
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof SalamanderEntity salamander) {
         return salamander.isTame() == this.isTame();
      } else {
         return entity instanceof IfritEntity ifrit ? ifrit.isTame() == this.isTame() : false;
      }
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
      if (this.isInWaterOrBubble() && this.tickCount % 10 == 0 && this.removeFluid(this, FluidTags.WATER, 3.0F, true, 0)) {
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 10.0F, 1.0F);
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide() && this.isAlive()) {
         if (this.tickCount % 10 == 0) {
            TensuraParticleHelper.addParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), 2.0);
         }

         if (this.tickCount % 200 == 0 && this.onGround() && !this.isSleeping()) {
            this.training = !this.training && this.getRandom().nextFloat() <= 0.25F;
         }

         Vec3 vec3 = this.getViewVector(1.0F).normalize();
         float radius = -0.15F;
         double yPos = this.getEyeY() + 0.2F;
         float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
         double extraX = Mth.sin((float)(Math.PI + angle));
         double extraZ = Mth.cos(angle);
         if (this.isSleeping()) {
            yPos -= 0.2F;
            radius = -0.5F;
         } else if (this.training) {
            yPos -= 1.6F;
            radius = 0.6F;
         } else if (this.getDeltaMovement().lengthSqr() > 0.03) {
            if (!this.onGround()) {
               yPos -= 0.2F;
               radius = 0.3F;
            } else if (this.isAngry()) {
               radius = 0.0F;
            }
         }

         for (int i = 0; i < 15; i++) {
            double ox = Math.random() * 0.3 - 0.15;
            double oy = Math.random() * 0.3 - 0.15;
            double oz = Math.random() * 0.3 - 0.15;
            Vec3 randomVec = new Vec3(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).normalize();
            Vec3 result = vec3.scale(-1.0).add(randomVec).normalize().scale(0.1);
            this.level()
               .addParticle(
                  (ParticleOptions)TensuraParticleTypes.HEAT_EFFECT.get(),
                  this.getX() - vec3.x * 0.1 + ox + extraX * radius,
                  yPos + oy,
                  this.getZ() - vec3.z * 0.1 + oz + extraZ * radius,
                  result.x,
                  result.y,
                  result.z
               );
            this.level()
               .addParticle(
                  ParticleTypes.FLAME,
                  this.getX() - vec3.x * 0.1 + ox + extraX * radius,
                  yPos + oy,
                  this.getZ() - vec3.z * 0.1 + oz + extraZ * radius,
                  result.x,
                  result.y,
                  result.z
               );
         }
      }
   }

   public void shootFireBolt(LivingEntity target) {
      FireBoltProjectile bolt = new FireBoltProjectile(this.level(), this);
      bolt.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)SpiritualMagics.FIRE_BOLT.get()));
      float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
      double xOffset = Mth.sin((float)(Math.PI + angle));
      double zOffset = Mth.cos(angle);
      bolt.moveTo(this.getX() + xOffset, this.getEyeY() - 0.2, this.getZ() + zOffset, this.getYRot(), this.getXRot());
      bolt.setSize(1.5F);
      bolt.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
      bolt.setExplosionRadius(1.0F);
      bolt.setBurnTicks(100);
      bolt.setSpeed(1.5F);
      bolt.setNoGravity(true);
      bolt.shootToward(target, 1.0F, 0.0F);
      this.level().addFreshEntity(bolt);
      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 0.95F + this.getRandom().nextFloat() * 0.1F);
      this.setMagicID(0);
   }

   protected void flameOrb() {
      int orbID = this.getMagicID();
      if (orbID == 0) {
         FlameSphereProjectile orb = new FlameSphereProjectile(this.level(), this);
         orb.setDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0));
         orb.setBurnTicks(100);
         orb.setMpCost(500.0);
         orb.setSkill((ManasSkillInstance)SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)ExtraSkills.FLAME_MANIPULATION.get()).orElse(null));
         orb.setExplosionRadius(4.0F);
         orb.setPos(this.getEyePosition().add(0.0, 4.0, 0.0));
         orb.setOwnerOffset(new Vec3(0.0, 4.0, 0.0));
         orb.setLookDistance(30.0F);
         orb.setDelayTick(15);
         orb.setDelaySizeChange(0.1F);
         orb.setNoGravity(true);
         this.level().addFreshEntity(orb);
         this.setMagicID(orb.getId());
      } else if (!(this.level().getEntity(orbID) instanceof FlameSphereProjectile orb)) {
         this.setMagicID(0);
         this.flameOrb();
      }

      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 3.0F, 0.95F + this.getRandom().nextFloat() * 0.1F);
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
      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 0.95F + this.getRandom().nextFloat() * 0.1F);
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

   protected void flareCircle(int heldTicks) {
      BlockPos pos = this.getFlarePos();
      if (heldTicks >= 20) {
         int flareID = this.getMagicID();
         if (flareID == 0) {
            FlareCircleEntity barrier = new FlareCircleEntity(this.level(), this);
            barrier.setDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0));
            barrier.setSize(5.0F);
            barrier.setHeight(7.0F);
            barrier.setLife(50);
            barrier.setHealth(200.0F);
            barrier.setPos(pos.getBottomCenter());
            barrier.setMpCost(2000.0);
            barrier.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)SpiritualMagics.FLARE_CIRCLE.get()));
            this.level().addFreshEntity(barrier);
            this.setMagicID(barrier.getId());
         } else if (this.level().getEntity(flareID) instanceof FlareCircleEntity barrier) {
            barrier.increaseLife(2);
         } else {
            this.setMagicID(0);
         }

         this.level()
            .playSound(
               null, this, (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 0.95F + this.getRandom().nextFloat() * 0.1F
            );
      }
   }

   public void combust(Predicate<LivingEntity> predicate) {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), this.getX(), this.getEyeY(), this.getZ(), 55, 0.08, 0.08, 0.08, 0.2, true
      );
      TensuraParticleHelper.spawnServerParticles(
         this.level(), (ParticleOptions)TensuraParticleTypes.HEAT_EFFECT.get(), this.getX(), this.getEyeY(), this.getZ(), 55, 0.08, 0.08, 0.08, 0.2, true
      );
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.LAVA, 2.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.LAVA, 3.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.LAVA, 4.0);
      AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 10.0);
      List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb, predicate);
      if (!list.isEmpty()) {
         DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.HEAT_WAVE, this);

         for (LivingEntity target : list) {
            target.hurt(source, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0F);
            target.setRemainingFireTicks(Math.max(200, target.getRemainingFireTicks()));
            target.setDeltaMovement(0.0, 0.1, 0.0);
            SkillHelper.knockBack(this, target, 2.0F);
         }
      }
   }

   private void summonSalamanders(int minRadius, int maxRadius) {
      if (this.level() instanceof ServerLevel level) {
         int var13 = Mth.floor(this.getX());
         int j = Mth.floor(this.getY());
         int k = Mth.floor(this.getZ());
         SalamanderEntity salamander = new SalamanderEntity((EntityType<? extends SalamanderEntity>)MonsterEntityTypes.SALAMANDER.get(), level);

         for (int l = 0; l < 50; l++) {
            int i1 = var13 + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int j1 = j + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int k1 = k + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            salamander.setPos(i1, j1, k1);
            if (level.isUnobstructed(salamander) && level.noCollision(salamander) && !level.containsAnyLiquid(salamander.getBoundingBox())) {
               salamander.finalizeSpawn(level, level.getCurrentDifficultyAt(salamander.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
               salamander.setTarget(this.getTarget());
               IExistence existence = TensuraStorages.getExistenceFrom(salamander);
               existence.setSummonedSecond(300);
               existence.setSummoner(this.getUUID());
               existence.markDirty();
               level.addFreshEntityWithPassengers(salamander);
               TensuraParticleHelper.addServerParticlesAroundSelf(salamander, (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(salamander, ParticleTypes.LAVA, 2.0);
               break;
            }
         }
      }
   }

   private void summonClones(int minRadius, int maxRadius) {
      if (this.level() instanceof ServerLevel level) {
         int var15 = Mth.floor(this.getX());
         int j = Mth.floor(this.getY());
         int k = Mth.floor(this.getZ());
         double EP = EnergyHelper.getBaseMaxEP(this);
         IfritCloneEntity clone = new IfritCloneEntity((EntityType<? extends IfritEntity>)MonsterEntityTypes.IFRIT_CLONE.get(), level);

         for (int l = 0; l < 50; l++) {
            int i1 = var15 + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int j1 = j + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int k1 = k + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            clone.setPos(i1, j1, k1);
            if (level.isUnobstructed(clone) && level.noCollision(clone) && !level.containsAnyLiquid(clone.getBoundingBox())) {
               clone.finalizeSpawn(level, level.getCurrentDifficultyAt(clone.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
               clone.setTarget(this.getTarget());
               clone.copySkills(this);
               EnergyHelper.setBaseMaxEP(clone, EP * 0.1F);
               IExistence existence = TensuraStorages.getExistenceFrom(clone);
               existence.setSummonedSecond(300);
               existence.setSummoner(this.getUUID());
               existence.markDirty();
               level.addFreshEntityWithPassengers(clone);
               TensuraParticleHelper.addServerParticlesAroundSelf(clone, (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(clone, ParticleTypes.LAVA, 2.0);
               break;
            }
         }
      }
   }

   @Override
   public Element getElemental() {
      return Element.FLAME;
   }

   @Override
   public Item getElementalCore() {
      return (Item)TensuraMaterialItems.ELEMENT_CORE_FIRE.get();
   }

   @Override
   public void die(DamageSource source) {
      super.die(source);
      if (!this.isAlive()) {
         this.triggerAnim("heldController", "death");
      }
   }

   protected void tickDeath() {
      if (++this.deathTime >= 38) {
         this.remove(RemovalReason.KILLED);
         this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 1.0F);
         this.spawnDeathParticles();
      }
   }

   @Override
   protected void spawnDeathParticles() {
      TensuraParticleHelper.addServerParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.RED_FIRE.get());
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.CLOUD);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, (ParticleOptions)TensuraParticleTypes.HEAT_EFFECT.get(), 2.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.CLOUD, 2.0);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_DEATH.get();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   public List<ExtendedSensor<IfritEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<IfritEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<IfritEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldTarget).whenStarting(entity -> entity.setMagicID(0)),
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

   public BrainActivityGroup<IfritEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new OrbitAttack()
               .requireInSight(false)
               .lookAtTargetWhileOrbiting(true)
               .orbitAttackInterval(entity -> 100 + entity.getRandom().nextInt(80))
               .orbitRadius((entity, target) -> 12.0)
               .orbitHeight((entity, target) -> 9.0)
               .canDoOrbitalAttack((entity, target) -> {
                  entity.summonMinions();
                  return false;
               })
               .onTick(entity -> {
                  entity.setFlying(true);
                  return true;
               }),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomHeldAttack()
                     .maxAttackRadius(40.0F)
                     .attackInterval(entity -> 0)
                     .onTick(
                        (entity, target, tick) -> {
                           if (this.shouldSummonSalamanders) {
                              switch (tick) {
                                 case 10:
                                    entity.summonSalamanders(3, 7);
                                    break;
                                 case 20:
                                    entity.summonSalamanders(5, 10);
                                    break;
                                 case 30:
                                    entity.summonSalamanders(7, 15);
                                    break;
                                 case 35:
                                    entity.shouldSummonSalamanders = false;
                              }
                           } else if (this.shouldSummonClones) {
                              switch (tick) {
                                 case 10:
                                    entity.summonClones(3, 7);
                                    break;
                                 case 20:
                                    entity.summonClones(5, 10);
                                    break;
                                 case 30:
                                    entity.summonClones(7, 15);
                                    break;
                                 case 35:
                                    entity.shouldSummonClones = false;
                              }
                           }

                           entity.level()
                              .playSound(
                                 null,
                                 entity,
                                 (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 10.0F,
                                 0.95F + entity.getRandom().nextFloat() * 0.1F
                              );
                           return tick < 40;
                        }
                     )
                     .whenStopping(entity -> {
                        entity.shouldSummonClones = false;
                        entity.shouldSummonSalamanders = false;
                     })
                     .startCondition(entity -> entity.getMagicID() == 0 && (entity.shouldSummonSalamanders || entity.shouldSummonClones))
                     .whenStarting(entity -> entity.triggerAnim("heldController", "summon")),
                  new CustomRangeAttack(15)
                     .maxAttackRadius(7.0F)
                     .performAttack((entity, target) -> {
                        entity.combust(this::shouldAttack);
                        entity.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 0.95F + entity.getRandom().nextFloat() * 0.1F);
                     })
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.2 && entity.getMagicID() == 0)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "burst")),
                  new CustomRangeAttack(25)
                     .maxAttackRadius(18.0F)
                     .attackInterval(entity -> 60)
                     .performAttack(
                        (entity, target) -> {
                           entity.hellFire(target);
                           entity.setMagicID(0);
                           entity.level()
                              .playSound(
                                 null,
                                 entity,
                                 (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 10.0F,
                                 0.95F + entity.getRandom().nextFloat() * 0.1F
                              );
                        }
                     )
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.1 && entity.getMagicID() == 0)
                     .whenStarting(entity -> {
                        entity.triggerAnim("miscController", "hellfire");
                        entity.setMagicID(1);
                     })
                     .whenStopping(entity -> entity.setMagicID(0)),
                  new CustomHeldAttack()
                     .minAttackRadius(0.0F)
                     .maxAttackRadius(18.0F)
                     .attackInterval(entity -> 60)
                     .onTick(
                        (entity, target, tick) -> {
                           if (tick == 0) {
                              entity.setMagicID(0);
                              entity.setFlarePos(target.blockPosition().getX(), target.blockPosition().getY(), target.blockPosition().getZ());
                              MagicCircle.castMagicCircle(
                                 5.0F,
                                 140,
                                 target.blockPosition().getBottomCenter().add(0.0, 0.1, 0.0),
                                 MagicCircleVariant.FLAME,
                                 entity,
                                 (ManasSkill)SpiritualMagics.FLARE_CIRCLE.get(),
                                 0,
                                 Pair.of(0.0, 2000.0)
                              );
                           } else {
                              entity.flareCircle(tick);
                           }

                           return tick < 65;
                        }
                     )
                     .whenStopping(entity -> entity.setMagicID(0))
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.2 && entity.getMagicID() == 0)
                     .whenStarting(entity -> entity.triggerAnim("heldController", "flare_circle")),
                  new CustomHeldAttack().minAttackRadius(0.0F).maxAttackRadius(40.0F).attackInterval(entity -> 60).onTick((entity, target, tick) -> {
                     if (tick >= 10 && tick <= 25) {
                        if (tick == 10) {
                           entity.setMagicID(0);
                        }

                        entity.flameOrb();
                     }

                     return tick < 40;
                  }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.3 && entity.getMagicID() == 0).whenStarting(entity -> {
                     entity.setMagicID(1);
                     entity.triggerAnim("heldController", "fire_ball_massive");
                  }).whenStopping(entity -> entity.setMagicID(0)),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(40.0F)
                     .performAttack(IfritEntity::shootFireBolt)
                     .startCondition(entity -> entity.getMagicID() == 0)
                     .whenStarting(entity -> {
                        entity.setMagicID(1);
                        entity.triggerAnim("miscController", entity.getRandom().nextBoolean() ? "fire_ball_right" : "fire_ball_left");
                     })
                     .whenStopping(entity -> entity.setMagicID(0))
               }
            )
         }
      );
   }

   private void summonMinions() {
      if (!this.isTame()) {
         List<SalamanderEntity> salamanders = this.level().getEntitiesOfClass(SalamanderEntity.class, this.getBoundingBox().inflate(20.0));
         if (!salamanders.isEmpty()) {
            for (SalamanderEntity entity : salamanders) {
               entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), 200, 1, false, false, false));
            }
         } else {
            this.shouldSummonSalamanders = this.getRandom().nextFloat() <= 0.25;
            if (!this.shouldSummonSalamanders) {
               if (this.isTame()) {
                  return;
               }

               List<IfritCloneEntity> clones = this.level().getEntitiesOfClass(IfritCloneEntity.class, this.getBoundingBox().inflate(20.0));
               if (clones.isEmpty()) {
                  this.shouldSummonClones = this.getRandom().nextFloat() <= 0.25;
               }
            }
         }
      }
   }

   protected PlayState loopController(AnimationState<IfritEntity> state) {
      String name;
      if (this.isNoAi() || this.isDeadOrDying()) {
         name = "animation.ifrit.idle";
      } else if (this.isSleeping()) {
         name = "animation.ifrit.sleep";
      } else if (this.isInSittingPose() && this.training) {
         name = "animation.ifrit.idle_train";
      } else if (state.isMoving()) {
         if (this.onGround()) {
            if (!this.isAngry() && !this.isSprinting()) {
               name = "animation.ifrit.walk";
            } else {
               name = "animation.ifrit.run";
            }
         } else {
            name = "animation.ifrit.fly";
         }
      } else if (this.onGround()) {
         name = "animation.ifrit.idle";
      } else {
         name = "animation.ifrit.idle_fly";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("fire_ball_right", RawAnimation.begin().then("animation.ifrit.fire_ball_right", LoopType.PLAY_ONCE))
               .triggerableAnim("fire_ball_left", RawAnimation.begin().then("animation.ifrit.fire_ball_left", LoopType.PLAY_ONCE))
               .triggerableAnim("hellfire", RawAnimation.begin().then("animation.ifrit.fire_wall", LoopType.PLAY_ONCE))
               .triggerableAnim("burst", RawAnimation.begin().then("animation.ifrit.burst", LoopType.PLAY_ONCE)),
            new AnimationController(this, "heldController", 3, event -> PlayState.STOP)
               .triggerableAnim("fire_ball_massive", RawAnimation.begin().then("animation.ifrit.fire_ball_massive", LoopType.PLAY_ONCE))
               .triggerableAnim("flare_circle", RawAnimation.begin().then("animation.ifrit.flare_circle", LoopType.PLAY_ONCE))
               .triggerableAnim("summon", RawAnimation.begin().then("animation.ifrit.summon", LoopType.PLAY_ONCE))
               .triggerableAnim("death", RawAnimation.begin().then("animation.ifrit.death", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
