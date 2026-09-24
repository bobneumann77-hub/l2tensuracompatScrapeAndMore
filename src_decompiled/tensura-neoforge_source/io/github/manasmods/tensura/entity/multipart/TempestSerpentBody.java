package io.github.manasmods.tensura.entity.multipart;

import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TempestSerpentBody extends LivingMultipartBody implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public TempestSerpentBody(EntityType<? extends TempestSerpentBody> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public TempestSerpentBody(EntityType<? extends TempestSerpentBody> pEntityType, LivingEntity parent) {
      super(pEntityType, parent);
   }

   public boolean canBeAffected(MobEffectInstance pEffectInstance) {
      if (pEffectInstance.getEffect().equals(MobEffects.POISON)) {
         return false;
      } else {
         return pEffectInstance.getEffect().equals(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON))
            ? false
            : super.canBeAffected(pEffectInstance);
      }
   }

   @Override
   public boolean hurt(@NotNull DamageSource source, float damage) {
      Entity head = this.getHead();
      return head != null && head.hurt(source, damage * 0.5F);
   }

   @Override
   public boolean isSaddleable() {
      return this.getBodyIndex() != 0 && !this.isEndSegment() ? this.isAlive() && !this.isBaby() : false;
   }

   @Override
   protected boolean canAddPassenger(Entity pPassenger) {
      return this.getBodyIndex() != 0 && !this.isChested() ? super.canAddPassenger(pPassenger) : false;
   }

   @Override
   public boolean canOpenMountInventory(Player owner) {
      return this.isChested();
   }

   @Override
   public int getChestsAllowed() {
      return this.getBodyIndex() != 0 && !this.isEndSegment() ? 1 : 0;
   }

   @Override
   public boolean isSaddleRequired() {
      return false;
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return pStack.is(ItemTags.MEAT) || pStack.is(ItemTags.FISHES);
   }

   @Override
   public InteractionResult handleCommanding(Player player, InteractionHand hand, ItemStack stack) {
      if (this.getHead() instanceof TensuraTamableEntity head && head.isTame()) {
         if (this.getBodyIndex() == 0) {
            return head.mobInteract(player, hand);
         }

         if (head.isOwnedBy(player) && player.isSecondaryUseActive()) {
            head.cycleCommands(head, player);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
         }

         if (this.getChests() < this.getChestsAllowed() || this.getChestsAllowed() <= 0) {
            if (this.isSaddleable() && stack.is(Items.CHEST) && this.getChestsAllowed() > 0) {
               this.equipChest(player, stack);
            } else if (this.isRideable(player)) {
               this.doPlayerRide(player);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide());
         }

         if (this.getChests() > 0) {
            if (stack.is(Items.SHEARS)) {
               this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
               this.dropChestEach();
            } else {
               this.openCustomInventoryScreen(player);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide());
         }
      }

      return InteractionResult.PASS;
   }

   protected PlayState loopController(AnimationState<TempestSerpentBody> state) {
      String name;
      if (this.isInSittingPose() && this.isEndSegment()) {
         name = "animation.tempest_serpent.stay";
      } else if (state.isMoving()) {
         name = "animation.tempest_serpent.sway";
      } else {
         name = "animation.tempest_serpent.idle";
      }

      return state.setAndContinue(RawAnimation.begin().thenLoop(name));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "loopController", 5, this::loopController));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
