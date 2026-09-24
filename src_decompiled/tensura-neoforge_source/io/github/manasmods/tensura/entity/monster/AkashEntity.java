package io.github.manasmods.tensura.entity.monster;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.OrbitAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.TeleportToEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomFlyAndWalkTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.beam.SpatialRayProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.DimensionCutProjectile;
import io.github.manasmods.tensura.entity.template.GreaterSpiritEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ITeleportation;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AkashEntity extends GreaterSpiritEntity implements GeoEntity, SmartBrainOwner<AkashEntity>, ITeleportation {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean shouldSummonCats = false;
   private boolean training = false;

   public AkashEntity(EntityType<? extends AkashEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.bossEvent = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.NOTCHED_20).setPlayBossMusic(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.MAX_HEALTH, 300.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.FLYING_SPEED, 0.7F)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.STEP_HEIGHT, 2.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 1.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 3.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 20.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 3.0);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || super.isInvulnerableTo(source);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (pSource.getEntity() instanceof WingedCatEntity || pSource.getEntity() instanceof AkashEntity) {
         return false;
      }

      if (this.shouldDodge(pSource, pAmount)) {
         return false;
      }

      boolean hurt = super.hurt(pSource, pAmount);
      if (hurt && pSource.getEntity() instanceof LivingEntity damageSource && !pSource.isCreativePlayer()) {
         if (!damageSource.isAlive() || this.isAlliedTo(damageSource)) {
            return true;
         }

         List<WingedCatEntity> list = this.level().getEntitiesOfClass(WingedCatEntity.class, this.getBoundingBox().inflate(32.0), entity -> !entity.isTame());
         if (!list.isEmpty()) {
            list.forEach(cat -> cat.setTarget(damageSource));
         }
      }

      return hurt;
   }

   private boolean shouldDodge(DamageSource source, float damage) {
      if (this.level().isClientSide()) {
         return false;
      } else if (!(source.getEntity() instanceof LivingEntity entity && damage >= 10.0F)) {
         return false;
      } else {
         if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
         }

         if (entity.getRandom().nextFloat() >= 0.1) {
            return false;
         }

         if (source.isCreativePlayer()) {
            return false;
         }

         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
         if (this.getTarget() != null) {
            this.teleportTowards(this, this.getTarget(), 12.0);
         }

         this.setLastHurtByMob(entity);
         return true;
      }
   }

   @Override
   public boolean isAlliedTo(Entity entity) {
      if (super.isAlliedTo(entity)) {
         return true;
      } else if (entity instanceof WingedCatEntity cat) {
         return cat.isTame() == this.isTame();
      } else {
         return entity instanceof AkashEntity akash ? akash.isTame() == this.isTame() : false;
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide() && this.isAlive()) {
         if (this.tickCount % 10 == 0) {
            TensuraParticleHelper.addParticlesAroundSelf(this, ParticleTypes.REVERSE_PORTAL, 2.0);
         }

         if (this.tickCount % 200 == 0 && this.onGround() && !this.isSleeping()) {
            this.training = !this.training && this.getRandom().nextFloat() <= 0.25F;
         }
      }
   }

   public void shootSpaceCut(LivingEntity target) {
      DimensionCutProjectile cut = new DimensionCutProjectile(this.level(), this);
      cut.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.SPATIAL_MANIPULATION.get()));
      float angle = (float) (Math.PI / 180.0) * this.yBodyRot;
      double xOffset = Mth.sin((float)(Math.PI + angle));
      double zOffset = Mth.cos(angle);
      cut.moveTo(this.getX() + xOffset, this.getEyeY() - 0.2, this.getZ() + zOffset, this.getYRot(), this.getXRot());
      cut.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
      cut.setSpeed(2.0F);
      cut.setNoGravity(true);
      cut.shootToward(target, 1.0F, 0.0F);
      this.level().addFreshEntity(cut);
      this.level()
         .playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 10.0F, 0.95F + this.random.nextFloat() * 0.1F);
      this.setMagicID(0);
   }

   private void spatialRay(LivingEntity target) {
      this.getLookControl().setLookAt(target.getX(), target.getY() + target.getBbHeight() / 2.0F, target.getZ());
      SpatialRayProjectile ray = new SpatialRayProjectile(this.level(), this);
      ray.setFollowingOwner(true);
      ray.setSize(1.0F);
      ray.setLife(25);
      float damage = (float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0);
      ray.setDamage(damage);
      ray.setRange(30.0F);
      ray.setPos(this.getEyePosition());
      ray.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.SPATIAL_MANIPULATION.get()));
      ray.setMpCost(1000.0);
      this.level().addFreshEntity(ray);
      this.level().playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 0.5F);
      this.setMagicID(0);
   }

   private void spatialStorm(LivingEntity target) {
      Vec3 pos = target.getEyePosition();
      ((ServerLevel)this.level()).sendParticles(ParticleTypes.FLASH, pos.x(), pos.y(), pos.z(), 1, 0.0, 0.0, 0.0, 0.0);
      int rayAmount = 10;

      for (int i = 0; i < rayAmount; i++) {
         Vec3 startOffset = new Vec3(0.0, 1.0 - Math.random() * 2.0, 0.6).normalize().scale(20.0).yRot(360.0F * i * (float) (Math.PI / 180.0) / rayAmount);
         SpatialRayProjectile ray = new SpatialRayProjectile(this.level(), this);
         ray.setFollowingOwner(false);
         ray.setLife(20);
         ray.setSize(0.75F);
         ray.setRange(10.0F);
         ray.setSkill(SkillUtils.getSkillOrNull(this, (ManasSkill)ExtraSkills.SPATIAL_MANIPULATION.get()));
         Vec3 rayPos = pos.add(startOffset);
         ray.setPos(rayPos.add(pos.subtract(rayPos).normalize().scale(10.0)));
         ray.setTargetPos(pos.x(), pos.y(), pos.z());
         ray.setDamage((float)(this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.5));
         this.level().addFreshEntity(ray);
         TensuraParticleHelper.addServerParticlesAroundSelf(ray, ParticleTypes.FLASH);
      }

      this.level().playSound(null, this, (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 0.5F);
      this.setMagicID(0);
   }

   public void combust(Predicate<LivingEntity> predicate) {
      TensuraParticleHelper.spawnServerParticles(this.level(), ParticleTypes.END_ROD, this.getX(), this.getEyeY(), this.getZ(), 55, 0.08, 0.08, 0.08, 0.2, true);
      TensuraParticleHelper.spawnServerParticles(
         this.level(), ParticleTypes.REVERSE_PORTAL, this.getX(), this.getEyeY(), this.getZ(), 55, 0.08, 0.08, 0.08, 0.2, true
      );
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.REVERSE_PORTAL, 3.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.REVERSE_PORTAL, 4.0);
      AABB aabb = this.getBoundingBox().inflate(this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE) + 10.0);
      List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb, predicate);
      if (!list.isEmpty()) {
         DamageSource source = TensuraDamageTypes.getEntityDamageSource(this.level(), TensuraDamageTypes.SPACE_ELEMENTAL, this)
            .tensura$setElement(Element.SPACE)
            .tensura$setMagicType(Magic.MagicType.SPIRITUAL);

         for (LivingEntity target : list) {
            target.hurt(source, (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F);
            target.setDeltaMovement(0.0, 0.1, 0.0);
            SkillHelper.knockBack(this, target, 2.0F);
         }
      }
   }

   private void summonWingedCat(int minRadius, int maxRadius) {
      if (this.level() instanceof ServerLevel serverLevel) {
         int var13 = Mth.floor(this.getX());
         int j = Mth.floor(this.getY());
         int k = Mth.floor(this.getZ());
         WingedCatEntity cat = new WingedCatEntity((EntityType<? extends WingedCatEntity>)MonsterEntityTypes.WINGED_CAT.get(), serverLevel);

         for (int l = 0; l < 50; l++) {
            int i1 = var13 + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int j1 = j + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            int k1 = k + Mth.nextInt(this.random, minRadius, maxRadius) * Mth.nextInt(this.random, -1, 1);
            cat.setPos(i1, j1, k1);
            if (serverLevel.isUnobstructed(cat) && serverLevel.noCollision(cat)) {
               cat.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(cat.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
               cat.setTarget(this.getTarget());
               IExistence existence = TensuraStorages.getExistenceFrom(cat);
               existence.setSummonedSecond(300);
               existence.setSummoner(this.getUUID());
               existence.markDirty();
               serverLevel.addFreshEntityWithPassengers(cat);
               TensuraParticleHelper.addServerParticlesAroundSelf(cat, ParticleTypes.REVERSE_PORTAL, 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(cat, ParticleTypes.END_ROD, 2.0);
               break;
            }
         }
      }
   }

   @Override
   public Element getElemental() {
      return Element.SPACE;
   }

   @Override
   public Item getElementalCore() {
      return (Item)TensuraMaterialItems.ELEMENT_CORE_SPACE.get();
   }

   @Override
   public void die(DamageSource source) {
      super.die(source);
      if (!this.isAlive()) {
         this.triggerAnim("heldController", "death");
      }
   }

   protected void tickDeath() {
      if (++this.deathTime >= 39) {
         this.remove(RemovalReason.KILLED);
         this.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 1.0F);
         this.spawnDeathParticles();
      }
   }

   @Override
   protected void spawnDeathParticles() {
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.REVERSE_PORTAL);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.END_ROD);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.REVERSE_PORTAL, 2.0);
      TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.END_ROD, 2.0);
   }

   protected SoundEvent getAmbientSound() {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_SPACE_AMBIENT.get();
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_SPACE_HURT.get();
   }

   protected SoundEvent getDeathSound() {
      return (SoundEvent)TensuraSoundEvents.SPIRIT_SPACE_DEATH.get();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this);
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<AkashEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<AkashEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new LookAtTarget(), TensuraTamableEntity.getMoveOrFlyToWalkTarget()});
   }

   public BrainActivityGroup<AkashEntity> getIdleTasks() {
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

   public BrainActivityGroup<AkashEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new TeleportToEntity()
               .following(Mob::getTarget)
               .canTeleportOffGroundWhen(entity -> true)
               .teleportRadius((entity, target) -> 8)
               .teleportToTargetAfter((entity, target) -> {
                  if (entity.tickCount % 200 == 0) {
                     return 10.0;
                  } else {
                     return entity.tickCount % 20 == 0 ? 20.0 : 32.0;
                  }
               })
               .onSuccessTeleport((entity, target) -> {
                  entity.setFlying(true);
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.REVERSE_PORTAL, 1.0);
                  entity.level()
                     .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               }),
            new OrbitAttack()
               .requireInSight(false)
               .lookAtTargetWhileOrbiting(true)
               .orbitAttackInterval(entity -> 120 + entity.getRandom().nextInt(80))
               .orbitRadius((entity, target) -> 12.0)
               .orbitHeight((entity, target) -> 8.0)
               .canDoOrbitalAttack((entity, target) -> {
                  if (entity.isTame()) {
                     return true;
                  }

                  List<WingedCatEntity> serpents = entity.level().getEntitiesOfClass(WingedCatEntity.class, entity.getBoundingBox().inflate(20.0));
                  if (!serpents.isEmpty()) {
                     for (WingedCatEntity cat : serpents) {
                        cat.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), 200, 1, false, false, false));
                     }
                  } else {
                     entity.shouldSummonCats = entity.getRandom().nextFloat() <= 0.25;
                  }

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
                           switch (tick) {
                              case 10:
                                 entity.summonWingedCat(3, 7);
                                 break;
                              case 20:
                                 entity.summonWingedCat(5, 10);
                                 break;
                              case 30:
                                 entity.summonWingedCat(7, 15);
                                 break;
                              case 35:
                                 entity.shouldSummonCats = false;
                           }

                           entity.level()
                              .playSound(
                                 null,
                                 entity,
                                 (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(),
                                 TensuraSkill.ABILITY_SOUND,
                                 10.0F,
                                 0.95F + entity.getRandom().nextFloat() * 0.1F
                              );
                           return tick < 40;
                        }
                     )
                     .whenStopping(entity -> entity.shouldSummonCats = false)
                     .startCondition(entity -> entity.getMagicID() == 0 && entity.shouldSummonCats)
                     .whenStarting(entity -> entity.triggerAnim("heldController", "summon")),
                  new CustomRangeAttack(15)
                     .maxAttackRadius(7.0F)
                     .performAttack((entity, target) -> {
                        entity.combust(this::shouldAttack);
                        entity.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 0.95F + entity.getRandom().nextFloat() * 0.1F);
                     })
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.2 && entity.getMagicID() == 0)
                     .whenStarting(entity -> entity.triggerAnim("miscController", "burst")),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(30.0F)
                     .attackInterval(entity -> 60)
                     .performAttack(AkashEntity::spatialStorm)
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.1 && entity.getMagicID() == 0)
                     .whenStarting(entity -> {
                        entity.triggerAnim("miscController", "burst");
                        entity.setMagicID(1);
                     })
                     .whenStopping(entity -> entity.setMagicID(0)),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(30.0F)
                     .attackInterval(entity -> 60)
                     .performAttack(AkashEntity::spatialRay)
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.3 && entity.getMagicID() == 0)
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
                              (ManasSkill)ExtraSkills.SPATIAL_MANIPULATION.get(),
                              0,
                              Pair.of(0.0, 1000.0)
                           );
                           entity.triggerAnim("miscController", "spatial_ray");
                           entity.setMagicID(1);
                        }
                     )
                     .whenStopping(entity -> entity.setMagicID(0)),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(40.0F)
                     .performAttack(AkashEntity::shootSpaceCut)
                     .startCondition(entity -> entity.getMagicID() == 0)
                     .whenStarting(entity -> {
                        entity.setMagicID(1);
                        entity.triggerAnim("miscController", "space_cut");
                     })
                     .whenStopping(entity -> entity.setMagicID(0))
               }
            )
         }
      );
   }

   protected PlayState loopController(AnimationState<AkashEntity> state) {
      String name;
      if (this.isNoAi() || this.isDeadOrDying()) {
         name = "animation.akash.idle";
      } else if (this.isSleeping()) {
         name = "animation.akash.relax";
      } else if (this.isInSittingPose()) {
         if (this.training) {
            name = "animation.akash.idle_train";
         } else {
            name = "animation.akash.idle";
         }
      } else if (state.isMoving()) {
         if (!this.isAngry() && !this.isSprinting()) {
            name = "animation.akash.fly";
         } else {
            name = "animation.akash.fly_fast";
         }
      } else {
         name = "animation.akash.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(this, "loopController", 10, this::loopController),
            new AnimationController(this, "miscController", 3, event -> PlayState.STOP)
               .triggerableAnim("space_cut", RawAnimation.begin().then("animation.akash.space_cut", LoopType.PLAY_ONCE))
               .triggerableAnim("spatial_ray", RawAnimation.begin().then("animation.akash.spatial_ray", LoopType.PLAY_ONCE))
               .triggerableAnim("burst", RawAnimation.begin().then("animation.akash.burst", LoopType.PLAY_ONCE)),
            new AnimationController(this, "heldController", 3, event -> PlayState.STOP)
               .triggerableAnim("summon", RawAnimation.begin().then("animation.akash.summon", LoopType.PLAY_ONCE))
               .triggerableAnim("death", RawAnimation.begin().then("animation.akash.death", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
