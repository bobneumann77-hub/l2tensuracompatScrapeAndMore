package io.github.manasmods.tensura.entity.human;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.movement.TeleportToEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.projectile.magic.SpatialArrowProjectile;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.ITeleportation;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.EntityHitResult;
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
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.InteractWithDoor;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
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

public class MaiFurukiEntity extends OtherworlderEntity implements SmartBrainOwner<MaiFurukiEntity>, ITeleportation {
   private SmartBrainSchedule schedule;

   public MaiFurukiEntity(EntityType<? extends MaiFurukiEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 10.0)
         .add(Attributes.ATTACK_DAMAGE, 30.0)
         .add(Attributes.ATTACK_KNOCKBACK, 2.0)
         .add(Attributes.MAX_HEALTH, 300.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(TensuraAttributes.PRESENCE_SENSE, 1.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 5.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 2.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 2.0);
   }

   @Override
   public ResourceLocation getTextureLocation() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/otherworlder/mai_furuki.png");
   }

   @Override
   public List<ManasSkill> getUniqueSkills() {
      return List.of((ManasSkill)UniqueSkills.TRAVELER.get());
   }

   public MaiFurukiEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      MaiFurukiEntity entity = (MaiFurukiEntity)((EntityType)HumanEntityTypes.MAI_FURUKI.get()).create(pLevel);
      if (entity == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         entity.setOwnerUUID(uuid);
         entity.setTame(true, true);
      }

      return entity;
   }

   public boolean hurt(DamageSource source, float pAmount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         return this.shouldDodge(source) ? false : super.hurt(source, pAmount);
      }
   }

   @Override
   public void onProjectileImpact(
      EntityHitResult hitResult, Projectile projectile, Changeable<ProjectileDeflection> deflection, Changeable<ProjectileHitResult> result
   ) {
      if (this.getTraveler() != null) {
         if (!(projectile instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0)) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
            if (!this.level().isClientSide() && this.getTarget() != null) {
               this.teleportTowards(this, this.getTarget(), 16.0);
            }

            if (projectile.getOwner() instanceof LivingEntity shooter) {
               this.setLastHurtByMob(shooter);
            }

            result.set(ProjectileHitResult.PASS);
         }
      }
   }

   private boolean shouldDodge(DamageSource source) {
      if (this.level().isClientSide()) {
         return false;
      }

      if (this.getTraveler() == null) {
         return false;
      }

      if (source.getDirectEntity() instanceof LivingEntity entity) {
         if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
         }

         if (!TensuraDamageHelper.isPhysicalAttack(source)) {
            return false;
         }

         if (entity.getRandom().nextFloat() >= 0.25) {
            return false;
         }

         if (source.isCreativePlayer()) {
            return false;
         }

         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
         if (!this.level().isClientSide() && this.getTarget() != null) {
            this.teleportTowards(this, this.getTarget(), 12.0);
         }

         this.setLastHurtByMob(entity);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void performBowAttack(LivingEntity target, ItemStack bowStack, float distance) {
      if (this.getTraveler() == null) {
         super.performBowAttack(target, bowStack, distance);
      } else if (this.getRandom().nextFloat() <= 0.3) {
         this.stardustRain(target);
      } else {
         SpatialArrowProjectile arrow = new SpatialArrowProjectile(this.level(), this);
         arrow.setLife(50);
         arrow.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
         arrow.setMpCost(150.0);
         arrow.setSkill(this.getTraveler());
         arrow.setMode(3);
         if (this.getHealth() < this.getMaxHealth() / 2.0F) {
            arrow.shootFromBehind(target, 2.0F, 0.0F);
         } else {
            arrow.setPos(this.getX(), this.getEyeY() - 0.1, this.getZ());
            double d0 = target.getX() - this.getX();
            double d1 = target.getY(0.3333333333333333) - arrow.getY();
            double d2 = target.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            arrow.shoot(d0, d1 + d3 * 0.2F, d2, 1.6F, 0.0F);
         }

         this.playSound((SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
         this.level().addFreshEntity(arrow);
      }
   }

   protected void stardustRain(LivingEntity target) {
      Vec3 pos = target.position().add(0.0, target.getEyeHeight(), 0.0);
      int arrowAmount = 12;

      for (int i = 0; i < arrowAmount; i++) {
         Vec3 arrowPos = pos.add(
            new Vec3(0.0, Math.random() - 0.5, 0.6).normalize().scale(target.getBbWidth() + 6.0F).yRot(360.0F * i * (float) (Math.PI / 180.0) / arrowAmount)
         );
         SpatialArrowProjectile arrow = new SpatialArrowProjectile(this.level(), this);
         arrow.setSpeed(1.0F);
         arrow.setPos(arrowPos);
         arrow.shootFromRot(pos.subtract(arrowPos).normalize());
         arrow.setSkill(this.getTraveler());
         arrow.setMode(4);
         arrow.setLife(50);
         arrow.setDamage((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
         arrow.setIgnoreInvulnerabilityOnHit(true);
         arrow.setMpCost(5000.0F / arrowAmount);
         this.level().addFreshEntity(arrow);
         this.level()
            .playSound(null, arrow.getX(), arrow.getY(), arrow.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   @Nullable
   private ManasSkillInstance getTraveler() {
      Optional<ManasSkillInstance> skill = SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)UniqueSkills.TRAVELER.get());
      if (skill.isEmpty()) {
         return null;
      } else {
         return !skill.get().canInteractSkill(this) ? null : skill.get();
      }
   }

   @Override
   protected float getEquipmentDropChance(EquipmentSlot pSlot) {
      if (this.isTame()) {
         return 0.0F;
      }

      float chance = super.getEquipmentDropChance(pSlot);
      return pSlot.equals(EquipmentSlot.MAINHAND) ? Math.max(0.05F, chance) : chance;
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         this.populateDefaultEquipmentSlots(this.random, pDifficulty);
      }

      TensuraBehaviourHelper.setHome(this, pLevel.getLevel(), this.blockPosition());
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      ItemStack stack = new ItemStack((ItemLike)TensuraToolItems.LONG_SPIDER_BOW.get());
      stack.set(DataComponents.ITEM_NAME, Component.literal("Crescent Bow").withStyle(ChatFormatting.GOLD));
      stack.enchant(TensuraEnchantmentHelper.getEnchantment(this.level(), Enchantments.MENDING), 1);
      stack.enchant(TensuraEnchantmentHelper.getEnchantment(this.level(), Enchantments.INFINITY), 1);
      this.setItemSlot(EquipmentSlot.MAINHAND, stack);
      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), stack);
      this.updateContainerEquipment();
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<MaiFurukiEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new SleepSensor()});
   }

   public BrainActivityGroup<MaiFurukiEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new WakeUp().stopIf(entity -> {
         if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
            entity.stopSleeping();
            return true;
         } else {
            return false;
         }
      }), new LookAtTarget(), new FloatToSurfaceOfFluid(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<MaiFurukiEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  TensuraBehaviourHelper.getPreyTargeting(this, this::shouldTarget),
                  new SubordinateFollowOwner(),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new InteractWithDoor(),
            new HumanoidConsumeItem().startCondition(entity -> entity.shouldHeal()).stopIf(entity -> !entity.shouldHeal()),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<MaiFurukiEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new TeleportToEntity()
               .following(Mob::getTarget)
               .canTeleportOffGroundWhen(entity -> true)
               .teleportRadius((entity, target) -> 10)
               .teleportToTargetAfter((entity, target) -> {
                  if (entity.tickCount % 200 == 0) {
                     return 10.0;
                  } else {
                     return entity.tickCount % 20 == 0 ? 20.0 : 32.0;
                  }
               })
               .onSuccessTeleport((entity, target) -> {
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.REVERSE_PORTAL, 1.0);
                  entity.level()
                     .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               }),
            new StrafeTarget().stopStrafingWhen(entity -> !entity.usingRangedWeapon()).startCondition(PlayerLikeEntity::usingRangedWeapon),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F).startCondition(entity -> !entity.usingRangedWeapon()),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  PlayerLikeEntity.getSpearAttack(20).attackInterval(entity -> 5).attackRadius(25.0F),
                  PlayerLikeEntity.getCrossbowAttack().attackInterval(entity -> 10).attackRadius(30.0F),
                  PlayerLikeEntity.getBowAttack().attackInterval(entity -> 10).attackRadius(25.0F),
                  new AnimatableMeleeAttack(1)
                     .attackInterval(entity -> 5)
                     .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
                     .startCondition(entity -> !entity.usingRangedWeapon())
               }
            )
         }
      );
   }

   public Map<Activity, BrainActivityGroup<? extends MaiFurukiEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends MaiFurukiEntity>>)Util.make(
         new Object2ObjectOpenHashMap(), map -> map.put(Activity.REST, TensuraBehaviourHelper.getHumanoidSleepActivityGroup(this))
      );
   }

   public SmartBrainSchedule getSchedule() {
      if (this.schedule == null) {
         this.schedule = new SmartBrainSchedule().activityAt(10, Activity.IDLE).activityAt(13000, Activity.REST);
      }

      return this.schedule;
   }

   public void startSleeping(BlockPos blockPos) {
      super.startSleeping(blockPos);
      BrainUtils.setMemory(this, MemoryModuleType.LAST_SLEPT, this.level().getGameTime());
      BrainUtils.clearMemory(this, MemoryModuleType.WALK_TARGET);
      BrainUtils.clearMemory(this, MemoryModuleType.LOOK_TARGET);
      BrainUtils.clearMemory(this, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
   }

   public void stopSleeping() {
      super.stopSleeping();
      BrainUtils.setMemory(this, MemoryModuleType.LAST_WOKEN, this.level().getGameTime());
   }
}
