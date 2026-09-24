package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.effect.debuff.SleepEffect;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayer {
   @ModifyReturnValue(method = "getHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/sounds/SoundEvent;", at = @At("RETURN"))
   protected SoundEvent getHurtSound(SoundEvent original, DamageSource pDamageSource) {
      Player player = (Player)this;
      return SkillUtils.shouldCancelPain(player) ? null : original;
   }

   @Inject(method = "causeFoodExhaustion(F)V", at = @At("HEAD"), cancellable = true)
   public void causeFoodExhaustion(float pExhaustion, CallbackInfo ci) {
      Player player = (Player)this;
      if (!player.hasEffect(MobEffects.HUNGER)) {
         IExistence existence = TensuraStorages.getExistenceFrom(player);
         if (!existence.getAlignment().isNeedFood() || existence.isSpiritualForm()) {
            if (TensuraEnchantmentHelper.getEnchantmentLevel(player.level(), TensuraEnchantments.ENERVATION, player) <= 0 || RaceUtils.isUndead(player)) {
               if (player.getFoodData().getFoodLevel() < 18) {
                  player.getFoodData().setFoodLevel(18);
                  ci.cancel();
               } else if (player.getFoodData().getFoodLevel() == 18) {
                  ci.cancel();
               }
            }
         }
      }
   }

   @ModifyReturnValue(method = "isHurt()Z", at = @At("RETURN"))
   public boolean isSeveranceHurt(boolean original) {
      if (!original) {
         return false;
      }

      Player player = (Player)this;
      return player.getHealth() >= EffectStorage.getSeveranceMaxHealth(player) ? false : original;
   }

   @ModifyReturnValue(method = "isSleepingLongEnough()Z", at = @At("RETURN"))
   public boolean isSleepingLongEnough(boolean original) {
      Player player = (Player)this;
      return player.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY)) != null ? false : original;
   }

   @Inject(
      method = "attack(Lnet/minecraft/world/entity/Entity;)V",
      at = @At(
         value = "INVOKE",
         ordinal = 0,
         shift = Shift.BEFORE,
         target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      ),
      cancellable = true
   )
   public void beforeAttack(Entity pTarget, CallbackInfo ci) {
      Player player = (Player)this;
      if (!player.level().isClientSide()) {
         if (!SkillUtils.shouldCancelInteraction(player)) {
            for (MobEffectInstance instance : player.getActiveEffects()) {
               if (instance.getEffect().value() instanceof DamageAction effect && !effect.onPlayerAttack(player, pTarget)) {
                  ci.cancel();
               }
            }
         }
      }
   }

   @Inject(
      method = "attack(Lnet/minecraft/world/entity/Entity;)V",
      at = @At(
         value = "INVOKE",
         ordinal = 0,
         shift = Shift.AFTER,
         target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      )
   )
   public void attack(Entity pTarget, CallbackInfo ci, @Local(ordinal = 3) float damage, @Local DamageSource source) {
      Player player = (Player)this;
      if (!player.level().isClientSide()) {
         if (!SkillUtils.shouldCancelInteraction(player)) {
            TensuraEnchantmentHelper.doAdditionalAfterAttack((ServerLevel)player.level(), pTarget, player, source, player.getMainHandItem(), damage);
         }
      }
   }

   @Inject(
      method = "attack(Lnet/minecraft/world/entity/Entity;)V",
      at = @At(
         value = "INVOKE",
         ordinal = 1,
         shift = Shift.AFTER,
         target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;doPostAttackEffects(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V"
      )
   )
   public void doPostAttackEffects(Entity pTarget, CallbackInfo ci, @Local(ordinal = 3) float damage, @Local DamageSource source) {
      Player player = (Player)this;
      if (!player.level().isClientSide()) {
         TensuraEnchantmentHelper.doAdditionalAfterDamage((ServerLevel)player.level(), pTarget, player, source, player.getMainHandItem(), damage);
      }
   }

   @WrapOperation(method = "updatePlayerPose()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSleeping()Z"))
   public boolean isSleeping(Player player, Operation<Boolean> original) {
      if ((Boolean)original.call(new Object[]{player})) {
         return true;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      return existence.getSleepModeTime() <= 0
            && existence.getHarvestGiftTick() <= 0
            && (existence.getHarvestTick() <= 0 || existence.getHarvestTick() >= 2400)
         ? SleepEffect.isSleeping(player)
         : true;
   }

   @ModifyReturnValue(method = "getName()Lnet/minecraft/network/chat/Component;", at = @At("RETURN"))
   public Component getName(Component original) {
      Player player = (Player)this;
      if (!player.level().getGameRules().getBoolean(TensuraGameRules.TENSURA_DISPLAY_NAME)) {
         return original;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(player);
      return (Component)(existence.getName() == null ? original : Component.literal(existence.getName()).withStyle(original.getStyle()));
   }
}
