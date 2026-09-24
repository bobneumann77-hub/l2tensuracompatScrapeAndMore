package io.github.manasmods.tensura.entity.human;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.unique.CookSkill;
import io.github.manasmods.tensura.ability.subclass.ICloning;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain.Provider;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
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

public class CloneEntity extends PlayerLikeEntity implements SmartBrainOwner<CloneEntity> {
   private static final EntityDataAccessor<Integer> LIFE = SynchedEntityData.defineId(CloneEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(CloneEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(CloneEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Boolean> CHUNK_LOADER = SynchedEntityData.defineId(CloneEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> ILLUSION = SynchedEntityData.defineId(CloneEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> STATIC = SynchedEntityData.defineId(CloneEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<CompoundTag> SKILL = SynchedEntityData.defineId(CloneEntity.class, EntityDataSerializers.COMPOUND_TAG);
   private static final EntityDataAccessor<CompoundTag> PROFILE = SynchedEntityData.defineId(CloneEntity.class, EntityDataSerializers.COMPOUND_TAG);
   private int life = 0;
   private ChunkPos chunkPos = null;

   public CloneEntity(EntityType<? extends CloneEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 0;
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
   }

   public static Builder setAttributes() {
      return Player.createAttributes().add(Attributes.STEP_HEIGHT, 1.0).add(Attributes.FOLLOW_RANGE, 64.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(LIFE, -1);
      builder.define(HEIGHT, 1.0F);
      builder.define(WIDTH, 1.0F);
      builder.define(CHUNK_LOADER, false);
      builder.define(ILLUSION, false);
      builder.define(STATIC, false);
      builder.define(SKILL, new CompoundTag());
      builder.define(PROFILE, new CompoundTag());
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Life", this.getLife());
      compound.putFloat("Height", this.getHeight());
      compound.putFloat("Width", this.getWidth());
      compound.putBoolean("ChunkLoader", this.isChunkLoader());
      compound.putBoolean("Illusion", this.isIllusion());
      compound.putBoolean("Static", this.isStatic());
      if (this.getSkill() != null) {
         compound.put("skill", (Tag)this.entityData.get(SKILL));
      } else if (compound.contains("skill")) {
         compound.remove("skill");
      }

      if (this.getProfile() != null) {
         compound.put("profile", (Tag)this.entityData.get(PROFILE));
      } else if (compound.contains("profile")) {
         compound.remove("profile");
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setLife(compound.getInt("Life"));
      this.setHeight(compound.getFloat("Height"));
      this.setWidth(compound.getFloat("Width"));
      this.setChunkLoader(compound.getBoolean("ChunkLoader"));
      this.setIllusion(compound.getBoolean("Illusion"));
      this.setStatic(compound.getBoolean("Static"));
      if (compound.contains("skill") && compound.get("skill") instanceof CompoundTag tag) {
         this.entityData.set(SKILL, tag);
      }

      if (compound.contains("profile") && compound.get("profile") instanceof CompoundTag tag) {
         this.entityData.set(PROFILE, tag);
      }
   }

   public int getLife() {
      return (Integer)this.entityData.get(LIFE);
   }

   public void setLife(int life) {
      this.entityData.set(LIFE, life);
   }

   public float getHeight() {
      return (Float)this.entityData.get(HEIGHT);
   }

   public void setHeight(float pSize) {
      this.entityData.set(HEIGHT, pSize);
   }

   public float getWidth() {
      return (Float)this.entityData.get(WIDTH);
   }

   public void setWidth(float pSize) {
      this.entityData.set(WIDTH, pSize);
   }

   public boolean isChunkLoader() {
      return (Boolean)this.entityData.get(CHUNK_LOADER);
   }

   public void setChunkLoader(boolean loader) {
      this.entityData.set(CHUNK_LOADER, loader);
   }

   public boolean isIllusion() {
      return (Boolean)this.entityData.get(ILLUSION);
   }

   public void setIllusion(boolean illusion) {
      this.entityData.set(ILLUSION, illusion);
   }

   public boolean isStatic() {
      return (Boolean)this.entityData.get(STATIC);
   }

   public void setStatic(boolean immobile) {
      this.entityData.set(STATIC, immobile);
   }

   public ManasSkillInstance getSkill() {
      return ((CompoundTag)this.entityData.get(SKILL)).isEmpty() ? null : ManasSkillInstance.fromNBT((CompoundTag)this.entityData.get(SKILL));
   }

   public void setSkill(@Nullable ManasSkillInstance instance) {
      this.entityData.set(SKILL, instance == null ? new CompoundTag() : instance.toNBT());
   }

   public ResolvableProfile getProfile() {
      return ((CompoundTag)this.entityData.get(PROFILE)).isEmpty()
         ? null
         : (ResolvableProfile)ResolvableProfile.CODEC.parse(NbtOps.INSTANCE, (Tag)this.entityData.get(PROFILE)).resultOrPartial().orElse(null);
   }

   public void setProfile(@Nullable ResolvableProfile profile) {
      if (profile == null) {
         this.entityData.set(PROFILE, new CompoundTag());
      } else {
         CompoundTag tag = ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, profile).resultOrPartial().orElse(new CompoundTag());
         this.entityData.set(PROFILE, tag);
      }
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (HEIGHT.equals(pKey) || WIDTH.equals(pKey)) {
         this.reapplyPosition();
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   public void tame(Player pPlayer) {
      super.tame(pPlayer);
      this.setProfile(new ResolvableProfile(pPlayer.getGameProfile()));
      IExistence existence = TensuraStorages.getExistenceFrom(this);
      existence.setOriginalAlignment(TensuraStorages.getExistenceFrom(pPlayer).getOriginalAlignment());
      existence.setAlignment(TensuraStorages.getExistenceFrom(pPlayer).getAlignment());
      existence.markDirty();
   }

   @NotNull
   @Override
   public EntityDimensions getDefaultDimensions(Pose pPose) {
      return super.getDefaultDimensions(pPose).scale(this.getWidth(), this.getHeight());
   }

   public boolean isSilent() {
      return true;
   }

   @Override
   public boolean canSleep() {
      LivingEntity owner = this.getOwner();
      return owner == null ? false : owner.isSleeping();
   }

   @Override
   public boolean shouldHeal() {
      return !this.isStatic() && super.shouldHeal();
   }

   @Override
   public boolean shouldSwim() {
      return !this.isStatic() && super.shouldSwim();
   }

   @Override
   public boolean wantsToPickUp(ItemStack pStack) {
      return false;
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   @Override
   public boolean shouldDropExperience() {
      return false;
   }

   @Override
   protected boolean shouldDropLoot() {
      return false;
   }

   @Nullable
   public Entity changeDimension(DimensionTransition dimensionTransition) {
      return null;
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return false;
   }

   public boolean hurt(DamageSource damageSource, float f) {
      boolean hurt = super.hurt(damageSource, f);
      if (hurt && this.isIllusion()) {
         this.remove();
         return false;
      } else {
         return hurt;
      }
   }

   @Override
   public boolean doHurtTarget(Entity entity, float multiplier) {
      if (this.isIllusion()) {
         if (entity instanceof LivingEntity target) {
            DamageSource damageSource = this.damageSources().mobAttack(this);
            float g = this.getKnockback(entity, damageSource) + 0.5F;
            if (g > 0.0F) {
               target.knockback(g * 0.5F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            target.handleDamageEvent(damageSource);
            target.invulnerableTime = 0;
            TensuraDamageHelper.markHurt(target, this);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.doHurtTarget(entity, multiplier);
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         this.skillHandling();
         if (this.isChunkLoader()) {
            this.loadChunkHandler();
         }

         if (this.getLife() >= 0 && this.life++ >= this.getLife()) {
            this.remove();
         }
      }
   }

   public void skillHandling() {
      if (this.getSkill() != null) {
         LivingEntity owner = this.getOwner();
         if (owner == null || owner.isRemoved()) {
            return;
         }

         if (this.tickCount % 100 == 0 && !SkillUtils.hasSkill(owner, this.getSkill().getSkill())) {
            this.remove();
         }

         if (this.getSkill().getSkill() instanceof ICloning cloning) {
            cloning.onCloneTick(this, owner);
         }
      }
   }

   public void loadChunkHandler() {
      if (this.level() instanceof ServerLevel serverLevel) {
         ChunkPos var3 = this.chunkPosition();
         if (this.chunkPos == null || !this.chunkPos.equals(var3)) {
            if (this.chunkPos != null) {
               forceLoadChunks(this.chunkPos, serverLevel, false);
            }

            this.chunkPos = var3;
            forceLoadChunks(var3, serverLevel, true);
         }
      }
   }

   public void remove(RemovalReason pReason) {
      super.remove(pReason);
      if (pReason.shouldDestroy()) {
         if (this.isChunkLoader()) {
            if (this.chunkPos != null && this.level() instanceof ServerLevel serverLevel) {
               forceLoadChunks(this.chunkPos, serverLevel, false);
            }
         }
      }
   }

   private static void forceLoadChunks(ChunkPos pos, ServerLevel pServerLevel, boolean add) {
      for (int i = -1; i < 2; i++) {
         for (int j = -1; j < 2; j++) {
            int k = pos.x + i;
            int l = pos.z + j;
            pServerLevel.setChunkForced(k, l, add);
         }
      }
   }

   public void remove() {
      this.level()
         .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      TensuraParticleHelper.addServerAuraParticles(
         this, TensuraParticleUtils.getBlackAura(0.5F, (float)this.getAttributeValue(Attributes.SCALE) * 4.0F, -0.3F), 10, 0.01
      );
      if (this.inventory != null) {
         for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
               this.spawnAtLocation(stack);
               this.inventory.setItem(i, ItemStack.EMPTY);
            }
         }
      }

      this.updateContainerEquipment();
      this.discard();
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (this.isTame() && this.isOwnedBy(player)) {
         InteractionResult interaction = this.getInventoryInteraction(player, hand);
         if (interaction.consumesAction()) {
            return interaction;
         }

         if (this.isStatic()) {
            return InteractionResult.PASS;
         }

         this.cycleCommands(this, player);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   @NotNull
   @Override
   public InteractionResult handleEating(Player player, InteractionHand hand, ItemStack stack) {
      return this.isStatic() ? InteractionResult.PASS : super.handleEating(player, hand, stack);
   }

   public static void copyRotation(LivingEntity from, LivingEntity to) {
      to.setXRot(from.getXRot());
      to.setYRot(from.getYRot());
      to.setYBodyRot(from.yBodyRot);
      to.setYHeadRot(from.yHeadRot);
      to.setOldPosAndRot();
   }

   public static void copyEffects(LivingEntity from, LivingEntity to) {
      to.setRemainingFireTicks(from.getRemainingFireTicks());
      IEffect effectFrom = TensuraStorages.getEffectFrom(from);
      IEffect effectTo = TensuraStorages.getEffectFrom(to);
      effectTo.setSeveranceAmount(effectFrom.getSeveranceAmount());
      effectTo.setSeveranceRemoveTime(effectFrom.getSeveranceRemoveTime());
      effectTo.markDirty();
      to.refreshDimensions();
   }

   public static void copyStatusEffect(LivingEntity from, LivingEntity to, CloneEntity.CopySkill copySkill, boolean removeOld) {
      if (copySkill != CloneEntity.CopySkill.NONE) {
         for (MobEffectInstance instance : List.copyOf(from.getActiveEffects())) {
            if (copySkill != CloneEntity.CopySkill.INTRINSIC || !instance.getEffect().is(TensuraTags.MobEffects.SPIRITUAL_AFFECTED)) {
               to.addEffect(new MobEffectInstance(instance));
               if (removeOld) {
                  from.removeEffect(instance.getEffect());
               }
            }
         }
      }
   }

   public static void copyAttributeModifiers(LivingEntity from, LivingEntity to) {
      for (Entry<Holder<Attribute>, AttributeInstance> entry : from.getAttributes().attributes.entrySet()) {
         if (!entry.getKey().equals(TensuraAttributes.MAX_AURA) && !entry.getKey().equals(TensuraAttributes.MAX_MAGICULE)) {
            AttributeInstance fromInstance = entry.getValue();
            if (fromInstance.getValue() != ((Attribute)entry.getKey().value()).getDefaultValue()) {
               AttributeInstance toInstance = to.getAttribute(entry.getKey());
               if (toInstance != null) {
                  for (AttributeModifier modifier : fromInstance.getModifiers()) {
                     toInstance.removeModifier(modifier.id());
                     toInstance.addOrReplacePermanentModifier(new AttributeModifier(modifier.id(), modifier.amount(), modifier.operation()));
                  }
               }
            }
         }
      }

      if (to.getHealth() > to.getMaxHealth()) {
         to.setHealth(to.getMaxHealth());
      }
   }

   public void copyStatsAndSkills(LivingEntity owner, CloneEntity.CopySkill copySkill, boolean copyRace) {
      this.setCustomName(owner.getName());
      copyRotation(owner, this);
      if (copyRace) {
         this.copyRace(owner);
      }

      for (Entry<Holder<Attribute>, AttributeInstance> entry : owner.getAttributes().attributes.entrySet()) {
         if (!entry.getKey().equals(TensuraAttributes.MAX_AURA)
            && !entry.getKey().equals(TensuraAttributes.LIMITED_SPIRITUAL_MAX_AURA)
            && !entry.getKey().equals(TensuraAttributes.MAX_MAGICULE)
            && !entry.getKey().equals(TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE)) {
            AttributeInstance fromInstance = entry.getValue();
            AttributeInstance toInstance = this.getAttribute(entry.getKey());
            if (toInstance != null) {
               double base = fromInstance.getBaseValue();
               if (entry.getKey().equals(Attributes.MOVEMENT_SPEED)) {
                  base = ICloning.getConvertedMovementSpeed(base, true);
               }

               toInstance.setBaseValue(base);

               for (AttributeModifier modifier : fromInstance.getModifiers()) {
                  if ((!modifier.id().getNamespace().equals("minecraft") || !modifier.id().getPath().contains("armor."))
                     && !Objects.equals(modifier.id(), CookSkill.COOK)) {
                     double amount = modifier.amount();
                     if (entry.getKey().equals(Attributes.MOVEMENT_SPEED) && modifier.operation().equals(Operation.ADD_VALUE)) {
                        amount = ICloning.getConvertedMovementSpeed(amount, true);
                     }

                     AttributeModifier modifierCopy = new AttributeModifier(modifier.id(), amount, modifier.operation());
                     toInstance.addOrReplacePermanentModifier(modifierCopy);
                  }
               }
            }
         }
      }

      this.setHealth(Math.max(1.0F, owner.getHealth()));
      IExistence existence = TensuraStorages.getExistenceFrom(this);
      existence.setSpiritualHealth(this.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH));
      existence.setAura(Math.min(existence.getAura(), EnergyHelper.getMaxAura(this)));
      existence.setMagicule(Math.min(existence.getMagicule(), EnergyHelper.getMaxMagicule(this)));
      existence.markDirty();
      this.reapplyPosition();
      this.refreshDimensions();
      if (copySkill != CloneEntity.CopySkill.NONE) {
         for (ManasSkillInstance instance : List.copyOf(SkillAPI.getSkillsFrom(owner).getLearnedSkills())) {
            ManasSkillInstance skill = TensuraSkillInstance.fromNBT(instance.toNBT());
            skill.getOrCreateTag().putBoolean("NoMagiculeCost", true);
            if (copySkill == CloneEntity.CopySkill.INTRINSIC) {
               if (skill.isTemporarySkill()) {
                  if (SkillHelper.learnSkill(this, skill) && skill.isToggled()) {
                     skill.onToggleOn(this);
                  }
               } else {
                  Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(this).getRace();
                  if (optional.isPresent()
                     && optional.get().getObtainedIntrinsicSkills().contains(skill.getSkill())
                     && SkillHelper.learnSkill(this, skill)
                     && skill.isToggled()) {
                     skill.onToggleOn(this);
                  }
               }
            } else if (SkillHelper.learnSkill(this, skill) && skill.isToggled()) {
               skill.onToggleOn(this);
            }
         }
      }
   }

   public void copyRace(LivingEntity owner) {
      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(owner).getRace();
      if (!optional.isEmpty()) {
         RaceAPI.getRaceFrom(this).setRace(optional.get().copy(), false, false, null);
      }
   }

   public void copyInventory(Player owner) {
      this.copyInventory(List.copyOf(owner.getInventory().items), owner.getInventory().selected);
      this.copyEquipments(owner);
   }

   public void copyInventory(List<ItemStack> stacks, int selected) {
      for (int i = 0; i < stacks.size(); i++) {
         if (i != selected) {
            this.inventory.setItem(this.getMiscSlots() + i, stacks.get(i));
         }
      }
   }

   public void copyEquipments(LivingEntity owner) {
      for (EquipmentSlot slot : EquipmentSlot.values()) {
         int id = this.getSlotId(slot);
         if (id >= 0) {
            ItemStack stack = owner.getItemBySlot(slot).copy();
            this.inventory.setItem(id, stack);
            this.updateContainerEquipment();
         }
      }
   }

   public void copyInventoryOntoOwner(Player owner, boolean forceReplace) {
      this.copyEquipmentsOntoOwner(owner, forceReplace);

      for (int i = this.getMiscSlots(); i < this.getInventorySize(); i++) {
         ItemStack stack = this.inventory.getItem(i);
         int id = i - this.getMiscSlots();
         if (id == owner.getInventory().selected && !owner.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            ItemHelper.dropItem(owner, owner.getRandom(), stack, 0, 0.1F);
         } else if (!forceReplace && !owner.getInventory().getItem(id).isEmpty()) {
            ItemHelper.dropItem(owner, owner.getRandom(), stack, 0, 0.1F);
         } else {
            owner.getInventory().setItem(id, stack);
         }

         this.inventory.setItem(i, ItemStack.EMPTY);
      }
   }

   public void copyEquipmentsOntoOwner(LivingEntity owner, boolean forceReplace) {
      for (EquipmentSlot slot : EquipmentSlot.values()) {
         int id = this.getSlotId(slot);
         if (id >= 0) {
            ItemStack stack = this.inventory.getItem(id);
            if (!forceReplace && !owner.getItemBySlot(slot).isEmpty()) {
               ItemHelper.dropItem(owner, owner.getRandom(), stack, 0, 0.1F);
            } else {
               owner.setItemSlot(slot, stack);
            }

            this.inventory.setItem(id, ItemStack.EMPTY);
            this.updateContainerEquipment();
         }
      }
   }

   @NotNull
   protected Provider<?> brainProvider() {
      return new SmartBrainProvider(this, true);
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (!this.isStatic()) {
         this.tickBrain(this);
      }
   }

   public List<ExtendedSensor<CloneEntity>> getSensors() {
      return ObjectArrayList.of(new ExtendedSensor[]{new NearbyLivingEntitySensor(), new HurtBySensor()});
   }

   public BrainActivityGroup<CloneEntity> getCoreTasks() {
      return BrainActivityGroup.coreTasks(
         new Behavior[]{
            new LookAtTarget().startCondition(entity -> !entity.isStatic()),
            new FloatToSurfaceOfFluid(),
            new MoveToWalkTarget()
               .cooldownFor(entity -> 0)
               .startCondition(entity -> !entity.isOrderedToSit() && !entity.isStatic())
               .stopIf(entity -> entity.isOrderedToSit() || entity.isStatic())
         }
      );
   }

   public BrainActivityGroup<CloneEntity> getIdleTasks() {
      return BrainActivityGroup.idleTasks(
         new Behavior[]{
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  TensuraBehaviourHelper.getPreyTargeting(this, entity -> false).startCondition(entity -> !entity.isStatic()),
                  TensuraBehaviourHelper.getMoveToWanderPos(),
                  new SubordinateFollowOwner().startCondition(entity -> !entity.isStatic() && !entity.isOrderedToSit() && !entity.isWandering()),
                  new SetPlayerLookTarget(),
                  new SetRandomLookTarget()
               }
            ),
            new InteractWithDoor(),
            new HumanoidConsumeItem().startCondition(CloneEntity::shouldHeal).stopIf(entity -> !entity.shouldHeal()),
            new OneRandomBehaviour(new ExtendedBehaviour[]{new SetRandomWalkTarget(), new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))})
               .startCondition(entity -> !entity.isStatic() && !entity.isOrderedToSit())
         }
      );
   }

   public BrainActivityGroup<CloneEntity> getFightTasks() {
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
                  new AnimatableMeleeAttack(1)
                     .attackInterval(entity -> 15)
                     .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND, true))
                     .startCondition(entity -> !entity.usingRangedWeapon())
               }
            )
         }
      );
   }

   public enum CopySkill {
      ALL,
      INTRINSIC,
      NONE;
   }
}
