package io.github.manasmods.tensura.entity.human.golem;

import io.github.manasmods.tensura.block.DummyBlock;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrainingDummyEntity extends TensuraHumanoidEntity {
   public int damageNumber = 0;
   public float damageTotal = 0.0F;
   public float lastDamageTaken = 0.0F;
   public int lastDamageTick = 0;

   public TrainingDummyEntity(EntityType<? extends TrainingDummyEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
      this.xpReward = 0;
   }

   public static Builder setAttributes() {
      return Player.createAttributes().add(Attributes.FOLLOW_RANGE, 0.0);
   }

   public boolean isPushable() {
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
      ItemStack itemStack = ((Item)TensuraBlocks.Items.TRAINING_DUMMY.get()).getDefaultInstance();
      if (this.hasCustomName()) {
         itemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
      }

      return itemStack;
   }

   protected void updateControlFlags() {
   }

   public void setDeltaMovement(Vec3 vec3) {
      if (!this.isNoGravity()) {
         super.setDeltaMovement(vec3);
      }
   }

   public void knockback(double strength, double x, double z) {
      if (!this.isNoGravity()) {
         super.knockback(strength, x, z);
      }
   }

   public boolean isPushedByFluid() {
      return !this.isNoGravity();
   }

   protected boolean isImmobile() {
      return this.isNoGravity();
   }

   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
      return false;
   }

   protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
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
   public boolean shouldShowHP() {
      return false;
   }

   @Override
   public boolean shouldShowSHP() {
      return false;
   }

   @Override
   public boolean shouldShowEP() {
      return false;
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
   public void tick() {
      super.tick();
      Level level = this.level();
      if (!level.isClientSide()) {
         BlockState state = level.getBlockState(this.blockPosition());
         if (!state.is((Block)TensuraBlocks.TRAINING_DUMMY.get())) {
            this.kill();
         }

         if (this.lastDamageTick != 0 && this.tickCount == this.lastDamageTick + 1) {
            CombatEntry entry = this.getLastCombat();
            if (entry != null && entry.source().getEntity() instanceof Player player) {
               player.displayClientMessage(
                  Component.translatable("tensura.message.damage.total", new Object[]{this.damageTotal}).withStyle(ChatFormatting.GOLD), true
               );
            }

            BlockState thatch = ((DummyBlock)TensuraBlocks.TRAINING_DUMMY.get()).defaultBlockState();
            ((ServerLevel)level)
               .sendParticles(
                  new BlockParticleOption(ParticleTypes.BLOCK, thatch),
                  this.getX(),
                  this.getY(0.66),
                  this.getZ(),
                  10,
                  this.getBbWidth() / 4.0F,
                  this.getBbHeight() / 4.0F,
                  this.getBbWidth() / 4.0F,
                  0.05
               );
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 1.0F, 1.0F);
            this.lastHurt = 0.0F;
         }
      }
   }

   @NotNull
   @Override
   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      return this.getInventoryInteraction(player, hand);
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

      this.level()
         .playSound(
            null,
            this.getX(),
            this.getY(),
            this.getZ(),
            ((RotatedPillarBlock)TensuraBlocks.THATCH_BLOCK.get()).defaultBlockState().getSoundType().getBreakSound(),
            this.getSoundSource(),
            1.0F,
            1.0F
         );
      this.remove(RemovalReason.KILLED);
      this.gameEvent(GameEvent.ENTITY_DIE);
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return SoundEvents.ARMOR_STAND_HIT;
   }

   @NotNull
   public SoundEvent getDeathSound() {
      return SoundEvents.ARMOR_STAND_BREAK;
   }

   public boolean isDeadOrDying() {
      return false;
   }

   public boolean hurt(DamageSource source, float f) {
      if (source.getEntity() == null) {
         return false;
      }

      this.lastDamageTaken = 0.0F;
      boolean hurt = super.hurt(source, f);
      if (this.level().isClientSide()) {
         return hurt;
      }

      if (this.lastDamageTaken == 0.0F) {
         Predicate<ServerPlayer> predicate = player -> player.distanceTo(this) <= 16.0F || player == source.getEntity();
         TensuraParticleHelper.spawnServerParticles(
            this.level(),
            this.getDamageParticle(source, 0.0F, this.damageNumber),
            this.getX(),
            this.getY() + this.getBbHeight() * 1.25F,
            this.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true,
            predicate
         );
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F);
      }

      return hurt;
   }

   public void setHealth(float newHealth) {
      if (newHealth == this.getMaxHealth()) {
         super.setHealth(newHealth);
      } else {
         Level level = this.level();
         if (level.isClientSide()) {
            return;
         }

         if (this.tickCount >= this.lastDamageTick && this.tickCount <= this.lastDamageTick + 5) {
            this.damageNumber++;
         } else {
            this.lastDamageTick = this.tickCount;
            this.damageNumber = 1;
            this.damageTotal = 0.0F;
         }

         CombatEntry entry = this.getLastCombat();
         if (entry == null) {
            this.lastDamageTaken = this.getHealth() - newHealth;
            Predicate<ServerPlayer> predicate = player -> player.distanceTo(this) <= 16.0F;
            TensuraParticleHelper.spawnServerParticles(
               this.level(),
               this.getDamageParticle(null, this.getHealth() - newHealth, this.damageNumber),
               this.getX(),
               this.getY() + this.getBbHeight() * 1.375F,
               this.getZ(),
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true,
               predicate
            );
         } else {
            this.lastDamageTaken = entry.damage();
            Predicate<ServerPlayer> predicate = player -> player.distanceTo(this) <= 16.0F || player == entry.source().getEntity();
            TensuraParticleHelper.spawnServerParticles(
               this.level(),
               this.getDamageParticle(entry.source(), entry.damage(), this.damageNumber),
               this.getX(),
               this.getY() + this.getBbHeight() * 1.375F,
               this.getZ(),
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true,
               predicate
            );
         }

         this.damageTotal = this.damageTotal + this.lastDamageTaken;
      }
   }

   public ParticleOptions getDamageParticle(@Nullable DamageSource source, float damage, int damageNumber) {
      if (damage < 0.0F) {
         return TensuraParticleUtils.getNumber(damage, TensuraColors.getTonedRGB(16733525, 0.9F), damageNumber);
      }

      int color = TensuraDamageHelper.getDamageColor(source);
      return source != null && source.tensura$isSlotting()
         ? TensuraParticleUtils.getNumber(damage, color, TensuraColors.getTonedRGB(16711935, 0.25F), damageNumber)
         : TensuraParticleUtils.getNumber(damage, color, damageNumber);
   }

   @Nullable
   public CombatEntry getLastCombat() {
      CombatTracker tracker = this.getCombatTracker();
      return tracker.entries.isEmpty() ? null : (CombatEntry)tracker.entries.getLast();
   }
}
