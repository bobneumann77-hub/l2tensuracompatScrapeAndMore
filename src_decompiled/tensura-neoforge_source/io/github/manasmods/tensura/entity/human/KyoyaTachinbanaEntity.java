package io.github.manasmods.tensura.entity.human;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.unique.SevererSkill;
import io.github.manasmods.tensura.data.otherworlder.OtherworlderSpawnDistribution;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.WakeUp;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.sensor.SleepSensor;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.SevererBladeProjectile;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.TensuraEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class KyoyaTachinbanaEntity extends OtherworlderEntity implements SmartBrainOwner<KyoyaTachinbanaEntity> {
   private SmartBrainSchedule schedule;

   public KyoyaTachinbanaEntity(EntityType<? extends KyoyaTachinbanaEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.MAX_HEALTH, 200.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 5.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 2.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 2.0);
   }

   @Override
   public ResourceLocation getTextureLocation() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/otherworlder/kyoya_tachibana.png");
   }

   @Override
   public List<ManasSkill> getUniqueSkills() {
      return List.of((ManasSkill)UniqueSkills.SEVERER.get());
   }

   public KyoyaTachinbanaEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      KyoyaTachinbanaEntity entity = (KyoyaTachinbanaEntity)((EntityType)HumanEntityTypes.KYOYA_TACHIBANA.get()).create(pLevel);
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

   private void bladeShoot(LivingEntity target) {
      if (this.getMainHandItem().is((Item)TensuraToolItems.SPATIAL_BLADE.get())) {
         SevererBladeProjectile blade = new SevererBladeProjectile(this.level(), this, true, this.getMainHandItem());
         blade.setSkill(this.getSeverer());
         blade.setMode(0);
         blade.shootToward(target, 1.5F, 0.0F);
         this.level().addFreshEntity(blade);
         this.level()
            .playSound(
               null,
               this.getX(),
               this.getY(),
               this.getZ(),
               (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(),
               TensuraSkill.ABILITY_SOUND,
               0.5F,
               0.4F + (this.random.nextFloat() * 0.4F + 0.8F)
            );
      }
   }

   private void bladeStorm(int arrowAmount) {
      int arrowRot = 360 / arrowAmount;

      for (int i = 0; i < arrowAmount; i++) {
         Vec3 arrowOffset = new Vec3(0.0, 1.0, 0.0).zRot((arrowRot * i - arrowRot / 2.0F) * (float) (Math.PI / 180.0));
         Vec3 arrowPos = this.getEyePosition()
            .add(this.getLookAngle().normalize().scale(1.0))
            .add(arrowOffset.xRot(-this.getXRot() * (float) (Math.PI / 180.0)).yRot(-this.getYRot() * (float) (Math.PI / 180.0)));
         SevererBladeProjectile blade = new SevererBladeProjectile(this.level(), this, this.getSpatialBlade());
         blade.setSkill(this.getSeverer());
         blade.setMode(1);
         blade.setNoGravity(true);
         blade.setPos(arrowPos);
         blade.setOwnerOffset(arrowOffset);
         blade.setLookDistance(30.0F);
         blade.setDelayTick(20);
         blade.updateShootRotation();
         this.level().addFreshEntity(blade);
         this.level()
            .playSound(null, arrowPos.x(), arrowPos.y(), arrowPos.z(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   private void bladeJail(LivingEntity target, int amount) {
      for (int i = 0; i < amount; i++) {
         Vec3 bladePos = target.getEyePosition()
            .add(new Vec3(0.0, Math.random() - 0.5, 0.6).normalize().scale(target.getBbWidth() + 5.0F).yRot(360 * i * (float) (Math.PI / 180.0) / amount));
         SevererBladeProjectile blade = new SevererBladeProjectile(this.level(), this, this.getSpatialBlade());
         blade.setSkill(this.getSeverer());
         blade.setMode(1);
         blade.setNoGravity(true);
         blade.setPos(bladePos);
         blade.setDelayVec(target.position().subtract(bladePos).normalize());
         blade.setDelayTick(20);
         blade.updateShootRotation();
         this.level().addFreshEntity(blade);
         this.level().playSound(null, bladePos.x(), bladePos.y(), bladePos.z(), SoundEvents.ENCHANTMENT_TABLE_USE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   private ItemStack getSpatialBlade() {
      if (this.getMainHandItem().is((Item)TensuraToolItems.SPATIAL_BLADE.get())) {
         return this.getMainHandItem();
      }

      Item item = (Item)TensuraToolItems.SPATIAL_BLADE.get();
      ItemStack blade = item.getDefaultInstance();
      blade.set((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), true);
      blade.set((DataComponentType)TensuraDataComponents.SKILL.get(), UniqueSkills.SEVERER.getId());
      blade.set(DataComponents.ITEM_NAME, ((SevererSkill)UniqueSkills.SEVERER.get()).getChatDisplayName(false).append(" ").append(item.getName(blade)));
      blade.enchant(TensuraEnchantmentHelper.getEnchantment(this.level(), TensuraEnchantments.SEVERANCE), 5);
      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), blade);
      this.updateContainerEquipment();
      return blade;
   }

   @Nullable
   private ManasSkillInstance getSeverer() {
      Optional<ManasSkillInstance> skill = SkillAPI.getSkillsFrom(this).getSkill((ManasSkill)UniqueSkills.SEVERER.get());
      if (skill.isEmpty()) {
         return null;
      } else {
         return !skill.get().canInteractSkill(this) ? null : skill.get();
      }
   }

   public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
      return TensuraEntityTypes.rollSpawn(TensuraEntityTypes.CONFIG.SpawnChance.otherworlder, pLevel, pSpawnReason)
         && super.checkSpawnRules(pLevel, pSpawnReason);
   }

   @Override
   protected boolean canSpawnSpecialVariant(MobSpawnType pSpawnType) {
      return pSpawnType != MobSpawnType.MOB_SUMMONED
            && pSpawnType != MobSpawnType.SPAWN_EGG
            && pSpawnType != MobSpawnType.DISPENSER
            && pSpawnType != MobSpawnType.TRIGGERED
            && pSpawnType != MobSpawnType.COMMAND
            && pSpawnType != MobSpawnType.SPAWNER
            && pSpawnType != MobSpawnType.TRIAL_SPAWNER
         ? pSpawnType != MobSpawnType.BUCKET
         : false;
   }

   @NotNull
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canSpawnSpecialVariant(pReason)) {
         if (TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.shizuChance, pLevel.getRandom())) {
            ShizuEntity shizu = new ShizuEntity((EntityType<? extends ShizuEntity>)HumanEntityTypes.SHIZU.get(), pLevel.getLevel());
            shizu.setPos(this.getX(), this.getY(), this.getZ());
            shizu.finalizeSpawn(pLevel, pLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.NATURAL, null);
            pLevel.addFreshEntity(shizu);
            this.discard();
            return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
         }

         if (TensuraEntityTypes.rollChance(TensuraEntityTypes.CONFIG.SpecialVariant.hinataChance, pLevel.getRandom())) {
            HinataSakaguchiEntity hinata = new HinataSakaguchiEntity(
               (EntityType<? extends HinataSakaguchiEntity>)HumanEntityTypes.HINATA_SAKAGUCHI.get(), pLevel.getLevel()
            );
            hinata.setPos(this.getX(), this.getY(), this.getZ());
            hinata.finalizeSpawn(pLevel, pLevel.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.NATURAL, null);
            pLevel.addFreshEntity(hinata);
            this.discard();
            return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
         }

         double chance = pLevel.getRandom().nextFloat();

         for (OtherworlderSpawnDistribution spawning : pLevel.registryAccess().registryOrThrow(TensuraCustomData.OTHERWORLDER_SPAWN_DISTRIBUTION)) {
            if (!(chance >= spawning.chance())) {
               if (!spawning.entity().equals(EntityType.getKey(this.getType()))) {
                  Optional<EntityType<?>> entityType = EntityType.byString(spawning.entity().toString());
                  if (!entityType.isEmpty()) {
                     Entity entity = entityType.get().create(pLevel.getLevel());
                     if (entity instanceof Mob mob) {
                        entity.setPos(this.position());
                        mob.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
                        pLevel.addFreshEntity(entity);
                        this.discard();
                        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
                     }
                  }
               }
               break;
            }

            chance -= spawning.chance();
         }
      }

      if (this.canRandomizeSpawnData(pReason)) {
         this.populateDefaultEquipmentSlots(this.random, pDifficulty);
      }

      TensuraBehaviourHelper.setHome(this, pLevel.getLevel(), this.blockPosition());
      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      super.populateDefaultEquipmentSlots(pRandom, pDifficulty);
      this.getSpatialBlade();
      if (!(pRandom.nextFloat() >= 0.2F)) {
         ItemStack stack = new ItemStack(Items.SHIELD);
         this.setItemSlot(EquipmentSlot.OFFHAND, stack);
         this.inventory.setItem(this.getSlotId(EquipmentSlot.OFFHAND), stack);
         this.updateContainerEquipment();
      }
   }

   @Nullable
   @Override
   public Item getEquipmentForArmor(EquipmentSlot pSlot, int pChance) {
      return switch (pSlot) {
         case HEAD -> pChance == 1 ? Items.LEATHER_HELMET : null;
         case CHEST -> pChance == 1 ? Items.LEATHER_CHESTPLATE : (pChance == 3 ? Items.CHAINMAIL_CHESTPLATE : null);
         case LEGS -> pChance == 1 ? Items.LEATHER_LEGGINGS : (pChance == 3 ? Items.CHAINMAIL_LEGGINGS : null);
         case FEET -> pChance == 1 ? Items.LEATHER_BOOTS : (pChance == 3 ? Items.CHAINMAIL_BOOTS : null);
         default -> null;
      };
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      this.tickBrain(this);
   }

   public List<ExtendedSensor<KyoyaTachinbanaEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor(), new SleepSensor()});
   }

   public BrainActivityGroup<KyoyaTachinbanaEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(new Behavior[]{new WakeUp().stopIf(entity -> {
         if (!TensuraBehaviourHelper.canContinueToSleep(entity)) {
            entity.stopSleeping();
            return true;
         } else {
            return false;
         }
      }), new LookAtTarget(), new FloatToSurfaceOfFluid(), TensuraTamableEntity.getMoveToWalkTarget()});
   }

   public BrainActivityGroup<KyoyaTachinbanaEntity> getIdleTasks() {
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

   public BrainActivityGroup<KyoyaTachinbanaEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new StrafeTarget().stopStrafingWhen(entity -> !entity.usingRangedWeapon()).startCondition(PlayerLikeEntity::usingRangedWeapon),
            new SetWalkTargetToAttackTarget().speedMod((owner, target) -> 2.0F).startCondition(entity -> !entity.usingRangedWeapon()),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  PlayerLikeEntity.getSpearAttack(20).attackInterval(entity -> 5).attackRadius(25.0F),
                  PlayerLikeEntity.getCrossbowAttack().attackInterval(entity -> 10).attackRadius(30.0F),
                  PlayerLikeEntity.getBowAttack().attackInterval(entity -> 10).attackRadius(25.0F),
                  new CustomRangeAttack(10)
                     .requireInSight(false)
                     .minAttackRadius(5.0F)
                     .maxAttackRadius(32.0F)
                     .attackInterval(entity -> 20)
                     .performAttack((entity, target) -> entity.bladeJail(target, 10))
                     .whenStarting(
                        entity -> MagicCircle.castMagicCircle(
                           1.0F, 25, MagicCircleVariant.SPACE, entity, 1.0F, Vec3.ZERO, (ManasSkill)null, 0, Pair.of(0.0, 0.0)
                        )
                     )
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.1 && entity.getSeverer() != null),
                  new CustomRangeAttack(10)
                     .minAttackRadius(5.0F)
                     .maxAttackRadius(32.0F)
                     .attackInterval(entity -> 20)
                     .performAttack((entity, target) -> entity.bladeStorm(10))
                     .whenStarting(
                        entity -> MagicCircle.castMagicCircle(
                           3.0F, 35, MagicCircleVariant.SPACE, entity, -1.0F, Vec3.ZERO, (ManasSkill)UniqueSkills.SEVERER.get(), 0, Pair.of(0.0, 2000.0)
                        )
                     )
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.2 && entity.getSeverer() != null),
                  new CustomRangeAttack(10)
                     .minAttackRadius(5.0F)
                     .maxAttackRadius(32.0F)
                     .attackInterval(entity -> 20)
                     .performAttack(KyoyaTachinbanaEntity::bladeShoot)
                     .startCondition(entity -> entity.getRandom().nextFloat() <= 0.5 && entity.getSeverer() != null),
                  new AnimatableMeleeAttack(1)
                     .attackInterval(entity -> 5)
                     .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
                     .startCondition(entity -> !entity.usingRangedWeapon())
               }
            )
         }
      );
   }

   public Map<Activity, BrainActivityGroup<? extends KyoyaTachinbanaEntity>> getAdditionalTasks() {
      return (Map<Activity, BrainActivityGroup<? extends KyoyaTachinbanaEntity>>)Util.make(
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
