package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MixinMob {
   @Inject(
      method = "finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;)Lnet/minecraft/world/entity/SpawnGroupData;",
      at = @At("TAIL")
   )
   public void finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor,
      DifficultyInstance difficultyInstance,
      MobSpawnType mobSpawnType,
      SpawnGroupData spawnGroupData,
      CallbackInfoReturnable<SpawnGroupData> cir
   ) {
      IExistence existence = TensuraStorages.getExistenceFrom((Mob)this);
      existence.setSpawnType(mobSpawnType);
      existence.markDirty();
   }

   @ModifyReturnValue(method = "getTarget()Lnet/minecraft/world/entity/LivingEntity;", at = @At("RETURN"))
   public LivingEntity getTarget(LivingEntity original) {
      if (original == null) {
         return null;
      } else {
         Mob entity = (Mob)this;
         if (entity.getActiveEffects().isEmpty()) {
            return original;
         } else {
            return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT)) ? null : original;
         }
      }
   }

   @Inject(method = "doHurtTarget(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
   public void doHurtTarget(Entity pEntity, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) float damage, @Local DamageSource source) {
      Mob entity = (Mob)this;
      if (!entity.level().isClientSide()) {
         if (!SkillUtils.shouldCancelInteraction(entity)) {
            TensuraEnchantmentHelper.doAdditionalAfterAttack((ServerLevel)entity.level(), pEntity, entity, source, entity.getMainHandItem(), damage);
         }
      }
   }

   @Inject(
      method = "doHurtTarget(Lnet/minecraft/world/entity/Entity;)Z",
      at = @At(
         value = "INVOKE",
         shift = Shift.AFTER,
         target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;doPostAttackEffects(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;)V"
      )
   )
   public void doPostAttackEffects(Entity pEntity, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) float damage, @Local DamageSource source) {
      Mob entity = (Mob)this;
      if (!entity.level().isClientSide()) {
         TensuraEnchantmentHelper.doAdditionalAfterDamage((ServerLevel)entity.level(), pEntity, entity, source, entity.getMainHandItem(), damage);
      }
   }

   @ModifyReturnValue(method = "isSunBurnTick()Z", at = @At("RETURN"))
   public boolean isSunBurnTick(boolean original) {
      if (!original) {
         return false;
      }

      Mob entity = (Mob)this;
      return entity.level().getBiome(entity.blockPosition()).is(TensuraBiomeTags.IS_MIASMIC) ? false : original;
   }
}
