package io.github.manasmods.tensura.entity.human.golem;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.ability.skill.unique.CookSkill;
import io.github.manasmods.tensura.ability.subclass.ICloning;
import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import io.github.manasmods.tensura.entity.variant.BoneGolemVariant;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BoneGolemEntity extends TensuraHumanoidEntity implements GeoEntity, VariantHolder<BoneGolemVariant> {
   private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(BoneGolemEntity.class, EntityDataSerializers.INT);
   public long lastHit;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public BoneGolemEntity(EntityType<? extends BoneGolemEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 0;
   }

   public static Builder setAttributes() {
      return Player.createAttributes().add(Attributes.FOLLOW_RANGE, 0.0);
   }

   @Override
   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(VARIANT, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("Variant", this.getTypeVariant());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.entityData.set(VARIANT, compound.getInt("Variant"));
   }

   @NotNull
   public BoneGolemVariant getVariant() {
      return BoneGolemVariant.byId(this.getTypeVariant() & 0xFF);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(VARIANT);
   }

   public void setVariant(BoneGolemVariant variant) {
      this.entityData.set(VARIANT, variant.getId() & 0xFF);
   }

   @NotNull
   @Override
   public EntityDimensions getDefaultDimensions(Pose pose) {
      EntityDimensions dimensions = this.getType().getDimensions().scale(this.getAgeScale());

      return switch (this.getPose()) {
         case SWIMMING -> dimensions.scale(1.0F, 0.2F);
         case CROUCHING, LONG_JUMPING, FALL_FLYING -> dimensions.scale(1.0F, 0.95F);
         case SITTING -> dimensions.scale(1.0F, 0.65F);
         default -> dimensions;
      };
   }

   public boolean isSilent() {
      return true;
   }

   public boolean isInvisible() {
      return false;
   }

   public boolean isInvisibleTo(Player player) {
      return false;
   }

   public boolean wantsToPickUp(ItemStack pStack) {
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

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return false;
   }

   @NotNull
   public ItemStack getPickResult() {
      ItemStack itemStack = this.getVariant().getDrop().getDefaultInstance();
      if (this.hasCustomName()) {
         itemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
      }

      return itemStack;
   }

   public boolean isPushable() {
      return this.isNoGravity() ? false : super.isPushable();
   }

   protected void doPush(Entity entity) {
      if (!this.isNoGravity()) {
         super.doPush(entity);
      }
   }

   protected void updateControlFlags() {
   }

   @Override
   public boolean canOpenMountInventory(Player owner) {
      return true;
   }

   @Override
   public int getChestSlots() {
      return 0;
   }

   @Override
   public boolean shouldShowSHP() {
      return false;
   }

   public void copyStatAndRace(LivingEntity owner) {
      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(owner).getRace();
      optional.ifPresent(instance -> RaceAPI.getRaceFrom(this).setRace(instance.copy(), false, false, null));

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

      AttributeInstance shp = this.getAttribute(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
      if (shp != null) {
         shp.removeModifiers();
      }

      AttributeInstance size = this.getAttribute(Attributes.SCALE);
      if (size != null) {
         size.removeModifiers();
      }

      this.setHealth(this.getMaxHealth());
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   @Override
   public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
      return false;
   }

   private Pose getNextPose() {
      return switch (this.getPose()) {
         case SWIMMING -> Pose.STANDING;
         case CROUCHING -> Pose.SITTING;
         case LONG_JUMPING -> Pose.SWIMMING;
         case FALL_FLYING -> Pose.CROUCHING;
         case SITTING -> Pose.LONG_JUMPING;
         case EMERGING -> Pose.SHOOTING;
         case SHOOTING -> Pose.FALL_FLYING;
         default -> Pose.EMERGING;
      };
   }

   @NotNull
   @Override
   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      if (player.isSecondaryUseActive()) {
         this.setPose(this.getNextPose());
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(this.level().isClientSide());
      } else {
         return this.getInventoryInteraction(player, hand);
      }
   }

   public boolean hurt(DamageSource source, float f) {
      if (this.isRemoved()) {
         return false;
      }

      if (this.level() instanceof ServerLevel serverLevel) {
         if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.kill();
            return false;
         } else if (this.isInvulnerableTo(source)) {
            return false;
         } else {
            boolean kill = source.is(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS);
            if (!source.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND) && !kill) {
               return false;
            } else if (source.getEntity() instanceof Player player && !player.getAbilities().mayBuild) {
               return false;
            } else {
               if (source.isCreativePlayer()) {
                  this.playBrokenSound();
                  this.showBreakingParticles();
                  this.kill();
                  return true;
               }

               long time = serverLevel.getGameTime();
               if (time - this.lastHit > 5L && !kill) {
                  serverLevel.broadcastEntityEvent(this, (byte)32);
                  this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
                  this.lastHit = time;
               } else {
                  this.showBreakingParticles();
                  this.spawnAtLocation(this.getVariant().getDrop().getDefaultInstance());
                  this.kill();
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public void kill() {
      if (this.inventory != null) {
         for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) {
               this.spawnAtLocation(stack);
               this.inventory.setItem(i, ItemStack.EMPTY);
            }
         }
      }

      this.remove(RemovalReason.KILLED);
      this.gameEvent(GameEvent.ENTITY_DIE);
   }

   public void handleEntityEvent(byte b) {
      if (b == 32) {
         if (this.level().isClientSide()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
            this.lastHit = this.level().getGameTime();
         }
      } else {
         super.handleEntityEvent(b);
      }
   }

   private void playBrokenSound() {
      this.level()
         .playSound(
            null,
            this.getX(),
            this.getY(),
            this.getZ(),
            this.getVariant().getBlock().defaultBlockState().getSoundType().getBreakSound(),
            this.getSoundSource(),
            1.0F,
            1.0F
         );
   }

   private void showBreakingParticles() {
      if (this.level() instanceof ServerLevel level) {
         level.sendParticles(
            new BlockParticleOption(ParticleTypes.BLOCK, this.getVariant().getBlock().defaultBlockState()),
            this.getX(),
            this.getY(0.66),
            this.getZ(),
            10,
            this.getBbWidth() / 4.0F,
            this.getBbHeight() / 4.0F,
            this.getBbWidth() / 4.0F,
            0.05
         );
      }
   }

   protected PlayState poseController(AnimationState<BoneGolemEntity> state) {
      return state.setAndContinue(RawAnimation.begin().thenLoop(switch (this.getPose()) {
         case SWIMMING -> "animation.bone_golem.sleep";
         case CROUCHING -> "animation.bone_golem.sneak";
         case LONG_JUMPING -> "animation.bone_golem.walk";
         case FALL_FLYING -> "animation.bone_golem.star";
         case SITTING -> "animation.bone_golem.sit";
         case EMERGING -> "animation.bone_golem.t_pose";
         case SHOOTING -> "animation.bone_golem.zombie";
         default -> "animation.bone_golem.stand";
      }));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "poseController", 1, this::poseController));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
