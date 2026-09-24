package io.github.manasmods.tensura.entity.magic.beam;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.extra.MolecularManipulationSkill;
import io.github.manasmods.tensura.ability.skill.unique.PredatorSkill;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ItemHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PredatorMistProjectile extends BeamProjectile implements GeoEntity {
   private static final EntityDataAccessor<Float> ATTACKING_RANGE = SynchedEntityData.defineId(PredatorMistProjectile.class, EntityDataSerializers.FLOAT);
   private boolean consumeProjectile;
   private int blockMode;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public PredatorMistProjectile(EntityType<? extends PredatorMistProjectile> entityType, Level level) {
      super(entityType, level);
      this.setFollowingOwner(true);
      this.setSize(1.5F);
      this.setRange(0.0F);
   }

   public PredatorMistProjectile(EntityType<? extends PredatorMistProjectile> entityType, Level level, LivingEntity entity) {
      this(entityType, level);
      this.setOwner(entity);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(ATTACKING_RANGE, 3.0F);
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("AttackingRange", this.getAttackingRange());
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setAttackingRange(pCompound.getFloat("AttackingRange"));
   }

   public float getAttackingRange() {
      return (Float)this.entityData.get(ATTACKING_RANGE);
   }

   public void setAttackingRange(float range) {
      this.entityData.set(ATTACKING_RANGE, range);
   }

   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return this.getType().getDimensions().scale(0.06666667F);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.DEVOURED;
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      if (!super.canHitEntity(pTarget)) {
         return false;
      } else {
         return pTarget instanceof ExperienceOrb ? true : pTarget instanceof LivingEntity entity && entity.isAlive();
      }
   }

   @Override
   public boolean piercingBlock() {
      return true;
   }

   @Override
   protected boolean shouldStopFollowOwner() {
      return false;
   }

   @Override
   protected Vec3 getFollowPos(Entity owner) {
      return owner.getEyePosition();
   }

   protected boolean canDevour(ManasSkillInstance instance, float magicChance) {
      if (instance.isTemporarySkill() || instance.getMastery() < 0.0) {
         return false;
      }

      if (instance.is(TensuraSkillTags.NO_PLUNDERING)) {
         return false;
      }

      if (instance.is(TensuraSkillTags.BATTLEWILL)) {
         return false;
      }

      ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
      if (instance.getSkill() == skill || skill == null) {
         return false;
      }

      if (instance.is(TensuraSkillTags.MAGIC)) {
         return magicChance > 0.0F && instance.is(TensuraSkillTags.COPIABLE_MAGIC) && this.getRandom().nextFloat() <= magicChance;
      }

      if (!instance.is(TensuraSkillTags.SKILLS)) {
         return false;
      }

      if (!this.getSkill().isSubInstance()) {
         if (skill == UniqueSkills.PREDATOR.get() && instance.getSkill() == UniqueSkills.STARVED.get()) {
            return true;
         }

         if (instance.getSkill() == UniqueSkills.DEGENERATE.get() && this.level().getGameRules().getBoolean(TensuraGameRules.RIMURU_MODE)) {
            return true;
         }
      }

      return instance.is(TensuraSkillTags.UNIQUE_SKILLS) ? false : !instance.is(TensuraSkillTags.ULTIMATE_SKILLS);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getLife() - this.getAge() < 29 && this.getRange() > 1.0F) {
         this.setRange(Math.max(1.0F, this.getRange() - this.getAttackingRange() / 30.0F));
         this.setSize(5.0F * this.getRange() / 10.0F);
      } else if (this.getRange() < this.getAttackingRange()) {
         this.setRange(Math.min(this.getAttackingRange(), this.getRange() + 0.25F));
         this.setSize(5.0F * this.getRange() / 10.0F);
      } else if (this.getRange() >= this.getAttackingRange()) {
         this.setRange(this.getAttackingRange());
         this.setSize(5.0F * this.getRange() / 10.0F);
      }
   }

   @Override
   public List<Entity> collectEntityCollision(Vec3 from, Vec3 to) {
      double size = this.getSize();
      AABB box = new AABB(
            Math.min(from.x, to.x), Math.min(from.y, to.y), Math.min(from.z, to.z), Math.max(from.x, to.x), Math.max(from.y, to.y), Math.max(from.z, to.z)
         )
         .inflate(size);
      Vec3 forward = this.getLookAngle().normalize();
      return this.level().getEntitiesOfClass(Entity.class, box, entity -> forward.dot(entity.position().subtract(this.position()).normalize()) > 0.0);
   }

   @Override
   protected boolean hitEntity(Entity entity, ProjectileHitResult customResult) {
      if (this.getLife() - this.getAge() < 29) {
         return false;
      } else if (this.isConsumeProjectile() && entity instanceof Projectile projectile) {
         return this.devourProjectile(projectile);
      } else if (this.getOwner() instanceof Player player && entity instanceof ExperienceOrb orb) {
         orb.playerTouch(player);
         return true;
      } else if (super.hitEntity(entity, customResult) && entity instanceof LivingEntity target) {
         if (this.getOwner() instanceof LivingEntity owner) {
            this.devourTarget(target, owner);
            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   protected boolean devourProjectile(Projectile projectile) {
      projectile.discard();
      if (!(this.getOwner() instanceof LivingEntity owner)) {
         return false;
      } else {
         if (projectile.getType() == ProjectileEntityTypes.TEMPEST_SCALE.get() && owner instanceof Player player) {
            ItemStack scale = ((Item)TensuraMobDropItems.CHARYBDIS_SCALE.get()).getDefaultInstance();
            if (!this.addItemToSpatialStorage(player, scale) && !player.addItem(scale)) {
               ItemHelper.dropItem(player, player.getRandom(), scale, 20, 1.0F);
            }
         } else if (projectile instanceof AbstractArrow arrow && arrow.pickup == Pickup.ALLOWED && owner instanceof Player player) {
            ItemStack pickupItem = arrow.getPickupItem();
            if (!this.addItemToSpatialStorage(player, pickupItem) && !player.addItem(pickupItem)) {
               ItemHelper.dropItem(player, player.getRandom(), pickupItem, 20, 1.0F);
            }
         }

         if (projectile instanceof TensuraFlyingProjectile flying) {
            ManasSkillInstance skill = flying.getSkill();
            if (skill != null && this.canDevour(skill, 1.0F)) {
               Changeable<ManasSkill> changeable = Changeable.of(skill.getSkill());
               if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker())
                     .plunder(projectile.getOwner(), owner, false, changeable)
                     .isFalse()
                  && SkillHelper.learnSkill(owner, (ManasSkill)changeable.get(), this.getSkill().getRemoveTime())) {
                  owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               }

               return true;
            } else {
               return true;
            }
         } else {
            return true;
         }
      }
   }

   protected void devourTarget(LivingEntity target, LivingEntity owner) {
      if (!target.getType().is(TensuraEntityTags.NO_ENERGY_DRAIN)) {
         EnergyHelper.drainEnergy(target, owner, PredatorSkill.CONFIG.predationEPDrain, false, EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NORMAL);
      }

      if (target.isAlive()) {
         if (target.getRandom().nextFloat() < PredatorSkill.CONFIG.predationSkillChance / 10.0F) {
            this.devourRandomSkill(target, owner, PredatorSkill.CONFIG.predationSkillNumber);
         }
      } else {
         this.devourAllSkills(target, owner);
         this.devourEP(target, owner, PredatorSkill.CONFIG.predationEPSteal);
         if (owner instanceof Player player) {
            for (ItemEntity item : owner.level().getEntitiesOfClass(ItemEntity.class, AABB.ofSize(target.position(), 2.0, 2.0, 2.0))) {
               if (this.addItemToSpatialStorage(player, item.getItem())) {
                  item.discard();
               } else if (player.addItem(item.getItem())) {
                  item.discard();
               } else {
                  item.teleportTo(player.position().x(), player.position().y(), player.position().z());
               }
            }
         }
      }
   }

   protected void devourRandomSkill(LivingEntity target, LivingEntity owner, int time) {
      if (!target.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)) {
         List<ManasSkillInstance> collection = new ArrayList<>(
            SkillAPI.getSkillsFrom(target).getLearnedSkills().stream().filter(instancex -> this.canDevour(instancex, 0.0F)).toList()
         );
         if (!collection.isEmpty()) {
            for (int i = 0; i < time; i++) {
               if (collection.isEmpty()) {
                  return;
               }

               ManasSkillInstance instance = collection.get(target.getRandom().nextInt(collection.size()));
               Changeable<ManasSkill> changeable = Changeable.of(instance.getSkill());
               if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker()).plunder(target, owner, false, changeable).isFalse()
                  && SkillHelper.learnSkill(owner, (ManasSkill)changeable.get(), this.getSkill().getRemoveTime())) {
                  owner.level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  collection.remove(instance);
               }
            }
         }
      }
   }

   protected void devourAllSkills(LivingEntity target, LivingEntity owner) {
      if (!target.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)) {
         List<ManasSkillInstance> targetSkills = SkillAPI.getSkillsFrom(target)
            .getLearnedSkills()
            .stream()
            .filter(instance -> this.canDevour(instance, PredatorSkill.CONFIG.predationMagicCopy))
            .toList();
         if (!targetSkills.isEmpty()) {
            for (ManasSkillInstance targetInstance : targetSkills) {
               if (!targetInstance.isTemporarySkill() && !(targetInstance.getMastery() < 0.0)) {
                  Changeable<ManasSkill> changeable = Changeable.of(targetInstance.getSkill());
                  if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker()).plunder(target, owner, false, changeable).isFalse()
                     && SkillHelper.learnSkill(owner, (ManasSkill)changeable.get(), this.getSkill().getRemoveTime())) {
                     owner.level()
                        .playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  }
               }
            }
         }
      }
   }

   protected void devourEP(LivingEntity target, LivingEntity owner, float amountToMax) {
      if (!target.getType().is(TensuraEntityTags.NO_EP_PLUNDER)) {
         Skills skills = SkillAPI.getSkillsFrom(owner);
         Optional<ManasSkillInstance> predator = skills.getSkill(this.getSkill().getSkill());
         if (!predator.isEmpty()) {
            ManasSkillInstance instance = predator.get();
            if (!instance.isTemporarySkill()) {
               CompoundTag tag = instance.getOrCreateTag();
               if (tag.contains("predationList")) {
                  CompoundTag predationList = (CompoundTag)tag.get("predationList");
                  if (predationList == null) {
                     return;
                  }

                  String targetID = EntityType.getKey(target.getType()).toString();
                  if (predationList.contains(targetID)) {
                     return;
                  }

                  predationList.putBoolean(targetID, true);
                  instance.markDirty();
               } else {
                  CompoundTag predationList = new CompoundTag();
                  predationList.putBoolean(EntityType.getKey(target.getType()).toString(), true);
                  tag.put("predationList", predationList);
                  instance.markDirty();
               }

               skills.markDirty();
               if (TensuraGameRules.canEpSteal(this.level()) || !(target instanceof Player)) {
                  double EP = Math.min(EnergyHelper.getEPGain(target, owner), EnergyHelper.CONFIG.maximumEPSteal / amountToMax);
                  if (EnergyHelper.drainEnergy(target, owner, EP / 2.0, false, EnergyHelper.DrainType.MAX_EP, EnergyHelper.GainType.NONE)) {
                     EnergyHelper.gainMagicule(owner, EP * amountToMax, EnergyHelper.GainType.MAX);
                     IExistence existence = TensuraStorages.getExistenceFrom(target);
                     existence.setSkippingEPDrop(true);
                     existence.markDirty();
                     this.saveMagiculeIntoStorage(owner, EP * (1.0F - amountToMax));
                  }
               }
            }
         }
      }
   }

   protected void saveMagiculeIntoStorage(LivingEntity owner, double amount) {
      ManasSkillInstance instance = this.getSkillInstance(owner);
      if (instance != null) {
         CompoundTag tag = instance.getOrCreateTag();
         tag.putDouble("MpStomach", tag.getDouble("MpStomach") + amount);
         instance.markDirty();
      }
   }

   @Nullable
   protected ManasSkillInstance getSkillInstance(LivingEntity owner) {
      Skills storage = SkillAPI.getSkillsFrom(owner);
      Optional<ManasSkillInstance> optional = storage.getSkill(this.getSkill().getSkill());
      return optional.orElse(null);
   }

   protected boolean addItemToSpatialStorage(Player player, ItemStack stack) {
      ManasSkillInstance instance = this.getSkillInstance(player);
      if (instance == null) {
         return false;
      } else {
         return instance.getSkill() instanceof ISpatialStorage spatialStorage ? spatialStorage.addItemToSpatialStorage(instance, player, stack) : false;
      }
   }

   @Override
   protected boolean canDestroyBlock(BlockPos pos) {
      BlockState state = this.level().getBlockState(pos);
      if (state.is(TensuraBlockTags.SKILL_UNBREAKABLE)) {
         return false;
      } else {
         return state.is(TensuraBlockTags.SKILL_UNOBTAINABLE) ? false : state.getBlock().defaultDestroyTime() > -1.0F;
      }
   }

   @Override
   protected void handleBlockInteraction() {
      if (this.shouldGrief()) {
         if (this.getLife() - this.getAge() >= 29) {
            if (this.getOwner() instanceof Player player) {
               int blockMode = this.getBlockMode();
               if (blockMode != 1) {
                  float range = (float) (Math.PI / 6);

                  for (int i = 0; i < this.getRange(); i++) {
                     Vec3 lookAngle = this.getOwner()
                        .getLookAngle()
                        .normalize()
                        .xRot(this.getRandom().nextFloat() * range * 2.0F - range)
                        .yRot(this.getRandom().nextFloat() * range * 2.0F - range)
                        .zRot(this.getRandom().nextFloat() * range * 2.0F - range);
                     if (blockMode == 2 || blockMode == 4) {
                        this.breakBlocks(player, lookAngle);
                     }

                     if (blockMode == 3 || blockMode == 4) {
                        this.consumeFluid(player, lookAngle);
                     }
                  }
               }
            }
         }
      }
   }

   protected void breakBlocks(Player player, Vec3 lookAngle) {
      BlockHitResult result = this.level()
         .clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(lookAngle.scale(this.getRange())), Block.OUTLINE, Fluid.NONE, this));
      if (result.getType() == Type.BLOCK) {
         BlockPos pos = result.getBlockPos();
         if (!this.canDestroyBlock(pos)) {
            return;
         }

         if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
            .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
            .isFalse()) {
            BlockState state = this.level().getBlockState(pos);
            if (MolecularManipulationSkill.shouldBreakInstead(this.level(), pos, state)) {
               this.level().destroyBlock(pos, true, player);
               state.getBlock().playerWillDestroy(this.level(), pos, state, player);
            } else {
               state.getBlock().playerWillDestroy(this.level(), pos, state, player);
               BlockEntity blockentity = this.level().getBlockEntity(pos);
               if (blockentity instanceof BeehiveBlockEntity beehiveblockentity) {
                  beehiveblockentity.emptyAllLivingFromHive(player, state, BeeReleaseStatus.EMERGENCY);
               }

               if (!state.is(BlockTags.CROPS) && !(blockentity instanceof ShulkerBoxBlockEntity box && !box.isEmpty())) {
                  ItemStack stack = new ItemStack(state.getBlock());
                  if (!this.addItemToSpatialStorage(player, stack) && !player.addItem(stack)) {
                     player.drop(stack, false);
                  }

                  this.level().destroyBlock(pos, false, player);
               } else {
                  this.level().destroyBlock(pos, !player.isCreative(), player);
               }
            }

            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());

            for (ItemEntity item : player.level().getEntitiesOfClass(ItemEntity.class, AABB.ofSize(Vec3.atCenterOf(pos), 2.0, 2.0, 2.0))) {
               if (this.addItemToSpatialStorage(player, item.getItem())) {
                  item.discard();
               } else if (player.addItem(item.getItem())) {
                  item.discard();
               } else {
                  item.teleportTo(player.position().x(), player.position().y(), player.position().z());
               }
            }
         }
      }
   }

   protected void consumeFluid(Player player, Vec3 lookAngle) {
      ClipContext context = new ClipContext(
         player.getEyePosition(), player.getEyePosition().add(lookAngle.scale(this.getRange())), Block.OUTLINE, Fluid.ANY, this
      );
      BlockHitResult result = this.level().clip(context);
      if (result.getType() == Type.BLOCK) {
         BlockPos pos = result.getBlockPos();
         BlockState state = this.level().getBlockState(pos);
         if (!state.getFluidState().isEmpty()
            && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
               .isFalse()) {
            if (state.getFluidState().isSource()) {
               IAbility ability = TensuraStorages.getAbilityFrom(player);
               if (state.getFluidState().is(FluidTags.WATER)) {
                  ability.setWaterPoint(ability.getWaterPoint() + 1.0);
                  ability.markDirty();
               } else if (state.getFluidState().is(FluidTags.LAVA)) {
                  ability.setLavaPoint(ability.getLavaPoint() + 1.0);
                  ability.markDirty();
               }
            }

            if (state.is(Blocks.WATER) || state.is(Blocks.BUBBLE_COLUMN) || state.is(Blocks.LAVA)) {
               this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            } else if (state.hasProperty(BlockStateProperties.WATERLOGGED) && (Boolean)state.getValue(BlockStateProperties.WATERLOGGED)) {
               this.level().setBlock(pos, (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, false), 11);
            } else if (state.is(Blocks.SEAGRASS) || state.is(Blocks.TALL_SEAGRASS) || state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT)) {
               this.level().destroyBlock(pos, true, player);
               state.getBlock().playerWillDestroy(this.level(), pos, state, player);
            }

            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(
               this,
               "loopController",
               0,
               event -> this.getLife() - this.getAge() < 29
                  ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.gluttony_mist.stop"))
                  : event.setAndContinue(RawAnimation.begin().thenLoop("animation.gluttony_mist.loop"))
            ),
            new AnimationController(this, "controller", 0, event -> PlayState.STOP)
               .triggerableAnim("start", RawAnimation.begin().then("animation.gluttony_mist.start", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static PredatorMistProjectile spawnPredationMist(
      EntityType<? extends PredatorMistProjectile> entityType,
      LivingEntity owner,
      ManasSkillInstance instance,
      int mode,
      float damage,
      Pair<Double, Double> cost,
      float length,
      int blockMode,
      boolean projectile
   ) {
      CompoundTag tag = instance.getOrCreateTag();
      Level level = owner.level();
      if (tag.getInt("Mist") == 0) {
         PredatorMistProjectile mist = (PredatorMistProjectile)entityType.create(level);
         if (mist == null) {
            return null;
         }

         mist.setLife(35);
         mist.setFollowingOwner(true);
         mist.setDamage(damage);
         mist.setOwner(owner);
         mist.setRange(3.0F);
         mist.setSize(1.5F);
         mist.setAttackingRange(length);
         mist.setBlockMode(blockMode);
         mist.setConsumeProjectile(projectile);
         mist.setPos(owner.position().add(0.0, owner.getBbHeight() / 2.0F, 0.0));
         mist.setApCost((Double)cost.getFirst());
         mist.setMpCost((Double)cost.getSecond());
         mist.setSkill(instance);
         mist.setMode(mode);
         owner.level().addFreshEntity(mist);
         mist.triggerAnim("controller", "start");
         tag.putInt("Mist", mist.getId());
         instance.markDirty();
         return mist;
      } else if (owner.level().getEntity(tag.getInt("Mist")) instanceof PredatorMistProjectile mist) {
         mist.setAge(0);
         mist.setAttackingRange(length);
         instance.markDirty();
         return mist;
      } else {
         tag.putInt("Mist", 0);
         return null;
      }
   }

   @Generated
   public boolean isConsumeProjectile() {
      return this.consumeProjectile;
   }

   @Generated
   public void setConsumeProjectile(boolean consumeProjectile) {
      this.consumeProjectile = consumeProjectile;
   }

   @Generated
   public int getBlockMode() {
      return this.blockMode;
   }

   @Generated
   public void setBlockMode(int blockMode) {
      this.blockMode = blockMode;
   }
}
